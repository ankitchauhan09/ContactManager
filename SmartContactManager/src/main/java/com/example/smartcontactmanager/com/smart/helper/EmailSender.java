package com.example.smartcontactmanager.com.smart.helper;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


@Component
public class EmailSender {

    public boolean sendEmail(String to, int otp){
        boolean flag = false;
        final String from = "ankit2211chauhan@gmail.com";
        final String password = "zcid fhaz cimh dicb";
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", true);
        prop.put("mail.smtp.port", 587);
        prop.put("mail.smtp.host", "smtp.gmail.com");


        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try{
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("OTP for forget password");
            message.setText("OTP for forgetting password for the email "+to+" is : "+otp+"\nEnter this otp to proceed further....");
            Transport.send(message);
            flag = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return flag;
    }

}
