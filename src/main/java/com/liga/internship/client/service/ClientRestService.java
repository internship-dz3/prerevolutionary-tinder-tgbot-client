package com.liga.internship.client.service;

import com.liga.internship.client.domain.UserProfile;
import com.liga.internship.client.domain.dto.UsersIdTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ClientRestService {
    private final WebClient webClient;

    @Autowired
    public ClientRestService(WebClient webClient) {
        this.webClient = webClient;
    }


    public List<UserProfile> getNotRatedUsers(long userId){
        return webClient.get().uri("http://localhost:8080/bibaboba/api/v1/user/list/" + userId)
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

    public void sendLikeRequest(UsersIdTo usersIdTo) {
        webClient.post().uri("http://localhost:8080/bibaboba/api/v1/user/like")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(usersIdTo), UsersIdTo.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    public void sendDislikeRequest(UsersIdTo usersIdTo) {
        webClient.post().uri("http://localhost:8080/bibaboba/api/v1/user/dislike")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(usersIdTo), UsersIdTo.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }
}
