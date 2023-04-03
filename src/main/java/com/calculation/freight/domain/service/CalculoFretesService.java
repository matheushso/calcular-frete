package com.calculation.freight.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.calculation.freight.domain.exception.CepInvalidoException;
import com.calculation.freight.domain.exception.CepNaoEncontradoException;
import com.calculation.freight.domain.model.FreteEndereco;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CalculoFretesService {

	@Value("${viacep.api.url}")
	private String viaCepApiUrl;

	private RestTemplate restTemplate = new RestTemplate();
	private ObjectMapper objectMapper = new ObjectMapper();

	public FreteEndereco calcularValorFrete(String cep) {

		JsonNode json = consultarCep(cep);

		FreteEndereco endereco = mapFromJsonNode(json);

		double valorFrete = 0.0;

		switch (json.get("uf").asText()) {
		case "SP":
		case "RJ":
		case "MG":
		case "ES":
			valorFrete = 7.85;
			break;
		case "MT":
		case "MS":
		case "GO":
		case "DF":
			valorFrete = 12.50;
			break;
		case "BA":
		case "AL":
		case "CE":
		case "MA":
		case "PB":
		case "PE":
		case "PI":
		case "RN":
		case "SE":
			valorFrete = 15.98;
			break;
		case "PR":
		case "SC":
		case "RS":
			valorFrete = 17.30;
			break;
		case "AM":
		case "AC":
		case "RO":
		case "RR":
		case "PA":
		case "AP":
		case "TO":
			valorFrete = 20.83;
			break;
		default:
			break;
		}

		endereco.setFrete(valorFrete);

		return endereco;
	}

	public JsonNode consultarCep(String cep) {
		String url = String.format(viaCepApiUrl, cep);

		JsonNode json = null;

		try {
			json = objectMapper.readTree(restTemplate.getForObject(url, String.class));
		} catch (JsonProcessingException | RestClientException e) {
			throw new CepInvalidoException("O CEP informado está em formato inválido.");
		}

		if (json.has("erro")) {
			throw new CepNaoEncontradoException("O CEP informado não foi encontrado.");
		}

		return json;
	}

	public FreteEndereco mapFromJsonNode(JsonNode jsonNode) {
		FreteEndereco freteEndereco = new FreteEndereco();
		freteEndereco.setCep(jsonNode.get("cep").asText());
		freteEndereco.setRua(jsonNode.get("logradouro").asText());
		freteEndereco.setComplemento(jsonNode.get("complemento").asText());
		freteEndereco.setBairro(jsonNode.get("bairro").asText());
		freteEndereco.setCidade(jsonNode.get("localidade").asText());
		freteEndereco.setEstado(jsonNode.get("uf").asText());

		return freteEndereco;
	}
}
