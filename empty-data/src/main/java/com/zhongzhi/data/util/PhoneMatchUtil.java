package com.zhongzhi.data.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.zhongzhi.data.constants.RedisConstant;
import com.zhongzhi.data.entity.UnicodeReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@Component
public final class PhoneMatchUtil {

    @Resource
    private PhoneRedisBitUtil phoneRedisBitUtil;

    @Resource
    private RedisTemplate redisTemplate;

    private PhoneMatchUtil() {
    }

    /**
     * 号码匹配
     * @param excelFile 待匹配文件
     * @param txtFile  匹配手机号
     * @param resultFile
     * @return
     */
    public Integer matchPhoneNumbers(File excelFile, File txtFile, File resultFile) {
        if (!excelFile.exists() || !txtFile.exists()) {
            return null;
        }

        // 将匹配手机号装入redis bit桶
        BufferedReader bufferedReader = new BufferedReader(new UnicodeReader(FileUtil.getInputStream(txtFile)));
        try {
            readLine(bufferedReader, phoneRedisBitUtil::addPhoneMatchNo);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(bufferedReader);
        }
        log.info("redis bit桶匹配号码装载完成");

        String fileExtension = FilenameUtils.getExtension(excelFile.getName());
        if (fileExtension.equals("xls")) {
            // .xls文件
            Integer count = processXsl(excelFile, txtFile, resultFile);
            if (count != null) {
                return count;
            }

        } else {
            // .csv文件
            Integer count = processCsv(excelFile, txtFile, resultFile);
            if (count != null) {
                return count;
            }

        }

        return null;
    }

    /**
     * 处理csv文件
     * @date 2021/11/23
     * @param excelFile
     * @param txtFile
     * @param resultFile
     * @return void
     */
    private Integer processCsv(File excelFile, File txtFile, File resultFile) {
        // 检测是否存在目录
        if (!resultFile.getParentFile().exists()) {
            resultFile.getParentFile().mkdirs();
        }

        String fileEncode = EncodingDetect.getJavaEncode(excelFile.getAbsolutePath());
        CsvWriter csvWriter = new CsvWriter(resultFile.getAbsolutePath(), ',', Charset.forName(fileEncode));
        try {
            AtomicInteger count = new AtomicInteger();
            CsvReader csvReader = new CsvReader(excelFile.getAbsolutePath(), ',', Charset.forName(fileEncode));

            int rowCount = 0;
            while (csvReader.readRecord()){
                // 第一层遍历读一整行
                String row = csvReader.getRawRecord();
                // 0：不写 1：实号 2：未找到
                int rowFlag = 0;
                String[] split = row.split(",");

                for (String elem : split) {
                    // 第二层遍历，遍历一行每个元素
                    final String phone = elem.trim();
                    if (PhoneUtil.checkPhone(phone)) {
                        // 如果是手机号码，拿redis数据进行对比。且必然写入数据
                        rowFlag = 2;
                        List<Boolean> booleanList = phoneRedisBitUtil.existsPhoneMatch(new ArrayList<String>(){{add(phone);}});
                        if (booleanList != null && booleanList.size() > 0 && booleanList.get(0)) {
                            // 匹配成功
                            rowFlag = 1;
                        }
                    }
                }

                // 根据flag，判断这一行的后面写什么内容。
                String resultRow = writeMatchResult(txtFile, row, rowFlag, count);
                csvWriter.writeRecord(resultRow.split(","));

                // 仅做日志：读了多少行了
                rowCount++;
                if (rowCount%20000==0) {
                    log.info("客户id：{}，处理完成号码匹配数据，第{}行。", ThreadLocalContainer.getCustomerId(), rowCount);
                }
            }
            log.info("客户id：{}，处理号码匹配数据完成，一共匹配{}行。", ThreadLocalContainer.getCustomerId(), rowCount);
            return count.intValue();

        } catch (Exception e) {
            log.error("客户id：{}，处理csv文件出现异常，e:\n{}", ThreadLocalContainer.getCustomerId(), ExceptionUtils.getStackTrace(e));
            return null;

        } finally {
            csvWriter.close();

            // 删除redis记录
            Set<String> stringSet = redisTemplate.keys("*");
            stringSet.stream().filter(key -> key.startsWith(RedisConstant.PHONE_MATCHER_KEY)).forEach(redisTemplate::delete);
        }
    }

