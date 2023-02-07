package com.zhongzhi.data.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhongzhi.data.vo.BusinessLicenseVo;
import com.zhongzhi.data.vo.IdCardIdentifyResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 创蓝文字识别工具类
 */
@Slf4j
public class ChuangLanOcrUtil {

    public static final String APP_ID = "pa1Ji2dT";
    public static final String APP_KEY = "U82xVQXX";
    public static final String ID_URL = "https://api.253.com/open/i/ocr/id-ocr-cl";
    public static final String BUS_URL = "https://api.253.com/open/qtsb/bus_license";

    /**
     * 身份证ocr文字识别
     * @param imageBytes
     * @param idCardSide
     * @return
     * {"chargeStatus":0,"message":"成功","data":{"tradeNo":"21060714185869024","address":"上海市金山区南门村8号","birth":"19191008","name":"姓朴槿惠","cardNum":"","sex":"女","nation":"汉","issuingDate":"","issuingAuthority":"","expiryDate":"","imageStatus":"blurred","direction":""},"code":"200000"}
     */
    public static IdCardIdentifyResultVo idCardOcrByImageBytes(byte[] imageBytes, String idCardSide) {
        CloseableHttpClient client = HttpClients.createDefault();
        // 创建一个Post对象
        HttpPost post = new HttpPost(ID_URL);
        // 创建一个entity模拟一个表单
        String fileBase64 = Base64Utils.encodeToString(imageBytes); // 身份证图片转码或者url
        String ocrType = "front".equalsIgnoreCase(idCardSide) ? "0" : "1"; // 标识身份证正反面 0表示正面，1表示反面
        List list = new ArrayList<>();
        // 视频文件的base64编码
        list.add(new BasicNameValuePair("imageType","BASE64"));
        list.add(new BasicNameValuePair("appKey", APP_KEY));
        list.add(new BasicNameValuePair("appId", APP_ID));
        list.add(new BasicNameValuePair("image", fileBase64));
        list.add(new BasicNameValuePair("ocrType", ocrType));
        CloseableHttpResponse response = null;

        try {
            // 包装成一个entity对象
            StringEntity entity = new UrlEncodedFormEntity(list,"utf-8");
            // 设置请求内容
            post.setEntity(entity);
            // 执行请求内容
            response = client.execute(post);
            int code = response.getStatusLine().getStatusCode();
            if(code != 200) {
                log.info("http连接错误: {}", response);
                return null;
            }
            HttpEntity contentEntity = response.getEntity();
            String content = EntityUtils.toString(contentEntity);
            log.info("身份证ocr接口返回：{}", content);
            if(StringUtils.isBlank(content)) {
                return null;
            }
            JSONObject resp = JSON.parseObject(content);
            String resultCode = resp.getString("code");
            if (StringUtils.equals("200000", resultCode)) {
                JSONObject jsonObject = resp.getJSONObject("data");
                IdCardIdentifyResultVo idCardIdentifyResultVo = new IdCardIdentifyResultVo();
                if ("front".equalsIgnoreCase(idCardSide)) {
                    String address = jsonObject.getString("address");
                    String name = jsonObject.getString("name");
                    String cardNum = jsonObject.getString("cardNum");
                    idCardIdentifyResultVo.setIdCardAddress(address)
                            .setIdCardName(name)
                            .setIdCardNumber(cardNum);
                } else {
                    String idCardExpireStartTime = jsonObject.getString("issuingDate");
                    if (StringUtils.isNotBlank(idCardExpireStartTime) && idCardExpireStartTime.length() >= 8) {
                        idCardExpireStartTime = idCardExpireStartTime.replaceAll("(?i)(\\d{4})(\\d{2})(\\d{2})", "$1-$2-$3");
                    }
                    String idCardExpireEndTime = jsonObject.getString("expiryDate");
                    if (StringUtils.isNotBlank(idCardExpireEndTime) && idCardExpireEndTime.length() >= 8) {
                        idCardExpireEndTime = idCardExpireEndTime.replaceAll("(?i)(\\d{4})(\\d{2})(\\d{2})", "$1-$2-$3");
                    }

                    idCardIdentifyResultVo.setIdCardExpireStartTime(idCardExpireStartTime)
                            .setIdCardExpireEndTime(idCardExpireEndTime);
                }
                log.info("身份证文字识别结果：{}", idCardIdentifyResultVo);
                return idCardIdentifyResultVo;
            } else {
                log.warn("身份证ocr识别返回响应码不正确: {}", content);
            }
        } catch (IOException e) {
            log.error("身份证ocr识别发生异常", e);
        } finally {
            IOUtils.close(response, client);
        }
       return null;
    }

