package com.liga.internship.client.bot.handler;

import com.liga.internship.client.bot.BotState;
import com.liga.internship.client.cache.UserDataCache;
import com.liga.internship.client.commons.Constant;
import com.liga.internship.client.domain.UserProfile;
import com.liga.internship.client.service.ReplyMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.liga.internship.client.commons.Constant.*;

@Slf4j
@Component
public class FillProfileHandler implements InputHandler {
    private final ReplyMessageService messageService;
    private final UserDataCache userDataCache;

    @Autowired
    public FillProfileHandler(ReplyMessageService messageService, UserDataCache userDataCache) {
        this.messageService = messageService;
        this.userDataCache = userDataCache;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_PROFILE;
    }

    @Override
    public SendMessage handleCallback(CallbackQuery callback) {
        return processUserButtonPush(callback);
    }

    private SendMessage processUserButtonPush(CallbackQuery callback) {
        String userAnswer = callback.getData();
        long userId = callback.getFrom().getId();
        long chatId = callback.getMessage().getChatId();
        UserProfile userProfile = userDataCache.getUserProfile(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        SendMessage replyToUser = null;
        if (botState.equals(BotState.ASK_NAME)) {
            userProfile.setGender(userAnswer);
            replyToUser = messageService.getReplyMessage(chatId, MESSAGE_ENTER_YOUR_NAME);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_DESCRIBE);
        }
        if (botState.equals(BotState.ASK_LOOK)) {
            userProfile.setLook(userAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.PROFILE_FILLED);
        }
        userDataCache.saveUserProfile(userId, userProfile);
        return replyToUser;
    }

    @Override
    public SendMessage handleMessage(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FILLING_PROFILE)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_GENDER);
        }
        return processUserInput(message);
    }

    private SendMessage processUserInput(Message message) {
        String usersAnswer = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        UserProfile userProfile = userDataCache.getUserProfile(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        SendMessage replyToUser = null;
        if (botState.equals(BotState.ASK_GENDER)) {
            replyToUser = messageService.getReplyMessage(chatId, Constant.MESSAGE_CHOOSE_YOUR_GENDER);
            InlineKeyboardMarkup markupInline = getGenderInlineKeyboardMarkup();
            replyToUser.setReplyMarkup(markupInline);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NAME);
        }
        if (botState.equals(BotState.ASK_DESCRIBE)) {
            userProfile.setUsername(usersAnswer);
            replyToUser = messageService.getReplyMessage(chatId, MESSAGE_LOOKFOR);
            InlineKeyboardMarkup markupInline = getLookGenderInlineKeyboardMarkup();
            replyToUser.setReplyMarkup(markupInline);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_LOOK);
        }
        userDataCache.saveUserProfile(userId, userProfile);
        return replyToUser;
    }

    private InlineKeyboardMarkup getLookGenderInlineKeyboardMarkup() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
        inlineKeyboardButtonList.add(getInlineKeyboardButton(BUTTON_MALE, CALLBACK_MALE));
        inlineKeyboardButtonList.add(getInlineKeyboardButton(BUTTON_FEMALE, CALLBACK_FEMALE));
        inlineKeyboardButtonList.add(getInlineKeyboardButton(BUTTON_ALL, CALLBACK_ALL));
        rowsInline.add(inlineKeyboardButtonList);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    private InlineKeyboardMarkup getGenderInlineKeyboardMarkup() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
        inlineKeyboardButtonList.add(getInlineKeyboardButton(BUTTON_MALE, CALLBACK_MALE));
        inlineKeyboardButtonList.add(getInlineKeyboardButton(BUTTON_FEMALE, CALLBACK_FEMALE));
        rowsInline.add(inlineKeyboardButtonList);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    private InlineKeyboardButton getInlineKeyboardButton(String buttonText, String buttonCommand) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(buttonText);
        inlineKeyboardButton.setCallbackData(buttonCommand);
        return inlineKeyboardButton;
    }
}
