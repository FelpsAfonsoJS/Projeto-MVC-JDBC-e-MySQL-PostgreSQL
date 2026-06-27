package com.escola.model;

import java.time.LocalDate;

public class Matricula {

    private int id;
    private int idAluno;
    private int idCurso;
    private LocalDate dataMatricula;
    private double valor;

    public Matricula() {}

    public Matricula(int idAluno, int idCurso, LocalDate dataMatricula, double valor) {
        this.idAluno = idAluno;
        this.idCurso = idCurso;
        this.dataMatricula = dataMatricula;
        this.valor = valor;
    }

    public Matricula(int id, int idAluno, int idCurso, LocalDate dataMatricula, double valor) {
        this.id = id;
        this.idAluno = idAluno;
        this.idCurso = idCurso;
        this.dataMatricula = dataMatricula;
        this.valor = valor;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdAluno() { return idAluno; }
    public void setIdAluno(int idAluno) { this.idAluno = idAluno; }

    public int getIdCurso() { return idCurso; }
    public void setIdCurso(int idCurso) { this.idCurso = idCurso; }

    public LocalDate getDataMatricula() { return dataMatricula; }
    public void setDataMatricula(LocalDate dataMatricula) { this.dataMatricula = dataMatricula; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    @Override
    public String toString() {
        return "Matricula{id=" + id + ", idAluno=" + idAluno + ", idCurso=" + idCurso +
                ", dataMatricula=" + dataMatricula + ", valor=" + valor + "}";
    }
}
