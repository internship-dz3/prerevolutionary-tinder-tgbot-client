package com.liga.internship.client.bot;

import com.liga.internship.client.domain.dto.UserTo;
import com.liga.internship.client.handler.RegistrationHandler;
import com.liga.internship.client.service.ImageCreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.liga.internship.client.commons.Constant.*;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final ImageCreatorService imageCreatorService;
    private final Map<String, RegistrationHandler> registrationHandlerMap;

    @Value("${bot.name}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    public TelegramBot(ImageCreatorService imageCreatorService) {
        this.imageCreatorService = imageCreatorService;
        this.registrationHandlerMap = new ConcurrentHashMap<>();
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            int messageId = update.getMessage().getMessageId();
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();
            if (text.equals("/start")) {
                registerHandler(chatId, messageId);
                startRegistration(chatId);
            } else {
                processUserTextInput(text, messageId, chatId);
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            processUserButtonInput(callbackQuery);
        }
    }

    private void startRegistration(String chatId) {
        getUserGenderRequest(chatId);
    }

    private void getUserGenderRequest(String chatId) {
        try {
            SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(MESSAGE_ENTER_YOUR_GENDER).build();
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
            inlineKeyboardButtonList.add(getInlineKeyboardButton(BUTTON_MALE, CALLBACK_MALE));
            inlineKeyboardButtonList.add(getInlineKeyboardButton(BUTTON_FEMALE, CALLBACK_FEMALE));
            rowsInline.add(inlineKeyboardButtonList);
            markupInline.setKeyboard(rowsInline);
            sendMessage.setReplyMarkup(markupInline);
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }

    private InlineKeyboardButton getInlineKeyboardButton(String buttonText, String buttonCommand) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(buttonText);
        inlineKeyboardButton.setCallbackData(buttonCommand);
        return inlineKeyboardButton;
    }

    private void registerHandler(String chatId, int messageId) {
        registrationHandlerMap.put(chatId, new RegistrationHandler(chatId, new UserTo(), messageId));
    }

    private void processUserButtonInput(CallbackQuery callbackQuery) {
        String callBackData = callbackQuery.getData();
        String chatId = callbackQuery.getMessage().getChatId().toString();
        if (registrationHandlerMap.containsKey(chatId)) {
            processNextRegistrtionStep(callBackData, chatId);
        } else {
            processCallbackQuery(callbackQuery);
        }
    }

    private void processNextRegistrtionStep(String callBackData, String chatId) {
        if (CALLBACK_MALE.equals(callBackData) || CALLBACK_FEMALE.equals(callBackData)) {
            setUserGender(chatId, callBackData);
            getUsernameRequest(chatId);
        } else if (callBackData.equals(CALLBACK_SEARCH_FEMALE) || callBackData.equals(CALLBACK_SEARCH_MALE) || callBackData.equals(CALLBACK_SEARCH_ALL)) {
            processUserSearchGenderParam(chatId, callBackData);
        }
    }

    private void getUsernameRequest(String chatId) {
        try {
            SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(MESSAGE_ENTER_YOUR_NAME).build();
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void processUserSearchGenderParam(String chatId, String callBackData) {
        RegistrationHandler registrationHandler = registrationHandlerMap.get(chatId);
        switch (callBackData) {
            case CALLBACK_SEARCH_MALE:
                registrationHandler.setUserGender(CALLBACK_MALE);
                break;
            case CALLBACK_SEARCH_FEMALE:
                registrationHandler.setUserGender(CALLBACK_FEMALE);
                break;
            case CALLBACK_SEARCH_ALL:
                registrationHandler.setUserGender(CALLBACK_ALL);
                break;
        }
        System.out.println(registrationHandler.getUserTo());

    }

    private void setUserGender(String chatId, String gender) {
        if (registrationHandlerMap.containsKey(chatId)) {
            RegistrationHandler registrationHandler = registrationHandlerMap.get(chatId);
            registrationHandler.setUserGender(gender);
        }
    }

    private void processCallbackQuery(CallbackQuery callbackQuery) {

    }

    private void processUserTextInput(String text, int messageId, String chatId) {
        RegistrationHandler registrationHandler = registrationHandlerMap.get(chatId);
        int registrationMessageId = registrationHandler.getMessageId();
        System.out.println("regId " + registrationMessageId);
        System.out.println("curreId " + messageId);
        if (messageId - registrationMessageId == 3) {
            if (processUsernameInput(text, registrationHandler)) {
                descriptionRequest(chatId);
            } else {
                registrationHandler.messageIdIncrease();
            }
        } else if (messageId - registrationMessageId == 5) {
            if (processUserDescription(text, registrationHandler)) {
                setSearchGenderParam(chatId);
            } else {
                registrationHandler.messageIdIncrease();
            }
        }
    }

    private void descriptionRequest(String chatId) {
        try {
            SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(MESSAGE_DESCRIBE_YOURSELF).build();
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }

    private boolean processUserDescription(String description, RegistrationHandler registrationHandler) {
        if (description.length() <= 200) {
            registrationHandler.getUserTo().setDescription(description);
            return true;
        }
        return false;
    }

    private boolean processUsernameInput(String username, RegistrationHandler registrationHandler) {
        if (username.length() <= 20) {
            registrationHandler.getUserTo().setUsername(username);
            return true;
        }
        return false;
    }

    private void setSearchGenderParam(String chatId) {
        try {
            SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(MESSAGE_LOOKFOR).build();
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
            inlineKeyboardButtonList.add(getInlineKeyboardButton(BUTTON_MALE, CALLBACK_SEARCH_MALE));
            inlineKeyboardButtonList.add(getInlineKeyboardButton(BUTTON_ALL, CALLBACK_SEARCH_ALL));
            inlineKeyboardButtonList.add(getInlineKeyboardButton(BUTTON_FEMALE, CALLBACK_SEARCH_FEMALE));
            rowsInline.add(inlineKeyboardButtonList);
            markupInline.setKeyboard(rowsInline);
            sendMessage.setReplyMarkup(markupInline);
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }

    private EditMessageMedia getEditedMessageMedia(String chatId, int messageId, String text) throws IOException {
        File file = imageCreatorService.getImageWithTextFile(text);
        EditMessageMedia editMessageMedia = new EditMessageMedia();
        InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
        inputMediaPhoto.setMedia(file, "image.jpg");
        editMessageMedia.setMedia(inputMediaPhoto);
        editMessageMedia.setChatId(chatId);
        editMessageMedia.setMessageId(messageId);
        return editMessageMedia;
    }

    private EditMessageMedia getEditedMessageMediaFrom(String chatId, int messageId, String text) throws IOException {
        File file = imageCreatorService.getImageWithTextFile(text);
        EditMessageMedia editMessageMedia = new EditMessageMedia();
        InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
        inputMediaPhoto.setMedia(file, "image.jpg");
        editMessageMedia.setMedia(inputMediaPhoto);
        editMessageMedia.setChatId(chatId);
        editMessageMedia.setMessageId(messageId);
        return editMessageMedia;
    }

    private SendPhoto getSendPhoto(String chatId, String text) throws IOException {
        File file = imageCreatorService.getImageWithTextFile(text);
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(file))
                .build();
    }
}
