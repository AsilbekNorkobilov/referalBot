package org.example.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.SneakyThrows;
import org.example.bot.MyBot;
import org.example.entity.Admin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

public class QRService {
    @SneakyThrows
    public static void madeQR(String number){
        SendMessage sendMessage=new SendMessage(Admin.chatId,"Iltimos kuting...");
        MyBot.telegramBot.execute(sendMessage);
        String data="t.me/water_referal_uz_bot?start="+number;
        BitMatrix matrix=new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE,370,370);
        MatrixToImageWriter.writeToPath(matrix,"jpg", Path.of("photos/qr.jpg"));

        BufferedImage qr= ImageIO.read(new File("photos/qr.jpg"));
        BufferedImage flayer= ImageIO.read(new File("photos/flayer.jpg"));

        BufferedImage combined=new BufferedImage(
                flayer.getWidth(),
                flayer.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d=combined.createGraphics();
        g2d.drawImage(flayer,0,0,null);
        g2d.drawImage(qr,285,783,null);
        g2d.dispose();
        ImageIO.write(combined,"png",new File("photos/combined"+number+".jpg"));
    }
}
