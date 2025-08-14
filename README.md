# Desafio BTG Pactual

## Sobre o Projeto

Projeto desenvolvido apenas para fins didáticos.

Trata-se da resolução de um desafio técnico, onde foi criado um microserviço, desenvolvido com Java e **Spring Boot** 
 e que se integra a um sistema de mensageria (**RabbitMQ**) e a um banco de dados NoSQL (**MongoDB**).
Ele realiza as seguintes operações principais:

1. Consumo de mensagens de pedidos de uma fila do RabbitMQ.
2. Persistência dos pedidos no MongoDB.
3. Disponibilização de informações via API Rest para:
    - Listagem de pedidos por cliente.
    - Cálculo do valor total de um pedido.
    - Quantidade de pedidos realizados por cliente.

O projeto também utiliza contêineres Docker para facilitar o desenvolvimento e integração de suas dependências.

---

## Tecnologias Utilizadas

- **Java 17** e **Spring Boot**:
    - Módulos utilizados: Spring Web, Spring Data MongoDB, Spring AMQP.
- Banco de Dados: **MongoDB** (via Docker).
- Mensageria: **RabbitMQ** (via Docker).
- Logs: **SLF4J** para geração e gerenciamento de logs.
- Gerenciamento de contêineres: **Docker Compose**.

---

## Estrutura do Projeto

O projeto está dividido nas seguintes etapas principais:

### 1. Configuração do Ambiente

O ambiente do projeto foi configurado para integrar o Spring Boot com os contêineres do RabbitMQ e MongoDB, executados via **Docker Compose**.

### 2. Funcionalidades

- **Consumo de Pedidos**:
    - A fila RabbitMQ é utilizada para consumir mensagens referentes a pedidos.
    - Os pedidos (contendo informações como cliente e itens do pedido) serão persistidos no MongoDB.

- **Interfaces RESTful**:
  Através da API, é possível consultar:
    - Lista de pedidos realizados por cliente.
    - Valor total de pedidos.
    - Quantidade de pedidos por cliente.

---

## Como Executar o Projeto

### Pré-Requisitos

Certifique-se de ter instalados em sua máquina:
- **Docker** instalado e ativo.

---

## Passos para Executar

### 1. Executar os Contêineres com Docker Compose

O arquivo `docker-compose.yaml` define a configuração dos contêineres necessários, incluindo:
- Banco de Dados **MongoDB** (porta `27017`).
- Servidor de Mensageria **RabbitMQ** (porta `5672` e painel de controle via `15672`)
  - Login e senha de acesso a interface web: 'secret'.

Para iniciar os serviços via docker-compose, entre no diretório raiz do projeto e execute o comando através do terminal:
```bash
docker-compose up -d --build
```

Aguarde até que os serviços estejam completamente iniciados. Verifique se o painel do RabbitMQ está acessível em: [http://localhost:15672](http://localhost:15672).

A aplicação estará disponível no endereço: [http://localhost:8080](http://localhost:8080).



---

### Testando as Funcionalidades

#### 1. Publicar Mensagens no RabbitMQ

Antes de acessar os endpoints da API, é necessário que o RabbitMQ receba as mensagens que serão processadas pelo microserviço. Você pode publicar novas mensagens diretamente pela interface gráfica do RabbitMQ.

**Passos para publicar uma mensagem no RabbitMQ:**

1. Acesse o painel do RabbitMQ em: [http://localhost:15672](http://localhost:15672).
2. Faça login no painel. Utilize as credenciais padrão se não tiver alterado no `docker-compose.yaml`:
    - **Usuário:** `secret`
    - **Senha:** `secret`
3. Vá até a aba `Queues` no painel e selecione a fila que está sendo utilizada, nesse caso: `create_order_queue`.
4. Clique no botão **Publish message**.
   5. No campo `Payload`, insira os dados da mensagem no formato JSON, como no exemplo abaixo:
      ```json
      {
           "codigoPedido": 1001,
           "codigoCliente":1,
           "itens": [
               {
                   "produto": "Mesa",
                   "quantidade": 1,
                   "preco": 400
               },
               {
                   "produto": "Monitor",
                   "quantidade": 1,
                   "preco": 1300.20
               }
           ]
       }
      ```
6. Clique no botão **Publish** para enviar a mensagem para a fila.

Agora, o microserviço processará a mensagem e armazenará os dados no MongoDB.

---

#### 2. Endpoints Disponíveis

- **Listar pedidos por cliente**:
  ```http
  GET /customers/{customerId}/orders
  ```

- **Calcular o valor total dos pedidos de um cliente**:
  ```http
  GET /customers/{customerId}/orders/total
  ```

- **Calcular a quantidade de pedidos por cliente**:
  ```http
  GET /customers/{customerId}/orders/count
  ```

Altere `{customerId}` para o ID do cliente que você deseja consultar.

---

### Exemplo de Requisição via Postman

Para listar os pedidos realizados por um cliente específico, você pode utilizar o Postman e executar uma requisição **GET** no seguinte endpoint:

**URL**: [http://localhost:8080/customers/1/orders](http://localhost:8080/customers/1/orders)


**Método**: GET

**Exemplo de resposta** *(200 OK)*:
```json
{
  "sumary": {
    "totalOnOrders": 1700.20
  },
  "data": [
    {
      "orderId": 1001,
      "customerId": 1,
      "total": 1700.20
    }
  ],
  "paginationResponse": {
    "page": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```
