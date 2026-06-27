package com.escola.controller;

import com.escola.model.Curso;
import com.escola.service.CursoService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CursoController {

    private final CursoService cursoService;

    public CursoController() {
        this.cursoService = new CursoService();
    }

    public Curso cadastrar(String nome, String descricao, int cargaHoraria, int vagasTotais) {
        try {
            Curso curso = cursoService.cadastrarCurso(nome, descricao, cargaHoraria, vagasTotais);
            System.out.println("[OK] Curso cadastrado: " + curso);
            return curso;
        } catch (IllegalArgumentException e) {
            System.out.println("[ERRO DE VALIDAÇÃO] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[ERRO DE BANCO] " + e.getMessage());
        }
        return null;
    }

    public Curso buscarPorId(int id) {
        try {
            Optional<Curso> curso = cursoService.buscarPorId(id);
            if (curso.isPresent()) {
                System.out.println("[OK] Curso encontrado: " + curso.get());
                return curso.get();
            } else {
                System.out.println("[INFO] Curso com id " + id + " não encontrado.");
            }
        } catch (SQLException e) {
            System.out.println("[ERRO DE BANCO] " + e.getMessage());
        }
        return null;
    }

    public List<Curso> listarTodos() {
        try {
            List<Curso> cursos = cursoService.listarTodos();
            System.out.println("[OK] Total de cursos: " + cursos.size());
            cursos.forEach(System.out::println);
            return cursos;
        } catch (SQLException e) {
            System.out.println("[ERRO DE BANCO] " + e.getMessage());
        }
        return List.of();
    }

    public void atualizar(Curso curso) {
        try {
            cursoService.atualizarCurso(curso);
            System.out.println("[OK] Curso atualizado: " + curso);
        } catch (SQLException e) {
            System.out.println("[ERRO DE BANCO] " + e.getMessage());
        }
    }

    public void remover(int id) {
        try {
            cursoService.removerCurso(id);
            System.out.println("[OK] Curso id=" + id + " removido.");
        } catch (SQLException e) {
            System.out.println("[ERRO DE BANCO] " + e.getMessage());
        }
    }
}
