package com.example.smartcontactmanager.com.smart.configuration;

import com.example.smartcontactmanager.com.smart.entities.User;
import com.example.smartcontactmanager.com.smart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User result = userRepository.getUserByUserName(username);
        if(result == null){
            throw new UsernameNotFoundException("Could not found user");
        }
        CustomerUserDetails customerUserDetails = new CustomerUserDetails(result);

        return customerUserDetails;
    }
}
