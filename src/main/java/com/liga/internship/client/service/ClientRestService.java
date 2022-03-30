package com.liga.internship.client.service;

import com.liga.internship.client.domain.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;
import java.util.List;

@Service
public class ClientRestService {
    private final WebClient webClient;

    @Autowired
    public ClientRestService(WebClient webClient) {
        this.webClient = webClient;
    }


    public List<UserProfile> getHttpResponse() throws URISyntaxException {
        return webClient.get().uri("http://localhost:8080/bibaboba/api/v1/user/list")
                .retrieve()
                .bodyToFlux(UserProfile.class)
                .collectList()
                .block();
    }

    public void registerNewUser(UserProfile userProfile) {
        webClient.post().uri("http://localhost:8080/bibaboba/api/v1/user/register")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(userProfile), UserProfile.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }
}
