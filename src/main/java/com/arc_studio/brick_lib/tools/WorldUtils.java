package com.arc_studio.brick_lib.tools;

// WorldUtils 工具类，提供了大量与世界(Level)相关的便捷操作方法
// 包括方块、实体、声音、战利品、命令等常用操作
// 适用于 Minecraft Mod 开发中对世界内容的批量处理、范围查询、掉落、声音播放等场景

import com.arc_studio.brick_lib.api.core.SingleBlock;
import com.arc_studio.brick_lib.api.core.SingleBlockWithNbt;
import com.arc_studio.brick_lib.api.core.interfaces.consumer.BlockPosConsumer;
import com.arc_studio.brick_lib.api.core.interfaces.consumer.BlockStateConsumer;
import com.arc_studio.brick_lib.api.core.interfaces.consumer.SingleBlockConsumer;
import com.arc_studio.brick_lib.api.core.interfaces.consumer.SingleBlockWithNbtConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WorldUtils {
    /**
     * 在指定位置掉落一个指定物品。
     *
     * @param level 世界对象（仅限服务端）
     * @param vec3 掉落物品的位置
     * @param item 需要掉落的物品类型
     */
    public static void dropItem(ServerLevel level, Vec3 vec3, Item item) {
        dropItem(level, vec3, new ItemStack(item));
    }

    /**
     * 在指定位置掉落指定物品堆。
     *
     * @param level 世界对象（仅限服务端）
     * @param vec3 掉落物品堆的位置
     * @param itemStack 需要掉落的物品堆
     */
    public static void dropItem(ServerLevel level, Vec3 vec3, ItemStack itemStack) {
        level.addFreshEntity(getItemEntity(level, vec3, itemStack));
    }

    /**
     * 抽取指定战利品表物品并在指定位置掉落。
     *
     * @param level 世界对象（仅限服务端）
     * @param pos 掉落物品的位置
     * @param lootTable 战利品表ID
     * @param thisEntity 战利品表中this指代的实体
     */
    public static void dropLootTable(ServerLevel level, Vec3 pos, ResourceLocation lootTable, @NotNull Entity thisEntity) {
        // 构造战利品参数，设置掉落实体和原点
        LootParams params = (new LootParams.Builder(level)).withParameter(LootContextParams.THIS_ENTITY, thisEntity).withParameter(LootContextParams.ORIGIN, pos).create(LootContextParamSets.GIFT);
        // 获取战利品表并生成物品掉落
        //? if >= 1.20.6 {
        /*level.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE,lootTable)).getRandomItems(params).forEach(itemStack -> level.addFreshEntity(getItemEntity(level, pos, itemStack)));
        *///?} else {
        level.getServer().getLootData().getLootTable(lootTable).getRandomItems(params).forEach(itemStack -> level.addFreshEntity(getItemEntity(level, pos, itemStack)));
        //?}
    }

    /**
     * 执行一条命令。
     *
     * @param stack 命令执行者
     * @param command 命令字符串
     * @return 命令执行结果
     */
    public static int executeCommand(CommandSourceStack stack, String command) {
        if (command.isEmpty()) {
            return 0;
        }
        //? if >= 1.20.4 {
        /*stack.getServer().getCommands().performPrefixedCommand(stack, command);
        final int[] result = new int[1];
        stack.withCallback((success, r) -> {
            result[0] = r;
        });
        return result[0];
        *///?} else {
        return stack.getServer().getCommands().performPrefixedCommand(stack, command);
        //?}
    }

    /**
     * 执行一条命令，可选择是否静默输出。
     *
     * @param stack 命令执行者
     * @param command 命令字符串
     * @param mute 是否静默输出
     * @return 命令执行结果
     */
    public static int executeCommand(CommandSourceStack stack, String command, boolean mute) {
        if (command.isEmpty()) {
            return 0;
        }
        //? if >= 1.20.4 {
        /*if (mute) {
            stack.getServer().getCommands().performPrefixedCommand(stack.withSuppressedOutput(), command);
        }else{
            stack.getServer().getCommands().performPrefixedCommand(stack, command);
        }
        final int[] result = new int[1];
        stack.withCallback((success, r) -> {
            result[0] = r;
        });
        return result[0];
        *///?} else {
        if (mute) {
            return stack.getServer().getCommands().performPrefixedCommand(stack.withSuppressedOutput(), command);
        }
        return stack.getServer().getCommands().performPrefixedCommand(stack, command);
        //?}
    }

    /**
     * 获取pos1和pos2之间的所有方块坐标（包含两端）。
     *
     * @param pos1 第一个方块坐标
     * @param pos2 第二个方块坐标
     * @return 所有方块坐标的集合
     */
    public static HashSet<BlockPos> getBlockPoses(BlockPos pos1, BlockPos pos2) {
        // 计算包围盒范围
        int x1 = Math.min(pos1.getX(), pos2.getX());
        int y1 = Math.min(pos1.getY(), pos2.getY());
        int z1 = Math.min(pos1.getZ(), pos2.getZ());
        int x2 = Math.max(pos1.getX(), pos2.getX());
        int y2 = Math.max(pos1.getY(), pos2.getY());
        int z2 = Math.max(pos1.getZ(), pos2.getZ());
        int length = Math.abs(x2 - x1);
        int width = Math.abs(y2 - y1);
        int height = Math.abs(z2 - z1);
        HashSet<BlockPos> blockPoses = new HashSet<>(length * width * height);
        // 遍历所有坐标
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    blockPoses.add(BlockPos.containing(x, y, z));
                }
            }
        }
        return blockPoses;
    }

    /**
     * 获取pos1和pos2之间的所有方块坐标并执行consumer操作。
     *
     * @param pos1 第一个方块坐标
     * @param pos2 第二个方块坐标
     * @param consumer 坐标处理函数
     */
    public static void getBlockPosesThen(BlockPos pos1, BlockPos pos2, BlockPosConsumer consumer) {
        int x1 = Math.min(pos1.getX(), pos2.getX());
        int y1 = Math.min(pos1.getY(), pos2.getY());
        int z1 = Math.min(pos1.getZ(), pos2.getZ());
        int x2 = Math.max(pos1.getX(), pos2.getX());
        int y2 = Math.max(pos1.getY(), pos2.getY());
        int z2 = Math.max(pos1.getZ(), pos2.getZ());
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    consumer.accept(BlockPos.containing(x, y, z));
                }
            }
        }
    }

    /**
     * 获取碰撞箱（AABB）表示的所有方块坐标。
     *
     * @param aabb 碰撞箱
     * @return 所有方块坐标集合
     */
    public static HashSet<BlockPos> getBlockPoses(AABB aabb) {
        int x1 = (int) Math.round(aabb.minX);
        int y1 = (int) Math.round(aabb.minY);
        int z1 = (int) Math.round(aabb.minZ);
        int x2 = (int) Math.round(aabb.maxX);
        int y2 = (int) Math.round(aabb.maxY);
        int z2 = (int) Math.round(aabb.maxZ);
        int length = Math.abs(x2 - x1);
        int width = Math.abs(y2 - y1);
        int height = Math.abs(z2 - z1);
        HashSet<BlockPos> blockPoses = new HashSet<>(length * width * height);
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    blockPoses.add(BlockPos.containing(x, y, z));
                }
            }
        }
        return blockPoses;
    }

    /**
     * 获取碰撞箱内所有方块坐标并执行操作。
     *
     * @param aabb 碰撞箱
     * @param consumer 坐标处理函数
     */
    public static void getBlockPosesThen(AABB aabb, BlockPosConsumer consumer) {
        int x1 = (int) Math.round(aabb.minX);
        int y1 = (int) Math.round(aabb.minY);
        int z1 = (int) Math.round(aabb.minZ);
        int x2 = (int) Math.round(aabb.maxX);
        int y2 = (int) Math.round(aabb.maxY);
        int z2 = (int) Math.round(aabb.maxZ);
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    consumer.accept(BlockPos.containing(x, y, z));
                }
            }
        }
    }

    /**
     * 获取pos1和pos2之间的所有方块坐标对应的方块状态。
     *
     * @param level 世界对象
     * @param pos1  第一个方块坐标
     * @param pos2  第二个方块坐标
     * @return 所有方块状态集合
     */
    public static HashSet<BlockState> getBlockStates(LevelAccessor level, BlockPos pos1, BlockPos pos2) {
        int x1 = Math.min(pos1.getX(), pos2.getX());
        int y1 = Math.min(pos1.getY(), pos2.getY());
        int z1 = Math.min(pos1.getZ(), pos2.getZ());
        int x2 = Math.max(pos1.getX(), pos2.getX());
        int y2 = Math.max(pos1.getY(), pos2.getY());
        int z2 = Math.max(pos1.getZ(), pos2.getZ());
        int length = Math.abs(x2 - x1);
        int width = Math.abs(y2 - y1);
        int height = Math.abs(z2 - z1);
        HashSet<BlockState> blockStates = new HashSet<>(length * width * height);
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    blockStates.add(level.getBlockState(BlockPos.containing(x, y, z)));
                }
            }
        }
        return blockStates;
    }

    /**
     * 获取pos1和pos2之间的所有方块坐标对应的方块状态并执行操作
     *
     * @param level    世界对象
     * @param pos1     第一个方块坐标
     * @param pos2     第二个方块坐标
     * @param consumer 方块状态处理函数
     */
    public static void getBlockStatesThen(LevelAccessor level, BlockPos pos1, BlockPos pos2, BlockStateConsumer consumer) {
        int x1 = Math.min(pos1.getX(), pos2.getX());
        int y1 = Math.min(pos1.getY(), pos2.getY());
        int z1 = Math.min(pos1.getZ(), pos2.getZ());
        int x2 = Math.max(pos1.getX(), pos2.getX());
        int y2 = Math.max(pos1.getY(), pos2.getY());
        int z2 = Math.max(pos1.getZ(), pos2.getZ());
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    consumer.accept(level.getBlockState(BlockPos.containing(x, y, z)));
                }
            }
        }
    }

    /**
     * 获取碰撞箱内所有{@link SingleBlock}对象
     *
     * @param level 世界对象
     * @param aabb  碰撞箱
     * @return 所有SingleBlock对象集合
     */
    public static HashSet<SingleBlock> getSingleBlocks(LevelAccessor level, AABB aabb) {
        HashSet<SingleBlock> blockStates = new HashSet<>();
        getBlockPoses(aabb).forEach(blockPos -> blockStates.add(SingleBlock.of(blockPos, level.getBlockState(blockPos))));
        return blockStates;
    }

    /**
     * 获取碰撞箱内所有{@link SingleBlock}对象并执行操作
     *
     * @param level    世界对象
     * @param aabb     碰撞箱
     * @param consumer SingleBlock处理函数
     */
    public static void getSingleBlocksThen(LevelAccessor level, AABB aabb, SingleBlockConsumer consumer) {
        int x1 = (int) Math.round(aabb.minX);
        int y1 = (int) Math.round(aabb.minY);
        int z1 = (int) Math.round(aabb.minZ);
        int x2 = (int) Math.round(aabb.maxX);
        int y2 = (int) Math.round(aabb.maxY);
        int z2 = (int) Math.round(aabb.maxZ);
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    BlockPos blockPos = BlockPos.containing(x, y, z);
                    consumer.accept(SingleBlock.of(blockPos, level.getBlockState(blockPos)));
                }
            }
        }
    }

    /**
     * 获取pos1和pos2之间的所有{@link SingleBlock}对象
     *
     * @param level 世界对象
     * @param pos1  第一个方块坐标
     * @param pos2  第二个方块坐标
     * @return 所有SingleBlock对象集合
     */
    public static HashSet<SingleBlock> getSingleBlocks(LevelAccessor level, BlockPos pos1, BlockPos pos2) {
        int x1 = Math.min(pos1.getX(), pos2.getX());
        int y1 = Math.min(pos1.getY(), pos2.getY());
        int z1 = Math.min(pos1.getZ(), pos2.getZ());
        int x2 = Math.max(pos1.getX(), pos2.getX());
        int y2 = Math.max(pos1.getY(), pos2.getY());
        int z2 = Math.max(pos1.getZ(), pos2.getZ());
        int length = Math.abs(x2 - x1);
        int width = Math.abs(y2 - y1);
        int height = Math.abs(z2 - z1);
        HashSet<SingleBlock> singleBlocks = new HashSet<>(length * width * height);
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    singleBlocks.add(SingleBlock.of(BlockPos.containing(x, y, z), level.getBlockState(BlockPos.containing(x, y, z))));
                }
            }
        }
        return singleBlocks;
    }
    /**
     * 获取pos1和pos2之间的所有{@link SingleBlock}对象并执行操作
     *
     * @param level    世界对象
     * @param pos1     第一个方块坐标
     * @param pos2     第二个方块坐标
     * @param consumer SingleBlock处理函数
     */
    public static void getSingleBlocksThen(LevelAccessor level, BlockPos pos1, BlockPos pos2, SingleBlockConsumer consumer) {
        int x1 = Math.min(pos1.getX(), pos2.getX());
        int y1 = Math.min(pos1.getY(), pos2.getY());
        int z1 = Math.min(pos1.getZ(), pos2.getZ());
        int x2 = Math.max(pos1.getX(), pos2.getX());
        int y2 = Math.max(pos1.getY(), pos2.getY());
        int z2 = Math.max(pos1.getZ(), pos2.getZ());
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    BlockPos blockPos = BlockPos.containing(x, y, z);
                    consumer.accept(SingleBlock.of(blockPos, level.getBlockState(blockPos)));
                }
            }
        }
    }

    /**
     * 获取pos1和pos2之间的所有SingleBlockWithNbt对象并执行操作
     *
     * @param level    世界对象
     * @param pos1     第一个方块坐标
     * @param pos2     第二个方块坐标
     * @param consumer SingleBlockWithNbt处理函数
     */
    public static void getSingleBlockWithNbtThen(LevelAccessor level, BlockPos pos1, BlockPos pos2, SingleBlockWithNbtConsumer consumer) {
        int x1 = Math.min(pos1.getX(), pos2.getX());
        int y1 = Math.min(pos1.getY(), pos2.getY());
        int z1 = Math.min(pos1.getZ(), pos2.getZ());
        int x2 = Math.max(pos1.getX(), pos2.getX());
        int y2 = Math.max(pos1.getY(), pos2.getY());
        int z2 = Math.max(pos1.getZ(), pos2.getZ());
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    BlockPos blockPos = BlockPos.containing(x, y, z);
                    Optional<BlockEntity> blockEntity;
                    if (level instanceof Level level1) {
                        if (level1.hasChunk(SectionPos.blockToSectionCoord(blockPos.getX()), SectionPos.blockToSectionCoord(blockPos.getZ()))) {
                            blockEntity = Optional.ofNullable(level1.getChunk(blockPos).getBlockEntity(blockPos));
                        } else{
                            blockEntity = Optional.empty();
                        }
                    }else{
                        blockEntity = Optional.ofNullable(level.getBlockEntity(blockPos));
                    }
                    blockEntity.ifPresentOrElse(blockEntity1 -> consumer.accept(SingleBlockWithNbt.of(blockPos, level.getBlockState(blockPos), blockEntity1.saveWithFullMetadata(
                            //? if >= 1.20.6 {
                            /*Constants.currentServer().registryAccess()
                            *///?}
                                    ))),
                            () -> consumer.accept(SingleBlockWithNbt.of(blockPos, level.getBlockState(blockPos), new CompoundTag())));
                }
            }
        }
    }

    /**
     * 获取pos1和pos2之间的所有非空气方块的SingleBlock对象
     *
     * @param level 世界对象
     * @param pos1  第一个方块坐标
     * @param pos2  第二个方块坐标
     * @return 非空气SingleBlock对象集合
     */
    public static HashSet<SingleBlock> getSingleBlocksWithoutAir(LevelAccessor level, BlockPos pos1, BlockPos pos2) {
        int x1 = Math.min(pos1.getX(), pos2.getX());
        int y1 = Math.min(pos1.getY(), pos2.getY());
        int z1 = Math.min(pos1.getZ(), pos2.getZ());
        int x2 = Math.max(pos1.getX(), pos2.getX());
        int y2 = Math.max(pos1.getY(), pos2.getY());
        int z2 = Math.max(pos1.getZ(), pos2.getZ());
        int length = Math.abs(x2 - x1);
        int width = Math.abs(y2 - y1);
        int height = Math.abs(z2 - z1);
        HashSet<SingleBlock> singleBlocks = new HashSet<>(length * width * height);
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    BlockState blockState = level.getBlockState(BlockPos.containing(x, y, z));
                    if (!blockState.isAir()) {
                        singleBlocks.add(SingleBlock.of(BlockPos.containing(x, y, z), blockState));
                    }
                }
            }
        }
        return singleBlocks;
    }

    /**
     * 获取pos1和pos2之间的所有非空气方块的SingleBlock对象并执行操作
     *
     * @param level    世界对象
     * @param pos1     第一个方块坐标
     * @param pos2     第二个方块坐标
     * @param consumer SingleBlock处理函数
     */
    public static void getSingleBlocksWithoutAirThen(LevelAccessor level, BlockPos pos1, BlockPos pos2, SingleBlockConsumer consumer) {
        int x1 = Math.min(pos1.getX(), pos2.getX());
        int y1 = Math.min(pos1.getY(), pos2.getY());
        int z1 = Math.min(pos1.getZ(), pos2.getZ());
        int x2 = Math.max(pos1.getX(), pos2.getX());
        int y2 = Math.max(pos1.getY(), pos2.getY());
        int z2 = Math.max(pos1.getZ(), pos2.getZ());
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    BlockState blockState = level.getBlockState(BlockPos.containing(x, y, z));
                    if (!blockState.isAir()) {
                        consumer.accept(SingleBlock.of(BlockPos.containing(x, y, z), blockState));
                    }
                }
            }
        }
    }

    /**
     * 获取指定实体在指定目标分数板目标下的分数
     *
     * @param server        服务器对象
     * @param objectiveName 目标分数板
     * @param entityName    实体名
     * @return 分数
     */
    public static int getScore(MinecraftServer server, Objective objectiveName, String entityName) {
        Scoreboard scoreboard = server.getScoreboard();
        //? if >= 1.20.4 {
        
        /*if (scoreboard.getPlayerScoreInfo(ScoreHolder.forNameOnly(entityName), objectiveName) == null) {
            return 0;
        } else {
            ScoreAccess score = scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(entityName), objectiveName);
            return score.get();
        }
        *///?} else {
        if (!scoreboard.hasPlayerScore(entityName, objectiveName)) {
            return 0;
        } else {
            Score score = scoreboard.getOrCreatePlayerScore(entityName, objectiveName);
            return score.getScore();
        }
        //?}
    }

    /**
     * 构造一个物品掉落实体
     *
     * @param level 世界对象
     * @param vec3  掉落位置
     * @param item  掉落物品堆
     * @return ItemEntity 实体
     */
    private static @NotNull ItemEntity getItemEntity(ServerLevel level, Vec3 vec3, ItemStack item) {
        ItemEntity itemEntity = new ItemEntity(level, vec3.x, vec3.y, vec3.z, item.copy());
        itemEntity.setDefaultPickUpDelay();
        return itemEntity;
    }

    /**
     * 获取指定坐标上方的地表方块坐标（遇到非空气即返回）
     *
     * @param level 世界对象
     * @param x     x坐标
     * @param y     y坐标
     * @param z     z坐标
     * @param minY  最小y
     * @param maxY  最大y
     * @return 地表方块坐标
     */
    public static BlockPos getSurfacePos(LevelAccessor level, int x, int y, int z, int minY, int maxY) {
        return getSurfacePos(level, BlockPos.containing(x, y, z), minY, maxY);
    }

    /**
     * 获取指定坐标上方的地表方块坐标（遇到非空气即返回）
     *
     * @param level  世界对象
     * @param blockPos 坐标
     * @param minY   最小y
     * @param maxY   最大y
     * @return 地表方块坐标
     */
    public static BlockPos getSurfacePos(LevelAccessor level, BlockPos blockPos, int minY, int maxY) {
        int x = blockPos.getX();
        int y = Math.min(maxY, level.getMaxBuildHeight());
        int z = blockPos.getZ();
        boolean isInitialAir = level.getBlockState(BlockPos.containing(x, y, z)).isAir();
        if (!isInitialAir) {
            return blockPos.above();
        }
        boolean isCurrentAir;
        while (y >= minY) {
            y--;
            isCurrentAir = level.getBlockState(BlockPos.containing(x, y, z)).isAir();
            if (!isCurrentAir) {
                return BlockPos.containing(x, y + 1, z);
            }
        }
        return blockPos.above();
    }

    /**
     * 获取指定坐标上方的地表方块坐标（自动取世界高度）
     *
     * @param level    世界对象
     * @param blockPos 坐标
     * @return 地表方块坐标
     */
    public static BlockPos getSurfacePos(LevelAccessor level, BlockPos blockPos) {
        return getSurfacePos(level, blockPos, level.getMinBuildHeight(), level.getMaxBuildHeight());
    }

    /**
     * 获取指定坐标上方的地表方块坐标（自动取世界高度）
     *
     * @param level 世界对象
     * @param x     x坐标
     * @param y     y坐标
     * @param z     z坐标
     * @return 地表方块坐标
     */
    public static BlockPos getSurfacePos(LevelAccessor level, int x, int y, int z) {
        return getSurfacePos(level, x, y, z, level.getMinBuildHeight(), level.getMaxBuildHeight());
    }

    /**
     * 获取球形范围内的所有实体，实体的坐标与中心坐标的距离小于等于半径
     *
     * @param level  世界对象
     * @param pos    中心
     * @param radius 半径
     * @return 指定半径范围内的所有实体
     */
    public static List<Entity> getEntitiesByRadio(Level level, Vec3 pos, double radius) {
        // 构造包围盒
        AABB box = new AABB(pos.x - radius, pos.y - radius, pos.z - radius,
                pos.x + radius, pos.y + radius, pos.z + radius);
        // 过滤距离
        return level.getEntities(null, box).stream()
                .filter(entity -> entity.position().distanceTo(pos) <= radius).toList();
    }

    /**
     * 获取球形范围内的所有实体并执行操作
     *
     * @param level    世界对象
     * @param pos      中心
     * @param radius   半径
     * @param consumer 实体处理函数
     */
    public static void getEntitiesByRadioThen(Level level, Vec3 pos, double radius, Consumer<Entity> consumer) {
        AABB box = new AABB(pos.x - radius, pos.y - radius, pos.z - radius,
                pos.x + radius, pos.y + radius, pos.z + radius);
        level.getEntities(null, box)
                .forEach(entity -> {
                    if (entity.position().distanceTo(pos) <= radius) {
                        consumer.accept(entity);
                    }
                });
    }

    /**
     * 在指定位置播放声音
     *
     * @param levelAccessor 世界对象
     * @param x             x坐标
     * @param y             y坐标
     * @param z             z坐标
     * @param soundEvent    声音事件
     * @param soundSource   声音源
     * @param volume        音量
     * @param pitch         音高
     */
    public static void playSound(LevelAccessor levelAccessor, double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch) {
        if (levelAccessor instanceof Level level) {
            if (!level.isClientSide()) {
                level.playSound(null, BlockPos.containing(x, y, z), soundEvent, soundSource, volume, pitch);
            } else {
                level.playLocalSound(x, y, z, soundEvent, soundSource, volume, pitch, false);
            }
        }
    }

    /**
     * 在指定位置播放声音
     * @param levelAccessor 世界对象
     * @param blockPos      坐标
     * @param soundEvent    声音事件
     * @param soundSource   声音源
     * @param volume        音量
     * @param pitch         音高
     */
    public static void playSound(LevelAccessor levelAccessor, BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch) {
        if (levelAccessor instanceof Level level) {
            if (!level.isClientSide()) {
                level.playSound(null, blockPos, soundEvent, soundSource, volume, pitch);
            } else {
                level.playLocalSound(blockPos, soundEvent, soundSource, volume, pitch, false);
            }
        }
    }

    /**
     * 在指定位置播放声音（默认音量1，音高1）
     */
    public static void playSound(LevelAccessor levelAccessor, double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource) {
        playSound(levelAccessor,x,y,z,soundEvent,soundSource,1, 1);
    }

    /**
     * 在指定位置播放声音（默认音量1，音高1）
     */
    public static void playSound(LevelAccessor levelAccessor, BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource) {
        playSound(levelAccessor,blockPos,soundEvent,soundSource,1, 1);
    }

    /**
     * 仅为指定玩家播放声音
     * @param player 玩家
     * @param soundEvent   声音事件
     * @param soundSource  声音源
     */
    public static void playSoundForPlayer(Player player, SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch) {
        if(player instanceof ServerPlayer serverPlayer){
            SideExecutor.runOnServer(() -> serverPlayer.connection.send(new ClientboundSoundPacket(Holder.direct(soundEvent),
                    soundSource,
                    serverPlayer.getX(),
                    serverPlayer.getY(),
                    serverPlayer.getZ(),
                    volume,
                    pitch,
                    serverPlayer.level().getRandom().nextLong())
            ));
        } else if (player instanceof LocalPlayer localPlayer) {
            SideExecutor.runOnClient(() -> {
                ClientLevel clientLevel = Minecraft.getInstance().level;
                if (clientLevel != null) {
                    clientLevel.playSeededSound(localPlayer,
                            localPlayer.getX(), localPlayer.getY(), localPlayer.getZ(),
                            soundEvent, soundSource,
                            volume, pitch, clientLevel.random.nextLong());
                }
            });
        }
    }

    /**
     * 仅为指定玩家播放声音（默认音量1，音高1）
     * @param player 玩家
     * @param soundEvent   声音事件
     * @param soundSource  声音源
     */
    public static void playSoundForPlayer(Player player, SoundEvent soundEvent, SoundSource soundSource) {
        playSoundForPlayer(player,soundEvent,soundSource,1,1);
    }

    /**
     * 在玩家当前位置召唤一个物品
     *
     * @param player 玩家
     * @param item   物品
     */
    public static void summonItem(ServerPlayer player, Item item) {
        Level level = player.level();
        level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), item.getDefaultInstance()));
    }

    /**
     * 获取球形范围内所有方块坐标
     *
     * @param level  世界对象
     * @param center 中心坐标
     * @param radius 半径
     * @return 所有方块坐标（LinkedHashSet，按距离排序）
     */
    public static LinkedHashSet<BlockPos> getBlocksByRadio(Level level, BlockPos center, int radius) {
        return getAllBlocksByRadio(level, center, radius, block -> true);
    }

    /**
     * 获取球形范围内所有方块坐标并执行操作
     */
    public static void getBlocksByRadioThen(Level level, BlockPos center, int radius, BlockPosConsumer consumer) {
        getAllBlocksByRadioThen(level, center, radius, block -> true, consumer);
    }

    /**
     * 获取指定球形范围内的所有满足条件的方块坐标，坐标会按照离中心距离从小到大排序
     *
     * @param level               Level对象
     * @param center              中心点的坐标
     * @param radius              球形范围半径
     * @param blockStatePredicate 方块测试
     * @return LinkedHashSet对象，保存了满足条件的方块的坐标
     */
    public static LinkedHashSet<BlockPos> getAllBlocksByRadio(Level level, BlockPos center, int radius, Predicate<BlockState> blockStatePredicate) {
        AABB box = new AABB(center.getX() - radius, center.getY() - radius, center.getZ() - radius,
                center.getX() + radius, center.getY() + radius, center.getZ() + radius);
        return BlockPos.betweenClosedStream(box)
                .filter(pos -> center.getCenter().distanceTo(pos.getCenter()) <= radius && blockStatePredicate.test(level.getBlockState(pos)))
                .map(BlockPos::immutable)
                .sorted(Comparator.comparingDouble(pos -> pos.getCenter().distanceToSqr(center.getCenter())))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * 获取指定球形范围内的所有满足条件的方块坐标并执行操作，坐标会按照离中心距离从小到大排序
     */
    public static void getAllBlocksByRadioThen(Level level, BlockPos center, int radius, Predicate<BlockState> blockStatePredicate, BlockPosConsumer consumer) {
        AABB box = new AABB(center.getX() - radius, center.getY() - radius, center.getZ() - radius,
                center.getX() + radius, center.getY() + radius, center.getZ() + radius);
        BlockPos.betweenClosedStream(box)
                .filter(pos -> center.getCenter().distanceTo(pos.getCenter()) <= radius && blockStatePredicate.test(level.getBlockState(pos)))
                .map(BlockPos::immutable)
                .sorted(Comparator.comparingDouble(pos -> pos.getCenter().distanceToSqr(center.getCenter())))
                .forEach(consumer);
    }

    /**
     * 获取指定球形范围内的所有满足条件而且和中心坐标直接或者间接相连(不包括对角线)的的方块坐标，坐标会按照离中心距离从小到大排序
     *
     * @param level               Level对象
     * @param center              中心点的坐标
     * @param radius              球形范围半径
     * @param blockStatePredicate 方块测试
     * @return LinkedHashSet对象，保存了满足条件的方块的坐标
     */
    public static LinkedHashSet<BlockPos> getConnectedBlocksByRadio(Level level, BlockPos center, int radius, Predicate<BlockState> blockStatePredicate) {
        LinkedHashSet<BlockPos> connectedBlocks = new LinkedHashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        HashSet<BlockPos> visited = new HashSet<>();

        queue.offer(center);
        visited.add(center);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                BlockPos current = queue.poll();
                if (current != null) {
                    connectedBlocks.add(current);
                    for (BlockPos neighbor : Arrays.stream(Direction.values())
                            .map(current::relative)
                            .filter(blockPos -> blockStatePredicate.test(level.getBlockState(current)))
                            .toList()) {
                        if (!visited.contains(neighbor) && (neighbor.distSqr(center) <= radius * radius)) {
                            queue.offer(neighbor);
                            visited.add(neighbor);
                        }
                    }
                }
            }
        }
        return connectedBlocks;
    }

    /**
     * 获取指定球形范围内的所有满足条件而且和中心坐标直接或者间接相连(不包括对角线)的的方块坐标并执行操作
     */
    public static void getConnectedBlocksByRadioThen(Level level, BlockPos center, int radius, Predicate<BlockState> blockStatePredicate, BlockPosConsumer consumer) {
        Queue<BlockPos> queue = new LinkedList<>();
        HashSet<BlockPos> visited = new HashSet<>();

        queue.offer(center);
        visited.add(center);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                BlockPos current = queue.poll();
                if (current != null) {
                    consumer.accept(current);
                    for (BlockPos neighbor : Arrays.stream(Direction.values())
                            .map(current::relative)
                            .filter(blockPos -> blockStatePredicate.test(level.getBlockState(blockPos)))
                            .toList()) {
                        if (!visited.contains(neighbor) && (neighbor.distSqr(center) <= radius * radius)) {
                            queue.offer(neighbor);
                            visited.add(neighbor);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取指定球形范围内的所有满足条件而且和中心坐标直接或者间接相连(不包括对角线)的的{@link SingleBlock}并执行操作
     */
    public static void getConnectedSingleBlocksByRadioThen(Level level, BlockPos center, int radius, Predicate<BlockState> blockStatePredicate, SingleBlockConsumer consumer) {
        Queue<BlockPos> queue = new LinkedList<>();
        HashSet<BlockPos> visited = new HashSet<>();

        queue.offer(center);
        visited.add(center);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                BlockPos current = queue.poll();
                if (current != null) {
                    consumer.accept(new SingleBlock(current,level.getBlockState(current)));
                    for (BlockPos neighbor : Arrays.stream(Direction.values())
                            .map(current::relative)
                            .filter(blockPos -> blockStatePredicate.test(level.getBlockState(blockPos)))
                            .toList()) {
                        if (!visited.contains(neighbor) && (neighbor.distSqr(center) <= radius * radius)) {
                            queue.offer(neighbor);
                            visited.add(neighbor);
                        }
                    }
                }
            }
        }
    }

    /**
     * 判断球形范围内是否存在指定方块
     *
     * @param level  世界对象
     * @param center 中心坐标
     * @param radius 半径
     * @param block  方块
     * @return 是否存在
     */
    public static boolean hasBlockInRadius(Level level, BlockPos center, int radius, Block block) {
        return getBlocksByRadio(level, center, radius).stream().anyMatch(blockPos -> level.getBlockState(blockPos).is(block));
    }

    /**
     * 获取球形范围内所有满足条件的实体
     *
     * @param level           世界对象
     * @param pos             中心
     * @param radius          半径
     * @param entityPredicate 实体过滤条件
     * @return 满足条件的实体列表
     */
    public static List<Entity> getEntitiesByRadio(Level level, Vec3 pos, double radius, Predicate<Entity> entityPredicate) {
        AABB box = new AABB(pos.x - radius, pos.y - radius, pos.z - radius,
                pos.x + radius, pos.y + radius, pos.z + radius);
        return level.getEntities(null, box).stream()
                .filter(entity -> entity.position().distanceTo(pos) <= radius && entityPredicate.test(entity))
                .toList();
    }

    /**
     * 获取球形范围内所有满足条件的实体并执行操作
     */
    public static void getEntitiesByRadioThen(Level level, Vec3 pos, double radius, Predicate<Entity> entityPredicate, Consumer<Entity> consumer) {
        AABB box = new AABB(pos.x - radius, pos.y - radius, pos.z - radius,
                pos.x + radius, pos.y + radius, pos.z + radius);
        level.getEntities(null, box)
                .forEach(entity -> {
                    if (entity.position().distanceTo(pos) <= radius && entityPredicate.test(entity)) {
                        consumer.accept(entity);
                    }
                });
    }

    /**
     * 遍历所有数据包文件并执行操作
     *
     * @param types     数据类型（如"recipes"、"advancements"等）
     * @param consumer  资源处理函数
     */
    public static void walkAllDataPackFilesThen(String[] types, BiConsumer<ResourceLocation, Resource> consumer) {
        walkAllDataPackFilesThen(types, resourceLocation -> true, consumer);
    }

    /**
     * 遍历所有数据包文件并执行操作（带过滤）
     *
     * @param types     数据类型
     * @param predicate 资源位置过滤
     * @param consumer  资源处理函数
     */
    public static void walkAllDataPackFilesThen(String[] types, Predicate<ResourceLocation> predicate, BiConsumer<ResourceLocation, Resource> consumer) {
        for (String dataType : types) {
            Constants.currentServer().getResourceManager().listResources(dataType, predicate)
                    .forEach(consumer);
        }
    }
}
