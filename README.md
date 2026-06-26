# Cenário 1 – Sistema de Clínica Veterinária

## 1. Contexto

Pequena clínica veterinária que hoje controla tudo em fichas de papel. O sistema deve
permitir cadastrar tutores e seus animais, e registrar consultas (o movimento do
sistema), evitando os problemas relatados: dificuldade de localizar o histórico de um
animal e confusão entre animais de nomes iguais pertencentes a tutores diferentes.

## 2. Entidades identificadas e campos mínimos

| Entidade | Campos mínimos | Observação |
|---|---|---|
| **Tutor** | id, nome, endereço, telefone | Cadastro simples — existe de forma independente |
| **Animal** | id, nome, espécie, raça, id_tutor | Cadastro simples — vinculado obrigatoriamente a um tutor |
| **Consulta** | id, id_animal, data, motivo, valor | Movimento — depende do animal (e indiretamente do tutor) |

## 3. Script SQL (DDL) — MySQL

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
        REFERENCES tutor (id)
        ON DELETE CASCADE
);

CREATE TABLE consulta (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    id_animal INT NOT NULL,
    data      DATE NOT NULL,
    motivo    VARCHAR(255) NOT NULL,
    valor     DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_consulta_animal FOREIGN KEY (id_animal)
        REFERENCES animal (id)
        ON DELETE CASCADE,
    CONSTRAINT chk_consulta_valor CHECK (valor >= 0)
);
```

**Relacionamentos:**
- `tutor 1 — N animal`: um tutor pode ter vários animais; cada animal pertence a exatamente um tutor (`id_tutor`).
- `animal 1 — N consulta`: um animal pode ter várias consultas; cada consulta pertence a exatamente um animal (`id_animal`).

## 4. Regras de negócio identificadas

1. **Tutor é cadastro independente.** Pode existir no sistema sem nenhum animal vinculado. Campos obrigatórios: nome, endereço e telefone.
2. **Um tutor pode ter mais de um animal.** A relação é 1:N entre tutor e animal.
3. **Cada animal pertence a um único tutor**, identificado pelo `id_tutor` (chave estrangeira). Isso resolve o problema relatado de animais com nomes iguais e donos diferentes: a identificação correta nunca é feita pelo nome do animal isoladamente, mas pelo seu `id` único, vinculado ao `id` do tutor correto.
4. **Não é permitido registrar consulta para um animal que não esteja cadastrado.** Antes de salvar a consulta, o sistema verifica se o `id_animal` informado existe na tabela `animal`.
5. **O valor da consulta não pode ser negativo.** Validado tanto na camada de `service` (Java) quanto via `CHECK` no banco de dados.
6. **É possível consultar o histórico de atendimentos de um animal específico**, listando todas as consultas vinculadas ao `id_animal`.
7. **É possível listar todos os animais de um tutor específico**, atendendo ao caso de uso "tutor liga novamente e quero ver todos os animais dele".
8. **Toda consulta precisa informar:** animal atendido, data do atendimento, motivo do atendimento e valor cobrado — todos campos obrigatórios.

## 5. Estrutura do projeto (MVC)

```
src/main/java/com/clinicavet/
├── model/        -> Tutor, Animal, Consulta
├── repository/   -> TutorRepository, AnimalRepository, ConsultaRepository (CRUD via JDBC)
├── service/      -> TutorService, AnimalService, ConsultaService (regras de negócio)
├── controller/   -> TutorController, AnimalController, ConsultaController
├── util/         -> Conexao.java (configuração JDBC)
└── Main.java     -> simula o fluxo: Tutor → Animal → Consulta
```

## 6. Como executar

1. Crie o banco de dados executando o script SQL da seção 3 em sua instância MySQL.
2. Ajuste usuário/senha/host em `src/main/java/com/clinicavet/util/Conexao.java`.
3. Importe o projeto no IntelliJ como projeto Maven (`pom.xml` na raiz).
4. Execute a classe `com.clinicavet.Main`.

A `Main` demonstra o encadeamento completo exigido: primeiro cria o tutor, depois o
animal vinculado a ele, depois registra a consulta — além de demonstrar as regras
de negócio (rejeição de consulta para animal inexistente e de valor negativo).
