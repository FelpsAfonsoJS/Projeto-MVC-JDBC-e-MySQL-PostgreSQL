package controller;

import model.OrdemServico;
import model.Servico;
import model.StatusOS;
import model.Veiculo;
import repository.OrdemServicoRepository;
import repository.ServicoRepository;
import repository.VeiculoRepository;

import java.util.ArrayList;
import java.util.List;

public class OrdemServicoController {

    private final OrdemServicoRepository dao = new OrdemServicoRepository();
    private final VeiculoRepository veiculoDAO = new VeiculoRepository();
    private final ServicoRepository servicoDAO = new ServicoRepository();

    public OrdemServico abrirOrdemParaPlaca(String placa, String descricao) {
        Veiculo v = veiculoDAO.findByPlaca(placa);
        if (v == null) {
            throw new IllegalArgumentException("Veículo não cadastrado: " + placa);
        }
        OrdemServico os = new OrdemServico(null, v, descricao);
        return dao.save(os);
    }

    public OrdemServico adicionarServico(int ordemId, String descricao, double valor) {
        OrdemServico os = dao.findById(ordemId);
        if (os == null) throw new IllegalArgumentException("Ordem não encontrada");

        StatusOS st = os.getStatus();
        if (!(st == StatusOS.ABERTA || st == StatusOS.PAUSADA || st == StatusOS.EM_MANUTENCAO)) {
            throw new IllegalStateException("Não é possível adicionar serviços quando status é: " + st);
        }

        Servico s = new Servico(null, descricao, valor);
        s = servicoDAO.save(s);


        servicoDAO.assignToOrdem(s.getId(), os.getId());

        os.addServico(s);
        dao.update(os);
        return os;
    }

    public OrdemServico iniciarManutencao(int ordemId) {
        OrdemServico os = dao.findById(ordemId);
        if (os == null) throw new IllegalArgumentException("Ordem não encontrada");
        os.iniciarManutencao();
        return dao.update(os);
    }

    public OrdemServico pausarManutencao(int ordemId) {
        OrdemServico os = dao.findById(ordemId);
        if (os == null) throw new IllegalArgumentException("Ordem não encontrada");
        os.pausarManutencao();
        return dao.update(os);
    }

    public OrdemServico retomarManutencao(int ordemId) {
        OrdemServico os = dao.findById(ordemId);
        if (os == null) throw new IllegalArgumentException("Ordem não encontrada");
        os.retomarManutencao();
        return dao.update(os);
    }

    public OrdemServico fecharManutencao(int ordemId) {
        OrdemServico os = dao.findById(ordemId);
        if (os == null) throw new IllegalArgumentException("Ordem não encontrada");
        os.fecharManutencao();
        return dao.update(os);
    }

    public OrdemServico findById(int id) {
        return dao.findById(id);
    }

    public List<OrdemServico> findByPlaca(String placa) {
        Veiculo v = veiculoDAO.findByPlaca(placa);
        if (v == null) return new ArrayList<>();
        return dao.findByVeiculoId(v.getId());
    }

    public List<OrdemServico> findAll() {
        return dao.findAll();
    }

    public List<OrdemServico> findByPeriod(java.sql.Date dataInicio, java.sql.Date dataFim) {
        return dao.findByPeriod(dataInicio, dataFim);
    }

    public List<OrdemServico> findByClienteId(int clienteId) {
        List<OrdemServico> resultado = new ArrayList<>();
        List<Veiculo> veiculos = veiculoDAO.findByClienteId(clienteId);
        for (Veiculo v : veiculos) {
            resultado.addAll(dao.findByVeiculoId(v.getId()));
        }
        return resultado;
    }

    // usado pelo menu "Buscar OS abertas"
    public List<OrdemServico> findOrdensAbertas() {
        return dao.findOrdensAbertas();
    }
}
