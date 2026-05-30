# Priorium 🚀

O Priorium API é uma API REST desenvolvida em Java com Spring Boot, projetada para a otimização de fluxos de trabalho e gerenciamento inteligente de tarefas. O diferencial do sistema é o seu motor de priorização automática e a gestão de grafos de dependência entre atividades, impedindo a conclusão de tarefas bloqueadas.

## 🏗️ Arquitetura do Sistema e Design Patterns

A aplicação adota uma arquitetura em camadas (MVC) bem delimitadas para garantir alta coesão e baixo acoplamento:

- Controller: Porta de entrada da API REST.
- Service: Detentora das regras de negócio, algoritmos de priorização e validações de segurança.
- Repository: Abstração de persistência através do Spring Data JPA.
- Model/Entity: Mapeamento objeto-relacional direto com o PostgreSQL.

## 🛠️ Stack Tecnológica
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
```

## 🛣️ Roadmap e Status do Desenvolvimento

- [x] Infraestrutura Core & Conexão com o Banco
- [x] Configuração de Segurança Inicial (BCrypt)
- [x] Versionamento de Banco (Flyway Baseline)
- [x] Módulo de Usuários
  - [x] Entidade User e UserRepository
  - [x] Endpoint de Cadastro (/api/users/register)
- [x] Autenticação e Login (Tokens JWT)
- [ ] Módulo de Categorias ⏳
- [ ] Módulo de Tarefas & Priorização ⏳
- [ ] Motor de Dependências (Prevenção de Deadlocks) ⏳

## 🚀 Como Executar Localmente

1. Clone o repositório.
2. Crie um banco de dados vazio no PostgreSQL.
3. Configure o arquivo .env com as suas credenciais.
4. Limpe e instale as dependências: `mvn clean install`
5. Inicie o servidor: `mvn spring-boot:run`
6. A API estará escutando na porta `8080`.