package com.arc_studio.brick_lib.tools;

import com.arc_studio.brick_lib.BrickLib;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class NBTUtils {
    public static String compress(CompoundTag tag) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            NbtIo.writeCompressed(tag, byteArrayOutputStream);
            return byteArrayOutputStream.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            BrickLib.LOGGER.error(e.toString());
            return null;
        }
    }

    public static CompoundTag decompress(String str) {
        try {
            return NbtIo.readCompressed(new ByteArrayInputStream(str.getBytes())/*? >=1.20.4 {*/ /*, NbtAccounter.unlimitedHeap() *//*?} else {*//*?}*/);
        } catch (IOException e) {
            BrickLib.LOGGER.error(e.toString());
            return null;
        }
    }

    public static CompoundTag read(File file) {
        try {
            return NbtIo.read(file/*? >=1.20.4 {*/ /*.toPath() *//*?} else {*//*?}*/);
        } catch (IOException e) {
            BrickLib.LOGGER.error(e.toString());
            return new CompoundTag();
        }
    }

    public static CompoundTag read(Path path) {
        try {
            return NbtIo.read(path/*? <1.20.4 {*/ .toFile() /*?} else {*//*?}*/);
        } catch (IOException e) {
            BrickLib.LOGGER.error(e.toString());
            return new CompoundTag();
        }
    }

    public static void write(CompoundTag tag, File file) {
        try {
            NbtIo.write(tag, file/*? >=1.20.4 {*/ /*.toPath() *//*?} else {*//*?}*/);
        } catch (IOException e) {
            BrickLib.LOGGER.error(e.toString());
        }
    }

    public static void write(CompoundTag tag, Path path) {
        try {
            NbtIo.write(tag, path/*? <1.20.4 {*/ .toFile() /*?} else {*//*?}*/);
        } catch (IOException e) {
            BrickLib.LOGGER.error(e.toString());
        }
    }
}
