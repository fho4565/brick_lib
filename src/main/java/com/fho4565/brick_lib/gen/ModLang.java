package com.fho4565.brick_lib.gen;

import com.fho4565.brick_lib.BrickLib;
import com.fho4565.brick_lib.Constants;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ModLang {
    public static final HashSet<String> languageKeys = new HashSet<>();
    public static final String langPath = Constants.brickLibFolder() + "\\datagen";
    private static final String out = """
            "%s" : "%s",
            """;
    private static int count = 0;

    public static int generate() throws IOException {
        count = 0;
        if(!Constants.isWorldInitiated()){
            BrickLib.LOGGER.error("World variables not initialed");
            return 0;
        }

        File file = new File(langPath+"\\" + Minecraft.getInstance().getLanguageManager().getSelected() + ".json");
        if (!file.exists()) {
            File dir = new File(langPath);
            if (!dir.mkdirs() && !file.createNewFile()) {
                BrickLib.LOGGER.error("Failed to create lang file");
            }
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        writeFile(bufferedWriter, generateContent());
        bufferedWriter.close();
        return count;
    }
    private static String generateContent() {
        return new GsonBuilder().setPrettyPrinting().create().toJsonTree(languageKeys.stream()
                .filter(key -> Component.translatable(key).plainCopy().getString().equals(key))
                .collect(Collectors.toMap( k -> k, v -> "§NO TRANSLATE"))).toString();
    }

    private static void writeFile(BufferedWriter bufferedWriter, String content) throws IOException {
        bufferedWriter.write(content);
        bufferedWriter.flush();
    }
}
