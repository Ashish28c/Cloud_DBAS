package com.cc.dbas.SecurityConfig;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cc.dbas.DAO.UserRepo;
import com.cc.dbas.entity.UserDTO;
import com.cc.dbas.entity.Users;


@Lazy
@Service
public class JwtUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PasswordEncoder bcryptEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		Users user = userRepo.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + email);
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),
				new ArrayList<>());
	}
	
	public Users save(UserDTO user) {
		Users newUser = new Users();
		newUser.setEmail(user.getEmailID());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		return userRepo.save(newUser);
	}

}
