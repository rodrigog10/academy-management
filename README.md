# 🎓 Academy Management API



API RESTful desenvolvida em **Java** e **Spring Boot** para gerenciamento de cadastros e operações de uma academia. O projeto aplica arquitetura em camadas, validação de dados e regras de negócio, paginação, tratamento centralizado de exceções e testes automatizados.



---



## 🛠️ Tecnologias Utilizadas



- **Java 17+** & **Spring Boot 3**

- **Spring Data JPA** (Persistência e consultas paginadas)

- **Spring Validation** (Validação de entrada e contratos)

- **PostgreSQL & Flyway** (Banco de dados relacional e versionamento de schema)

- **MapStruct & Lombok** (Mapeamento entre camadas e produtividade)

- **JUnit 5, Mockito & JaCoCo** (Testes unitários automatizados e análise de cobertura)

- **Docker & Docker Compose** (Containerização do ambiente)

- **Maven** (Gerenciamento de dependências e build)

- **SpringDoc OpenAPI & Scalar** (Documentação interativa e moderna da API)



---



## 🚀 Arquitetura e Destaques Técnicos



- **Separação Estrita de Camadas e DTOs:** Isolamento total entre as entidades de persistência e os contratos expostos na API (`Request`, `PatchRequest` e `Response`), protegendo o modelo de domínio.

- **Integridade e Regras de Negócio:** Validação de unicidade para dados sensíveis (CPF e e-mail) tanto no cadastro quanto nas operações de atualização, evitando inconsistências na base.

- **Resiliência em Atualizações Parciais (PATCH):** Lógica robusta que garante a integridade dos dados e regras de negócio mesmo em cenários de integração adversos (ex: front-end re-enviando payloads completos de dados não modificados).

- **Tratamento Global de Exceções:** Captura centralizada de erros de validação, regras de negócio e recursos não encontrados, retornando respostas HTTP semânticas e payloads JSON padronizados.

- **Qualidade de Software com Testes Unitários:** Cobertura da camada de serviços com JUnit 5, Mockito e JaCoCo (meta mínima de 80% de *branches* em `services`), validando fluxos de sucesso, exceções de negócio e isolamento de efeitos colaterais.



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

   git clone https://github.com/SEU-USUARIO/gestao-academia.git

   cd gestao-academia

   ```



2. **Suba o banco de dados PostgreSQL via Docker Compose:**

   ```bash

   docker compose up -d

   ```



3. **Execute a aplicação:**

   ```bash

   ./mvnw spring-boot:run

   ```



A API estará disponível em `http://localhost:8080`.



---



## 🧪 Executando os Testes



Para rodar apenas os testes unitários isolados (sem necessidade de subir o banco de dados):

```bash

./mvnw clean test -Dtest=StudentServiceTests

```



Para rodar a suíte completa (requer o PostgreSQL ativo via Docker):

```bash

./mvnw clean test

```



Após a execução, o relatório de cobertura do **JaCoCo** estará disponível em `target/site/jacoco/index.html`.



---



## 🗺️ Próximos Passos



- [ ] Testes de camada Web (`@WebMvcTest` + `MockMvc`) para validação de contratos HTTP

- [ ] Testes de integração com persistência real (`Testcontainers` + `Flyway`)

- [ ] Autenticação e autorização stateless com `Spring Security` + `JWT`
