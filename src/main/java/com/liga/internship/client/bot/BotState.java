package com.liga.internship.client.bot;

/**
 * Класс характеризующий состояние бота. Постоянно храниться в UserDataCache
 */
public enum BotState {
    FILLING_PROFILE_ASK_NAME,
    FILLING_PROFILE_ASK_AGE,
    FILLING_PROFILE_ASK_GENDER,
    FILLING_PROFILE_ASK_DESCRIBE,
    FILLING_PROFILE_ASK_LOOK,
    FILLING_PROFILE_COMPLETE,

    HANDLER_PROFILE_FILLING,
    HANDLER_MAIN_MENU,
    HANDLER_SHOW_FAVORITES,
    HANDLER_LOGIN,
    HANDLER_TINDER,

    SHOW_NEXT_FAVORITE,
    SHOW_PREV_FAVORITE,
    SHOW_USER_PROFILE,


    CONTINUE_VOTING,
    STOP_VOTING,
}
