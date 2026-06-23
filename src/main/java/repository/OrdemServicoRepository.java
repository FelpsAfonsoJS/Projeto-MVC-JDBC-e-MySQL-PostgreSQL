package repository;

import connection.ConnectionFactory;
import model.OrdemServico;
import model.Servico;
import model.StatusOS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OrdemServicoRepository {

	private final VeiculoRepository veiculoDAO = new VeiculoRepository();
	private final ServicoRepository servicoDAO = new ServicoRepository();

	public OrdemServico save(OrdemServico os) {
		String sql = "INSERT INTO ordem_servico(veiculo_id, descricao, status, data_abertura, entrada_manutencao, saida_manutencao, valor_hora, valor_mao_obra, valor_servicos, valor_total) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

			ps.setInt(1, os.getVeiculo().getId());
			ps.setString(2, os.getDescricao());
			ps.setString(3, os.getStatus() != null ? os.getStatus().name() : null);
			ps.setTimestamp(4, os.getDataAbertura() != null ? Timestamp.valueOf(os.getDataAbertura()) : null);
			ps.setTimestamp(5, os.getEntradaManutencao() != null ? Timestamp.valueOf(os.getEntradaManutencao()) : null);
			ps.setTimestamp(6, os.getSaidaManutencao() != null ? Timestamp.valueOf(os.getSaidaManutencao()) : null);
			ps.setDouble(7, os.getValorHora());
			ps.setDouble(8, os.getValorMaoObra());
			ps.setDouble(9, os.getValorServicos());
			ps.setDouble(10, os.getValorTotal());

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) os.setId(rs.getInt(1));
			}


			for (Servico s : os.getServicos()) {
				if (s.getId() != null) servicoDAO.assignToOrdem(s.getId(), os.getId());
			}

			return os;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public OrdemServico update(OrdemServico os) {
		if (os.getId() == null) throw new IllegalArgumentException("Ordem sem id");

		String sql = "UPDATE ordem_servico SET veiculo_id = ?, descricao = ?, status = ?, data_abertura = ?, entrada_manutencao = ?, saida_manutencao = ?, valor_hora = ?, valor_mao_obra = ?, valor_servicos = ?, valor_total = ? WHERE id = ?";

		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, os.getVeiculo().getId());
			ps.setString(2, os.getDescricao());
			ps.setString(3, os.getStatus() != null ? os.getStatus().name() : null);
			ps.setTimestamp(4, os.getDataAbertura() != null ? Timestamp.valueOf(os.getDataAbertura()) : null);
			ps.setTimestamp(5, os.getEntradaManutencao() != null ? Timestamp.valueOf(os.getEntradaManutencao()) : null);
			ps.setTimestamp(6, os.getSaidaManutencao() != null ? Timestamp.valueOf(os.getSaidaManutencao()) : null);
			ps.setDouble(7, os.getValorHora());
			ps.setDouble(8, os.getValorMaoObra());
			ps.setDouble(9, os.getValorServicos());
			ps.setDouble(10, os.getValorTotal());
			ps.setInt(11, os.getId());

			ps.executeUpdate();


			for (Servico s : os.getServicos()) {
				if (s.getId() != null) servicoDAO.assignToOrdem(s.getId(), os.getId());
			}

			return os;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void delete(int id) {
		String sql = "DELETE FROM ordem_servico WHERE id = ?";
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public OrdemServico findById(int id) {
		String sql = "SELECT id, veiculo_id, descricao, status, data_abertura, entrada_manutencao, saida_manutencao, valor_hora, valor_mao_obra, valor_servicos, valor_total FROM ordem_servico WHERE id = ?";
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

	public List<OrdemServico> findByVeiculoId(int veiculoId) {
		String sql = "SELECT id, veiculo_id, descricao, status, data_abertura, entrada_manutencao, saida_manutencao, valor_hora, valor_mao_obra, valor_servicos, valor_total FROM ordem_servico WHERE veiculo_id = ?";
		List<OrdemServico> res = new ArrayList<>();
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, veiculoId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) res.add(mapRow(rs));
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}

	public List<OrdemServico> findAll() {
		String sql = "SELECT id, veiculo_id, descricao, status, data_abertura, entrada_manutencao, saida_manutencao, valor_hora, valor_mao_obra, valor_servicos, valor_total FROM ordem_servico";
		List<OrdemServico> res = new ArrayList<>();

		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {

			while (rs.next()) res.add(mapRow(rs));
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}

	public List<OrdemServico> findByPeriod(java.sql.Date dataInicio, java.sql.Date dataFim) {
		String sql = "SELECT id, veiculo_id, descricao, status, data_abertura, entrada_manutencao, saida_manutencao, valor_hora, valor_mao_obra, valor_servicos, valor_total FROM ordem_servico WHERE DATE(data_abertura) >= ? AND DATE(data_abertura) <= ?";
		List<OrdemServico> res = new ArrayList<>();
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setDate(1, dataInicio);
			ps.setDate(2, dataFim);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) res.add(mapRow(rs));
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}

	// Novas: encontrar OS abertas (ABERTA, EM_MANUTENCAO, PAUSADA)
	public List<OrdemServico> findOrdensAbertas() {
		String sql =
				"SELECT id, veiculo_id, descricao, status, data_abertura, entrada_manutencao, saida_manutencao, valor_hora, valor_mao_obra, valor_servicos, valor_total " +
						"FROM ordem_servico " +
						"WHERE status IN ('ABERTA','EM_MANUTENCAO','PAUSADA')";

		List<OrdemServico> res = new ArrayList<>();
		try (Connection conn = ConnectionFactory.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {

			while (rs.next()) res.add(mapRow(rs));
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}

	private OrdemServico mapRow(ResultSet rs) throws SQLException {
		OrdemServico os = new OrdemServico();
		os.setId(rs.getInt("id"));

		int vid = rs.getInt("veiculo_id");
		os.setVeiculo(veiculoDAO.findById(vid));

		os.setDescricao(rs.getString("descricao"));

		String status = rs.getString("status");
		if (status != null) {
			try {
				os.setStatus(StatusOS.valueOf(status));
			} catch (IllegalArgumentException e) {
				os.setStatus(StatusOS.ABERTA);
			}
		}

		Timestamp t;
		t = rs.getTimestamp("data_abertura");
		if (t != null) os.setDataAbertura(t.toLocalDateTime());

		t = rs.getTimestamp("entrada_manutencao");
		if (t != null) os.setEntradaManutencao(t.toLocalDateTime());

		t = rs.getTimestamp("saida_manutencao");
		if (t != null) os.setSaidaManutencao(t.toLocalDateTime());

		os.setValorHora(rs.getDouble("valor_hora"));


		List<Servico> servs = servicoDAO.findByOrdemId(os.getId());
		for (Servico s : servs) os.addServico(s);


		os.recomputeAfterAdjust();
		return os;
	}
}
