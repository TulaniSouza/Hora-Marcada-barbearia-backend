# Atom Backend

Breve resumo da API:

Esta é uma API backend construída com Spring Boot para gerenciar agendamentos de uma barbearia. Fornece autenticação via JWT, gerenciamento de tipos de serviço, barbeiros e agendamentos, além de tratamento centralizado de erros e documentação OpenAPI.

Funcionalidades principais:

- Autenticação e autorização JWT (`AuthController`, `JwtService`, `JwtAuthenticationFilter`).
- CRUD e operações de agendamento (`AppointmentController`, `AppointmentService`).
- Gestão de tipos de serviço (`ServiceTypeController`) e disponibilidade/agenda (`ScheduleController`).
- Persistência com Spring Data JPA e migrations SQL em `src/main/resources/db`.
- Converters/mappers para DTOs (`AppointmentMapper`, `ServiceTypeMapper`).
- Tratamento de exceções centralizado (`GlobalExceptionHandler`, `BusinessException`, `ResourceNotFoundException`).
- Documentação OpenAPI configurada em `OpenApiConfig`.

Estrutura relevante do projeto:

- `src/main/java/dev/guilhermesilva/atom_backend/controller` - Controllers REST.
- `src/main/java/dev/guilhermesilva/atom_backend/service` - Lógica de negócio.
- `src/main/java/dev/guilhermesilva/atom_backend/entity` - Entidades JPA.
- `src/main/java/dev/guilhermesilva/atom_backend/repository` - Repositórios Spring Data.
- `src/main/resources/db` - Migrations SQL (`V1__...`, `V2__...`).

Execução (com Maven Wrapper):

No Windows (PowerShell):

```powershell
mvnw.cmd spring-boot:run
```

No macOS / Linux:

```bash
./mvnw spring-boot:run
```

Build do jar:

```bash
./mvnw clean package
# ou no Windows
mvnw.cmd clean package
```

Documentação e testes:

- A API expõe documentação OpenAPI/Swagger conforme configurado em `OpenApiConfig`.
- Testes de integração/uniários podem ser executados via Maven: `./mvnw test`.

Endpoints principais (resumo):

- `POST /auth/login` - Autenticar usuário e receber token JWT.
- `POST /appointments` - Criar agendamento.
- `GET /appointments` - Listar agendamentos.
- `PUT /appointments/{id}` - Atualizar status/detalhes do agendamento.
- `GET /service-types` - Listar tipos de serviço.

Observações:

- Ajuste as configurações de banco em `src/main/resources/application.properties` conforme necessário.
- As migrations iniciais estão em `src/main/resources/db` e são aplicadas pela configuração do banco durante a inicialização (dependendo da configuração do projeto).

