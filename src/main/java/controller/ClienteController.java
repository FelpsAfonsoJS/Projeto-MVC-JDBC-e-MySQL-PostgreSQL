package controller;

import repository.ClienteRepository;
import model.Cliente;

import java.util.List;

public class ClienteController {

	private final ClienteRepository dao = new ClienteRepository();

	public Cliente createCliente(String nome, String cpf, String telefone) {
		Cliente c = new Cliente(null, nome, cpf, telefone);
		return dao.save(c);
	}

	public Cliente updateCliente(Cliente c) {
		return dao.update(c);
	}

	public void deleteCliente(int id) {
		dao.delete(id);
	}

	public Cliente findByCpf(String cpf) {
		return dao.findByCpf(cpf);
	}

	public List<Cliente> findByNome(String nome) {
		return dao.findByNome(nome);
	}

	public List<Cliente> findAll() {
		return dao.findAll();
	}
}
