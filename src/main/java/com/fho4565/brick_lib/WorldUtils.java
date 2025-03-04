package com.fho4565.brick_lib;

import com.fho4565.brick_lib.core.SingleBlock;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.*;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WorldUtils {
    /**
     * 在指定位置掉落一个指定物品
     *
     * @param level 世界对象
     * @param vec3  掉落物品的位置
     * @param item  需要掉落的物品类型
     */
    public static void dropItem(ServerLevel level, Vec3 vec3, Item item) {
        dropItem(level, vec3, new ItemStack(item));
    }

    /**
     * 在指定位置掉落指定物品堆
     *
     * @param level     世界对象
     * @param vec3      掉落物品堆的位置
     * @param itemStack 需要掉落的物品堆
     */
    public static void dropItem(ServerLevel level, Vec3 vec3, ItemStack itemStack) {
        level.addFreshEntity(getItemEntity(level, vec3, itemStack));
    }

    /**
     * 抽取指定战利品表物品并在指定位置掉落
     *
     * @param level     世界对象
     * @param pos      掉落物品的位置
     * @param lootTable 需要抽取的战利品表
     * @param thisEntity 战利品表中this指代的实体
     */
    public static void dropLootTable(ServerLevel level, Vec3 pos, ResourceLocation lootTable, @NotNull Entity thisEntity) {
        LootParams params = (new LootParams.Builder(level)).withParameter(LootContextParams.THIS_ENTITY, thisEntity).withParameter(LootContextParams.ORIGIN, pos).create(LootContextParamSets.GIFT);
        level.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE,lootTable)).getRandomItems(params).forEach(itemStack -> level.addFreshEntity(getItemEntity(level, pos, itemStack)));
    }

    public static int executeCommand(CommandSourceStack stack, String command) {
        if (command.isEmpty()) {
            return 0;
        }
        stack.getServer().getCommands().performPrefixedCommand(stack, command);
        final int[] result = new int[1];
        stack.withCallback((success, r) -> {
            result[0] = r;
        });
        return result[0];
    }

    public static int executeCommand(CommandSourceStack stack, String command, boolean mute) {
        if (command.isEmpty()) {
            return 0;
        }
        if (mute) {
            stack.getServer().getCommands().performPrefixedCommand(stack.withSuppressedOutput(), command);
        }else{
            stack.getServer().getCommands().performPrefixedCommand(stack, command);
        }
        final int[] result = new int[1];
        stack.withCallback((success, r) -> {
            result[0] = r;
        });
        return result[0];
    }

    public static HashSet<BlockPos> getBlockPoses(BlockPos pos1, BlockPos pos2) {
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
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z < z2; z++) {
                    blockPoses.add(BlockPos.containing(x, y, z));
                }
            }
        }
        return blockPoses;
    }

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
                for (int z = z1; z < z2; z++) {
                    blockPoses.add(BlockPos.containing(x, y, z));
                }
            }
        }
        return blockPoses;
    }

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
                for (int z = z1; z < z2; z++) {
                    blockStates.add(level.getBlockState(BlockPos.containing(x, y, z)));
                }
            }
        }
        return blockStates;
    }

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
                for (int z = z1; z < z2; z++) {
                    singleBlocks.add(SingleBlock.of(BlockPos.containing(x, y, z), level.getBlockState(BlockPos.containing(x, y, z))));
                }
            }
        }
        return singleBlocks;
    }

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
                for (int z = z1; z < z2; z++) {
                    BlockState blockState = level.getBlockState(BlockPos.containing(x, y, z));
                    if (!blockState.isAir()) {
                        singleBlocks.add(SingleBlock.of(BlockPos.containing(x, y, z), blockState));
                    }
                }
            }
        }
        return singleBlocks;
    }

    public static HashSet<SingleBlock> getSingleBlocks(LevelAccessor level, AABB aabb) {
        HashSet<SingleBlock> blockStates = new HashSet<>();
        getBlockPoses(aabb).forEach(blockPos -> blockStates.add(SingleBlock.of(blockPos, level.getBlockState(blockPos))));
        return blockStates;
    }

    public static int getScore(MinecraftServer server, Objective objectiveName, String entityName) {
        Scoreboard scoreboard = server.getScoreboard();
        if (scoreboard.getPlayerScoreInfo(ScoreHolder.forNameOnly(entityName), objectiveName) == null) {
            return 0;
        } else {
            ScoreAccess score = scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(entityName), objectiveName);
            return score.get();
        }
    }

    private static @NotNull ItemEntity getItemEntity(ServerLevel level, Vec3 vec3, ItemStack item) {
        ItemEntity itemEntity = new ItemEntity(level, vec3.x, vec3.y, vec3.z, item.copy());
        itemEntity.setDefaultPickUpDelay();
        return itemEntity;
    }

    public static BlockPos getSurfacePos(LevelAccessor level, int x, int y, int z, int minY, int maxY) {
        return getSurfacePos(level, BlockPos.containing(x, y, z), minY, maxY);
    }

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

    public static BlockPos getSurfacePos(LevelAccessor level, BlockPos blockPos) {
        return getSurfacePos(level, blockPos, level.getMinBuildHeight(), level.getMaxBuildHeight());
    }

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
        AABB box = new AABB(pos.x - radius, pos.y - radius, pos.z - radius,
                pos.x + radius, pos.y + radius, pos.z + radius);
        return level.getEntities(null, box).stream()
                .filter(entity -> entity.position().distanceTo(pos) <= radius).toList();
    }

    public static void playSound(LevelAccessor levelAccessor, double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch) {
        if (levelAccessor instanceof Level level) {
            if (!level.isClientSide()) {
                level.playSound(null, BlockPos.containing(x, y, z), soundEvent, soundSource, volume, pitch);
            } else {
                level.playLocalSound(x, y, z, soundEvent, soundSource, volume, pitch, false);
            }
        }
    }

    public static void playSound(LevelAccessor levelAccessor, double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource) {
        if (levelAccessor instanceof Level level) {
            if (!level.isClientSide()) {
                level.playSound(null, BlockPos.containing(x, y, z), soundEvent, soundSource, 1, 1);
            } else {
                level.playLocalSound(x, y, z, soundEvent, soundSource, 1, 1, false);
            }
        }
    }

    public static void playSoundForPlayer(ServerPlayer serverPlayer, SoundEvent soundEvent, SoundSource soundSource) {
        serverPlayer.connection.send(new ClientboundSoundPacket(Holder.direct(soundEvent),
                soundSource,
                serverPlayer.getX(),
                serverPlayer.getY(),
                serverPlayer.getZ(),
                1,
                1,
                serverPlayer.level().getRandom().nextLong()));

    }

    public static void summonItem(ServerPlayer player, RegistryObject<Item> item) {
        player.level().addFreshEntity(
                new ItemEntity(player.level(),
                        player.getX(), player.getY(), player.getZ(),
                        new ItemStack(item.get())));
    }

    public static LinkedHashSet<BlockPos> getBlocksByRadio(Level level, BlockPos center, int radius) {
        return getAllBlocksByRadio(level, center, radius, block -> true);
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

    public static boolean hasBlockInRadius(Level level, BlockPos center, int radius, Block block) {
        return getBlocksByRadio(level, center, radius).stream().anyMatch(blockPos -> level.getBlockState(blockPos).is(block));
    }


    public static List<Entity> getEntitiesByRadio(Level level, Vec3 pos, double radius, Predicate<Entity> entityPredicate) {
        return getEntitiesByRadio(level, pos, radius).stream()
                .filter(entityPredicate)
                .toList();
    }

}
