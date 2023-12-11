package com.cc.dbas.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ApiResponse {
	
	public static final ApiResponse INTERNAL_SERVER_ERROR= new ApiResponse(500, "Internal Server Error", null);

	private int status;
	private String msg;
	private Object data;

}