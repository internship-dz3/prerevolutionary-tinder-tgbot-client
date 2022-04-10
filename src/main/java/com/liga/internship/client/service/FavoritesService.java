package com.liga.internship.client.service;

import com.liga.internship.client.commons.ButtonCallback;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
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

/**
 * Сервис для FavoritesHandler, предоставляет создание ответов в виде:
 * -текстового сообщения с reply клавиатурой
 * -медиа сообщения с inline клавиатурой
 * -изменяемого медиа сообщения с inline клавиатурой
 */
@Service
@AllArgsConstructor
public class FavoritesService {
    private final MainMenuService mainMenuService;
    private final ReplyService replyService;

    public SendMessage getMainMenuMessage(long chatId, String messageMainMenu) {
        return mainMenuService.getMainMenuMessage(chatId, messageMainMenu);
    }

    /**
     * Получение изменяемого медиа сообщения с клавиатурой Menu.
     * **************
     * ****message***
     * **************
     * -caption-
     * [prev]  [next]
     * [    menu    ]
     *
     * @param chatId    - id чата
     * @param messageId - id изменяемого сообщение
     * @param image     - изображение профиля
     * @param caption   - подпись к профилю
     * @return EditedMessageMedia - сообщение с клавиатурой next prev menu
     */
    public EditMessageMedia getNextPrevInlineKeyboardEditedMessage(long chatId, int messageId, File image, String caption) {
        final InlineKeyboardMarkup replyKeyboardMarkup = getInlineNextPrevMenuKeyboard();
        return replyService.createEditedPhotoMessageWithKeyboard(chatId, messageId, image, caption, replyKeyboardMarkup);
    }

    public SendPhoto getNextPrevInlineKeyboardPhotoMessage(long chatId, File image, String caption) {
        final InlineKeyboardMarkup replyKeyboardMarkup = getInlineNextPrevMenuKeyboard();
        return replyService.createPhotoMessageWithKeyboard(chatId, image, caption, replyKeyboardMarkup);
    }

    /**
     * Получение фото сообщения с клавиатурой Menu.
     * **************
     * ****message***
     * **************
     * [    menu    ]
     *
     * @param chatId  - id чата
     * @param image   - изображение профиля
     * @param caption - подпись к профилю
     * @return SendPhoto сообщение с клавиатурой menu
     */
    public SendPhoto getPhotoMessageWithInlineMenuKeyboard(long chatId, File image, String caption) {
        final InlineKeyboardMarkup replyKeyboardMarkup = getInlineOneMenuKeyboard();
        return replyService.createPhotoMessageWithKeyboard(chatId, image, caption, replyKeyboardMarkup);
    }

    /**
     * Получение сообщения с главным меню хэндлера
     * <p>
     * [мои любимцы][я любимец][взаимность]
     * [           главное меню           ]
     *
     * @param chatId  - id чата
     * @param message - текст отправляемого сообщения
     * @return SendMessage с главным меню
     */
    public SendMessage getReplyFavoritesKeyboardTextMessage(long chatId, String message) {
        final ReplyKeyboard replyKeyboardMarkup = getReplyKeyboard();
        return replyService.createMessageWithKeyboard(chatId, message, replyKeyboardMarkup);
    }

    private InlineKeyboardButton getInlineKeyboardButton(String buttonText, String buttonCommand) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(buttonText);
        inlineKeyboardButton.setCallbackData(buttonCommand);
        return inlineKeyboardButton;
    }

    private InlineKeyboardMarkup getInlineNextPrevMenuKeyboard() {
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

    private InlineKeyboardMarkup getInlineOneMenuKeyboard() {
        final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> menuRow = new ArrayList<>();
        menuRow.add(getInlineKeyboardButton(ButtonCallback.MENU, ButtonCallback.CALLBACK_MENU));
        rowsInline.add(menuRow);
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        return inlineKeyboardMarkup;
    }

    /**
     * Клавиатура ReplyKeyboardMarkup главного меню в разделе любимцы
     *
     * @return ReplyKeyboard
     */
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
