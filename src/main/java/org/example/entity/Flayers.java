package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.bot.MyBot;

import java.io.File;
import java.net.URI;

@Data
@Builder
public class Flayers {
    private String phone;
    private Integer countOfReferrals;
    private String photoPath;

}
