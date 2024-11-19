# Auth API

## Descrição

O projeto `mme-auth` é uma API desenvolvida com o framework Spring Boot, que tem como objetivo fornecer uma interface login e cadastro dos usuários. Esta API é construída utilizando Java 17 e utiliza o Spring Boot para simplificar a configuração e o desenvolvimento da aplicação.

## Tecnologias Utilizadas

### Spring Boot

- **Spring Boot Starter Data JPA**: Facilita a integração com JPA (Java Persistence API) para operações de banco de dados. Este starter simplifica a configuração do JPA e do Hibernate.
- **Spring Boot Starter Web**: Fornece os componentes essenciais para o desenvolvimento de aplicações web, incluindo APIs RESTful. Inclui o Tomcat como o contêiner embutido padrão.
- **Spring Boot DevTools**: Melhora a experiência de desenvolvimento oferecendo recursos como reinicializações automáticas e recarregamento ao vivo. Esta dependência é usada apenas em tempo de execução e é opcional.
- **Spring Boot Starter Test**: Fornece ferramentas para testar aplicações Spring Boot, incluindo JUnit, Hamcrest e Mockito.

### Banco de Dados

- **Microsoft SQL Server JDBC Driver**: Esta dependência (`mssql-jdbc`) é usada para conectar a aplicação a um banco de dados Microsoft SQL Server. A versão especificada é `12.8.1.jre11`, compatível com o Java 17.

### Java

- **Java 17**: O projeto está configurado para usar o Java 17, que é uma versão de suporte de longo prazo (LTS) do Java, oferecendo estabilidade e recursos aprimorados.

## Configuração do Projeto

### Dependências

O projeto utiliza o Maven como ferramenta de construção e gerenciamento de dependências. As principais dependências são:

- **spring-boot-starter-data-jpa**: Para integração com JPA e acesso a dados.
- **spring-boot-starter-web**: Para desenvolvimento de APIs REST.
- **spring-boot-devtools**: Para uma melhor experiência de desenvolvimento com reinicialização automática.
- **spring-boot-starter-test**: Para testes automatizados.
- **mssql-jdbc**: Para conexão com o banco de dados Microsoft SQL Server.

### Plugins

- **spring-boot-maven-plugin**: Permite criar JARs executáveis e simplifica o processo de execução e empacotamento da aplicação.

## Execução do Projeto

Para executar o projeto, você pode usar o comando Maven:

```bash
mvn spring-boot:run
```
Isso iniciará a aplicação e a deixará disponível no seu servidor local.

## Testes
Os testes podem ser executados usando o Maven:
```bash
mvn test
```
Isso executará todos os testes definidos no projeto.


# Manual Instalação do ambiente local 

1 - Para subir a aplicação localmente é necessário ter o docker instalado 
2- Deve ser instalado a imagem do banco de dados sqlserver 

```bash
docker pull mcr.microsoft.com/mssql/server:2019-latest
```
Este comando irá efetuar o download da imagem docker 

3- Deve-se subir a imagem do sqlserver no docker 

```bash
docker run -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=StrongPassword1!' -p 1433:1433 --name sqlserver_container -d mcr.microsoft.com/mssql/server:2019-latest
```
Este comando irá iniciar a image do sqlserver com as credencial defeaut conforme configurado no arquivo application.properties


