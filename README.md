# AltisLab - Library Backend

API REST para gerenciamento de uma biblioteca, desenvolvida com **Java e Spring Boot**.

O sistema possui autenticação utilizando **JWT**, controle de acesso entre administradores e locatários, gerenciamento de usuários, livros, editoras e aluguéis, além de dashboards com informações sobre os empréstimos e o acervo da biblioteca.

## Tecnologias utilizadas

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- BCrypt
- PostgreSQL
- Flyway
- Maven
- Lombok

## Estrutura do projeto

O projeto está organizado por funcionalidades:

```text
src/main/java/com/altis/library_backend/

├── auth/
│   ├── controllers/
│   ├── models/dtos/
│   └── services/
│
├── books/
│   ├── controllers/
│   ├── models/
│   ├── repositories/
│   └── services/
│
├── dashboard/
│   ├── controllers/
│   ├── models/dtos/
│   └── services/
│
├── publishers/
│   ├── controllers/
│   ├── models/
│   │   ├──dtos/
│   │   └── entities/
│   ├── repositories/
│   └── services/
│
├── rentals/
│   ├── controllers/
│   ├── models/
│   │   ├──dtos/
│   │   └── entities/
│   ├── repositories/
│   └── services/
│
├── users/
│   ├── controllers/
│   ├── models/
│   │   ├──dtos/
│   │   └── entities/
│   ├── repositories/
│   └── services/
│
└── infra/
    └── security/
```

## Autenticação e autorização

A API utiliza **Spring Security e JWT** para autenticação.

Após realizar o login corretamente, o usuário recebe um token JWT que deve ser enviado nas requisições protegidas:

```text
Authorization: Bearer <token>
```

O sistema possui dois níveis de acesso:

### Locatário

O locatário é o tipo padrão de usuário criado pelo cadastro público.

Pode:

- Visualizar livros;
- Visualizar editoras;
- Consultar seus próprios dados;
- Atualizar informações permitidas do próprio perfil;
- Consultar sua dashboard;
- Consultar informações relacionadas aos seus aluguéis.

### Administrador

O administrador possui acesso às funcionalidades administrativas da biblioteca.

Pode:

- Cadastrar, editar e excluir livros;
- Cadastrar, editar e excluir editoras;
- Consultar usuários;
- Alterar dados de usuários;
- Excluir usuários;
- Gerenciar aluguéis;
- Consultar a dashboard administrativa.

As permissões são controladas pelas authorities:

```text
ROLE_USER
ROLE_ADMIN
```

## Login

O login é realizado utilizando as credenciais do usuário.

```http
POST /auth/login
```

Após a autenticação, a API gera um JWT contendo o e-mail do usuário como `subject`.

O token é posteriormente validado pelo filtro de segurança em cada requisição protegida.

## Cadastro

```http
POST /auth/register
```

Todo usuário cadastrado pela rota pública é criado como locatário.

Por padrão:

```text
isAdmin = false
isDisabled = false
```

A senha é armazenada utilizando BCrypt.

## Recuperação de senha

```http
POST /auth/forgot-password
```

A recuperação utiliza os dados cadastrados do usuário para validar a solicitação antes da alteração da senha.

A nova senha é novamente criptografada utilizando BCrypt antes de ser armazenada.

## Usuários

Principais endpoints:

```http
GET /users
GET /users/{id}
GET /users/me
PUT /users/{id}
PUT /users/me
DELETE /users/{id}
```

`/users/me` utiliza o usuário autenticado através do `@AuthenticationPrincipal`, evitando a necessidade de informar manualmente o ID do próprio usuário.

Algumas operações de gerenciamento são exclusivas para administradores.

## Livros

A API permite o gerenciamento do acervo da biblioteca.

Entre as informações de um livro estão:

- ISBN;
- Título;
- Gênero;
- Data de lançamento;
- Quantidade disponível;
- Editora.

As operações de criação, alteração e exclusão são restritas ao administrador.

Usuários autenticados podem consultar o acervo.

## Editoras

O sistema também permite o gerenciamento das editoras responsáveis pelos livros.

As operações administrativas são restritas a usuários com `ROLE_ADMIN`.

## Aluguéis

Os aluguéis relacionam:

```text
Usuário
   ↓
Rental
   ↓
Livro
```

Cada aluguel possui informações como:

- Data inicial;
- Data de devolução;
- Status;
- Indicação de devolução;
- Indicação de renovação.

O sistema utiliza as datas do aluguel para identificar sua situação.

### Status dos aluguéis

Os status utilizados na dashboard são:

```text
ON_TIME
```

Aluguel dentro do prazo.

```text
NEAR_DUE
```

