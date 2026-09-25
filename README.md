# AltisLab - Library Backend

API REST para gerenciamento de uma biblioteca, desenvolvida com **Java e Spring Boot**.

O sistema possui autenticação utilizando **JWT**, controle de acesso entre administradores e locatários, gerenciamento de usuários, livros, editoras e aluguéis, dashboards, paginação e filtros dinâmicos para consulta dos dados.

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
- Springdoc OpenAPI / Swagger

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
│   ├── services/
│   └── specifications/
│
├── dashboard/
│   ├── controllers/
│   ├── models/dtos/
│   └── services/
│
├── publishers/
│   ├── controllers/
│   ├── models/
│   │   ├── dtos/
│   │   └── entities/
│   ├── repositories/
│   ├── services/
│   └── specifications/
│
├── rentals/
│   ├── controllers/
│   ├── models/
│   │   ├── dtos/
│   │   └── entities/
│   ├── repositories/
│   ├── services/
│   └── specifications/
│
├── users/
│   ├── controllers/
│   ├── models/
│   │   ├── dtos/
│   │   └── entities/
│   ├── repositories/
│   ├── services/
│   └── specifications/
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

- Visualizar informações permitidas do acervo;
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

O token também possui informações adicionais através de **claims**, como a identificação do nível administrativo do usuário.

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

As consultas de usuários podem utilizar **paginação e filtros dinâmicos**, implementados com `Pageable` e `JpaSpecificationExecutor`.

Contas administrativas podem ser excluídas das consultas destinadas ao gerenciamento comum de usuários.

Também é possível utilizar informações como o estado da conta (`isDisabled`) como critério de filtragem.

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

As consultas utilizam **paginação** e permitem a aplicação de **filtros dinâmicos**, evitando a necessidade de criar um método específico no repositório para cada combinação de pesquisa.

Entre os critérios que podem ser utilizados estão informações como:

- Título;
- ISBN;
- Gênero;
- Editora.

Os filtros podem ser combinados em uma mesma consulta.

## Editoras

O sistema também permite o gerenciamento das editoras responsáveis pelos livros.

As operações administrativas são restritas a usuários com `ROLE_ADMIN`.

As consultas podem utilizar paginação e filtros dinâmicos através das Specifications implementadas para a funcionalidade.

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

- Usuário responsável;
- Livro alugado;
- Data inicial;
- Data de devolução;
- Status.

Ao realizar um novo aluguel, a quantidade disponível do livro é reduzida.

Quando o livro é devolvido, sua quantidade disponível é incrementada novamente.

O prazo inicial de um aluguel é de **14 dias**.

A renovação adiciona mais **14 dias** à data prevista para devolução.

### Status dos aluguéis

A situação do aluguel é calculada utilizando a data prevista para devolução e seu estado atual.

Os estados apresentados pela API incluem:

```text
ACTIVE
```

Aluguel ativo e dentro do prazo.

```text
DUE_SOON
```

Aluguel próximo da data prevista para devolução.

```text
LATE
```

Aluguel cuja data prevista para devolução já passou e que ainda não foi devolvido.

```text
RETURNED
```

Aluguel já devolvido.

### Aluguéis atrasados

Um aluguel atrasado **não bloqueia automaticamente o acesso do usuário à API**.

A situação de atraso é identificada pelo sistema e pode ser apresentada nas consultas e dashboards para informar que existem pendências relacionadas aos empréstimos.

Dessa forma, a aplicação mantém a informação sobre o atraso sem desativar automaticamente a conta ou impedir o login do usuário.

### Pesquisa de aluguéis

As consultas de aluguéis utilizam **JPA Specifications** e podem ser combinadas com paginação.

É possível pesquisar os aluguéis utilizando informações relacionadas às entidades associadas, como:

- Nome do usuário;
- Título do livro.

Por exemplo, uma pesquisa pelo nome do usuário acessa o relacionamento entre `Rental` e `User`, enquanto uma pesquisa pelo título acessa o relacionamento entre `Rental` e `Book`.

## Paginação

As consultas que podem retornar uma quantidade maior de registros utilizam o `Pageable` do Spring Data.

A paginação permite controlar:

```text
page → página desejada
size → quantidade máxima de elementos por página
sort → ordenação dos resultados
```

Exemplo:

```http
GET /books?page=0&size=10&sort=title,asc
```

