package com.mycompany.app;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;


public class EmailSender {



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
            //Storing the comma seperated values to email addresses
            String to = "spekulents@gmail.com";
            /*Parsing the String with defualt delimiter as a comma by marking the boolean as true and storing the email
            addresses in an array of InternetAddress objects*/
            InternetAddress[] address = InternetAddress.parse(to, true);
            //Setting the recepients from the address variable
            msg.setRecipients(Message.RecipientType.TO, address);
            String timeStamp = new SimpleDateFormat("yyyymmdd_hh-mm-ss").format(new Date());
            msg.setSubject(jobPosition.getJobTitle());
            msg.setSentDate(new Date());

            msg.setText("Hi, I hope you have a nice day \n"
                    + "Please check new cool position available to you - " + jobPosition.getHyperLink());

            msg.setHeader("XPriority", "1");

            Transport.send(msg);

            System.out.println("Mail has been sent successfully");

        } catch (MessagingException mex) {

            System.out.println("Unable to send an email" + mex);

        }

    }

}