Aluguel que vence no dia atual ou nos próximos dois dias.

```text
OVERDUE
```

Aluguel cuja data de devolução já passou e que ainda não foi devolvido.

```text
RETURNED
```

Aluguel já devolvido.

## 📊 Dashboard do locatário

```http
GET /dashboard/me
```

A dashboard do usuário reúne informações relacionadas ao próprio histórico e ao acervo da biblioteca.

Ela apresenta:

- Últimos aluguéis;
- Quantidade de aluguéis em dia;
- Quantidade próxima do vencimento;
- Quantidade de aluguéis atrasados;
- Livro mais alugado pelo usuário;
- Lista de livros e suas quantidades disponíveis.

Exemplo de parte da resposta:

```json
{
  "onTimeRentals": 2,
  "nearDueRentals": 1,
  "overdueRentals": 0,
  "mostRentedBook": "1984",
  "availableBooks": [
    {
      "title": "1984",
      "quantity": 4
    },
    {
      "title": "Dom Casmurro",
      "quantity": 7
    }
  ]
}
```

## Dashboard administrativa

```http
GET /dashboard/admin
```

Disponível somente para administradores.

Apresenta informações globais da biblioteca, como:

- Aluguéis em dia;
- Aluguéis próximos do vencimento;
- Aluguéis atrasados;
- Últimos aluguéis realizados;
- Total de aluguéis;
- Total de livros cadastrados;
- Total de editoras cadastradas;
- Livro mais alugado.

## Segurança

As rotas são protegidas utilizando Spring Security.

Exemplos das regras utilizadas:

```text
/auth/login             → público
/auth/register          → público
/auth/forgot-password   → público

/dashboard/me           → usuário autenticado
/dashboard/admin        → somente ADMIN

GET /books/**           → somente ADMIN
POST /books/**          → somente ADMIN
PUT /books/**           → somente ADMIN
DELETE /books/**        → somente ADMIN
```

A aplicação utiliza uma política **stateless**, portanto o servidor não mantém uma sessão de autenticação do usuário.

Cada requisição protegida deve apresentar um JWT válido.

## Security Filter

O filtro de segurança intercepta as requisições protegidas e:

1. Recupera o JWT do header `Authorization`;
2. Remove o prefixo `Bearer`;
3. Valida o token;
4. Recupera o e-mail armazenado no `subject`;
5. Busca o usuário no banco de dados;
6. Recupera suas permissões;
7. Registra sua autenticação no `SecurityContext`.

Dessa forma, o Spring Security consegue identificar o usuário responsável pela requisição e verificar suas permissões.

## Banco de dados

O projeto utiliza **PostgreSQL**.

As alterações na estrutura do banco são controladas através de migrations utilizando **Flyway**.

As principais entidades do sistema são:

```text
Users
Books
Publishers
Rentals
```

Os relacionamentos entre entidades são realizados utilizando JPA, incluindo relações como:

```java
@ManyToOne
@JoinColumn(...)
```

## Executando o projeto

### Pré-requisitos

É necessário possuir:

- Java 21;
- Maven;
- PostgreSQL.

Clone o repositório:

```bash
git clone <URL_DO_REPOSITORIO>
```

Entre na pasta:

```bash
cd altislab-backend
```

Configure a conexão com o PostgreSQL no arquivo:

```text
src/main/resources/application.properties
```

Também configure o secret utilizado na geração dos JWTs:

```properties
api.security.token.secret=SEU_SECRET
```

Execute:

```bash
mvn spring-boot:run
```

Ou execute a classe principal da aplicação diretamente pela IDE.

## Testando a API

A API pode ser testada utilizando ferramentas como Postman ou Insomnia.

Para acessar uma rota protegida:

1. Realize o login;
2. Copie o JWT retornado;
3. Selecione autenticação do tipo Bearer Token;
4. Informe o JWT;
5. Realize a requisição.

## Arquitetura

O projeto utiliza separação de responsabilidades entre:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Os DTOs são utilizados para controlar os dados recebidos e retornados pela API, evitando a exposição direta das entidades.

## Principais funcionalidades

- Autenticação com JWT;
- Senhas protegidas com BCrypt;
- Controle de acesso entre administrador e locatário;
- Cadastro de usuários;
- Atualização do próprio perfil;
- Recuperação de senha;
- CRUD de livros;
- CRUD de editoras;
- Gerenciamento de aluguéis;
- Controle de devolução e renovação;
- Status de vencimento dos aluguéis;
- Dashboard do locatário;
- Dashboard administrativa;
- Controle de migrations com Flyway.

## Desenvolvimento

Projeto desenvolvido como API backend para gerenciamento de biblioteca utilizando Java, Spring Boot e PostgreSQL.
