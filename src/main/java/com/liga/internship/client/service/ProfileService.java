package com.liga.internship.client.service;

import com.liga.internship.client.commons.ButtonInput;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.liga.internship.client.commons.ButtonInput.*;

/**
 * Сервис для FillProfileService и ShowProfile хэндлеров, предоставляет создание ответов в виде:
 * -текстового сообщения с reply клавиатурой
 * -медиа сообщения с inline клавиатурой
 * -изменяемого медиа сообщения с inline клавиатурой
 */
@Service
@AllArgsConstructor
public class ProfileService {
    private final MainMenuService mainMenuService;
    private final ReplyService replyService;

    /**
     * Графическое сообщение с главным меню
     *
     * @param chatId  - id чата
     * @param image   - изображение профиля
     * @param caption - подпись изображения
     * @return SendPhoto с главным меню
     */
    public SendPhoto getMainMenuPhotoMessage(long chatId, File image, String caption) {
        return mainMenuService.getMainMenuPhotoMessage(chatId, image, caption);
    }

    /**
     * Текстовое сообщение с меню для выбора гендера
     *
     * @param chatId  - id чата
     * @param message - текстовое сообщение
     * @return SendMessage с меню для выбора гендера
     */
    public SendMessage getMessageWithGenderChooseKeyboard(long chatId, String message) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = genderChooseMenuKeyboard();
        return replyService.createMessageWithKeyboard(chatId, message, replyKeyboardMarkup);
    }

    /**
     * Текстовое сообщение с меню для выбора поиска гендера
     *
     * @param chatId  - id чата
     * @param message - текстовое сообщение
     * @return SendMessage с меню для выбора поиска гендера
     */
    public SendMessage getMessageWithgetLookGenderChooseKeyboard(long chatId, String message) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = lookGenderChooseKeyboard();
        return replyService.createMessageWithKeyboard(chatId, message, replyKeyboardMarkup);
    }

    /**
     * Сообщение с профилем активного пользователя
     *
     * @param chatId  -id чата
     * @param image   - изображение профиля
     * @param caption - подпись к изображению
     * @return SendPhoto c меню профиля
     */
    public SendPhoto getProfileTextMessageWihProfileMenu(long chatId, File image, String caption) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = getProfileMenuKeyboard();
        return replyService.createPhotoMessageWithKeyboard(chatId, image, caption, replyKeyboardMarkup);
    }

    public SendMessage getReplyMessage(long chatId, String message) {
        return replyService.getReplyMessage(chatId, message);
    }

    private ReplyKeyboardMarkup genderChooseMenuKeyboard() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardMarkup(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add(new KeyboardButton(MALE));
        row2.add(new KeyboardButton(FEMALE));
        keyboard.add(row1);
        keyboard.add(row2);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private ReplyKeyboardMarkup getProfileMenuKeyboard() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardMarkup(false);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add(new KeyboardButton(CHANGE_PROFILE));
        row2.add(new KeyboardButton(ButtonInput.MAIN_MENU));
        keyboard.add(row1);
        keyboard.add(row2);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private ReplyKeyboardMarkup getReplyKeyboardMarkup(boolean oneTime) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(oneTime);
        return replyKeyboardMarkup;
    }

    private ReplyKeyboardMarkup lookGenderChooseKeyboard() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardMarkup(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add(new KeyboardButton(MALE));
        row1.add(new KeyboardButton(FEMALE));
        row2.add(new KeyboardButton(ALL));
        keyboard.add(row1);
        keyboard.add(row2);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

}
