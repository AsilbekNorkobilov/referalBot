package org.example.entity;

import com.pengrad.telegrambot.model.Sticker;
import com.pengrad.telegrambot.request.DeleteMessage;
import lombok.Builder;
import lombok.Data;
import org.example.bot.MyBot;
import org.example.entity.enums.UserStatus;

@Data
@Builder
public class TelegramUser {
    private Long chatId;
    private String phone;
    private UserStatus status;
    private String comment;
    private String clientsStatus;
    private Integer messageToDelete;

    public void deleteMessage() {
        DeleteMessage deleteMessage=new DeleteMessage(chatId,messageToDelete);
        MyBot.telegramBot.execute(deleteMessage);
    }
}
