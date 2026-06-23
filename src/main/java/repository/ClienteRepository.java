package repository;

import connection.ConnectionFactory;
import model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteRepository {

	public Cliente save(Cliente c) {
		String sql = "INSERT INTO cliente(nome, cpf, telefone) VALUES (?, ?, ?)";
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, c.getNome());
			ps.setString(2, c.getCpf());
			ps.setString(3, c.getTelefone());
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					c.setId(rs.getInt(1));
				}
			}
			return c;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public Cliente update(Cliente c) {
		String sql = "UPDATE cliente SET nome = ?, cpf = ?, telefone = ? WHERE id = ?";
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, c.getNome());
			ps.setString(2, c.getCpf());
			ps.setString(3, c.getTelefone());
			ps.setInt(4, c.getId());
			ps.executeUpdate();
			return c;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void delete(int id) {
		String sql = "DELETE FROM cliente WHERE id = ?";
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public Cliente findById(int id) {
		String sql = "SELECT id, nome, cpf, telefone FROM cliente WHERE id = ?";
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapRow(rs);
				}
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return null;
	}

	public Cliente findByCpf(String cpf) {
		String sql = "SELECT id, nome, cpf, telefone FROM cliente WHERE cpf = ?";
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, cpf);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapRow(rs);
				}
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return null;
	}

	public List<Cliente> findByNome(String nome) {
		String sql = "SELECT id, nome, cpf, telefone FROM cliente WHERE nome LIKE ?";
		List<Cliente> res = new ArrayList<>();
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, "%" + nome + "%");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					res.add(mapRow(rs));
				}
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}

	public List<Cliente> findAll() {
		String sql = "SELECT id, nome, cpf, telefone FROM cliente";
		List<Cliente> res = new ArrayList<>();
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				res.add(mapRow(rs));
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}


	private Cliente mapRow(ResultSet rs) throws SQLException {
		Cliente c = new Cliente();
		c.setId(rs.getInt("id"));
		c.setNome(rs.getString("nome"));
		c.setCpf(rs.getString("cpf"));
		c.setTelefone(rs.getString("telefone"));
		return c;
	}
}
