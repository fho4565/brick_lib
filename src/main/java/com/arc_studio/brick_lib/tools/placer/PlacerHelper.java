package com.arc_studio.brick_lib.tools.placer;

import com.arc_studio.brick_lib.BrickLib;
import com.arc_studio.brick_lib.tools.Constants;
import com.arc_studio.brick_lib.tools.WorldUtils;
import com.arc_studio.brick_lib.api.core.SingleBlockWithNbt;
import com.arc_studio.brick_lib.register.Placers;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static net.minecraft.nbt.TagParser.parseTag;

/**
 * 生成和放置方块放置器的工具类
 */
@ApiStatus.Experimental
public class PlacerHelper {
    public static boolean placeFromDiskFile(LevelAccessor levelAccessor, BlockPos origin, String fileName, Placer placer) {
        File file = new File(Constants.brickLibPlacersFolder() + File.separator + fileName);
        if (!file.exists()) {
            BrickLib.LOGGER.error("File Not Found: {}", file.getPath());
            return false;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            return processInputStream(fis, levelAccessor, origin, placer, file.getPath());
        } catch (IOException e) {
            BrickLib.LOGGER.error("Error opening file '{}': {}", file.getPath(), e.toString());
            return false;
        }
    }

    public static boolean placeFromDataPackFile(LevelAccessor levelAccessor, BlockPos origin, ResourceLocation resourceLocation, Placer placer) {
        Placers.getPlacerData(resourceLocation, resource -> {
            try (InputStream is = resource.open()) {
                processInputStream(is, levelAccessor, origin, placer, resourceLocation.toString());
            } catch (IOException e) {
                BrickLib.LOGGER.error("Error processing resource levelAccessor '{}': {}", resourceLocation, e.toString());
            }
        });
        return true;
    }

    public static boolean placeFromDiskFile(ChunkAccess levelAccessor, BlockPos origin, String fileName, Placer placer) {
        File file = new File(Constants.brickLibPlacersFolder() + File.separator + fileName);
        if (!file.exists()) {
            BrickLib.LOGGER.error("File Not Found: {}", file.getPath());
            return false;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            return processInputStream(fis, levelAccessor, origin, placer, file.getPath());
        } catch (IOException e) {
            BrickLib.LOGGER.error("Error opening file '{}': {}", file.getPath(), e.toString());
            return false;
        }
    }

    public static boolean placeFromDataPackFile(ChunkAccess chunkAccess, BlockPos origin, ResourceLocation resourceLocation, Placer placer) {
        Placers.getPlacerData(resourceLocation, resource -> {
            try (InputStream is = resource.open()) {
                processInputStream(is, chunkAccess, origin, placer, resourceLocation.toString());
            } catch (IOException e) {
                BrickLib.LOGGER.error("Error processing resource chunkAccess '{}': {}", resourceLocation, e.toString());
            }
        });
        return true;
    }