### Script para criação do banco de dados e uma tabela exemplo 
```sql

CREATE ROLE "autobot_crypto" SUPERUSER CREATEDB CREATEROLE INHERIT LOGIN PASSWORD 'autobot_crypto';
CREATE DATABASE autobot_crypto OWNER=autobot_crypto;
    
-- Criação do banco de dados
CREATE DATABASE auth;

-- Uso do banco de dados criado
USE auth;

-- AUTH.dbo.TB_AUD_RECUPERA_SENHA_USUARIO definition

-- Drop table
-- DROP TABLE AUTH.dbo.TB_AUD_RECUPERA_SENHA_USUARIO;

CREATE TABLE TB_AUD_RECUPERA_SENHA_USUARIO (
                                               PK_AUD_RECUPERA_SENHA_USUARIO serial PRIMARY KEY,
                                               USUARIO_ID bigint NOT NULL,
                                               DT_CREATED_AT timestamp NOT NULL,
                                               SUCCESSO_RECUPERAR char(1) NOT NULL,
                                               IP_ADDRESS varchar(45) NOT NULL,
                                               CONSTRAINT CKC_SUCCESSO_RECUPERAR CHECK (SUCCESSO_RECUPERAR IN ('N', 'S'))
);

-- AUTH.dbo.TB_PERMISSAO_ACESSO definition

-- Drop table
-- DROP TABLE AUTH.dbo.TB_PERMISSAO_ACESSO;

CREATE TABLE TB_PERMISSAO_ACESSO (
                                     PK_PERMISSAO_ACESSO serial PRIMARY KEY,
                                     DS_PERMISSAO_ACESSO varchar(100) NOT NULL
);

-- AUTH.dbo.TB_SISTEMA definition

-- Drop table
-- DROP TABLE AUTH.dbo.TB_SISTEMA;

CREATE TABLE TB_SISTEMA (
                            PK_SISTEMA serial PRIMARY KEY,
                            DS_SISTEMA varchar(100)
);

-- AUTH.dbo.TB_USUARIO definition

-- Drop table
-- DROP TABLE AUTH.dbo.TB_USUARIO;

CREATE TABLE TB_USUARIO (
                            PK_USUARIO serial PRIMARY KEY,
                            DS_EMAIL varchar(100) NOT NULL,
                            DS_SENHA varchar(100) NOT NULL,
                            NU_CPF varchar(11) NOT NULL,
                            NO_USUARIO varchar(200) NOT NULL,
                            NO_INSTITUICAO varchar(200),
                            SG_UF char(2) NOT NULL,
                            DT_CREATED_AT timestamp NOT NULL,
                            DT_UPDATED_AT timestamp,
                            NU_DELETED smallint DEFAULT 0 NOT NULL,
                            DS_TOKEN varchar(500),
                            DS_REFRESH_TOKEN varchar(500),
                            QT_TENTATIVAS_RECUPERAR smallint DEFAULT 0 NOT NULL,
                            DT_ULTIMA_TENTATIVA_RECUPERACAO timestamp,
                            IS_ACTIVE smallint DEFAULT 0 NOT NULL,
                            CONSTRAINT CKC_NU_DELETED_USUARIO CHECK (NU_DELETED IN (0, 1))
);

-- AUTH.dbo.TB_CONTATO definition

-- Drop table
-- DROP TABLE AUTH.dbo.TB_CONTATO;

CREATE TABLE TB_CONTATO (
                            PK_CONTATO serial PRIMARY KEY,
                            PK_USUARIO bigint NOT NULL,
                            NU_DDD int NOT NULL,
                            NU_TELEFONE varchar(14) NOT NULL,
                            TP_CONTATO char(1) DEFAULT '1' NOT NULL,
                            DT_CREATED_AT timestamp NOT NULL,
                            DT_UPDATED_AT timestamp,
                            NU_DELETED smallint DEFAULT 0 NOT NULL,
                            CONSTRAINT CONTATO_USUARIO_FK FOREIGN KEY (PK_USUARIO) REFERENCES TB_USUARIO(PK_USUARIO),
                            CONSTRAINT CC_CONTATO_TIPO_CONTATO CHECK (TP_CONTATO IN ('1', '2', '3')),
                            CONSTRAINT CKC_NU_DELETED_CONTATO CHECK (NU_DELETED IN (0, 1))
);

-- AUTH.dbo.TB_PERFIL_ACESSO definition

-- Drop table
-- DROP TABLE AUTH.dbo.TB_PERFIL_ACESSO;

CREATE TABLE TB_PERFIL_ACESSO (
                                  PK_PERFIL_ACESSO serial PRIMARY KEY,
                                  PK_SISTEMA bigint,
                                  NO_PERFIL_ACESSO varchar(150) NOT NULL,
                                  DS_PERFIL varchar(300) NOT NULL,
                                  CONSTRAINT TB_PERFIL_ACESSO_TB_SISTEMA_FK FOREIGN KEY (PK_SISTEMA) REFERENCES TB_SISTEMA(PK_SISTEMA)
);

-- AUTH.dbo.TB_USUARIO_PERFIL definition

-- Drop table
-- DROP TABLE AUTH.dbo.TB_USUARIO_PERFIL;

CREATE TABLE TB_USUARIO_PERFIL (
                                   PK_USUARIO bigint NOT NULL,
                                   PK_PERFIL_ACESSO bigint NOT NULL,
                                   CONSTRAINT PK_TB_USUARIO_PERFIL PRIMARY KEY (PK_USUARIO, PK_PERFIL_ACESSO),
                                   CONSTRAINT TB_USUARIO_PERFIL_TB_PERFIL_ACESSO_FK FOREIGN KEY (PK_PERFIL_ACESSO) REFERENCES TB_PERFIL_ACESSO(PK_PERFIL_ACESSO),
                                   CONSTRAINT TB_USUARIO_PERFIL_USUARIO_FK FOREIGN KEY (PK_USUARIO) REFERENCES TB_USUARIO(PK_USUARIO)
);

-- AUTH.dbo.TB_USUARIO_SISTEMA definition

-- Drop table
-- DROP TABLE AUTH.dbo.TB_USUARIO_SISTEMA;

CREATE TABLE TB_USUARIO_SISTEMA (
                                    PK_USUARIO bigint,
                                    PK_SISTEMA bigint,
                                    CONSTRAINT TB_USUARIO_SISTEMA_TB_SISTEMA_FK FOREIGN KEY (PK_SISTEMA) REFERENCES TB_SISTEMA(PK_SISTEMA),
                                    CONSTRAINT TB_USUARIO_SISTEMA_USUARIO_FK FOREIGN KEY (PK_USUARIO) REFERENCES TB_USUARIO(PK_USUARIO)
);

```
Esse script serve para criar o database e a tabela exemplo para os cruds.


Suba a aplicação e teste o swagger na url http://localhost:8080/swagger-ui/