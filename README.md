# 🐾 Sistema de Clínica Veterinária

Projeto acadêmico desenvolvido em **Java**, aplicando o padrão **MVC** com persistência de dados via **JDBC** e banco de dados **MySQL**.

O sistema resolve o problema de uma clínica veterinária que ainda controla tudo em fichas de papel, permitindo cadastrar tutores, seus animais e registrar consultas, com histórico de atendimentos por animal.

---

## 📋 Sobre o projeto

Cenário proposto: uma clínica veterinária precisa digitalizar seu controle de tutores, animais e consultas, evitando erros como registrar atendimentos em animais errados quando dois pets têm o mesmo nome, mas donos diferentes.

A solução implementada permite:

- ✅ Cadastrar tutores (donos dos animais)
- ✅ Cadastrar animais vinculados a um tutor
- ✅ Registrar consultas veterinárias
- ✅ Consultar o histórico de atendimentos de um animal específico
- ✅ Listar todos os animais de um tutor

---

## 🏗️ Arquitetura

O projeto segue o padrão **MVC (Model-View-Controller)**, com a camada de persistência isolada em `repository`:

```
src/main/java/com/clinicavet/
├── model/        → Entidades: Tutor, Animal, Consulta
├── repository/    → CRUD de cada entidade via SQL puro (JDBC)
├── service/       → Regras de negócio e validações
├── controller/    → Orquestra a chamada entre a ação e o service
├── util/          → Conexao.java (configuração da conexão com o MySQL)
└── Main.java      → Simula o fluxo completo: Tutor → Animal → Consulta
```

---

## 🗃️ Modelo de dados

| Entidade | Campos | Relacionamento |
|---|---|---|
| **Tutor** | id, nome, endereço, telefone | Cadastro independente |
| **Animal** | id, nome, espécie, raça, id_tutor | N:1 com Tutor |
| **Consulta** | id, id_animal, data, motivo, valor | N:1 com Animal |

```sql
CREATE DATABASE IF NOT EXISTS clinica_veterinaria;
USE clinica_veterinaria;

CREATE TABLE tutor (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nome      VARCHAR(150) NOT NULL,
    endereco  VARCHAR(255) NOT NULL,
    telefone  VARCHAR(20)  NOT NULL
);

CREATE TABLE animal (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nome      VARCHAR(100) NOT NULL,
    especie   VARCHAR(50)  NOT NULL,
    raca      VARCHAR(50),
    id_tutor  INT NOT NULL,
    CONSTRAINT fk_animal_tutor FOREIGN KEY (id_tutor)
        REFERENCES tutor (id) ON DELETE CASCADE
);

CREATE TABLE consulta (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    id_animal INT NOT NULL,
    data      DATE NOT NULL,
    motivo    VARCHAR(255) NOT NULL,
    valor     DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_consulta_animal FOREIGN KEY (id_animal)
        REFERENCES animal (id) ON DELETE CASCADE,
    CONSTRAINT chk_consulta_valor CHECK (valor >= 0)
);
```

---

## ⚙️ Regras de negócio

- Um tutor pode ter vários animais cadastrados
- Cada animal pertence a exatamente um tutor (identificação por `id`, evitando confusão entre animais com nomes iguais)
- Não é permitido registrar consulta para um animal não cadastrado
- O valor da consulta não pode ser negativo
- É possível consultar o histórico de atendimentos de um animal específico
- É possível listar todos os animais de um tutor

---

## 🚀 Tecnologias utilizadas

- Java 17
- Maven
- JDBC (sem ORM — SQL puro)
- MySQL 8

---

## ▶️ Como executar

### Pré-requisitos
- JDK 17+
- MySQL rodando localmente
- Maven (ou usar o suporte integrado do IntelliJ)

### Passo a passo

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/clinica-veterinaria.git
   ```

2. Execute o script SQL (seção acima) no seu MySQL para criar o banco e as tabelas.

3. Configure suas credenciais em `src/main/java/com/clinicavet/util/Conexao.java`:
   ```java
   private static final String USUARIO = "root";
   private static final String SENHA = "sua_senha";
   ```

4. Abra o projeto no IntelliJ (`File → Open` → selecione a pasta com o `pom.xml`).

5. Execute a classe `Main.java`.

A `Main` simula o fluxo completo: cria um tutor, cadastra animais vinculados a ele, registra consultas e demonstra as validações de negócio (consulta para animal inexistente e valor negativo).

---

## 📌 Status do projeto

Projeto acadêmico finalizado, desenvolvido para fins de aprendizado de **JDBC**, **MVC** e **SQL** com Java.

---

## 👤 Autor

Desenvolvido por **Alisson** — estudante da UMFG Faculdade.
