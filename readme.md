# 🏦 Banco Digital API

API REST completa para gerenciamento de banco digital, desenvolvida com **Spring Boot 3**, **PostgreSQL**, **JWT** e **Swagger/OpenAPI**.

![Java](https://img.shields.io/badge/Java-17+-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 🚀 Tecnologias Utilizadas

- **Java 17+**
- **Spring Boot 3.5.6**
- **Spring Security + JWT**
- **Spring Data JPA**
- **PostgreSQL**
- **Swagger/OpenAPI 3**
- **Lombok**
- **JUnit 5 + Mockito**
- **Maven**

## 📋 Funcionalidades

### Autenticação
- ✅ Registro de usuários
- ✅ Login com JWT
- ✅ Autenticação stateless (sem sessão)
- ✅ Token válido por 24 horas

### Usuários
- ✅ Criar usuário
- ✅ Buscar por ID
- ✅ Listar todos (com e sem paginação)
- ✅ Atualizar dados
- ✅ Deletar usuário
- ✅ Validação de CPF e email únicos

### Contas Bancárias
- ✅ Criar conta vinculada a usuário
- ✅ Buscar por ID
- ✅ Listar todas (com e sem paginação)
- ✅ Listar por usuário
- ✅ Atualizar conta
- ✅ Deletar conta (apenas com saldo zero)
- ✅ Saldo automático iniciado em R$ 0,00

### Transações Financeiras
- ✅ Depósito
- ✅ Saque (com validação de saldo)
- ✅ Transferência entre contas
- ✅ Consultar extrato completo
- ✅ Histórico ordenado por data

## 🏗️ Arquitetura

com.bancodigital
├── config # Configurações (Security, JWT, CORS, Swagger)
├── controller # Endpoints REST
├── dto
│ ├── request # DTOs de entrada
│ └── response # DTOs de saída
├── exception # Tratamento de exceções
├── model # Entidades JPA
│ └── enums # Enumerações
├── repository # Acesso a dados
└── service # Lógica de negócio
└── impl # Implementações

text

## ⚙️ Pré-requisitos

- **JDK 17** ou superior
- **PostgreSQL 12+** instalado e rodando
- **Maven 3.8+**
- **IntelliJ IDEA** (Community ou Ultimate)

## 🔧 Configuração e Instalação

### 1. Clone o repositório
git clone https://github.com/seu-usuario/banco-digital-api.git
cd banco-digital-api

text

### 2. Configure o banco de dados
Crie o banco no PostgreSQL:
CREATE DATABASE banco_digital;

text

### 3. Configure as credenciais
Edite o arquivo `src/main/resources/application-dev.properties`:
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA_AQUI

text

### 4. Execute a aplicação

**Pelo Maven:**
mvn spring-boot:run

text

**Pelo IntelliJ:**
- Clique com botão direito em `BancoDigitalApiApplication.java`
- Selecione **Run 'BancoDigitalApiApplication'**

A aplicação estará disponível em: [**http://localhost:8080**](http://localhost:8080)

## 📚 Documentação da API (Swagger)

Após executar a aplicação, acesse a documentação interativa:

http://localhost:8080/swagger-ui.html

text

A documentação Swagger permite:
- Visualizar todos os endpoints
- Testar as APIs diretamente pelo navegador
- Ver modelos de request/response
- Autenticar com JWT

## 🧪 Executar Testes

**Todos os testes:**
mvn test

text

**Com relatório de cobertura:**
mvn clean test

text

## 🔐 Como Usar (Passo a Passo)

### 1. Criar um novo usuário
POST /api/users
Content-Type: application/json

{
"nome": "João Silva",
"cpf": "12345678901",
"email": "joao@email.com",
"senha": "senha123",
"telefone": "11987654321"
}

text

**Resposta (201 Created):**
{
"id": 1,
"nome": "João Silva",
"cpf": "12345678901",
"email": "joao@email.com",
"telefone": "11987654321",
"createdAt": "2025-10-06T19:30:00"
}

text

### 2. Fazer login
POST /api/auth/login
Content-Type: application/json

{
"email": "joao@email.com",
"senha": "senha123"
}

text

**Resposta (200 OK):**
{
"token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2FvQGVtYWlsLmNvbSIsImlhdCI6MTY5NjYxMjgwMCwiZXhwIjoxNjk2Njk5MjAwfQ.abc123...",
"tipo": "Bearer",
"userId": 1,
"nome": "João Silva",
"email": "joao@email.com",
"role": "USER"
}

text

### 3. Criar uma conta bancária
**⚠️ Adicione o token JWT no header:**
POST /api/accounts
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
"numeroConta": "123456",
"agencia": "0001",
"userId": 1
}

text

**Resposta (201 Created):**
{
"id": 1,
"numeroConta": "123456",
"agencia": "0001",
"saldo": 0.00,
"userId": 1,
"nomeUsuario": "João Silva",
"createdAt": "2025-10-06T19:35:00"
}

text

### 4. Realizar um depósito
POST /api/transactions
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
"tipo": "DEPOSITO",
"valor": 1000.00,
"descricao": "Depósito inicial",
"accountId": 1
}

