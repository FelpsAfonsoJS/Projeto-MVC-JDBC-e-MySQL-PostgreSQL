package com.clinicavet.service;

import com.clinicavet.model.Tutor;
import com.clinicavet.repository.TutorRepository;

import java.util.List;

public class TutorService {

    private final TutorRepository tutorRepository;

    public TutorService() {
        this.tutorRepository = new TutorRepository();
    }

    public Tutor cadastrar(String nome, String endereco, String telefone) {
        validarDadosObrigatorios(nome, endereco, telefone);

        Tutor tutor = new Tutor(nome.trim(), endereco.trim(), telefone.trim());
        return tutorRepository.salvar(tutor);
    }

    public Tutor buscarPorId(Integer id) {
        Tutor tutor = tutorRepository.buscarPorId(id);
        if (tutor == null) {
            throw new IllegalArgumentException("Tutor não encontrado para o id: " + id);
        }
        return tutor;
    }

    public List<Tutor> listarTodos() {
        return tutorRepository.listarTodos();
    }

    public Tutor atualizar(Integer id, String nome, String endereco, String telefone) {
        validarDadosObrigatorios(nome, endereco, telefone);

        Tutor existente = buscarPorId(id);
        existente.setNome(nome.trim());
        existente.setEndereco(endereco.trim());
        existente.setTelefone(telefone.trim());

        tutorRepository.atualizar(existente);
        return existente;
    }

    public void deletar(Integer id) {
        buscarPorId(id); // garante que existe antes de deletar
        tutorRepository.deletar(id);
    }

    public boolean existePorId(Integer id) {
        return tutorRepository.existePorId(id);
    }

    private void validarDadosObrigatorios(String nome, String endereco, String telefone) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do tutor é obrigatório.");
        }
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException("O endereço do tutor é obrigatório.");
        }
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("O telefone do tutor é obrigatório.");
        }
    }
}
