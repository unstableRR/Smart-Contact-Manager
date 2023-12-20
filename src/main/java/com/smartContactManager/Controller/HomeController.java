package com.smartContactManager.Controller;

import com.smartContactManager.dao.UserRepository;
import com.smartContactManager.entities.User;
import com.smartContactManager.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class HomeController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @RequestMapping("/")
    public String home(Model model){
        model.addAttribute("title","Home - Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About - Smart Contact Manager");
        return "about";
    }

    @RequestMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title","Register - Smart Contact Manager");
        model.addAttribute("user",new User());
        return "signup";
    }

    //handler for registering user
    @PostMapping("/do_register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result1,
                               Model model,
                               @RequestParam("userImage") MultipartFile file,
                               @RequestParam(value="agreement", defaultValue = "false") boolean agreement,
                               HttpSession session){

        try {
            if(!agreement){
                System.out.println("You have not agreed the terms and conditions");
                throw new Exception("You have not agreed the terms and conditions");
            }

            if(result1.hasErrors()){

                System.out.println("ERROR "+result1.toString());
                model.addAttribute("user",user);
                return "signup";
            }

            System.out.println("NAMASTE !");

            if(file.isEmpty()) {
                //if the file is empty then try our message
                System.out.println("File is empty !");
                user.setImageUrl("default.png");

            }else{
                //upload the file to folder
                user.setImageUrl(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());


                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image is uploaded !");


            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);
           // user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            System.out.println("Agreement "+agreement);
            System.out.println("User "+user);

            User result= this.repo.save(user);

            model.addAttribute("user",new User());

            session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));
            return "signup";

        }catch (Exception e){
             e.printStackTrace();
             model.addAttribute("user", user);
             session.setAttribute("message", new Message("Something went wrong ! "+e.getMessage(), "alert-danger"));
        }


        return "signup";
    }

    //handler for custom login
    @GetMapping("/user_login")
    public String customLogin(Model model){
        model.addAttribute("title","Login Page");
        return "login";
    }

    @GetMapping("/test")
    @ResponseBody
    public String test(){
        User user = new User();
        user.setName("Ritik");
        user.setEmail("r@gmail.com");
        repo.save(user);
        return "Working";
    }
}
