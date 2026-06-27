# Sistema de Oficina Mecânica — Cenário 2

Sistema de linha de comando (Java + JDBC + MySQL) para gestão de clientes, veículos e ordens de serviço de uma oficina mecânica, desenvolvido como atividade prática de MVC, JDBC e Banco de Dados.

## Contexto (a dor do cliente)

A oficina hoje controla tudo em caderno. Quando um cliente retorna, é preciso lembrar manualmente qual é o carro dele, o que já foi feito e quanto ele deve. O sistema resolve isso permitindo cadastrar clientes e seus veículos, abrir ordens de serviço vinculadas a um veículo já cadastrado, e consultar todo o histórico de manutenções de um veículo específico.

## Entidades identificadas e campos mínimos

| Entidade | Campos mínimos exigidos pelo cliente | Observação |
|---|---|---|
| **Cliente** | nome, telefone | Cadastro simples — existe de forma independente |
| **Veículo** | placa, modelo, ano, vínculo com o cliente | Cadastro simples — vinculado a um cliente |
| **Ordem de Serviço** | veículo, descrição do problema, valor do serviço, status (aberta/concluída) | Movimento — depende do veículo (e indiretamente do cliente) |

> Observação: o projeto também persiste o CPF do cliente e detalha a ordem de serviço em sub-itens de **serviço** (mão de obra calculada por tempo + lista de serviços/peças), o que amplia o mínimo pedido pelo cliente sem contradizer nenhum requisito original.

## Modelo de dados (MySQL)

```sql
CREATE DATABASE IF NOT EXISTS OficinaMecanica;
USE OficinaMecanica;

-- Tabela de clientes
CREATE TABLE cliente (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nome      VARCHAR(150) NOT NULL,
    cpf       VARCHAR(14)  NOT NULL UNIQUE,
    telefone  VARCHAR(20)  NOT NULL
);

-- Tabela de veículos (cada veículo pertence a um único cliente)
CREATE TABLE veiculo (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    placa       VARCHAR(8)  NOT NULL UNIQUE,
    modelo      VARCHAR(100) NOT NULL,
    ano         INT NOT NULL,
    cliente_id  INT NOT NULL,
    CONSTRAINT fk_veiculo_cliente FOREIGN KEY (cliente_id)
        REFERENCES cliente(id)
        ON DELETE CASCADE
);

-- Tabela de ordens de serviço (o "movimento" do sistema)
CREATE TABLE ordem_servico (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    veiculo_id          INT NOT NULL,
    descricao           VARCHAR(255) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'ABERTA',
    data_abertura       DATETIME NOT NULL,
    entrada_manutencao  DATETIME NULL,
    saida_manutencao    DATETIME NULL,
    valor_hora          DECIMAL(10,2) NOT NULL DEFAULT 60.00,
    valor_mao_obra      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    valor_servicos      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    valor_total         DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_os_veiculo FOREIGN KEY (veiculo_id)
        REFERENCES veiculo(id)
        ON DELETE CASCADE,
    CONSTRAINT chk_os_valor_total CHECK (valor_total >= 0)
);

-- Itens/serviços executados dentro de uma ordem de serviço
CREATE TABLE servico (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    ordem_servico_id INT NULL,
    descricao        VARCHAR(255) NOT NULL,
    valor            DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_servico_os FOREIGN KEY (ordem_servico_id)
        REFERENCES ordem_servico(id)
        ON DELETE CASCADE,
    CONSTRAINT chk_servico_valor CHECK (valor >= 0)
);
```

### Relacionamentos
- Um **Cliente** pode ter vários **Veículos** (1:N).
- Um **Veículo** pode ter várias **Ordens de Serviço** ao longo do tempo (1:N) — isso forma o histórico de manutenções.
- Uma **Ordem de Serviço** pode ter vários **Serviços** (itens de mão de obra/peças) associados (1:N).

## Regras de negócio levantadas a partir do texto do cliente

