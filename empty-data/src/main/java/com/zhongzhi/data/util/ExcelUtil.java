package com.zhongzhi.data.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Excel工具类
 * @author liuh
 * @date 2021年11月4日
 */
public class ExcelUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * 获取文件行数
     *
     * @param filePath   文件地址
     * @param sheetIndex sheetIndex
     * @return 文件行数
     */
    public static int getLineNum(String filePath, int sheetIndex,String newFilePath) {
        return getLineNum(new File(filePath), sheetIndex,newFilePath);
    }

    /**
     * 获取文件行数
     *
     * @param file       文件句柄
     * @param sheetIndex sheetIndex
     * @return 文件行数
     */
    public static int getLineNum(File file, int sheetIndex,String newFilePath) {
        try {
        	Set<String> list = new HashSet<String>();
        	String fileEncoding = null;
        	DecimalFormat dFormat = new DecimalFormat("0");
            Workbook workbook = WorkbookFactory.create(file);
            if (null != workbook) {
            	fileEncoding = EncodingDetect
                        .getJavaEncode(file.getAbsolutePath());
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                if (null != sheet) {
                    int rowNum = sheet.getLastRowNum() + 1;
                    int lineNum = 0;
                    for (int i = 0; i < rowNum; i++) {
                    	String mobileString = "";
                        Row row = sheet.getRow(i);
                        if(null == row){
                            // 排除row为null的情况
                            continue;
                        }
                        Cell cell = row.getCell(0);
                        if (null == cell) {
                            continue;
                        }
                        Object cellValue = null;
                        CellType cellType = cell.getCellType();
                        switch (cellType) {
                            case NUMERIC:
                            	mobileString = (dFormat.format(cell.getNumericCellValue()) + "").replace(" ", "").replace("　", "");
                                break;
                            case STRING:
                            	mobileString = cell.getStringCellValue().replace(" ", "").replace("　", "");
                                break;                            
                            default:
                            	mobileString = cell.toString().replace(" ", "").replace("　", "");
                        }
                        
                        if (StringUtils.isBlank(mobileString)) {
                            continue;
                        }
                        
                        lineNum++;
                        list.add(mobileString);
                    }
                    
                    // 读取execl文件内容保存到txt中
                    if(!CollectionUtils.isEmpty(list)) {
                    	cn.hutool.core.io.FileUtil.del(file);
                    	FileUtil.saveTxt(list, newFilePath, fileEncoding, false);
                    }
                    
                    return lineNum;
                }
                
                workbook.close();
            }
        } catch (Exception e) {
            logger.error("getLineNum - [fileName:{}, sheetIndex:{}]", file.getName(), sheetIndex, e);
            return -1;
        }

        return 0;
    }

    /**
     * 写入Excel
     *
     * @param filePath  文件地址
     * @param sheetName sheetName
     * @param dataList  数据列表
     */
    public static void write(String filePath, String sheetName, List<List<String>> dataList) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            write(file, sheetName, dataList);
        } catch (Exception e) {
            logger.error("write - [filePath:{}, sheetName:{}]", filePath, sheetName, e);
        }
    }

    /**
     * 写入Excel
     *
     * @param file      文件句柄
     * @param sheetName sheetName
     * @param dataList  数据列表
     */
    public static void write(File file, String sheetName, List<List<String>> dataList) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(sheetName);
            for (int i = 0; i < dataList.size(); i++) {
                List<String> innerList = dataList.get(i);
                if (null == innerList) {
                    continue;
                }
                Row row = sheet.createRow(i);
                for (int j = 0; j < innerList.size(); j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(innerList.get(j));
                }
            }
            OutputStream out = new FileOutputStream(file);
            workbook.write(out);

            workbook.close();
            out.close();
        } catch (Exception e) {
            logger.error("write - [fileName:{}, sheetName:{}]", file.getName(), sheetName, e);
        }
    }

    /**
     * 写入多Sheet到Excel
     *
     * @param file        文件句柄
     * @param dataListMap dataListMap
     */
    public static void write(File file, Map<String, List<List<String>>> dataListMap) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            for (Map.Entry<String, List<List<String>>> entry : dataListMap.entrySet()) {
                Sheet sheet = workbook.createSheet(entry.getKey());
                List<List<String>> dataList = entry.getValue();
                if (null == dataList) {
                    continue;
                }
                for (int i = 0; i < dataList.size(); i++) {
                    List<String> innerList = dataList.get(i);
                    if (null == innerList) {
                        continue;
                    }
                    Row row = sheet.createRow(i);
                    for (int j = 0; j < innerList.size(); j++) {
                        Cell cell = row.createCell(j);
                        cell.setCellValue(innerList.get(j));
                    }
                }
            }

            OutputStream out = new FileOutputStream(file);
            workbook.write(out);

            workbook.close();
            out.close();
        } catch (Exception e) {
            logger.error("write - [fileName:{}]", file.getName(), e);
        }
    }
}
