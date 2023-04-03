package com.calculation.freight.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;

import com.calculation.freight.domain.exception.CepInvalidoException;
import com.calculation.freight.domain.exception.CepNaoEncontradoException;
import com.calculation.freight.domain.model.FreteEndereco;
import com.calculation.freight.domain.service.CalculoFretesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringJUnitConfig
@SpringBootTest
public class CalculoFretesServiceTests {

	@Value("${viacep.api.url}")
	private String viaCepApiUrl;

	@Autowired
	private CalculoFretesService service;

	CalculoFretesService serviceMock = mock(CalculoFretesService.class);

	private RestTemplate restTemplate = mock(RestTemplate.class);

	ObjectMapper objectMapper = new ObjectMapper();

	private final double FRETE_SUDESTE = 7.85;
	private final double FRETE_CENTROESTE = 12.50;
	private final double FRETE_NORDESTE = 15.98;
	private final double FRETE_SUL = 17.30;
	private final double FRETE_NORTE = 20.83;

	@Test
	public void testMapFromJsonNode() throws JsonMappingException, JsonProcessingException {

		String json = "{\"cep\":\"87010-530\",\"logradouro\":\"Praça da Catedral\",\"complemento\":\"B\",\"bairro\":\"Zona 01\",\"localidade\":\"Maringá\",\"uf\":\"PR\",\"ibge\":\"4115200\",\"gia\":\"\",\"ddd\":\"44\",\"siafi\":\"7691\"}";

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);

		FreteEndereco endereco = service.mapFromJsonNode(jsonNode);

