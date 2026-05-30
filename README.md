# 📬 RabbitMail

Sistema de envio de e-mails em lote utilizando mensageria assíncrona com **RabbitMQ** e **Spring Boot**.

> Atividade prática — Disciplina de Mensageria 

---

## 👥 Integrantes

| Nome Completo |
Huan Cláudio Souza Viana
Gabriel de Oliveira Nascimento

---

## 📌 Descrição

O **RabbitMail** é uma aplicação que permite cadastrar destinatários de e-mail em banco de dados e disparar mensagens em lote de forma **assíncrona**, utilizando uma fila no RabbitMQ como intermediário entre a requisição do usuário e o processamento do envio.

O fluxo principal é:

```
[Frontend]
    │
    ▼
[Controller REST]  ──publica──▶  [Exchange / Fila RabbitMQ]
                                         │
                                         ▼
                                   [Consumer / Listener]
                                         │
                                         ▼
                               [Serviço de E-mail (SMTP)]
```

A separação entre **publicar** e **processar** garante que a requisição HTTP retorne imediatamente, enquanto o envio ocorre de forma desacoplada no background.

---

## 🏗️ Arquitetura

### Stack

| Camada | Tecnologia |
|---|---|
| Backend | Java 21 + Spring Boot 4 |
| Mensageria | RabbitMQ (via Docker Compose) |
| Banco de Dados | MySQL (via Docker Compose) |
| ORM | Spring Data JPA + Hibernate |
| E-mail (teste) | Mailtrap / Ethereal Email |
| Frontend | HTML + JavaScript (fetch API) |
| Utilitários | Lombok |

### Componentes RabbitMQ

| Componente | Nome | Descrição |
|---|---|---|
| Exchange | `mail.exchange` | Direct exchange responsável por rotear as mensagens |
| Fila | `mail.queue` | Fila principal que armazena as solicitações de envio |
| Routing Key | `mail.routing.key` | Chave de roteamento que liga a exchange à fila |
| Producer | `EmailProducer` | Publica a mensagem na exchange via `RabbitTemplate` |
| Consumer | `EmailConsumer` | Escuta a fila com `@RabbitListener` e processa o envio |

---

## 📁 Estrutura do Projeto

```
src/main/java/com/mensageria/rabbitMail/
│
├── config/
│   └── RabbitMQConfig.java          # Declara Exchange, Queue, Binding e RabbitTemplate
│
├── controller/
│   └── EmailController.java         # Endpoints REST (cadastro, listagem, disparo)
│
├── model/
│   └── EmailDestinatario.java       # Entidade JPA — e-mails cadastrados no banco
│
├── repository/
│   └── EmailDestinatarioRepository  # Interface Spring Data JPA
│
├── dto/
│   └── MensagemDTO.java             # DTO com assunto e corpo da mensagem
│
├── producer/
│   └── EmailProducer.java           # Publica mensagem na fila via RabbitTemplate
│
├── consumer/
│   └── EmailConsumer.java           # @RabbitListener — consome e envia os e-mails
│
├── service/
│   └── EmailService.java            # Regras de negócio: buscar destinatários, enviar
│
└── RabbitMailApplication.java       # Classe principal
```

---

## ⚙️ Configuração

### Pré-requisitos

- Java 21+
- Maven 3.9+
- Docker e Docker Compose

### Subindo os serviços (MySQL + RabbitMQ)

```bash
docker compose up -d
```

O `compose.yaml` já sobe automaticamente:
- **MySQL** na porta `3306` (banco: `mydatabase`, usuário: `myuser`, senha: `secret`)
- **RabbitMQ** na porta `5672` (usuário: `myuser`, senha: `secret`)
  - Management UI disponível em `http://localhost:15672` (adicionar porta no compose)

### application.properties

