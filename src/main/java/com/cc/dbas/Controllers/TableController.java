package com.cc.dbas.Controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cc.dbas.Services.TableService;
import com.cc.dbas.entity.ApiResponse;
import com.cc.dbas.entity.ColumnDefinition;
import com.cc.dbas.entity.TableDetails;

@RestController
@CrossOrigin
@RequestMapping("/table")
public class TableController {

    private static final Logger logger = LoggerFactory.getLogger(TableController.class);

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
            logger.info("Table " + tableName + " created successfully in schema " + schemaId);
            ApiResponse response = new ApiResponse(201, "Table created successfully", createdTable);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating table", e);
            ApiResponse response = ApiResponse.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/deletTable")
    public ResponseEntity<ApiResponse> deleteSchemaById(@RequestParam int tableId) {
        try {
            boolean tableExists = tableService.TableExistByID(tableId);
            if (!tableExists) {
                ApiResponse response = new ApiResponse(404, "Table not found", null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            tableService.deletTableId(tableId);

            logger.info("Table with ID " + tableId + " deleted successfully");
            ApiResponse response = new ApiResponse(200, "Table deleted successfully", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in deleting table", e);
            ApiResponse response = ApiResponse.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @GetMapping("/getTables")
    public ResponseEntity<ApiResponse> getUsersSchema(@RequestParam int schemaId) {
        try {
            List<TableDetails> tables = tableService.getTablesBySchemaId(schemaId);
            logger.info("Tables retrieved successfully for schema " + schemaId);
            ApiResponse response = new ApiResponse(200, "Tables retrieved successfully", tables);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in retrieving tables", e);
            ApiResponse response = ApiResponse.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @PostMapping("/addColumn")
    public ResponseEntity<ApiResponse> addColumn(
            @RequestBody Map<String, Object> requestMap
    ) {
        try {
            int tableId = (int) requestMap.get("tableId");
            Map<String, String> newColumn = (Map<String, String>) requestMap.get("newColumn");
            tableService.addColumn(tableId, newColumn);

            logger.info("Column added successfully to table with ID " + tableId);
            ApiResponse response = new ApiResponse(200, "Column added successfully", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error adding column", e);
            ApiResponse response = ApiResponse.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @DeleteMapping("/deleteColumn")
    public ResponseEntity<ApiResponse> deleteColumn(
            @RequestParam int tableId,
            @RequestParam String columnName
    ) {
        try {
            tableService.deleteColumn(tableId, columnName);
            logger.info("Column deleted successfully from table with ID " + tableId);
            ApiResponse response = new ApiResponse(200, "Column deleted successfully", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error deleting column", e);
            ApiResponse response = ApiResponse.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @PutMapping("/updateColumn")
    public ResponseEntity<ApiResponse> changeColumn(
            @RequestParam int tableId,
            @RequestParam String oldColumnName,
            @RequestParam String newColumnName,
            @RequestParam String newDataType
    ) {
        try {
            tableService.changeColumn(tableId, oldColumnName, newColumnName, newDataType);
            logger.info("Column changed successfully in table with ID " + tableId);
            ApiResponse response = new ApiResponse(200, "Column changed successfully", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error changing column", e);
            ApiResponse response = ApiResponse.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @GetMapping("/getAllColumns")
    public ResponseEntity<ApiResponse> getAllColumnsByTableId(@RequestParam int tableId) {
        try {
            List<Map<String, String>> columns = tableService.getAllColumnsByTableId(tableId);
            logger.info("Columns retrieved successfully for table with ID " + tableId);
            ApiResponse response = new ApiResponse(200, "Columns retrieved successfully", columns);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving columns", e);
            ApiResponse response = ApiResponse.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @PostMapping("/copyTable")
    public ResponseEntity<ApiResponse> copyTable(
            @RequestParam int tableId,
            @RequestParam String newTableName
    ) {
        try {
            TableDetails copiedTable = tableService.copyTable(tableId, newTableName);
            logger.info("Table with ID " + tableId + " copied successfully");
            ApiResponse response = new ApiResponse(201, "Table copied successfully", copiedTable);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error copying table", e);
            ApiResponse response = ApiResponse.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
