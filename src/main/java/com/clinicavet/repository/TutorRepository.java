package com.clinicavet.repository;

import com.clinicavet.model.Tutor;
import com.clinicavet.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TutorRepository {

    public Tutor salvar(Tutor tutor) {
        String sql = "INSERT INTO tutor (nome, endereco, telefone) VALUES (?, ?, ?)";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, tutor.getNome());
            stmt.setString(2, tutor.getEndereco());
            stmt.setString(3, tutor.getTelefone());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    tutor.setId(rs.getInt(1));
                }
            }
            return tutor;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar tutor: " + e.getMessage(), e);
        }
    }

    public Tutor buscarPorId(Integer id) {
        String sql = "SELECT id, nome, endereco, telefone FROM tutor WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearTutor(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tutor por id: " + e.getMessage(), e);
        }
    }

    public List<Tutor> listarTodos() {
        String sql = "SELECT id, nome, endereco, telefone FROM tutor ORDER BY nome";
        List<Tutor> tutores = new ArrayList<>();

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tutores.add(mapearTutor(rs));
            }
            return tutores;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tutores: " + e.getMessage(), e);
        }
    }

    public boolean atualizar(Tutor tutor) {
        String sql = "UPDATE tutor SET nome = ?, endereco = ?, telefone = ? WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, tutor.getNome());
            stmt.setString(2, tutor.getEndereco());
            stmt.setString(3, tutor.getTelefone());
            stmt.setInt(4, tutor.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar tutor: " + e.getMessage(), e);
        }
    }

    public boolean deletar(Integer id) {
        String sql = "DELETE FROM tutor WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar tutor: " + e.getMessage(), e);
        }
    }

    public boolean existePorId(Integer id) {
        return buscarPorId(id) != null;
    }

    private Tutor mapearTutor(ResultSet rs) throws SQLException {
        return new Tutor(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("endereco"),
                rs.getString("telefone")
        );
    }
}
