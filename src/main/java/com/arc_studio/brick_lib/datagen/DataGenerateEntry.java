package com.arc_studio.brick_lib.datagen;

import com.arc_studio.brick_lib.api.core.SideType;
import net.minecraft.data.DataProvider;

import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Function;

public record DataGenerateEntry(
        SideType side,
        Function<Collection<Path>, DataProvider.Factory<?>> factory
) {
}
