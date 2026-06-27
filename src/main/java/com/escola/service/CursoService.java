package com.escola.service;

import com.escola.model.Curso;
import com.escola.repository.CursoRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService() {
        this.cursoRepository = new CursoRepository();
    }

    public Curso cadastrarCurso(String nome, String descricao, int cargaHoraria, int vagasTotais) throws SQLException {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do curso é obrigatório.");
        }
        if (cargaHoraria <= 0) {
            throw new IllegalArgumentException("Carga horária deve ser maior que zero.");
        }
        if (vagasTotais <= 0) {
            throw new IllegalArgumentException("Número de vagas deve ser maior que zero.");
        }
        Curso curso = new Curso(nome, descricao, cargaHoraria, vagasTotais);
        return cursoRepository.salvar(curso);
    }

    public Optional<Curso> buscarPorId(int id) throws SQLException {
        return cursoRepository.buscarPorId(id);
    }

    public List<Curso> listarTodos() throws SQLException {
        return cursoRepository.buscarTodos();
    }

    public void atualizarCurso(Curso curso) throws SQLException {
        cursoRepository.atualizar(curso);
    }

    public void removerCurso(int id) throws SQLException {
        cursoRepository.deletar(id);
    }

    // Chamado internamente pelo MatriculaService ao confirmar matrícula
    public void decrementarVaga(Curso curso) throws SQLException {
        int novasVagas = curso.getVagasDisponiveis() - 1;
        cursoRepository.atualizarVagasDisponiveis(curso.getId(), novasVagas);
        curso.setVagasDisponiveis(novasVagas);
    }
}
