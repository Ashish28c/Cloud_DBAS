package com.cc.dbas.Services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(TableService.class);

    @Autowired
    private TableRepo tableRepo;

    @Autowired
    private SchemaRepo schemaRepo;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public TableDetails createTable(int schemaId, String tableName, ColumnDefinition columnDefinition) {
        try {
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

            logger.info("Table created successfully. Schema ID: {}, Table Name: {}", schemaId, tableName);

            return tableDetails;
        } catch (Exception e) {
            logger.error("Error creating table. Schema ID: {}, Table Name: {}", schemaId, tableName, e);
            throw e;
        }
    }

    @Transactional
    public void deleteTableDetailsBySchemaId(int schemaId) {
        try {
            // Execute native query to delete rows from TableDetails where schemaId is specific
            String deleteTablesQuery = "DELETE FROM tables_details WHERE schema_id = :schemaId";
            Query query = entityManager.createNativeQuery(deleteTablesQuery);
            query.setParameter("schemaId", schemaId);
            query.executeUpdate();
            logger.info("Deleted table details by schema ID: {}", schemaId);
        } catch (Exception e) {
            logger.error("Error deleting table details by schema ID: {}", schemaId, e);
            throw e;
        }
    }

    @Transactional
    public String deletTableId(int table_id) {
        try {
            Optional<TableDetails> optionalTableDetails = tableRepo.findById(table_id);
            Integer SchemaId = optionalTableDetails.get().getSchemaId();

            if (!optionalTableDetails.isPresent()) {
                logger.error("Table not found with ID: {}", table_id);
                return "Table not found";
            } else {

                Optional<Schema> schema = schemaRepo.findById(SchemaId);
                String deleteTableQuery = "DROP TABLE " + schema.get().getSchemaName() + "." + optionalTableDetails.get().getTableName();
                entityManager.createNativeQuery(deleteTableQuery).executeUpdate();
                tableRepo.deleteById(table_id);

                logger.info("Table deleted successfully. Table ID: {}", table_id);

                return "Table Deleted Successfully";
            }
        } catch (Exception e) {
            logger.error("Error deleting table. Table ID: {}", table_id, e);
            throw e;
        }
    }

    public boolean TableExistByID(int tableId) {
        try {
            Optional<Schema> optionalSchema = schemaRepo.findById(tableId);
            return optionalSchema.isPresent();
        } catch (Exception e) {
            logger.error("Error checking if table exists by ID: {}", tableId, e);
            throw e;
        }
    }

    @Transactional
    public List<TableDetails> getTablesBySchemaId(int schemaId) {
        try {
            String nativeQuery = "SELECT * FROM tables_details WHERE schema_id = ?";
            List<TableDetails> TableDetailList = entityManager.createNativeQuery(nativeQuery, TableDetails.class)
                    .setParameter(1, schemaId)
                    .getResultList();
            logger.info("Retrieved tables by schema ID: {}", schemaId);
            return TableDetailList;
        } catch (Exception e) {
            logger.error("Error getting tables by schema ID: {}", schemaId, e);
            throw e;
        }
    }

    @Transactional
    public void addColumn(int tableId, Map<String, String> newColumn) {
        try {
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

                logger.info("Column added successfully. Table ID: {}, Column Name: {}", tableId, columnName);
            } else {
                throw new RuntimeException("Table not found");
            }
        } catch (Exception e) {
            logger.error("Error adding column. Table ID: {}", tableId, e);
            throw e;
        }
    }

    @Transactional
    public void deleteColumn(int tableId, String columnName) {
        try {
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

                logger.info("Column deleted successfully. Table ID: {}, Column Name: {}", tableId, columnName);
            } else {
                throw new RuntimeException("Table not found");
            }
        } catch (Exception e) {
            logger.error("Error deleting column. Table ID: {}, Column Name: {}", tableId, columnName, e);
            throw e;
        }
    }

    @Transactional
    public void changeColumn(int tableId, String oldColumnName, String newColumnName, String newDataType) {
        try {
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

                // Execute native query to change the column name and datatype
                String changeColumnQuery = "ALTER TABLE " + schemaName + "." + tableName +
                        " CHANGE COLUMN " + oldColumnName + " " + newColumnName + " " + newDataType;

                entityManager.createNativeQuery(changeColumnQuery).executeUpdate();

                Map<String, String> columns = tableDetails.getColumns();
                columns.remove(oldColumnName);
                columns.put(newColumnName, newDataType);
                tableDetails.setColumns(columns);
                tableDetails.setUpdatedDatetime(new Date());

                // Save the updated table details
                tableRepo.save(tableDetails);

                logger.info("Column changed successfully. Table ID: {}, Old Column Name: {}, New Column Name: {}, New Data Type: {}", tableId, oldColumnName, newColumnName, newDataType);
            } else {
                throw new RuntimeException("Table not found");
            }
        } catch (Exception e) {
            logger.error("Error changing column. Table ID: {}, Old Column Name: {}, New Column Name: {}, New Data Type: {}", tableId, oldColumnName, newColumnName, newDataType, e);
            throw e;
        }
    }

    public List<Map<String, String>> getAllColumnsByTableId(int tableId) {
        try {
            // Fetch existing table details
            Optional<TableDetails> optionalTableDetails = tableRepo.findById(tableId);
            if (optionalTableDetails.isPresent()) {
                TableDetails tableDetails = optionalTableDetails.get();

                // Deserialize the columns string into a Map
                Map<String, String> columns = tableDetails.getColumns();

                // Convert Map entries into a List of Maps with column names and data types
                List<Map<String, String>> columnList = columns.entrySet().stream()
                        .map(entry -> {
                            Map<String, String> columnMap = new HashMap<>();
                            columnMap.put("columnName", entry.getKey());
                            columnMap.put("dataType", entry.getValue());
                            return columnMap;
                        })
                        .collect(Collectors.toList());

                logger.info("Retrieved columns by table ID: {}", tableId);

                return columnList;
            } else {
                throw new RuntimeException("Table not found");
            }
        } catch (Exception e) {
            logger.error("Error getting columns by table ID: {}", tableId, e);
            throw e;
        }
    }

    @Transactional
    public TableDetails copyTable(int tableId, String newTableName) {
        try {
            Optional<TableDetails> optionalTableDetails = tableRepo.findById(tableId);
            if (optionalTableDetails.isPresent()) {
                TableDetails sourceTable = optionalTableDetails.get();

                // Create a copy of the table
                TableDetails newTable = new TableDetails();
                newTable.setSchemaId(sourceTable.getSchemaId());
                newTable.setTableName(newTableName);
                newTable.setColumns(sourceTable.getColumns());
                newTable.setCreatedDatetime(new Date());
                newTable.setUpdatedDatetime(new Date());

                // Save the new table details
                newTable = tableRepo.save(newTable);

                Optional<Schema> optionalSchema = schemaRepo.findById(sourceTable.getSchemaId());
                String schemaName = optionalSchema.get().getSchemaName();

                // Execute native query to create the table in the database
                String createTableQuery = "CREATE TABLE " + schemaName + "." + newTableName +
                        " AS SELECT * FROM " + schemaName + "." + sourceTable.getTableName();
                entityManager.createNativeQuery(createTableQuery).executeUpdate();

                logger.info("Table copied successfully. Source Table ID: {}, New Table Name: {}", tableId, newTableName);

                return newTable;
            } else {
                throw new RuntimeException("Table not found");
            }
        } catch (Exception e) {
            logger.error("Error copying table. Source Table ID: {}, New Table Name: {}", tableId, newTableName, e);
            throw e;
        }
    }
}
