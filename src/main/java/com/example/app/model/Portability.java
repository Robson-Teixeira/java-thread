package com.example.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Portability {
    private String portabilityId;
    private String cnpjCreditor;
    private String bankName;
}
