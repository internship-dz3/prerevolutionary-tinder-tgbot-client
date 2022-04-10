package com.liga.internship.client.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.liga.internship.client.commons.ButtonInput.*;

/**
 * Сервис главного меню, предоставляет текстовые и графические сообщения с главным меню
 */

@Service
@AllArgsConstructor
public class MainMenuService {
    private final ReplyService replyService;

    /**
     * Получение текстового сообщения с главным меню
     *
     * @param chatId  - id чата
     * @param message - текст сообщения
     * @return SendMessage с главным меню
     */
    public SendMessage getMainMenuMessage(long chatId, String message) {
        final ReplyKeyboard replyKeyboardMarkup = getMainMenuKeyboard();
        return replyService.createMessageWithKeyboard(chatId, message, replyKeyboardMarkup);
    }

    /**
     * Получение фото сообщения с главным меню
     *
     * @param chatId  - id чата
     * @param image   - прикрепляемое изображение
     * @param caption - подпись к изображению
     * @return SendPhoto с главным меню
     */
    public SendPhoto getMainMenuPhotoMessage(long chatId, File image, String caption) {
        final ReplyKeyboard replyKeyboardMarkup = getMainMenuKeyboard();
        return replyService.createPhotoMessageWithKeyboard(chatId, image, caption, replyKeyboardMarkup);
    }

    private ReplyKeyboard getMainMenuKeyboard() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add(new KeyboardButton(SEARCH));
        row2.add(new KeyboardButton(FAVORITES));
        row2.add(new KeyboardButton(USERFORM));
        keyboard.add(row1);
        keyboard.add(row2);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        return replyKeyboardMarkup;
    }
}
