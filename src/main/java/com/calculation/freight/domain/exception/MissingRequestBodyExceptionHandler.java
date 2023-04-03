package com.calculation.freight.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.calculation.freight.domain.model.ErroResponse;

@ControllerAdvice
public class MissingRequestBodyExceptionHandler {

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErroResponse> handleMissingRequestBodyException(HttpMessageNotReadableException ex) {
		String mensagem = "É obrigatório informar um CEP.";
		ErroResponse erroResponse = new ErroResponse(mensagem, HttpStatus.BAD_REQUEST.value());
		return ResponseEntity.badRequest().body(erroResponse);
	}
}
