package com.clinicavet.controller;

import com.clinicavet.model.Tutor;
import com.clinicavet.service.TutorService;

import java.util.List;


public class TutorController {

    private final TutorService tutorService;

    public TutorController() {
        this.tutorService = new TutorService();
    }

    public Tutor cadastrarTutor(String nome, String endereco, String telefone) {
        Tutor tutor = tutorService.cadastrar(nome, endereco, telefone);
        System.out.println("[Tutor cadastrado] " + tutor);
        return tutor;
    }

    public Tutor buscarTutor(Integer id) {
        Tutor tutor = tutorService.buscarPorId(id);
        System.out.println("[Tutor encontrado] " + tutor);
        return tutor;
    }

    public List<Tutor> listarTutores() {
        List<Tutor> tutores = tutorService.listarTodos();
        System.out.println("[Tutores cadastrados]");
        tutores.forEach(System.out::println);
        return tutores;
    }

    public Tutor atualizarTutor(Integer id, String nome, String endereco, String telefone) {
        Tutor tutor = tutorService.atualizar(id, nome, endereco, telefone);
        System.out.println("[Tutor atualizado] " + tutor);
        return tutor;
    }

    public void deletarTutor(Integer id) {
        tutorService.deletar(id);
        System.out.println("[Tutor removido] id=" + id);
    }
}
