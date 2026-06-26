package com.clinicavet.controller;

import com.clinicavet.model.Animal;
import com.clinicavet.service.AnimalService;

import java.util.List;

public class AnimalController {

    private final AnimalService animalService;

    public AnimalController() {
        this.animalService = new AnimalService();
    }

    public Animal cadastrarAnimal(String nome, String especie, String raca, Integer idTutor) {
        Animal animal = animalService.cadastrar(nome, especie, raca, idTutor);
        System.out.println("[Animal cadastrado] " + animal);
        return animal;
    }

    public Animal buscarAnimal(Integer id) {
        Animal animal = animalService.buscarPorId(id);
        System.out.println("[Animal encontrado] " + animal);
        return animal;
    }

    public List<Animal> listarAnimais() {
        List<Animal> animais = animalService.listarTodos();
        System.out.println("[Animais cadastrados]");
        animais.forEach(System.out::println);
        return animais;
    }

    public List<Animal> listarAnimaisDoTutor(Integer idTutor) {
        List<Animal> animais = animalService.listarPorTutor(idTutor);
        System.out.println("[Animais do tutor id=" + idTutor + "]");
        animais.forEach(System.out::println);
        return animais;
    }

    public Animal atualizarAnimal(Integer id, String nome, String especie, String raca, Integer idTutor) {
        Animal animal = animalService.atualizar(id, nome, especie, raca, idTutor);
        System.out.println("[Animal atualizado] " + animal);
        return animal;
    }

    public void deletarAnimal(Integer id) {
        animalService.deletar(id);
        System.out.println("[Animal removido] id=" + id);
    }
}
