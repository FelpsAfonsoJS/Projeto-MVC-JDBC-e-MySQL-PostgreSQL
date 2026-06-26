package com.clinicavet.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Conexao {

    private static final String HOST = "localhost";
    private static final String PORTA = "3307";
    private static final String BANCO = "clinica_veterinaria";
    private static final String USUARIO = "root";
    private static final String SENHA = "";

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORTA + "/" + BANCO +
            "?useTimezone=true&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";

    private Conexao() {
    }

    public static Connection conectar() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }

    public static void fechar(Connection conexao) {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}
