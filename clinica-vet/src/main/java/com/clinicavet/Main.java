package com.clinicavet;

import com.clinicavet.controller.AnimalController;
import com.clinicavet.controller.ConsultaController;
import com.clinicavet.controller.TutorController;
import com.clinicavet.model.Animal;
import com.clinicavet.model.Tutor;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        TutorController tutorController = new TutorController();
        AnimalController animalController = new AnimalController();
        ConsultaController consultaController = new ConsultaController();

        System.out.println("===== SIMULAÇÃO: SISTEMA DE CLÍNICA VETERINÁRIA =====\n");

        System.out.println("--- Passo 1: Cadastrando o tutor ---");
        Tutor tutor = tutorController.cadastrarTutor(
                "Carlos Mendes",
                "Rua das Flores, 123 - Centro",
                "(44) 99999-1234"
        );

        System.out.println("\n--- Passo 2: Cadastrando o animal vinculado ao tutor ---");
        Animal animal = animalController.cadastrarAnimal(
                "Rex",
                "Cão",
                "Labrador",
                tutor.getId()
        );

        Animal segundoAnimal = animalController.cadastrarAnimal(
                "Mimi",
                "Gato",
                "Siamês",
                tutor.getId()
        );

        System.out.println("\n--- Passo 3: Registrando a consulta do animal ---");
        consultaController.registrarConsulta(
                animal.getId(),
                LocalDate.now(),
                "Vacinação anual",
                new BigDecimal("150.00")
        );

        consultaController.registrarConsulta(
                animal.getId(),
                LocalDate.now().minusMonths(2),
                "Consulta de rotina",
                new BigDecimal("120.00")
        );

        System.out.println("\n--- Passo 4: Consultando o histórico de atendimentos do animal Rex ---");
        consultaController.listarHistoricoDoAnimal(animal.getId());

        System.out.println("\n--- Passo 5: Listando todos os animais do tutor Carlos Mendes ---");
        animalController.listarAnimaisDoTutor(tutor.getId());

        System.out.println("\n--- Passo 6: Testando regras de negócio ---");

        System.out.println("\nTentando registrar consulta para animal inexistente (id = 9999):");
        try {
            consultaController.registrarConsulta(
                    9999,
                    LocalDate.now(),
                    "Consulta inválida",
                    new BigDecimal("50.00")
            );
        } catch (IllegalArgumentException e) {
            System.out.println("[Erro esperado] " + e.getMessage());
        }

        System.out.println("\nTentando registrar consulta com valor negativo:");
        try {
            consultaController.registrarConsulta(
                    animal.getId(),
                    LocalDate.now(),
                    "Consulta com valor inválido",
                    new BigDecimal("-30.00")
            );
        } catch (IllegalArgumentException e) {
            System.out.println("[Erro esperado] " + e.getMessage());
        }

        System.out.println("\n===== FIM DA SIMULAÇÃO =====");
    }
}
