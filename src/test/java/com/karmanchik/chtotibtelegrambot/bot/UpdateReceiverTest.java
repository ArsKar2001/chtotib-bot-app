package com.karmanchik.chtotibtelegrambot.bot;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Base64;
import java.util.Locale;

class UpdateReceiverTest {
    @Test
    void testEncode() {
        try {
            final File file = new File("src\\main\\resources\\black_background_red_color_paint_explosion_burst_9844_1920x1080.jpg");
            final byte[] byteArray = FileUtils.readFileToByteArray(file);
            final String bytesEncode = Base64.getEncoder().encodeToString(byteArray);
            System.out.println(file.getName() + " encode - " + bytesEncode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testLocalDate() {
        LocalDate now = LocalDate.now();
        System.out.println(now.getDayOfWeek().getValue());
        System.out.println(now.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ru")));
    }
}