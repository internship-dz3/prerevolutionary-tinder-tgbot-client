package com.liga.internship.client.service;

import com.liga.internship.client.domain.UserProfile;
import com.liga.internship.client.domain.dto.UsersIdTo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static com.liga.internship.client.commons.V1RestTemplate.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Сервис веб-клиента
 * Версия API 1
 */
@Service
@AllArgsConstructor
public class V1RestService {
    private final WebClient webClient;

    /**
     * Получение списка поклонников
     *
     * @param id - id активного пользователя
     * @return список поклонников
     */
    public List<UserProfile> getAdmirerList(long id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(GET_ADMIRER_LIST)
                        .build(id))
                .retrieve()
                .bodyToFlux(UserProfile.class)
                .collectList()
                .block();
    }

    /**
     * Получение списка любимцев
     *
     * @param id - id активного пользователя
     * @return список любимцев
     */
    public List<UserProfile> getFavoritesList(long id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(GET_FAVORITE_LIST)
                        .build(id))
                .retrieve()
                .bodyToFlux(UserProfile.class)
                .collectList()
                .block();
    }

    /**
     * Получение списка взаимностей
     *
     * @param id - id активного пользователя
     * @return список взаимностей
     */
    public List<UserProfile> getLoveList(long id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(GET_LOVE_LIST)
                        .build(id))
                .retrieve()
                .bodyToFlux(UserProfile.class)
                .collectList()
                .block();
    }

    /**
     * Получение списка поклонников
     *
     * @param id - id активного пользователя
     * @return список поклонников
     */
    public List<UserProfile> getNotRatedUsers(Long id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(GET_NOT_RATED_LIST)
                        .build(id))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToFlux(UserProfile.class)
                .collectList()
                .block();
    }

    /**
     * Регистрация нового пользователя
     *
     * @param userProfile - профиль нового пользователя
     * @return опционального пользователя при успешной регистрации или Optional.empty() при ошибке
     */
    public Optional<UserProfile> registerNewUser(UserProfile userProfile) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(POST_REGISTER_NEW_USER)
                        .build())
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(Mono.just(userProfile), UserProfile.class)
                .retrieve()
                .bodyToMono(UserProfile.class)
                .onErrorResume(WebClientResponseException.class,
                        ex -> ex.getRawStatusCode() == 404 ? Mono.empty() : Mono.error(ex))
                .blockOptional();
    }

    /**
     * Запрос дизлайк
     *
     * @param usersIdTo - дто айдишников
     */
    public void sendDislikeRequest(UsersIdTo usersIdTo) {
        webClient.post()
                .uri(uriBuilder -> uriBuilder.path(POST_DISLIKE)
                        .build())
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(Mono.just(usersIdTo), UsersIdTo.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    /**
     * Запрос лайк
     *
     * @param usersIdTo - дто айдишников
     */
    public boolean sendLikeRequest(UsersIdTo usersIdTo) {
        return Boolean.TRUE.equals(webClient.post()
                .uri(uriBuilder -> uriBuilder.path(POST_LIKE)
                        .build())
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(Mono.just(usersIdTo), UsersIdTo.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block());
    }

    /**
     * Обновление существующего пользователя
     *
     * @param userProfile - профиль существующего профиля
     */
    public void updateUser(UserProfile userProfile) {
        webClient.put()
                .uri(uriBuilder -> uriBuilder.path(PUT_UPDATE_USER)
                        .build())
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(Mono.just(userProfile), UserProfile.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    /**
     * Запрос логина
     *
     * @param id - telegram id пользователя
     * @return Опционального пользователя при успешном логине
     */
    public Optional<UserProfile> userLogin(long id) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(LOGIN)
                        .build())
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(Mono.just(id), Long.class)
                .retrieve()
                .bodyToMono(UserProfile.class)
                .onErrorResume(WebClientResponseException.class,
                        ex -> ex.getRawStatusCode() == 404 ? Mono.empty() : Mono.error(ex))
                .blockOptional();

    }
}
