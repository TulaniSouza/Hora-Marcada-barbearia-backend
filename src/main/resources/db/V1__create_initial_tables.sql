CREATE TABLE barbeiros (
                           id BIGSERIAL PRIMARY KEY,
                           nome VARCHAR(255) NOT NULL,
                           email VARCHAR(255) NOT NULL UNIQUE,
                           senha VARCHAR(255) NOT NULL,
                           ativo BOOLEAN NOT NULL DEFAULT TRUE,
                           criado_em TIMESTAMP NOT NULL
);

CREATE TABLE tipos_corte (
                             id BIGSERIAL PRIMARY KEY,
                             nome VARCHAR(255) NOT NULL,
                             descricao VARCHAR(500),
                             duracao_em_minutos INTEGER NOT NULL,
                             preco NUMERIC(10, 2) NOT NULL,
                             ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE agendamentos (
                              id BIGSERIAL PRIMARY KEY,
                              nome_cliente VARCHAR(255) NOT NULL,
                              telefone_cliente VARCHAR(30),
                              data_hora_inicio TIMESTAMP NOT NULL,
                              data_hora_fim TIMESTAMP NOT NULL,
                              status VARCHAR(30) NOT NULL,
                              barbeiro_id BIGINT NOT NULL,
                              tipo_corte_id BIGINT NOT NULL,
                              criado_em TIMESTAMP NOT NULL,

                              CONSTRAINT fk_agendamento_barbeiro
                                  FOREIGN KEY (barbeiro_id)
                                      REFERENCES barbeiros(id),

                              CONSTRAINT fk_agendamento_tipo_corte
                                  FOREIGN KEY (tipo_corte_id)
                                      REFERENCES tipos_corte(id)
);

CREATE INDEX idx_agendamentos_barbeiro_inicio_fim
    ON agendamentos (barbeiro_id, data_hora_inicio, data_hora_fim);

CREATE INDEX idx_agendamentos_status
    ON agendamentos (status);