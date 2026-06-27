# Sistema de Oficina Mecânica

## Visão Geral
Sistema completo e interativo para gerenciar clientes, veículos e ordens de serviço em uma oficina mecânica, com conexão a banco de dados MySQL. Inclui validações, histórico detalhado, proteção de dados e opções de gerenciamento de records.

## Requisitos
- Java 17 ou superior
- Maven 3.6+
- MySQL 5.7+ rodando em 127.0.0.1:3307
- Banco de dados `OficinaMecanica` criado (veja instruções abaixo)

## Configuração

### 1. Configurar Banco de Dados MySQL
Execute os comandos SQL fornecidos no seu MySQL Workbench:

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

Ou use variáveis de ambiente (PowerShell):

```powershell
$env:MYSQL_URL="jdbc:mysql://127.0.0.1:3307/OficinaMecanica?useSSL=false&serverTimezone=UTC"
$env:MYSQL_USER="seu_usuario"
$env:MYSQL_PASS="sua_senha"
```

### 3. Compilar
```bash
mvn -DskipTests clean compile
```

## Executar

### Via IDE
Abra a classe `Main.java` e clique em "Run" (ou pressione Shift+F10 no IntelliJ).

### Via Maven
```bash
mvn -Dexec.mainClass=Main exec:java
```

## Estrutura de Menu

O sistema oferece um menu hierárquico com as seguintes opções:

```
MENU PRINCIPAL
├─ 1. CADASTRO
│  ├─ 1.1 Cadastrar Cliente
│  └─ 1.2 Cadastrar Veículo
├─ 2. BUSCAR CLIENTES / VEÍCULOS
│  ├─ 2.1 Buscar por Nome de Cliente
│  ├─ 2.2 Buscar por CPF de Cliente
│  └─ 2.3 Buscar por Placa de Veículo
├─ 3. ORDEM DE SERVIÇO
│  ├─ 3.1 Abrir Nova Ordem de Serviço
│  └─ 3.2 Histórico de OS
│     ├─ 3.2.1 Buscar por Placa
│     ├─ 3.2.2 Buscar por Período
│     ├─ 3.2.3 Buscar por Nome de Cliente
│     └─ 3.2.4 Buscar por CPF de Cliente
└─ 4. GERENCIAR DADOS
   ├─ 4.1 Apagar Cliente
   ├─ 4.2 Apagar Veículo
   └─ 4.3 Transferir Veículo para Outro Cliente
```

## Funcionalidades Principais

### 1. Cadastro
- **Cadastrar Cliente**: Nome (apenas letras), CPF (validado e único), Telefone
- **Cadastrar Veículo**: Placa (ABC-1234, única), Modelo, Ano, associado a Cliente existente
- ✅ Validação de campos por tipo
- ✅ Prevenção de duplicatas (CPF e Placa)

### 2. Buscar
- **Por Nome do Cliente**: Busca parcial (case-insensitive)
- **Por CPF do Cliente**: Busca exata
- **Por Placa de Veículo**: Busca exata, exibe proprietário
- ✅ Proteção de dados: CPF parcialmente mascarado

### 3. Ordem de Serviço
- **Abrir OS**: Requer placa (obrigatório), valida que veículo existe
- **Adicionar Serviços**: Múltiplos serviços por OS com descrição e valor
- **Iniciar Manutenção**: Registra horário de entrada
- **Fechar Manutenção**: Registra horário de saída, calcula valores automaticamente
  - Mão de obra = (horas de duração) × R$ 60,00
  - Total = Mão de obra + Soma de Serviços

### 4. Histórico de OS
- **Por Placa**: Todas as ordens do veículo
- **Por Período**: Ordens entre datas (formato YYYY-MM-DD)
- **Por Nome de Cliente**: Todas as ordens de clientes com aquele nome
- **Por CPF de Cliente**: Todas as ordens do cliente específico
- ✅ Exibe: ID, Cliente, Placa, Status, Data/hora, Serviços, Valores, Total
- ✅ Cálculo totalizador ao final

### 5. Gerenciar Dados
- **Apagar Cliente**: Com confirmação
- **Apagar Veículo**: Com confirmação
- **Transferir Veículo**: Mudar cliente proprietário (ex: venda do veículo)
- ✅ Confirmações obrigatórias para operações críticas

## Validações Automáticas

### Campos de Entrada
- **Nome**: Apenas letras e espaços (rejeita números/caracteres especiais)
- **CPF**: Formato XXX.XXX.XXX-XX ou 11 dígitos (valida unicidade)
- **Placa**: Formato ABC-1234 ou ABC1234 (valida unicidade)
- **Números**: Validação de tipo e reconversão automática
- **Datas**: Formato ISO YYYY-MM-DD

