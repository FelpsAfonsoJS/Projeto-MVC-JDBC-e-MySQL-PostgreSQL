package repository;

import connection.ConnectionFactory;
import model.Cliente;
import model.Veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VeiculoRepository {

	private final ClienteRepository clienteDAO = new ClienteRepository();

	public Veiculo save(Veiculo v) {
		String sql = "INSERT INTO veiculo(placa, modelo, ano, cliente_id) VALUES (?, ?, ?, ?)";
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, v.getPlaca());
			ps.setString(2, v.getModelo());
			ps.setInt(3, v.getAno());
			ps.setInt(4, v.getCliente().getId());
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) v.setId(rs.getInt(1));
			}
			return v;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public Veiculo update(Veiculo v) {
		String sql = "UPDATE veiculo SET placa = ?, modelo = ?, ano = ?, cliente_id = ? WHERE id = ?";
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, v.getPlaca());
			ps.setString(2, v.getModelo());
			ps.setInt(3, v.getAno());
			ps.setInt(4, v.getCliente().getId());
			ps.setInt(5, v.getId());
			ps.executeUpdate();
			return v;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void delete(int id) {
		String sql = "DELETE FROM veiculo WHERE id = ?";
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public Veiculo findById(int id) {
		String sql = "SELECT id, placa, modelo, ano, cliente_id FROM veiculo WHERE id = ?";
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return mapRow(rs);
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return null;
	}

	public Veiculo findByPlaca(String placa) {
		String sql = "SELECT id, placa, modelo, ano, cliente_id FROM veiculo WHERE placa = ?";
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, placa);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return mapRow(rs);
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return null;
	}

	public List<Veiculo> findByClienteId(int clienteId) {
		String sql = "SELECT id, placa, modelo, ano, cliente_id FROM veiculo WHERE cliente_id = ?";
		List<Veiculo> res = new ArrayList<>();
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, clienteId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) res.add(mapRow(rs));
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}

	public List<Veiculo> findAll() {
		String sql = "SELECT id, placa, modelo, ano, cliente_id FROM veiculo";
		List<Veiculo> res = new ArrayList<>();
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) res.add(mapRow(rs));
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}

	private Veiculo mapRow(ResultSet rs) throws SQLException {
		Veiculo v = new Veiculo();
		v.setId(rs.getInt("id"));
		v.setPlaca(rs.getString("placa"));
		v.setModelo(rs.getString("modelo"));
		v.setAno(rs.getInt("ano"));
		int clienteId = rs.getInt("cliente_id");
		Cliente c = clienteDAO.findById(clienteId);
		v.setCliente(c);
		return v;
	}
}
