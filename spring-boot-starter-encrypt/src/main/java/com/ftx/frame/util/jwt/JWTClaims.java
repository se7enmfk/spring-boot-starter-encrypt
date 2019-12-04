/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.util.jwt;

/**
 * Created by se7en on 2017/6/22.
 */
public class JWTClaims {

    private String audience;
    private String subject;
    private String issuer;
    private String uuid;


    private String user_code;
    private String de_user_code;
    private String role_code;
    private String role_type;
    private String role_strset;
    private String bran_code;
    private String bran_strset;

    public String getUuid() {
        return uuid;
    }

    public JWTClaims setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getAudience() {
        return audience;
    }

    public JWTClaims setAudience(String audience) {
        this.audience = audience;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public JWTClaims setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getIssuer() {
        return issuer;
    }

    public JWTClaims setIssuer(String issuer) {
        this.issuer = issuer;
        return this;
    }

    public String getUser_code() {
        return user_code;
    }

    public JWTClaims setUser_code(String user_code) {
        this.user_code = user_code;
        return this;
    }

    public String getDe_user_code() {
        return de_user_code;
    }

    public JWTClaims setDe_user_code(String de_user_code) {
        this.de_user_code = de_user_code;
        return this;
    }

    public String getRole_code() {
        return role_code;
    }

    public JWTClaims setRole_code(String role_code) {
        this.role_code = role_code;
        return this;
    }

    public String getRole_type() {
        return role_type;
    }

    public JWTClaims setRole_type(String role_type) {
        this.role_type = role_type;
        return this;
    }

    public String getRole_strset() {
        return role_strset;
    }

    public JWTClaims setRole_strset(String role_strset) {
        this.role_strset = role_strset;
        return this;
    }

    public String getBran_code() {
        return bran_code;
    }

    public JWTClaims setBran_code(String bran_code) {
        this.bran_code = bran_code;
        return this;
    }

    public String getBran_strset() {
        return bran_strset;
    }

    public JWTClaims setBran_strset(String bran_strset) {
        this.bran_strset = bran_strset;
        return this;
    }
}
