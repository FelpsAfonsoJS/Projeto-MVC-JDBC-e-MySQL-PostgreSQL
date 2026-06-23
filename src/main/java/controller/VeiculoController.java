package controller;

import repository.VeiculoRepository;
import model.Cliente;
import model.Veiculo;

import java.util.List;

public class VeiculoController {

	private final VeiculoRepository dao = new VeiculoRepository();

	public Veiculo createVeiculo(String placa, String modelo, int ano, Cliente cliente) {
		if (cliente == null || cliente.getId() == null) throw new IllegalArgumentException("Veículo precisa estar associado a um cliente cadastrado");
		Veiculo v = new Veiculo(null, placa, modelo, ano, cliente);
		return dao.save(v);
	}

	public Veiculo updateVeiculo(Veiculo v) {
		return dao.update(v);
	}

	public void deleteVeiculo(int id) {
		dao.delete(id);
	}

	public Veiculo findByPlaca(String placa) {
		return dao.findByPlaca(placa);
	}

	public List<Veiculo> findByClienteId(int clienteId) {
		return dao.findByClienteId(clienteId);
	}

	public List<Veiculo> findAll() {
		return dao.findAll();
	}

	public Veiculo transferirProprietario(Veiculo v, Cliente novoCliente) {
		if (v == null || v.getId() == null) throw new IllegalArgumentException("Veículo inválido");
		v.setCliente(novoCliente);
		return dao.update(v);
	}
}
