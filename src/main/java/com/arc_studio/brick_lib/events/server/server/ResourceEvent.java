package com.arc_studio.brick_lib.events.server.server;

import com.arc_studio.brick_lib.api.event.BaseEvent;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public class ResourceEvent extends BaseEvent {
    @ApiStatus.Experimental
    public static class Reload extends ResourceEvent {
        private final List<PreparableReloadListener> listeners = new ArrayList<>();
        private final ReloadableServerResources serverResources;
        private final RegistryAccess registryAccess;

        public Reload(ReloadableServerResources serverResources, RegistryAccess registryAccess) {
            this.serverResources = serverResources;
            this.registryAccess = registryAccess;
        }

        public ReloadableServerResources getServerResources() {
            return serverResources;
        }

        public RegistryAccess getRegistryAccess() {
            return registryAccess;
        }
        public void addListener(PreparableReloadListener listener)
        {
            listeners.add(listener);
        }

        public List<PreparableReloadListener> getListeners()
        {
            return ImmutableList.copyOf(listeners);
        }
    }
}
