package com.smartContactManager.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

@Service
public class EmailService {

    public boolean sendEmail(String subject, String otp, String to){

        //rest of the code ..

        boolean f = false;
        String from = "groveritik316@gmail.com";

        //variable for gmail host
        String host = "smtp.gmail.com";

        //get the system properties
        Properties properties = System.getProperties();
        System.out.println("properties : "+properties);

        //setting important information to properties object

        //host set
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        //Step 1: to get the session object..
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("groveritik316@gmail.com", "efpezcoyqigxcrpq");
            }
        });

        session.setDebug(true);

        //Step 2: compose the message [text, multimedia]
        MimeMessage msg = new MimeMessage(session);

        try {

            //from email
            msg.setFrom(from);

            //adding recipient to message
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            //adding subject to message
            msg.setSubject(subject);

            //adding text to message
            //msg.setText(otp);
            msg.setContent(otp,"text/html");

            //attachment ..
//            String path = "C:\\Users\\HP\\OneDrive\\Pictures\\me15.JPG";
//
//            MimeMultipart mp = new MimeMultipart();
//            //text
//            //file
//
//            MimeBodyPart textMime = new MimeBodyPart();
//            MimeBodyPart fileMime = new MimeBodyPart();
//
//            try{
//                textMime.setText(otp);
//
//                File file = new File(path);
//                fileMime.attachFile(file);
//
//                mp.addBodyPart(textMime);
//                mp.addBodyPart(fileMime);
//
//
//            }catch(Exception e){
//
//            }
//
//            msg.setContent(mp);


            //send

            //Step 3: send the message using Transport class
            Transport.send(msg);

            System.out.println("Sent successfully !!");
            f = true;

        }catch(Exception e){
            e.printStackTrace();
        }

        return f;
    }
}
