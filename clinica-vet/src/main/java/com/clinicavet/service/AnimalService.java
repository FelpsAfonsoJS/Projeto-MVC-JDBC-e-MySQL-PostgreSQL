package com.clinicavet.service;

import com.clinicavet.model.Animal;
import com.clinicavet.repository.AnimalRepository;
import com.clinicavet.repository.TutorRepository;

import java.util.List;

public class AnimalService {

    private final AnimalRepository animalRepository;
    private final TutorRepository tutorRepository;

    public AnimalService() {
        this.animalRepository = new AnimalRepository();
        this.tutorRepository = new TutorRepository();
    }

    public Animal cadastrar(String nome, String especie, String raca, Integer idTutor) {
        validarDadosObrigatorios(nome, especie, idTutor);

        if (!tutorRepository.existePorId(idTutor)) {
            throw new IllegalArgumentException(
                    "Não é possível cadastrar o animal: tutor com id " + idTutor + " não existe.");
        }

        Animal animal = new Animal(nome.trim(), especie.trim(), raca == null ? null : raca.trim(), idTutor);
        return animalRepository.salvar(animal);
    }

    public Animal buscarPorId(Integer id) {
        Animal animal = animalRepository.buscarPorId(id);
        if (animal == null) {
            throw new IllegalArgumentException("Animal não encontrado para o id: " + id);
        }
        return animal;
    }

    public List<Animal> listarTodos() {
        return animalRepository.listarTodos();
    }

    /**
     * Lista todos os animais de um tutor.
     * Atende ao requisito: "se um tutor me ligar novamente, eu quero ver
     * todos os animais que ele tem no sistema".
     */
    public List<Animal> listarPorTutor(Integer idTutor) {
        if (!tutorRepository.existePorId(idTutor)) {
            throw new IllegalArgumentException("Tutor com id " + idTutor + " não existe.");
        }
        return animalRepository.listarPorTutor(idTutor);
    }

    public Animal atualizar(Integer id, String nome, String especie, String raca, Integer idTutor) {
        validarDadosObrigatorios(nome, especie, idTutor);

        if (!tutorRepository.existePorId(idTutor)) {
            throw new IllegalArgumentException("Tutor com id " + idTutor + " não existe.");
        }

        Animal existente = buscarPorId(id);
        existente.setNome(nome.trim());
        existente.setEspecie(especie.trim());
        existente.setRaca(raca == null ? null : raca.trim());
        existente.setIdTutor(idTutor);

        animalRepository.atualizar(existente);
        return existente;
    }

    public void deletar(Integer id) {
        buscarPorId(id);
        animalRepository.deletar(id);
    }

    public boolean existePorId(Integer id) {
        return animalRepository.existePorId(id);
    }

    private void validarDadosObrigatorios(String nome, String especie, Integer idTutor) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do animal é obrigatório.");
        }
        if (especie == null || especie.trim().isEmpty()) {
            throw new IllegalArgumentException("A espécie do animal é obrigatória.");
        }
        if (idTutor == null) {
            throw new IllegalArgumentException("O animal precisa estar vinculado a um tutor.");
        }
    }
}
