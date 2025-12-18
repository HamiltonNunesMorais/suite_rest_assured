package com.exemplo.utils;

public class CnpjUtils {

    public static String gerarCnpjAleatorio() {
        StringBuilder cnpj = new StringBuilder();
        for (int i = 0; i < 14; i++) {
            int digito = (int) (Math.random() * 10);
            cnpj.append(digito);
        }
        return cnpj.toString();
    }
}
