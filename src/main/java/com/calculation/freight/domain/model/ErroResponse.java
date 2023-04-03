package com.calculation.freight.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErroResponse {

	private String message;
	private int statusCode;
}
