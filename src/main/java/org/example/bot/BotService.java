package org.example.bot;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.example.db.DB;
import org.example.entity.Admin;
import org.example.entity.Flayers;
import org.example.entity.TelegramUser;
import org.example.entity.enums.AdminStatus;
import org.example.entity.enums.UserStatus;
import org.example.service.QRService;
import org.example.service.UserService;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotService {
    public static void askContact(TelegramUser currentUser, String text) {
        String[] split = text.split(" ");
        if (split.length>1){
            String number=split[1];
            addReferral(number);
        }
        SendMessage sendMessage=new SendMessage(
                currentUser.getChatId(),
                "Assalomu aleykum. Botimizga xush kelibsiz. Iltimos kontaktingizni qoldiring");
        sendMessage.replyMarkup(BotUtils.getContactBtn());
        currentUser.setStatus(UserStatus.ASK_CONTACT);
        SendResponse res = MyBot.telegramBot.execute(sendMessage);
        currentUser.setMessageToDelete(res.message().messageId());
    }

    private static void addReferral(String number) {
        Flayers flayer = DB.getFlayer(number);
        if (flayer==null) return;
        flayer.setCountOfReferrals(flayer.getCountOfReferrals()+1);
    }

    public static void acceptContactSendMessage(TelegramUser currentUser, Message message) {
        currentUser.deleteMessage();
        String phone = message.contact().phoneNumber();
        currentUser.setPhone(phone);
        UserService.hasFlayer(currentUser);
        if (currentUser.getStatus().equals(UserStatus.HAS_FLAYER)){
            sendUsersFlayerBtn(currentUser);
            return;
        }
        currentUser.setComment(phone+":");
        currentUser.setClientsStatus(phone+" \uD83C\uDD95");
        currentUser.setStatus(UserStatus.WAITING);
        SendMessage sendMessage=new SendMessage(currentUser.getChatId(),"Raxmat. Biz siz bilan albatta bog'lanamiz");
        MyBot.telegramBot.execute(sendMessage);

    }

    public static void showAdminMenu() {
        Admin.currentPage=0;
        Admin.deleteMessage();
        SendMessage sendMessage=new SendMessage(Admin.chatId,"Menu");
        sendMessage.replyMarkup(BotUtils.getAdminMenuBtn());
        Admin.status= AdminStatus.MENU;
        SendResponse res = MyBot.telegramBot.execute(sendMessage);
        Admin.deleteMessageId.add(res.message().messageId());
    }

    public static void acceptMenuAskNumber() {
        Admin.deleteMessage();
        SendMessage sendMessage=new SendMessage(Admin.chatId,
        "Iltimos telefon raqam kiriting(998XXXXXXXXX formatda)");
        sendMessage.replyMarkup(BotUtils.getBackBtn());
        Admin.status=AdminStatus.ASK_NUMBER;
        SendResponse res = MyBot.telegramBot.execute(sendMessage);
        Admin.deleteMessageId.add(res.message().messageId());
    }


    public static void showClients(String text) {
        String[] split = text.split(" ");
        String emoji = split[split.length-1];
        Admin.categoryUsers = DB.TELEGRAM_USERS.stream()
                .filter(item -> item.getClientsStatus().contains(emoji))
                .toList();
        Admin.deleteMessage();
        if (Admin.categoryUsers.isEmpty()){
            SendMessage sendMessage=new SendMessage(Admin.chatId,"Mijozlar yo'q");
            MyBot.telegramBot.execute(sendMessage);
            showClientsCategory();
            return;
        }
        SendMessage sendMessage=new SendMessage(Admin.chatId,"Mijozlar "+text);
        sendMessage.replyMarkup(BotUtils.getClientsBtn(Admin.categoryUsers));
        Admin.status=AdminStatus.CLINETS_LIST;
        SendResponse res = MyBot.telegramBot.execute(sendMessage);
        Admin.deleteMessageId.add(res.message().messageId());
        Admin.inlineMarkupId=res.message().messageId();
    }

    public static void acceptNumberSendFlayer(String text) {
        if (!checkNumber(text)) {
            SendMessage sendMessage=new SendMessage(Admin.chatId,"Nomer xato kiritildi");
            MyBot.telegramBot.execute(sendMessage);
            acceptMenuAskNumber();
            return;
        }
        Flayers flayer = DB.createFlayer(text);
        if (flayer==null){
            SendMessage sendMessage=new SendMessage(Admin.chatId,"Bunday flayer allaqachon mavjud");
            MyBot.telegramBot.execute(sendMessage);
        } else {
        QRService.madeQR(text);
        flayer.setPhotoPath("photos/combined"+text+".jpg");
        SendPhoto sendPhoto=new SendPhoto(
                Admin.chatId,
                new File(flayer.getPhotoPath())
        );
        MyBot.telegramBot.execute(sendPhoto);
        }
        showAdminMenu();
    }

    private static boolean checkNumber(String text) {
        Pattern pattern=Pattern.compile("998[0-9]{9}");
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }


    public static void askComment(String phone) {
        Admin.deleteMessage();
        Admin.selectedUser= DB.getTelegramUserByPhone(phone);
        SendMessage sendMessage=new SendMessage(Admin.chatId,phone+" :Izoh qoldiring");
        Admin.status=AdminStatus.COMMENT;
        sendMessage.replyMarkup(BotUtils.getBackBtn());
        SendResponse res = MyBot.telegramBot.execute(sendMessage);
        Admin.deleteMessageId.add(res.message().messageId());
    }

    public static void addCommentToUser(Message message) {
        String text = message.text();
        Admin.deleteMessageId.add(message.messageId());
        Admin.selectedUser.setComment(Admin.selectedUser.getPhone()+":"+text);
        Admin.deleteMessage();
        SendMessage sendMessage=new SendMessage(Admin.chatId,"Izoh saqlandi");
        MyBot.telegramBot.execute(sendMessage);
        showClientsCategory();
    }

    public static void showSelectedUsersMenu(String phone) {
        Admin.deleteMessage(Admin.inlineMarkupId);
        Admin.selectedUser = DB.getTelegramUserByPhone(phone);
        String text;
        if (Admin.selectedUser.getComment().split(":").length<2){
            text=Admin.selectedUser.getComment()+"Izoh hali yo'q";
        }else text=Admin.selectedUser.getComment();
        SendMessage sendMessage=new SendMessage(Admin.chatId,text);
        Admin.status=AdminStatus.SELECT_USER;
        sendMessage.replyMarkup(BotUtils.getEmojiBtn());
        SendResponse res = MyBot.telegramBot.execute(sendMessage);
        Admin.deleteMessageId.add(res.message().messageId());
    }

    public static void addStatusToClient(String emoji) {
        switch (emoji) {
            case BotConstant.AGREE ->
                Admin.selectedUser.setClientsStatus(Admin.selectedUser.getPhone()+" ✅");
            case BotConstant.AGREEMENT ->
                    Admin.selectedUser.setClientsStatus(Admin.selectedUser.getPhone()+" \uD83D\uDCCB");
            case BotConstant.PHONE ->
                    Admin.selectedUser.setClientsStatus(Admin.selectedUser.getPhone() + " \uD83D\uDCDE");
            case BotConstant.IN_PROGRESS ->
                    Admin.selectedUser.setClientsStatus(Admin.selectedUser.getPhone() + " \uD83D\uDD04");
            case BotConstant.IGNORE ->
                    Admin.selectedUser.setClientsStatus(Admin.selectedUser.getPhone() + " \uD83D\uDEAB");
        }
        showClientsCategory();
    }
    public static void showFlayersList(){
        Admin.deleteMessage();
        Admin.startIndex=10*Admin.currentPage;
        Admin.endIndex= Math.min(Admin.startIndex+10,DB.FLAYERS.size());
        if (DB.FLAYERS.isEmpty()){
            SendMessage sendMessage=new SendMessage(Admin.chatId,"Flayerlar yo'q");
            MyBot.telegramBot.execute(sendMessage);
            showAdminMenu();
            return;
        }
        StringBuilder pageContent= new StringBuilder();
        for (int i = Admin.startIndex; i <Admin.endIndex ; i++) {
            pageContent.append(i % 10 + 1).append(".").append(DB.FLAYERS.get(i).getPhone()).append(": ").append(DB.FLAYERS.get(i).getCountOfReferrals()).append(" ta").append("\n");
        }
        Admin.status=AdminStatus.SHOW_FLAYERS;
        SendMessage sendMessage=new SendMessage(Admin.chatId,pageContent.toString());
        sendMessage.replyMarkup(BotUtils.getPageBtn(Admin.startIndex,Admin.endIndex));
        SendResponse res = MyBot.telegramBot.execute(sendMessage);
        Admin.deleteMessageId.add(res.message().messageId());
        Admin.inlineMarkupId=res.message().messageId();
    }

    public static void sendSelectedFlayersMenu(String data) {
        Admin.deleteMessage();
        int indexOfFlayer = Integer.parseInt(data)-1;
        Admin.selectedFlayer = DB.FLAYERS.get(Admin.currentPage * 10 + indexOfFlayer);
        FlayersMenu();
    }

    private static void FlayersMenu() {
        SendMessage sendMessage=new SendMessage(Admin.chatId,Admin.selectedFlayer.getPhone()+": "+Admin.selectedFlayer.getCountOfReferrals()+" ta referal");
        sendMessage.replyMarkup(BotUtils.getFlayersBtn());
        Admin.status=AdminStatus.FLAYERS_MENU;
        SendResponse res = MyBot.telegramBot.execute(sendMessage);
        Admin.deleteMessageId.add(res.message().messageId());
    }

    public static void sendEditedFlayersList() {
        Admin.startIndex=10*Admin.currentPage;
        Admin.endIndex= Math.min(Admin.startIndex+10,DB.FLAYERS.size());
        StringBuilder pageContent= new StringBuilder();
        for (int i = Admin.startIndex; i <Admin.endIndex ; i++) {
            pageContent.append(DB.FLAYERS.get(i).getPhone()).append(": ").append(DB.FLAYERS.get(i).getCountOfReferrals()).append(" ta").append("\n");
        }
        EditMessageText editMessageText=new EditMessageText(Admin.chatId,Admin.inlineMarkupId,pageContent.toString());
        editMessageText.replyMarkup(BotUtils.getPageBtn(Admin.startIndex,Admin.endIndex));
        MyBot.telegramBot.execute(editMessageText);
    }

    public static void sendEditedClientsList() {
        EditMessageReplyMarkup editMessageReplyMarkup=new EditMessageReplyMarkup(Admin.chatId,Admin.inlineMarkupId);
        editMessageReplyMarkup.replyMarkup(BotUtils.getClientsBtn(Admin.categoryUsers));
        SendResponse response = (SendResponse) MyBot.telegramBot.execute(editMessageReplyMarkup);
        Admin.deleteMessageId.add(response.message().messageId());
    }

    public static void showClientsCategory() {
        Admin.deleteMessage();
        SendMessage sendMessage=new SendMessage(Admin.chatId,"Kategoriya tanlang");
        sendMessage.replyMarkup(BotUtils.getClientsCategoryBtn());
        Admin.status=AdminStatus.SELECT_CLIENTS_CATEGORY;
        SendResponse res = MyBot.telegramBot.execute(sendMessage);
        Admin.deleteMessageId.add(res.message().messageId());
    }

    public static void searchFlayer() {
        acceptMenuAskNumber();
        Admin.status=AdminStatus.SHOW_FINDED_FLAYER_MENU;
    }

    public static void changeCountOfReferrals() {
        SendMessage sendMessage=new SendMessage(Admin.chatId,"Vaucher uchun kerakli referallar soni: "+Admin.referrals +". Yangi referallar sonini kiriting ");
        sendMessage.replyMarkup(BotUtils.getBackToMenuBtn());
        Admin.status=AdminStatus.CHANGE_REFERRALS;
        MyBot.telegramBot.execute(sendMessage);
    }

    public static void sendFoundFlayerMenu(String text) {
        Admin.selectedFlayer= DB.getFlayer(text);
        if (Admin.selectedFlayer==null){
            SendMessage sendMessage=new SendMessage(Admin.chatId,"Flayer topilmadi");
            MyBot.telegramBot.execute(sendMessage);
            showAdminMenu();
            return;
        }
        FlayersMenu();
    }

    public static void sendFlayersPhoto() {
        SendPhoto sendPhoto=new SendPhoto(Admin.chatId,new File(Admin.selectedFlayer.getPhotoPath()));
        MyBot.telegramBot.execute(sendPhoto);
        FlayersMenu();
    }

    public static void giveVoucher() {
        if (Admin.selectedFlayer.getCountOfReferrals()>Admin.referrals) {
            Admin.selectedFlayer.setCountOfReferrals(Admin.selectedFlayer.getCountOfReferrals()-Admin.referrals);
            SendMessage sendMessage = new SendMessage(Admin.chatId, "Voucher berildi\uD83C\uDF81. Qolgan referallar soni "+ Admin.selectedFlayer.getCountOfReferrals()+" ta");
            MyBot.telegramBot.execute(sendMessage);
        }else {
            SendMessage sendMessage=new SendMessage(Admin.chatId,"Referallar soni yetarli emas\uD83D\uDE1E");
            MyBot.telegramBot.execute(sendMessage);
            FlayersMenu();
        }
    }

    public static void acceptReferrals(String text) {
        try {
            Admin.referrals= Integer.parseInt(text);
            SendMessage sendMessage=new SendMessage(Admin.chatId,"Vaucher olish uchun referallar soni yangilandi "+Admin.referrals+"✅");
            MyBot.telegramBot.execute(sendMessage);
            showAdminMenu();
        }catch (Exception e){
            SendMessage sendMessage=new SendMessage(Admin.chatId,"Xato kiritildi");
            MyBot.telegramBot.execute(sendMessage);
            changeCountOfReferrals();
        }
    }

    private static void sendUsersFlayerBtn(TelegramUser currentUser) {
        SendMessage sendMessage=new SendMessage(currentUser.getChatId(),"Tanlang");
        sendMessage.replyMarkup(BotUtils.getUsersBtn());
        currentUser.setStatus(UserStatus.SHOW_USERS_MENU);
        MyBot.telegramBot.execute(sendMessage);
    }

    public static void sendFlayerToUser(TelegramUser currentUser) {
        Flayers flayers=DB.FLAYERS.stream()
                .filter(item->item.getPhone().equals(currentUser.getPhone()))
                .limit(1)
                .toList().get(0);
        SendPhoto sendPhoto=new SendPhoto(currentUser.getChatId(),new File(flayers.getPhotoPath()));
        MyBot.telegramBot.execute(sendPhoto);
        sendUsersFlayerBtn(currentUser);
    }

    public static void sendReferralsCountToUser(TelegramUser currentUser) {
        System.out.println(currentUser.getPhone());
        Flayers flayers=DB.FLAYERS.stream()
                .filter(item->{
                    System.out.println(item.getPhone());
                    return item.getPhone().equals(currentUser.getPhone());})
                .limit(1)
                .toList().get(0);
        SendMessage sendMessage=new SendMessage(currentUser.getChatId(),flayers.getCountOfReferrals().toString());
        MyBot.telegramBot.execute(sendMessage);
        sendUsersFlayerBtn(currentUser);
    }
}
