package com.fho4565.brick_lib.core.providers.blockstate;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockStateProvider {
    public abstract BlockState sample(RandomSource source);
}
