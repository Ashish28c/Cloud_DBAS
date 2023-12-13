package com.cc.dbas.Services;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cc.dbas.DAO.SchemaRepo;
import com.cc.dbas.DAO.TableRepo;
import com.cc.dbas.entity.ColumnDefinition;
import com.cc.dbas.entity.Schema;
import com.cc.dbas.entity.TableDetails;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class TableService {

    @Autowired
    private TableRepo tableRepo;

    @Autowired
    private SchemaRepo schemaRepo;

    @Autowired
    private EntityManager entityManager;

    
	private TableService tableService;

    @Transactional
    public TableDetails createTable(int schemaId, String tableName, ColumnDefinition columnDefinition) {
        Optional<Schema> optionalSchema = schemaRepo.findById(schemaId);
        if (!optionalSchema.isPresent()) {
            throw new RuntimeException("Schema not found");
        }

        Schema schema = optionalSchema.get();

        TableDetails tableDetails = new TableDetails();
        tableDetails.setSchemaId(schemaId);
        tableDetails.setTableName(tableName);
        tableDetails.setColumns(columnDefinition.getColumns());
        tableDetails.setCreatedDatetime(new Date());
        tableDetails.setUpdatedDatetime(new Date());

        // Save the table details
        tableDetails = tableRepo.save(tableDetails);

        StringBuilder columnDefinitions = new StringBuilder();
        Map<String, String> columns = columnDefinition.getColumns();
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            columnDefinitions.append(entry.getKey()).append(" ").append(entry.getValue()).append(", ");
        }
        // Remove the trailing comma
        columnDefinitions.delete(columnDefinitions.length() - 2, columnDefinitions.length());

        // Execute native query to create the table in the database
        String createTableQuery = "CREATE TABLE " + schema.getSchemaName() + "." + tableName +
                " (" + columnDefinitions.toString() + ")";
        entityManager.createNativeQuery(createTableQuery).executeUpdate();
        return tableDetails;
    }
    
    
    @Transactional
    public void deleteTableDetailsBySchemaId(int schemaId) {
        // Execute native query to delete rows from TableDetails where schemaId is specific
        String deleteTablesQuery = "DELETE FROM tables_details WHERE schema_id = :schemaId";
        Query query = entityManager.createNativeQuery(deleteTablesQuery);
        query.setParameter("schemaId", schemaId);
        query.executeUpdate();
    }
    
    
    @Transactional
	 public String deletTableId(int table_id) {
		    Optional<TableDetails> optionalTableDetails = tableRepo.findById(table_id);
		    Integer SchemaId = optionalTableDetails.get().getSchemaId();
		    
		    if (!optionalTableDetails.isPresent()) {
		        return "Table not found";
		    } else {
		       
		        try {
		        	Optional<Schema> schema = schemaRepo.findById(SchemaId);
		        	String deleteTableQuery = "DROP TABLE " + schema.get().getSchemaName() + "." + optionalTableDetails.get().getTableName();
			        entityManager.createNativeQuery(deleteTableQuery).executeUpdate();
			        tableRepo.deleteById(table_id);
	      					
				} catch (Exception e) {
					System.out.println(e);
				}
		        
		        return "Table Deleted Successfully";
		    }
		}
    
    public boolean TableExistByID(int tabelId) {
	    Optional<Schema> optionalSchema = schemaRepo.findById(tabelId);
	    return optionalSchema.isPresent();
	}
    
    @Transactional
    public List<TableDetails> getTablesBySchemaId(int schemaId) {
        String nativeQuery = "SELECT * FROM tables_details WHERE schema_id = ?";
        List<TableDetails> TableDetailList = entityManager.createNativeQuery(nativeQuery, TableDetails.class)
                .setParameter(1, schemaId)
                .getResultList();
        return TableDetailList;
    }
    
    @Transactional
    public void addColumn(int tableId, Map<String, String> newColumn) {
        // Fetch existing table details
        Optional<TableDetails> optionalTableDetails = tableRepo.findById(tableId);
        if (optionalTableDetails.isPresent()) {
            TableDetails tableDetails = optionalTableDetails.get();

            // Get the schema name
            Optional<Schema> optionalSchema = schemaRepo.findById(tableDetails.getSchemaId());
            if (!optionalSchema.isPresent()) {
                throw new RuntimeException("Schema not found");
            }

            String schemaName = optionalSchema.get().getSchemaName();
            String tableName = tableDetails.getTableName();
            String columnName = newColumn.get("columnName");
            String dataType = newColumn.get("dataType");

            // Execute native query to add a new column
            String addColumnQuery = "ALTER TABLE " + schemaName + "." + tableName +
                    " ADD COLUMN " + columnName + " " + dataType;
            
            entityManager.createNativeQuery(addColumnQuery).executeUpdate();
            
            Map<String, String> column = tableDetails.getColumns();
            column.put(columnName, dataType);
            tableDetails.setColumns(column);
            tableDetails.setCreatedDatetime(tableDetails.getCreatedDatetime());
            tableDetails.setUpdatedDatetime(new Date());

            // Save the updated table details
            tableRepo.save(tableDetails);
            
        } else {
            throw new RuntimeException("Table not found");
        }
    }
    
    
    @Transactional
    public void deleteColumn(int tableId, String columnName) {
        // Fetch existing table details
        Optional<TableDetails> optionalTableDetails = tableRepo.findById(tableId);
        if (optionalTableDetails.isPresent()) {
            TableDetails tableDetails = optionalTableDetails.get();

            // Get the schema name
            Optional<Schema> optionalSchema = schemaRepo.findById(tableDetails.getSchemaId());
            if (!optionalSchema.isPresent()) {
                throw new RuntimeException("Schema not found");
            }

            String schemaName = optionalSchema.get().getSchemaName();
            String tableName = tableDetails.getTableName();

            // Execute native query to drop the column
            String dropColumnQuery = "ALTER TABLE " + schemaName + "." + tableName +
                    " DROP COLUMN " + columnName;

            entityManager.createNativeQuery(dropColumnQuery).executeUpdate();

            // Remove the column from the map of columns
            Map<String, String> columns = tableDetails.getColumns();
            columns.remove(columnName);

            // Update the columns field in TableDetails
            tableDetails.setColumns(columns);
            tableDetails.setUpdatedDatetime(new Date());

            // Save the updated table details
            tableRepo.save(tableDetails);

        } else {
            throw new RuntimeException("Table not found");
        }
    }

}
