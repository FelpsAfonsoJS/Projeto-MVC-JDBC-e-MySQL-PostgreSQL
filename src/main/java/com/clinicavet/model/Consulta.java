package com.clinicavet.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Consulta {

    private Integer id;
    private Integer idAnimal;
    private LocalDate data;
    private String motivo;
    private BigDecimal valor;

    public Consulta() {
    }

    public Consulta(Integer idAnimal, LocalDate data, String motivo, BigDecimal valor) {
        this.idAnimal = idAnimal;
        this.data = data;
        this.motivo = motivo;
        this.valor = valor;
    }

    public Consulta(Integer id, Integer idAnimal, LocalDate data, String motivo, BigDecimal valor) {
        this.id = id;
        this.idAnimal = idAnimal;
        this.data = data;
        this.motivo = motivo;
        this.valor = valor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(Integer idAnimal) {
        this.idAnimal = idAnimal;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Consulta{id=" + id +
                ", idAnimal=" + idAnimal +
                ", data=" + data +
                ", motivo='" + motivo + '\'' +
                ", valor=" + valor +
                '}';
    }
}
