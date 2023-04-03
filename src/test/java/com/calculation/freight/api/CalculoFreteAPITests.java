package com.calculation.freight.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import com.calculation.freight.domain.model.EnderecoRequest;

import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CalculoFreteAPITests {

	@LocalServerPort
	private Integer port;
	
	private final String mensagemErroQuandoCepNaoInformado = "É obrigatório informar um CEP.";
	private final String mensagemErroQuandoCepInformadoForInvalido = "O CEP informado não foi encontrado.";
	
	@Test
	public void deveRetornarSucessoQuandoBuscarPorFreteComCepValidoFormatado() {
		EnderecoRequest enderecoRequest = new EnderecoRequest();
		
		enderecoRequest.setCep("87010-530");
		
		given()
			.basePath("v1/consulta-endereco")
			.port(port)
			.body(enderecoRequest)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("cep", is("87010-530"))
		    .body("rua", is("Praça da Catedral"))
		    .body("complemento", is(""))
		    .body("bairro", is("Zona 01"))
		    .body("cidade", is("Maringá"))
		    .body("estado", is("PR"))
		    .body("frete", is(17.3f));
	}
	
	@Test
	public void deveRetornarSucessoQuandoBuscarPorFreteComCepValidoSemFormatar() {
		EnderecoRequest enderecoRequest = new EnderecoRequest();
		
		enderecoRequest.setCep("87010530");
		
		given()
			.basePath("v1/consulta-endereco")
			.port(port)
			.body(enderecoRequest)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.OK.value())
			.body("cep", is("87010-530"))
		    .body("rua", is("Praça da Catedral"))
		    .body("complemento", is(""))
		    .body("bairro", is("Zona 01"))
		    .body("cidade", is("Maringá"))
		    .body("estado", is("PR"))
		    .body("frete", is(17.3f));
	}

	@Test
	public void deveRetornarErroQuandoBuscarPorFreteSemInformarCep() {
		given()
			.basePath("v1/consulta-endereco")
			.port(port)
			.contentType(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("message", is(mensagemErroQuandoCepNaoInformado));
	}
	
	@Test
	public void deveRetornarErroQuandoBuscarPorFreteComCepInvalido() {
		EnderecoRequest enderecoRequest = new EnderecoRequest();
		
		enderecoRequest.setCep("11111-111");
		
		given()
			.basePath("v1/consulta-endereco")
			.port(port)
			.body(enderecoRequest)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post()
		.then()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("message", is(mensagemErroQuandoCepInformadoForInvalido));
	}
}
