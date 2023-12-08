package com.cc.dbas.DAO;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cc.dbas.entity.Users;

public interface UserRepo extends JpaRepository<Users, Integer>{
	Users findByEmail(String email);

}
