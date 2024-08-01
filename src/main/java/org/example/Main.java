package org.example;


import org.example.bot.MyBot;
import org.example.db.DB;
import org.example.entity.Flayers;
import org.example.entity.TelegramUser;
import org.example.entity.enums.UserStatus;

public class Main {
    public static void main(String[] args)  {

        //Mine RUs 5445318666L
        //Mine UZ 87701653L
        //Teacher 420040267L
        DB.FLAYERS.add(Flayers.builder().countOfReferrals(12).phone("123123").photoPath("photos/flayer.jpg").build());
        for (int i = 0; i < 28; i++) {
            DB.TELEGRAM_USERS.add(TelegramUser.builder()
                            .chatId((long) i)
                            .status(UserStatus.START)
                            .phone(String.valueOf(i))
                            .comment(i+":")
                            .clientsStatus(i+" \uD83C\uDD95")
                    .build());
        }
        MyBot.start();
    }
}