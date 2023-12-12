package com.cc.dbas.entity;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tables_details")
public class TableDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    private int tableId;

    @Column(name = "schema_id")
    private Integer schemaId;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "columns")
    private String columns;

    @Column(name = "created_datetime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDatetime;

    @Column(name = "updated_datetime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedDatetime;

    public void setColumns(Map<String, String> columns) {
        try {
            this.columns = new ObjectMapper().writeValueAsString(columns);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getColumns() {
        try {
            return new ObjectMapper().readValue(this.columns, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