A resposta utiliza `Page`, mantendo informações como:

- Conteúdo da página;
- Número da página atual;
- Quantidade de elementos;
- Total de elementos;
- Total de páginas.

A paginação também é utilizada em conjunto com as Specifications, permitindo filtrar os dados antes de separá-los em páginas.

## Filtros dinâmicos com JPA Specifications

O projeto utiliza **JPA Specifications** para construir consultas dinâmicas.

Os repositórios que precisam desse recurso utilizam:

```java
JpaSpecificationExecutor<Entity>
```

As Specifications permitem criar condições independentes e combiná-las conforme os parâmetros recebidos pela API.

Exemplo conceitual:

```text
Filtro por título
        +
Filtro por gênero
        +
Filtro por editora
        ↓
Consulta final
```

Caso determinado parâmetro não seja informado, seu filtro pode ser ignorado.

Essa abordagem evita a criação de vários métodos de repositório para cada possível combinação de filtros.

As Specifications são utilizadas em conjunto com `Pageable`, permitindo executar consultas filtradas e paginadas:

```java
repository.findAll(specification, pageable);
```

## Dashboard do locatário

```http
GET /dashboard/me
```

A dashboard do usuário reúne informações relacionadas ao próprio histórico e ao acervo da biblioteca.

Ela apresenta:

- Últimos aluguéis;
- Quantidade de aluguéis em dia;
- Quantidade de aluguéis próximos do vencimento;
- Quantidade de aluguéis atrasados;
- Livro mais alugado pelo usuário;
- Livros disponíveis no acervo.

A listagem de livros disponíveis utiliza paginação para evitar o retorno de todo o acervo em uma única requisição.

As estatísticas da dashboard continuam sendo calculadas considerando os dados necessários para cada indicador, sem limitar os cálculos somente à página atual.

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
- Livro mais alugado;
- Livros disponíveis no acervo.

A listagem de livros disponíveis também utiliza paginação.

## Swagger / OpenAPI

A API possui documentação interativa utilizando **Swagger/OpenAPI** através do Springdoc.

O Swagger permite:

- Visualizar os endpoints disponíveis;
- Consultar os parâmetros das requisições;
- Testar endpoints;
- Informar o JWT para acessar rotas protegidas;
- Testar parâmetros de paginação;
- Testar os filtros disponibilizados pelas Specifications.

O `Pageable` é exposto na documentação através de `@ParameterObject`, permitindo visualizar parâmetros como:

```text
page
size
sort
```

## Segurança

As rotas são protegidas utilizando Spring Security.

Exemplos de rotas da aplicação:

```text
/auth/login             → público
/auth/register          → público
/auth/forgot-password   → público

/dashboard/me           → usuário autenticado
/dashboard/admin        → somente ADMIN
```

As demais permissões são configuradas conforme o tipo de operação e o nível de acesso necessário.

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

A API pode ser testada utilizando **Swagger**, Postman ou Insomnia.

Para acessar uma rota protegida:

1. Realize o login;
2. Copie o JWT retornado;
3. Informe o token como Bearer Token;
4. Realize a requisição.

Para endpoints paginados, também podem ser informados parâmetros como:

```text
page=0
size=10
sort=title,asc
```

Quando disponíveis, os filtros das Specifications também podem ser combinados com esses parâmetros.

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

As Specifications adicionam uma camada responsável pela construção dos critérios de pesquisa utilizados nas consultas dinâmicas.

## Principais funcionalidades

- Autenticação com JWT;
- Claims personalizados no JWT;
- Senhas protegidas com BCrypt;
- Controle de acesso entre administrador e locatário;
- Cadastro de usuários;
- Atualização do próprio perfil;
- Recuperação de senha;
- CRUD de livros;
- CRUD de editoras;
- Gerenciamento de aluguéis;
- Controle de devolução e renovação;
- Identificação de aluguéis próximos do vencimento e atrasados;
- Dashboard do locatário;
- Dashboard administrativa;
- Paginação com Spring Data `Pageable`;
- Filtros dinâmicos com JPA Specifications;
- Consultas combinando Specification e Pageable;
- Documentação e testes através do Swagger/OpenAPI;
- Controle de migrations com Flyway.

## Desenvolvimento

Projeto desenvolvido como API backend para gerenciamento de biblioteca utilizando Java, Spring Boot e PostgreSQL.
