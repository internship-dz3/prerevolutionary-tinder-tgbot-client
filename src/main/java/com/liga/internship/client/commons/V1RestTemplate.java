package com.liga.internship.client.commons;

public class V1RestTemplate {
    public static final String GET_ADMIRER_LIST = "api/v1/user/{id}/admirers";
    public static final String GET_FAVORITE_LIST = "api/v1/user/{id}/likes";
    public static final String GET_LOVE_LIST = "api/v1/user/{id}/lovers";
    public static final String GET_NOT_RATED_LIST = "api/v1/user/{id}/tinder";
    public static final String POST_REGISTER_NEW_USER = "api/v1/user/register";
    public static final String PUT_UPDATE_USER = "api/v1/user/update";
    public static final String LOGIN = "api/v1/user/login";
    public static final String POST_LIKE = "api/v1/user/like";
    public static final String POST_DISLIKE = "api/v1/user/dislike";

    private V1RestTemplate() {
    }
}
