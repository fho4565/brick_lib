package com.fho4565.brick_lib.core.providers.blockpos;

import com.fho4565.brick_lib.WorldUtils;
import com.fho4565.brick_lib.core.SingleBlock;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 范围内随机表面方块坐标的方块坐标提供者
 * */
public class RandomSurfaceBlockPos extends CachedRandomBlockPos {
    public RandomSurfaceBlockPos(LevelAccessor levelAccessor, AABB aabb) {
        super(levelAccessor,aabb);
        refreshCache();
    }

    @Override
    public void refreshCache() {
        resultCache = WorldUtils.getSingleBlocks(levelAccessor, aabb).stream()
                .map(SingleBlock::blockPos)
                .filter(blockPos -> blockPos.getY() == aabb.maxY)
                .map(blockPos -> WorldUtils.getSurfacePos(levelAccessor,blockPos, (int) aabb.minY, (int) aabb.maxY))
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
