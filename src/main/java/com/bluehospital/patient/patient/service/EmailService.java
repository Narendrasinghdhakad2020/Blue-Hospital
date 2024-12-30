package com.bluehospital.patient.patient.service;

import org.apache.naming.factory.SendMailFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    EmailService(JavaMailSender javaMailSender){
        this.javaMailSender=javaMailSender;
    }

    //method to send email for verification of email
    public void sendVerificationEmail(String to,String code){

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject("Email Verification");
        mail.setText("Your OTP is: "+code);
        javaMailSender.send(mail); //send mail to email of patient
        System.out.println("Email-Service: Email Successfully Sended!");
    }



}
