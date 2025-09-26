package com.arc_studio.brick_lib.mixin.common;

import com.arc_studio.brick_lib.config.ConfigTracker;
import com.arc_studio.brick_lib.config.ModConfig;
import com.arc_studio.brick_lib.register.BrickRegistries;
import com.arc_studio.brick_lib.tools.Constants;
import com.arc_studio.brick_lib.tools.SideExecutor;
import com.arc_studio.brick_lib.tools.update_checker.UpdateChecker;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author fho4565
 */
@Mixin(ReloadableResourceManager.class)
public abstract class ReloadableResourceManagerMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    public void initMixin(PackType type, CallbackInfo ci) {
        SideExecutor.runOnClient(() -> () -> {
            ConfigTracker.loadConfigs(ModConfig.Type.CLIENT, Constants.globalConfigFolderPath());
        });
        ConfigTracker.loadConfigs(ModConfig.Type.COMMON, Constants.globalConfigFolderPath());
    }
    @Unique
    private ReloadableResourceManager brick_lib$getThis() {
        return (ReloadableResourceManager) (Object) this;
    }
}