```properties
spring.application.name=rabbitMail

# Datasource
spring.datasource.url=jdbc:mysql://localhost:3306/mydatabase
spring.datasource.username=myuser
spring.datasource.password=secret
spring.jpa.hibernate.ddl-auto=update

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=myuser
spring.rabbitmq.password=secret

# Mail (exemplo com Mailtrap)
spring.mail.host=smtp.mailtrap.io
spring.mail.port=587
spring.mail.username=SEU_USUARIO_MAILTRAP
spring.mail.password=SUA_SENHA_MAILTRAP
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# RabbitMQ — nomes da fila
rabbitmq.exchange=mail.exchange
rabbitmq.queue=mail.queue
rabbitmq.routingkey=mail.routing.key
```

> **Atenção:** não commite credenciais reais. Use variáveis de ambiente ou um arquivo `.env` ignorado pelo `.gitignore`.

---

## 🚀 Como Executar

```bash
# 1. Suba os containers
docker compose up -d

# 2. Execute a aplicação
./mvnw spring-boot:run
```

A API ficará disponível em `http://localhost:8080`.

---

## 🔌 Endpoints da API

### Destinatários

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/destinatarios` | Cadastra um novo e-mail |
| `GET` | `/api/destinatarios` | Lista todos os e-mails cadastrados |
| `DELETE` | `/api/destinatarios/{id}` | Remove um destinatário |

**Corpo do POST `/api/destinatarios`:**
```json
{
  "email": "exemplo@email.com",
  "nome": "Nome do Destinatário"
}
```

### Envio de Mensagem

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/mensagens/enviar` | Publica mensagem na fila para envio em lote |

**Corpo do POST `/api/mensagens/enviar`:**
```json
{
  "assunto": "Assunto do e-mail",
  "corpo": "Conteúdo HTML ou texto do e-mail"
}
```

> Ao chamar este endpoint, a aplicação **publica uma mensagem na fila** do RabbitMQ e retorna imediatamente. O consumer processa o envio de forma assíncrona.

---

## 🔑 Classes Principais

### `RabbitMQConfig.java`
Declara os beans do RabbitMQ: `Queue`, `DirectExchange`, `Binding` e configura o `RabbitTemplate` com `MessageConverter` para serialização em JSON.

### `EmailProducer.java`
Injeta o `RabbitTemplate` e publica um objeto `MensagemDTO` na exchange com a routing key configurada:
```java
rabbitTemplate.convertAndSend(exchange, routingKey, mensagemDTO);
```

### `EmailConsumer.java`
Anota o método com `@RabbitListener(queues = "${rabbitmq.queue}")`, recebe o `MensagemDTO` e delega ao `EmailService` o envio em lote para todos os destinatários do banco.

### `EmailService.java`
Busca todos os destinatários via `EmailDestinatarioRepository`, itera a lista e usa o `JavaMailSender` para enviar `SimpleMailMessage` (ou `MimeMessage` para HTML) para cada um.

### `EmailController.java`
Expõe os endpoints REST. O endpoint de envio chama apenas o `EmailProducer` — nunca envia e-mail diretamente, garantindo o desacoplamento assíncrono.

---

## 📊 Evidências do Sistema

_Adicionar prints após a implementação:_

- [ ] Painel do RabbitMQ/CloudAMQP com a fila e mensagens processadas
- [ ] Banco de dados com a tabela de destinatários populada
- [ ] Frontend com as funcionalidades demonstradas
- [ ] Console/logs mostrando o Consumer processando as mensagens
- [ ] Caixa de entrada do Mailtrap com os e-mails recebidos

---

## 🧪 Serviço de E-mail para Testes

Recomendamos o **[Mailtrap](https://mailtrap.io)** ou **[Ethereal Email](https://ethereal.email)** para simular o envio sem precisar de um servidor SMTP real. Ambos capturam os e-mails enviados e permitem visualizá-los em uma caixa de entrada virtual.

---

## 📝 Observações

- O envio de e-mails **nunca é feito diretamente** pelo controller — sempre passa pela fila.
- O `compose.yaml` gerencia MySQL e RabbitMQ localmente; em produção pode-se usar CloudAMQP.
- Logs do consumer são exibidos no console a cada mensagem processada, servindo como evidência do processamento assíncrono.
