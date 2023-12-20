package com.smartContactManager.config;

import com.smartContactManager.dao.UserRepository;
import com.smartContactManager.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //fetching user from database
        User user = userRepo.getUserByUserName(username);

        if(user == null){
            throw new UsernameNotFoundException("Could not found user !");
        }

        CustomUserDetails cu = new CustomUserDetails(user);

        return cu;
    }
}
