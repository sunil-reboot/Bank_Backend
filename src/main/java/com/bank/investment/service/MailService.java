package com.bank.investment.service;

import com.bank.investment.model.NomineeDetailsT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Slf4j
@Service
public class MailService {

    public void sendEmailToNominee(final NomineeDetailsT nomineeDetailsT) {
        String to = "";
        //String to = "amrhusain@gmail.com";
        String from = "amrhusain@gmail.com";
        String nameOfUser = nomineeDetailsT.getUserName();
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
            to = nomineeDetailsT.getEmail();
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            message.setSubject(nameOfUser+" has requested you for financial suggestions!");
            String suggestionUrl = nomineeDetailsT.getUrl();
            message.setContent("<html><a href='"+suggestionUrl+"'>Click Here</a></html>", "text/html; charset=utf-8");

            log.info("sending...");
            // Send message
            Transport.send(message);
            log.info("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

}
