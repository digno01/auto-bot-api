
-- Criar tabela de robô investidor
CREATE TABLE tb_robo_investidor
(
    pk_robo_investidor  serial4        NOT NULL,
    ds_nome             varchar(50)    NOT NULL,
    nu_dias_periodo     int4           NOT NULL,
    pc_rendimento_min   numeric(5, 2)  NOT NULL,
    pc_rendimento_max   numeric(5, 2)  NOT NULL,
    vl_investimento_min numeric(15, 2) NOT NULL,
    vl_investimento_max numeric(15, 2) NOT NULL,
    is_active           bool           NOT NULL DEFAULT true,
    dt_created_at       timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT tb_robo_investidor_pkey PRIMARY KEY (pk_robo_investidor)
);


-- DROP TABLE tb_nivel_indicacao;

CREATE TABLE tb_nivel_indicacao
(
    pk_nivel_indicacao       serial4       NOT NULL,
    nu_nivel                 int2          NOT NULL,
    nu_percentual_rendimento numeric(5, 2) NOT NULL,
    ds_descricao             varchar(200)  NOT NULL,
    is_active                bool          NOT NULL DEFAULT true,
    dt_created_at            timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT tb_nivel_indicacao_pkey PRIMARY KEY (pk_nivel_indicacao)
);


-- public.tb_perfil_acesso definition

-- Drop table

-- DROP TABLE tb_perfil_acesso;

CREATE TABLE tb_perfil_acesso
(
    pk_perfil_acesso serial4      NOT NULL,
    no_perfil_acesso varchar(150) NOT NULL,
    ds_perfil        varchar(300) NOT NULL,
    CONSTRAINT tb_perfil_acesso_pkey PRIMARY KEY (pk_perfil_acesso)
);


-- public.tb_permissao_acesso definition

-- Drop table

-- DROP TABLE tb_permissao_acesso;

CREATE TABLE tb_permissao_acesso
(
    pk_permissao_acesso serial4      NOT NULL,
    ds_permissao_acesso varchar(100) NOT NULL,
    CONSTRAINT tb_permissao_acesso_pkey PRIMARY KEY (pk_permissao_acesso)
);


-- public.tb_usuario definition

-- Drop table

-- DROP TABLE tb_usuario;

CREATE TABLE public.tb_usuario (
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
                                   vl_saldo_disponivel numeric(15, 8) NOT NULL DEFAULT 0,
                                   vl_saldo_investido numeric(15, 8) NOT NULL DEFAULT 0,
                                   vl_saldo_rendimentos numeric(15, 8) NOT NULL DEFAULT 0,
                                   ds_codigo_indicacao varchar(8) NOT NULL,
                                   CONSTRAINT tb_usuario_codigo_indicacao_unique UNIQUE (ds_codigo_indicacao),
                                   CONSTRAINT tb_usuario_cpf_unique UNIQUE (nu_cpf),
                                   CONSTRAINT tb_usuario_email_unique UNIQUE (ds_email),
                                   CONSTRAINT tb_usuario_pkey PRIMARY KEY (pk_usuario)
);
CREATE INDEX idx_usuario_codigo_indicacao ON public.tb_usuario USING btree (ds_codigo_indicacao);


CREATE TABLE TB_INVESTIMENTO (
                                        PK_INVESTIMENTO SERIAL4 NOT NULL,
                                        PK_USUARIO INT8 NOT NULL,
                                        PK_ROBO_INVESTIDOR INT8 NULL,
                                        VL_INICIAL NUMERIC(15, 2) NOT NULL,
                                        SALDO_ATUAL NUMERIC(15, 2) NULL,
                                        DT_INVESTIMENTO TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        DT_LIBERACAO TIMESTAMP NULL,
                                        ST_INVESTIMENTO BPCHAR(1) NOT NULL DEFAULT 'A'::BPCHAR,
                                        IS_LIBERADO_SAQUE BOOL NULL,
                                        IS_ULTIMO_RENDIMENTO_LOSS BOOL NULL,
                                        DT_CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        DT_UPDATED_AT TIMESTAMP NULL,

                                        CONSTRAINT TB_INVESTIMENTO_PKEY PRIMARY KEY (PK_INVESTIMENTO),
                                        CONSTRAINT TB_INVESTIMENTO_ROBO_FK FOREIGN KEY (PK_ROBO_INVESTIDOR) REFERENCES PUBLIC.TB_ROBO_INVESTIDOR(PK_ROBO_INVESTIDOR),
                                        CONSTRAINT TB_INVESTIMENTO_USUARIO_FK FOREIGN KEY (PK_USUARIO) REFERENCES PUBLIC.TB_USUARIO(PK_USUARIO)
);
CREATE INDEX IDX_INVESTIMENTO_ROBO ON TB_INVESTIMENTO USING BTREE (PK_ROBO_INVESTIDOR);
CREATE INDEX IDX_INVESTIMENTO_STATUS ON TB_INVESTIMENTO USING BTREE (ST_INVESTIMENTO);