### Regras de Negócio
- ✅ Um cliente pode ter vários veículos
- ✅ Um veículo pode ter apenas um cliente
- ✅ Não é permitido abrir OS sem placa informada
- ✅ OS só pode ser aberta para veículo cadastrado
- ✅ Se veículo não existe, oferece opção de cadastrar
- ✅ Veiculo não pode existir sem cliente associado
- ✅ OS não pode existir sem veiculo

## Exemplo de Uso Completo

```
1. CADASTRO
   1.1 → Cadastrar Cliente
       Nome: João Silva
       CPF: 123.456.789-00
       Telefone: (11) 99999-0000
       ✓ Cliente ID 1 cadastrado
   
   1.2 → Cadastrar Veículo
       ID Cliente: 1
       Placa: ABC-1234
       Modelo: Fiat Uno
       Ano: 2010
       ✓ Veículo ID 1 cadastrado

2. ORDEM DE SERVIÇO
   3.1 → Abrir Nova Ordem
       Placa: ABC-1234
       Descrição: Revisão geral
       → Adicionar Serviço: Troca óleo (R$ 120)
       → Adicionar Serviço: Filtro (R$ 30)
       → Iniciar Manutenção (registra entrada)
       → Fechar Manutenção (registra saída)
       ✓ Mão de obra: R$ 180,00 (3h × 60)
       ✓ Serviços: R$ 150,00
       ✓ Total: R$ 330,00

3. HISTÓRICO
   3.2.4 → Buscar por CPF
       CPF: 123.456.789-00
       ✓ Exibe todas as OS do cliente com detalhes
       ✓ Total geral de valores
```

## Proteção de Dados

- **CPF do Cliente**: Parcialmente mascarado ao exibir (ex: ***9000)
- **Dados Confidenciais**: Exibidos apenas quando necessário
- **Validação de Acesso**: Confirmar antes de deletar registros

## Solução de Problemas

### "No suitable driver found"
- Recarregue Maven no IDE: clique direito em `pom.xml` → Maven → Reload Project
- Execute: `mvn -U clean package`

### "Connection refused"
- Verifique: `Test-NetConnection -ComputerName 127.0.0.1 -Port 3307`
- Confirme credenciais em `db.properties`
- Verifique se banco `OficinaMecanica` existe

### "Access denied for user"
- Corrija usuário e senha em `db.properties`

### "CPF/Placa duplicada"
- Se tentar cadastrar CPF/placa já existentes, o sistema rejeitará
- Use outro CPF/placa ou apague o registro anterior (opção 4)

## Estrutura do Projeto

```
OficinaMecanica/
├── src/main/java/
│   ├── Main.java (Menu interativo hierárquico)
│   ├── connection/
│   │   ├── ConnectionFactory.java
│   │   └── TestConnection.java
│   ├── controller/
│   │   ├── ClienteController.java
│   │   ├── VeiculoController.java
│   │   └── OrdemServicoController.java
│   ├── dao/
│   │   ├── ClienteDAO.java (JDBC)
│   │   ├── VeiculoDAO.java (JDBC)
│   │   ├── OrdemServicoDAO.java (JDBC)
│   │   └── ServicoDAO.java (JDBC)
│   ├── model/
│   │   ├── Cliente.java
│   │   ├── Veiculo.java
│   │   ├── OrdemServico.java
│   │   ├── Servico.java
│   │   └── StatusOS.java (Enum: ABERTA, EM_MANUTENCAO, FECHADA)
│   └── util/
│       ├── InputValidator.java (Validações de entrada)
│       └── MenuHelper.java (Formatação e menus)
├── src/main/resources/
│   └── db.properties (Credenciais MySQL)
├── pom.xml (Dependências: mysql-connector-java 8.0.33)
├── seed.sql (Dados de exemplo)
└── README.md
```

## Notas Técnicas

- **Persistência**: JDBC com Pool de conexões via ConnectionFactory
- **Validações**: Implementadas em `InputValidator` com regex
- **Menu Hierárquico**: Submenus via loops com switch/case
- **Cálculos**: Javatime.LocalDateTime para duração de manutenção
- **Proteção**: Mascaramento de CPF, confirmações em operações críticas
- **Escalabilidade**: DAO pattern permite migração futura para JPA/Hibernate

## Versão
2.0 - Junho 2026 (Menu hierárquico, histórico completo, gerenciamento de dados, transferência de veículos)

