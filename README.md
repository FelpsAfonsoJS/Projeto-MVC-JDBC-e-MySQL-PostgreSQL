# Sistema de Oficina Mecânica

## Visão Geral
Sistema completo e interativo para gerenciar **clientes**, **veículos** e **ordens de serviço (OS)** em uma oficina mecânica, com persistência em **MySQL** via JDBC. O sistema permite:
- Cadastrar clientes e veículos
- Abrir OS por **placa** (apenas se o veículo estiver cadastrado)
- Adicionar **serviços** a uma OS
- **Iniciar** e **fechar** a manutenção (status e cálculo de valores)
- Consultar o **histórico** de OS por placa, período, nome e CPF do cliente

> Observação: o sistema **não** implementa “pausar e retomar” a OS em estados intermediários diferentes de `EM_MANUTENCAO` nem “dar baixa em OS abertas” via menu. Os estados suportados são apenas `ABERTA`, `EM_MANUTENCAO` e `FECHADA`.

## Requisitos
- Java 17 ou superior
- Maven 3.6+
- MySQL 5.7+ rodando em `127.0.0.1:3307`
- Banco de dados `OficinaMecanica` criado (ver instruções abaixo)

## Configuração

### 1. Configurar Banco de Dados MySQL
Execute os comandos SQL no MySQL Workbench:

```sql
create database OficinaMecanica;
use OficinaMecanica;

create table cliente(
  id int auto_increment primary key,
  nome varchar(100) not null,
  cpf varchar(14) not null unique,
  telefone varchar(20)
);

create table veiculo(
  id int auto_increment primary key,
  placa varchar(10) not null unique,
  modelo varchar(100) not null,
  ano int not null,
  cliente_id int not null,
  constraint fk_cliente
  foreign key(cliente_id)
  references cliente(id)
);

CREATE TABLE ordem_servico(
    id INT AUTO_INCREMENT PRIMARY KEY,
    veiculo_id INT NOT NULL,
    descricao TEXT,
    status VARCHAR(30),
    data_abertura DATETIME,
    entrada_manutencao DATETIME,
    saida_manutencao DATETIME,
    valor_hora DECIMAL(10,2),
    valor_mao_obra DECIMAL(10,2),
    valor_servicos DECIMAL(10,2),
    valor_total DECIMAL(10,2),
    FOREIGN KEY(veiculo_id)
    REFERENCES veiculo(id)
);

CREATE TABLE servico(
    id INT AUTO_INCREMENT PRIMARY KEY,
    ordem_servico_id INT,
    descricao VARCHAR(200),
    valor DECIMAL(10,2),
    FOREIGN KEY(ordem_servico_id)
    REFERENCES ordem_servico(id)
);
```

### 2. Configurar Credenciais MySQL
Edite `src/main/resources/db.properties`:

```properties
url=jdbc:mysql://127.0.0.1:3307/OficinaMecanica?useSSL=false&serverTimezone=UTC
user=seu_usuario
password=sua_senha
```

### 3. Compilar
```bash
mvn -DskipTests clean compile
```

## Executar
### Via IDE
Abra `Main.java` e clique em **Run**.

### Via Maven
```bash
mvn -Dexec.mainClass=Main exec:java
```

## Estrutura de Menu
```
MENU PRINCIPAL
├─ 1. Cadastro
│  ├─ 1.1 Cadastrar Cliente
│  └─ 1.2 Cadastrar Veículo
├─ 2. Buscar Clientes / Veículos
│  ├─ 2.1 Buscar por Nome de Cliente
│  ├─ 2.2 Buscar por CPF de Cliente
│  ├─ 2.3 Buscar por Placa de Veículo
│  └─ (listar veículos por cliente)
└─ 3. Ordem de Serviço
   ├─ 3.1 Abrir Nova Ordem de Serviço
   │  ├─ Informar placa
   │  ├─ Informar descrição da OS
   │  ├─ Adicionar 0..N serviços (descrição e valor)
   │  ├─ Iniciar manutenção (opcional)
   │  └─ Fechar manutenção (opcional; ao fechar calcula valores)
   └─ 3.2 Histórico de OS
      ├─ Buscar por placa
      ├─ Buscar por período (YYYY-MM-DD)
      ├─ Buscar por nome de cliente
      └─ Buscar por CPF de cliente
```

## Funcionalidades Principais

### 1) Cadastro
- **Cadastrar Cliente**: nome, CPF e telefone
- **Cadastrar Veículo**: placa, modelo, ano e cliente (o cliente precisa existir)

### 2) Buscar
- **Clientes**
  - por nome (busca parcial)
  - por CPF (exata)
- **Veículos**
  - por placa (exata)
  - listar veículos de um cliente

### 3) Ordem de Serviço
- **Abrir OS (por placa)**  
  - Requer **placa** informada
  - Valida que o **veículo existe**
  - Salva uma OS com status inicial `ABERTA`

- **Adicionar Serviços à OS**
  - Permite adicionar múltiplos serviços à OS aberta
  - Cada serviço possui `descricao` e `valor`

