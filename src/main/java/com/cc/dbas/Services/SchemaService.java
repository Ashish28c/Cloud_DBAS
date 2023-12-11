package com.cc.dbas.Services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cc.dbas.DAO.SchemaRepo;
import com.cc.dbas.entity.Schema;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class SchemaService {
	
	@Autowired
	private SchemaRepo schemaRepo;
	
	@Autowired
	private EntityManager entityManager;
	
	@Transactional
	public Schema saveSchemaDetails(Schema schema) {
		schema.setCreated_datetime(new Date());
		schemaRepo.save(schema);
		entityManager.persist(schema);
        String nativeSql = "CREATE SCHEMA " + schema.getSchemaName();
        entityManager.createNativeQuery(nativeSql).executeUpdate();
		return schema;	
	}
	
	
	 @Transactional
	    public List<Schema> getUsersSchema(int userId) {
	        String nativeQuery = "SELECT * FROM schema_details WHERE user_id = ?";
	        List<Schema> schemaList = entityManager.createNativeQuery(nativeQuery, Schema.class)
	                .setParameter(1, userId)
	                .getResultList();
	        return schemaList;
	    }
	 
	 
	 public String deletSchemabyId(int schema_id) {
		 
		 if(schemaRepo.findById(schema_id) != null) {
			 schemaRepo.deleteById(schema_id);			 
		 }
		return null;
		
	 }
	 
	}
