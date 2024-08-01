package org.example.bot;

import com.pengrad.telegrambot.model.request.*;
import org.example.db.DB;
import org.example.entity.Admin;
import org.example.entity.TelegramUser;

import java.util.ArrayList;
import java.util.List;

public class BotUtils {
    public static Keyboard getContactBtn() {
        KeyboardButton keyboardButton=new KeyboardButton("Kontakt jo'natish");
        keyboardButton.requestContact(true);
        ReplyKeyboardMarkup replyKeyboardMarkup=new ReplyKeyboardMarkup(keyboardButton);
        replyKeyboardMarkup.resizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public static Keyboard getAdminMenuBtn() {
        ReplyKeyboardMarkup replyKeyboardMarkup=new ReplyKeyboardMarkup(new String[][]{
                {BotConstant.CREATE_FLAYER,BotConstant.SEARCH_FLAYER},
                {BotConstant.SHOW_FLAYERS,BotConstant.SHOW_CLIENTS},
                {BotConstant.COUNT_OF_REFERRALS}
        });
        replyKeyboardMarkup.resizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public static InlineKeyboardMarkup getClientsBtn(List<TelegramUser> categoryUsers) {
        List<TelegramUser> users = categoryUsers.stream()
                .skip(9L * Admin.currentPage)
                .limit(9)
                .toList();
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        for (TelegramUser telegramUser : users) {
            inlineKeyboardMarkup.addRow(
                    new InlineKeyboardButton(telegramUser.getClientsStatus()).callbackData(telegramUser.getPhone()+"/"+BotConstant.NUMBER)
            );
        }
        inlineKeyboardMarkup.addRow(
                new InlineKeyboardButton("⬅️").callbackData(BotConstant.PREVIOUS),
                new InlineKeyboardButton(BotConstant.BACK_TO_MENU).callbackData(BotConstant.BACK_TO_MENU),
                new InlineKeyboardButton("➡️").callbackData(BotConstant.NEXT)
        );
        return inlineKeyboardMarkup;
    }


    public static Keyboard getBackBtn() {
        ReplyKeyboardMarkup replyKeyboardMarkup=new ReplyKeyboardMarkup(BotConstant.BACK);
        replyKeyboardMarkup.resizeKeyboard(true);
        return replyKeyboardMarkup;
    }
    public static Keyboard getBackToMenuBtn() {
        ReplyKeyboardMarkup replyKeyboardMarkup=new ReplyKeyboardMarkup(BotConstant.BACK_TO_MENU);
        replyKeyboardMarkup.resizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public static Keyboard getEmojiBtn() {
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        inlineKeyboardMarkup.addRow(
                new InlineKeyboardButton("Rozi bo'ldi ✅").callbackData(BotConstant.AGREE),
                new InlineKeyboardButton("Shartnoma qilindi \uD83D\uDCCB").callbackData(BotConstant.AGREEMENT)
        );
        inlineKeyboardMarkup.addRow(new InlineKeyboardButton("Telefon qilish kerak \uD83D\uDCDE").callbackData(BotConstant.PHONE));
        inlineKeyboardMarkup.addRow(
                new InlineKeyboardButton("O'ylayabti \uD83E\uDD14").callbackData(BotConstant.IN_PROGRESS),
                new InlineKeyboardButton("Rad etdi \uD83D\uDEAB").callbackData(BotConstant.IGNORE)
        );
        inlineKeyboardMarkup.addRow(new InlineKeyboardButton("Izoh qoldirish\uD83D\uDCDD").callbackData(BotConstant.COMMENT));
        inlineKeyboardMarkup.addRow(new InlineKeyboardButton(BotConstant.BACK).callbackData(BotConstant.BACK));
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup getPageBtn(int startIndex, int endIndex) {
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        if (endIndex-startIndex>5){
            List<InlineKeyboardButton> btn0=new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                btn0.add(new InlineKeyboardButton(String.valueOf(i)).callbackData(String.valueOf(i)));
            }
            inlineKeyboardMarkup.addRow(btn0.toArray(new InlineKeyboardButton[0]));
            List<InlineKeyboardButton> btn1=new ArrayList<>();
            for (int i = 6; i <=endIndex-startIndex ; i++) {
                btn1.add(new InlineKeyboardButton(String.valueOf(i)).callbackData(String.valueOf(i)));
            }
            inlineKeyboardMarkup.addRow(btn1.toArray(new InlineKeyboardButton[0]));
        }else {
            List<InlineKeyboardButton> btn0=new ArrayList<>();
            for (int i = 1; i <= endIndex-startIndex; i++) {
                btn0.add(new InlineKeyboardButton(String.valueOf(i)).callbackData(String.valueOf(i)));
            }
            inlineKeyboardMarkup.addRow(btn0.toArray(new InlineKeyboardButton[0]));
        }
        inlineKeyboardMarkup.addRow(
                new InlineKeyboardButton("⬅️").callbackData(BotConstant.PREVIOUS),
                new InlineKeyboardButton(BotConstant.BACK_TO_MENU).callbackData(BotConstant.BACK_TO_MENU),
                new InlineKeyboardButton("➡️").callbackData(BotConstant.NEXT)
        );
        return inlineKeyboardMarkup;
    }

    public static Keyboard getClientsCategoryBtn() {
        ReplyKeyboardMarkup replyKeyboardMarkup=new ReplyKeyboardMarkup(new String[][]{
                {"Yangilar \uD83C\uDD95","Telefon qilish kerak \uD83D\uDCDE"},
                {"O'ylayabti \uD83E\uDD14","Rad etdi \uD83D\uDEAB"},
                {"Rozi bo'ldi ✅","Shartnoma qilindi \uD83D\uDCCB"},
                {BotConstant.BACK_TO_MENU}
        });
        replyKeyboardMarkup.resizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public static Keyboard getFlayersBtn() {
        ReplyKeyboardMarkup replyKeyboardMarkup=new ReplyKeyboardMarkup(new String [][]{
                {BotConstant.SELECTED_FLAYER,BotConstant.VOUCHER},
            {BotConstant.BACK_TO_MENU}
        });
        replyKeyboardMarkup.resizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public static Keyboard getUsersBtn() {
        ReplyKeyboardMarkup replyKeyboardMarkup=new ReplyKeyboardMarkup(new String []{
                BotConstant.USERS_FLAYER,BotConstant.USERS_REFERRALS
        }
        );
        replyKeyboardMarkup.resizeKeyboard(true);
        return replyKeyboardMarkup;
    }
}
