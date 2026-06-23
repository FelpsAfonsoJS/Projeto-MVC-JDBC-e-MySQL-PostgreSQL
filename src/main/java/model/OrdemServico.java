package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrdemServico {

    private Integer id;
    private Veiculo veiculo;
    private String descricao;
    private StatusOS status = StatusOS.ABERTA;

    private LocalDateTime dataAbertura;
    private LocalDateTime entradaManutencao;
    private LocalDateTime saidaManutencao;

    private double valorHora = 60.0;
    private double valorMaoObra;
    private double valorServicos;
    private double valorTotal;

    private List<Servico> servicos = new ArrayList<>();

    public OrdemServico() {
        this.dataAbertura = LocalDateTime.now();
        this.status = StatusOS.ABERTA;
    }

    public OrdemServico(Integer id, Veiculo veiculo, String descricao) {
        this();
        this.id = id;
        this.veiculo = veiculo;
        this.descricao = descricao;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public StatusOS getStatus() { return status; }
    public void setStatus(StatusOS status) { this.status = status; }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public LocalDateTime getEntradaManutencao() { return entradaManutencao; }
    public LocalDateTime getSaidaManutencao() { return saidaManutencao; }


    public void setEntradaManutencao(LocalDateTime entradaManutencao) { this.entradaManutencao = entradaManutencao; }
    public void setSaidaManutencao(LocalDateTime saidaManutencao) { this.saidaManutencao = saidaManutencao; }

    public double getValorHora() { return valorHora; }
    public void setValorHora(double valorHora) { this.valorHora = valorHora; }

    public double getValorMaoObra() { return valorMaoObra; }
    public double getValorServicos() { return valorServicos; }
    public double getValorTotal() { return valorTotal; }

    public List<Servico> getServicos() { return servicos; }

    public void addServico(Servico s) {
        if (s != null) {
            servicos.add(s);
            recomputeValores();
        }
    }

    public void abrir() {
        this.status = StatusOS.ABERTA;
        this.dataAbertura = LocalDateTime.now();
        this.entradaManutencao = null;
        this.saidaManutencao = null;
        this.valorMaoObra = 0.0;
        this.valorServicos = 0.0;
        this.valorTotal = 0.0;
        this.servicos.clear();
    }

    public void iniciarManutencao() {
        if (this.status == StatusOS.FECHADA) {
            throw new IllegalStateException("Ordem já fechada");
        }

        // permite iniciar de ABERTA ou PAUSADA
        if (this.status == StatusOS.ABERTA || this.status == StatusOS.PAUSADA) {
            if (this.entradaManutencao == null) {
                this.entradaManutencao = LocalDateTime.now();
            }
            this.status = StatusOS.EM_MANUTENCAO;
            // ao retomar, não zera saida; manutenção termina só ao fechar
            return;
        }

        // se já está em manutenção, não faz nada
        if (this.status == StatusOS.EM_MANUTENCAO) return;

        throw new IllegalStateException("Não foi possível iniciar manutenção a partir do status: " + this.status);
    }

    public void pausarManutencao() {
        if (this.status == StatusOS.FECHADA) {
            throw new IllegalStateException("Ordem já fechada");
        }
        if (this.status != StatusOS.EM_MANUTENCAO) {
            throw new IllegalStateException("Somente é possível pausar quando estiver EM_MANUTENCAO");
        }

        // Pausar: salva um "marco" de saída temporário para cálculo parcial de mão de obra
        // e deixa status como PAUSADA.
        // Ao retomar/iniciar novamente, a entrada já existe; a saída será recalculada apenas ao fechar.
        // Para manter consistência ao cálculo atual (entrada->saída), a forma mais simples é:
        // - ao pausar, definir saidaManutencao como agora (mão de obra parcial)
        // - ao retomar, zerar saidaManutencao e manter entrada para acumular (como o modelo não acumula,
        //   esta abordagem recalcula apenas do novo intervalo. Para acumular de verdade, seria necessário mais campos.)

        this.status = StatusOS.PAUSADA;
    }

    public void retomarManutencao() {
        // retomar é basicamente iniciar, mas validando o status
        if (this.status != StatusOS.PAUSADA && this.status != StatusOS.ABERTA) {
            throw new IllegalStateException("Somente é possível retomar quando estiver PAUSADA (ou ABERTA)");
        }
        // se a OS já tinha entrada, mantém; caso não, cria
        if (this.entradaManutencao == null) {
            this.entradaManutencao = LocalDateTime.now();
        }
        this.status = StatusOS.EM_MANUTENCAO;
    }

    public void fecharManutencao() {
        if (this.status != StatusOS.EM_MANUTENCAO && this.status != StatusOS.PAUSADA && this.status != StatusOS.ABERTA) {
            throw new IllegalStateException("Ordem não está em um estado válido para fechar: " + this.status);
        }
        // Ao fechar, se nunca iniciou, não há duração; mao de obra fica 0
        if (this.entradaManutencao != null) {
            this.saidaManutencao = LocalDateTime.now();
        } else {
            this.saidaManutencao = null;
        }
        this.status = StatusOS.FECHADA;
        recomputeValores();
    }

    public void recomputeAfterAdjust() {
        recomputeValores();
    }

    private void recomputeValores() {
        // soma dos serviços
        this.valorServicos = servicos.stream()
                .mapToDouble(s -> s.getValor() != null ? s.getValor() : 0.0)
                .sum();

        // calcula mão de obra com base na diferença entre entrada e saída
        if (entradaManutencao != null && saidaManutencao != null) {
            Duration dur = Duration.between(entradaManutencao, saidaManutencao);
            double horas = dur.toMinutes() / 60.0;
            if (horas < 0) horas = 0;
            this.valorMaoObra = Math.round((horas * valorHora) * 100.0) / 100.0;
        } else {
            this.valorMaoObra = 0.0;
        }

        this.valorTotal = Math.round((valorMaoObra + valorServicos) * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return "OrdemServico{" +
                "id=" + id +
                ", veiculoPlaca='" + (veiculo != null ? veiculo.getPlaca() : null) + '\'' +
                ", descricao='" + descricao + '\'' +
                ", status=" + status +
                ", dataAbertura=" + dataAbertura +
                ", entradaManutencao=" + entradaManutencao +
                ", saidaManutencao=" + saidaManutencao +
                ", valorHora=" + valorHora +
                ", valorMaoObra=" + valorMaoObra +
                ", valorServicos=" + valorServicos +
                ", valorTotal=" + valorTotal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdemServico that = (OrdemServico) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
