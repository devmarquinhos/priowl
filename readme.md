# Priowl 🚀

O Priowl API é uma API REST desenvolvida em Java com Spring Boot, projetada para a otimização de fluxos de trabalho e gerenciamento inteligente de tarefas. O diferencial do sistema é o seu motor de priorização automática e a gestão de grafos de dependência entre atividades, impedindo a conclusão de tarefas bloqueadas.

## 🏗️ Arquitetura do Sistema e Design Patterns

A aplicação adota uma arquitetura em camadas (MVC) bem delimitadas para garantir alta coesão e baixo acoplamento:

- Controller: Porta de entrada da API REST.
- Service: Detentora das regras de negócio, algoritmos de priorização e validações de segurança.
- Repository: Abstração de persistência através do Spring Data JPA.
- Model/Entity: Mapeamento objeto-relacional direto com o PostgreSQL.

## 🛠️ Stack
- Linguagem: Java 17+
- Framework: Spring Boot 3.x+
- Banco de Dados: PostgreSQL
- ORM: Spring Data JPA / Hibernate
- Migrations: Flyway
- Segurança: Spring Security (BCrypt)
- Ferramentas: Maven, Lombok

## 🗄️ Variáveis de Ambiente (.env.properties)

O projeto segue a arquitetura 12-Factor App. Crie um arquivo .env na raiz do projeto contendo as seguintes credenciais:

```
    DB_URL=jdbc:postgresql://localhost:5432/nome_do_seu_banco
    DB_USERNAME=postgres
    DB_PASSWORD=sua_senha
    JWT_SECRET=seu_jwt_secret
```

## 📍 Documenacao dos Endpoints
### 👤 Módulo de Usuários

| Tipo | Endpoint              | Response Esperado                                                       |
|:----:|-----------------------|-------------------------------------------------------------------------|
| POST | `/api/users/register` | Objeto contendo os dados do usuário recém-cadastrado (omitindo a senha) |
| POST | `/api/users/login`    | Token JWT                                                               |

### 🏷️ Módulo de Categorias

|  Tipo  | Endpoint               | Response Esperado                                                                      |
|:------:|------------------------|----------------------------------------------------------------------------------------|
|  POST  | `/api/categories`      | Detalhes da nova categoria criada e vinculada ao usuário autenticado                   |
|  GET   | `/api/categories`      | Lista completa contendo todas as categorias ativas atreladas ao usuário logado.        |
|  PUT   | `/api/categories/{id}` | O objeto da categoria totalmente atualizado após as modificações de nome ou cor/ícone. |
| DELETE | `/api/categories/{id}` | Confirmação de exclusão da categoria (Status HTTP 204 No Content).                     |

### 🎯 Modulo das Tarefas

|  Tipo  | Endpoint             | Response Esperado                                                                                              |
|:------:|----------------------|----------------------------------------------------------------------------------------------------------------|
|  POST  | `/api/tasks`         | Entidade da tarefa recém-criada (com ID gerado, status inicial e amarrações de herança).                       |
|  GET   | `/api/tasks`         | Todas as tarefas do usuário, com o percentual de progresso da ramificação (branchProgress).                    |
|  GET   | `/api/tasks/{id}`    | Detalhes de uma tarefa específica, incluindo sua posição na hierarquia de dependências.                        |
|  PUT   | `/api/tasks/{id}`    | Objeto da tarefa atualizado após as modificações de título, descrição, importância ou prazo.                   |
| DELETE | `/api/tasks/{id}`    | Confirmação de exclusão da atividade (Status HTTP 204 No Content).                                             |
|  GET   | `/api/tasks/summary` | Sumário global estatístico para o Dashboard (percentual de progresso total, ativas e concluídas).              |
|  GET   | `/api/tasks/filter?` | Lista de tarefas filtrada pela interseção de status, importância, título parcial ou data limite de vencimento. |

## 🛣️ Roadmap e Status do Desenvolvimento

- [x] Infraestrutura Core & Conexão com o Banco
- [x] Configuração de Segurança Inicial (BCrypt)
- [x] Versionamento de Banco (Flyway Baseline)
- [x] Módulo de Usuários
  - [x] Entidade User e UserRepository
  - [x] Endpoint de Cadastro (/api/users/register)
- [x] Autenticação e Login (Tokens JWT)
- [x] Módulo de Categorias ⏳
- [x] Módulo de Tarefas & Priorização ⏳
- [x] Motor de Dependências (Prevenção de Deadlocks) ⏳

## 🚀 Como Executar Localmente

1. Clone o repositório.
2. Crie um banco de dados vazio no PostgreSQL.
3. Configure o arquivo .env com as suas credenciais.
4. Limpe e instale as dependências: `mvn clean install`
5. Inicie o servidor: `mvn spring-boot:run`
6. A API estará escutando na porta `8080`.
