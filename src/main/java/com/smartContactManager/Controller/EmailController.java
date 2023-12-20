package com.smartContactManager.Controller;

import com.smartContactManager.model.EmailRequest;
import com.smartContactManager.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    @Autowired
    private EmailService es;

    @RequestMapping("/welcome")
    public String welcome(){
        return "Hello this is my email api !";
    }

    //api to send email
    @PostMapping("/send_email")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest request){

        boolean result = this.es.sendEmail(request.getSubject(), request.getOtp(), request.getTo());

        if(result) {
            System.out.println(request);
            return ResponseEntity.ok("Email is sent successfully...");
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Email not sent..");
        }

    }

}
