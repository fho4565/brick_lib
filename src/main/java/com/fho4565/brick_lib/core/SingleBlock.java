package com.fho4565.brick_lib.core;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * 单个坐标和方块的保存对象
 */
public class SingleBlock {
    protected final BlockPos blockPos;
    @Nullable
    protected final BlockState blockState;

    /**
     *
     */
    public SingleBlock(BlockPos blockPos, @Nullable BlockState blockState) {
        this.blockPos = blockPos;
        this.blockState = blockState;
    }

    public static SingleBlock of(BlockPos blockPos, @Nullable BlockState blockState) {
        return new SingleBlock(blockPos, blockState);
    }

    /**
     * 将此对象序列化成CompoundTag对象
     */
    public CompoundTag serialize() {
        CompoundTag compoundTag = new CompoundTag();
        long pos = blockPos.asLong();
        compoundTag.putLong("pos", pos);
        if (this.blockState != null) {
            Tag blockState = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, this.blockState).get().orThrow();
            compoundTag.put("state", blockState);
        }
        return compoundTag;
    }

    /**
     * 将CompoundTag对象反序列化成SingleBlock对象
     */
    public static SingleBlock deserialize(CompoundTag compoundTag) {
        long pos = compoundTag.getLong("pos");
        BlockState state = null;
        if (compoundTag.contains("state")) {
            state = BlockState.CODEC.decode(NbtOps.INSTANCE, compoundTag.get("state")).result().orElseThrow().getFirst();
        }
        return SingleBlock.of(BlockPos.of(pos), state);
    }

    public BlockPos blockPos() {
        return blockPos;
    }

    @Nullable
    public BlockState blockState() {
        return blockState;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SingleBlock) obj;
        return Objects.equals(this.blockPos, that.blockPos) &&
                Objects.equals(this.blockState, that.blockState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockPos, blockState);
    }

    @Override
    public String toString() {
        return "SingleBlock[" +
                "blockPos=" + blockPos + ", " +
                "blockState=" + blockState + ']';
    }

}
