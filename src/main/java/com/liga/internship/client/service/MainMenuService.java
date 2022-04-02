package com.liga.internship.client.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.liga.internship.client.commons.ButtonInput.*;

@Service
public class MainMenuService {
    public SendMessage getMainMenuMessage(long chatId, String message) {
        final ReplyKeyboard replyKeyboardMarkup = getMainMenuKeyboard();
        return createMessageWithKeyboard(chatId, message, replyKeyboardMarkup);
    }

    public SendPhoto getMainMenuPhotoMessage(long chatId, File image, String caption) {
        final ReplyKeyboard replyKeyboardMarkup = getMainMenuKeyboard();
        return createPhotoMessageWithKeyboard(chatId, image, caption, replyKeyboardMarkup);
    }

    private SendMessage createMessageWithKeyboard(long chatId, String message, ReplyKeyboard replyKeyboardMarkup) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        return sendMessage;
    }

    private SendPhoto createPhotoMessageWithKeyboard(long chatId, File image, String caption, ReplyKeyboard replyKeyboardMarkup) {
        return SendPhoto.builder()
                .photo(new InputFile(image))
                .chatId(String.valueOf(chatId))
                .replyMarkup(replyKeyboardMarkup)
                .caption(caption)
                .build();
    }

    private ReplyKeyboard getMainMenuKeyboard() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        row1.add(new KeyboardButton(SEARCH));
        row2.add(new KeyboardButton(USERFORM));
        row3.add(new KeyboardButton(FAVORITES));
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
}
