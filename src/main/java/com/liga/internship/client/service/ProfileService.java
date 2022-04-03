package com.liga.internship.client.service;

import com.liga.internship.client.commons.ButtonInput;
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
public class ProfileService {
    public SendMessage getMessageWithGenderChooseKeyboard(long chatId, String message) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = genderChooseMenuKeyboard();
        return createMessageWithKeyboard(chatId, message, replyKeyboardMarkup);
    }

    private ReplyKeyboardMarkup genderChooseMenuKeyboard() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardMarkup();
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

    public SendMessage getMessageWithgetLookGenderChooseKeyboard(long chatId, String message) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = lookGenderChooseKeyboard();
        return createMessageWithKeyboard(chatId, message, replyKeyboardMarkup);
    }

    private ReplyKeyboardMarkup lookGenderChooseKeyboard() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardMarkup();
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

    private ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        return replyKeyboardMarkup;
    }

    private SendMessage createMessageWithKeyboard(long chatId, String message, ReplyKeyboardMarkup replyKeyboardMarkup) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        return sendMessage;
    }

    public SendPhoto getProfileTextMessageWihProfileMenu(long chatId, File image, String caption) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = getProfileMenuKeyboard();
        return createPhotoMessageWithKeyboard(chatId, image, caption, replyKeyboardMarkup);
    }

    private ReplyKeyboardMarkup getProfileMenuKeyboard() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add(new KeyboardButton(ButtonInput.SHOW_PROFILE));
        row2.add(new KeyboardButton(ButtonInput.MAIN_MENU));
        keyboard.add(row1);
        keyboard.add(row2);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private SendPhoto createPhotoMessageWithKeyboard(long chatId, File image, String caption, ReplyKeyboard replyKeyboardMarkup) {
        return SendPhoto.builder()
                .photo(new InputFile(image))
                .chatId(String.valueOf(chatId))
                .replyMarkup(replyKeyboardMarkup)
                .caption(caption)
                .build();
    }
}
