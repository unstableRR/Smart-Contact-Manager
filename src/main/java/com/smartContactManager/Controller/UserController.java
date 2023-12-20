package com.smartContactManager.Controller;

import com.smartContactManager.dao.ContactRepository;
import com.smartContactManager.dao.UserRepository;
import com.smartContactManager.entities.Contact;
import com.smartContactManager.entities.User;
import com.smartContactManager.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ContactRepository contactRepo;

    //method for adding data to response
    @ModelAttribute
    public void addCommonData(Model m, Principal principal){

        String username = principal.getName();
        System.out.println("USERNAME "+username);

        User user = userRepo.getUserByUserName(username);

        System.out.println("USER: "+user);

        m.addAttribute("user", user);

        //get the user using username(email)
    }

    //dashboard home
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal){

        model.addAttribute("title","User Dashboard");
        return "normal/user_dashboard";
    }

    //open add form handler
    @GetMapping("/add_contact")
    public String openAddContactFrom(Model model){

        model.addAttribute("title","Add Contact");
        model.addAttribute("contact", new Contact());

        return "normal/add_contact_form";
    }

    //processing add contact form
    @PostMapping("/process-contact")
    public String processContact(@Valid @ModelAttribute Contact contact,
                                 BindingResult result,
                                 Model model,
                                 @RequestParam("profileImage") MultipartFile file,
                                 Principal principal,
                                 HttpSession session){

       try {
           String name = principal.getName();
           User user = this.userRepo.getUserByUserName(name);

           contact.setUser(user);

           //processing and uploading file

           if(result.hasErrors()){

               System.out.println("ERROR "+result.toString());
               model.addAttribute("contact", contact);
               return "normal/add_contact_form";
           }

           if(file.isEmpty()){
               //if the file is empty then try our message
               System.out.println("File is empty !");
               contact.setImageUrl("user.png");

           }else{
               //upload the file to folder
               contact.setImageUrl(file.getOriginalFilename());

               File saveFile = new ClassPathResource("static/img").getFile();

               Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());


               Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

               System.out.println("Image is uploaded !");


           }

           user.getContacts().add(contact);
           this.userRepo.save(user);
           System.out.println("Added to database");

           //System.out.println(contact);

           model.addAttribute("contact", new Contact());

           //message success...
           session.setAttribute("message", new Message("Your contact is added !", "success"));

       }catch(Exception e){
           System.out.println("Error "+e.getMessage());
           e.printStackTrace();

           //message error...
           session.setAttribute("message", new Message("Something went wrong ! Try again...", "danger"));
       }

        return "normal/add_contact_form";
    }

    //show contacts handler
    @GetMapping("/show_contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal){
        m.addAttribute("title", "Show User Contacts");

        //send the contact list using user

//        String username = principal.getName();
//        User user = this.userRepo.getUserByUserName(username);
//        List<Contact> contacts = user.getContacts();

        String username = principal.getName();
        User user = this.userRepo.getUserByUserName(username);

        //per page = 5 contacts
        //current page = 0 [page]
        Pageable pag = PageRequest.of(page, 4);

        Page<Contact> contacts = this.contactRepo.findContactsByUser(user.getId(), pag);

        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", contacts.getTotalPages());

        return "normal/show_contacts";
    }

    //showing particular contact detail
    @RequestMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal){

        System.out.println( "CID: "+cId);

        Optional<Contact> conOptional = this.contactRepo.findById(cId);
        Contact contact = conOptional.get();

        String username = principal.getName();
        User user = this.userRepo.getUserByUserName(username);

        if(user.getId() == contact.getUser().getId()) {

            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getName());
        }

        return "normal/contact_detail";
    }

    //delete contact handler
    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cId, Model model, Principal principal, HttpSession session){


        try {
            Optional<Contact> contactOptional = this.contactRepo.findById(cId);
            Contact contact = contactOptional.get();

            //check...
            String username = principal.getName();
            User user = this.userRepo.getUserByUserName(username);

            if (user.getId() == contact.getUser().getId()) {

                //delete an image file from folder img
                //contact.getImage();

                System.out.println("COntact image : "+contact.getImageUrl());

                if(!contact.getImageUrl().equals("user.png")) {
                    File deleteFile = new ClassPathResource("static/img").getFile();
                    File file1 = new File(deleteFile, contact.getImageUrl());
                    file1.delete();
                }

                user.getContacts().remove(contact);

                this.userRepo.save(user);
                System.out.println("Deleted !!");

                session.setAttribute("message", new Message("Contact deleted successfully...", "success"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return "redirect:/user/show_contacts/0";
    }

    //open update form handler
    @PostMapping("/update_contact/{cid}")
    public String updateForm(@PathVariable("cid") Integer cid, Model m){

        m.addAttribute("title","Update Contact");

        Contact contact = this.contactRepo.findById(cid).get();

        m.addAttribute("contact",contact);

        return "normal/update_form";
    }

    //update contact handler
    @RequestMapping(value = "/process-update", method = RequestMethod.POST)
    public String updateHandler(@Valid @ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file
            , Model m, HttpSession session,Principal principal){

        try{

            //old contact details
            Contact oldContact = this.contactRepo.findById(contact.getcId()).get();

            //image..
            if(!file.isEmpty()){

                //file work (save new image)
                //delete old photo

                System.out.println("contact image name: "+oldContact.getImageUrl());

                if(!oldContact.getImageUrl().equals("user.png")) {
                    File deleteFile = new ClassPathResource("static/img").getFile();
                    File file1 = new File(deleteFile, oldContact.getImageUrl());
                    file1.delete();
                }

                //update new photo
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                contact.setImageUrl(file.getOriginalFilename());

            }else{
                contact.setImageUrl(oldContact.getImageUrl());
            }

            User user = this.userRepo.getUserByUserName(principal.getName());

            contact.setUser(user);

            this.contactRepo.save(contact);

            session.setAttribute("message", new Message("Your contact is updated...", "success"));

        }catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("CONTACT: "+contact.getName());
        return "redirect:/user/"+contact.getcId()+"/contact";
    }

    //Your profile handler
    @GetMapping("/profile")
    public String yourProfile(Model m){

        m.addAttribute("title", "Profile Page");
        return "normal/profile";
    }

    //open settings handler
    @GetMapping("/settings")
    public String openSettings(Model m){

        m.addAttribute("title", "settings");
        return "normal/settings";
    }

    //change password.. handler
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Principal principal, HttpSession session){

        System.out.println("Old password: "+oldPassword);
        System.out.println("New password: "+newPassword);

        String userName = principal.getName();
        User currentUser = this.userRepo.getUserByUserName(userName);

        if(this.passwordEncoder.matches(oldPassword, currentUser.getPassword())){

            //change the password

            if(newPassword == ""){
                System.out.println("I m here!");
                session.setAttribute("message", new Message("Enter correct new password","danger"));
                return "redirect:/user/settings";
            }

            currentUser.setPassword(this.passwordEncoder.encode(newPassword));
            this.userRepo.save(currentUser);
            session.setAttribute("message", new Message("Your password is successfully changed..","success"));

        }else{
            //error..
            session.setAttribute("message", new Message("please Enter correct password","danger"));
            return "redirect:/user/settings";
        }

        return "redirect:/user/index";
    }

    @PostMapping("/update_user/{id}")
    public String updateUser(@PathVariable("id") Integer id, Model m){

        m.addAttribute("title","Update User");

        User user = this.userRepo.findById(id).get();

        m.addAttribute("user",user);

        return "normal/update_user";
    }

    @PostMapping("/process-update-user")
    public String updateUserHandler(@Valid @ModelAttribute User user, @RequestParam("profileImage") MultipartFile file
            , Model m, HttpSession session,Principal principal){

        try{

            //old contact details
            User oldUser = this.userRepo.getUserByUserName(principal.getName());

            //image..
            if(!file.isEmpty()){

                //file work (save new image)
                //delete old photo

                System.out.println("contact image name: "+oldUser.getImageUrl());

                if(!oldUser.getImageUrl().equals("default.png")) {
                    File deleteFile = new ClassPathResource("static/img").getFile();
                    File file1 = new File(deleteFile, oldUser.getImageUrl());
                    file1.delete();
                }

                //update new photo
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                user.setImageUrl(file.getOriginalFilename());

            }else{
                user.setImageUrl(oldUser.getImageUrl());
            }

            this.userRepo.save(user);

            session.setAttribute("message", new Message("User is updated...", "success"));

        }catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("user: "+user.getName());
        return "redirect:/user/profile";
    }
}