    public static void generatePlacerFile(LevelAccessor levelAccessor, BlockPos pos1, BlockPos pos2, Placer placer, CommandSourceStack source, boolean compressed, boolean noAir) throws IOException {
        File placerFile = new File(Constants.brickLibPlacersFolder() + File.separator + placer.name());
        if (!placerFile.exists() && !placerFile.createNewFile()) {
            throw new IOException("Failed to create file: " + placerFile.getPath());
        }
        CompletableFuture.runAsync(() -> {
                    try (FileOutputStream out = new FileOutputStream(placerFile);
                         OutputStreamWriter writer = compressed ? new OutputStreamWriter(new GZIPOutputStream(out), StandardCharsets.UTF_8) : new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
                        writer.write(compressed ? "true" : "false");
                        writer.write("\n");
                        WorldUtils.getSingleBlockWithNbtThen(levelAccessor, pos1, pos2, singleBlock -> {
                            BlockPos blockPos = singleBlock.blockPos().immutable();
                            BlockState blockState = singleBlock.blockState();
                            if (blockState != null && (!noAir || !blockState.isAir())) {
                                SingleBlockWithNbt blockData = new SingleBlockWithNbt(blockPos.subtract(pos1), blockState, singleBlock.nbt());
                                Placer.PlaceAction action = new Placer.PlaceAction(Placer.PlaceAction.Type.OFFSET_AND_PLACE, blockData);
                                try {
                                    writer.append(action.serialize().toString()).append("\n").flush();
                                } catch (IOException e) {
                                    BrickLib.LOGGER.error("Failed to write block data \n{}", e.getLocalizedMessage());
                                }
                            }
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 1, Integer.MAX_VALUE,
                        60L, TimeUnit.SECONDS,
                        new SynchronousQueue<>())
        ).thenRun(() -> {
            if (source != null) {
                source.sendSystemMessage(Component.literal("generated"));
            }
        });
    }

    private static boolean processInputStream(InputStream inputStream, LevelAccessor levelAccessor, BlockPos origin, Placer placer, String sourceName) {
        try (BufferedInputStream bis = new BufferedInputStream(inputStream)) {
            bis.mark(2);
            byte[] header = new byte[2];
            int bytesRead = bis.read(header);
            bis.reset();

            boolean isGzip = (bytesRead == 2) && (header[0] == (byte) 0x1F) && (header[1] == (byte) 0x8B);

            try (InputStream decompressedStream = isGzip ? new GZIPInputStream(bis) : bis;
                 BufferedReader reader = new BufferedReader(new InputStreamReader(decompressedStream))) {
                BlockPos.MutableBlockPos current = origin.mutable();
                reader.readLine();

                String line;
                while ((line = reader.readLine()) != null) {
                    processLine(line, levelAccessor, current, placer);
                }
                return true;
            }
        } catch (IOException e) {
            BrickLib.LOGGER.error("Error processing input from '{}': {}", sourceName, e.toString());
            return false;
        }
    }

    private static boolean processInputStream(InputStream inputStream, ChunkAccess levelAccessor, BlockPos origin, Placer placer, String sourceName) {
        try (BufferedInputStream bis = new BufferedInputStream(inputStream)) {
            bis.mark(2);
            byte[] header = new byte[2];
            int bytesRead = bis.read(header);
            bis.reset();

            boolean isGzip = (bytesRead == 2) && (header[0] == (byte) 0x1F) && (header[1] == (byte) 0x8B);

            try (InputStream decompressedStream = isGzip ? new GZIPInputStream(bis) : bis;
                 BufferedReader reader = new BufferedReader(new InputStreamReader(decompressedStream))) {
                BlockPos.MutableBlockPos current = origin.mutable();
                reader.readLine();

                String line;
                while ((line = reader.readLine()) != null) {
                    processLine(line, levelAccessor, current, placer);
                }
                return true;
            }
        } catch (IOException e) {
            BrickLib.LOGGER.error("Error processing input from '{}': {}", sourceName, e.toString());
            return false;
        }
    }

    private static void processLine(String line, LevelAccessor levelAccessor, BlockPos.MutableBlockPos current, Placer placer) {
        try {
            CompoundTag tag = parseTag(line);
            Placer.PlaceAction action = Placer.PlaceAction.deserialize(tag);

            if (!placer.shouldPlaceAir && action.singleBlock().blockState().isAir()) {
                return;
            }

            BlockPos offset = current.offset(action.singleBlock().blockPos());
            if (!placer.canPlaceBlock(levelAccessor.getBlockState(offset))) {
                return;
            }

            levelAccessor.setBlock(offset, action.singleBlock().blockState(), 2);
            Optional<BlockEntity> getEntity;
            if (levelAccessor instanceof Level level1) {
                if (level1.hasChunk(SectionPos.blockToSectionCoord(offset.getX()), SectionPos.blockToSectionCoord(offset.getZ()))) {
                    getEntity = Optional.ofNullable(level1.getChunk(offset).getBlockEntity(offset));
                } else{
                    getEntity = Optional.empty();
                }
            }else{
                getEntity = Optional.ofNullable(levelAccessor.getBlockEntity(offset));
            }
            getEntity.ifPresent(blockEntity -> {
                //? if >= 1.20.6 {
                /*blockEntity.loadWithComponents(action.singleBlock().nbt(), Constants.currentServer().registryAccess());
                 *///?} else {
                blockEntity.load(action.singleBlock().nbt());
                //?}
            });
        } catch (CommandSyntaxException e) {
            BrickLib.LOGGER.error("Failed to parse line: {}", e.toString());
        }
    }

    private static void processLine(String line, ChunkAccess levelAccessor, BlockPos.MutableBlockPos current, Placer placer) {
        try {
            CompoundTag tag = parseTag(line);
            Placer.PlaceAction action = Placer.PlaceAction.deserialize(tag);

            if (!placer.shouldPlaceAir && action.singleBlock().blockState().isAir()) {
                return;
            }

            BlockPos offset = current.offset(action.singleBlock().blockPos());
            if (!placer.canPlaceBlock(levelAccessor.getBlockState(offset))) {
                return;
            }

            levelAccessor.setBlockState(offset, action.singleBlock().blockState(), false);
            Optional.ofNullable(action.singleBlock().blockEntity()).ifPresent(levelAccessor::setBlockEntity);
        } catch (CommandSyntaxException e) {
            BrickLib.LOGGER.error("Failed to parse line: {}", e.toString());
        }
    }
}
