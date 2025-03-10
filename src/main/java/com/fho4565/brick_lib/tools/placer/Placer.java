package com.fho4565.brick_lib.tools.placer;

import com.fho4565.brick_lib.BrickLib;
import com.fho4565.brick_lib.Constants;
import com.fho4565.brick_lib.core.SingleBlockWithNbt;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.nbt.TagParser.parseTag;

/**
 * <p>方块放置器，提供了上下左右前后移动和放置方块的方法，以及保存和回滚的功能</p>
 * <p>放置器默认面向X轴正方向，"左"的方向是Z轴负方向</p>
 * <p style = "color:red">放置器默认在放置前不检查是否可以放置，且不会放置步骤中的空气方块</p>
 *
 * @author fho4565
 * @version 1.0
 */
public class Placer implements INBTSerializable<CompoundTag> {
    protected final RandomSource RANDOM_SOURCE = RandomSource.create();
    protected final Set<PlaceAction> steps = ConcurrentHashMap.newKeySet();
    protected final BlockPos.MutableBlockPos current = new BlockPos.MutableBlockPos(0, 0, 0);
    protected final ArrayList<BlockState> replaceableBlocks = new ArrayList<>(List.of(Blocks.AIR.defaultBlockState()));
    protected final ArrayList<TagKey<Block>> replaceableBlockTags = new ArrayList<>();
    private final Stack<Triple<Integer, Integer, Integer>> stack = new Stack<>();
    protected FormerDirection formerDirection = FormerDirection.X;
    protected boolean checkBeforePlace = false;
    protected boolean shouldPlaceAir = false;
    protected final File placerFile;
    protected final String name;

    public Placer() {
        name = "P_" + RandomStringUtils.random(16,"abcdefghijklmnopqrstuvwxyz");
        placerFile = new File(Constants.brickLibPlacersFolder() + "\\" + name);
    }

    public Placer(FormerDirection formerDirection) {
        this();
        this.formerDirection = formerDirection;
    }

    public Placer randomFormerDirection(){
        int i = RANDOM_SOURCE.nextIntBetweenInclusive(0, 4);
        switch (i){
            case 0:
                this.formerDirection = FormerDirection.X;
                break;
            case 1:
                this.formerDirection = FormerDirection.NX;
                break;
            case 2:
                this.formerDirection = FormerDirection.Z;
                break;
            case 3:
                this.formerDirection = FormerDirection.NZ;
        }
        return this;
    }

