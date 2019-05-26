package com.mycompany.app;

import org.apache.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;


public class EmailSender {

    private final static org.apache.log4j.Logger logger = Logger.getLogger(EmailSender.class);

    private final static String EMAILTO = "spekulents@gmail.com";

    private String EmailPassword;

    public EmailSender(String EmailPassword) {
        this.EmailPassword = EmailPassword;
    }

    public void sendEmail(JobPosition jobPosition) {


        //Setting up configurations for the email connection to the Google SMTP server using TLS
        Properties props = new Properties();
        props.put("mail.smtp.host", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        //Establishing a session with required user details
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("spekulents@gmail.com", EmailPassword);
            }

        });

        try {

            //Creating a Message object to set the email content
            MimeMessage msg = new MimeMessage(session);

            /*Parsing the String with defualt delimiter as a comma by marking the boolean as true and storing the email
            addresses in an array of InternetAddress objects*/
            InternetAddress[] address = InternetAddress.parse(EMAILTO, true);
            //Setting the recepients from the address variable
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(jobPosition.getJobTitle());
            msg.setSentDate(new Date());

            String message = "Hi, I hope you have a nice day \n"
                    + "Please check new cool position available to you - " + jobPosition.getHyperLink();
            msg.setText(message);
            msg.setHeader("XPriority", "1");
            Transport.send(msg);

            logger.info("sending Email to - " + EMAILTO + " message body - " + message);

        } catch (MessagingException mex) {
            logger.error("failed to send Email to - " + EMAILTO + " using information form, " + jobPosition.toString());
        }
    }
}