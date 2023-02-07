package com.zhongzhi.data.enums;

/**
 * 检测产品类型枚举
 *
 * @author rivers
 * @since 2019-10-24
 **/
public enum ProductTypeEnum implements BaseEnum {
    EMPTY(0, "空号检测"),
    REALTIME(1, "实时检测"),
    INTERNATIONAL(2, "国际号码检测"),
    DIRECT_COMMON(4, "定向通用检测"),
    LINE_DIRECT(5, "line定向检测"),
    ;

    private Integer code;
    private String desc;

    ProductTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}
