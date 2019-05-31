package com.mycompany.app.scripts;

import com.mycompany.app.data.EmailProperties;
import com.mycompany.app.data.JobPosition;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Service
public class EmailSender {

    private final static org.apache.log4j.Logger logger = Logger.getLogger(EmailSender.class);

    private EmailProperties properties;

    public EmailSender(EmailProperties properties) {
        this.properties = properties;
    }

    public void sendEmail(JobPosition jobPosition) {

        logger.info("sendEmail is being run");

        //Setting up configurations for the email connection to the Google SMTP server using TLS
        Properties props = new Properties();
        props.put("mail.smtp.host", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getSourceEmailAddress(), properties.getPassword());
            }

        });

        try {

            MimeMessage msg = new MimeMessage(session);

            InternetAddress[] address = InternetAddress.parse(properties.getTargetEmailAddress(), true);

            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(jobPosition.getJobTitle());
            msg.setSentDate(new Date());

            String emailText = "Hi, I hope you have a nice day \n"
                    + "Please check new cool position available to you - " + jobPosition.getHyperLink();
            msg.setText(emailText);
            msg.setHeader("XPriority", "1");
            Transport.send(msg);

            logger.info("sending Email to - " + properties.getTargetEmailAddress() + " message body - " + emailText);

        } catch (MessagingException mex) {
            logger.error("failed to send Email to - " + properties.getTargetEmailAddress() + " using information form, " + jobPosition.toString());
        }
    }
}