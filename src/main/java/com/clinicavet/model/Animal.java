package com.clinicavet.model;
public class Animal {

    private Integer id;
    private String nome;
    private String especie;
    private String raca;
    private Integer idTutor;

    public Animal() {
    }

    public Animal(String nome, String especie, String raca, Integer idTutor) {
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idTutor = idTutor;
    }

    public Animal(Integer id, String nome, String especie, String raca, Integer idTutor) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idTutor = idTutor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public Integer getIdTutor() {
        return idTutor;
    }

    public void setIdTutor(Integer idTutor) {
        this.idTutor = idTutor;
    }

    @Override
    public String toString() {
        return "Animal{id=" + id +
                ", nome='" + nome + '\'' +
                ", especie='" + especie + '\'' +
                ", raca='" + raca + '\'' +
                ", idTutor=" + idTutor +
                '}';
    }
}
