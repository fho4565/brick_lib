package com.arc_studio.brick_lib.register;

import com.arc_studio.brick_lib.tools.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

@ApiStatus.Experimental
public class Placers {
    private static final Set<ResourceLocation> PLACERS = Collections.unmodifiableSet(Constants.currentServer().getResourceManager()
            .listResources("placer", resourceLocation -> true).keySet());

    public static void getPlacerData(ResourceLocation resourceLocation, Consumer<Resource> consumer) {
        if (!resourceLocation.getPath().endsWith(".placer")) {
            resourceLocation = new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath() + ".placer");
        }
        Constants.currentServer().getResourceManager().getResource(resourceLocation).ifPresent(consumer);
    }

    public static Set<ResourceLocation> getPlacers() {
        return PLACERS;
    }
}
