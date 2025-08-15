/*
 * Copyright (c) 2025 arc_studio.
 *
 * Licensed under the LGPL v3 License.
 * (you may not receive this file in the LGPL v3 License)
 */

package com.arc_studio.brick_lib.tools.placer;

import com.arc_studio.brick_lib.api.core.Direction4;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction8;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * 树放置器，提供了上下左右前后移动的方法
 *
 * @author arc_studio
 */
@ApiStatus.Experimental
public class TreePlacer extends Placer {
    private final BlockState trunk, leaves, roots;

    private TreePlacer(BlockState trunk, BlockState leaves, BlockState roots) {
        this.trunk = trunk;
        this.leaves = leaves;
        this.roots = roots;
    }

    private TreePlacer(BlockState trunk, BlockState leaves) {
        this(trunk, leaves, Blocks.AIR.defaultBlockState());
    }

    /**
     * 获取树放置器的实例
     *
     * @param trunk         树干方块
     * @param leaves        树叶方块
     * @param roots         树根方块
     */
    public static TreePlacer of(BlockState trunk, BlockState leaves, BlockState roots) {
        return new TreePlacer(trunk, leaves, roots);
    }

    /**
     * 获取树放置器的实例，默认树根方块是空气
     *
     * @param trunk         树干方块
     * @param leaves        树叶方块
     */
    public static TreePlacer of(BlockState trunk, BlockState leaves) {
        return new TreePlacer(trunk, leaves);
    }


    /**
     * 移动并放置放置树干方块
     *
     * @param moveDirection 移动方向
     */
    public TreePlacer moveAndPlaceTrunk(MoveDirection moveDirection) {
        moveAndPlaceBlock(moveDirection, trunk);
        return this;
    }

    /**
     * 重复移动并放置放置树干方块
     *
     * @param moveDirection 移动方向
     * @param times         重复次数
     */
    public TreePlacer moveAndPlaceTrunk(MoveDirection moveDirection, IntProvider times) {
        int sample = times.sample(RANDOM_SOURCE);
        for (int i = 0; i < sample; i++) {
            moveAndPlaceTrunk(moveDirection);
        }
        return this;
    }

    /**
     * 移动并放置放置树叶方块
     *
     * @param moveDirection 移动方向
     */
    public TreePlacer moveAndPlaceLeaves(MoveDirection moveDirection) {
        moveAndPlaceBlock(moveDirection, leaves);
        return this;
    }

    /**
     * 重复移动并放置放置树叶方块
     *
     * @param moveDirection 移动方向
     * @param times         重复次数
     */
    public TreePlacer moveAndPlaceLeaves(MoveDirection moveDirection, IntProvider times) {
        int sample = times.sample(RANDOM_SOURCE);
        for (int i = 0; i < sample; i++) {
            moveAndPlaceLeaves(moveDirection);
        }
        return this;
    }

    /**
     * 移动并放置放置树根方块
     *
     * @param moveDirection 移动方向
     */
    public TreePlacer moveAndPlaceRoots(MoveDirection moveDirection) {
        moveAndPlaceBlock(moveDirection, roots);
        return this;
    }

    /**
     * 重复移动并放置放置树根方块
     *
     * @param moveDirection 移动方向
     * @param times         重复次数
     */
    public TreePlacer moveAndPlaceRoots(MoveDirection moveDirection, IntProvider times) {
        int sample = times.sample(RANDOM_SOURCE);
        for (int i = 0; i < sample; i++) {
            moveAndPlaceRoots(moveDirection);
        }
        return this;
    }

    public TreePlacer placeRoots() {
        placeBlock(roots);
        return this;
    }

    public TreePlacer placeTrunk() {
        placeBlock(trunk);
        return this;
    }

