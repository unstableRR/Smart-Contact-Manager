package com.smartContactManager.Controller;

import com.smartContactManager.dao.ContactRepository;
import com.smartContactManager.dao.UserRepository;
import com.smartContactManager.entities.Contact;
import com.smartContactManager.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ContactRepository contactRepo;

    //search handler
    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){

        System.out.println(query);

        User user = this.userRepo.getUserByUserName(principal.getName());

        List<Contact> contacts = this.contactRepo.findByNameContainingAndUser(query, user);

        return ResponseEntity.ok(contacts);
    }

}
