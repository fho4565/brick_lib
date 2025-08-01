package com.arc_studio.brick_lib.api.core.providers.blockpos;

import com.arc_studio.brick_lib.api.tools.WorldUtils;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * 范围内随机特定方块坐标的方块坐标提供者
 * */
public class RandomCertainBlockPos extends CachedRandomBlockPos{
    final BlockState blockState;
    public RandomCertainBlockPos(LevelAccessor levelAccessor, AABB aabb, BlockState blockState) {
        super(levelAccessor,aabb);
        this.blockState = blockState;
        refreshCache();
    }

    @Override
    public void refreshCache() {
        WorldUtils.getSingleBlocks(levelAccessor,aabb).forEach(singleBlock -> {
            if (singleBlock.blockState() != null) {
                if(singleBlock.blockState().is(blockState.getBlock())){
                    resultCache.add(singleBlock.blockPos());
                }
            }
        });
    }
}
