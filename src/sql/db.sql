-- public.tb_nivel_indicacao definition

-- Drop table

-- DROP TABLE tb_nivel_indicacao;

CREATE TABLE tb_nivel_indicacao (
                                    pk_nivel_indicacao serial4 NOT NULL,
                                    nu_nivel int2 NOT NULL,
                                    nu_percentual_rendimento numeric(5, 2) NOT NULL,
                                    ds_descricao varchar(200) NOT NULL,
                                    is_active bool NOT NULL DEFAULT true,
                                    dt_created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    CONSTRAINT tb_nivel_indicacao_pkey PRIMARY KEY (pk_nivel_indicacao)
);


-- public.tb_perfil_acesso definition

-- Drop table

-- DROP TABLE tb_perfil_acesso;

CREATE TABLE tb_perfil_acesso (
                                  pk_perfil_acesso serial4 NOT NULL,
                                  no_perfil_acesso varchar(150) NOT NULL,
                                  ds_perfil varchar(300) NOT NULL,
                                  CONSTRAINT tb_perfil_acesso_pkey PRIMARY KEY (pk_perfil_acesso)
);


-- public.tb_permissao_acesso definition

-- Drop table

-- DROP TABLE tb_permissao_acesso;

CREATE TABLE tb_permissao_acesso (
                                     pk_permissao_acesso serial4 NOT NULL,
                                     ds_permissao_acesso varchar(100) NOT NULL,
                                     CONSTRAINT tb_permissao_acesso_pkey PRIMARY KEY (pk_permissao_acesso)
);


-- public.tb_usuario definition

-- Drop table

-- DROP TABLE tb_usuario;

CREATE TABLE tb_usuario (
                            pk_usuario serial4 NOT NULL,
                            ds_email varchar(100) NOT NULL,
                            ds_senha varchar(100) NOT NULL,
                            nu_cpf varchar(11) NOT NULL,
                            no_usuario varchar(200) NOT NULL,
                            dt_created_at timestamp NOT NULL,
                            dt_updated_at timestamp NULL,
                            nu_deleted bool NOT NULL DEFAULT false,
                            ds_token varchar(500) NULL,
                            ds_refresh_token varchar(500) NULL,
                            qt_tentativas_recuperar_senha int2 NOT NULL DEFAULT 0,
                            dt_ultima_tentativa_recuperacao timestamp NULL,
                            is_active bool NOT NULL DEFAULT false,
                            vl_saldo_disponivel numeric(15, 2) NOT NULL DEFAULT 0,
                            vl_saldo_investido numeric(15, 2) NOT NULL DEFAULT 0,
                            vl_saldo_rendimentos numeric(15, 2) NOT NULL DEFAULT 0,
                            ds_codigo_indicacao varchar(8) NOT NULL,
                            CONSTRAINT tb_usuario_pkey PRIMARY KEY (pk_usuario),
                            CONSTRAINT tb_usuario_cpf_unique UNIQUE (nu_cpf),
                            CONSTRAINT tb_usuario_email_unique UNIQUE (ds_email),
                            CONSTRAINT tb_usuario_codigo_indicacao_unique UNIQUE (ds_codigo_indicacao)
);

CREATE INDEX idx_usuario_codigo_indicacao ON tb_usuario(ds_codigo_indicacao);


-- public.tb_contato definition

-- Drop table

-- DROP TABLE tb_contato;

CREATE TABLE tb_contato (
                            pk_contato serial4 NOT NULL,
                            pk_usuario int8 NOT NULL,
                            nu_ddd int4 NOT NULL,
                            nu_telefone varchar(14) NOT NULL,
                            tp_contato bpchar(1) NOT NULL DEFAULT '1'::bpchar,
                            dt_created_at timestamp NOT NULL,
                            dt_updated_at timestamp NULL,
                            nu_deleted bool NOT NULL DEFAULT false,
                            CONSTRAINT tb_contato_pkey PRIMARY KEY (pk_contato),
                            CONSTRAINT contato_usuario_fk FOREIGN KEY (pk_usuario) REFERENCES tb_usuario(pk_usuario)
);


-- public.tb_deposito definition

-- Drop table

-- DROP TABLE tb_deposito;

CREATE TABLE tb_deposito (
                             pk_deposito serial4 NOT NULL,
                             pk_usuario int8 NOT NULL,
                             vl_deposito numeric(15, 2) NOT NULL,
                             dt_deposito timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             st_deposito bpchar(1) NOT NULL DEFAULT 'P'::bpchar,
                             ds_comprovante varchar(500) NULL,
                             dt_aprovacao timestamp NULL,
                             CONSTRAINT tb_deposito_pkey PRIMARY KEY (pk_deposito),
                             CONSTRAINT tb_deposito_usuario_fk FOREIGN KEY (pk_usuario) REFERENCES tb_usuario(pk_usuario)
);


