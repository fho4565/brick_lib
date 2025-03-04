package com.fho4565.brick_lib.client.gui;

import com.fho4565.brick_lib.ComponentUtils;
import com.fho4565.brick_lib.client.gui.component.BComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.network.chat.Component;

import java.util.List;

public class BToolTip extends BComponent implements NarrationSupplier {
    Component tooltip;

    public BToolTip(Component tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * Updates the narration output with the current narration information.
     *
     * @param narrationElementOutput the output to update with narration information.
     */
    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.POSITION, tooltip);
    }

    /**
     * Renders the graphical user interface (GUI) element.
     *
     * @param guiGraphics the GuiGraphics object used for rendering.
     * @param mouseX      the x-coordinate of the mouse cursor.
     * @param mouseY      the y-coordinate of the mouse cursor.
     * @param partialTick the partial tick time.
     */
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();

        List<Component> lines = ComponentUtils.splitToLines(tooltip, width/minecraft.font.width("A") * 4 / 5);
        int maxWidth = 0;
        for (Component line : lines) {
            maxWidth = Math.max(maxWidth, font.width(line));
        }
        int tooltipHeight = lines.size() * 10;

        int x = mouseX + 10;
        int y = mouseY - 10 - tooltipHeight;

        // 处理水平方向的边界问题
        if (x + maxWidth > width) {
            x = mouseX - maxWidth - 10;
            if (x < 0) {
                x = 0;
            }
        }

        // 处理垂直方向的边界问题
        if (y < 0) {
            y = mouseY + 10;
            if (y + tooltipHeight > height) {
                y = height - tooltipHeight;
            }
        }

        guiGraphics.fill(x - 2, y - 2, x + maxWidth + 2, y + tooltipHeight + 2, 0x80000000);

        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawString(font, lines.get(i), x, y + i * 10, 0xFFFFFFFF);
        }
    }

}
