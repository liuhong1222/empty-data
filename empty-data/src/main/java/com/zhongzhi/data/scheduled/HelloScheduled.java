/*
 * Copyright 2019-2029 geekidea2(https://github.com/geekidea2)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhongzhi.data.scheduled;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.zhongzhi.data.entity.UnicodeReader;
import com.zhongzhi.data.util.PhoneRedisBitUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Hello任务调度
 *
 * @author geekidea2
 * @since 2019-10-29
 **/
@Slf4j
@Component
public class HelloScheduled {

    @Resource
    private PhoneRedisBitUtil phoneRedisBitUtil;

    /**
     * 每天4点10分执行一次
     */
    @Scheduled(cron = "0 10 4 * * ? ")
    public void execute() {
        log.info("risk phone input Scheduled...");
        BufferedReader bufferedReader = new BufferedReader(new UnicodeReader(
                FileUtil.getInputStream("/root/test/black_mobile.txt")));
        try {
            readLine(bufferedReader, phoneRedisBitUtil::addRiskNo);
            log.info("风险号码导入完成，black_mobile.txt");
        } catch (IOException e) {
            log.error("风险号码导入发生异常", e);
        } finally {
            IoUtil.close(bufferedReader);
        }
        log.info("black_mobile.txt completed");

    }

    /**
     * 每小时执行一次
     */
//    @Async
//    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000, initialDelay = 1 * 60 * 1000)
    public void hello() {
        log.info("Test Scheduled...");
        BufferedReader bufferedReader = null;
        for (int i = 0; i < 26; i++) {
            int num = i + 1;
            bufferedReader = new BufferedReader(new UnicodeReader(
                    FileUtil.getInputStream("/root/test/bm_" + num + ".txt")));
            try {
                readLine(bufferedReader, phoneRedisBitUtil::addRiskNo);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IoUtil.close(bufferedReader);
            }
            log.info("bm_" + num + ".txt completed");
        }
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

    private void readLineForDelete(BufferedReader reader, Consumer<String[]> consumer) throws IOException {
        int batchSize = 4096;
        int i = 0;
        List<String> nos = new ArrayList<>(batchSize);
        String line;
        while ((line = reader.readLine()) != null) {
            nos.add(line);
            i++;
            if (i >= batchSize) {
                consumer.accept(nos.toArray(new String[nos.size()]));
                nos.clear();
                i = 0;
            }
        }

        consumer.accept(nos.toArray(new String[nos.size()]));
    }

    /**
     * 每小时执行一次
     */
//    @Async
//    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000, initialDelay = 1 * 60 * 1000)
    public void phonesImport() {
        log.info("phone import Scheduled start...");
        BufferedReader activeBufferedReader = new BufferedReader(new UnicodeReader(
                FileUtil.getInputStream("/root/test/active_109341260.txt")));
        try {
            readLine(activeBufferedReader, phoneRedisBitUtil::addActiveNo);
            readLineForDelete(activeBufferedReader, phoneRedisBitUtil::delEmptyNo);
            readLineForDelete(activeBufferedReader, phoneRedisBitUtil::delRiskNo);
            readLineForDelete(activeBufferedReader, phoneRedisBitUtil::delSilentNo);
            log.info("实号导入完成，active_109341260.txt");
        } catch (IOException e) {
            log.error("实号导入发生异常", e);
        } finally {
            IoUtil.close(activeBufferedReader);
        }

        BufferedReader emptyBufferedReader = new BufferedReader(new UnicodeReader(
                FileUtil.getInputStream("/root/test/empty_17336349.txt")));
        try {
            readLine(emptyBufferedReader, phoneRedisBitUtil::addEmptyNo);
            readLineForDelete(emptyBufferedReader, phoneRedisBitUtil::delActiveNo);
            readLineForDelete(emptyBufferedReader, phoneRedisBitUtil::delRiskNo);
            readLineForDelete(emptyBufferedReader, phoneRedisBitUtil::delSilentNo);
            log.info("空号导入完成，empty_17336349.txt");
        } catch (IOException e) {
            log.error("空号导入发生异常", e);
        } finally {
            IoUtil.close(emptyBufferedReader);
        }

        BufferedReader riskBufferedReader = new BufferedReader(new UnicodeReader(
                FileUtil.getInputStream("/root/test/risk_5402995.txt")));
        try {
            readLine(riskBufferedReader, phoneRedisBitUtil::addRiskNo);
            readLineForDelete(riskBufferedReader, phoneRedisBitUtil::delActiveNo);
            readLineForDelete(riskBufferedReader, phoneRedisBitUtil::delEmptyNo);
            readLineForDelete(riskBufferedReader, phoneRedisBitUtil::delSilentNo);
            log.info("风险号码导入完成，risk_5402995.txt");
        } catch (IOException e) {
            log.error("风险号码导入发生异常", e);
        } finally {
            IoUtil.close(riskBufferedReader);
        }

        log.info("phone import Scheduled end...");

    }
}
