package com.escola.controller;

import com.escola.model.Aluno;
import com.escola.service.AlunoService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController() {
        this.alunoService = new AlunoService();
    }

    public Aluno cadastrar(String nome, String email, String telefone) {
        try {
            Aluno aluno = alunoService.cadastrarAluno(nome, email, telefone);
            System.out.println("[OK] Aluno cadastrado: " + aluno);
            return aluno;
        } catch (IllegalArgumentException e) {
            System.out.println("[ERRO DE VALIDAÇÃO] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[ERRO DE BANCO] " + e.getMessage());
        }
        return null;
    }

    public Aluno buscarPorId(int id) {
        try {
            Optional<Aluno> aluno = alunoService.buscarPorId(id);
            if (aluno.isPresent()) {
                System.out.println("[OK] Aluno encontrado: " + aluno.get());
                return aluno.get();
            } else {
                System.out.println("[INFO] Aluno com id " + id + " não encontrado.");
            }
        } catch (SQLException e) {
            System.out.println("[ERRO DE BANCO] " + e.getMessage());
        }
        return null;
    }

    public List<Aluno> listarTodos() {
        try {
            List<Aluno> alunos = alunoService.listarTodos();
            System.out.println("[OK] Total de alunos: " + alunos.size());
            alunos.forEach(System.out::println);
            return alunos;
        } catch (SQLException e) {
            System.out.println("[ERRO DE BANCO] " + e.getMessage());
        }
        return List.of();
    }

    public void atualizar(Aluno aluno) {
        try {
            alunoService.atualizarAluno(aluno);
            System.out.println("[OK] Aluno atualizado: " + aluno);
        } catch (IllegalArgumentException e) {
            System.out.println("[ERRO DE VALIDAÇÃO] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[ERRO DE BANCO] " + e.getMessage());
        }
    }

    public void remover(int id) {
        try {
            alunoService.removerAluno(id);
            System.out.println("[OK] Aluno id=" + id + " removido.");
        } catch (SQLException e) {
            System.out.println("[ERRO DE BANCO] " + e.getMessage());
        }
    }
}
