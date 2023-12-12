package com.cc.dbas.DAO;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cc.dbas.entity.TableDetails;

public interface TableRepo extends JpaRepository<TableDetails, Integer> {
	
	void deleteAllBySchemaId(int schemaId);

	

}
