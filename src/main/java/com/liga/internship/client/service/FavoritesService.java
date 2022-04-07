package com.liga.internship.client.service;

import com.liga.internship.client.commons.ButtonCallback;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.liga.internship.client.commons.ButtonInput.*;

@Service
@AllArgsConstructor
public class FavoritesService {
    public PartialBotApiMethod<?> getMenuInlineKeyBoardService(long chatId, File image, String caption) {
        final InlineKeyboardMarkup replyKeyboardMarkup = getInlineOneMenuKeyboard();
        return createPhotoMessageWithInlineKeyBoard(chatId, image, caption, replyKeyboardMarkup);
    }

    private SendPhoto createPhotoMessageWithInlineKeyBoard(long chatId, File image, String caption, ReplyKeyboard replyKeyboardMarkup) {
        return SendPhoto.builder()
                .photo(new InputFile(image))
                .chatId(String.valueOf(chatId))
                .replyMarkup(replyKeyboardMarkup)
                .caption(caption)
                .build();
    }

    private InlineKeyboardMarkup getInlineOneMenuKeyboard() {
        final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> menuRow = new ArrayList<>();
        menuRow.add(getInlineKeyboardButton(ButtonCallback.MENU, ButtonCallback.CALLBACK_MENU));
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

    public EditMessageMedia getNextPrevInlineKeyboardEditedMessage(long chatId, int messageId, File image, String caption) {
        final InlineKeyboardMarkup replyKeyboardMarkup = getInlineMenuKeyboard();
        return createEditedPhotoMessageWithKeyboard(chatId, messageId, image, caption, replyKeyboardMarkup);
    }

    private InlineKeyboardMarkup getInlineMenuKeyboard() {
        final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> topRow = new ArrayList<>();
        topRow.add(getInlineKeyboardButton(ButtonCallback.BUTTON_PREV, ButtonCallback.CALLBACK_PREV));
        topRow.add(getInlineKeyboardButton(ButtonCallback.BUTTON_NEXT, ButtonCallback.CALLBACK_NEXT));
        List<InlineKeyboardButton> menuRow = new ArrayList<>();
        menuRow.add(getInlineKeyboardButton(ButtonCallback.MENU, ButtonCallback.CALLBACK_MENU));
        rowsInline.add(topRow);
        rowsInline.add(menuRow);
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        return inlineKeyboardMarkup;
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

    public SendPhoto getNextPrevInlineKeyboardPhotoMessage(long chatId, File image, String caption) {
        final InlineKeyboardMarkup replyKeyboardMarkup = getInlineMenuKeyboard();
        return createPhotoMessageWithInlineKeyBoard(chatId, image, caption, replyKeyboardMarkup);
    }


    public SendMessage getReplyFavoritesKeyboardTextMessage(long chatId, String message) {
        final ReplyKeyboard replyKeyboardMarkup = getReplyKeyboard();
        return createMessageWithFavoritesMenuKeyboard(chatId, message, replyKeyboardMarkup);
    }

    private SendMessage createMessageWithFavoritesMenuKeyboard(long chatId, String message, ReplyKeyboard replyKeyboardMarkup) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        return sendMessage;
    }

    private ReplyKeyboard getReplyKeyboard() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add(new KeyboardButton(FAVORITE));
        row1.add(new KeyboardButton(ADMIRER));
        row1.add(new KeyboardButton(LOVE));
        row2.add(new KeyboardButton(MAIN_MENU));
        keyboard.add(row1);
        keyboard.add(row2);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
}
