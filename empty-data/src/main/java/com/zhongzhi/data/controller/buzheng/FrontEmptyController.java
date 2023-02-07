package com.zhongzhi.data.controller.buzheng;

import cn.hutool.core.date.DateUtil;
import com.zhongzhi.data.annotation.FrontAgent;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.interceptor.Interceptor;
import com.zhongzhi.data.interceptor.InterceptorConstants;
import com.zhongzhi.data.service.agent.AgentSettingsService;
import com.zhongzhi.data.service.empty.EmptyCheckService;
import com.zhongzhi.data.util.ThreadLocalContainer;
import com.zhongzhi.data.vo.PersonalStatisticalDataBo;
import com.zhongzhi.data.vo.PersonalStatisticalDataVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 空号检测记录
 * @author xybb
 * @date 2021-11-10
 */
@RestController
@RequestMapping("/front/empty")
@Interceptor(name = InterceptorConstants.AUTHENTICATION)
public class FrontEmptyController {

    @Autowired
    private EmptyCheckService emptyCheckService;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Autowired
    private AgentSettingsService agentSettingsService;

    /**
     * 获取客户号码检测统计信息
     */
    @FrontAgent
    @GetMapping("/getStatisticalData")
    @ApiOperation(value = "获取客户号码检测统计信息", notes = "获取客户号码检测统计信息", response = PersonalStatisticalDataVo.class)
    public ApiResult<PersonalStatisticalDataVo> getStatisticalData() throws Exception {
        Long customerId = ThreadLocalContainer.getCustomerId();

        // 获取今日客户号码检测总消耗数，活跃号码数
        PersonalStatisticalDataBo todayData = emptyCheckService.getPersonalStatisticalData(customerId,
                DateUtil.beginOfDay(new Date()).toJdkDate(), DateUtil.endOfDay(new Date()).toJdkDate());

        // 获取昨天客户号码检测总消耗数，活跃号码数
        PersonalStatisticalDataBo yesterdayData = emptyCheckService.getPersonalStatisticalData(customerId,
                DateUtil.beginOfDay(DateUtil.yesterday()).toJdkDate(), DateUtil.endOfDay(DateUtil.yesterday()).toJdkDate());

        // 获取本月客户号码检测总消耗数，活跃号码数
        PersonalStatisticalDataBo monthData = emptyCheckService.getPersonalStatisticalData(customerId,
                DateUtil.beginOfMonth(new Date()).toJdkDate(), DateUtil.endOfDay(new Date()).toJdkDate());

        // 获取上月客户号码检测总消耗数，活跃号码数
        PersonalStatisticalDataBo lastMonthData = emptyCheckService.getPersonalStatisticalData(customerId,
                DateUtil.beginOfMonth(DateUtil.lastMonth()).toJdkDate(), DateUtil.endOfMonth(DateUtil.lastMonth()).toJdkDate());

        PersonalStatisticalDataVo personalStatisticalDataVo = new PersonalStatisticalDataVo();
        personalStatisticalDataVo
                .setCustomerId(customerId)
                .setTodayConsumeTotal(todayData != null ? todayData.getConsumeTotal() : 0)
                .setTodayActiveTotal(todayData != null ? todayData.getActiveTotal() : 0)
                .setMonthConsumeTotal(monthData != null ? monthData.getConsumeTotal() : 0)
                .setMonthActiveTotal(monthData != null ? monthData.getActiveTotal() : 0)
                .setYesterdayConsumeTotal(yesterdayData != null ? yesterdayData.getConsumeTotal() : 0)
                .setYesterdayActiveTotal(yesterdayData != null ? yesterdayData.getActiveTotal() : 0)
                .setLastMonthConsumeTotal(lastMonthData != null ? lastMonthData.getConsumeTotal() : 0)
                .setLastMonthActiveTotal(lastMonthData != null ? lastMonthData.getActiveTotal() : 0);

        return ApiResult.ok(personalStatisticalDataVo);
    }

}
