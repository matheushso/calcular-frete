Feature: Cálculo de frete

  Background:
    Given que o endpoint de consulta de endereço é "/v1/consulta-endereco"

  Scenario: Buscar frete com CEP válido formatado
    Given um CEP "87010-530"
    When enviar uma requisição POST para o endpoint
    Then a resposta deve ter o status code 200
    And o endereço retornado deve ser
      | cep        | rua                 | complemento | bairro  | cidade   | estado | frete |
      | 87010-530  | Praça da Catedral   |             | Zona 01 | Maringá  | PR     | 17.3  |

  Scenario: Buscar frete com CEP válido sem formatação
    Given um CEP válido sem formatação "87010530"
    When enviar uma requisição POST para o endpoint
    Then a resposta deve ter o status code 200
    And o endereço retornado deve ser
      | cep        | rua                 | complemento | bairro  | cidade   | estado | frete |
      | 87010-530  | Praça da Catedral   |             | Zona 01 | Maringá  | PR     | 17.3  |

  Scenario: Buscar frete sem informar o CEP
    Given que o CEP não foi informado
    When enviar uma requisição POST para o endpoint
    Then a resposta deve ter o status code 400
    And a mensagem de erro deve ser "É obrigatório informar um CEP."

  Scenario: Buscar frete com CEP inválido
    Given um CEP inválido "11111-111"
    When enviar uma requisição POST para o endpoint
    Then a resposta deve ter o status code 400
    And a mensagem de erro deve ser "O CEP informado não foi encontrado."
