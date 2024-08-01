package org.example.entity;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.bot.BotConstant;
import org.example.bot.BotService;
import org.example.bot.MyBot;
import org.example.db.DB;
import org.example.entity.enums.AdminStatus;

import java.util.ArrayList;
import java.util.List;


public class Admin {
    public static Long chatId=87701653L;
    public static AdminStatus status=AdminStatus.START;
    public static List<Integer> deleteMessageId=new ArrayList<>();
    public static Integer inlineMarkupId;
    public static TelegramUser selectedUser;
    public  static Flayers selectedFlayer;
    public static int currentPage=0;
    public static int startIndex;
    public static int endIndex;
    public static List<TelegramUser> categoryUsers;
    public static int referrals=5;

    public static void deleteMessage( ) {
        for (Integer i : deleteMessageId) {
            DeleteMessage deleteMessage=new DeleteMessage(Admin.chatId,i);
            MyBot.telegramBot.execute(deleteMessage);
        }
        deleteMessageId.clear();
    }
    public static void deleteMessage(Integer integer ) {
        DeleteMessage deleteMessage=new DeleteMessage(Admin.chatId,integer);
        MyBot.telegramBot.execute(deleteMessage);
    }

    public static void adminMenu(Update update) {
        if (update.message() != null) {
            Message message = update.message();
            if (message.text() != null) {
                String text = message.text();
                if (text.equals("/start")) {
                    BotService.showAdminMenu();
                } else if (text.equals(BotConstant.BACK_TO_MENU)) {
                    BotService.showAdminMenu();
                } else if (status.equals(AdminStatus.MENU)) {
                    if (text.equals(BotConstant.CREATE_FLAYER)) {
                        BotService.acceptMenuAskNumber();
                    } else if (text.equals(BotConstant.SHOW_FLAYERS)) {
                        BotService.showFlayersList();
                    } else if (text.equals(BotConstant.SHOW_CLIENTS)) {
                        BotService.showClientsCategory();
                    } else if (text.equals(BotConstant.SEARCH_FLAYER)) {
                        BotService.searchFlayer();
                    } else if (text.equals(BotConstant.COUNT_OF_REFERRALS)) {
                        BotService.changeCountOfReferrals();
                    }
                }else if (status.equals(AdminStatus.CHANGE_REFERRALS)) {
                    BotService.acceptReferrals(text);
                } else if (status.equals(AdminStatus.SHOW_FINDED_FLAYER_MENU)) {
                    BotService.sendFoundFlayerMenu(text);
                } else if (status.equals(AdminStatus.ASK_NUMBER)) {
                    BotService.acceptNumberSendFlayer(text);
                } else if (status.equals(AdminStatus.COMMENT)) {
                    BotService.addCommentToUser(message);
                } else if (status.equals(AdminStatus.SELECT_CLIENTS_CATEGORY)) {
                    BotService.showClients(text);
                } else if (status.equals(AdminStatus.FLAYERS_MENU)) {
                    if (text.equals(BotConstant.SELECTED_FLAYER)){
                        BotService.sendFlayersPhoto();
                    } else if (text.equals(BotConstant.VOUCHER)) {
                        BotService.giveVoucher();
                    } else if (text.equals(BotConstant.BACK)) {
                        BotService.showFlayersList();
                    }
                }
            }
        } else if (update.callbackQuery() != null) {
            CallbackQuery callbackQuery = update.callbackQuery();
            if (callbackQuery.data()!=null){
                String data = callbackQuery.data();
                String[] split = data.split("/");
                if (status.equals(AdminStatus.CLINETS_LIST)){
                    if (data.equals(BotConstant.BACK_TO_MENU)){
                        BotService.showAdminMenu();
                    } else if (data.equals(BotConstant.PREVIOUS) && currentPage != 0) {
                        currentPage--;
                        BotService.sendEditedClientsList();
                    } else if (data.equals(BotConstant.NEXT) && currentPage < (categoryUsers.size()-1) / 9) {
                        currentPage++;
                        BotService.sendEditedClientsList();
                    } else if (Character.isDigit(data.charAt(0))&&split[1].equals(BotConstant.NUMBER)){
                        BotService.showSelectedUsersMenu(split[0]);
                    }
                } else if (status.equals(AdminStatus.SELECT_USER)) {
                    if (data.equals(BotConstant.BACK)){
                        BotService.showClientsCategory();
                    } else if (data.equals(BotConstant.COMMENT)) {
                        BotService.askComment(selectedUser.getPhone());
                    }else {
                        BotService.addStatusToClient(data);
                    }
                } else if (status.equals(AdminStatus.SHOW_FLAYERS)) {
                    if (data.equals(BotConstant.PREVIOUS)&&currentPage!=0){
                        currentPage--;
                        BotService.sendEditedFlayersList();
                    } else if (data.equals(BotConstant.BACK_TO_MENU)) {
                        BotService.showAdminMenu();
                    } else if (data.equals(BotConstant.NEXT)&&currentPage< (DB.FLAYERS.size()-1)/10){
                        currentPage++;
                        BotService.sendEditedFlayersList();
                    } else if (Character.isDigit(data.charAt(0))) {
                        BotService.sendSelectedFlayersMenu(data);
                    }
                }
            }
        }
    }
}
