package com.example.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Eligibility {
    private String status;
    private Boolean isEligible;
    private Ineligible ineligible;
    private String channel;
    private Portability portability;
}
