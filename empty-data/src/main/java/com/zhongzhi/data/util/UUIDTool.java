package com.zhongzhi.data.util;

import java.util.UUID;

/**
 * uuid工具类
 * @author liuh
 * @date 2021年3月16日
 */
public class UUIDTool {
	
	
	private static UUIDTool uuidtool;
	
	public static UUIDTool getInstance() {  
        if (uuidtool == null) {    
            synchronized (UUIDTool.class) {    
               if (uuidtool == null) {    
            	   uuidtool = new UUIDTool();   
               }    
            }    
        }    
        return uuidtool;   
    }  
	
	/**  
     * 自动生成32位的UUid，对应数据库的主键id进行插入用。  
     * @return  
     */  
    public String getUUID() {    
        return UUID.randomUUID().toString().replace("-", "");  
    }  
}
