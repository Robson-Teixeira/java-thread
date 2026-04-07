package com.example.domain.adapter;

import com.example.app.model.Eligibility;
import com.example.app.model.Portability;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "mockoonClient", url = "http://localhost:3001")
public interface MockoonClient {

    @GetMapping("/api/v1/of")
    List<Portability> getPortabilities();

    @GetMapping("/api/v1/rgt")
    Portability getPortability();

    @GetMapping("/api/v1/prd")
    Eligibility getEligibility();

}
