package com.escola.controller;

import com.escola.model.Matricula;
import com.escola.service.MatriculaService;

import java.sql.SQLException;
import java.util.List;

public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController() {
        this.matriculaService = new MatriculaService();
    }

    public Matricula realizarMatricula(int idAluno, int idCurso, double valor) {
        try {
            Matricula matricula = matriculaService.realizarMatricula(idAluno, idCurso, valor);
            System.out.println("[OK] Matrícula realizada: " + matricula);
            return matricula;
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("[REGRA DE NEGÓCIO] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[ERRO DE BANCO] " + e.getMessage());
        }
        return null;
    }

    public List<Matricula> listarPorAluno(int idAluno) {
        try {
            List<Matricula> matriculas = matriculaService.listarMatriculasPorAluno(idAluno);
            System.out.println("[OK] Matrículas do aluno id=" + idAluno + ": " + matriculas.size());
            matriculas.forEach(System.out::println);
            return matriculas;
        } catch (IllegalArgumentException e) {
            System.out.println("[REGRA DE NEGÓCIO] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[ERRO DE BANCO] " + e.getMessage());
        }
        return List.of();
    }

    public List<Matricula> listarPorCurso(int idCurso) {
        try {
            List<Matricula> matriculas = matriculaService.listarMatriculasPorCurso(idCurso);
            System.out.println("[OK] Alunos matriculados no curso id=" + idCurso + ": " + matriculas.size());
            matriculas.forEach(System.out::println);
            return matriculas;
        } catch (IllegalArgumentException e) {
            System.out.println("[REGRA DE NEGÓCIO] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[ERRO DE BANCO] " + e.getMessage());
        }
        return List.of();
    }

    public List<Matricula> listarTodas() {
        try {
            List<Matricula> matriculas = matriculaService.listarTodas();
            System.out.println("[OK] Total de matrículas: " + matriculas.size());
            matriculas.forEach(System.out::println);
            return matriculas;
        } catch (SQLException e) {
            System.out.println("[ERRO DE BANCO] " + e.getMessage());
        }
        return List.of();
    }

    public void cancelar(int idMatricula) {
        try {
            matriculaService.cancelarMatricula(idMatricula);
            System.out.println("[OK] Matrícula id=" + idMatricula + " cancelada.");
        } catch (SQLException e) {
            System.out.println("[ERRO DE BANCO] " + e.getMessage());
        }
    }
}