-- public.tb_contato definition

-- Drop table

-- DROP TABLE tb_contato;

CREATE TABLE tb_contato
(
    pk_contato    serial4     NOT NULL,
    pk_usuario    int8        NOT NULL,
    nu_ddd        int4        NOT NULL,
    nu_telefone   varchar(14) NOT NULL,
    tp_contato    bpchar(1) NOT NULL DEFAULT '1'::bpchar,
    dt_created_at timestamp   NOT NULL,
    dt_updated_at timestamp NULL,
    nu_deleted    bool        NOT NULL DEFAULT false,
    CONSTRAINT tb_contato_pkey PRIMARY KEY (pk_contato),
    CONSTRAINT contato_usuario_fk FOREIGN KEY (pk_usuario) REFERENCES tb_usuario (pk_usuario)
);


-- public.tb_deposito definition

-- Drop table

-- DROP TABLE tb_deposito;

CREATE TABLE tb_deposito
(
    pk_deposito    serial4        NOT NULL,
    pk_usuario     int8           NOT NULL,
    vl_deposito    numeric(15, 2) NOT NULL,
    dt_deposito    timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    st_deposito    bpchar(1) NOT NULL DEFAULT 'P'::bpchar,
    dt_aprovacao   timestamp NULL,
    CONSTRAINT tb_deposito_pkey PRIMARY KEY (pk_deposito),
    CONSTRAINT tb_deposito_usuario_fk FOREIGN KEY (pk_usuario) REFERENCES tb_usuario (pk_usuario)
);


-- public.tb_indicacao definition

-- Drop table

-- DROP TABLE tb_indicacao;

CREATE TABLE tb_indicacao
(
    pk_indicacao         serial4   NOT NULL,
    pk_usuario           int8      NOT NULL,
    pk_usuario_indicador int8 NULL,
    nu_nivel             int2      NOT NULL,
    dt_created_at        timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active            bool      NOT NULL DEFAULT true,
    CONSTRAINT tb_indicacao_pkey PRIMARY KEY (pk_indicacao),
    CONSTRAINT tb_indicacao_usuario_fk FOREIGN KEY (pk_usuario) REFERENCES tb_usuario (pk_usuario),
    CONSTRAINT tb_indicacao_usuario_indicador_fk FOREIGN KEY (pk_usuario_indicador) REFERENCES tb_usuario (pk_usuario)
);


-- public.tb_investimento definition

-- Drop table





-- public.tb_rendimento definition

-- Drop table

-- DROP TABLE tb_rendimento;

CREATE TABLE tb_rendimento
(
    pk_rendimento   serial4        NOT NULL,
    pk_investimento int8 NULL,
    pk_usuario      int8           NOT NULL,
    vl_rendimento   numeric(15, 2) NOT NULL,
    dt_rendimento   timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tp_rendimento   bpchar(1) NOT NULL,
    pc_rendimento   numeric(15, 2) NULL,
    CONSTRAINT tb_rendimento_pkey PRIMARY KEY (pk_rendimento),
    CONSTRAINT tb_rendimento_investimento_fk FOREIGN KEY (pk_investimento) REFERENCES tb_investimento (pk_investimento),
    CONSTRAINT tb_rendimento_usuario_fk FOREIGN KEY (pk_usuario) REFERENCES tb_usuario (pk_usuario)
);


-- public.tb_saque definition

-- Drop table

-- DROP TABLE tb_saque;

