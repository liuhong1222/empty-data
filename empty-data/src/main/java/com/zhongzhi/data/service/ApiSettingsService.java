package com.zhongzhi.data.service;

import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.constants.CaffeineConstant;
import com.zhongzhi.data.entity.ApiSettings;
import com.zhongzhi.data.entity.customer.Customer;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.exception.BusinessException;
import com.zhongzhi.data.mapper.ApiSettingsMapper;
import com.zhongzhi.data.service.front.LoginService;
import com.zhongzhi.data.util.ThreadLocalContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


/**
 * <pre>
 * 对外api接口管理 服务实现类
 * </pre>
 *
 * @author rivers
 * @since 2020-02-13
 */
@Service
public class ApiSettingsService {

    private static final Logger logger = LoggerFactory.getLogger(ApiSettingsService.class);

    @Autowired
    private ApiSettingsMapper apiSettingsMapper;

    @Autowired
    private LoginService loginService;

    /**
     * 对外api接口-新增
     * @date 2021/11/4
     * @param apiSettings
     * @return int
     */
    public ApiResult save(ApiSettings apiSettings) {
        if (apiSettings.getCustomerId() == null) {
            return ApiResult.fail();
        }

        int i = apiSettingsMapper.save(apiSettings);
        if (i<=0) {
            logger.error("客户id：{}，新增对外api接口记录失败。apiSettings:{}", apiSettings.getCustomerId(), apiSettings);
            return ApiResult.fail(ApiCode.DAO_EXCEPTION);
        } else {
            logger.info("客户id：{}，新增对外api接口记录成功。apiSettings:{}", apiSettings.getCustomerId(), apiSettings);
            return ApiResult.ok(apiSettings);
        }
    }

    /**
     * 对外api接口-查找（通过条件）
     * @date 2021/11/10
     * @param apiSettings
     * @return ApiSettings
     */
    public ApiSettings findByCondition(ApiSettings apiSettings) {
        return apiSettingsMapper.findByCondition(apiSettings);
    }

    @Cacheable(cacheManager = "caffeineCacheManager", value = CaffeineConstant.API_SETTINGS_INFO, key = "#customerId", unless = "#result == null")
    public ApiSettings findOne(Long customerId) {
    	return apiSettingsMapper.findOne(customerId);
    }

    /**
     * 对外api接口-查找（通过客户id）
     * @date 2021/11/10
     * @param customerId
     * @return ApiSettings
     */
    public ApiSettings getInfo(Long customerId) {
        ApiSettings apiSettings = apiSettingsMapper.findOne(customerId);
        if (apiSettings == null) {
            Customer customer = new Customer();
            customer.setId(customerId);
            ApiResult apiResult = loginService.saveApiSettings(customer);
            if (apiResult.isSuccess()) {
                apiSettings = (ApiSettings) apiResult.getData();
            } else {
                throw new BusinessException(ApiCode.DAO_EXCEPTION.getMsg());
            }
        }
        return apiSettings;
    }
}
