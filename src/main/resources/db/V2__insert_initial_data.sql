INSERT INTO barbeiros (nome, email, senha, ativo, criado_em)
VALUES
    ('João Barbeiro', 'joao@barbearia.com', '123456', TRUE, NOW());

INSERT INTO tipos_corte (nome, descricao, duracao_em_minutos, preco, ativo)
VALUES
    ('Corte simples', 'Corte masculino tradicional', 30, 35.00, TRUE),
    ('Barba', 'Aparar e desenhar barba', 30, 25.00, TRUE),
    ('Corte + barba', 'Corte masculino com barba', 60, 55.00, TRUE);