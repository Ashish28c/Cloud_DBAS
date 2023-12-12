package com.cc.dbas.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cc.dbas.Services.SchemaService;
import com.cc.dbas.entity.ApiResponse;
import com.cc.dbas.entity.Schema;

@RestController
@CrossOrigin
@RequestMapping("/schema")
public class SchemaController {

    @Autowired
    private SchemaService schemaService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createSchema(@Validated @RequestBody Schema schema) {
        try {
            Schema createdSchema = schemaService.saveSchemaDetails(schema);
            ApiResponse response = new ApiResponse(201, "Schema created successfully", createdSchema);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getSchemas")
    public ResponseEntity<ApiResponse> getUsersSchema(@RequestParam int user_id) {
        try {
            List<Schema> schemas = schemaService.getUsersSchema(user_id);
            ApiResponse response = new ApiResponse(200, "Schemas retrieved successfully", schemas);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/deleteSchema")
    public ResponseEntity<ApiResponse> deleteSchemaById(@RequestParam int schemaId) {
        try {
            boolean schemaExists = schemaService.schemaExistsById(schemaId);
           System.out.println(schemaExists);
            if (schemaExists==false) {
                ApiResponse response = new ApiResponse(404, "Schema not found", null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            schemaService.deletSchemabyId(schemaId);

            ApiResponse response = new ApiResponse(200, "Schema deleted successfully", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    
}
