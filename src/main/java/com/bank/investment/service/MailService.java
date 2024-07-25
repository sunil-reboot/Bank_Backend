package com.bank.investment.service;

import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Slf4j
public class MailService {

    public void sendEmailToNominee() {
        String to = "sunilpareekps@gmail.com";
        //String to = "amrhusain@gmail.com";
        String from = "amrhusain@gmail.com";

        String host = "smtp.gmail.com";

        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("amrhusain@gmail.com", "ftkf hxiv bked pzkr");

            }

        });

        // Used to debug SMTP issues
        session.setDebug(false);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            message.setSubject("Your better investment plan!");
            message.setContent("<html><a href='www.eenadu.net'>Click Here</a></html>", "text/html; charset=utf-8");
            // Now set the actual message
            //message.setText("Mail aaya kya!!");

            log.info("sending...");
            // Send message
            Transport.send(message);
            log.info("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

}