CREATE TABLE tb_saque
(
    pk_saque           serial4        NOT NULL,
    pk_usuario         int8           NOT NULL,
    vl_saque           numeric(15, 2) NOT NULL,
    dt_solicitacao     timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dt_processamento   timestamp NULL,
    st_saque           bpchar(1) NOT NULL DEFAULT 'P'::bpchar,
    ds_dados_bancarios text           NOT NULL,
    CONSTRAINT tb_saque_pkey PRIMARY KEY (pk_saque),
    CONSTRAINT tb_saque_usuario_fk FOREIGN KEY (pk_usuario) REFERENCES tb_usuario (pk_usuario)
);


-- public.tb_usuario_perfil definition

-- Drop table

-- DROP TABLE tb_usuario_perfil;

CREATE TABLE tb_usuario_perfil
(
    pk_usuario       int8 NOT NULL,
    pk_perfil_acesso int8 NOT NULL,
    CONSTRAINT pk_tb_usuario_perfil PRIMARY KEY (pk_usuario, pk_perfil_acesso),
    CONSTRAINT tb_usuario_perfil_tb_perfil_acesso_fk FOREIGN KEY (pk_perfil_acesso) REFERENCES tb_perfil_acesso (pk_perfil_acesso),
    CONSTRAINT tb_usuario_perfil_usuario_fk FOREIGN KEY (pk_usuario) REFERENCES tb_usuario (pk_usuario)
);


-- Inserir níveis de indicação padrão
INSERT INTO tb_nivel_indicacao (nu_nivel, nu_percentual_rendimento, ds_descricao)
VALUES (1, 10.00, 'Indicação Direta - Nível 1'),
       (2, 5.00, 'Indicação Indireta - Nível 2'),
       (3, 2.00, 'Indicação Indireta - Nível 3');

-- Inserir perfil de acesso padrão
INSERT INTO tb_perfil_acesso (no_perfil_acesso, ds_perfil)
VALUES ('ADMIN', 'Administrador do Sistema'),
       ('USER', 'Usuário Comum');




CREATE INDEX idx_rendimento_data ON tb_rendimento (dt_rendimento);

-- Criar view para análise de rendimentos por robô
CREATE VIEW vw_rendimentos_por_robo AS
SELECT ri.ds_nome                        as nome_robo,
       COUNT(DISTINCT i.pk_investimento) as total_investimentos,
       SUM(r.vl_rendimento)              as total_rendimentos,
       AVG(r.pc_rendimento)              as media_rendimento,
       MIN(r.dt_rendimento)              as primeiro_rendimento,
       MAX(r.dt_rendimento)              as ultimo_rendimento
FROM tb_robo_investidor ri
         LEFT JOIN tb_investimento i ON i.pk_robo_investidor = ri.pk_robo_investidor
         LEFT JOIN tb_rendimento r ON r.pk_investimento = i.pk_investimento
GROUP BY ri.pk_robo_investidor, ri.ds_nome;

-- Criar função para validar limites de investimento
CREATE
OR REPLACE FUNCTION fn_validar_limites_investimento()
RETURNS TRIGGER AS $$
BEGIN
    IF
NEW.vl_inicial < (
        SELECT vl_investimento_min
        FROM tb_robo_investidor
        WHERE pk_robo_investidor = NEW.pk_robo_investidor
    ) THEN
        RAISE EXCEPTION 'Valor de investimento abaixo do mínimo permitido para este robô';
END IF;

    IF
NEW.vl_inicial > (
        SELECT vl_investimento_max
        FROM tb_robo_investidor
        WHERE pk_robo_investidor = NEW.pk_robo_investidor
    ) THEN
        RAISE EXCEPTION 'Valor de investimento acima do máximo permitido para este robô';
END IF;

RETURN NEW;
END;
$$
LANGUAGE plpgsql;

-- Criar trigger para validação de limites
CREATE TRIGGER tg_validar_limites_investimento
    BEFORE INSERT OR
UPDATE ON tb_investimento
    FOR EACH ROW
    EXECUTE FUNCTION fn_validar_limites_investimento();

-- Criar função para calcular rendimento ajustado
CREATE
OR REPLACE FUNCTION fn_calcular_rendimento_ajustado(
    p_valor_base numeric,
    p_ultimo_loss boolean,
    p_valor_ultimo_rendimento numeric
) RETURNS numeric AS $$
BEGIN
    IF
p_ultimo_loss THEN
        RETURN p_valor_base + (ABS(p_valor_ultimo_rendimento) / 2);
ELSE
        RETURN p_valor_base;
