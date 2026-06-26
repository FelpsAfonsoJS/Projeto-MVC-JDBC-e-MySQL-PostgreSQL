package com.clinicavet.controller;

import com.clinicavet.model.Consulta;
import com.clinicavet.service.ConsultaService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController() {
        this.consultaService = new ConsultaService();
    }

    public Consulta registrarConsulta(Integer idAnimal, LocalDate data, String motivo, BigDecimal valor) {
        Consulta consulta = consultaService.registrar(idAnimal, data, motivo, valor);
        System.out.println("[Consulta registrada] " + consulta);
        return consulta;
    }

    public Consulta buscarConsulta(Integer id) {
        Consulta consulta = consultaService.buscarPorId(id);
        System.out.println("[Consulta encontrada] " + consulta);
        return consulta;
    }

    public List<Consulta> listarConsultas() {
        List<Consulta> consultas = consultaService.listarTodos();
        System.out.println("[Consultas registradas]");
        consultas.forEach(System.out::println);
        return consultas;
    }

    public List<Consulta> listarHistoricoDoAnimal(Integer idAnimal) {
        List<Consulta> consultas = consultaService.listarHistoricoPorAnimal(idAnimal);
        System.out.println("[Histórico de consultas do animal id=" + idAnimal + "]");
        consultas.forEach(System.out::println);
        return consultas;
    }

    public Consulta atualizarConsulta(Integer id, Integer idAnimal, LocalDate data, String motivo, BigDecimal valor) {
        Consulta consulta = consultaService.atualizar(id, idAnimal, data, motivo, valor);
        System.out.println("[Consulta atualizada] " + consulta);
        return consulta;
    }

    public void deletarConsulta(Integer id) {
        consultaService.deletar(id);
        System.out.println("[Consulta removida] id=" + id);
    }
}
