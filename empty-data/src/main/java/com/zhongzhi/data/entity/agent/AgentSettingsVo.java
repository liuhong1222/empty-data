package com.zhongzhi.data.entity.agent;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.Version;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * <pre>
 * 代理商设置 Vo类
 * </pre>
 *
 * @author rivers
 * @since 2020-02-13
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode
@ApiModel(value = "AgentSettingsVo对象", description = "代理商设置")
public class AgentSettingsVo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "代理商编号")
    @NotNull(message = "代理商编号不能为空")
    private String agentId;

    @ApiModelProperty(value = "代理商名称")
    @Length(max = 20, message = "代理商名称最大长度20")
    private String agentName;

    @ApiModelProperty(value = "代理商logo")
    @Length(max = 200, message = "代理商logo最大长度200")
    private String agentLogo;

    @ApiModelProperty(value = "代理商icon")
    @Length(max = 200, message = "代理商icon最大长度200")
    private String agentIcon;

    @ApiModelProperty(value = "代表签字")
    @Length(max = 200, message = "代表签字最大长度200")
    private String deputySignature;

    @ApiModelProperty(value = "公司红章")
    @Length(max = 200, message = "公司红章最大长度200")
    private String companyChop;

    @ApiModelProperty(value = "短信签名")
    @Length(max = 20, message = "短信签名最大长度20")
    private String smsSignature;

    @ApiModelProperty(value = "代理商域名")
    @Length(max = 64, message = "代理商域名最大长度64")
    private String domain;

    @ApiModelProperty(value = "代理商网站名称")
    @Length(max = 64, message = "代理商域名最大长度64")
    private String siteName;

    @ApiModelProperty(value = "seo优化页面关键词")
    @Length(max = 200, message = "seo优化页面关键词最大长度200")
    private String seoKeywords;

    @ApiModelProperty(value = "seo优化页面描述内容")
    @Length(max = 600, message = "seo优化页面描述内容最大长度600")
    private String seoDescription;

    @ApiModelProperty(value = "51la的站长统计")
    @Length(max = 200, message = "51la的站长统计最大长度200")
    private String la51Src;

    @ApiModelProperty(value = "百度的站长统计")
    @Length(max = 200, message = "百度的站长统计最大长度200")
    private String baiduSrc;

    @ApiModelProperty(value = "浏览器右侧显示，0：否，1：是")
    @NotNull(message = "浏览器右侧显示项不能为空")
    @Range(min = 0, max = 1, message = "浏览器右侧显示参数无效")
    private Integer browserRightDisplay;

    @ApiModelProperty(value = "客服热线")
    @Length(max = 20, message = "客服热线最大长度20")
    private String hotline;

    @ApiModelProperty(value = "客服QQ")
    @Length(max = 20, message = "客服QQ最大长度20")
    private String qq;

    @ApiModelProperty(value = "商务合作号")
    @Length(max = 20, message = "商务合作号最大长度20")
    private String businessCode;

    @ApiModelProperty(value = "微信二维码")
    @Length(max = 200, message = "微信二维码最大长度200")
    private String wechatQrcode;

    @ApiModelProperty(value = "域名版权信息")
    @Length(max = 40, message = "域名版权信息最大长度40")
    private String domainCopyright;

    @ApiModelProperty(value = "公司地址")
    @Length(max = 40, message = "公司地址最大长度40")
    private String domainCompanyAddress;

    @ApiModelProperty(value = "联系方式")
    @Length(max = 20, message = "联系方式最大长度20")
    private String domainContactWay;

    @ApiModelProperty(value = "增值电信业务经营许可证")
    @Length(max = 20, message = "增值电信业务经营许可证最大长度20")
    private String telecomCertification;

    @ApiModelProperty(value = "ICP备案")
    @Length(max = 20, message = "ICP备案最大长度20")
    private String icp;

    @ApiModelProperty(value = "公安备案")
    @Length(max = 20, message = "公安备案最大长度20")
    private String publicSecurityFiling;

    @ApiModelProperty(value = "支付宝应用ID")
    @Length(max = 20, message = "支付宝应用ID最大长度20")
    private String alipayAppid;

    @ApiModelProperty(value = "支付宝网关地址")
    @Length(max = 40, message = "支付宝网关地址最大长度40")
    private String alipayGateway;

    @ApiModelProperty(value = "支付回调地址")
    @Length(max = 100, message = "支付回调地址最大长度100")
    private String alipayNotify;

    @ApiModelProperty(value = "支付宝公钥")
    @Length(max = 500, message = "支付宝公钥最大长度500")
    private String alipayPublicKey;

    @ApiModelProperty(value = "应用私钥")
    @Length(max = 2000, message = "应用私钥最大长度2000")
    private String applicationPrivateKey;

    @ApiModelProperty(value = "合同资料公司名称")
    @Length(max = 20, message = "合同资料公司名称最大长度20")
    private String contactCompanyName;

    @ApiModelProperty(value = "合同资料公司地址")
    @Length(max = 40, message = "合同资料公司地址最大长度40")
    private String contactCompanyAddress;

    @ApiModelProperty(value = "合同资料公司账户")
    @Length(max = 20, message = "合同资料公司账户最大长度20")
    private String contactCompanyAccount;

    @ApiModelProperty(value = "合同资料公司开户行")
    @Length(max = 40, message = "合同资料公司开户行最大长度40")
    private String contactCompanyBank;

    @ApiModelProperty(value = "合同资料邮编")
    @Length(max = 20, message = "合同资料邮编最大长度20")
    private String contactPostcode;

    @ApiModelProperty(value = "合同资料电话")
    @Length(max = 20, message = "合同资料电话最大长度20")
    private String contactPhone;

    @ApiModelProperty(value = "微信网关地址")
    @Length(max = 40, message = "微信网关地址最大长度40")
    private String wechatGateway;

    @ApiModelProperty(value = "微信支付回调地址")
    @Length(max = 100, message = "微信支付回调地址最大长度100")
    private String wechatpayNotify;

    @ApiModelProperty(value = "微信应用ID")
    @Length(max = 20, message = "微信应用ID最大长度20")
    private String wechatAppid;

    @ApiModelProperty(value = "微信mchid")
    @Length(max = 20, message = "微信mchid最大长度20")
    private String wechatMchid;

    @ApiModelProperty(value = "微信key")
    @Length(max = 512, message = "微信key最大长度512")
    private String wechatKey;

    @ApiModelProperty(value = "微信秘钥")
    @Length(max = 64, message = "微信秘钥最大长度64")
    private String wechatAppsecret;

    @ApiModelProperty(value = "服务协议")
    private String agreement;

    @ApiModelProperty(value = "百度营销帐号token")
    private String baiduocpcToken;

    @ApiModelProperty(value = "备注")
    @Length(max = 200, message = "备注最大长度200")
    private String remark;

    @ApiModelProperty(value = "审批状态，0：待审核，9：已认证，1：已驳回")
    private Integer state;

    @Version
    @ApiModelProperty(value = "版本")
    private Integer version;

    @ApiModelProperty(value = "客服昵称")
    @Length(max = 20, message = "客服昵称最大长度20")
    private String kefuNickname;

    @ApiModelProperty(value = "运维人手机号")
    @Length(max = 20, message = "运维人手机号最大长度20")
    private String maintainerPhone;

    @ApiModelProperty(value = "运维人QQ号")
    @Length(max = 20, message = "运维人QQ号最大长度20")
    private String maintainerQq;

    @ApiModelProperty(value = "运维人昵称")
    @Length(max = 20, message = "运维人昵称最大长度20")
    private String maintainerNickname;

    @ApiModelProperty(value = "运维人微信二维码")
    @Length(max = 200, message = "运维人微信二维码最大长度200")
    private String maintainerWechatQrcode;

    @ApiModelProperty(value = "对公收款人")
    @Length(max = 20, message = "对公收款人最大长度20")
    private String payeePublic;

    @ApiModelProperty(value = "对公收款银行")
    @Length(max = 20, message = "对公收款银行最大长度20")
    private String payBankPublic;

    @ApiModelProperty(value = "对公收款账号")
    @Length(max = 20, message = "对公收款账号最大长度20")
    private String payAccountPublic;

    @ApiModelProperty(value = "对私收款人")
    @Length(max = 20, message = "对私收款人最大长度20")
    private String payeePrivate;

    @ApiModelProperty(value = "对私收款银行")
    @Length(max = 20, message = "对私收款银行最大长度20")
    private String payBankPrivate;

    @ApiModelProperty(value = "对私收款账号")
    @Length(max = 20, message = "对私收款账号最大长度20")
    private String payAccountPrivate;

    @ApiModelProperty(value = "微信收款人")
    @Length(max = 20, message = "微信收款人最大长度20")
    private String payeeWechat;

    @ApiModelProperty(value = "微信收款二维码")
    @Length(max = 200, message = "微信收款二维码最大长度200")
    private String payQrcodeWechat;

    @ApiModelProperty(value = "微信收款账号")
    @Length(max = 20, message = "微信收款账号最大长度20")
    private String payAccountWechat;

    @ApiModelProperty(value = "支付宝收款人")
    @Length(max = 20, message = "支付宝收款人最大长度20")
    private String payeeAlipay;

    @ApiModelProperty(value = "支付宝收款二维码")
    @Length(max = 200, message = "支付宝收款二维码最大长度200")
    private String payQrcodeAlipay;

    @ApiModelProperty(value = "支付宝收款账号")
    @Length(max = 20, message = "支付宝收款账号最大长度20")
    private String payAccountAlipay;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "官网类型。1：迅龙，2：步正云")
    private Integer officialWeb;

    @ApiModelProperty(value = "api域名")
    private String apiDomain;

    @ApiModelProperty(value = "电子营业执照地址")
    private String onlineBlUrl;

    @ApiModelProperty(value = "公司简称")
    @Length(max = 20, message = "公司简称最大长度20")
    private String companyShortName;

    /**
     * 代理商设置状态
     */
    public enum State2 {
        /**
         * 待审核：0
         */
        UNAUDITED(0),
        /**
         * 已认证：9
         */
        AUDITED(9),
        /**
         * 已驳回：1
         */
        REJECTED(1);

        private int state;

        State2(int state) {
            this.state = state;
        }

        /**
         * 将数字状态转成枚举类型
         *
         * @param state 数字状态
         * @return 枚举状态，返回null表示无效数字
         */
        public static State2 toState(Integer state) {
            if (state == null) {
                return null;
            }

            for (State2 s : State2.values()) {
                if (s.state == state) {
                    return s;
                }
            }

            return null;
        }

        public Integer getState() {
            return state;
        }
    }
}
