package com.escola.repository;

import com.escola.model.Curso;
import com.escola.util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CursoRepository {

    public Curso salvar(Curso curso) throws SQLException {
        String sql = "INSERT INTO curso (nome, descricao, carga_horaria, vagas_totais, vagas_disponiveis) " +
                     "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = Conexao.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, curso.getNome());
            stmt.setString(2, curso.getDescricao());
            stmt.setInt(3, curso.getCargaHoraria());
            stmt.setInt(4, curso.getVagasTotais());
            stmt.setInt(5, curso.getVagasDisponiveis());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                curso.setId(rs.getInt("id"));
            }
        }
        return curso;
    }

    public Optional<Curso> buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM curso WHERE id = ?";
        try (Connection conn = Conexao.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    public List<Curso> buscarTodos() throws SQLException {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT * FROM curso ORDER BY nome";
        try (Connection conn = Conexao.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                cursos.add(mapear(rs));
            }
        }
        return cursos;
    }

    public void atualizar(Curso curso) throws SQLException {
        String sql = "UPDATE curso SET nome = ?, descricao = ?, carga_horaria = ?, " +
                     "vagas_totais = ?, vagas_disponiveis = ? WHERE id = ?";
        try (Connection conn = Conexao.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, curso.getNome());
            stmt.setString(2, curso.getDescricao());
            stmt.setInt(3, curso.getCargaHoraria());
            stmt.setInt(4, curso.getVagasTotais());
            stmt.setInt(5, curso.getVagasDisponiveis());
            stmt.setInt(6, curso.getId());
            stmt.executeUpdate();
        }
    }

    public void atualizarVagasDisponiveis(int idCurso, int novasVagas) throws SQLException {
        String sql = "UPDATE curso SET vagas_disponiveis = ? WHERE id = ?";
        try (Connection conn = Conexao.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, novasVagas);
            stmt.setInt(2, idCurso);
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM curso WHERE id = ?";
        try (Connection conn = Conexao.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Curso mapear(ResultSet rs) throws SQLException {
        return new Curso(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("descricao"),
                rs.getInt("carga_horaria"),
                rs.getInt("vagas_totais"),
                rs.getInt("vagas_disponiveis")
        );
    }
}