    public static synchronized void generateIO(LevelAccessor levelAccessor, BlockPos pos1, BlockPos pos2, Placer placer) throws IOException {
        if(!placer.placerFile.exists()){
            try {
                placer.placerFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        FileWriter writer = new FileWriter(placer.placerFile);
        CompletableFuture<Void> task = CompletableFuture.supplyAsync(() -> {
                    int x1 = Math.min(pos1.getX(), pos2.getX());
                    int y1 = Math.min(pos1.getY(), pos2.getY());
                    int z1 = Math.min(pos1.getZ(), pos2.getZ());
                    int x2 = Math.max(pos1.getX(), pos2.getX());
                    int y2 = Math.max(pos1.getY(), pos2.getY());
                    int z2 = Math.max(pos1.getZ(), pos2.getZ());
                    for (int x = x1; x <= x2; x++) {
                        for (int y = y1; y <= y2; y++) {
                            for (int z = z1; z < z2; z++) {
                                SingleBlockWithNbt singleBlockWithNbt;
                                BlockPos blockPos = BlockPos.containing(x, y, z);
                                BlockState blockState = levelAccessor.getBlockState(blockPos);
                                BlockEntity blockEntity = levelAccessor.getBlockEntity(blockPos);
                                System.out.println((blockEntity != null));
                                if (blockEntity != null) {
                                    System.out.println(blockEntity);
                                    singleBlockWithNbt = new SingleBlockWithNbt(blockPos.subtract(pos1), blockState, blockEntity.serializeNBT());
                                } else {
                                    singleBlockWithNbt = new SingleBlockWithNbt(blockPos.subtract(pos1), blockState, new CompoundTag());
                                }
                                try {
                                    writer.append(String.valueOf(new PlaceAction(PlaceAction.Type.OFFSET_AND_PLACE, singleBlockWithNbt).serialize()));
                                    writer.append("\n");
                                    writer.flush();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                    return null;
                }, BrickLib.THREAD_POOL
        );
        task.thenAccept(unused -> {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Constants.currentServer().sendSystemMessage(Component.literal("generated"));
        });
    }

    public static synchronized void generateIOWithoutAir(LevelAccessor levelAccessor, BlockPos pos1, BlockPos pos2, Placer placer) throws IOException {
        if(!placer.placerFile.exists()){
            try {
                placer.placerFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        FileWriter writer = new FileWriter(placer.placerFile);
        CompletableFuture<Void> task = CompletableFuture.supplyAsync(() -> {
                    int x1 = Math.min(pos1.getX(), pos2.getX());
                    int y1 = Math.min(pos1.getY(), pos2.getY());
                    int z1 = Math.min(pos1.getZ(), pos2.getZ());
                    int x2 = Math.max(pos1.getX(), pos2.getX());
                    int y2 = Math.max(pos1.getY(), pos2.getY());
                    int z2 = Math.max(pos1.getZ(), pos2.getZ());
                    for (int x = x1; x <= x2; x++) {
                        for (int y = y1; y <= y2; y++) {
                            for (int z = z1; z < z2; z++) {
                                SingleBlockWithNbt actionPos;
                                BlockPos blockPos = BlockPos.containing(x, y, z);
                                BlockState blockState = levelAccessor.getBlockState(blockPos);
                                if(!blockState.isAir()) {
                                    BlockEntity blockEntity = levelAccessor.getChunk(blockPos).getBlockEntity(blockPos);
                                    if (blockEntity != null) {
                                        actionPos = new SingleBlockWithNbt(blockPos.subtract(pos1), blockState, blockEntity.serializeNBT());
                                    } else {
                                        actionPos = new SingleBlockWithNbt(blockPos.subtract(pos1), blockState, new CompoundTag());
                                    }
                                    try {
                                        writer.append(String.valueOf(new PlaceAction(PlaceAction.Type.OFFSET_AND_PLACE, actionPos).serialize()));
                                        writer.append("\n");
                                        writer.flush();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }
                    }
                    return null;
                }, BrickLib.THREAD_POOL
        );
        task.thenAccept(unused -> {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Constants.currentServer().execute(()->{
                Constants.currentServer().sendSystemMessage(Component.literal("generated"));
            });
        });
    }

    public String name() {
        return name;
    }

    public boolean shouldPlaceAir() {
        return shouldPlaceAir;
    }

    public ArrayList<BlockState> replaceableBlocks() {
        return replaceableBlocks;
    }

    public ArrayList<TagKey<Block>> replaceableBlockTags() {
        return replaceableBlockTags;
    }

    /**
     * 按顺序执行此放置器预定义好的所有放置步骤，会先检查指定位置是否可以放置
     *
     * @param levelAccessor levelAccessor对象
     * @param origin        放置起点
     * @return 是否放置成功
     */
    public synchronized boolean place(LevelAccessor levelAccessor, @NotNull BlockPos origin) {
        BlockPos.MutableBlockPos current = origin.mutable();
        if (canPlace(levelAccessor, origin, Vec3i.ZERO)) {
            steps.forEach(placeAction -> {
                switch (placeAction.type) {
                    case MOVE -> current.move(placeAction.singleBlock.blockPos());
                    case PLACE -> {
                        if (shouldPlaceAir || !placeAction.singleBlock.blockState().isAir()) {
                            levelAccessor.setBlock(current, placeAction.singleBlock.blockState(), 2);
                            if (!placeAction.singleBlock.nbt().isEmpty()) {
                                BlockEntity blockEntity = levelAccessor.getBlockEntity(current);
                                if (blockEntity != null) {
                                    blockEntity.load(placeAction.singleBlock.nbt());
                                }
                            }
                        }
                    }
                    case SAVEPOINT -> stack.push(Triple.of(current.getX(), current.getY(), current.getZ()));
                    case ROLLBACK -> {
                        if (!this.stack.empty()) {
                            Triple<Integer, Integer, Integer> popped = this.stack.pop();
                            current.set(BlockPos.containing(popped.getLeft(), popped.getMiddle(), popped.getRight()));
                        }
                    }
                    case MOVE_AND_PLACE -> {
                        if (shouldPlaceAir || !placeAction.singleBlock.blockState().isAir()) {
                            BlockPos moved = current.move(placeAction.singleBlock.blockPos()).immutable();
                            levelAccessor.setBlock(moved, placeAction.singleBlock.blockState(), 2);
                            if (!placeAction.singleBlock.nbt().isEmpty()) {
                                BlockEntity blockEntity = levelAccessor.getBlockEntity(moved);
                                if (blockEntity != null) {
                                    blockEntity.load(placeAction.singleBlock.nbt());
                                }
                            }
                        }
                    }
                    case MOVE_TO -> current.set(placeAction.singleBlock.blockPos());
                    case OFFSET_AND_PLACE -> {
                        if (shouldPlaceAir || !placeAction.singleBlock.blockState().isAir()) {
                            BlockPos offset = current.offset(placeAction.singleBlock.blockPos());
                            levelAccessor.setBlock(offset, placeAction.singleBlock.blockState(), 2);
                            if (!placeAction.singleBlock.nbt().isEmpty()) {
                                BlockEntity blockEntity = levelAccessor.getBlockEntity(offset);
                                if (blockEntity != null) {
                                    blockEntity.load(placeAction.singleBlock.nbt());
                                }
                            }
                        }
                    }
                }
            });
            return true;
        }
        return false;
    }


    public static synchronized boolean placeIO(LevelAccessor levelAccessor, @NotNull BlockPos origin, String fileName, Placer placer) throws IOException, CommandSyntaxException {
        File file = new File(Constants.brickLibPlacersFolder()+"\\"+fileName);
        if(!file.exists()){
            BrickLib.LOGGER.error("File Not Found : {}", file.getPath());
            return false;
        }
        FileReader reader = new FileReader(file, Charset.defaultCharset());
        BlockPos.MutableBlockPos current = origin.mutable();
        String nextLine;
        while((nextLine = readLine(reader)) != null){
            CompoundTag compoundTag = parseTag(nextLine);
            PlaceAction placeAction = PlaceAction.deserialize(compoundTag);
            if (placer.shouldPlaceAir || !placeAction.singleBlock.blockState().isAir()) {
                BlockPos offset = current.offset(placeAction.singleBlock.blockPos());
                if(placer.canPlaceBlock(levelAccessor.getBlockState(placeAction.singleBlock.blockPos()))) {
                    levelAccessor.setBlock(offset, placeAction.singleBlock.blockState(), 2);
                    if (!placeAction.singleBlock.nbt().isEmpty()) {
                        BlockEntity blockEntity = levelAccessor.getChunk(offset).getBlockEntity(offset);
                        if (blockEntity != null) {
                            blockEntity.load(placeAction.singleBlock.nbt());
                        }
                    }
                }
            }
        }
        return true;
    }
    private static String readLine(FileReader reader) throws IOException {
        StringBuilder line = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1) {
            if (c == '\n') {
                return line.toString();
            } else {
                line.append((char) c);
            }
        }
        if (!line.isEmpty()) {
            return line.toString();
        }else{
            return null;
        }
    }

    public boolean checkBeforePlace() {
        return checkBeforePlace;
    }

    public boolean setCheckBeforePlace(boolean checkBeforePlace) {
        return this.checkBeforePlace = checkBeforePlace;
    }

    /**
     * 添加一个移动并放置放置方块的步骤
     *
     * @param moveDirection 移动方向
     * @param block         要放置的方块
     */
    public Placer moveAndPlaceBlock(MoveDirection moveDirection, BlockState block) {
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
        if (block.hasProperty(BlockStateProperties.AXIS)) {
            if (Math.abs(dx) == 1) {
                axis = Direction.Axis.X;
            } else if (Math.abs(dz) == 1) {
                axis = Direction.Axis.Z;
            }
        }
        current.move(dx, dy, dz);
        steps.add(new PlaceAction(PlaceAction.Type.MOVE_AND_PLACE, SingleBlockWithNbt.of(BlockPos.containing(dx, dy, dz), block.setValue(BlockStateProperties.AXIS, axis), new CompoundTag())));
        return this;
    }

    /**
     * 添加一个移动并放置放置方块的步骤。如果当前  方块是方块实体，则应用nbt
     *
     * @param moveDirection 移动方向
     * @param block         要放置的方块
     */
    public Placer moveAndPlaceBlock(MoveDirection moveDirection, BlockState block, CompoundTag nbt) {
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
        if (block.hasProperty(BlockStateProperties.AXIS)) {
            if (Math.abs(dx) == 1) {
                axis = Direction.Axis.X;
            } else if (Math.abs(dz) == 1) {
                axis = Direction.Axis.Z;
            }
        }
        current.move(dx, dy, dz);
        steps.add(new PlaceAction(PlaceAction.Type.MOVE_AND_PLACE, SingleBlockWithNbt.of(BlockPos.containing(dx, dy, dz), block.setValue(BlockStateProperties.AXIS, axis), nbt)));
        return this;
    }


    /**
     * 添加多个移动并放置放置方块的步骤
     *
     * @param moveDirection 移动方向
     * @param block         要放置的方块
     * @param times         移动并放置的次数
     */
    public Placer moveAndPlaceBlocks(MoveDirection moveDirection, BlockState block, int times) {
        for (int i = 0; i < times; i++) {
            moveAndPlaceBlock(moveDirection, block);
        }
        return this;
    }

    /**
     * 放置一个方块
     *
     * @param blockState 要放置的方块
     */
    public Placer placeBlock(BlockState blockState) {
        steps.add(new PlaceAction(PlaceAction.Type.PLACE, SingleBlockWithNbt.of(null, blockState, new CompoundTag())));
        return this;
    }

    /**
     * 放置一个方块。如果当前方块是方块实体，则应用nbt
     *
     * @param blockState 要放置的方块
     */
    public Placer placeBlock(BlockState blockState, CompoundTag nbt) {
        steps.add(new PlaceAction(PlaceAction.Type.PLACE, SingleBlockWithNbt.of(null, blockState, nbt)));
        return this;
    }

    /**
     * 偏移并放置一个方块，不会改变当前坐标
     */
    public Placer offsetAndPlaceBlock(BlockPos offset, BlockState blockState) {
        steps.add(new PlaceAction(PlaceAction.Type.OFFSET_AND_PLACE, SingleBlockWithNbt.of(offset, blockState, new CompoundTag())));
        return this;
    }

    /**
     * 偏移并放置一个方块，不会改变当前坐标。如果当前方块是方块实体，则应用nbt
     */
    public Placer offsetAndPlaceBlock(BlockPos offset, BlockState blockState, CompoundTag nbt) {
        steps.add(new PlaceAction(PlaceAction.Type.OFFSET_AND_PLACE, SingleBlockWithNbt.of(offset, blockState, nbt)));
        return this;
    }

    /**
     * 为当前坐标创建一个记录点
     */
    public Placer savepoint() {
        steps.add(new PlaceAction(PlaceAction.Type.SAVEPOINT, null));
        return this;
    }

    /**
     * 回溯到上一个记录点
     */
    public Placer rollback() {
        steps.add(new PlaceAction(PlaceAction.Type.ROLLBACK, null));
        return this;
    }

    /**
     * 判断指定位置是否可以放置
     */
    public boolean canPlace(LevelAccessor levelAccessor, BlockPos blockPos, Vec3i offset) {
        if (!checkBeforePlace) {
            return true;
        }
        AABB aabb = getCCTV(blockPos.offset(offset));
        for (BlockState blockState : levelAccessor.getBlockStates(aabb).toList()) {
            if (!canPlaceBlock(blockState)) return false;
        }
        return true;
    }

    private boolean canPlaceBlock(BlockState blockState) {
        boolean tagContains = false;
        for (TagKey<Block> blockTag : replaceableBlockTags) {
            if (blockState.is(blockTag)) {
                tagContains = true;
                break;
            }
        }
        if (!replaceableBlocks.contains(blockState) && !tagContains) {
            return false;
        }
        return true;
    }

    /**
     * 获取所有坐标和对应方块，由于有单个移动操作的缘故，方块可能是null<p>
     * 这个方法应该在设置完所有步骤后，{@link #place(LevelAccessor, BlockPos)}方法调用后立即执行
     *
     * @param blockPos 基准方块坐标，获取到的所有方块坐标都以此坐标偏移
     */
    public List<SingleBlockWithNbt> getAllPosAndBlocks(BlockPos blockPos) {
        Vec3 vec = new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        ArrayList<SingleBlockWithNbt> blocks = new ArrayList<>();

        Stack<Vec3> s = new Stack<>();
        for (PlaceAction action : steps) {
            switch (action.type) {
                case MOVE, MOVE_AND_PLACE -> {
                    Vec3 modify = new Vec3(action.singleBlock.blockPos().getX(), action.singleBlock.blockPos().getY(), action.singleBlock.blockPos().getZ());
                    vec = vec.add(modify);
                    blocks.add(SingleBlockWithNbt.of(BlockPos.containing(vec), action.singleBlock().blockState(), action.singleBlock.nbt()));
                }
                case MOVE_TO -> {
                    vec = new Vec3(action.singleBlock.blockPos().getX(), action.singleBlock.blockPos().getY(), action.singleBlock.blockPos().getZ());
                    blocks.add(SingleBlockWithNbt.of(BlockPos.containing(vec), action.singleBlock().blockState(), action.singleBlock.nbt()));
                }
                case OFFSET_AND_PLACE -> {
                    Vec3 b = new Vec3(action.singleBlock.blockPos().getX() + vec.x, action.singleBlock.blockPos().getY() + vec.y, action.singleBlock.blockPos().getZ() + vec.z);
                    blocks.add(SingleBlockWithNbt.of(BlockPos.containing(b), action.singleBlock().blockState(), action.singleBlock.nbt()));
                }
                case SAVEPOINT -> s.push(new Vec3(vec.x, vec.y, vec.z));
                case ROLLBACK -> vec = s.pop();
            }
        }
        return blocks;
    }

    public boolean placeAir() {
        return shouldPlaceAir;
    }

    public void setShouldPlaceAir(boolean shouldPlaceAir) {
        this.shouldPlaceAir = shouldPlaceAir;
    }

    /**
     * 获取结构的两个边界点
     */
    protected AABB getCCTV(BlockPos blockPos) {
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        Vec3 vec = new Vec3(x, y, z);
        Vec3 min = new Vec3(x, y, z);
        Vec3 max = new Vec3(x, y, z);

        Stack<Vec3> s = new Stack<>();
        for (PlaceAction action : steps) {
            switch (action.type) {
                case MOVE, MOVE_AND_PLACE -> {
                    Vec3 b = new Vec3(action.singleBlock.blockPos().getX(), action.singleBlock.blockPos().getY(), action.singleBlock.blockPos().getZ());
                    vec = vec.add(b);
                    max = new Vec3(Math.max(vec.x, max.x), Math.max(vec.y, max.y), Math.max(vec.z, max.z));
                    min = new Vec3(Math.min(vec.x, min.x), Math.min(vec.y, min.y), Math.min(vec.z, min.z));
                }
                case MOVE_TO -> {
                    vec = new Vec3(action.singleBlock.blockPos().getX(), action.singleBlock.blockPos().getY(), action.singleBlock.blockPos().getZ());
                    max = new Vec3(Math.max(vec.x, max.x), Math.max(vec.y, max.y), Math.max(vec.z, max.z));
                    min = new Vec3(Math.min(vec.x, min.x), Math.min(vec.y, min.y), Math.min(vec.z, min.z));
                }
                case OFFSET_AND_PLACE -> {
                    Vec3 b = new Vec3(action.singleBlock.blockPos().getX(), action.singleBlock.blockPos().getY(), action.singleBlock.blockPos().getZ());
                    max = new Vec3(Math.max(b.x + vec.x, max.x), Math.max(b.y + vec.y, max.y), Math.max(b.z + vec.z, max.z));
                    min = new Vec3(Math.min(b.x + vec.x, min.x), Math.min(b.y + vec.y, min.y), Math.min(b.z + vec.z, min.z));
                }
                case SAVEPOINT -> s.push(new Vec3(vec.x, vec.y, vec.z));
                case ROLLBACK -> vec = s.pop();
            }
        }
        return new AABB(BlockPos.containing(min.x, min.y, min.z), BlockPos.containing(max.x, max.y, max.z));
    }

    /**
     * 移动光标位置
     *
     * @param moveDirection 移动的方向
     */
    public Placer move(MoveDirection moveDirection) {
        return move(moveDirection, 1);
    }

    /**
     * 移动光标位置
     *
     * @param moveDirection 移动的方向
     * @param distance      在方向上移动的距离
     */
    public Placer move(MoveDirection moveDirection, int distance) {
        int dx = 0, dy = 0, dz = 0;
        switch (moveDirection) {
            case UP -> dy = distance;
            case DOWN -> dy = -distance;
            case LEFT -> {
                switch (this.formerDirection) {
                    case X -> dz = distance;
                    case Z -> dx = -distance;
                    case NX -> dz = -distance;
                    case NZ -> dx = distance;
                }
            }
            case RIGHT -> {
                switch (this.formerDirection) {
                    case X -> dz = -distance;
                    case Z -> dx = distance;
                    case NX -> dz = distance;
                    case NZ -> dx = -distance;
                }
            }
            case BACK -> {
                switch (this.formerDirection) {
                    case X -> dx = -distance;
                    case Z -> dz = -distance;
                    case NX -> dx = distance;
                    case NZ -> dz = distance;
                }
            }
            case FORMER -> {
                switch (this.formerDirection) {
                    case X -> dx = distance;
                    case Z -> dz = distance;
                    case NX -> dx = -distance;
                    case NZ -> dz = -distance;
                }
            }
        }
        steps.add(new PlaceAction(PlaceAction.Type.MOVE, SingleBlockWithNbt.of(BlockPos.containing(dx, dy, dz), null, new CompoundTag())));
        current.move(dx, dy, dz);
        return this;
    }

    /**
     * 移动光标位置
     */
    public Placer move(int x, int y, int z) {
        steps.add(new PlaceAction(PlaceAction.Type.MOVE, SingleBlockWithNbt.of(BlockPos.containing(x, y, z), null, new CompoundTag())));
        current.move(x, y, z);
        return this;
    }

    /**
     * 移动光标位置
     */
    public Placer move(BlockPos pos) {
        return move(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * 将光标移动到位置
     */
    public Placer moveTo(int x, int y, int z) {
        steps.add(new PlaceAction(PlaceAction.Type.MOVE_TO, SingleBlockWithNbt.of(BlockPos.containing(x, y, z), null, new CompoundTag())));
        current.set(x, y, z);
        return this;
    }

    /**
     * 将光标移动到位置
     */
    public Placer moveTo(BlockPos pos) {
        return moveTo(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag root = new CompoundTag();
        ListTag list = new ListTag();
        steps.forEach(placeAction -> list.add(placeAction.serialize()));
        root.put("steps", list);
        root.putString("former", this.formerDirection.name());
        return root;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        LinkedList<PlaceAction> steps = new LinkedList<>();
        ListTag list = nbt.getList("steps", 10);
        for (int i = 0; i < list.size(); i++) {
            steps.add(PlaceAction.deserialize(list.getCompound(i)));
        }
        this.steps.clear();
        this.steps.addAll(steps);
        this.formerDirection = FormerDirection.valueOf(nbt.getString("former"));
    }

    @Override
    public String toString() {
        return "PlacerBlock{" +
                "steps=" + steps +
                ", formerDirection=" + formerDirection +
                ", placeAir=" + shouldPlaceAir +
                ", checkBeforePlace=" + checkBeforePlace +
                '}';
    }

    /**
     * 放置器中"前"的方向的枚举
     */
    public enum FormerDirection {
        /**
         * X 轴正方向
         */
        X,
        /**
         * Z 轴正方向
         */
        Z,
        /**
         * X 轴负方向
         */
        NX,
        /**
         * Z 轴负方向
         */
        NZ
    }

    /**
     * 放置器中方向的枚举
     */
    public enum MoveDirection {
        LEFT,
        RIGHT,
        UP,
        DOWN,
        BACK,
        FORMER
    }

    protected record PlaceAction(Type type, SingleBlockWithNbt singleBlock) {
        public static PlaceAction deserialize(CompoundTag compoundTag) {
            Type type = Type.byName(compoundTag.getString("type"));
            SingleBlockWithNbt singleBlock = SingleBlockWithNbt.deserialize(compoundTag.getCompound("block"));
            return new PlaceAction(type, singleBlock);
        }

        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.putString("type", type.typeName);
            tag.put("block", singleBlock.serialize());
            return tag;
        }

        enum Type {
            PLACE("place"),
            MOVE("move"),
            MOVE_TO("move_to"),
            SAVEPOINT("save_point"),
            ROLLBACK("rollback"),
            MOVE_AND_PLACE("move_and_place"),
            OFFSET_AND_PLACE("offset_and_place");
            final String typeName;

            Type(String typeName) {
                this.typeName = typeName;
            }

            public static Type byName(String typeName) {
                return switch (typeName) {
                    case "place" -> PLACE;
                    case "move" -> MOVE;
                    case "move_to" -> MOVE_TO;
                    case "save_point" -> SAVEPOINT;
                    case "rollback" -> ROLLBACK;
                    case "move_and_place" -> MOVE_AND_PLACE;
                    case "offset_and_place" -> OFFSET_AND_PLACE;
                    default -> throw new IllegalStateException("Unexpected value: " + typeName);
                };
            }
        }
    }
}
