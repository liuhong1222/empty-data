package com.zhongzhi.data.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zhongzhi.data.constants.Constant;
import com.zhongzhi.data.exception.VerificationCodeException;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FrontTokenUtils {
    /**
     * token秘钥，请勿泄露，请勿随便修改 backups:JKKLJOoasdlfj
     */
    public static final String SECRET = "zhongZhiLogin";

    /**
     * JWT生成Token.<br/>
     * <p>
     * JWT构成: header, payload, signature
     *
     * @param username
     * @param agentId
     */
    public static String createToken(String username, Long agentId, String phone) throws Exception {
        // header Map
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        // build token
        // param backups {iss:Service, aud:APP}
        String token = JWT.create().withHeader(map) // header
                .withClaim("loginName", username)
                .withClaim("agentId", agentId)
                .withClaim("phone", phone)
                //设置过期时间-->间隔一定时间验证是否本人登入
                .withExpiresAt(new Date(System.currentTimeMillis() + Constant.ONE_DAY * 1000))
                .withIssuer("****")//签名是有谁生成 例如 服务器
                .withSubject("*****")//签名的主题
                .withAudience("*****")//签名的观众 也可以理解谁接受签名的
                /*签名 Signature */
                .sign(Algorithm.HMAC256(SECRET)); // signature

        return token;
    }

    /**
     * 解密Token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static Map<String, Claim> verifyToken(String token) {
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
            jwt = verifier.verify(token);
        } catch (Exception e) {
            // e.printStackTrace();
            // token 校验失败, 抛出Token验证非法异常
            throw new VerificationCodeException("token验证信息异常");

        }
        return jwt.getClaims();
    }

    /**
     * 根据Token获取loginName
     *
     * @param token
     * @return loginName
     */
    public static String getLoginName(String token) {
        Map<String, Claim> claims = verifyToken(token);
        Claim loginName_claim = claims.get("loginName");
        if (null == loginName_claim || StringUtils.isEmpty(loginName_claim.asString())) {
            // token 校验失败, 抛出Token验证非法异常
            throw new VerificationCodeException("登录授权信息异常");
        }
        return loginName_claim.asString();
    }

    /**
     * 根据Token获取phone
     * @param token
     * @return loginName
     */
    public static String getPhone(String token) {
        Map<String, Claim> claims = verifyToken(token);
        Claim loginName_claim = claims.get("phone");
        if (null == loginName_claim || StringUtils.isEmpty(loginName_claim.asString())) {
            // token 校验失败, 抛出Token验证非法异常
            throw new VerificationCodeException("登录授权信息异常");
        }
        return loginName_claim.asString();
    }

    /**
     * 根据Token获取user_id
     *
     * @param token
     * @return user_id
     */
    public static Long getAgentId(String token) {
        Map<String, Claim> claims = verifyToken(token);
        Claim agentId_claim = claims.get("agentId");
        if (null == agentId_claim || null == agentId_claim.asLong()) {
            // token 校验失败, 抛出Token验证非法异常
            throw new VerificationCodeException("登录授权信息异常");
        }
        return agentId_claim.asLong();
    }

    /**
     * 从请求头或者请求参数中
     *
     * @return
     */
    public static String getToken() {
        return getToken(HttpServletRequestUtil.getRequest());
    }

    /**
     * 从请求头或者请求参数中
     *
     * @param request
     * @return
     */
    public static String getToken(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request不能为空");
        }
        // 从请求头中获取token
        String token = request.getHeader("token");
        if (org.apache.commons.lang3.StringUtils.isBlank(token)) {
            // 从请求参数中获取token
            token = request.getParameter("token");
        }
        return token;
    }

}

