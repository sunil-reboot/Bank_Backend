package com.bank.investment.service;

import java.util.ArrayList;

import com.bank.investment.model.User;
import com.bank.investment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(final String phoneNumber) throws UsernameNotFoundException {
        final User user = repository.findByUserName(phoneNumber);
        if (user == null) {
            throw new UsernameNotFoundException("Incorrect username");
        }
        String phNo = String.valueOf(user.getUserName());
        return new org.springframework.security.core.userdetails.User(phNo, user.getPassword(), new ArrayList<>());
    }
}
