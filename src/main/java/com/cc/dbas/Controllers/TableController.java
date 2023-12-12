package com.cc.dbas.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cc.dbas.Services.TableService;
import com.cc.dbas.entity.ApiResponse;
import com.cc.dbas.entity.ColumnDefinition;
import com.cc.dbas.entity.Schema;
import com.cc.dbas.entity.TableDetails;

@RestController
@CrossOrigin
@RequestMapping("/table")
public class TableController {

    @Autowired
    private TableService tableService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createTable(
            @RequestParam int schemaId,
            @RequestParam String tableName,
            @RequestBody ColumnDefinition columnDefinition
    ) {
        try {
            TableDetails createdTable = tableService.createTable(schemaId, tableName, columnDefinition);
            ApiResponse response = new ApiResponse(201, "Table created successfully", createdTable);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/deletTable")
    public ResponseEntity<ApiResponse> deleteSchemaById(@RequestParam int tableId) {
        try {
            boolean schemaExists = tableService.TableExistByID(tableId);
            if (schemaExists==false) {
                ApiResponse response = new ApiResponse(404, "Table not found", null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            tableService.deletTableId(tableId);

            ApiResponse response = new ApiResponse(200, "Table deleted successfully", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @GetMapping("/getTables")
    public ResponseEntity<ApiResponse> getUsersSchema(@RequestParam int schemaId) {
        try {
            List<TableDetails> schemas = tableService.getTablesBySchemaId(schemaId);
            ApiResponse response = new ApiResponse(200, "Tables retrieved successfully", schemas);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
