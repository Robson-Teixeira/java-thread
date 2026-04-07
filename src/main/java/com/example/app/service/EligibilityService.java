package com.example.app.service;

import com.example.app.model.Eligibility;
import com.example.app.model.Portability;
import com.example.domain.adapter.MockoonClient;
import com.example.domain.enums.TypeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class EligibilityService {

    // Injeção via construtor (gerenciado pelo Lombok @RequiredArgsConstructor) é a melhor prática.
    private final MockoonClient mockoonClient;
    private final ExecutorService taskExecutor;

    private final String portabilityIdCurrent = "1dfd2ba3-5843-4f0b-9b47-a25b5547428f";

    private final WebClient webClient = WebClient.create("http://localhost:3001");

    // Método 1: Sequencial
    public Eligibility method1(String typeRequest) {

        long startMethod = System.currentTimeMillis();

        Eligibility eligibilityResponse = null;

        long tStartOf = System.currentTimeMillis();
        List<Portability> portabilityList = getPortabilities(typeRequest);
        long tEndOf = System.currentTimeMillis();
        log.info("Resposta endpoint 1: " + (tEndOf - tStartOf) + "ms");
//        log.info("Resposta: {}", portabilityList);

        if (portabilityList != null && !portabilityList.isEmpty()) {

            portabilityList.stream()
                    .filter(p -> p.getPortabilityId().equals(portabilityIdCurrent))
                    .findFirst()
                    .ifPresent(p -> {
                        portabilityList.remove(p);
//                        log.info("Portabilidade removida, pois foi encontrada no endpoint 1.");
                    });

            if (!portabilityList.isEmpty()) {

                eligibilityResponse = Eligibility.builder()
                        .status("EM_ANDAMENTO")
                        .isEligible(true)
                        .channel("OF")
                        .portability(portabilityList.get(0))
                        .build();

                lifetimeMethod(startMethod, "1");

                return eligibilityResponse;
            }
        }

        long tStartRgt = System.currentTimeMillis();
        Portability portability = getPortability(typeRequest);
        long tEndRgt = System.currentTimeMillis();
        log.info("Resposta endpoint 2: " + (tEndRgt - tStartRgt) + "ms");
//        log.info("Resposta: {}", portability);

        if (portability != null && !portability.getCnpjCreditor().isBlank()) {

            eligibilityResponse = Eligibility.builder()
                    .status("EM_ANDAMENTO")
                    .isEligible(true)
                    .channel("RGT")
                    .portability(portability)
                    .build();

            lifetimeMethod(startMethod, "1");

            return eligibilityResponse;
        }

        long tStartPrd = System.currentTimeMillis();
        Eligibility eligibility = getEligibility(typeRequest);
        long tEndPrd = System.currentTimeMillis();
        log.info("Resposta endpoint 3: " + (tEndPrd - tStartPrd) + "ms");
//        log.info("Resposta: {}", eligibility);

        eligibilityResponse = eligibility;

        lifetimeMethod(startMethod, "1");

        return eligibilityResponse;
    }

    // Método 2: Paralelo (threads)
    public Eligibility method2(String typeRequest) throws InterruptedException, ExecutionException {

        long startMethod = System.currentTimeMillis();

        Eligibility eligibilityResponse = null;

        try {

            Callable<List<Portability>> callableOf = () -> {
                long tStartOf = System.currentTimeMillis();
                List<Portability> portabilityList = getPortabilities(typeRequest);
                long tEndOf = System.currentTimeMillis();
                log.info("Thread endpoint 1: " + (tEndOf - tStartOf) + "ms");
                return portabilityList;
            };

            Future<List<Portability>> portabilityListFuture = taskExecutor.submit(callableOf);

            Callable<Portability> callableRgt = () -> {
                long tStartRgt = System.currentTimeMillis();
                Portability portability = getPortability(typeRequest);
                long tEndRgt = System.currentTimeMillis();
                log.info("Thread endpoint 2: " + (tEndRgt - tStartRgt) + "ms");
                return portability;
            };

            Future<Portability> portabilityFuture = taskExecutor.submit(callableRgt);

            Callable<Eligibility> callablePrd = () -> {
                long tStartPrd = System.currentTimeMillis();
                Eligibility eligibility = getEligibility(typeRequest);
                long tEndPrd = System.currentTimeMillis();
                log.info("Thread endpoint 3: " + (tEndPrd - tStartPrd) + "ms");
                return eligibility;
            };

            Future<Eligibility> eligibilityFuture = taskExecutor.submit(callablePrd);

            List<Portability> portabilityList = portabilityListFuture.get();
//            log.info("Resposta: {}", portabilityList);

            if (portabilityList != null && !portabilityList.isEmpty()) {

                portabilityList.stream()
                        .filter(p -> p.getPortabilityId().equals(portabilityIdCurrent))
                        .findFirst()
                        .ifPresent(p -> {
                            portabilityList.remove(p);
//                            log.info("Portabilidade removida, pois foi encontrada no endpoint 1.");
                        });

                if (!portabilityList.isEmpty()) {

                    eligibilityResponse = Eligibility.builder()
                            .status("EM_ANDAMENTO")
                            .isEligible(true)
                            .channel("OF")
                            .portability(portabilityList.get(0))
                            .build();

                    lifetimeMethod(startMethod, "2");

                    return eligibilityResponse;
                }
            }

            Portability portability = portabilityFuture.get();
//            log.info("Resposta: {}", portability);

            if (portability != null && !portability.getCnpjCreditor().isBlank()) {
//                log.info("Portabilidade encontrada no endpoint 2: " + portability);

                eligibilityResponse = Eligibility.builder()
                        .status("EM_ANDAMENTO")
                        .isEligible(true)
                        .channel("RGT")
                        .portability(portability)
                        .build();

                lifetimeMethod(startMethod, "2");

                return eligibilityResponse;
            }

            Eligibility eligibility = eligibilityFuture.get();
//            log.info("Resposta: {}", eligibility);

            eligibilityResponse = eligibility;

            lifetimeMethod(startMethod, "2");

            return eligibilityResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void lifetimeMethod(long startMethod, String method) {
        long endMethod = System.currentTimeMillis();
        log.info("Tempo total método {}: {}", method, (endMethod - startMethod) + "ms");
    }

    private List<Portability> getPortabilities(String typeRequest) {
        return TypeRequest.fromAcronym(typeRequest).equals(TypeRequest.Feign) ? mockoonClient.getPortabilities() :
                webClient.get()
                        .uri("/api/v1/of")
                        .retrieve()
                        .bodyToFlux(Portability.class)
                        .collectList().block();

    }

    private Portability getPortability(String typeRequest) {
        return TypeRequest.fromAcronym(typeRequest).equals(TypeRequest.Feign) ? mockoonClient.getPortability() :
                webClient.get()
                        .uri("/api/v1/rgt")
                        .retrieve()
                        .bodyToMono(Portability.class)
                        .block();
    }

    private Eligibility getEligibility(String typeRequest) {
        return TypeRequest.fromAcronym(typeRequest).equals(TypeRequest.Feign) ? mockoonClient.getEligibility() :
                webClient.get()
                        .uri("/api/v1/prd")
                        .retrieve()
                        .bodyToMono(Eligibility.class)
                        .block();
    }
}
