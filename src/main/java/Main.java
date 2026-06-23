import controller.ClienteController;
import controller.VeiculoController;
import controller.OrdemServicoController;
import model.Cliente;
import model.Veiculo;
import model.OrdemServico;
import util.InputValidator;
import util.MenuHelper;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

	private static ClienteController clienteController = new ClienteController();
	private static VeiculoController veiculoController = new VeiculoController();
	private static OrdemServicoController osController = new OrdemServicoController();

	public static void main(String[] args) {
		boolean continuar = true;
		while (continuar) {
			int opcao = MenuHelper.showMenu("MENU PRINCIPAL",
				"Cadastro", 
				"Buscar Clientes", 
				"Buscar Veículos",
				"Ordem de Serviço");
			
			switch (opcao) {
				case 1:
					menuCadastro();
					break;
				case 2:
					menuBuscarClientes();
					break;
				case 3:
					menuBuscarVeiculos();
					break;
				case 4:
					menuOrdenServico();
					break;
				case 0:
					continuar = false;
					break;
				default:
					MenuHelper.printError("Opção inválida!");
			}
		}
		InputValidator.closeScanner();
		MenuHelper.printInfo("Sistema encerrado.");
	}

	// ========== MENU 1: CADASTRO ==========
	private static void menuCadastro() {
		boolean voltar = false;
		while (!voltar) {
			int opcao = MenuHelper.showMenu("Cadastro", 
				"Cadastrar Cliente", 
				"Cadastrar Veículo");
			
			switch (opcao) {
				case 1:
					cadastrarCliente();
					break;
				case 2:
					cadastrarVeiculo();
					break;
				case 0:
					voltar = true;
					break;
				default:
					MenuHelper.printError("Opção inválida!");
			}
		}
	}

	private static void cadastrarCliente() {
		MenuHelper.printInfo("--- Cadastrar Novo Cliente ---");
		try {
			String nome = InputValidator.readNome("Nome (apenas letras e espaços): ");
			String cpf = InputValidator.readCPF("CPF (XXX.XXX.XXX-XX ou apenas dígitos): ");
			String telefone = InputValidator.readTelefone("Telefone: ");
			
			Cliente cli = clienteController.createCliente(nome, cpf, telefone);
			MenuHelper.printSuccess("Cliente cadastrado com ID: " + cli.getId() + " - " + cli.getNome());
		} catch (IllegalArgumentException e) {
			MenuHelper.printError(e.getMessage());
		} catch (RuntimeException e) {
			MenuHelper.printError("Erro ao cadastrar cliente: " + e.getMessage());
		}
	}

	private static void cadastrarVeiculo() {
		MenuHelper.printHeader("Cadastrar Novo Veículo");
		try {
			// Listar clientes disponíveis
			List<Cliente> clientes = clienteController.findAll();
			if (clientes.isEmpty()) {
				MenuHelper.printError("Nenhum cliente cadastrado. Cadastre um cliente primeiro.");
				return;
			}

			MenuHelper.printInfo("Clientes disponíveis:");
			for (Cliente c : clientes) {
				System.out.println("  ID: " + c.getId() + " | " + c.getNome());
			}
			
			int clienteId = InputValidator.readInt("ID do Cliente: ");
			Cliente cliente = clientes.stream()
				.filter(c -> c.getId() == clienteId)
				.findFirst()
				.orElse(null);
			
			if (cliente == null) {
				MenuHelper.printError("Cliente com ID " + clienteId + " não encontrado.");
				return;
			}
			
			String placa = InputValidator.readPlaca("Placa (ABC-1234): ");
			String modelo = InputValidator.readString("Modelo (ex: Fiat Uno): ");
			int ano = InputValidator.readInt("Ano: ");
			
			Veiculo v = veiculoController.createVeiculo(placa, modelo, ano, cliente);
			MenuHelper.printSuccess("Veículo cadastrado com ID: " + v.getId() + " - Placa: " + v.getPlaca());
		} catch (IllegalArgumentException e) {
			MenuHelper.printError(e.getMessage());
		} catch (RuntimeException e) {
			MenuHelper.printError("Erro ao cadastrar veículo: " + e.getMessage());
		}
	}

	// MENU 2: BUSCAR CLIENTES
	private static void menuBuscarClientes() {
		boolean voltar = false;
		while (!voltar) {
			int opcao = MenuHelper.showMenu("Buscar Clientes", 
				"Buscar por Nome", 
				"Buscar por CPF",
				"Listar Todos");
			
			switch (opcao) {
				case 1:
					buscarClientePorNome();
					break;
				case 2:
					buscarClientePorCPF();
					break;
				case 3:
					listarTodosClientes();
					break;
				case 0:
					voltar = true;
					break;
				default:
					MenuHelper.printError("Opção inválida!");
			}
		}
	}

	private static void buscarClientePorNome() {
		MenuHelper.printHeader("Buscar Cliente por Nome");
		String nome = InputValidator.readString("Nome (ou parte dele): ");
		List<Cliente> resultado = clienteController.findByNome(nome);
		
		if (resultado.isEmpty()) {
			MenuHelper.printInfo("Nenhum cliente encontrado.");
			return;
		}
		
		exibirClientesComOpcao(resultado);
	}

	private static void buscarClientePorCPF() {
		MenuHelper.printHeader("Buscar Cliente por CPF");
		String cpf = InputValidator.readCPF("CPF: ");
		Cliente cliente = clienteController.findByCpf(cpf);
		
		if (cliente == null) {
			MenuHelper.printInfo("Nenhum cliente encontrado com esse CPF.");
			return;
		}
		
		exibirClientesComOpcao(java.util.Arrays.asList(cliente));
	}

	private static void listarTodosClientes() {
		MenuHelper.printHeader("Listando Todos os Clientes");
		List<Cliente> clientes = clienteController.findAll();
		if (clientes.isEmpty()) {
			MenuHelper.printInfo("Nenhum cliente cadastrado.");
			return;
		}
		exibirClientesComOpcao(clientes);
	}

	private static void exibirClientesComOpcao(List<Cliente> clientes) {
		MenuHelper.printInfo("Clientes encontrados:");
		for (Cliente c : clientes) {
			System.out.println("  ID: " + c.getId() + " | Nome: " + c.getNome() + " | CPF: " + c.getCpf());
		}
		
		int opcao = InputValidator.readInt("Selecione um ID para ver detalhes (0=Voltar): ");
		if (opcao == 0) return;
		
		Cliente selecionado = clientes.stream()
			.filter(c -> c.getId() == opcao)
			.findFirst()
			.orElse(null);
		
		if (selecionado == null) {
			MenuHelper.printError("Cliente não encontrado.");
			return;
		}
		
		exibirDetalhesCliente(selecionado);
	}

	private static void exibirDetalhesCliente(Cliente cliente) {
		MenuHelper.printHeader("Detalhes do Cliente");
		System.out.println("ID: " + cliente.getId());
		System.out.println("Nome: " + cliente.getNome());
		System.out.println("CPF: " + cliente.getCpf());
		System.out.println("Telefone: " + cliente.getTelefone());
		
		// Listar veículos desse cliente
		List<Veiculo> veiculos = veiculoController.findByClienteId(cliente.getId());
		if (!veiculos.isEmpty()) {
			MenuHelper.printInfo("Veículos deste cliente:");
			for (Veiculo v : veiculos) {
				System.out.println("  ID: " + v.getId() + " | Placa: " + v.getPlaca() + " | " + v.getModelo() + " (" + v.getAno() + ")");
			}
		} else {
			MenuHelper.printInfo("Nenhum veículo cadastrado para este cliente.");
		}
		
		// Opção de apagar
		int resposta = InputValidator.readInt("Deseja apagar este cliente? (1=Sim, 0=Não): ");
		if (resposta == 1) {
			try {
				clienteController.deleteCliente(cliente.getId());
				MenuHelper.printSuccess("Cliente apagado com sucesso.");
			} catch (RuntimeException e) {
				MenuHelper.printError("Erro ao apagar cliente: " + e.getMessage());
			}
		}
	}

	// MENU 3: BUSCAR VEÍCULOS
	private static void menuBuscarVeiculos() {
		boolean voltar = false;
		while (!voltar) {
			int opcao = MenuHelper.showMenu("Buscar Veículos", 
				"Buscar por Placa", 
				"Listar Veículos de um Cliente");
			
			switch (opcao) {
				case 1:
					buscarVeiculoPorPlaca();
					break;
				case 2:
					listarVeiculosCliente();
					break;
				case 0:
					voltar = true;
					break;
				default:
					MenuHelper.printError("Opção inválida!");
			}
		}
	}

	private static void buscarVeiculoPorPlaca() {
		MenuHelper.printHeader("Buscar Veículo por Placa");
		String placa = InputValidator.readPlaca("Placa: ");
		Veiculo veiculo = veiculoController.findByPlaca(placa);
		
		if (veiculo == null) {
			MenuHelper.printInfo("Nenhum veículo encontrado com essa placa.");
			return;
		}
		
		exibirDetalhesVeiculo(veiculo);
	}

	private static void listarVeiculosCliente() {
		MenuHelper.printHeader("Listar Veículos de um Cliente");
		
		// Primeiro buscar o cliente
		List<Cliente> clientes = clienteController.findAll();
		if (clientes.isEmpty()) {
			MenuHelper.printError("Nenhum cliente cadastrado.");
			return;
		}
		
		MenuHelper.printInfo("Clientes disponíveis:");
		for (Cliente c : clientes) {
			System.out.println("  ID: " + c.getId() + " | " + c.getNome());
		}
		
		int clienteId = InputValidator.readInt("ID do Cliente: ");
		Cliente cliente = clientes.stream()
			.filter(c -> c.getId() == clienteId)
			.findFirst()
			.orElse(null);
		
		if (cliente == null) {
			MenuHelper.printError("Cliente não encontrado.");
			return;
		}
		
		List<Veiculo> veiculos = veiculoController.findByClienteId(clienteId);
		if (veiculos.isEmpty()) {
			MenuHelper.printInfo("Nenhum veículo cadastrado para este cliente.");
			return;
		}
		
		MenuHelper.printInfo("Veículos de " + cliente.getNome() + ":");
		for (Veiculo v : veiculos) {
			System.out.println("  ID: " + v.getId() + " | Placa: " + v.getPlaca() + " | " + v.getModelo() + " (" + v.getAno() + ")");
		}
		
		// Opção de selecionar veículo para apagar ou transferir
		int opcao = InputValidator.readInt("Selecione ID do veículo (0=Voltar): ");
		if (opcao == 0) return;
		
		Veiculo selecionado = veiculos.stream()
			.filter(v -> v.getId() == opcao)
			.findFirst()
			.orElse(null);
		
		if (selecionado == null) {
			MenuHelper.printError("Veículo não encontrado.");
			return;
		}
		
		exibirDetalhesVeiculo(selecionado);
	}

	private static void exibirDetalhesVeiculo(Veiculo veiculo) {
		MenuHelper.printHeader("Detalhes do Veículo");
		System.out.println("ID: " + veiculo.getId());
		System.out.println("Placa: " + veiculo.getPlaca());
		System.out.println("Modelo: " + veiculo.getModelo());
		System.out.println("Ano: " + veiculo.getAno());
		if (veiculo.getCliente() != null) {
			System.out.println("Proprietário: " + veiculo.getCliente().getNome());
		}
		
		boolean continuar = true;
		while (continuar) {
			int opcao = InputValidator.readInt("(1) Apagar | (2) Transferir | (0) Voltar: ");
			switch (opcao) {
				case 1:
					try {
						int confirmar = InputValidator.readInt("Tem certeza? (1=Sim, 0=Não): ");
						if (confirmar == 1) {
							veiculoController.deleteVeiculo(veiculo.getId());
							MenuHelper.printSuccess("Veículo apagado com sucesso.");
							continuar = false;
						}
					} catch (RuntimeException e) {
						MenuHelper.printError("Erro ao apagar: " + e.getMessage());
					}
					break;
				case 2:
					transferirVeiculoParaCliente(veiculo);
					continuar = false;
					break;
				case 0:
					continuar = false;
					break;
				default:
					MenuHelper.printError("Opção inválida!");
			}
		}
	}

	private static void transferirVeiculoParaCliente(Veiculo veiculo) {
		MenuHelper.printHeader("Transferir Veículo para Outro Cliente");
		
		List<Cliente> clientes = clienteController.findAll();
		MenuHelper.printInfo("Clientes disponíveis:");
		for (Cliente c : clientes) {
			System.out.println("  ID: " + c.getId() + " | " + c.getNome());
		}
		
		int novoClienteId = InputValidator.readInt("ID do novo cliente: ");
		Cliente novoCliente = clientes.stream()
			.filter(c -> c.getId() == novoClienteId)
			.findFirst()
			.orElse(null);
		
		if (novoCliente == null) {
			MenuHelper.printError("Cliente não encontrado.");
			return;
		}
		
		try {
			veiculoController.transferirProprietario(veiculo, novoCliente);
			MenuHelper.printSuccess("Veículo transferido para " + novoCliente.getNome() + " com sucesso.");
		} catch (RuntimeException e) {
			MenuHelper.printError("Erro ao transferir: " + e.getMessage());
		}
	}

	// MENU 3: ORDEM DE SERVIÇO
	private static void menuOrdenServico() {
		boolean voltar = false;
		while (!voltar) {
			int opcao = MenuHelper.showMenu("Ordem de Serviço",
					"Abrir Nova Ordem de Serviço",
					"Buscar OS abertas (pausar/retomar/finalizar e adicionar itens)",
					"Histórico de OS");

			switch (opcao) {
				case 1:
					abrirOrdemServico();
					break;
				case 2:
					menuOperarOSAbertas();
					break;
				case 3:
					menuHistoricoOS();
					break;
				case 0:
					voltar = true;
					break;
				default:
					MenuHelper.printError("Opção inválida!");
			}
		}
	}
	private static void menuOperarOSAbertas() {
		MenuHelper.printHeader("Operar OS Abertas");

		List<OrdemServico> abertas = osController.findOrdensAbertas();
		if (abertas.isEmpty()) {
			MenuHelper.printInfo("Nenhuma OS aberta no momento.");
			return;
		}

		System.out.println("OS abertas:");
		for (OrdemServico os : abertas) {
			String placa = (os.getVeiculo() != null ? os.getVeiculo().getPlaca() : "N/A");
			String cliente = (os.getVeiculo() != null && os.getVeiculo().getCliente() != null ? os.getVeiculo().getCliente().getNome() : "N/A");
			System.out.println("  ID: " + os.getId()
					+ " | Placa: " + placa
					+ " | Cliente: " + cliente
					+ " | Status: " + os.getStatus());
		}

		int id = InputValidator.readInt("Selecione o ID da OS (0=Voltar): ");
		if (id == 0) return;

		OrdemServico os = osController.findById(id);
		if (os == null) {
			MenuHelper.printError("OS não encontrada.");
			return;
		}

		boolean voltar = false;
		while (!voltar) {

			os = osController.findById(id);

			int opcao = MenuHelper.showMenu(
					"OS ID: " + os.getId() + " | Status: " + os.getStatus(),
					"Adicionar serviço",
					"Pausar manutenção",
					"Retomar manutenção",
					"Finalizar OS",
					"Ver detalhes",
					"Voltar"
			);



			switch (opcao) {
				case 1:
					adicionarServicoEmOS(os.getId());
					break;
				case 2:
					try {
						osController.pausarManutencao(os.getId());
						MenuHelper.printSuccess("OS pausada.");
					} catch (Exception e) {
						MenuHelper.printError(e.getMessage());
					}
					break;
				case 3:
					try {
						osController.retomarManutencao(os.getId());
						MenuHelper.printSuccess("OS retomada.");
					} catch (Exception e) {
						MenuHelper.printError(e.getMessage());
					}
					break;
				case 4:
					try {
						osController.fecharManutencao(os.getId());
						os = osController.findById(os.getId());

						System.out.println("Finalizada! Totais:");
						System.out.println("  Peças/Serviços: R$ " + String.format("%.2f", os.getValorServicos()));
						System.out.println("  Mão de obra:     R$ " + String.format("%.2f", os.getValorMaoObra()));
						System.out.println("  Total:           R$ " + String.format("%.2f", os.getValorTotal()));

						MenuHelper.printSuccess("OS finalizada com sucesso.");
						voltar = true;
					} catch (Exception e) {
						MenuHelper.printError(e.getMessage());
					}
					break;
				case 5:
					exibirDetalhesOS(os);
					break;
				case 6:
					voltar = true;
					break;
				default:
					MenuHelper.printError("Opção inválida!");
			}
		}
	}

	private static void adicionarServicoEmOS(int ordemId) {
		try {
			String descServ = InputValidator.readTexto("Descrição do serviço: ");
			double valor = InputValidator.readDouble("Valor do serviço (R$): ");
			osController.adicionarServico(ordemId, descServ, valor);
			MenuHelper.printSuccess("Serviço adicionado com sucesso.");
		} catch (Exception e) {
			MenuHelper.printError(e.getMessage());
		}
	}

	private static void exibirDetalhesOS(OrdemServico os) {
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.println("ID: " + os.getId());
		System.out.println("Placa: " + (os.getVeiculo() != null ? os.getVeiculo().getPlaca() : "N/A"));
		System.out.println("Cliente: " + (os.getVeiculo() != null && os.getVeiculo().getCliente() != null ? os.getVeiculo().getCliente().getNome() : "N/A"));
		System.out.println("Descrição: " + os.getDescricao());
		System.out.println("Status: " + os.getStatus());
		System.out.println("Data abertura: " + os.getDataAbertura());
		System.out.println("Entrada manutenção: " + os.getEntradaManutencao());
		System.out.println("Saída manutenção: " + os.getSaidaManutencao());
		System.out.println("Mão de obra: R$ " + String.format("%.2f", os.getValorMaoObra()));
		System.out.println("Serviços:    R$ " + String.format("%.2f", os.getValorServicos()));
		System.out.println("Total:       R$ " + String.format("%.2f", os.getValorTotal()));

		System.out.println("Serviços realizados:");
		for (var s : os.getServicos()) {
			System.out.println("  - " + s.getDescricao() + ": R$ " + String.format("%.2f", s.getValor() != null ? s.getValor() : 0.0));
		}
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
	}



	private static void abrirOrdemServico() {
		MenuHelper.printHeader("Abrir Nova Ordem de Serviço");
		try {
			// Pedir placa do veículo (obrigatório)
			String placa = InputValidator.readPlaca("Placa do Veículo (obrigatório): ");
			Veiculo veiculo = veiculoController.findByPlaca(placa);
			
			if (veiculo == null) {
				MenuHelper.printError("Veículo com placa " + placa + " não encontrado.");
				int criar = InputValidator.readInt("Deseja cadastrar um novo veículo? (1=Sim, 0=Não): ");
				if (criar == 1) {
					cadastrarVeiculo();
				}
				return;
			}

			// Pedir nome do cliente e placa (confirmação)
			MenuHelper.printInfo("Veículo encontrado:");
			System.out.println("  Placa: " + veiculo.getPlaca() + " | Modelo: " + veiculo.getModelo());
			System.out.println("  Proprietário: " + veiculo.getCliente().getNome());
			
			String descricao = InputValidator.readTexto("Descrição da Atividade: ");
			
			OrdemServico os = osController.abrirOrdemParaPlaca(placa, descricao);
			MenuHelper.printSuccess("Ordem de Serviço aberta com ID: " + os.getId());
			
			// Menu de serviços
			adicionarServicosOS(os);
			
			// Iniciar manutenção
			int iniciar = InputValidator.readInt("Iniciar manutenção? (1=Sim, 0=Não): ");
			if (iniciar == 1) {
				osController.iniciarManutencao(os.getId());
				MenuHelper.printSuccess("Manutenção iniciada em: " + LocalDateTime.now());
				
				// Fechar manutenção
				int fechar = InputValidator.readInt("Fechar manutenção? (1=Sim, 0=Não): ");
				if (fechar == 1) {
					osController.fecharManutencao(os.getId());
					os = osController.findById(os.getId());
					MenuHelper.printSuccess("Manutenção fechada. Valores calculados:");
					System.out.println("  Mão de obra: R$ " + String.format("%.2f", os.getValorMaoObra()));
					System.out.println("  Serviços: R$ " + String.format("%.2f", os.getValorServicos()));
					System.out.println("  Total: R$ " + String.format("%.2f", os.getValorTotal()));
				}
			}
		} catch (IllegalArgumentException e) {
			MenuHelper.printError(e.getMessage());
		} catch (RuntimeException e) {
			MenuHelper.printError("Erro ao processar ordem: " + e.getMessage());
		}
	}

	private static void adicionarServicosOS(OrdemServico os) {
		boolean adicionarMais = true;
		while (adicionarMais) {
			int resp = InputValidator.readInt("Adicionar serviço? (1=Sim, 0=Não): ");
			if (resp == 1) {
				String descServ = InputValidator.readTexto("Descrição do serviço: ");
				double valor = InputValidator.readDouble("Valor do serviço: R$ ");
				osController.adicionarServico(os.getId(), descServ, valor);
				MenuHelper.printSuccess("Serviço adicionado.");
				os = osController.findById(os.getId()); // refresh
			} else {
				adicionarMais = false;
			}
		}
	}

	private static void menuHistoricoOS() {
		boolean voltar = false;
		while (!voltar) {
			int opcao = MenuHelper.showMenu("Histórico de OS", 
				"Buscar por Placa", 
				"Buscar por Período",
				"Buscar por Nome de Cliente",
				"Buscar por CPF de Cliente");
			
			switch (opcao) {
				case 1:
					historicoOSPorPlaca();
					break;
				case 2:
					historicoOSPorPeriodo();
					break;
				case 3:
					historicoOSPorNome();
					break;
				case 4:
					historicoOSPorCPF();
					break;
				case 0:
					voltar = true;
					break;
				default:
					MenuHelper.printError("Opção inválida!");
			}
		}
	}

	private static void historicoOSPorPlaca() {
		MenuHelper.printHeader("Histórico de OS - Buscar por Placa");
		String placa = InputValidator.readPlaca("Placa do Veículo: ");
		List<OrdemServico> ordens = osController.findByPlaca(placa);
		
		if (ordens.isEmpty()) {
			MenuHelper.printInfo("Nenhuma ordem encontrada para a placa " + placa + ".");
			return;
		}
		
		exibirHistoricoOS(ordens, "Histórico para placa: " + placa);
	}

	private static void historicoOSPorPeriodo() {
		MenuHelper.printHeader("Histórico de OS - Buscar por Período");
		try {
			LocalDate dataIni = LocalDate.parse(InputValidator.readString("Data inicial (YYYY-MM-DD): "));
			LocalDate dataFim = LocalDate.parse(InputValidator.readString("Data final (YYYY-MM-DD): "));
			
			List<OrdemServico> ordens = osController.findByPeriod(Date.valueOf(dataIni), Date.valueOf(dataFim));
			
			if (ordens.isEmpty()) {
				MenuHelper.printInfo("Nenhuma ordem encontrada no período informado.");
				return;
			}
			
			exibirHistoricoOS(ordens, "Histórico de " + dataIni + " a " + dataFim);
		} catch (Exception e) {
			MenuHelper.printError("Data inválida. Use formato YYYY-MM-DD.");
		}
	}

	private static void historicoOSPorNome() {
		MenuHelper.printHeader("Histórico de OS - Buscar por Nome de Cliente");
		String nome = InputValidator.readString("Nome do Cliente (ou parte dele): ");
		List<Cliente> clientes = clienteController.findByNome(nome);
		
		if (clientes.isEmpty()) {
			MenuHelper.printInfo("Nenhum cliente encontrado com esse nome.");
			return;
		}
		
		List<OrdemServico> todasOrdens = new java.util.ArrayList<>();
		for (Cliente c : clientes) {
			todasOrdens.addAll(osController.findByClienteId(c.getId()));
		}
		
		if (todasOrdens.isEmpty()) {
			MenuHelper.printInfo("Nenhuma ordem encontrada para esses clientes.");
			return;
		}
		
		exibirHistoricoOS(todasOrdens, "Histórico para clientes com nome contendo: " + nome);
	}

	private static void historicoOSPorCPF() {
		MenuHelper.printHeader("Histórico de OS - Buscar por CPF de Cliente");
		String cpf = InputValidator.readCPF("CPF do Cliente: ");
		Cliente cliente = clienteController.findByCpf(cpf);
		
		if (cliente == null) {
			MenuHelper.printInfo("Nenhum cliente encontrado com esse CPF.");
			return;
		}
		
		List<OrdemServico> ordens = osController.findByClienteId(cliente.getId());
		
		if (ordens.isEmpty()) {
			MenuHelper.printInfo("Nenhuma ordem encontrada para esse cliente.");
			return;
		}
		
		exibirHistoricoOS(ordens, "Histórico para cliente: " + cliente.getNome());
	}

	private static void exibirHistoricoOS(List<OrdemServico> ordens, String titulo) {
		MenuHelper.printHeader(titulo);
		double totalGeral = 0;
		for (OrdemServico os : ordens) {
			System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
			System.out.println("ID: " + os.getId());
			System.out.println("Placa: " + (os.getVeiculo() != null ? os.getVeiculo().getPlaca() : "N/A"));
			System.out.println("Cliente: " + (os.getVeiculo() != null && os.getVeiculo().getCliente() != null ? os.getVeiculo().getCliente().getNome() : "N/A"));
			System.out.println("Descrição: " + os.getDescricao());
			System.out.println("Status: " + os.getStatus());
			System.out.println("Data Abertura: " + os.getDataAbertura());
			System.out.println("Entrada Manutenção: " + os.getEntradaManutencao());
			System.out.println("Saída Manutenção: " + os.getSaidaManutencao());
			System.out.println("Mão de Obra: R$ " + String.format("%.2f", os.getValorMaoObra()));
			System.out.println("Serviços: R$ " + String.format("%.2f", os.getValorServicos()));
			System.out.println("Total: R$ " + String.format("%.2f", os.getValorTotal()));
			System.out.println("Serviços realizados:");
			for (var s : os.getServicos()) {
				System.out.println("  - " + s.getDescricao() + ": R$ " + String.format("%.2f", s.getValor() != null ? s.getValor() : 0.0));
			}
			totalGeral += os.getValorTotal();
		}
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.println("Total Geral: R$ " + String.format("%.2f", totalGeral));
		MenuHelper.printSeparator();
	}
}