		Assertions.assertEquals("87010-530", endereco.getCep());
		Assertions.assertEquals("Praça da Catedral", endereco.getRua());
		Assertions.assertEquals("B", endereco.getComplemento());
		Assertions.assertEquals("Zona 01", endereco.getBairro());
		Assertions.assertEquals("Maringá", endereco.getCidade());
		Assertions.assertEquals("PR", endereco.getEstado());
	}

	@Test
	public void testConsultarCepComSucesso() {
		String cep = "87010-530";
		String json = "{\"cep\":\"87010-530\",\"logradouro\":\"Praça da Catedral\",\"complemento\":\"B\",\"bairro\":\"Zona 01\",\"localidade\":\"Maringá\",\"uf\":\"PR\",\"ibge\":\"4115200\",\"gia\":\"\",\"ddd\":\"44\",\"siafi\":\"7691\"}";

		String url = String.format(viaCepApiUrl, cep);

		when(restTemplate.getForObject(eq(url), eq(String.class))).thenReturn(json);

		JsonNode result = service.consultarCep(cep);

		assertNotNull(result);
		assertEquals("87010-530", result.get("cep").asText());
		assertEquals("Praça da Catedral", result.get("logradouro").asText());
		assertEquals("Zona 01", result.get("bairro").asText());
		assertEquals("Maringá", result.get("localidade").asText());
		assertEquals("PR", result.get("uf").asText());
		assertEquals("4115200", result.get("ibge").asText());
		assertEquals("", result.get("gia").asText());
		assertEquals("44", result.get("ddd").asText());
		assertEquals("7691", result.get("siafi").asText());
	}

	@Test
	public void testConsultarCepInvalido() {
		String cep = null;
		String json = "{\"message\": \"O CEP informado está em formato inválido.\",\"statusCode\": 400\"}";

		String url = String.format(viaCepApiUrl, cep);

		when(restTemplate.getForObject(eq(url), eq(String.class))).thenReturn(json);

		Exception exception = assertThrows(CepInvalidoException.class, () -> service.consultarCep(cep));

		assertNotNull(exception);
		assertEquals("O CEP informado está em formato inválido.", exception.getMessage());
	}

	@Test
	public void testConsultarCepNaoInformado() {
		String cep = "11111111";
		String json = "{\"message\": \"O CEP informado não foi encontrado.\",\"statusCode\": 400\"}";

		String url = String.format(viaCepApiUrl, cep);

		when(restTemplate.getForObject(eq(url), eq(String.class))).thenReturn(json);

		Exception exception = assertThrows(CepNaoEncontradoException.class, () -> service.consultarCep(cep));

		assertNotNull(exception);
		assertEquals("O CEP informado não foi encontrado.", exception.getMessage());
	}

	@Test
	public void testCalcularValorFreteSudeste() throws JsonMappingException, JsonProcessingException {
		String cep = "22241-330";
		String json = "{\"cep\":\"22241-330\",\"logradouro\":\"Rua Cosme Velho\",\"complemento\":\"\",\"bairro\":\"Cosme Velho\",\"localidade\":\"Rio de Janeiro\",\"uf\":\"RJ\",\"ibge\":\"3304557\",\"gia\":\"\",\"ddd\":\"21\",\"siafi\":\"6001\"}";

		JsonNode jsonNode = objectMapper.readTree(json);

		FreteEndereco freteEndereco = FreteEndereco.builder().cep("22241-330").rua("Rua Cosme Velho")
				.bairro("Cosme Velho").cidade("Rio de Janeiro").estado("RJ").build();

		when(serviceMock.consultarCep(cep)).thenReturn(jsonNode);
		when(serviceMock.mapFromJsonNode(jsonNode)).thenReturn(freteEndereco);

		freteEndereco = service.calcularValorFrete(cep);

		assertEquals(FRETE_SUDESTE, freteEndereco.getFrete());
	}

	@Test
	public void testCalcularValorFreteCentroEste() throws JsonMappingException, JsonProcessingException {
		String cep = "78005-600";
		String json = "{\"cep\":\"78005-600\",\"logradouro\":\"Rua General João Severiano da Fonseca\",\"complemento\":\"\",\"bairro\":\"Araés\",\"localidade\":\"Cuiabá\",\"uf\":\"MT\",\"ibge\":\"5103403\",\"gia\":\"\",\"ddd\":\"65\",\"siafi\":\"9065\"}";

		JsonNode jsonNode = objectMapper.readTree(json);

		FreteEndereco freteEndereco = FreteEndereco.builder().cep("78005-600")
				.rua("Rua General João Severiano da Fonseca").bairro("Araés").cidade("Cuiabá").estado("MT").build();

		when(serviceMock.consultarCep(cep)).thenReturn(jsonNode);
		when(serviceMock.mapFromJsonNode(jsonNode)).thenReturn(freteEndereco);

		freteEndereco = service.calcularValorFrete(cep);

		assertEquals(FRETE_CENTROESTE, freteEndereco.getFrete());
	}

	@Test
	public void testCalcularValorFreteNordeste() throws JsonMappingException, JsonProcessingException {
		String cep = "40015-970";
		String json = "{\"cep\":\"40015-970\",\"logradouro\":\"Avenida Estados Unidos\",\"complemento\":\"\",\"bairro\":\"Comércio\",\"localidade\":\"Salvador\",\"uf\":\"BA\",\"ibge\":\"2927408\",\"gia\":\"\",\"ddd\":\"71\",\"siafi\":\"3644\"}";

		JsonNode jsonNode = objectMapper.readTree(json);

		FreteEndereco freteEndereco = FreteEndereco.builder().cep("40015-970").rua("Avenida Estados Unidos")
				.complemento("").bairro("Comércio").cidade("Salvador").estado("BA").build();

		when(serviceMock.consultarCep(cep)).thenReturn(jsonNode);
		when(serviceMock.mapFromJsonNode(jsonNode)).thenReturn(freteEndereco);

		freteEndereco = service.calcularValorFrete(cep);

		assertEquals(FRETE_NORDESTE, freteEndereco.getFrete());
	}

	@Test
	public void testCalcularValorFreteSul() throws JsonMappingException, JsonProcessingException {
		String cep = "87010-530";
		String json = "{\"cep\":\"87010-530\",\"logradouro\":\"Praça da Catedral\",\"complemento\":\"B\",\"bairro\":\"Zona 01\",\"localidade\":\"Maringá\",\"uf\":\"PR\",\"ibge\":\"4115200\",\"gia\":\"\",\"ddd\":\"44\",\"siafi\":\"7691\"}";

		JsonNode jsonNode = objectMapper.readTree(json);

		FreteEndereco freteEndereco = FreteEndereco.builder().cep("87010-530").rua("Praça da Catedral")
				.bairro("Zona 01").cidade("Maringa").estado("PR").build();

		when(serviceMock.consultarCep(cep)).thenReturn(jsonNode);
		when(serviceMock.mapFromJsonNode(jsonNode)).thenReturn(freteEndereco);

		freteEndereco = service.calcularValorFrete(cep);

		assertEquals(FRETE_SUL, freteEndereco.getFrete());
	}

	@Test
	public void testCalcularValorFreteNorte() throws JsonMappingException, JsonProcessingException {
		String cep = "69004-400";
		String json = "{\"cep\":\"69004-400\",\"logradouro\":\"Rua Costa Azevedo\",\"complemento\":\"\",\"bairro\":\"Centro\",\"localidade\":\"Manaus\",\"uf\":\"AM\",\"ibge\":\"1302603\",\"gia\":\"\",\"ddd\":\"92\",\"siafi\":\"1306\"}";

		JsonNode jsonNode = objectMapper.readTree(json);

		FreteEndereco freteEndereco = FreteEndereco.builder().cep("69004-400").rua("Rua Costa Azevedo").bairro("Centro")
				.cidade("Manaus").estado("AM").build();

		when(serviceMock.consultarCep(cep)).thenReturn(jsonNode);
		when(serviceMock.mapFromJsonNode(jsonNode)).thenReturn(freteEndereco);

		freteEndereco = service.calcularValorFrete(cep);

		assertEquals(FRETE_NORTE, freteEndereco.getFrete());
	}

}
