package com.clinicavet.service;

import com.clinicavet.model.Consulta;
import com.clinicavet.repository.AnimalRepository;
import com.clinicavet.repository.ConsultaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final AnimalRepository animalRepository;

    public ConsultaService() {
        this.consultaRepository = new ConsultaRepository();
        this.animalRepository = new AnimalRepository();
    }

    public Consulta registrar(Integer idAnimal, LocalDate data, String motivo, BigDecimal valor) {
        validarDadosObrigatorios(idAnimal, data, motivo, valor);

        if (!animalRepository.existePorId(idAnimal)) {
            throw new IllegalArgumentException(
                    "Não é possível registrar a consulta: animal com id " + idAnimal + " não está cadastrado.");
        }

        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor da consulta não pode ser negativo.");
        }

        Consulta consulta = new Consulta(idAnimal, data, motivo.trim(), valor);
        return consultaRepository.salvar(consulta);
    }

    public Consulta buscarPorId(Integer id) {
        Consulta consulta = consultaRepository.buscarPorId(id);
        if (consulta == null) {
            throw new IllegalArgumentException("Consulta não encontrada para o id: " + id);
        }
        return consulta;
    }

    public List<Consulta> listarTodos() {
        return consultaRepository.listarTodos();
    }

    /**
     * Histórico de atendimentos de um animal específico.
     * Atende ao requisito: "consultar todas as consultas de um animal específico".
     */
    public List<Consulta> listarHistoricoPorAnimal(Integer idAnimal) {
        if (!animalRepository.existePorId(idAnimal)) {
            throw new IllegalArgumentException("Animal com id " + idAnimal + " não está cadastrado.");
        }
        return consultaRepository.listarPorAnimal(idAnimal);
    }

    public Consulta atualizar(Integer id, Integer idAnimal, LocalDate data, String motivo, BigDecimal valor) {
        validarDadosObrigatorios(idAnimal, data, motivo, valor);

        if (!animalRepository.existePorId(idAnimal)) {
            throw new IllegalArgumentException("Animal com id " + idAnimal + " não está cadastrado.");
        }
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor da consulta não pode ser negativo.");
        }

        Consulta existente = buscarPorId(id);
        existente.setIdAnimal(idAnimal);
        existente.setData(data);
        existente.setMotivo(motivo.trim());
        existente.setValor(valor);

        consultaRepository.atualizar(existente);
        return existente;
    }

    public void deletar(Integer id) {
        buscarPorId(id);
        consultaRepository.deletar(id);
    }

    private void validarDadosObrigatorios(Integer idAnimal, LocalDate data, String motivo, BigDecimal valor) {
        if (idAnimal == null) {
            throw new IllegalArgumentException("O id do animal é obrigatório para registrar a consulta.");
        }
        if (data == null) {
            throw new IllegalArgumentException("A data da consulta é obrigatória.");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("O motivo do atendimento é obrigatório.");
        }
        if (valor == null) {
            throw new IllegalArgumentException("O valor da consulta é obrigatório.");
        }
    }
}
