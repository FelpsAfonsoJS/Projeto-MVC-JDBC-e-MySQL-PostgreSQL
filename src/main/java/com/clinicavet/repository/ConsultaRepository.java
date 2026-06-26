package com.clinicavet.repository;

import com.clinicavet.model.Consulta;
import com.clinicavet.util.Conexao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ConsultaRepository {

    public Consulta salvar(Consulta consulta) {
        String sql = "INSERT INTO consulta (id_animal, data, motivo, valor) VALUES (?, ?, ?, ?)";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, consulta.getIdAnimal());
            stmt.setDate(2, Date.valueOf(consulta.getData()));
            stmt.setString(3, consulta.getMotivo());
            stmt.setBigDecimal(4, consulta.getValor());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    consulta.setId(rs.getInt(1));
                }
            }
            return consulta;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar consulta: " + e.getMessage(), e);
        }
    }

    public Consulta buscarPorId(Integer id) {
        String sql = "SELECT id, id_animal, data, motivo, valor FROM consulta WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearConsulta(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar consulta por id: " + e.getMessage(), e);
        }
    }

    public List<Consulta> listarTodos() {
        String sql = "SELECT id, id_animal, data, motivo, valor FROM consulta ORDER BY data DESC";
        List<Consulta> consultas = new ArrayList<>();

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                consultas.add(mapearConsulta(rs));
            }
            return consultas;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar consultas: " + e.getMessage(), e);
        }
    }

    /**
     * Lista o histórico de consultas de um animal específico.
     * Atende ao requisito: "consultar todas as consultas de um animal específico".
     */
    public List<Consulta> listarPorAnimal(Integer idAnimal) {
        String sql = "SELECT id, id_animal, data, motivo, valor FROM consulta WHERE id_animal = ? ORDER BY data DESC";
        List<Consulta> consultas = new ArrayList<>();

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idAnimal);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    consultas.add(mapearConsulta(rs));
                }
            }
            return consultas;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar consultas por animal: " + e.getMessage(), e);
        }
    }

    public boolean atualizar(Consulta consulta) {
        String sql = "UPDATE consulta SET id_animal = ?, data = ?, motivo = ?, valor = ? WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, consulta.getIdAnimal());
            stmt.setDate(2, Date.valueOf(consulta.getData()));
            stmt.setString(3, consulta.getMotivo());
            stmt.setBigDecimal(4, consulta.getValor());
            stmt.setInt(5, consulta.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar consulta: " + e.getMessage(), e);
        }
    }

    public boolean deletar(Integer id) {
        String sql = "DELETE FROM consulta WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar consulta: " + e.getMessage(), e);
        }
    }

    private Consulta mapearConsulta(ResultSet rs) throws SQLException {
        BigDecimal valor = rs.getBigDecimal("valor");
        return new Consulta(
                rs.getInt("id"),
                rs.getInt("id_animal"),
                rs.getDate("data").toLocalDate(),
                rs.getString("motivo"),
                valor
        );
    }
}
