package com.junmooo.lingji.utils;


import com.junmooo.lingji.model.UserToken;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

import java.nio.charset.StandardCharsets;
import java.security.Key;

/**
 * Description:Token生成工具
 * <p>
 * 第一部分我们称它为头部(header),第二部分我们称其为载荷(payload, 类似于飞机上承载的物品)，第三部分是签证(signature).
 * <p>
 * Auth: Frank
 * <p>
 * Date: 2020-11-05
 * <p>
 * Time: 下午 5:05
 */

public class TokenUtils {

    public static String generateUserToken(UserToken userToken, int expire) throws JoseException {
        JwtClaims claims = new JwtClaims();
        claims.setSubject(userToken.getName());
        claims.setClaim("ID", userToken.getId());
        claims.setClaim("NAME", userToken.getName());
        claims.setClaim("EMAIL", userToken.getEmail());
        claims.setClaim("AVATAR", userToken.getAvatar());
        claims.setExpirationTimeMinutesInTheFuture(expire == 0 ? 60 * 24 : expire);

        Key key = new HmacKey("CLIENT".getBytes(StandardCharsets.UTF_8));

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        jws.setKey(key);
        jws.setDoKeyValidation(false); // relaxes the key length requirement

        //签名
        return jws.getCompactSerialization();
    }

    public static UserToken getInfoFromUserToken(String token) throws Exception {

        if (token == null) {
            return null;
        }

        Key key = new HmacKey("CLIENT".getBytes(StandardCharsets.UTF_8));

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setVerificationKey(key)
                .setRelaxVerificationKeyValidation() // relaxes key length requirement
                .build();

        JwtClaims processedClaims = jwtConsumer.processToClaims(token);
        UserToken userToken = UserToken.builder().id(processedClaims.getClaimValue("ID").toString())
                .name(processedClaims.getClaimValue("NAME").toString())
                .id(processedClaims.getClaimValue("ID").toString())
                .build();
        return userToken;
    }

    public static void main(String[] agars) throws Exception {
        UserToken userToken = new UserToken("1", "junmo", "xxx@qq.com", "http://www.baidu.com");
        String token = generateUserToken(userToken, 6000);
        System.out.println(token);
        UserToken infoFromToken = getInfoFromUserToken(token);
        System.out.println(infoFromToken);
    }
}