    public TreePlacer placeTrunk(Direction.Axis axis) {
        placeBlock(trunk.setValue(BlockStateProperties.AXIS, axis));
        return this;
    }
    public TreePlacer placeTrunk(MoveDirection moveDirection) {
        int dx = 0, dy = 0, dz = 0;
        switch (moveDirection) {
            case UP -> dy = 1;
            case DOWN -> dy = -1;
            case LEFT -> {
                switch (this.formerDirection) {
                    case X -> dz = 1;
                    case Z -> dx = -1;
                    case NX -> dz = -1;
                    case NZ -> dx = 1;
                }
            }
            case RIGHT -> {
                switch (this.formerDirection) {
                    case X -> dz = -1;
                    case Z -> dx = 1;
                    case NX -> dz = 1;
                    case NZ -> dx = -1;
                }
            }
            case BACK -> {
                switch (this.formerDirection) {
                    case X -> dx = -1;
                    case Z -> dz = -1;
                    case NX -> dx = 1;
                    case NZ -> dz = 1;
                }
            }
            case FORMER -> {
                switch (this.formerDirection) {
                    case X -> dx = 1;
                    case Z -> dz = 1;
                    case NX -> dx = -1;
                    case NZ -> dz = -1;
                }
            }
        }
        Direction.Axis axis = Direction.Axis.Y;
        if (trunk.hasProperty(BlockStateProperties.AXIS)) {
            if (Math.abs(dx) == 1) {
                axis = Direction.Axis.X;
            } else if (Math.abs(dz) == 1) {
                axis = Direction.Axis.Z;
            }
        }
        placeBlock(trunk.setValue(BlockStateProperties.AXIS, axis));
        return this;
    }

    public TreePlacer placeLeaves() {
        placeBlock(leaves);
        return this;
    }

    public TreePlacer place3x3Leaves(IntProvider maxVacancy) {
        int vacancy = Mth.clamp(4-maxVacancy.sample(RANDOM_SOURCE), 0, 4);
        ArrayList<Direction8> corners = new ArrayList<>(List.of(Direction8.NORTH_EAST, Direction8.NORTH_WEST, Direction8.SOUTH_EAST, Direction8.SOUTH_WEST));
        Collections.shuffle(corners);
        Stream.concat(corners.stream().limit(vacancy), Stream.of(Direction8.NORTH,Direction8.WEST,Direction8.EAST,Direction8.SOUTH)).forEach(direction8 -> offsetAndPlaceBlock(BlockPos.containing(direction8.getStepX(), 0, direction8.getStepZ()), leaves));
        placeLeaves();
        return this;
    }

    public TreePlacer place4x4Leaves(int forwardOffset, int leftOffset, IntProvider maxVacancy) {
        int ox = 0, oz = 0;
        switch (this.formerDirection) {
            case X -> {
                ox = forwardOffset;
                oz = leftOffset;
            }
            case Z -> {
                ox = -leftOffset;
                oz = forwardOffset;
            }
            case NX -> {
                ox = -forwardOffset;
                oz = -leftOffset;
            }
            case NZ -> {
                ox = leftOffset;
                oz = -forwardOffset;
            }
        }
        int[][] blocks = new int[][]{
                {1,1,1,1},
                {1,1,1,1},
                {1,1,1,1},
                {1,1,1,1}
        };
        int vacancy = Mth.clamp(maxVacancy.sample(RANDOM_SOURCE), 0, 4);
        ArrayList<Pair<Integer, Integer>> corners = new ArrayList<>(List.of(Pair.of(0, 0), Pair.of(0, 3), Pair.of(3, 0), Pair.of(3, 3)));
        Collections.shuffle(corners);
        corners.stream().limit(vacancy).forEach(pair -> blocks[pair.getLeft()][pair.getRight()] = 0);
        for (int x = 0; x < blocks.length; x++) {
            for (int z = 0; z < blocks.length; z++) {
                if(blocks[x][z] == 1){
                        offsetAndPlaceBlock(BlockPos.containing(x+ox,0,z+oz),leaves);
                }
            }
        }
        return this;
    }

    public TreePlacer place4Leaves(IntProvider maxVacancy) {
        int vacancy = Mth.clamp(4-maxVacancy.sample(RANDOM_SOURCE), 0, 4);
        ArrayList<Direction4> d = new ArrayList<>(List.of(Direction4.values()));
        Collections.shuffle(d);
        d.stream().limit(vacancy).forEach(direction4 -> offsetAndPlaceBlock(BlockPos.containing(direction4.getStepX(), 0, direction4.getStepZ()), leaves));
        return this;
    }

}
