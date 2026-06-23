-- Seed data for OficinaMecanica
-- Use this to populate the database with sample data. Run in MySQL Workbench or mysql CLI.

USE OficinaMecanica;

-- Clientes
INSERT INTO cliente(nome, cpf, telefone) VALUES ('João Silva', '123.456.789-00', '(11) 99999-0000');
INSERT INTO cliente(nome, cpf, telefone) VALUES ('Maria Souza', '987.654.321-00', '(11) 98888-0000');

-- Veículos (associados aos clientes acima)
INSERT INTO veiculo(placa, modelo, ano, cliente_id) VALUES ('ABC-1234', 'Fiat Uno', 2010, 1);
INSERT INTO veiculo(placa, modelo, ano, cliente_id) VALUES ('DEF-5678', 'Chevrolet Celta', 2012, 2);

-- Ordens de serviço (exemplo)
INSERT INTO ordem_servico(veiculo_id, descricao, status, data_abertura, valor_hora, valor_mao_obra, valor_servicos, valor_total)
VALUES (1, 'Revisão geral e troca de óleo', 'ABERTA', NOW(), 60.00, 0.00, 0.00, 0.00);

-- Serviços para a ordem 1
INSERT INTO servico(ordem_servico_id, descricao, valor) VALUES (1, 'Troca de óleo', 120.00);
INSERT INTO servico(ordem_servico_id, descricao, valor) VALUES (1, 'Filtro de óleo', 30.00);

-- Example: start and finish maintenance for order 1 (optional)
-- UPDATE ordem_servico SET entrada_manutencao = '2026-06-23 09:00:00', saida_manutencao = '2026-06-23 12:00:00' WHERE id = 1;

-- After setting times, you can compute and update values manually or let the application compute and persist them.

INSERT INTO ordem_servico(veiculo_id, descricao, status, data_abertura, valor_hora, valor_mao_obra, valor_servicos, valor_total)
VALUES (1, 'OS pausada exemplo', 'PAUSADA', NOW(), 60.00, 0.00, 0.00, 0.00);
