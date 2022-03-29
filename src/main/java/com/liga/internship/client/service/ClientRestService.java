package com.liga.internship.client.service;

import com.liga.internship.client.domain.dto.UserTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URISyntaxException;
import java.util.List;

@Service
public class ClientRestService {
    private final WebClient webClient;

    @Autowired
    public ClientRestService(WebClient webClient) {
        this.webClient = webClient;
    }


    public List<UserTo> getHttpResponse() throws URISyntaxException {
        return webClient.get().uri("http://localhost:8080/api/v1/user/list")
                .retrieve()
                .bodyToFlux(UserTo.class)
                .collectList()
                .block();
    }
}
