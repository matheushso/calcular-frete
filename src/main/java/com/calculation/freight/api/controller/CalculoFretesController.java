package com.calculation.freight.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.calculation.freight.domain.exception.CepInvalidoException;
import com.calculation.freight.domain.exception.CepNaoEncontradoException;
import com.calculation.freight.domain.model.EnderecoRequest;
import com.calculation.freight.domain.model.ErroResponse;
import com.calculation.freight.domain.model.FreteEndereco;
import com.calculation.freight.domain.service.CalculoFretesService;

@RestController
@RequestMapping("v1/consulta-endereco")
public class CalculoFretesController {

	@Autowired
	private CalculoFretesService service;

	@PostMapping
	private ResponseEntity<?> freightCalculation(@RequestBody EnderecoRequest cep) {
		try {
			FreteEndereco endereco = service.calcularValorFrete(cep.getCep());
			return ResponseEntity.ok(endereco);

		} catch (CepInvalidoException | CepNaoEncontradoException e) {
			ErroResponse errorResponse = new ErroResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}
	}
}
