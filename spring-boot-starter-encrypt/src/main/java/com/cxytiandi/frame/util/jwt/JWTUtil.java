/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.cxytiandi.frame.util.jwt;

import com.ftx.frame.common.component.SystemConfig;
import com.ftx.frame.util.BaseConstant;
import com.ftx.frame.util.jwt.JWTClaims;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.text.ParseException;

public class JWTUtil {

    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);

    /**
     * get token
     *
     * @return
     */
    public static String getToken(JWTClaims jwtClaims) {

        String token = "";
        Date now = new Date(System.currentTimeMillis());
        Date exp = new Date(now.getTime() + (SystemConfig.EXP_TIME * 60 * 1000));

        try {

            // header
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256, null, "JWT",
                    null, null, null, null, null, null, null, null, null, null);

            // payLoad
            Builder builder = new Builder()
                    .audience(jwtClaims.getAudience())
                    .subject(jwtClaims.getSubject())
                    .issuer(jwtClaims.getIssuer())
                    .expirationTime(exp)
                    .issueTime(now)
                    .claim(BaseConstant.SYSTEM_NAME, SystemConfig.SYSTEM_NAME)
                    .claim(BaseConstant.ROLE, jwtClaims.getRole_code())
                    .claim(BaseConstant.USER, jwtClaims.getUser_code())
                    .claim(BaseConstant.DE_USER, jwtClaims.getDe_user_code())
                    .claim(BaseConstant.BRAN, jwtClaims.getBran_code())
                    .claim(BaseConstant.ROLE_TYPE, jwtClaims.getRole_type())
                    .claim(BaseConstant.ROLE_STRSET, jwtClaims.getRole_strset())
                    .claim(BaseConstant.BRAN_STRSET, jwtClaims.getBran_strset())
                    .jwtID(jwtClaims.getUuid());
            JWTClaimsSet claimsSet = builder.build();

            SignedJWT signedJWT = new SignedJWT(header, claimsSet);

            // signer
            JWSSigner signer = new MACSigner(jwtClaims.getUuid().getBytes());

            // sign
            signedJWT.sign(signer);

            // get token
            token = signedJWT.serialize();

//			logger.info("token = " + token);

        } catch (JOSEException e) {
            e.printStackTrace();
        }
        return token;
    }

    /**
     * verify token and set request
     *
     * @param token
     * @return
     */
    public static boolean verifyToken(String token, HttpServletRequest request) {
        boolean valid = false;
        Date now = new Date(System.currentTimeMillis());
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            JWSVerifier verifier = new MACVerifier(claimsSet.getJWTID().getBytes());
            Object system_name = claimsSet.getClaim(BaseConstant.SYSTEM_NAME);
            if (signedJWT.verify(verifier) && SystemConfig.SYSTEM_NAME.equals(system_name) && claimsSet.getExpirationTime().getTime() > now.getTime()) {
                valid = true;
                request.setAttribute(BaseConstant.USER, claimsSet.getClaim(BaseConstant.USER));
                request.setAttribute(BaseConstant.DE_USER, claimsSet.getClaim(BaseConstant.DE_USER));
                request.setAttribute(BaseConstant.ROLE, claimsSet.getClaim(BaseConstant.ROLE));
                request.setAttribute(BaseConstant.BRAN, claimsSet.getClaim(BaseConstant.BRAN));
                request.setAttribute(BaseConstant.UUID, claimsSet.getJWTID());
            }
        } catch (ParseException | JOSEException e) {
            e.printStackTrace();
        }

        return valid;
    }

}
