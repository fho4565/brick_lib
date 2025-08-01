package com.arc_studio.brick_lib.compatibility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public class ForgeCompatibility {

    @Nullable
    public static BlockEntity getExistingBlockEntity(LevelAccessor levelAccessor, BlockPos pos) {
        if (levelAccessor instanceof Level level) {
            if (!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
            {
                return null;
            }

            return getExistingBlockEntity(levelAccessor.getChunk(pos), pos);
        }
        return levelAccessor.getBlockEntity(pos);
    }

    @Nullable
    public static BlockEntity getExistingBlockEntity(LevelChunk chunk, BlockPos pos) {
        return chunk.getBlockEntities().get(pos);
    }

    @Nullable
    public static BlockEntity getExistingBlockEntity(ChunkAccess chunk, BlockPos pos) {
        return chunk.getBlockEntity(pos);
    }

    @Nullable
    public static BlockEntity getExistingBlockEntity(ImposterProtoChunk chunk, BlockPos pos) {
        return getExistingBlockEntity(chunk.getWrapped(), pos);
    }
}
