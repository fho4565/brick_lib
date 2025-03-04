package com.fho4565.brick_lib.network;

import com.fho4565.brick_lib.Constants;
import com.fho4565.brick_lib.compact.V1201;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.function.Consumer;

public class PacketContent {
    protected RegistryFriendlyByteBuf friendlyByteBuf;

    public PacketContent() {
        friendlyByteBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), Constants.currentServer().registryAccess());
    }

    public PacketContent(RegistryFriendlyByteBuf friendlyByteBuf) {
        this.friendlyByteBuf = friendlyByteBuf;
    }

    public PacketContent writeUTF(String string, int maxLength) {
        friendlyByteBuf.writeUtf(string.substring(0, Math.min(string.length(), maxLength)));
        return this;
    }
    public PacketContent writeBoolean(boolean bool) {
        friendlyByteBuf.writeBoolean(bool);
        return this;
    }
    public PacketContent writeInt(int i) {
        friendlyByteBuf.writeInt(i);
        return this;
    }
    public PacketContent writeLong(long l) {
        friendlyByteBuf.writeLong(l);
        return this;
    }
    public PacketContent writeDouble(double d) {
        friendlyByteBuf.writeDouble(d);
        return this;
    }
    public PacketContent writeFloat(float f) {
        friendlyByteBuf.writeFloat(f);
        return this;
    }
    public PacketContent writeShort(short s) {
        friendlyByteBuf.writeShort(s);
        return this;
    }
    public PacketContent writeByte(byte b) {
        friendlyByteBuf.writeByte(b);
        return this;
    }
    public PacketContent writeBytes(byte[] bytes) {
        friendlyByteBuf.writeBytes(bytes);
        return this;
    }
    public PacketContent writeItemStack(ItemStack itemStack) {
        V1201.writeItemStack(friendlyByteBuf, itemStack);
        return this;
    }

    public PacketContent writeUTF(String string) {
        friendlyByteBuf.writeUtf(string);
        return this;
    }
    public PacketContent writePosition(BlockPos blockPos) {
        friendlyByteBuf.writeBlockPos(blockPos);
        return this;
    }
    public PacketContent writeVector3f(Vector3f vector3f) {
        friendlyByteBuf.writeVector3f(vector3f);
        return this;
    }
    public PacketContent writeResourceLocation(ResourceLocation resourceLocation) {
        friendlyByteBuf.writeResourceLocation(resourceLocation);
        return this;
    }
    public PacketContent writeResourceKey(ResourceKey<?> resourceKey) {
        friendlyByteBuf.writeResourceKey(resourceKey);
        return this;
    }
    public PacketContent writeNBT(CompoundTag compoundTag) {
        friendlyByteBuf.writeNbt(compoundTag);
        return this;
    }
    public PacketContent writeGameProfile(GameProfile gameProfile) {
        V1201.writeGameProfile(friendlyByteBuf,gameProfile);
        return this;
    }
    public PacketContent writeChunkPos(ChunkPos chunkPos) {
        friendlyByteBuf.writeChunkPos(chunkPos);
        return this;
    }
    public int readInt() {
        return friendlyByteBuf.readInt();
    }
    public long readLong() {
        return friendlyByteBuf.readLong();
    }
    public double readDouble() {
        return friendlyByteBuf.readDouble();
    }
    public float readFloat() {
        return friendlyByteBuf.readFloat();
    }
    public short readShort() {
        return friendlyByteBuf.readShort();
    }
    public byte readByte() {
        return friendlyByteBuf.readByte();
    }
    public byte[] readBytes(int length) {
        return friendlyByteBuf.readByteArray(length);
    }
    public ItemStack readItemStack() {
        if (!this.readBoolean()) {
            return ItemStack.EMPTY;
        } else {
            Item item = V1201.readById(this.friendlyByteBuf,BuiltInRegistries.ITEM);
            int i = this.readByte();
            ItemStack itemstack = null;
            if (item != null) {
                itemstack = new ItemStack(item, i);
                Optional<Pair<DataComponentMap, Tag>> result = DataComponentMap.CODEC.decode(NbtOps.INSTANCE, this.friendlyByteBuf.readNbt()).result();
                if(result.isPresent()){
                    itemstack.applyComponents(result.get().getFirst());
                }

            }
            return itemstack;
        }
    }
    public String readUTF() {
        return friendlyByteBuf.readUtf();
    }
    public BlockPos readPosition() {
        return friendlyByteBuf.readBlockPos();
    }
    public Vector3f readVector3f() {
        return friendlyByteBuf.readVector3f();
    }
    public ResourceLocation readResourceLocation() {
        return friendlyByteBuf.readResourceLocation();
    }
    public<T> ResourceKey<T> readResourceKey(ResourceKey<? extends Registry<T>> registryKey) {
        return friendlyByteBuf.readResourceKey(registryKey);
    }
    public CompoundTag readNBT() {
        return friendlyByteBuf.readNbt();
    }
    public GameProfile readGameProfile() {
        return V1201.readGameProfile(friendlyByteBuf);
    }
    public ChunkPos readChunkPos() {
        return friendlyByteBuf.readChunkPos();
    }
    public boolean readBoolean() {
        return friendlyByteBuf.readBoolean();
    }
    public String readUTF(int maxLength) {
        return friendlyByteBuf.readUtf(maxLength);
    }
}
