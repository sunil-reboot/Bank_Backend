package com.bank.investment.controller;


import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.bank.investment.model.ChangePasswordRequest;
import com.bank.investment.model.LoginRequest;
import com.bank.investment.model.RegisterRequest;
import com.bank.investment.model.User;
import com.bank.investment.repository.UserRepository;
import com.bank.investment.service.TokenBlackList;
import com.bank.investment.service.UserDetailsService;
import com.bank.investment.util.Constants;
import com.bank.investment.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping(value = "api/client/auth/")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtTokenUtil;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserRepository repository;
    @Autowired
    private TokenBlackList tokenBlacklist;

    @RequestMapping(value = "register/", method = RequestMethod.POST)
    public Map<String, Object> register(@RequestBody final RegisterRequest registerRequest) {
        final Map<String, Object> returnMap = new HashMap<>();
        if(registerRequest.getPhoneNumber().trim().length() > 10 || registerRequest.getPhoneNumber().trim().length() < 10) {
            returnMap.put(Constants.STATUS, Constants.FAILED);
            returnMap.put(Constants.MESSAGE, "invalid phone number");
            return returnMap;
        }
        try {
            final boolean userAlreadyExist = checkIfUserAlreadyExist(registerRequest);
            if (userAlreadyExist) {
                returnMap.put(Constants.STATUS, Constants.FAILED);
                returnMap.put(Constants.MESSAGE, "user already exist");
                return returnMap;
            }
            String phoneNumber = registerRequest.getPhoneNumber();
            if(phoneNumber.trim().length() == 10)
                phoneNumber = "+91"+phoneNumber;

        } catch (final Exception e) {
            returnMap.put(Constants.STATUS, Constants.FAILED);
            returnMap.put(Constants.MESSAGE, e.getMessage());
        }

        return returnMap;
    }

    @RequestMapping(value = "changePassword/", method = RequestMethod.POST)
    public Map<String, Object> changePassword(@RequestBody final ChangePasswordRequest changePasswordRequest) {
        final Map<String, Object> returnMap = new HashMap<>();

        return returnMap;
    }




    @RequestMapping(value = "login/", method = RequestMethod.POST)
    public Map<String, Object> login(@RequestBody final LoginRequest loginRequest) {
        final Map<String, Object> returnMap = new HashMap<>();
        try {
            String phoneNumber = loginRequest.getPhoneNumber().trim();
            log.info("The phone number that we have got is {}", phoneNumber);
            if(phoneNumber.length() < 10 || phoneNumber.length() > 10) {
                 returnMap.put(Constants.STATUS, Constants.FAILED);
                returnMap.put(Constants.MESSAGE, "Incorrect mobile number");
                return returnMap;
            }
            if(phoneNumber.length() == 10)
                phoneNumber = "+91"+phoneNumber;
            User user = repository.findByPhoneNumber(phoneNumber);
            log.info("We have found the user {}", user.toString());
            if(user != null && user.getUserName() != null) {
                final String jwtToken = createAuthenticationToken(loginRequest);
                log.info("jwt token is {}", jwtToken);
                returnMap.put(Constants.STATUS, Constants.SUCCESS);
                returnMap.put(Constants.MESSAGE, "login successful");
                returnMap.put("jwt", jwtToken);
                returnMap.put("roles", user.getRoleName());
                returnMap.put("mobilesNo", user.getPhoneNumber());
                returnMap.put("usersName", user.getPersonName());
            } else {
                returnMap.put(Constants.STATUS, Constants.FAILED);
                returnMap.put(Constants.MESSAGE, "Incorrect Credentials");
            }
        } catch (final Exception e) {
            returnMap.put(Constants.STATUS, Constants.FAILED);
            returnMap.put(Constants.MESSAGE, "Incorrect Credentials");
        }

        return returnMap;
    }

    @RequestMapping(value = "logout/", method = RequestMethod.POST)
    public Map<String, Object> logout(final HttpServletRequest request) {
        final Map<String, Object> returnMap = new HashMap<>();
        final String token = extractTokenFromRequest(request);
        tokenBlacklist.addToBlacklist(token);
        returnMap.put(Constants.STATUS, Constants.SUCCESS);
        return returnMap;
    }

    private String createAuthenticationToken(final LoginRequest authenticationRequest) throws Exception {
        String phoneNumber = authenticationRequest.getPhoneNumber().trim();
        if(phoneNumber.length() == 10)
            phoneNumber = "+91"+phoneNumber;
        try {
            log.info("The phone number that we are passing in createAuthenticationToken is  {}", phoneNumber);
            final Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(phoneNumber, authenticationRequest.getPassword()));
            SecurityContextHolder.getContext()
                    .setAuthentication(authenticate);
        } catch (final BadCredentialsException e) {
            log.error(e.getMessage());
            throw new Exception("Incorrect username or password", e);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber);
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();
        String userName = userDetails.getUsername();// Fetch roles from UserDetails
        User user = repository.findByUserName(userName);
        String role = user.getRoleName();
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // Add a String as a SimpleGrantedAuthority
        authorities.add(new SimpleGrantedAuthority(role));
        return jwtTokenUtil.generateToken(userDetails, authorities); // Pass roles to token generator
    }

    private boolean checkIfUserAlreadyExist(final RegisterRequest registerRequest) {
        String phoneNumber = registerRequest.getPhoneNumber();
        if(phoneNumber.trim().length() == 10)
            phoneNumber = "+91"+phoneNumber;
        final User user = repository.findByPhoneNumber(phoneNumber);
        return user != null;
    }

    private User getUser(String phoneNumber) {
        if(phoneNumber.trim().length() == 10)
            phoneNumber = "+91"+phoneNumber;
        return repository.findByPhoneNumber(phoneNumber);
    }

    private void saveUserDetails(final RegisterRequest registerRequest) {
        String phoneNumber = registerRequest.getPhoneNumber().trim();
        if(phoneNumber.length() == 10)
            phoneNumber = "+91"+phoneNumber;
        final User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setPhoneNumber(phoneNumber);
        user.setUserName(phoneNumber);
        user.setPersonName(registerRequest.getName());
        user.setPassword(registerRequest.getPassword());
        user.setRoleName("USER");
        repository.save(user);
    }

    private void updateUserDetails(final User user, final ChangePasswordRequest changePasswordRequest) {
        user.setPassword(changePasswordRequest.getNewPassword());
        repository.save(user);
    }

    public String extractTokenFromRequest(final HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
