package com.escola;

import com.escola.controller.AlunoController;
import com.escola.controller.CursoController;
import com.escola.controller.MatriculaController;
import com.escola.model.Aluno;
import com.escola.model.Curso;
import com.escola.model.Matricula;

public class Main {

    public static void main(String[] args) {

        AlunoController alunoController = new AlunoController();
        CursoController cursoController = new CursoController();
        MatriculaController matriculaController = new MatriculaController();

        System.out.println("=== SISTEMA DE ESCOLA DE CURSOS LIVRES ===\n");

        // -------------------------------------------------------
        // ETAPA 1: Cadastro de alunos
        // -------------------------------------------------------
        System.out.println("--- Cadastro de Alunos ---");
        Aluno aluno1 = alunoController.cadastrar("Ana Souza", "ana@email.com", "44999990001");
        Aluno aluno2 = alunoController.cadastrar("Bruno Lima", "bruno@email.com", "44999990002");

        // -------------------------------------------------------
        // ETAPA 2: Cadastro de cursos
        // -------------------------------------------------------
        System.out.println("\n--- Cadastro de Cursos ---");
        // Curso com 1 vaga apenas (para testar lotação)
        Curso cursoJava = cursoController.cadastrar(
                "Java para Iniciantes",
                "Fundamentos da linguagem Java com projetos práticos.",
                40, 1
        );
        Curso cursoDesign = cursoController.cadastrar(
                "Design Gráfico",
                "Criação visual com ferramentas profissionais.",
                32, 10
        );

        // -------------------------------------------------------
        // ETAPA 3: Matrícula válida — Ana no curso de Java
        // -------------------------------------------------------
        System.out.println("\n--- Matrícula Válida: Ana → Java ---");
        Matricula m1 = matriculaController.realizarMatricula(
                aluno1.getId(), cursoJava.getId(), 350.00
        );

        // -------------------------------------------------------
        // ETAPA 4: Teste de matrícula duplicada — Ana tenta se
        // matricular novamente no mesmo curso
        // -------------------------------------------------------
        System.out.println("\n--- Tentativa de Matrícula Duplicada: Ana → Java (novamente) ---");
        matriculaController.realizarMatricula(
                aluno1.getId(), cursoJava.getId(), 350.00
        );

        // -------------------------------------------------------
        // ETAPA 5: Teste de curso sem vagas — Bruno tenta entrar
        // no curso de Java, mas a única vaga já foi ocupada
        // -------------------------------------------------------
        System.out.println("\n--- Tentativa de Matrícula sem Vaga: Bruno → Java (sem vaga) ---");
        matriculaController.realizarMatricula(
                aluno2.getId(), cursoJava.getId(), 350.00
        );

        // -------------------------------------------------------
        // ETAPA 6: Bruno se matricula no Design (com vagas)
        // -------------------------------------------------------
        System.out.println("\n--- Matrícula Válida: Bruno → Design ---");
        Matricula m2 = matriculaController.realizarMatricula(
                aluno2.getId(), cursoDesign.getId(), 280.00
        );

        // -------------------------------------------------------
        // ETAPA 7: Teste de valor negativo
        // -------------------------------------------------------
        System.out.println("\n--- Tentativa de Matrícula com Valor Negativo ---");
        matriculaController.realizarMatricula(
                aluno1.getId(), cursoDesign.getId(), -50.00
        );

        // -------------------------------------------------------
        // ETAPA 8: Consultas
        // -------------------------------------------------------
        System.out.println("\n--- Cursos de Ana ---");
        matriculaController.listarPorAluno(aluno1.getId());

        System.out.println("\n--- Alunos do curso Java ---");
        matriculaController.listarPorCurso(cursoJava.getId());

        System.out.println("\n--- Listagem geral de alunos ---");
        alunoController.listarTodos();

        System.out.println("\n--- Listagem geral de cursos ---");
        cursoController.listarTodos();

        System.out.println("\n=== FIM DA SIMULAÇÃO ===");
    }
}
