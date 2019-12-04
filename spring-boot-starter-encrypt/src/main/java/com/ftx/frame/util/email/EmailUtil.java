/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.ftx.frame.util.email;

import com.ftx.frame.common.component.SystemConfig;
import com.ftx.frame.util.BaseConstant;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * Created by se7en on 2017/6/21.
 */
public class EmailUtil {

    // 发件人的 邮箱 和 密码（替换为自己的邮箱和密码）
    // PS: 某些邮箱服务器为了增加邮箱本身密码的安全性，给 SMTP 客户端设置了独立密码（有的邮箱称为“授权码”）,
    //     对于开启了独立密码的邮箱, 这里的邮箱密码必需使用这个独立密码（授权码）。
    //public static String hostEmailAccount = "sevenzhou@ftecx.com";
    //public static String hostEmailSender = "se7en";
    // public static String hostEmailPassword = "";

    // 发件人邮箱的 SMTP 服务器地址, 必须准确, 不同邮件服务器地址不同, 一般(只是一般, 绝非绝对)格式为: smtp.xxx.com
    // 网易163邮箱的 SMTP 服务器地址为: smtp.163.com
    // public static String hostEmailSMTPHost = "smtp.exmail.qq.com";

    // 收件人邮箱（替换为自己知道的有效邮箱）
    //public static String receiveMailAccount = "sevenzhou@ftecx.com";


    private static void sendEmail(EmailEntity emailEntity) throws Exception {

        //是否发送email
        emailEntity.setEmailInd(BaseConstant.STRING_TRUE.equals(SystemConfig.getProperty("base.emailInd")));
        if (!emailEntity.isEmailInd()) return;

        if (emailEntity.getSendFrom() == null) {
            emailEntity.setSendFrom(new InternetAddress(SystemConfig.HOST_EMAIL_ACCOUNT, SystemConfig.HOST_EMAIL_SENDER, BaseConstant.UTF8));
        }
        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", SystemConfig.HOST_EMAIL_SMTP);   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证

        // PS: 某些邮箱服务器要求 SMTP 连接需要使用 SSL 安全认证 (为了提高安全性, 邮箱支持SSL连接, 也可以自己开启),
        //     如果无法连接邮件服务器, 仔细查看控制台打印的 log, 如果有有类似 “连接失败, 要求 SSL 安全连接” 等错误,
        //     打开下面 *//* ... *//* 之间的注释代码, 开启 SSL 安全连接。
        //
        // SMTP 服务器的端口 (非 SSL 连接的端口一般默认为 25, 可以不添加, 如果开启了 SSL 连接,
        //                  需要改为对应邮箱的 SMTP 服务器的端口, 具体可查看对应邮箱服务的帮助,
        //                  QQ邮箱的SMTP(SLL)端口为465或587, 其他邮箱自行去查看)
        /*final String smtpPort = "465";
        props.setProperty("mail.smtp.port", smtpPort);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", smtpPort);*/


        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);                                 // 设置为debug模式, 可以查看详细的发送 log

        // 3. 创建一封邮件

        MimeMessage message = createMimeMessage(session, emailEntity);

        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();

        // 5. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
        //
        //    PS_01: 成败的判断关键在此一句, 如果连接服务器失败, 都会在控制台输出相应失败原因的 log,
        //           仔细查看失败原因, 有些邮箱服务器会返回错误码或查看错误类型的链接, 根据给出的错误
        //           类型到对应邮件服务器的帮助网站上查看具体失败原因。
        //
        //    PS_02: 连接失败的原因通常为以下几点, 仔细检查代码:
        //           (1) 邮箱没有开启 SMTP 服务;
        //           (2) 邮箱密码错误, 例如某些邮箱开启了独立密码;
        //           (3) 邮箱服务器要求必须要使用 SSL 安全连接;
        //           (4) 请求过于频繁或其他原因, 被邮件服务器拒绝服务;
        //           (5) 如果以上几点都确定无误, 到邮件服务器网站查找帮助。
        //
        //    PS_03: 仔细看log, 认真看log, 看懂log, 错误原因都在log已说明。
        transport.connect(SystemConfig.HOST_EMAIL_ACCOUNT, SystemConfig.HOST_EMAIL_PASSWORD);

        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());

        // 7. 关闭连接
        transport.close();
    }

    /**
     * 创建一封只包含文本的简单邮件
     *
     * @param session     和服务器交互的会话
     * @param emailEntity
     * @return
     * @throws Exception
     */
    private static MimeMessage createMimeMessage(Session session, EmailEntity emailEntity) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);

        // 2. From: 发件人
        message.setFrom(emailEntity.getSendFrom());

        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        if (emailEntity.getRecipientTo() != null)
            message.setRecipients(MimeMessage.RecipientType.TO, emailEntity.getRecipientTo());

        if (emailEntity.getRecipientCC() != null)
            message.setRecipients(MimeMessage.RecipientType.CC, emailEntity.getRecipientCC());

        if (emailEntity.getRecipientBCC() != null)
            message.setRecipients(MimeMessage.RecipientType.BCC, emailEntity.getRecipientBCC());

        //    To: 增加收件人（可选）
        // message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress("dd@receive.com", "USER_DD", "UTF-8"));
        //    Cc: 抄送（可选）
        // message.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress("ee@receive.com", "USER_EE", "UTF-8"));
        //    Bcc: 密送（可选）
        //  message.setRecipient(MimeMessage.RecipientType.BCC, new InternetAddress("ff@receive.com", "USER_FF", "UTF-8"));

        // 4. Subject: 邮件主题
        message.setSubject(emailEntity.getSubject(), BaseConstant.UTF8);

        // 5. Content: 邮件正文（可以使用html标签）
        message.setContent(emailEntity.getContent(), BaseConstant.CONTENT_TYPE);

        // 6. 设置发件时间
        message.setSentDate(new Date());

        // 7. 保存设置
        message.saveChanges();

        return message;
    }

    public static void wrapAndSendEmail(EmailEntity emailEntity) {
            try {
                String[][] address = emailEntity.getAddress();
                InternetAddress[] addresses = new InternetAddress[address.length];
                for (int i = 0; i < address.length; i++) {
                    addresses[i] = new InternetAddress(address[i][0], address[i][1], BaseConstant.UTF8);
                }
                emailEntity.setRecipientTo(addresses);
                emailEntity.setEmailInd(BaseConstant.STRING_TRUE.equals(SystemConfig.getProperty("base.emailInd")));
                EmailUtil.sendEmail(emailEntity);

            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    public static void main(String[] args) throws Exception {
        EmailEntity emailEntity = new EmailEntity();

        InternetAddress[] addresses = new InternetAddress[2];
        addresses[0] = new InternetAddress(SystemConfig.HOST_EMAIL_ACCOUNT, "XX用户", "UTF-8");
        addresses[1] = new InternetAddress(SystemConfig.HOST_EMAIL_ACCOUNT, "XX用户1", "UTF-8");
        emailEntity.setRecipientTo(addresses);
        emailEntity.setSubject("打折钜惠");
        emailEntity.setContent("X用户你好, 今天全场5折, 快来抢购, 错过今天再等一年");
        emailEntity.setEmailInd(true);
        sendEmail(emailEntity);
    }
}