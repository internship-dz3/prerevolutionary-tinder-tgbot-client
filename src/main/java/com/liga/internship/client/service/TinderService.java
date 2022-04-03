package com.liga.internship.client.service;

import com.liga.internship.client.commons.ButtonCallback;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TinderService {
    public EditMessageMedia getEditedLikeDislikePhotoMessage(long chatId, int messageId, File image, String caption) {
        final InlineKeyboardMarkup replyKeyboardMarkup = getInlineMenuKeyboard();
        return createEditedPhotoMessageWithKeyboard(chatId, messageId, image, caption, replyKeyboardMarkup);
    }

    private EditMessageMedia createEditedPhotoMessageWithKeyboard(long chatId, int messageId, File image, String caption, InlineKeyboardMarkup replyKeyboardMarkup) {
        InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
        inputMediaPhoto.setMedia(image, String.format("image%d.jpg", chatId));
        inputMediaPhoto.setCaption(caption);

        EditMessageMedia editMessageMedia = new EditMessageMedia();
        editMessageMedia.setMedia(inputMediaPhoto);
        editMessageMedia.setChatId(String.valueOf(chatId));
        editMessageMedia.setMessageId(messageId);
        editMessageMedia.setReplyMarkup(replyKeyboardMarkup);
        return editMessageMedia;
    }

    private InlineKeyboardMarkup getInlineMenuKeyboard() {
        final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> topRow = new ArrayList<>();
        topRow.add(getInlineKeyboardButton(ButtonCallback.BUTTON_DISLIKE, ButtonCallback.CALLBACK_DISLIKE));
        topRow.add(getInlineKeyboardButton(ButtonCallback.BUTTON_LIKE, ButtonCallback.CALLBACK_LIKE));
        List<InlineKeyboardButton> menuRow = new ArrayList<>();
        menuRow.add(getInlineKeyboardButton(ButtonCallback.MENU, ButtonCallback.CALLBACK_MENU));
        rowsInline.add(topRow);
        rowsInline.add(menuRow);
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardButton getInlineKeyboardButton(String buttonText, String buttonCommand) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(buttonText);
        inlineKeyboardButton.setCallbackData(buttonCommand);
        return inlineKeyboardButton;
    }

    public SendPhoto getLikeDislikeMenuPhotoMessage(long chatId, File image, String caption) {
        final InlineKeyboardMarkup replyKeyboardMarkup = getInlineMenuKeyboard();
        return createPhotoMessageWithKeyboard(chatId, image, caption, replyKeyboardMarkup);
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