- **Iniciar manutenção**
  - Define `status = EM_MANUTENCAO`
  - Registra `entrada_manutencao = agora`

- **Fechar manutenção**
  - Define `status = FECHADA`
  - Registra `saida_manutencao = agora`
  - Recalcula valores e persiste:
    - **valorMaoObra** = horas * `valor_hora` (padrão: 60,00)
    - **valorTotal** = valorMaoObra + valorServicos

- **Histórico de OS**
  - Exibe detalhes (ID, placa, cliente, descrição, status, datas, mão de obra, serviços, total)
  - Soma o **total geral** ao final da consulta

## Regras de Negócio (alinhadas ao sistema)

1. **Cliente → Veículos**
   - Um cliente pode ter **vários veículos**.
2. **Veículo → Cliente**
   - Um veículo pertence a **um único cliente** (`veiculo.cliente_id`).
3. **Veículo deve existir para abrir OS**
   - O sistema não permite abrir OS para **placa não cadastrada**.
4. **Estados da OS**
   - `ABERTA` → `EM_MANUTENCAO` → `FECHADA`
5. **Cálculo de valores**
   - `valorServicos` = soma dos serviços vinculados à OS
   - `valorMaoObra` = duração (`saida_manutencao - entrada_manutencao`) em horas × `valor_hora`
   - `valorTotal` = `valorMaoObra + valorServicos`
6. **Valor do serviço não é validado como não-negativo**
   - O sistema atual **não bloqueia** valores negativos na entrada (não há validação em `InputValidator`).
   - Logo, a regra “valor do serviço não pode ser negativo” **não está implementada** como validação.

> Se você quiser, a próxima etapa pode ser implementar essa validação no `InputValidator` e/ou no `OrdemServicoController.adicionarServico`.

## Tabelas Identificadas (campos mínimos)

### `cliente`
- `id` (PK)
- `nome` (NOT NULL)
- `cpf` (NOT NULL, UNIQUE)
- `telefone` (opcional)

### `veiculo`
- `id` (PK)
- `placa` (NOT NULL, UNIQUE)
- `modelo` (NOT NULL)
- `ano` (NOT NULL)
- `cliente_id` (NOT NULL, FK → `cliente.id`)

### `ordem_servico`
- `id` (PK)
- `veiculo_id` (NOT NULL, FK → `veiculo.id`)
- `descricao`
- `status` (ex.: `ABERTA`, `EM_MANUTENCAO`, `FECHADA`)
- `data_abertura`
- `entrada_manutencao`
- `saida_manutencao`
- `valor_hora`
- `valor_mao_obra`
- `valor_servicos`
- `valor_total`

### `servico`
- `id` (PK)
- `ordem_servico_id` (FK → `ordem_servico.id`, pode ficar NULL no processo de criação/associação dependendo do fluxo)
- `descricao`
- `valor`

## Seed de exemplo
O arquivo `seed.sql` cria dados para:
- clientes
- veículos
- uma ordem de serviço em status `ABERTA`
- serviços vinculados a essa OS

## Versão
2.0 - Junho 2026 (Menu hierárquico, histórico completo, gerenciamento de dados, transferência de veículos)

## Notas Técnicas
- Persistência: JDBC com `ConnectionFactory`
- Regras: aplicadas no fluxo do controller e no cálculo do modelo `OrdemServico`
- Enum: `StatusOS` (`ABERTA`, `EM_MANUTENCAO`, `FECHADA`)




Sistema de Escola de Cursos Livres
Visão Geral
Sistema completo para gerenciar alunos, cursos e matrículas em uma escola de cursos livres, com persistência em PostgreSQL via JDBC. O sistema permite:

Cadastrar alunos e cursos
Realizar matrículas (apenas para alunos e cursos já cadastrados)
Consultar os cursos de um aluno e os alunos de um curso
Listar todos os alunos e cursos cadastrados
Cancelar matrículas

Observação: o sistema não implementa edição de alunos ou cursos após o cadastro, nem reativação de matrícula cancelada. As operações suportadas são cadastro, matrícula, consulta e cancelamento.

Requisitos

Java 17 ou superior
Maven 3.6+
PostgreSQL rodando localmente
Banco de dados escola_cursos criado (ver instruções abaixo)


Configuração
1. Configurar Banco de Dados PostgreSQL
Execute os comandos SQL no pgAdmin ou psql:
sqlCREATE DATABASE escola_cursos;

CREATE TABLE aluno (
    id        SERIAL PRIMARY KEY,
    nome      VARCHAR(100) NOT NULL,
    email     VARCHAR(100) NOT NULL,
    telefone  VARCHAR(20)  NOT NULL
);

CREATE TABLE curso (
    id                 SERIAL PRIMARY KEY,
    nome               VARCHAR(100) NOT NULL,
    descricao          TEXT,
    carga_horaria      INTEGER      NOT NULL,
    vagas_totais       INTEGER      NOT NULL,
    vagas_disponiveis  INTEGER      NOT NULL
);

