package com.arc_studio.brick_lib.core.global_pack;

import com.arc_studio.brick_lib.BrickLib;
import com.arc_studio.brick_lib.core.global_pack.files.GlobalPackFileType;
import com.arc_studio.brick_lib.core.global_pack.files.PackMcMetaFile;
import com.arc_studio.brick_lib.tools.Constants;
import com.arc_studio.brick_lib.register.BrickRegistries;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

public class GlobalPacks {
    public static void read()  {
        try (Stream<Path> stream = Files.walk(Constants.globalPackPath()).skip(1)) {
            stream.forEach(path -> {
                if(isAvailable(path)){
                    try {
                        final PackMcMetaFile packMcMetaFile = new PackMcMetaFile();
                        packMcMetaFile.deserialize(JsonParser.parseString(Files.readString(path.resolve(PackMcMetaFile.fileName()))).getAsJsonObject());
                        tryParse(path.getFileName().toString(),packMcMetaFile,path);
                    } catch (IOException e) {
                        System.out.println("GlobalPacks.read");
                        BrickLib.LOGGER.error(e.toString());
                    }
                }
            });
        } catch (RuntimeException | IOException e) {
            System.out.println("GlobalPacks.read");
            BrickLib.LOGGER.error(e.toString());
        }
    }
    public static boolean createExample(){
        try {
            Path dir = Constants.globalPackPath().resolve("demo global pack");
            dir.toFile().mkdirs();
            File gpMeta = dir.resolve(PackMcMetaFile.fileName()).toFile();
            gpMeta.createNewFile();
            BrickRegistries.GLOBAL_PACK_FILE_TYPE.forEach(globalPackFileType -> {
                 Path dir1 = dir.resolve(globalPackFileType.dirName());
                dir1.toFile().mkdirs();
                File file = dir1.resolve("demo" + globalPackFileType.fileSuffix()).toFile();
                try {
                    file.createNewFile();
                    Files.writeString(file.toPath(),
                            new GsonBuilder().setPrettyPrinting().create()
                                    .toJson(globalPackFileType.createEmpty()),
                            StandardOpenOption.TRUNCATE_EXISTING);
                } catch (IOException e) {
                    System.out.println("GlobalPacks.createExample.lambda");
                    BrickLib.LOGGER.error(e.toString());
                }
            });
            Files.writeString(gpMeta.toPath(), new GsonBuilder().setPrettyPrinting().create().toJson(new PackMcMetaFile(Component.literal("This is a demo global pack")).serialize()));
            return true;
        } catch (IOException e) {
            System.out.println("GlobalPacks.createExample");
            BrickLib.LOGGER.error(e.toString());
            return false;
        }
    }
    private static void tryParse(String name,PackMcMetaFile packMcMetaFile,Path packPath) throws IOException {
        HashMap<GlobalPackFileType, ArrayList<Path>> gpFiles = new HashMap<>();
        try (Stream<Path> stream = Files.walk(packPath,1).skip(1)) {
            stream.forEach(path -> {
                if(path.toFile().isDirectory()){
                    Stream<GlobalPackFileType> typeStream = BrickRegistries.GLOBAL_PACK_FILE_TYPE.values()
                            .stream().filter(globalPackFileType ->
                                    globalPackFileType.dirName().equals(path.getFileName().toString()));
                    typeStream.findAny().ifPresentOrElse(globalPackFileType -> {
                        try (Stream<Path> s2 = Files.walk(path)) {
                            s2.forEach(path2 -> {
                                if(path2.getFileName().toString().endsWith(globalPackFileType.fileSuffix())){
                                    final ArrayList<Path> def = gpFiles.getOrDefault(globalPackFileType, new ArrayList<>());
                                    def.add(path2);
                                    gpFiles.put(globalPackFileType, def);
                                }
                            });
                        } catch (RuntimeException | IOException e) {
                            System.out.println("GlobalPacks.tryParse");
                            BrickLib.LOGGER.error(e.toString());
                        }
                    }, () -> BrickLib.LOGGER.warn("Find invalid global file type {}.Did you forget to register it?",
                            path.getFileName().toString()));
                }
            });
        } catch (RuntimeException e) {
            System.out.println("GlobalPacks.tryParse2");
            BrickLib.LOGGER.error(e.toString());
        }
        String gpName = packPath.getFileName().toString();
        BrickLib.LOGGER.info("Registered Global Pack {}",gpName);
        BrickRegistries.GLOBAL_PACK.register(toSnakeCase(gpName),new GlobalPack(name,packMcMetaFile,gpFiles));
    }
    public static String toSnakeCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // 处理空格、连字符等分隔符
        String result = input.replaceAll("[\\s\\-]+", "_");

        // 在驼峰命名的边界处插入下划线
        result = result.replaceAll("([a-z0-9])([A-Z])", "$1_$2");
        result = result.replaceAll("([A-Z])([A-Z][a-z])", "$1_$2");

        // 转为小写并清理多余的下划线
        result = result.toLowerCase();
        result = result.replaceAll("_+", "_"); // 将多个下划线合并为一个
        result = result.replaceAll("^_|_$", ""); // 去除首尾下划线

        return result;
    }
    private static boolean isAvailable(Path path) {
        if(!path.toFile().isDirectory()){
            return false;
        }
        try {
            return new PackMcMetaFile().deserialize(JsonParser.parseString(Files.readString(path.resolve(PackMcMetaFile.fileName()))).getAsJsonObject());
        } catch (RuntimeException | IOException e) {
            System.out.println("GlobalPacks.isAvailable");
            BrickLib.LOGGER.error(e.toString());
            return false;
        }
    }
}
