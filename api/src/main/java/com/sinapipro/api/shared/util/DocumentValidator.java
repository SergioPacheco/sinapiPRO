package com.sinapipro.api.shared.util;

public final class DocumentValidator {

    private DocumentValidator() {}

    public static boolean isValidCpf(String cpf) {
        if (cpf == null) return false;
        cpf = cpf.replaceAll("[^0-9]", "");
        if (cpf.length() != 11) return false;
        if (cpf.chars().distinct().count() == 1) return false;

        int sum = 0;
        for (int i = 0; i < 9; i++) sum += (cpf.charAt(i) - '0') * (10 - i);
        int d1 = 11 - (sum % 11);
        if (d1 >= 10) d1 = 0;
        if (d1 != (cpf.charAt(9) - '0')) return false;

        sum = 0;
        for (int i = 0; i < 10; i++) sum += (cpf.charAt(i) - '0') * (11 - i);
        int d2 = 11 - (sum % 11);
        if (d2 >= 10) d2 = 0;
        return d2 == (cpf.charAt(10) - '0');
    }

    public static boolean isValidCnpj(String cnpj) {
        if (cnpj == null) return false;
        cnpj = cnpj.replaceAll("[^0-9]", "");
        if (cnpj.length() != 14) return false;
        if (cnpj.chars().distinct().count() == 1) return false;

        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < 12; i++) sum += (cnpj.charAt(i) - '0') * weights1[i];
        int d1 = sum % 11 < 2 ? 0 : 11 - (sum % 11);
        if (d1 != (cnpj.charAt(12) - '0')) return false;

        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        sum = 0;
        for (int i = 0; i < 13; i++) sum += (cnpj.charAt(i) - '0') * weights2[i];
        int d2 = sum % 11 < 2 ? 0 : 11 - (sum % 11);
        return d2 == (cnpj.charAt(13) - '0');
    }
}
