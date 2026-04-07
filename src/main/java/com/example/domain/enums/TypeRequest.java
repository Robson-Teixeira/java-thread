package com.example.domain.enums;

import java.util.Arrays;

public enum TypeRequest {
    Feign("f"),
    Web("w");

    final String acronym;

    TypeRequest(String acronym) {
        this.acronym = acronym;
    }

    public String getAcronym() {
        return acronym;
    }

    public static TypeRequest fromAcronym(String text) {
        return Arrays.stream(TypeRequest.values())
                .filter(type -> type.acronym.equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Sigla desconhecida: " + text));
    }
}
