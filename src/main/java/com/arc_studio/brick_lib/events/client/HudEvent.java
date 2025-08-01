package com.arc_studio.brick_lib.events.client;

import com.arc_studio.brick_lib.api.event.BaseEvent;
import net.minecraft.client.gui.GuiGraphics;

public abstract class HudEvent extends BaseEvent {
    GuiGraphics guiGraphics;
    int tickDelta;

    public HudEvent(GuiGraphics guiGraphics, int tickDelta) {
        this.guiGraphics = guiGraphics;
        this.tickDelta = tickDelta;
    }
    public static class Render extends HudEvent{

        public Render(GuiGraphics guiGraphics, int tickDelta) {
            super(guiGraphics, tickDelta);
        }
    }
}
