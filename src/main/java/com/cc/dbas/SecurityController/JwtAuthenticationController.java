package com.cc.dbas.SecurityController;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cc.dbas.DAO.UserRepo;
import com.cc.dbas.SecurityConfig.JwtTokenUtil;
import com.cc.dbas.SecurityConfig.JwtUserDetailsService;
import com.cc.dbas.entity.Users;
import com.cc.dbas.jwtEntity.JwtRequest;
import com.cc.dbas.jwtEntity.JwtResponse;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationController.class);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @RequestMapping(value = "users/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        try {
            authenticate(authenticationRequest.getEmailID(), authenticationRequest.getPassword());

            logger.info("User authenticated successfully: " + authenticationRequest.getEmailID());
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmailID());

            final String token = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            logger.error("Error authenticating user", e);
            throw e;
        }
    }

    @RequestMapping(value = "users/createuser", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestBody Users user) throws Exception {
        try {
            user.setCreatedTime(new Date());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            Users savedUser = userRepo.save(user);

            logger.info("User created successfully: " + savedUser.getEmail()+" ,user id "+savedUser.getUserID());
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            logger.error("Error creating user", e);
            throw e;
        }
    }

    private void authenticate(String emailID, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(emailID, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
