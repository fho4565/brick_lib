package com.fho4565.brick_lib.core;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

/**
 * 单个坐标和方块的保存对象，并带有NBT
 * */
public record SingleBlockWithNbt(BlockPos blockPos, BlockState blockState, CompoundTag nbt) {

    public static SingleBlockWithNbt of(BlockPos blockPos, BlockState blockState,CompoundTag nbt) {
        return new SingleBlockWithNbt(blockPos, blockState,nbt);
    }

    public CompoundTag serialize() {
        CompoundTag compoundTag = new CompoundTag();
        long pos = blockPos.asLong();
        compoundTag.putLong("pos", pos);
        if (this.blockState != null) {
            Tag blockState = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, this.blockState).get().orThrow();
            compoundTag.put("state", blockState);
        }
        compoundTag.put("nbt", nbt);
        return compoundTag;
    }

    /**
     * 将CompoundTag对象反序列化成SingleBlockWithNbt对象
     */
    public static SingleBlockWithNbt deserialize(CompoundTag compoundTag) {
        long pos = compoundTag.getLong("pos");
        BlockState state = null;
        if (compoundTag.contains("state")) {
            state = BlockState.CODEC.decode(NbtOps.INSTANCE, compoundTag.get("state")).result().orElseThrow().getFirst();
        }
        return SingleBlockWithNbt.of(BlockPos.of(pos), state, compoundTag.getCompound("nbt"));
    }
}
