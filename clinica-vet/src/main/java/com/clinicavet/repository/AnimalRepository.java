package com.clinicavet.repository;

import com.clinicavet.model.Animal;
import com.clinicavet.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AnimalRepository {

    public Animal salvar(Animal animal) {
        String sql = "INSERT INTO animal (nome, especie, raca, id_tutor) VALUES (?, ?, ?, ?)";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, animal.getNome());
            stmt.setString(2, animal.getEspecie());
            stmt.setString(3, animal.getRaca());
            stmt.setInt(4, animal.getIdTutor());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    animal.setId(rs.getInt(1));
                }
            }
            return animal;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar animal: " + e.getMessage(), e);
        }
    }

    public Animal buscarPorId(Integer id) {
        String sql = "SELECT id, nome, especie, raca, id_tutor FROM animal WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearAnimal(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar animal por id: " + e.getMessage(), e);
        }
    }

    public List<Animal> listarTodos() {
        String sql = "SELECT id, nome, especie, raca, id_tutor FROM animal ORDER BY nome";
        List<Animal> animais = new ArrayList<>();

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                animais.add(mapearAnimal(rs));
            }
            return animais;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar animais: " + e.getMessage(), e);
        }
    }

    /**
     * Lista todos os animais pertencentes a um tutor específico.
     * Atende ao requisito: "ver todos os animais que ele tem no sistema".
     */
    public List<Animal> listarPorTutor(Integer idTutor) {
        String sql = "SELECT id, nome, especie, raca, id_tutor FROM animal WHERE id_tutor = ? ORDER BY nome";
        List<Animal> animais = new ArrayList<>();

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idTutor);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    animais.add(mapearAnimal(rs));
                }
            }
            return animais;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar animais por tutor: " + e.getMessage(), e);
        }
    }

    public boolean atualizar(Animal animal) {
        String sql = "UPDATE animal SET nome = ?, especie = ?, raca = ?, id_tutor = ? WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, animal.getNome());
            stmt.setString(2, animal.getEspecie());
            stmt.setString(3, animal.getRaca());
            stmt.setInt(4, animal.getIdTutor());
            stmt.setInt(5, animal.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar animal: " + e.getMessage(), e);
        }
    }

    public boolean deletar(Integer id) {
        String sql = "DELETE FROM animal WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar animal: " + e.getMessage(), e);
        }
    }

    public boolean existePorId(Integer id) {
        return buscarPorId(id) != null;
    }

    private Animal mapearAnimal(ResultSet rs) throws SQLException {
        return new Animal(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("especie"),
                rs.getString("raca"),
                rs.getInt("id_tutor")
        );
    }
}
