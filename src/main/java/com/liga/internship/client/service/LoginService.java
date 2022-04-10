package com.liga.internship.client.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static com.liga.internship.client.commons.ButtonInput.FILL_FORM;

/**
 * Сервис LoginHandler, предоставляет создание ответов в виде:
 * - сообщение с предложением заполнить анкету
 */
@Service
@AllArgsConstructor
public class LoginService {
    private final ReplyService replyService;

    /**
     * @param chatId  - id чата
     * @param message - текст сообщения
     * @return SendMessage с кнопкой fill form
     */
    public SendMessage getMessageWithFillFormMenu(long chatId, String message) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = fillFormMenuKeyboard();
        return replyService.createMessageWithKeyboard(chatId, message, replyKeyboardMarkup);
    }

    private ReplyKeyboardMarkup fillFormMenuKeyboard() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(FILL_FORM));
        keyboard.add(row1);
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
