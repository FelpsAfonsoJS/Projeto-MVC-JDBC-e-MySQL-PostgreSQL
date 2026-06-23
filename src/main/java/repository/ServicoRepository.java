package repository;

import connection.ConnectionFactory;
import model.Servico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicoRepository {

	public Servico save(Servico s) {
		String sql = "INSERT INTO servico(ordem_servico_id, descricao, valor) VALUES (?, ?, ?)";
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			if (s.getId() != null) {

				return update(s);
			}

			ps.setObject(1, null);
			ps.setString(2, s.getDescricao());
			ps.setDouble(3, s.getValor() != null ? s.getValor() : 0.0);
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) s.setId(rs.getInt(1));
			}
			return s;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public Servico update(Servico s) {
		String sql = "UPDATE servico SET ordem_servico_id = ?, descricao = ?, valor = ? WHERE id = ?";
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setObject(1, null);
			ps.setString(2, s.getDescricao());
			ps.setDouble(3, s.getValor() != null ? s.getValor() : 0.0);
			ps.setInt(4, s.getId());
			ps.executeUpdate();
			return s;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public Servico findById(int id) {
		String sql = "SELECT id, ordem_servico_id, descricao, valor FROM servico WHERE id = ?";
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

	public List<Servico> findByOrdemId(int ordemId) {
		String sql = "SELECT id, ordem_servico_id, descricao, valor FROM servico WHERE ordem_servico_id = ?";
		List<Servico> res = new ArrayList<>();
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, ordemId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) res.add(mapRow(rs));
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}

	public List<Servico> findAll() {
		String sql = "SELECT id, ordem_servico_id, descricao, valor FROM servico";
		List<Servico> res = new ArrayList<>();
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) res.add(mapRow(rs));
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}

	private Servico mapRow(ResultSet rs) throws SQLException {
		Servico s = new Servico();
		s.setId(rs.getInt("id"));
		s.setDescricao(rs.getString("descricao"));
		s.setValor(rs.getDouble("valor"));
		return s;
	}

	public void assignToOrdem(int servicoId, int ordemId) {
		String sql = "UPDATE servico SET ordem_servico_id = ? WHERE id = ?";
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, ordemId);
			ps.setInt(2, servicoId);
			ps.executeUpdate();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
}