1. Um cliente pode ter mais de um veículo cadastrado.
2. Não é permitido abrir uma ordem de serviço para um veículo que não esteja cadastrado — a abertura da OS exige a busca prévia do veículo pela placa; se não existir, a operação é rejeitada.
3. O valor do serviço não pode ser negativo.
4. Uma ordem de serviço deve indicar claramente se está **aberta** ou **concluída** (no sistema, o status evolui entre `ABERTA` → `EM_MANUTENCAO` → `FECHADA`, cobrindo o ciclo completo do atendimento).
5. É possível consultar todo o histórico de manutenções (ordens de serviço) de um determinado veículo — por placa, por período de datas, ou pelo cliente proprietário (nome ou CPF).
6. A ordem de serviço é o "movimento" do sistema: ela não existe sem um veículo cadastrado, e o veículo não existe sem um cliente — refletindo a dependência cliente → veículo → ordem de serviço.

### Regras adicionais implementadas no sistema (extensões além do mínimo pedido)
- Não é permitido fechar uma manutenção que ainda não foi iniciada, nem iniciar uma manutenção de uma ordem já fechada.
- O valor de mão de obra é calculado automaticamente a partir do tempo entre entrada e saída da manutenção, multiplicado pelo valor/hora da ordem.
- O valor total da ordem de serviço é a soma da mão de obra calculada com o valor de todos os serviços/itens adicionados a ela.
- O CPF do cliente é único no sistema, evitando cadastros duplicados da mesma pessoa.

## Arquitetura (padrão MVC)

```
src/main/java/
├── model/        → Cliente, Veiculo, OrdemServico, Servico, StatusOS
├── dao/          → ClienteDAO, VeiculoDAO, OrdemServicoDAO, ServicoDAO (CRUD via JDBC)
├── controller/   → ClienteController, VeiculoController, OrdemServicoController
├── view/         → MenuPrincipal, MenuCliente, MenuVeiculo, MenuOS
├── util/         → InputValidator, MenuHelper
├── connection/   → ConnectionFactory (configuração da conexão MySQL)
└── Main.java     → simula o fluxo completo via menu interativo
```

> Nota: as classes de acesso a dados estão no pacote `dao`, equivalentes ao pacote `repository` pedido no enunciado — cada classe concentra os métodos CRUD (`save`, `update`, `delete`, `findById`, `findAll`, buscas específicas) usando SQL puro via `PreparedStatement`.

## Simulação do movimento (fluxo na Main)

O `Main.java` implementa, via menu interativo, o encadeamento completo pedido no exercício:

1. **Cliente** → cadastro de um novo cliente (nome, CPF, telefone).
2. **Veículo** → cadastro de um veículo vinculado ao cliente criado (placa, modelo, ano).
3. **Ordem de Serviço** → abertura de uma OS para o veículo cadastrado (busca por placa), com possibilidade de:
   - adicionar serviços/itens à ordem;
   - iniciar a manutenção (`EM_MANUTENCAO`);
   - fechar a manutenção (`FECHADA`), calculando automaticamente mão de obra, valor dos serviços e valor total.

O histórico de ordens de serviço de um veículo pode então ser consultado por placa, por período ou pelo cliente associado.

## Tecnologias

- Java 17+ (Maven)
- JDBC com MySQL (`com.mysql:mysql-connector-j`)
- Padrão MVC (model / dao / controller / view / util / connection)

## Como executar

1. Crie o banco `OficinaMecanica` no MySQL e execute o script de criação das tabelas acima.
2. Configure a conexão em `src/main/resources/db.properties` (ou pelas variáveis de ambiente `MYSQL_URL`, `MYSQL_USER`, `MYSQL_PASS`).
3. (Opcional) Popule dados de exemplo com o `seed.sql` incluído no projeto.
4. Execute a classe `Main.java` (ou `mvn exec:java`) e siga o menu interativo.
