package com.liga.internship.client.service;

import com.liga.internship.client.commons.ButtonCallback;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

/**
 * Сервис хендлера голосования предоставляет создание ответов в виде:
 * -текстовое сообщение с главным меню
 * -фото сообщение с главиатурой голосования
 * -изменяемое фотосообщение с клавиатурой голосования
 */
@Service
@AllArgsConstructor
public class TinderService {
    private final MainMenuService mainMenuService;

    /**
     * Измененное медиа сообщение с клавиатурой голосования
     * *******************
     * ****** edited *****
     * ****** image ******
     * *******************
     * *******************
     * -caption-
     * [dislike][  like  ]
     * [    main menu    ]
     *
     * @param chatId    - id чата
     * @param messageId - id изменяемого сообщщения
     * @param image     - изображение изменяемого сообщения
     * @param caption   - подпись к изображению
     * @return EditMessageMedia
     */
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

    /**
     * Поучение графического сообщения с клавиатурой голосования
     *
     * *******************
     * ****** image ******
     * *******************
     * -caption-
     * [dislike][  like  ]
     * [    main menu    ]
     *
     * @param chatId  - id чата
     * @param image   - изображение изменяемого сообщения
     * @param caption - подпись к изображению
     * @return SendPhoto c клавиатурой голосования
     */
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

    /**
     * Текстовое сообщение с главным меню
     *
     * @param chatId  - id чата
     * @param message - текст сообщения
     * @return SendMessage
     */
    public SendMessage getMainMenuMessage(long chatId, String message) {
        return mainMenuService.getMainMenuMessage(chatId, message);
    }
}
