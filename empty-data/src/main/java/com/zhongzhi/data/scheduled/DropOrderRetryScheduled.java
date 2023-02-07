package com.zhongzhi.data.scheduled;


import com.zhongzhi.data.entity.realtime.RealtimeCheck;
import com.zhongzhi.data.service.empty.EmptyCheckService;
import com.zhongzhi.data.service.realtime.RealtimeCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

/**
 * 异常等原因引起的掉单重试任务调度
 *
 * @author rivers
 * @since 2021-4-8
 **/
@Slf4j
@Component
public class DropOrderRetryScheduled {

    @Autowired
    private EmptyCheckService emptyCheckService;

    @Autowired
    private RealtimeCheckService realtimeCheckService;

    @Value("${file.upload.path}")
    private String uploadPath;


    /**
     * 每分钟执行一次
     */
    // @Async
    // @Scheduled(fixedDelay = 1 * 60 * 1000, initialDelay = 2 * 60 * 1000)
    public void realtimeDropRetry() {
        log.info("实时检测掉单重试定时器启动...");
        ZonedDateTime zonedDateTime = LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault());
        Date date = Date.from(zonedDateTime.toInstant());

        RealtimeCheck query = new RealtimeCheck().setStatus(RealtimeCheck.RealtimeCheckStatus.WORK_FINISH.getStatus());
        List<RealtimeCheck> list = realtimeCheckService.findByStatusAndCreateTime(RealtimeCheck.RealtimeCheckStatus.WORK_FINISH.getStatus(), date);

        for (RealtimeCheck realtimeCheck : list) {
            if (realtimeCheck.getLine() == null) {
                continue;
            }
            Long line = null;
            try {
                line = Long.parseLong(realtimeCheck.getLine());
            } catch (NumberFormatException e) {
                log.error("实号检测返回检测条数类型转换异常", e);
                continue;
            }

            if (line == null || line >= realtimeCheck.getTotalNumber()) {
                continue;
            }

            String folder = uploadPath + "/realtime/" + realtimeCheck.getCustomerId() + "/" +
                            realtimeCheck.getId();
            File checkFolder = new File(folder);

            if (checkFolder.exists() && checkFolder.isDirectory()) {
                // 过滤号码

            }
        }
        log.info("定时删除检测文件定时器结束...");
    }

}
