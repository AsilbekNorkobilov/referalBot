package org.example.db;

import org.example.entity.Flayers;
import org.example.entity.TelegramUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public interface DB {
    List<TelegramUser> TELEGRAM_USERS=new ArrayList<>();
    //ConcurrentHashMap<Long, TelegramUser> TELEGRAM_USERS=new ConcurrentHashMap<>();
    List<Flayers> FLAYERS=new ArrayList<>();

    static Flayers getFlayer(String number) {
        List<Flayers> flayers= FLAYERS.stream()
                .filter(item->item.getPhone().equals(number))
                .limit(1)
                .toList();
        if (flayers.isEmpty()){
            return null;
        }
        return flayers.get(0);
    }

    static TelegramUser getTelegramUserByPhone(String phone) {
        return TELEGRAM_USERS.stream()
                .filter(telegramUser -> telegramUser.getPhone().equals(phone))
                .limit(1)
                .toList().get(0);
    }

    static Flayers createFlayer(String text) {
        for (Flayers flayer : FLAYERS) {
            if (flayer.getPhone().equals(text)){
                return null;
            }
        }
        Flayers flayers=Flayers.builder()
                .phone(text)
                .countOfReferrals(0).build();
        FLAYERS.add(flayers);
        return flayers;
    }
}
