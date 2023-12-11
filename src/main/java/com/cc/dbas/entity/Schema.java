package com.cc.dbas.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

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
@Table(name = "schema_details")
public class Schema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schema_id")
    private int schema_id;

    @Column(name = "user_id")
    private int user_id;

    @Column(name = "schema_name")
    private String schemaName;

    @Column(name = "tables")
    private String tables;

    @Column(name = "created_datetime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created_datetime;

    @Column(name = "updated_datetime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedDatetime;
}
