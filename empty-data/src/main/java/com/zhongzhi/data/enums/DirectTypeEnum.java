package com.zhongzhi.data.enums;

/**
 * 定向国际检测类型
 * @author liuh
 * @date 2022年10月18日
 */
public enum DirectTypeEnum {
    VIBER("viber", "7A4A66DB1B7B7777",ProductTypeEnum.DIRECT_COMMON),
    ZALO("zalo", "431039DC0568D3FD",ProductTypeEnum.DIRECT_COMMON),
    BOTIM("botim", "E6C1CD22E635B389",ProductTypeEnum.DIRECT_COMMON),
    LINE("line", "28D47F60DA5B52FC",ProductTypeEnum.LINE_DIRECT),
    ;

    private String name;
    private String code;
    private ProductTypeEnum productTypeEnum;

    DirectTypeEnum(String name, String code,ProductTypeEnum productTypeEnum) {
        this.code = code;
        this.name = name;
        this.productTypeEnum = productTypeEnum;
    }
    
    public static ProductTypeEnum getProductTypeEnumByName(String name) {
    	DirectTypeEnum[] pes = DirectTypeEnum.values();
        for (DirectTypeEnum pe : pes) {
            if (pe.getName().equals(name)) {
                return pe.getProductTypeEnum();
            }
        }
        return null;
    }
    
    public static ProductTypeEnum getProductTypeEnumByCode(String code) {
    	DirectTypeEnum[] pes = DirectTypeEnum.values();
        for (DirectTypeEnum pe : pes) {
            if (pe.getCode().equals(code)) {
                return pe.getProductTypeEnum();
            }
        }
        return null;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ProductTypeEnum getProductTypeEnum() {
		return productTypeEnum;
	}

	public void setProductTypeEnum(ProductTypeEnum productTypeEnum) {
		this.productTypeEnum = productTypeEnum;
	}
}
