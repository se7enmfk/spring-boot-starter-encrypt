/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.util.email;

import javax.mail.internet.InternetAddress;

/**
 * Created by se7en on 2017/6/22.
 */
public class EmailEntity {

    private InternetAddress sendFrom;
    private InternetAddress[] recipientTo;
    private InternetAddress[] recipientCC;
    private InternetAddress[] recipientBCC;
    private String[][] address;
    private String subject;
    private String content;

    private boolean emailInd;

    public String[][] getAddress() {
        return address;
    }

    public EmailEntity setAddress(String[][] address) {
        this.address = address;
        return this;
    }

    public InternetAddress getSendFrom() {
        return sendFrom;
    }

    public EmailEntity setSendFrom(InternetAddress sendFrom) {
        this.sendFrom = sendFrom;
        return this;
    }

    public InternetAddress[] getRecipientTo() {
        return recipientTo;
    }

    public EmailEntity setRecipientTo(InternetAddress[] recipientTo) {
        this.recipientTo = recipientTo;
        return this;
    }

    public InternetAddress[] getRecipientCC() {
        return recipientCC;
    }

    public EmailEntity setRecipientCC(InternetAddress[] recipientCC) {
        this.recipientCC = recipientCC;
        return this;
    }

    public InternetAddress[] getRecipientBCC() {
        return recipientBCC;
    }

    public EmailEntity setRecipientBCC(InternetAddress[] recipientBCC) {
        this.recipientBCC = recipientBCC;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public EmailEntity setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getContent() {
        return content;
    }

    public EmailEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public boolean isEmailInd() {
        return emailInd;
    }

    public EmailEntity setEmailInd(boolean emailInd) {
        this.emailInd = emailInd;
        return this;
    }
}
