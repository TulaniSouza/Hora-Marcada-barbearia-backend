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

Execução com Docker:

```bash
docker compose up --build
```

Isso sobe a API na porta `8080` e o Postgres na porta `5432`. A API já vem configurada para usar as variáveis de ambiente do `docker-compose.yml`, mas você também pode apontar para um Postgres externo alterando `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD`.

Se quiser subir só a imagem da API e conectar em um banco já existente, use as mesmas variáveis no `docker run`.

Documentação e testes:

- A API expõe documentação OpenAPI/Swagger conforme configurado em `OpenApiConfig`.
- Testes de integração/uniários podem ser executados via Maven: `./mvnw test`.

Endpoints principais (resumo):

- `POST /api/auth/register` - Registrar novo barbeiro (retorna token JWT).
- `POST /api/auth/login` - Autenticar usuário e receber token JWT.
- `POST /api/appointments` - Criar agendamento.
- `GET /api/appointments` - Listar agendamentos.
- `GET /api/appointments/date?date=YYYY-MM-DD` - Buscar agendamentos por data.
- `GET /api/appointments/date/scheduled?date=YYYY-MM-DD` - Buscar agendamentos marcados por data.
- `PATCH /api/appointments/{id}/cancel` - Cancelar um agendamento pelo ID.
- `PATCH /api/appointments/{id}/complete` - Concluir um agendamento pelo ID.
- `GET /api/appointments/available-times?date=YYYY-MM-DD&serviceTypeId=ID` - Horários disponíveis para um tipo de serviço na data informada.
- `GET /api/barber/schedule?date=YYYY-MM-DD[&status=STATUS]` - Agenda do barbeiro para a data (opcional `status`).
- `POST /api/service-types` - Criar tipo de serviço.
- `GET /api/service-types` - Listar tipos de serviço.
- `GET /api/service-types/active` - Listar apenas tipos de serviço ativos.
- `GET /api/service-types/{id}` - Buscar tipo de serviço pelo ID.
- `PUT /api/service-types/{id}` - Atualizar tipo de serviço pelo ID.
- `DELETE /api/service-types/{id}` - Desativar tipo de serviço pelo ID.

Observações:

- Ajuste as configurações de banco em `src/main/resources/application.properties` conforme necessário.
- As migrations iniciais estão em `src/main/resources/db` e são aplicadas pela configuração do banco durante a inicialização (dependendo da configuração do projeto).
- O segredo JWT também pode ser sobrescrito via `JWT_SECRET_KEY`.

