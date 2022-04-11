package com.liga.internship.client.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.io.File;

/**
 * Сервис - Сборщик сообщений
 */
@Service
public class ReplyService {
    /**
     * Сборка изменяемого медиа сообщения
     *
     * @param chatId-             - id чата
     * @param messageId           - id изменяемого сообщения
     * @param image               - изображение профиля
     * @param caption             - подпись к профилю
     * @param replyKeyboardMarkup - прикрепляемая клавиатура
     * @return EditMessageMedia
     */
    public EditMessageMedia createEditedPhotoMessageWithKeyboard(long chatId, int messageId, File image, String caption, InlineKeyboardMarkup replyKeyboardMarkup) {
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

    /**
     * Сборка текстового сообщения с клавиатурой
     *
     * @param chatId              - id чата
     * @param message             - текст сообщения
     * @param replyKeyboardMarkup - прикрепляемая клавиатура
     * @return SendMessage с кастомной клавиатурой
     */
    public SendMessage createMessageWithKeyboard(long chatId, String message, ReplyKeyboard replyKeyboardMarkup) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        return sendMessage;
    }

    /**
     * Сборщик фотосообщения
     *
     * @param chatId              - id чата
     * @param image               - изображение профиля
     * @param caption             - подпись к профилю
     * @param replyKeyboardMarkup - прикрепляемая клавиатура
     * @return SendPhoto с кастомной клавиатурой
     */
    public SendPhoto createPhotoMessageWithKeyboard(long chatId, File image, String caption, ReplyKeyboard replyKeyboardMarkup) {
        return SendPhoto.builder()
                .photo(new InputFile(image))
                .chatId(String.valueOf(chatId))
                .replyMarkup(replyKeyboardMarkup)
                .caption(caption)
                .build();
    }

    /**
     * Текстовое сообщение
     *
     * @param chatId  - id чата
     * @param message - текстовое сообщение
     * @return SedMessage
     */
    public SendMessage getReplyMessage(Long chatId, String message) {
        return new SendMessage(chatId.toString(), message);
    }
}
