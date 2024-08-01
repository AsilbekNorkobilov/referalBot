package org.example.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.example.entity.Admin;
import org.example.service.UserService;

import java.util.Objects;

public class MyBot {
    public static final String TOKEN="7093606030:AAEUZNE4OLcPDByrHGwzRw8rEVV_Om32Eoo";
    public static TelegramBot telegramBot=new TelegramBot(TOKEN);
    public static void start() {
        telegramBot.setUpdatesListener((updates)->{
            for (Update update : updates) {
                handle(update);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private static void handle(Update update) {
        if (update.message()!=null){
            Message message = update.message();
            System.out.println(message.chat().id());
            if (Objects.equals(message.chat().id(), Admin.chatId)){
                Admin.adminMenu(update);
            }else {
                UserService.menu(message);
            }
        } else if (update.callbackQuery() != null) {
            if (update.callbackQuery().from().id().equals(Admin.chatId)){
                Admin.adminMenu(update);
            }
        }
    }
}