CREATE TABLE matricula (
    id              SERIAL PRIMARY KEY,
    id_aluno        INTEGER        NOT NULL REFERENCES aluno(id),
    id_curso        INTEGER        NOT NULL REFERENCES curso(id),
    data_matricula  DATE           NOT NULL,
    valor           NUMERIC(10,2)  NOT NULL
);
2. Configurar Credenciais PostgreSQL
Edite src/main/java/com/escola/util/Conexao.java:
java// Padrão: usuário postgres / senha postgres
// Ajuste conforme sua instalação local
3. Compilar
mvn -DskipTests clean compile

Executar
Via IDE: abra Main.java e clique em Run.
Via Maven:
mvn -Dexec.mainClass=com.escola.Main exec:java

Estrutura de Projeto (MVC)
src/main/java/com/escola/
├── model/
│   ├── Aluno.java
│   ├── Curso.java
│   └── Matricula.java
├── repository/
│   ├── AlunoRepository.java
│   ├── CursoRepository.java
│   └── MatriculaRepository.java
├── service/
│   ├── AlunoService.java
│   ├── CursoService.java
│   └── MatriculaService.java
├── controller/
│   ├── AlunoController.java
│   ├── CursoController.java
│   └── MatriculaController.java
├── util/
│   └── Conexao.java
└── Main.java

Fluxo Simulado na Main
A classe Main demonstra o seguinte encadeamento de operações:

Cadastro de dois alunos (Ana e Bruno)
Cadastro de dois cursos (Java com 1 vaga, Design com 10 vagas)
✅ Matrícula válida: Ana → Java (R$ 350,00)
❌ Matrícula duplicada bloqueada: Ana tenta se matricular em Java novamente
❌ Curso sem vagas bloqueado: Bruno tenta entrar em Java (já lotado)
✅ Matrícula válida: Bruno → Design (R$ 280,00)
❌ Valor negativo bloqueado: Ana tenta pagar −R$ 50,00 no Design
Consultas: cursos de Ana, alunos do Java, listagem geral de alunos e cursos


Funcionalidades Principais
1) Cadastro
Cadastrar Aluno: nome, e-mail e telefone (todos obrigatórios).
Cadastrar Curso: nome, descrição, carga horária, vagas totais e vagas disponíveis (nome, carga horária e vagas são obrigatórios; carga horária e vagas devem ser maiores que zero).
2) Consultas
Alunos: listagem geral de todos os alunos cadastrados.
Cursos: listagem geral de todos os cursos cadastrados.
Matrículas: listar todos os cursos de um aluno; listar todos os alunos de um curso.
3) Matrícula
Realizar Matrícula

Requer aluno e curso previamente cadastrados
Valida que o aluno existe (RN01)
Valida que o curso existe (RN02)
Valida que o valor não é negativo (RN03)
Bloqueia matrícula duplicada no mesmo curso (RN04)
Valida disponibilidade de vagas (RN05)
Ao matricular, decrementa vagas_disponiveis do curso (RN06)
Registra a data da matrícula como a data atual

Cancelar Matrícula

Remove a matrícula pelo ID
Não há restauração automática de vaga no cancelamento (comportamento atual)


Regras de Negócio
#RegraOnde validarRN01Aluno deve estar cadastrado para realizar matrículaMatriculaServiceRN02Curso deve estar cadastrado para realizar matrículaMatriculaServiceRN03O valor da matrícula não pode ser negativoMatriculaServiceRN04Um aluno não pode ser matriculado duas vezes no mesmo cursoMatriculaServiceRN05Não é possível matricular em curso sem vagas disponíveisMatriculaServiceRN06A cada matrícula, vagas_disponiveis do curso é decrementado em 1MatriculaService / CursoServiceRN07Nome, e-mail e telefone do aluno são obrigatóriosAlunoServiceRN08Nome, carga horária e vagas do curso são obrigatórios; carga horária e vagas > 0CursoServiceRN09É possível consultar todos os cursos de um alunoMatriculaServiceRN10É possível consultar todos os alunos de um cursoMatriculaService

Tabelas Identificadas (campos mínimos)
aluno

id (PK)
nome (NOT NULL)
email (NOT NULL)
telefone (NOT NULL)

curso

id (PK)
nome (NOT NULL)
descricao
carga_horaria (NOT NULL)
vagas_totais (NOT NULL)
vagas_disponiveis (NOT NULL)

matricula

id (PK)
id_aluno (NOT NULL, FK → aluno.id)
id_curso (NOT NULL, FK → curso.id)
data_matricula (NOT NULL)
valor (NOT NULL)


Versão
1.0 — Junho 2026 (Cadastro de alunos e cursos, matrícula com validação de vagas, consultas por aluno e por curso)

Notas Técnicas

Persistência: JDBC com classe utilitária Conexao
Regras: aplicadas na camada de serviço (MatriculaService, AlunoService, CursoService)
Banco: PostgreSQL (diferente do cenário da oficina, que usa MySQL)
