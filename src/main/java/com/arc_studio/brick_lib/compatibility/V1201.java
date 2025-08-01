package com.arc_studio.brick_lib.compatibility;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.IdMap;
//? if >= 1.20.6 {
import net.minecraft.core.component.DataComponentMap;
//?}
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * 从1.20.1版本的兼容方法
 */
public class V1201 {
    //? if >= 1.20.6 {
    public static FriendlyByteBuf writeItemStack(FriendlyByteBuf buf, ItemStack pStack) {
        if (pStack.isEmpty()) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            Item item = pStack.getItem();
            writeId(buf, BuiltInRegistries.ITEM, item);
            buf.writeByte(pStack.getCount());
            DataComponentMap.CODEC.encodeStart(NbtOps.INSTANCE, pStack.getComponents()).result().ifPresent(new Consumer<Tag>() {
                @Override
                public void accept(Tag tag) {
                    buf.writeNbt(tag);
                }
            });
        }

        return buf;
    }

    public static <T> void writeId(FriendlyByteBuf buf, IdMap<T> pIdMap, T pValue) {
        int i = pIdMap.getId(pValue);
        if (i == -1) {
            throw new IllegalArgumentException("Can't find id for '" + pValue + "' in map " + pIdMap);
        } else {
            buf.writeVarInt(i);
        }
    }

    public static <T> T readById(FriendlyByteBuf buf, IdMap<T> pIdMap) {
        int i = buf.readVarInt();
        return pIdMap.byId(i);
    }

    public static void writeGameProfile(FriendlyByteBuf buf, GameProfile pGameProfile) {
        buf.writeUUID(pGameProfile.getId());
        buf.writeUtf(pGameProfile.getName());
        buf.writeCollection(pGameProfile.getProperties().values(), V1201::writeProperty);
    }

    public static void writeProperty(FriendlyByteBuf buf, Property property) {
        buf.writeUtf(property.name());
        buf.writeUtf(property.value());
        if (property.hasSignature()) {
            buf.writeBoolean(true);
            if (property.signature() != null) {
                buf.writeUtf(property.signature());
            }
        } else {
            buf.writeBoolean(false);
        }

    }

    public static ItemStack readItemStack(FriendlyByteBuf buf) {
        if (!buf.readBoolean()) {
            return ItemStack.EMPTY;
        } else {
            Item item = V1201.readById(buf, BuiltInRegistries.ITEM);
            int i = buf.readByte();
            ItemStack itemstack = null;
            if (item != null) {
                itemstack = new ItemStack(item, i);
                Optional<Pair<DataComponentMap, Tag>> result = DataComponentMap.CODEC.decode(NbtOps.INSTANCE, buf.readNbt()).result();
                if (result.isPresent()) {
                    itemstack.applyComponents(result.get().getFirst());
                }

            }
            return itemstack;
        }
    }

    public static GameProfile readGameProfile(FriendlyByteBuf buf) {
        UUID uuid = buf.readUUID();
        String s = buf.readUtf(16);
        GameProfile gameprofile = new GameProfile(uuid, s);
        gameprofile.getProperties().putAll(readGameProfileProperties(buf));
        return gameprofile;
    }

    private static Multimap<String, Property> readGameProfileProperties(FriendlyByteBuf buf) {
        PropertyMap propertymap = new PropertyMap();
        buf.readWithCount((p_236809_) -> {
            Property property = readProperty(buf);
            propertymap.put(property.name(), property);
        });
        return propertymap;
    }

    public static Property readProperty(FriendlyByteBuf buf) {
        String s = buf.readUtf();
        String s1 = buf.readUtf();
        if (buf.readBoolean()) {
            String s2 = buf.readUtf();
            return new Property(s, s1, s2);
        } else {
            return new Property(s, s1);
        }
    }

    public static boolean equals(ItemStack i1, ItemStack other) {
        if (i1.isEmpty())
            return other.isEmpty();
        else
            return !other.isEmpty() && i1.getCount() == other.getCount() && i1.getItem() == other.getItem() &&
                    (Objects.equals(i1.getComponentsPatch(), other.getComponentsPatch()));
    }
    //?}
}
