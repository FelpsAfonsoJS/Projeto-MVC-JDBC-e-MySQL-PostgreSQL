package com.escola.service;

import com.escola.model.Aluno;
import com.escola.model.Curso;
import com.escola.model.Matricula;
import com.escola.repository.AlunoRepository;
import com.escola.repository.CursoRepository;
import com.escola.repository.MatriculaRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;
    private final CursoService cursoService;

    public MatriculaService() {
        this.matriculaRepository = new MatriculaRepository();
        this.alunoRepository = new AlunoRepository();
        this.cursoRepository = new CursoRepository();
        this.cursoService = new CursoService();
    }

    public Matricula realizarMatricula(int idAluno, int idCurso, double valor) throws SQLException {
        // RN01 - Aluno deve estar cadastrado
        Optional<Aluno> aluno = alunoRepository.buscarPorId(idAluno);
        if (aluno.isEmpty()) {
            throw new IllegalArgumentException("Aluno com id " + idAluno + " não está cadastrado.");
        }

        // RN02 - Curso deve estar cadastrado
        Optional<Curso> curso = cursoRepository.buscarPorId(idCurso);
        if (curso.isEmpty()) {
            throw new IllegalArgumentException("Curso com id " + idCurso + " não está cadastrado.");
        }

        // RN03 - Valor não pode ser negativo
        if (valor < 0) {
            throw new IllegalArgumentException("O valor da matrícula não pode ser negativo.");
        }

        // RN04 - Não pode haver matrícula duplicada (mesmo aluno, mesmo curso)
        if (matriculaRepository.existeMatricula(idAluno, idCurso)) {
            throw new IllegalStateException(
                "Aluno '" + aluno.get().getNome() + "' já está matriculado no curso '" + curso.get().getNome() + "'."
            );
        }

        // RN05 - Curso deve ter vagas disponíveis
        Curso cursoObj = curso.get();
        if (cursoObj.getVagasDisponiveis() <= 0) {
            throw new IllegalStateException(
                "Curso '" + cursoObj.getNome() + "' não possui vagas disponíveis."
            );
        }

        // Persiste a matrícula
        Matricula matricula = new Matricula(idAluno, idCurso, LocalDate.now(), valor);
        matriculaRepository.salvar(matricula);

        // Decrementa as vagas do curso
        cursoService.decrementarVaga(cursoObj);

        return matricula;
    }

    public List<Matricula> listarMatriculasPorAluno(int idAluno) throws SQLException {
        Optional<Aluno> aluno = alunoRepository.buscarPorId(idAluno);
        if (aluno.isEmpty()) {
            throw new IllegalArgumentException("Aluno com id " + idAluno + " não encontrado.");
        }
        return matriculaRepository.buscarPorAluno(idAluno);
    }

    public List<Matricula> listarMatriculasPorCurso(int idCurso) throws SQLException {
        Optional<Curso> curso = cursoRepository.buscarPorId(idCurso);
        if (curso.isEmpty()) {
            throw new IllegalArgumentException("Curso com id " + idCurso + " não encontrado.");
        }
        return matriculaRepository.buscarPorCurso(idCurso);
    }

    public List<Matricula> listarTodas() throws SQLException {
        return matriculaRepository.buscarTodos();
    }

    public void cancelarMatricula(int idMatricula) throws SQLException {
        matriculaRepository.deletar(idMatricula);
    }
}
