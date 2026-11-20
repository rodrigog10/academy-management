# 🎓 Academy Management API

API RESTful desenvolvida em **Java** e **Spring Boot** para gerenciamento de cadastros e operações de uma academia. O projeto aplica arquitetura em camadas, validação de dados e regras de negócio, paginação, tratamento centralizado de exceções e testes automatizados.

---

## 🛠️ Tecnologias Utilizadas

- **Java 17+** & **Spring Boot 3**
- **Spring Data JPA** (Persistência e consultas paginadas)
- **Spring Validation** (Validação de entrada e contratos)
- **PostgreSQL** (Banco de dados relacional)
- **MapStruct & Lombok** (Mapeamento entre camadas e produtividade)
- **JUnit 5 & Mockito** (Testes unitários automatizados)
- **Docker & Docker Compose** (Containerização do ambiente)
- **Maven** (Gerenciamento de dependências e build)
- **SpringDoc OpenAPI & Scalar** (Documentação interativa e moderna da API)

---

## 🚀 Arquitetura e Destaques Técnicos

- **Separação Estrita de Camadas e DTOs:** Isolamento total entre as entidades de persistência e os contratos expostos na API (`Request`, `PatchRequest` e `Response`), protegendo o modelo de domínio.
- **Integridade e Regras de Negócio:** Validação de unicidade para dados sensíveis (CPF e e-mail) tanto no cadastro quanto nas operações de atualização, evitando inconsistências na base.
- **Resiliência em Atualizações Parciais (PATCH):** Lógica robusta que garante a integridade dos dados e regras de negócio mesmo em cenários de integração adversos (ex: front-end re-enviando payloads completos de dados não modificados).
- **Tratamento Global de Exceções:** Captura centralizada de erros de validação, regras de negócio e recursos não encontrados, retornando respostas HTTP semânticas e payloads JSON padronizados.
- **Qualidade de Software com Testes Unitários:** Cobertura abrangente da camada de serviços com JUnit 5 e Mockito. A suíte de testes valida fluxos de sucesso, exceções de negócio, isolamento de efeitos colaterais e blinda o código contra falsos positivos em mutações de dados.

---

## 📌 Endpoints da API

| Método | Rota | Descrição | Status Sucesso |
| :--- | :--- | :--- | :--- |
| `POST` | `/students` | Cadastra um novo aluno | `201 Created` |
| `GET` | `/students` | Lista os alunos de forma paginada | `200 OK` |
| `GET` | `/students/{id}` | Busca um aluno específico pelo ID | `200 OK` |
| `PUT` | `/students/{id}` | Atualização completa dos dados do aluno | `200 OK` |
| `PATCH` | `/students/{id}` | Atualização parcial dos dados do aluno | `200 OK` |
| `DELETE` | `/students/{id}` | Remove um aluno da base de dados | `204 No Content` |

---

## ⚙️ Como Executar o Projeto

### Pré-requisitos
- **JDK 17** ou superior
- **Docker** e **Docker Compose**
- **Git**

### Passo a Passo

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/SEU-USUARIO/NOME-DO-REPOSITORIO.git
   cd NOME-DO-REPOSITORIO

    Suba o banco de dados PostgreSQL via Docker Compose:
    Bash

    docker compose up -d

    Execute a aplicação:
    Bash

    ./mvnw spring-boot:run

    A API estará disponível em http://localhost:8080.

🧪 Executando os Testes

Para rodar a suíte completa de testes automatizados via Maven:
Bash

./mvnw test

🗺️ Próximos Passos

    [ ] Testes de camada Web (@WebMvcTest + MockMvc) para validação de contratos HTTP
