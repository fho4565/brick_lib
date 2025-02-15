package com.fho4565.brick_lib.blocks;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class LogBlock extends RotatedPillarBlock {
    Block stripped = null;
    final Function<BlockState, BlockState> blockBlockStateFunction = blockState -> stripped != null ? stripped.defaultBlockState().setValue(RotatedPillarBlock.AXIS, blockState.getValue(RotatedPillarBlock.AXIS)) : null;

    public LogBlock(Properties properties) {
        super(properties);
    }

    public LogBlock(Properties properties, Block stripped) {
        super(properties);
        this.stripped = stripped;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        if (toolAction == ToolActions.AXE_STRIP) {
            return blockBlockStateFunction.apply(state);
        }
        return super.getToolModifiedState(state, context, toolAction, simulate);
    }
}
