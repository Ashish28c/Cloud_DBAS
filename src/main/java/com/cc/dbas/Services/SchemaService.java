package com.cc.dbas.Services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cc.dbas.DAO.SchemaRepo;
import com.cc.dbas.DAO.TableRepo;
import com.cc.dbas.entity.Schema;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class SchemaService {
	
	@Autowired
	private SchemaRepo schemaRepo;
	
	@Autowired
	private TableRepo tableRepo;
	
	@Autowired 
	private TableService tableService;
	
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
	 
	 @Transactional
	 public String deletSchemabyId(int schema_id) {
		    Optional<Schema> optionalSchema = schemaRepo.findById(schema_id);
		    
		    if (!optionalSchema.isPresent()) {
		        return "Schema not Found";
		    } else {
		       
		        try {
		        	Optional<Schema> schema = schemaRepo.findById(schema_id);
			        String dropSchemaQuery = "DROP DATABASE IF EXISTS " + schema.get().getSchemaName();
			        entityManager.createNativeQuery(dropSchemaQuery).executeUpdate();
			        schemaRepo.deleteById(schema_id);
			        tableService.deleteTableDetailsBySchemaId(schema_id);
	      					
				} catch (Exception e) {
					System.out.println(e);
				}
		        
		        return "Schema Deleted Successfully";
		    }
		}

		public boolean schemaExistsById(int schemaId) {
		    Optional<Schema> optionalSchema = schemaRepo.findById(schemaId);
		    return optionalSchema.isPresent();
		}
		
		
	
	 
	}
