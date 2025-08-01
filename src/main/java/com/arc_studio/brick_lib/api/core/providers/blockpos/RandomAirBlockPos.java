package com.arc_studio.brick_lib.api.core.providers.blockpos;

import com.arc_studio.brick_lib.api.tools.WorldUtils;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

/**
 * 范围内随机空气方块坐标的方块坐标提供者
 * */
public class RandomAirBlockPos extends CachedRandomBlockPos {
    public RandomAirBlockPos(LevelAccessor levelAccessor, AABB aabb) {
        super(levelAccessor,aabb);
        refreshCache();
    }

    @Override
    public void refreshCache() {
        WorldUtils.getSingleBlocks(levelAccessor,aabb).forEach(singleBlock -> {
            if (singleBlock.blockState() != null) {
                if(singleBlock.blockState().isAir()){
                    resultCache.add(singleBlock.blockPos());
                }
            }
        });
    }
}
