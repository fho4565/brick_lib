package com.arc_studio.brick_lib.api.network;

//? if >= 1.20.6 {
import com.arc_studio.brick_lib.compatibility.V1201;
//?}
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import org.joml.Vector3f;

/**
 * 网络数据包内容处理器，封装了对 {@link FriendlyByteBuf} 的读写操作，
 * 提供类型安全的链式读写方法，用于模组网络通信中数据的序列化与反序列化。
 */
public class PacketContent {
    protected final FriendlyByteBuf friendlyByteBuf;

    public PacketContent() {
        friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());
    }

    public PacketContent(FriendlyByteBuf friendlyByteBuf) {
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

    public PacketContent writeByteArray(byte[] bytes) {
        friendlyByteBuf.writeByteArray(bytes);
        return this;
    }

    public PacketContent writeItemStack(ItemStack itemStack) {
        //? if >= 1.20.6 {
        V1201.writeItemStack(friendlyByteBuf,itemStack);
        //?} else {
        /*friendlyByteBuf.writeItem(itemStack);
        *///?}
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
        //? if >= 1.20.6 {
        V1201.writeGameProfile(friendlyByteBuf,gameProfile);
        //?} else {
        /*friendlyByteBuf.writeGameProfile(gameProfile);
        *///?}
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

    public byte[] readByteArray() {
        return friendlyByteBuf.readByteArray();
    }

    public byte[] readBytes(int length) {
        return friendlyByteBuf.readByteArray(length);
    }

    public ItemStack readItemStack() {
        //? if >= 1.20.6 {
        return V1201.readItemStack(friendlyByteBuf);
        //?} else {
        /*return friendlyByteBuf.readItem();
        *///?}
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

    public <T> ResourceKey<T> readResourceKey(ResourceKey<? extends Registry<T>> registryKey) {
        return friendlyByteBuf.readResourceKey(registryKey);
    }

    public CompoundTag readNBT() {
        return friendlyByteBuf.readNbt();
    }

    public GameProfile readGameProfile() {
        //? if >= 1.20.6 {
        return V1201.readGameProfile(friendlyByteBuf);
        //?} else {
        /*return friendlyByteBuf.readGameProfile();
        *///?}
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

    public FriendlyByteBuf friendlyByteBuf() {
        return new FriendlyByteBuf(this.friendlyByteBuf.retainedDuplicate());
    }
}