-- public.tb_indicacao definition

-- Drop table

-- DROP TABLE tb_indicacao;

CREATE TABLE tb_indicacao (
                              pk_indicacao serial4 NOT NULL,
                              pk_usuario int8 NOT NULL,
                              pk_usuario_indicador int8 NULL,
                              nu_nivel int2 NOT NULL,
                              dt_created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              is_active bool NOT NULL DEFAULT true,
                              CONSTRAINT tb_indicacao_pkey PRIMARY KEY (pk_indicacao),
                              CONSTRAINT tb_indicacao_usuario_fk FOREIGN KEY (pk_usuario) REFERENCES tb_usuario(pk_usuario),
                              CONSTRAINT tb_indicacao_usuario_indicador_fk FOREIGN KEY (pk_usuario_indicador) REFERENCES tb_usuario(pk_usuario)
);


-- public.tb_investimento definition

-- Drop table

-- DROP TABLE tb_investimento;

CREATE TABLE tb_investimento (
                                 pk_investimento serial4 NOT NULL,
                                 pk_usuario int8 NOT NULL,
                                 vl_investido numeric(15, 2) NOT NULL,
                                 nu_percentual_rendimento_diario numeric(5, 2) NOT NULL,
                                 dt_inicio timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 dt_fim timestamp NULL,
                                 st_investimento bpchar(1) NOT NULL DEFAULT 'A'::bpchar,
                                 CONSTRAINT tb_investimento_pkey PRIMARY KEY (pk_investimento),
                                 CONSTRAINT tb_investimento_usuario_fk FOREIGN KEY (pk_usuario) REFERENCES tb_usuario(pk_usuario)
);


-- public.tb_rendimento definition

-- Drop table

-- DROP TABLE tb_rendimento;

CREATE TABLE tb_rendimento (
                               pk_rendimento serial4 NOT NULL,
                               pk_investimento int8 NULL,
                               pk_usuario int8 NOT NULL,
                               vl_rendimento numeric(15, 2) NOT NULL,
                               dt_rendimento timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               tp_rendimento bpchar(1) NOT NULL,
                               pc_rendimento numeric(15, 2) NULL,
                               CONSTRAINT tb_rendimento_pkey PRIMARY KEY (pk_rendimento),
                               CONSTRAINT tb_rendimento_investimento_fk FOREIGN KEY (pk_investimento) REFERENCES tb_investimento(pk_investimento),
                               CONSTRAINT tb_rendimento_usuario_fk FOREIGN KEY (pk_usuario) REFERENCES tb_usuario(pk_usuario)
);


-- public.tb_saque definition

-- Drop table

-- DROP TABLE tb_saque;

CREATE TABLE tb_saque (
                          pk_saque serial4 NOT NULL,
                          pk_usuario int8 NOT NULL,
                          vl_saque numeric(15, 2) NOT NULL,
                          dt_solicitacao timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          dt_processamento timestamp NULL,
                          st_saque bpchar(1) NOT NULL DEFAULT 'P'::bpchar,
                          ds_dados_bancarios text NOT NULL,
                          CONSTRAINT tb_saque_pkey PRIMARY KEY (pk_saque),
                          CONSTRAINT tb_saque_usuario_fk FOREIGN KEY (pk_usuario) REFERENCES tb_usuario(pk_usuario)
);


-- public.tb_usuario_perfil definition

-- Drop table

-- DROP TABLE tb_usuario_perfil;

CREATE TABLE tb_usuario_perfil (
                                   pk_usuario int8 NOT NULL,
                                   pk_perfil_acesso int8 NOT NULL,
                                   CONSTRAINT pk_tb_usuario_perfil PRIMARY KEY (pk_usuario, pk_perfil_acesso),
                                   CONSTRAINT tb_usuario_perfil_tb_perfil_acesso_fk FOREIGN KEY (pk_perfil_acesso) REFERENCES tb_perfil_acesso(pk_perfil_acesso),
                                   CONSTRAINT tb_usuario_perfil_usuario_fk FOREIGN KEY (pk_usuario) REFERENCES tb_usuario(pk_usuario)
);


-- Inserir níveis de indicação padrão
INSERT INTO tb_nivel_indicacao (nu_nivel, nu_percentual_rendimento, ds_descricao)
VALUES
    (1, 10.00, 'Indicação Direta - Nível 1'),
    (2, 5.00, 'Indicação Indireta - Nível 2'),
    (3, 2.00, 'Indicação Indireta - Nível 3');

-- Inserir perfil de acesso padrão
INSERT INTO tb_perfil_acesso (no_perfil_acesso, ds_perfil)
VALUES
    ('ADMIN', 'Administrador do Sistema'),
    ('USER', 'Usuário Comum');