    /**
     * 根据flag在每一行后面写匹配结果
     * @date 2021/11/23
     * @param txtFile
     * @param row
     * @param rowFlag
     * @return String
     */
    private String writeMatchResult(File txtFile, String row, int rowFlag, AtomicInteger count) {
        String resultRow = null;
        switch (rowFlag) {
            case 0:
                // 不写入结果
                resultRow = row;
                break;

            case 1:
                // 写入txt文件名
                String fileName = txtFile.getName();
                int index = fileName.lastIndexOf(".");
                String fieldName = fileName.substring(0, index);
                resultRow = row + "," + fieldName;
                count.getAndIncrement();
                break;

            case 2:
                // 写入未找到
                resultRow = row + "," + "未找到";
                break;

        }

        return resultRow;
    }

    /**
     * 处理xsl文件
     * @date 2021/11/23
     * @param excelFile
     * @param txtFile
     * @param resultFile
     * @return Integer
     */
    private Integer processXsl(File excelFile, File txtFile, File resultFile) {
        BigExcelWriter writer = null;
        AtomicInteger size = new AtomicInteger(0);
        try {
            writer = ExcelUtil.getBigWriter(resultFile);
            writer.getStyleSet().setBorder(BorderStyle.NONE, IndexedColors.AUTOMATIC);
            AtomicInteger count = new AtomicInteger();
            // 读取待匹配文件
            BigExcelWriter finalWriter = writer;
            ExcelUtil.readBySax(excelFile, 0, (sheetIndex, rowIndex, rowList) -> {
                try {
                    // 强制当前线程放弃剩下的时间片，并休息 1 毫秒
                    // 解决CPU占用率高的问题
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(rowList == null || rowList.size() == 0) {
                    return;
                }
                if (rowList.stream().noneMatch(column -> column != null && PhoneUtil.checkPhone(column.toString()))) {
                    return;
                }
                if (size.get() == 0) {
                    size.set(rowList.size());
                }
                if (size.get() > 0) {
                    if (rowList.size() > size.get()) {
                        rowList = rowList.subList(0, size.get());
                    }
                    if (rowList.size() < size.get()) {
                        for (int i = 0; i < size.get() - rowList.size(); i++) {
                            rowList.add("");
                        }
                    }
                } else {
                    return;
                }
                if (rowList.stream().anyMatch(column -> {
                    if (column == null || StringUtils.isBlank(column.toString())) {
                        return false;
                    }
                    String phone = StringUtils.trim(column.toString());
                    if (PhoneUtil.checkPhone(phone)) {
                        List<Boolean> booleanList = phoneRedisBitUtil.existsPhoneMatch(new ArrayList<String>(){{add(phone);}});
                        if (booleanList != null && booleanList.size() > 0 && booleanList.get(0)) {
                            return true;
                        }
                    }
                    return false;
                })) {
                    // 获取txt文件名称
                    String fileName = txtFile.getName();
                    int index = fileName.lastIndexOf(".");
                    String fieldName = fileName.substring(0, index);
                    // rowList.add("已匹配");
                    rowList.add(fieldName);
                    count.getAndIncrement();
                } else {
                    rowList.add("未找到");
                }
                finalWriter.writeRow(rowList);

                log.debug("[" + sheetIndex + "] [" + rowIndex + "] " + rowList);
            });
            return count.intValue();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writer.close();
            // 1. 删除redis记录
            Set<String> stringSet = redisTemplate.keys("*");
            stringSet.stream().filter(key -> key.startsWith(RedisConstant.PHONE_MATCHER_KEY))
                    .forEach(redisTemplate::delete);
        }
        return null;
    }

    private void readLine(BufferedReader reader, Consumer<List<String>> consumer) throws IOException {
        int batchSize = 4096;
        int i = 0;
        List<String> nos = new ArrayList<>(batchSize);
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains(",")) {
                line = line.split(",")[0];
            }
            nos.add(line);
            i++;
            if (i >= batchSize) {
                consumer.accept(nos);
                nos.clear();
                i = 0;
            }
        }

        consumer.accept(nos);
    }

    /**
     * 手动刷新号码匹配缓存的方法
     * @date 2021/11/25
     * @param
     * @return String
     */
    public void flushMobileMatchCache() {
        // 删除redis记录
        log.info("删除redis号码匹配缓存开始");
        Set<String> stringSet = redisTemplate.keys("*");
        stringSet.stream().filter(key -> key.startsWith(RedisConstant.PHONE_MATCHER_KEY)).forEach(redisTemplate::delete);
        log.info("删除redis号码匹配缓存成功");
    }
}