END IF;
END;
$$
LANGUAGE plpgsql;



-- Criar trigger para atualização automática
CREATE
OR REPLACE FUNCTION fn_atualizar_data_modificacao()
RETURNS TRIGGER AS $$
BEGIN
    NEW.dt_updated_at
= CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tg_atualizar_data_modificacao
    BEFORE UPDATE
    ON tb_investimento
    FOR EACH ROW
    EXECUTE FUNCTION fn_atualizar_data_modificacao();


-- Modificar tabela de depósito para incluir robô
ALTER TABLE tb_deposito
    ADD COLUMN pk_robo_investidor int8,
ADD CONSTRAINT tb_deposito_robo_fk
    FOREIGN KEY (pk_robo_investidor)
    REFERENCES tb_robo_investidor(pk_robo_investidor);

-- Criar tabela de histórico de troca de robô
CREATE TABLE tb_historico_troca_robo (
                                         pk_historico_troca_robo serial4 NOT NULL,
                                         pk_usuario int8 NOT NULL,
                                         pk_robo_origem int8,
                                         pk_robo_destino int8 NOT NULL,
                                         vl_saldo_transferido numeric(15,2) NOT NULL,
                                         vl_rendimentos_incorporados numeric(15,2) NOT NULL DEFAULT 0,
                                         dt_troca timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         ds_observacao text,
                                         CONSTRAINT tb_historico_troca_robo_pkey PRIMARY KEY (pk_historico_troca_robo),
                                         CONSTRAINT tb_historico_troca_robo_usuario_fk FOREIGN KEY (pk_usuario) REFERENCES tb_usuario(pk_usuario),
                                         CONSTRAINT tb_historico_troca_robo_origem_fk FOREIGN KEY (pk_robo_origem) REFERENCES tb_robo_investidor(pk_robo_investidor),
                                         CONSTRAINT tb_historico_troca_robo_destino_fk FOREIGN KEY (pk_robo_destino) REFERENCES tb_robo_investidor(pk_robo_investidor)
);



ALTER TABLE tb_deposito
ALTER COLUMN st_deposito TYPE char(1);

-- Se precisar converter dados existentes
UPDATE tb_deposito SET st_deposito = 'P' WHERE st_deposito = 'PENDENTE';
UPDATE tb_deposito SET st_deposito = 'A' WHERE st_deposito = 'APROVADO';
UPDATE tb_deposito SET st_deposito = 'R' WHERE st_deposito = 'REJEITADO';
UPDATE tb_deposito SET st_deposito = 'C' WHERE st_deposito = 'CANCELADO';



-- Converter dados existentes
UPDATE tb_investimento SET st_investimento = 'A' WHERE st_investimento = 'ATIVO';
UPDATE tb_investimento SET st_investimento = 'F' WHERE st_investimento = 'FINALIZADO';
UPDATE tb_investimento SET st_investimento = 'C' WHERE st_investimento = 'CANCELADO';



ALTER TABLE tb_saque
    ADD COLUMN pk_investimento bigint,
ADD CONSTRAINT fk_saque_investimento
    FOREIGN KEY (pk_investimento)
    REFERENCES tb_investimento(pk_investimento);

ALTER TABLE tb_saque
ALTER COLUMN st_saque TYPE char(1);


-- Alterar a coluna para aceitar o tipo enum
ALTER TABLE tb_rendimento
ALTER COLUMN tp_rendimento TYPE char(1);

-- Converter dados existentes
UPDATE tb_rendimento SET tp_rendimento = 'I' WHERE tp_rendimento = 'INVESTIMENTO';
UPDATE tb_rendimento SET tp_rendimento = '1' WHERE tp_rendimento = 'N1';
UPDATE tb_rendimento SET tp_rendimento = '2' WHERE tp_rendimento = 'N2';
UPDATE tb_rendimento SET tp_rendimento = '3' WHERE tp_rendimento = 'N3';


ALTER TABLE tb_rendimento
    ADD COLUMN tp_resultado char(1);

-- Atualizar registros existentes
UPDATE tb_rendimento SET tp_resultado = 'L' WHERE vl_rendimento >= 0;
UPDATE tb_rendimento SET tp_resultado = 'P' WHERE vl_rendimento < 0;




-----