text

### 5. Realizar um saque
POST /api/transactions
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
"tipo": "SAQUE",
"valor": 200.00,
"descricao": "Saque no caixa eletrônico",
"accountId": 1
}

text

### 6. Fazer uma transferência
POST /api/transactions
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
"tipo": "TRANSFERENCIA",
"valor": 500.00,
"descricao": "Transferência para amigo",
"accountId": 1,
"accountDestinoId": 2
}

text

### 7. Consultar extrato
GET /api/transactions/account/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

text

## 🎯 Princípios e Boas Práticas Aplicadas

- ✅ **SOLID** - Princípios de design orientado a objetos
- ✅ **Clean Code** - Código limpo e legível
- ✅ **Clean Architecture** - Arquitetura em camadas
- ✅ **DTOs** - Desacoplamento entre camadas
- ✅ **Exception Handling** - Tratamento centralizado de erros
- ✅ **Validações** - Bean Validation em todas as entradas
- ✅ **Testes Automatizados** - Cobertura de testes unitários e de integração
- ✅ **Logs** - Sistema profissional de logs com SLF4J
- ✅ **Paginação** - Suporte a paginação em listagens
- ✅ **CORS** - Configurado para frontends
- ✅ **Profiles** - Ambientes dev e prod separados

## 📊 Endpoints Principais

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/auth/login` | Login e geração de token JWT | ❌ |
| POST | `/api/users` | Criar novo usuário | ❌ |
| GET | `/api/users` | Listar todos os usuários | ✅ |
| GET | `/api/users/{id}` | Buscar usuário por ID | ✅ |
| PUT | `/api/users/{id}` | Atualizar usuário | ✅ |
| DELETE | `/api/users/{id}` | Deletar usuário | ✅ |
| POST | `/api/accounts` | Criar conta bancária | ✅ |
| GET | `/api/accounts` | Listar todas as contas | ✅ |
| GET | `/api/accounts/{id}` | Buscar conta por ID | ✅ |
| GET | `/api/accounts/user/{userId}` | Listar contas de um usuário | ✅ |
| POST | `/api/transactions` | Realizar transação | ✅ |
| GET | `/api/transactions/account/{accountId}` | Consultar extrato | ✅ |

## 🐳 Docker (Opcional)

### Criar imagem Docker:
mvn clean package
docker build -t banco-digital-api .

text

### Executar com Docker Compose:
docker-compose up -d

text

## 📝 Estrutura do Banco de Dados

### Tabela: tb_users
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGSERIAL | Chave primária |
| nome | VARCHAR(100) | Nome completo |
| cpf | VARCHAR(11) | CPF único |
| email | VARCHAR | Email único |
| senha | VARCHAR | Senha encriptada |
| telefone | VARCHAR(15) | Telefone |
| role | VARCHAR | Papel (USER/ADMIN) |
| created_at | TIMESTAMP | Data de criação |
| updated_at | TIMESTAMP | Data de atualização |

### Tabela: tb_accounts
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGSERIAL | Chave primária |
| numero_conta | VARCHAR | Número único da conta |
| agencia | VARCHAR | Agência |
| saldo | DECIMAL(15,2) | Saldo atual |
| user_id | BIGINT | FK para tb_users |
| created_at | TIMESTAMP | Data de criação |

### Tabela: tb_transactions
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGSERIAL | Chave primária |
| tipo | VARCHAR | DEPOSITO/SAQUE/TRANSFERENCIA |
| valor | DECIMAL(15,2) | Valor da transação |
| descricao | VARCHAR(500) | Descrição opcional |
| account_id | BIGINT | FK para tb_accounts |
| account_destino_id | BIGINT | FK para conta destino (transferências) |
| created_at | TIMESTAMP | Data da transação |

## 🔒 Segurança

- Senhas criptografadas com **BCrypt**
- Autenticação **JWT** stateless
- Tokens com expiração de 24 horas
- Endpoints protegidos por autenticação
- CORS configurado para origens permitidas

## 🚀 Melhorias Futuras

- [ ] Implementar refresh token
- [ ] Adicionar roles (ADMIN, USER)
- [ ] Histórico de transações com filtros por data
- [ ] Limite de saque diário
- [ ] Notificações por email
- [ ] Docker Compose completo
- [ ] CI/CD com GitHub Actions
- [ ] Deploy na nuvem (AWS/Heroku)

## 👨‍💻 Autor

**Seu Nome**
- LinkedIn: [seu-linkedin](https://linkedin.com/in/seu-perfil)
- GitHub: [seu-github](https://github.com/seu-usuario)
- Email: seu-email@example.com
- Portfolio: [seu-portfolio.com](https://seu-portfolio.com)

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

⭐ Se este projeto foi útil, deixe uma estrela no GitHub!