package com.smartContactManager.Controller;

import com.smartContactManager.dao.UserRepository;
import com.smartContactManager.entities.User;
import com.smartContactManager.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Controller
public class ForgotController {

    @Autowired
    private EmailService es;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder bcrypt;

    //email id form open handler
    @RequestMapping("/forgot")
    public String openEmailForm(){

        return "forgot_email";
    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session){

        System.out.println("Email : "+email);

        //generating otp of 4 digits
        long number = (long) (Math.random()*(999999-100000+1)+100000);
        System.out.println("OTP : "+number);

        //write code for send otp to email..

        String subject = "OTP from Smart Contact Manager";
        String otp = "<div style='border:2px solid #e2e2e2; padding:20px'>"
                     + "<h1>"
                     + "OTP is "
                     + number
                     + "</h1>"
                     + "</div>";
        String to = email;

        boolean flag = this.es.sendEmail(subject, otp, to);

        if(flag){

            session.setAttribute("myotp", number);
            session.setAttribute("email", email);
            return "verify-otp";
        }else {

            session.setAttribute("message","Check your email id !!");
            return "forgot_email";
        }
    }

    @PostMapping("/verify-otp")
    public String verifyOTP(@RequestParam("otp") long otp, HttpSession session){

        long myOtp = (long) session.getAttribute("myotp");
        String email = (String) session.getAttribute("email");

        if(myOtp == otp){

            //password change form
            User user = this.userRepo.getUserByUserName(email);

            if(user == null){

                // send error message
                session.setAttribute("message", "User does not exists with this email !");

                return "forgot_email";

            }else {

                //send change password form
                return "reset_password";
            }
        }else{

            session.setAttribute("message","You have entered wrong OTP ...");
            return "verify-otp";
        }

    }

    @PostMapping("/reset-password")
    public String changePassword(@RequestParam("newpassword") String password, HttpSession session){

        String email = (String)session.getAttribute("email");

        User user = this.userRepo.getUserByUserName(email);
        user.setPassword(this.bcrypt.encode(password));
        this.userRepo.save(user);

        return "redirect:/user_login?change=Password changed successfully !!";
    }
}