-- Criar tabela TB_OPERACAO_CRIPTO
CREATE TABLE TB_OPERACAO_CRIPTO (
                                    PK_OPERACAO_CRIPTO BIGSERIAL PRIMARY KEY,
                                    PK_RENDIMENTO BIGINT NOT NULL,
                                    DS_MOEDA VARCHAR(10) NOT NULL,
                                    VL_COMPRA NUMERIC(20,8) NOT NULL,
                                    VL_VENDA NUMERIC(20,8) NOT NULL,
                                    QT_MOEDA NUMERIC(20,8) NOT NULL,
                                    DT_COMPRA TIMESTAMP NOT NULL,
                                    DT_VENDA TIMESTAMP NOT NULL,
                                    PC_VARIACAO NUMERIC(10,2) NOT NULL,
                                    VL_LUCRO NUMERIC(20,8) NOT NULL,
                                    DS_URL_IMAGEM VARCHAR(500),

                                    CONSTRAINT fk_operacao_cripto_rendimento
                                        FOREIGN KEY (PK_RENDIMENTO)
                                            REFERENCES TB_RENDIMENTO (PK_RENDIMENTO)
);


CREATE INDEX idx_operacao_cripto_rendimento ON TB_OPERACAO_CRIPTO(PK_RENDIMENTO);
CREATE INDEX idx_operacao_cripto_moeda ON TB_OPERACAO_CRIPTO(DS_MOEDA);
CREATE INDEX idx_operacao_cripto_data_compra ON TB_OPERACAO_CRIPTO(DT_COMPRA);

ALTER TABLE tb_investimento ADD id_transaction_gateway numeric(15) NOT NULL DEFAULT 0;
ALTER TABLE public.tb_investimento ADD url_qr_code varchar(200) NULL;
CREATE INDEX idx_transaction_gateway ON tb_investimento(id_transaction_gateway);
ALTER TABLE public.tb_usuario ADD avatar varchar(50) NULL;

ALTER TABLE public.tb_saque ADD id_saque_gateway int8 NOT NULL;
ALTER TABLE public.tb_saque ADD end_to_end_id varchar(50) NOT NULL;
ALTER TABLE public.tb_investimento ALTER COLUMN st_investimento TYPE bpchar(2) USING st_investimento::bpchar;


-- Índice para dt_investimento
CREATE INDEX idx_investimento_dt_investimento ON public.tb_investimento (dt_investimento);

-- Índice para dt_liberacao
CREATE INDEX idx_investimento_dt_liberacao ON public.tb_investimento (dt_liberacao);

-- Índice composto para ambas as datas
CREATE INDEX idx_investimento_datas ON public.tb_investimento (dt_investimento, dt_liberacao);

ALTER TABLE public.tb_saque DROP COLUMN ds_dados_bancarios;
ALTER TABLE public.tb_saque ALTER COLUMN id_saque_gateway DROP NOT NULL;
ALTER TABLE public.tb_saque ALTER COLUMN end_to_end_id DROP NOT NULL;



CREATE TABLE TB_NOTIFICACAO_USUARIO (
                                        PK_NOTIFICACAO SERIAL4 NOT NULL,
                                        PK_USUARIO INT8 NOT NULL,
                                        DS_TITULO VARCHAR(100) NOT NULL,
                                        DS_MENSAGEM VARCHAR(500) NOT NULL,
                                        VL_REFERENCIA NUMERIC(15, 2) NULL,
                                        TP_NOTIFICACAO VARCHAR(30) NOT NULL, -- Em vez de enum, usando VARCHAR
                                        ST_LIDA BOOL NOT NULL DEFAULT FALSE,
                                        DT_CRIACAO TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        DT_LEITURA TIMESTAMP NULL,
                                        DT_CREATED_AT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        DT_UPDATED_AT TIMESTAMP NULL,

                                        CONSTRAINT TB_NOTIFICACAO_USUARIO_PKEY PRIMARY KEY (PK_NOTIFICACAO),
                                        CONSTRAINT TB_NOTIFICACAO_USUARIO_FK FOREIGN KEY (PK_USUARIO)
                                            REFERENCES PUBLIC.TB_USUARIO(PK_USUARIO)
);