    /**
     * 营业执照ocr识别
     * @param imageBytes
     * @return
     * "{\"tradeNo\":\"851485504565284864\",\"chargeStatus\":1,\"message\":\"成功\",\"data\":{\"msg\":\"SUCCESS\",\"data\":{\"log_id\":\"8e697f8419784516a71f8512f4ed4789\",\"words_result\":{\"社会信用代码\":{\"words\":\"91330200750364874C\"},\"组成形式\":{\"words\":\"\"},\"经营范围\":{\"words\":\"商用宁询，商用鞋和交调及具事配的、电子元具件、智能控制系统的研发、新造，称测、销售、安装、维修及相关信息咨询服务；日营和代理各类流品和技术营进出口业务，和济家和龙经营围禁业进出口的息物的技术服外、依法及件批用的项目，经相关部门批准后方可开展经营活动)\"},\"成立日期\":{\"words\":\"2003年06月24日\"},\"法人\":{\"words\":\"崔华波\"},\"注册资本\":{\"words\":\"110000000元整\"},\"证件编号\":{\"words\":\"\"},\"地址\":{\"words\":\"宁波市鄞州区姜山镇明光北路1166号\"},\"单位名称\":{\"words\":\"宁波奥克斯电气股份有限公司\"},\"有效期\":{\"words\":\"长期\"},\"类型\":{\"words\":\"股份有限公司(非上市)\"}},\"risk_type\":\"normal\",\"words_result_num\":11,\"image_status\":\"normal\"},\"status\":1},\"code\":\"200000\"}"
     *
     * {
     * 	"tradeNo": "851486134641885184",
     * 	"chargeStatus": 1,
     * 	"message": "成功",
     * 	"data": {
     * 		"msg": "SUCCESS",
     * 		"data": {
     * 			"log_id": "40050626d871424c93e153a8653e4226",
     * 			"words_result": {
     * 				"社会信用代码": {
     * 					"words": "91330200750364874C"
     *                                },
     * 				"组成形式": {
     * 					"words": ""
     *                },
     * 				"经营范围": {
     * 					"words": "商用宁询，商用鞋和交调及具事配的、电子元具件、智能控制系统的研发、新造，称测、销售、安装、维修及相关信息咨询服务；日营和代理各类流品和技术营进出口业务，和济家和龙经营围禁业进出口的息物的技术服外、依法及件批用的项目，经相关部门批准后方可开展经营活动)"
     *                },
     * 				"成立日期": {
     * 					"words": "2003年06月24日"
     *                },
     * 				"法人": {
     * 					"words": "崔华波"
     *                },
     * 				"注册资本": {
     * 					"words": "110000000元整"
     *                },
     * 				"证件编号": {
     * 					"words": ""
     *                },
     * 				"地址": {
     * 					"words": "宁波市鄞州区姜山镇明光北路1166号"
     *                },
     * 				"单位名称": {
     * 					"words": "宁波奥克斯电气股份有限公司"
     *                },
     * 				"有效期": {
     * 					"words": "长期"
     *                },
     * 				"类型": {
     * 					"words": "股份有限公司(非上市)"
     *                }
     * 			},
     * 			"risk_type": "normal",
     * 			"words_result_num": 11,
     * 			"image_status": "normal"
     *        },
     * 		"status": 1
     * 	},
     * 	"code": "200000"
     * }
     */
    public static BusinessLicenseVo bussinessLicenseOcrByImageBytes(byte[] imageBytes) {
        CloseableHttpClient client = HttpClients.createDefault();
        // 创建一个Post对象
        HttpPost post = new HttpPost(BUS_URL);
        // 创建一个entity模拟一个表单
        String fileBase64 = Base64Utils.encodeToString(imageBytes); // 图片转码或者url
        List list = new ArrayList<>();
        // 视频文件的base64编码
        list.add(new BasicNameValuePair("appKey", APP_KEY));
        list.add(new BasicNameValuePair("appId", APP_ID));
        list.add(new BasicNameValuePair("image", fileBase64));
        list.add(new BasicNameValuePair("fixMode", "1"));
        CloseableHttpResponse response = null;

        try {
            // 包装成一个entity对象
            StringEntity entity = new UrlEncodedFormEntity(list,"utf-8");
            // 设置请求内容
            post.setEntity(entity);
            // 执行请求内容
            response = client.execute(post);
            int code = response.getStatusLine().getStatusCode();
            if(code != 200) {
                log.info("http连接错误: {}", response);
                return null;
            }
            HttpEntity contentEntity = response.getEntity();
            String content = EntityUtils.toString(contentEntity);
            log.info("营业执照ocr接口返回：{}", content);
            if(StringUtils.isBlank(content)) {
                return null;
            }
            JSONObject resp = JSON.parseObject(content);
            String resultCode = resp.getString("code");
            if (StringUtils.equals("200000", resultCode)) {
                JSONObject jsonObject = resp.getJSONObject("data").getJSONObject("data");
                JSONObject wordsResult = jsonObject.getJSONObject("words_result");
                String socialCreditCode = wordsResult.getJSONObject("社会信用代码").getString("words");
                String compositionForm = wordsResult.getJSONObject("组成形式").getString("words");
                String businessScope = wordsResult.getJSONObject("经营范围").getString("words");
                String legalPerson = wordsResult.getJSONObject("法人").getString("words");
                String establishmentDate = wordsResult.getJSONObject("成立日期").getString("words");
                String registeredCapital = wordsResult.getJSONObject("注册资本").getString("words");
                String idNo = wordsResult.getJSONObject("证件编号").getString("words");
                String address = wordsResult.getJSONObject("地址").getString("words");
                String companyName = wordsResult.getJSONObject("单位名称").getString("words");
                String type = wordsResult.getJSONObject("类型").getString("words");
                String validityTerm = wordsResult.getJSONObject("有效期").getString("words");
                BusinessLicenseVo businessLicenseVo = new BusinessLicenseVo();
                businessLicenseVo.setAddress(address)
                        .setBusinessScope(businessScope)
                        .setCompanyName(companyName)
                        .setCompositionForm(compositionForm)
                        .setEstablishmentDate(establishmentDate)
                        .setIdNo(idNo)
                        .setLegalPerson(legalPerson)
                        .setRegisteredCapital(registeredCapital)
                        .setSocialCreditCode(socialCreditCode)
                        .setType(type)
                        .setValidityTerm(validityTerm);

                log.info("营业执照文字识别结果：{}", businessLicenseVo);
                return businessLicenseVo;
            } else {
                log.warn("营业执照文字识别返回响应码不正确: {}", content);
            }
        } catch (IOException e) {
            log.error("营业执照文字识别发生异常", e);
        } finally {
            IOUtils.close(response, client);
        }
        return null;
    }

}
