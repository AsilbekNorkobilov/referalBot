package org.example.service;

import com.pengrad.telegrambot.model.Message;
import org.example.bot.BotConstant;
import org.example.bot.BotService;
import org.example.db.DB;
import org.example.entity.Flayers;
import org.example.entity.TelegramUser;
import org.example.entity.enums.UserStatus;

public class UserService {
    public static void menu(Message message) {
        Long chatId = message.chat().id();
        TelegramUser currentUser=getUser(chatId);
        if (message.text()!=null){
            String text = message.text();
            if (currentUser.getStatus().equals(UserStatus.START)){
                BotService.askContact(currentUser,text);
            } else if (currentUser.getStatus().equals(UserStatus.SHOW_USERS_MENU)) {
                if (text.equals(BotConstant.USERS_FLAYER)){
                    BotService.sendFlayerToUser(currentUser);
                } else if (text.equals(BotConstant.USERS_REFERRALS)) {
                    BotService.sendReferralsCountToUser(currentUser);
                }
            }
        } else if (message.contact()!=null) {
            if (currentUser.getStatus().equals(UserStatus.ASK_CONTACT)) {
                BotService.acceptContactSendMessage(currentUser,message);
            }
        }
    }

    public static void hasFlayer(TelegramUser currentUser) {
        for (Flayers flayer : DB.FLAYERS) {
            if (flayer.getPhone().equals(currentUser.getPhone())){
                currentUser.setStatus(UserStatus.HAS_FLAYER);
            }
        }
    }

    private static TelegramUser getUser(Long chatId) {
            for (TelegramUser telegramUser : DB.TELEGRAM_USERS) {
                if (telegramUser.getChatId().equals(chatId))
                    return telegramUser;
            }
        TelegramUser telegramUser=TelegramUser.builder()
                .chatId(chatId)
                .status(UserStatus.START)
                .build();
        DB.TELEGRAM_USERS.add(telegramUser);
        return telegramUser;
    }
}