-- Índices para melhorar a performance
CREATE INDEX IDX_NOTIFICACAO_USUARIO ON TB_NOTIFICACAO_USUARIO USING BTREE (PK_USUARIO);
CREATE INDEX IDX_NOTIFICACAO_TIPO ON TB_NOTIFICACAO_USUARIO USING BTREE (TP_NOTIFICACAO);
CREATE INDEX IDX_NOTIFICACAO_LIDA ON TB_NOTIFICACAO_USUARIO USING BTREE (ST_LIDA);
CREATE INDEX IDX_NOTIFICACAO_DATA ON TB_NOTIFICACAO_USUARIO USING BTREE (DT_CRIACAO);

ALTER TABLE public.tb_robo_investidor ADD vl_ganho numeric(15, 2) NULL DEFAULT 0;
ALTER TABLE public.tb_usuario ADD nivel_conta int NOT NULL DEFAULT 0;
ALTER TABLE public.tb_usuario ADD qtd_indicacoes_diretas int NOT NULL DEFAULT 0;
ALTER TABLE public.tb_usuario ADD qtd_indicacoes_indiretas int NOT NULL DEFAULT 0;
ALTER TABLE public.tb_usuario RENAME COLUMN vl_saldo_disponivel TO vl_comissao;
ALTER TABLE public.tb_robo_investidor ADD descricao varchar(500) NULL;
ALTER TABLE public.tb_robo_investidor ADD nu_nivel int NULL;
ALTER TABLE public.tb_robo_investidor ADD apelido varchar(50) NULL;
ALTER TABLE public.tb_robo_investidor ADD "avatar" varchar(50) NULL;
ALTER TABLE public.tb_rendimento ADD vl_acumulado numeric(15, 2) NULL;
ALTER TABLE public.tb_investimento ADD vl_comissao_deposito numeric(15, 2) NULL;
ALTER TABLE public.tb_investimento ADD vl_efetuado_pix numeric(15, 2) NULL;



INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(2, 'ROBÔ 3 DIAS', 3, 3.40, 3.40, 60.00, 300.00, true, '2024-11-23 18:04:32.358', 0.00, 'Operações de curto prazo com estratégia balanceada. Perfeito para quem busca resultados consistentes em poucos dias.', 1, 'Michael Berry', 'robo02.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(3, 'ROBÔ 7 DIAS', 7, 3.50, 3.50, 20.00, 100.00, true, '2024-11-23 18:04:32.358', 0.00, 'Estratégia semanal com análise técnica avançada. Combina diferentes mercados para maximizar resultados.', 1, 'Warren Buffet', 'robo03.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(4, 'ROBÔ 15 DIAS', 15, 3.70, 3.70, 20.00, 200.00, true, '2024-11-23 18:04:32.358', 0.00, 'Trading quinzenal com múltiplas estratégias. Utiliza inteligência artificial para identificar as melhores oportunidades.', 1, 'George Soros', 'robo04.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(5, 'ROBÔ 30 DIAS', 30, 4.00, 4.00, 20.00, 200.00, true, '2024-11-23 18:04:32.358', 0.00, 'Estratégia mensal com análise fundamentalista e técnica. Foco em operações de maior duração e retornos expressivos.', 1, 'Peter Lynch', 'robo05.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(6, 'ROBÔ 90 DIAS', 90, 4.15, 4.15, 50.00, 500.00, true, '2024-11-23 18:04:32.358', 0.00, 'Trading trimestral com estratégias avançadas de hedge. Ideal para investidores que buscam maior previsibilidade.', 1, 'Ray Dalio', 'robo06.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(7, 'ROBÔ 180 DIAS', 180, 4.25, 4.25, 100.00, 5000.00, true, '2024-11-23 18:04:32.358', 0.00, 'Estratégia semestral de alto rendimento. Combina múltiplos mercados e técnicas avançadas para maximizar resultados.', 1, 'Carl Icahn', 'robo07.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(1, 'ROBÔ 1 DIA', 1, 3.00, 3.00, 5.00, 10.00, true, '2024-11-23 18:04:32.358', 0.00, 'Estratégia day trade para resultados rápidos. Ideal para iniciantes que buscam retornos diários com risco controlado.', 1, 'Jonh Paulson 1', 'robo01.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(10, 'ROBÔ  NIVEL_2 3 DIAS', 3, 3.40, 3.40, 10.00, 50.00, true, '2024-11-23 18:04:32.358', 0.00, 'Operações de curto prazo com estratégia balanceada. Perfeito para quem busca resultados consistentes em poucos dias.', 3, 'Michael Berry', '6_robo_lvl_03.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(11, 'ROBÔ  NIVEL_2 7 DIAS', 7, 3.50, 3.50, 20.00, 100.00, true, '2024-11-23 18:04:32.358', 0.00, 'Estratégia semanal com análise técnica avançada. Combina diferentes mercados para maximizar resultados.', 2, 'Warren Buffet', '2_robo_lvl_02.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(12, 'ROBÔ NIVEL_2 15 DIAS', 15, 3.70, 3.70, 20.00, 200.00, true, '2024-11-23 18:04:32.358', 0.00, 'Trading quinzenal com múltiplas estratégias. Utiliza inteligência artificial para identificar as melhores oportunidades.', 2, 'George Soros', '3_robo_lvl_02.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(13, 'ROBÔ NIVEL_2 30 DIAS', 30, 4.00, 4.00, 20.00, 200.00, true, '2024-11-23 18:04:32.358', 0.00, 'Estratégia mensal com análise fundamentalista e técnica. Foco em operações de maior duração e retornos expressivos.', 2, 'Peter Lynch', '4_robo_lvl_02.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(14, 'ROBÔ NIVEL_2 90 DIAS', 90, 4.15, 4.15, 50.00, 500.00, true, '2024-11-23 18:04:32.358', 0.00, 'Trading trimestral com estratégias avançadas de hedge. Ideal para investidores que buscam maior previsibilidade.', 2, 'Ray Dalio', '5_robo_lvl_02.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(15, 'ROBÔ NIVEL_2 180 DIAS', 180, 4.25, 4.25, 100.00, 5000.00, true, '2024-11-23 18:04:32.358', 0.00, 'Estratégia semestral de alto rendimento. Combina múltiplos mercados e técnicas avançadas para maximizar resultados.', 2, 'Carl Icahn', '6_robo_lvl_02.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(16, 'ROBÔ NIVEL_3 3 DIAS', 3, 3.40, 3.40, 10.00, 50.00, true, '2024-11-23 18:04:32.358', 0.00, 'Operações de curto prazo com estratégia balanceada. Perfeito para quem busca resultados consistentes em poucos dias.', 3, 'Michael Berry', '1_robo_lvl_03.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(17, 'ROBÔ NIVEL_3  7 DIAS', 7, 3.50, 3.50, 20.00, 100.00, true, '2024-11-23 18:04:32.358', 0.00, 'Estratégia semanal com análise técnica avançada. Combina diferentes mercados para maximizar resultados.', 3, 'Warren Buffet', '2_robo_lvl_03.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(18, 'ROBÔ NIVEL_3  15 DIAS', 15, 3.70, 3.70, 20.00, 200.00, true, '2024-11-23 18:04:32.358', 0.00, 'Trading quinzenal com múltiplas estratégias. Utiliza inteligência artificial para identificar as melhores oportunidades.', 3, 'George Soros', '2_robo_lvl_03.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(19, 'ROBÔ NIVEL_3 30 DIAS', 30, 4.00, 4.00, 20.00, 200.00, true, '2024-11-23 18:04:32.358', 0.00, 'Estratégia mensal com análise fundamentalista e técnica. Foco em operações de maior duração e retornos expressivos.', 3, 'Peter Lynch', '3_robo_lvl_03.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(20, 'ROBÔ NIVEL_3  90 DIAS', 90, 4.15, 4.15, 50.00, 500.00, true, '2024-11-23 18:04:32.358', 0.00, 'Trading trimestral com estratégias avançadas de hedge. Ideal para investidores que buscam maior previsibilidade.', 3, 'Ray Dalio', '4_robo_lvl_03.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(21, 'ROBÔ NIVEL_3  180 DIAS', 180, 4.25, 4.25, 100.00, 5000.00, true, '2024-11-23 18:04:32.358', 0.00, 'Estratégia semestral de alto rendimento. Combina múltiplos mercados e técnicas avançadas para maximizar resultados.', 3, 'Carl Icahn', '5_robo_lvl_03.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(22, 'ROBÔ NIVEL_4 3 DIAS', 3, 3.40, 3.40, 10.00, 50.00, true, '2024-11-23 18:04:32.358', 0.00, 'Operações de curto prazo com estratégia balanceada. Perfeito para quem busca resultados consistentes em poucos dias.', 4, 'Michael Berry', '1_robo_lvl_04.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(23, 'ROBÔ NIVEL_4 7 DIAS', 7, 3.50, 3.50, 20.00, 100.00, true, '2024-11-23 18:04:32.358', 0.00, 'Estratégia semanal com análise técnica avançada. Combina diferentes mercados para maximizar resultados.', 4, 'Warren Buffet', '2_robo_lvl_04.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(24, 'ROBÔ NIVEL_4 15 DIAS', 15, 3.70, 3.70, 20.00, 200.00, true, '2024-11-23 18:04:32.358', 0.00, 'Trading quinzenal com múltiplas estratégias. Utiliza inteligência artificial para identificar as melhores oportunidades.', 4, 'George Soros', '3_robo_lvl_04.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(25, 'ROBÔ NIVEL_4 30 DIAS', 30, 4.00, 4.00, 20.00, 200.00, true, '2024-11-23 18:04:32.358', 0.00, 'Estratégia mensal com análise fundamentalista e técnica. Foco em operações de maior duração e retornos expressivos.', 4, 'Peter Lynch', '4_robo_lvl_04.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(26, 'ROBÔ NIVEL_4 90 DIAS', 90, 4.15, 4.15, 50.00, 500.00, true, '2024-11-23 18:04:32.358', 0.00, 'Trading trimestral com estratégias avançadas de hedge. Ideal para investidores que buscam maior previsibilidade.', 4, 'Ray Dalio', '5_robo_lvl_04.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(27, 'ROBÔ NIVEL_4 180 DIAS', 180, 4.25, 4.25, 100.00, 5000.00, true, '2024-11-23 18:04:32.358', 0.00, 'Estratégia semestral de alto rendimento. Combina múltiplos mercados e técnicas avançadas para maximizar resultados.', 4, 'Carl Icahn', '6_robo_lvl_04.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(28, 'ROBÔ NIVEL_5 3 DIAS', 3, 3.40, 3.40, 10.00, 50.00, true, '2024-11-23 18:04:32.358', 0.00, 'Operações de curto prazo com estratégia balanceada. Perfeito para quem busca resultados consistentes em poucos dias.', 4, 'Michael Berry', '1_robo_lvl_05.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(29, 'ROBÔ NIVEL_5 7 DIAS', 7, 3.50, 3.50, 20.00, 100.00, true, '2024-11-23 18:04:32.358', 0.00, 'Estratégia semanal com análise técnica avançada. Combina diferentes mercados para maximizar resultados.', 4, 'Warren Buffet', '2_robo_lvl_05.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(30, 'ROBÔ NIVEL_5 15 DIAS', 15, 3.70, 3.70, 20.00, 200.00, true, '2024-11-23 18:04:32.358', 0.00, 'Trading quinzenal com múltiplas estratégias. Utiliza inteligência artificial para identificar as melhores oportunidades.', 4, 'George Soros', '3_robo_lvl_05.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(31, 'ROBÔ NIVEL_5 30 DIAS', 30, 4.00, 4.00, 20.00, 200.00, true, '2024-11-23 18:04:32.358', 0.00, 'Estratégia mensal com análise fundamentalista e técnica. Foco em operações de maior duração e retornos expressivos.', 4, 'Peter Lynch', '4_robo_lvl_5.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(32, 'ROBÔ NIVEL_5 90 DIAS', 90, 4.15, 4.15, 50.00, 500.00, true, '2024-11-23 18:04:32.358', 0.00, 'Trading trimestral com estratégias avançadas de hedge. Ideal para investidores que buscam maior previsibilidade.', 4, 'Ray Dalio', '5_robo_lvl_05.jpg');
INSERT INTO tb_robo_investidor (pk_robo_investidor, ds_nome, nu_dias_periodo, pc_rendimento_min, pc_rendimento_max, vl_investimento_min, vl_investimento_max, is_active, dt_created_at, vl_ganho, descricao, nu_nivel, apelido, avatar) VALUES(33, 'ROBÔ NIVEL_5 180 DIAS', 180, 4.25, 4.25, 100.00, 5000.00, true, '2024-11-23 18:04:32.358', 0.00, 'Estratégia semestral de alto rendimento. Combina múltiplos mercados e técnicas avançadas para maximizar resultados.', 4, 'Carl Icahn', '6_robo_lvl_05.jpg');
