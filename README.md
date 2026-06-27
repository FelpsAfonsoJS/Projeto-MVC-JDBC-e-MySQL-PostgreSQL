# Cenário 3 — Sistema de Escola de Cursos Livres

## Tabelas Identificadas

### aluno
| Campo     | Tipo         | Restrições         |
|-----------|--------------|--------------------|
| id        | SERIAL       | PRIMARY KEY        |
| nome      | VARCHAR(100) | NOT NULL           |
| email     | VARCHAR(100) | NOT NULL           |
| telefone  | VARCHAR(20)  | NOT NULL           |

### curso
| Campo               | Tipo         | Restrições         |
|---------------------|--------------|--------------------|
| id                  | SERIAL       | PRIMARY KEY        |
| nome                | VARCHAR(100) | NOT NULL           |
| descricao           | TEXT         |                    |
| carga_horaria       | INTEGER      | NOT NULL           |
| vagas_totais        | INTEGER      | NOT NULL           |
| vagas_disponiveis   | INTEGER      | NOT NULL           |

### matricula
| Campo           | Tipo    | Restrições                          |
|-----------------|---------|-------------------------------------|
| id              | SERIAL  | PRIMARY KEY                         |
| id_aluno        | INTEGER | NOT NULL, FK → aluno(id)            |
| id_curso        | INTEGER | NOT NULL, FK → curso(id)            |
| data_matricula  | DATE    | NOT NULL                            |
| valor           | NUMERIC(10,2) | NOT NULL                      |

---

## Comandos CREATE TABLE

```sql
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
```

---

## Regras de Negócio

| #    | Regra                                                                                             | Onde validar   |
|------|---------------------------------------------------------------------------------------------------|----------------|
| RN01 | Não é permitido realizar uma matrícula para um aluno que não esteja cadastrado.                   | MatriculaService |
| RN02 | Não é permitido realizar uma matrícula em um curso que não esteja cadastrado.                     | MatriculaService |
| RN03 | O valor pago na matrícula não pode ser negativo.                                                  | MatriculaService |
| RN04 | Um aluno não pode ser matriculado duas vezes no mesmo curso (matrícula duplicada é bloqueada).    | MatriculaService |
| RN05 | Não é possível matricular um aluno em um curso que já atingiu o limite de vagas.                  | MatriculaService |
| RN06 | A cada matrícula realizada, o campo `vagas_disponiveis` do curso deve ser decrementado em 1.      | MatriculaService / CursoService |
| RN07 | O nome do aluno, e-mail e telefone são obrigatórios no cadastro.                                  | AlunoService   |
| RN08 | O nome do curso, carga horária e número de vagas são obrigatórios. Carga horária e vagas > 0.    | CursoService   |
| RN09 | É possível consultar todos os cursos em que um aluno está matriculado.                            | MatriculaService |
| RN10 | É possível consultar todos os alunos matriculados em um determinado curso.                        | MatriculaService |

---

## Estrutura do Projeto (MVC)

```
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
```

---

## Como executar

1. Crie o banco de dados no PostgreSQL:
   ```sql
   CREATE DATABASE escola_cursos;
   ```
2. Execute os `CREATE TABLE` acima no banco criado.
3. Ajuste usuário e senha em `util/Conexao.java` se necessário (padrão: `postgres`/`postgres`).
4. Execute `Main.java` para ver a simulação completa do fluxo.

---

## Fluxo simulado na Main

A `Main` demonstra o seguinte encadeamento:

1. Cadastro de dois alunos (Ana e Bruno)
2. Cadastro de dois cursos (Java com **1 vaga**, Design com 10 vagas)
3. ✅ Matrícula válida: Ana → Java
4. ❌ Matrícula duplicada bloqueada: Ana tenta entrar em Java novamente
5. ❌ Curso sem vaga bloqueado: Bruno tenta entrar em Java (já lotado)
6. ✅ Matrícula válida: Bruno → Design
7. ❌ Valor negativo bloqueado: Ana tenta pagar -R$50 no Design
8. Consultas: cursos de Ana, alunos do Java, listagem geral
