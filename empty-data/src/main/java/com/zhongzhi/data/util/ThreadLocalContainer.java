package com.zhongzhi.data.util;

import com.zhongzhi.data.entity.agent.AgentSettings;
import com.zhongzhi.data.entity.customer.Customer;
import com.zhongzhi.data.vo.CustomerInfoVo;

import java.util.UUID;

/**
 * 当前线程变量容器
 * @author liuh
 * @date 2021年3月8日
 */
public final class ThreadLocalContainer {
    /**
     * 存储当前线程uuid
     */
    private static final ThreadLocal<String> uuidLocal = new XThreadLocal<>();

    /**
     * 存储当前线程账号id
     */
    private static final ThreadLocal<Long> sysUserIdLocal = new XThreadLocal<>();

    /**
     * 存储当前线程AccountInfo
     */
    private static final ThreadLocal<CustomerInfoVo> customerInfoVoLocal = new XThreadLocal<>();
    
    /**
     * 存储当前线程AccountInfo
     */
    private static final ThreadLocal<Customer> customerLocal = new XThreadLocal<>();

    /**
     * 存储当前线程AgentSettings
     */
    private static final ThreadLocal<AgentSettings> agentSettingsLocal = new XThreadLocal<>();


    public static Long getCustomerId() {
        return sysUserIdLocal.get();
    }

    public static void setCustomerId(long accountNo) {
    	sysUserIdLocal.set(accountNo);
    }
    
    public static Customer getCustomer() {
        return customerLocal.get();
    }

    public static void setCustomer(Customer customer) {
    	customerLocal.set(customer);
    }

    public static CustomerInfoVo getCustomerInfo() {
        return customerInfoVoLocal.get();
    }

    public static void setCustomerInfo(CustomerInfoVo customerInfoVo) {
    	customerInfoVoLocal.set(customerInfoVo);
    }

    public static String getUUID() {
        return uuidLocal.get();
    }

    public static void setUUID() {
        uuidLocal.set(UUID.randomUUID().toString().replace("-", ""));
    }

    public static AgentSettings getAgentSettings() {
        return agentSettingsLocal.get();
    }

    public static void setAgentSettings(AgentSettings customerInfoVo) {
        agentSettingsLocal.set(customerInfoVo);
    }

    /**
     * clear all thread local object
     */
    public static void clearAll() {
    	sysUserIdLocal.remove();
    	customerInfoVoLocal.remove();
        uuidLocal.remove();
        customerLocal.remove();
        agentSettingsLocal.remove();
    }

}
