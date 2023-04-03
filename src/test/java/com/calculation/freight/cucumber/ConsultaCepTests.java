package com.calculation.freight.cucumber;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features/ConsultaCep.feature")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConsultaCepTests {

	@LocalServerPort
	private Integer port;
	
	private String endpoint;
	private String cep;
	private Response response;

	@Dado("que o endpoint de consulta de endereço é {string}")
    public void que_o_endpoint_de_consulta_de_endereco_e(String endpoint) {
        this.endpoint = endpoint;
    }

    @Dado("um CEP {string}")
    public void um_CEP_válido_formatado(String cep) {
        this.cep = cep;
    }

    @Quando("enviar uma requisição POST para o endpoint")
    public void enviar_uma_requisição_POST_para_o_endpoint() {
    	response = given()
			.basePath(endpoint)
			.port(port)
			.body(cep)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post();
    }

    @Então("a resposta deve ter o status code {int}")
    public void a_resposta_deve_ter_o_status_code(int statusCode) {
        assertEquals(statusCode, this.response.getStatusCode());
    }

    @E("o endereço retornado deve ser")
    public void o_endereço_retornado_deve_ser(io.cucumber.datatable.DataTable dataTable) {
        HashMap<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("cep", dataTable.cell(1,0));
        expectedResponse.put("rua", dataTable.cell(1,1));
        expectedResponse.put("complemento", dataTable.cell(1,2));
        expectedResponse.put("bairro", dataTable.cell(1,3));
        expectedResponse.put("cidade", dataTable.cell(1,4));
        expectedResponse.put("estado", dataTable.cell(1,5));
        expectedResponse.put("frete", Float.parseFloat(dataTable.cell(1,6)));

        HashMap<String, Object> actualResponse = this.response.jsonPath().get();

        assertEquals(expectedResponse, actualResponse);
    }
}
