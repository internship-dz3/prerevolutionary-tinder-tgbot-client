package com.liga.internship.client.service;

import com.liga.internship.client.domain.UserProfile;
import com.liga.internship.client.domain.dto.UsersIdTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
public class V1RestService {
    private final WebClient webClient;

    @Autowired
    public V1RestService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<UserProfile> getAdmirerList(long userId) {
        return webClient.get().uri(String.format("http://localhost:8080/bibaboba/api/v1/user/%d/admirers", userId))
                .retrieve()
                .bodyToFlux(UserProfile.class)
                .collectList()
                .block();
    }

    public List<UserProfile> getFavoritesList(long userId) {
        return webClient.get().uri(String.format("http://localhost:8080/bibaboba/api/v1/user/%d/likes", userId))
                .retrieve()
                .bodyToFlux(UserProfile.class)
                .collectList()
                .block();
    }

    public List<UserProfile> getLoveList(long userId) {
        return webClient.get().uri(String.format("http://localhost:8080/bibaboba/api/v1/user/%d/lovers", userId))
                .retrieve()
                .bodyToFlux(UserProfile.class)
                .collectList()
                .block();
    }

    public List<UserProfile> getNotRatedUsers(UserProfile userProfile) {
        return webClient.post()
                .uri("http://localhost:8080/bibaboba/api/v1/user/list/")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(Mono.just(userProfile), UserProfile.class)
                .retrieve()
                .bodyToFlux(UserProfile.class)
                .collectList()
                .block();
    }

    public Optional<UserProfile> registerNewUser(UserProfile userProfile) {
        Mono<UserProfile> register = webClient.post()
                .uri("http://localhost:8080/bibaboba/api/v1/user/register")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(Mono.just(userProfile), UserProfile.class)
                .retrieve()
                .bodyToMono(UserProfile.class)
                .onErrorResume(WebClientResponseException.class,
                        ex -> ex.getRawStatusCode() == 404 ? Mono.empty() : Mono.error(ex));
        return register.blockOptional();
    }

    public void sendDislikeRequest(UsersIdTo usersIdTo) {
        webClient.post().uri("http://localhost:8080/bibaboba/api/v1/user/dislike")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(Mono.just(usersIdTo), UsersIdTo.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    public void sendLikeRequest(UsersIdTo usersIdTo) {
        webClient.post()
                .uri("http://localhost:8080/bibaboba/api/v1/user/like")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(Mono.just(usersIdTo), UsersIdTo.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    public void updateUser(UserProfile userProfile) {
        webClient.put()
                .uri("http://localhost:8080/bibaboba/api/v1/user/update")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(Mono.just(userProfile), UserProfile.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    public Optional<UserProfile> userLogin(long id) {
        Mono<UserProfile> userProfile = webClient.post().uri("http://localhost:8080/bibaboba/api/v1/user/login")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(Mono.just(id), Long.class)
                .retrieve()
                .bodyToMono(UserProfile.class)
                .onErrorResume(WebClientResponseException.class,
                        ex -> ex.getRawStatusCode() == 404 ? Mono.empty() : Mono.error(ex));
        return userProfile.blockOptional();
    }
}
