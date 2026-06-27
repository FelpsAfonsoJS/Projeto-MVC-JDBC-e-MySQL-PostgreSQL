package com.escola.service;

import com.escola.model.Aluno;
import com.escola.repository.AlunoRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AlunoService {

    private final AlunoRepository alunoRepository;

    public AlunoService() {
        this.alunoRepository = new AlunoRepository();
    }

    public Aluno cadastrarAluno(String nome, String email, String telefone) throws SQLException {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do aluno é obrigatório.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-mail do aluno é obrigatório.");
        }
        if (telefone == null || telefone.isBlank()) {
            throw new IllegalArgumentException("Telefone do aluno é obrigatório.");
        }
        Aluno aluno = new Aluno(nome, email, telefone);
        return alunoRepository.salvar(aluno);
    }

    public Optional<Aluno> buscarPorId(int id) throws SQLException {
        return alunoRepository.buscarPorId(id);
    }

    public List<Aluno> listarTodos() throws SQLException {
        return alunoRepository.buscarTodos();
    }

    public void atualizarAluno(Aluno aluno) throws SQLException {
        if (aluno.getNome() == null || aluno.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do aluno é obrigatório.");
        }
        alunoRepository.atualizar(aluno);
    }

    public void removerAluno(int id) throws SQLException {
        alunoRepository.deletar(id);
    }
}
