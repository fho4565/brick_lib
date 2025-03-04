package com.fho4565.brick_lib.client.gui.component;

import com.fho4565.brick_lib.client.gui.border.BBorder;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.Nullable;

public abstract class BComponent implements Renderable,GuiEventListener, NarratableEntry {
    protected int x = 0,y = 0,width = 0,height = 0;
    protected BBorder border;
    protected boolean focused = false;
    protected boolean active = false;
    protected boolean visible = false;

    /**
     * Called when the mouse is moved within the GUI element.
     *
     * @param pMouseX the X coordinate of the mouse.
     * @param pMouseY the Y coordinate of the mouse.
     */
    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        GuiEventListener.super.mouseMoved(pMouseX, pMouseY);
    }

    /**
     * Called when a mouse button is clicked within the GUI element.
     * <p>
     *
     * @param pMouseX the X coordinate of the mouse.
     * @param pMouseY the Y coordinate of the mouse.
     * @param pButton the button that was clicked.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return GuiEventListener.super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    /**
     * Called when a mouse button is released within the GUI element.
     * <p>
     *
     * @param pMouseX the X coordinate of the mouse.
     * @param pMouseY the Y coordinate of the mouse.
     * @param pButton the button that was released.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        return GuiEventListener.super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    /**
     * Called when the mouse is dragged within the GUI element.
     * <p>
     *
     * @param pMouseX the X coordinate of the mouse.
     * @param pMouseY the Y coordinate of the mouse.
     * @param pButton the button that is being dragged.
     * @param pDragX  the X distance of the drag.
     * @param pDragY  the Y distance of the drag.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return GuiEventListener.super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    /**
     * Called when the mouse wheel is scrolled within the GUI element.
     * <p>
     *
     * @param pMouseX the X coordinate of the mouse.
     * @param pMouseY the Y coordinate of the mouse.
     * @param pDelta  the scrolling delta.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        return GuiEventListener.super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    /**
     * Called when a keyboard key is pressed within the GUI element.
     * <p>
     *
     * @param pKeyCode   the key code of the pressed key.
     * @param pScanCode  the scan code of the pressed key.
     * @param pModifiers the keyboard modifiers.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return GuiEventListener.super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    /**
     * Called when a keyboard key is released within the GUI element.
     * <p>
     *
     * @param pKeyCode   the key code of the released key.
     * @param pScanCode  the scan code of the released key.
     * @param pModifiers the keyboard modifiers.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        return GuiEventListener.super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    /**
     * Called when a character is typed within the GUI element.
     * <p>
     *
     * @param pCodePoint the code point of the typed character.
     * @param pModifiers the keyboard modifiers.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        return GuiEventListener.super.charTyped(pCodePoint, pModifiers);
    }

    /**
     * Retrieves the next focus path based on the given focus navigation event.
     * <p>
     *
     * @param pEvent the focus navigation event.
     * @return the next focus path as a ComponentPath, or {@code null} if there is no next focus path.
     */
    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent pEvent) {
        return GuiEventListener.super.nextFocusPath(pEvent);
    }

    /**
     * Checks if the given mouse coordinates are over the GUI element.
     * <p>
     *
     * @param pMouseX the X coordinate of the mouse.
     * @param pMouseY the Y coordinate of the mouse.
     * @return {@code true} if the mouse is over the GUI element, {@code false} otherwise.
     */
    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return GuiEventListener.super.isMouseOver(pMouseX, pMouseY);
    }

    /**
     * Sets the focus state of the GUI element.
     *
     * @param pFocused {@code true} to apply focus, {@code false} to remove focus
     */
    @Override
    public void setFocused(boolean pFocused) {
        focused = pFocused;
    }

    /**
     * {@return {@code true} if the GUI element is focused, {@code false} otherwise}
     */
    @Override
    public boolean isFocused() {
        return focused;
    }

    /**
     * {@return the current focus path as a ComponentPath, or {@code null}}
     */
    @Override
    public @Nullable ComponentPath getCurrentFocusPath() {
        return GuiEventListener.super.getCurrentFocusPath();
    }

    /**
     * {@return the {@link ScreenRectangle} occupied by the GUI element}
     */
    @Override
    public ScreenRectangle getRectangle() {
        return new ScreenRectangle(x,y,width,height);
    }

    /**
     * {@return the narration priority}
     */
    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    /**
     * @param pX
     */
    public void setX(int pX) {
        x = pX;
    }

    /**
     * @param pY
     */
    public void setY(int pY) {
        y = pY;
    }

    /**
     * @return
     */
    public int getX() {
        return x;
    }

    /**
     * @return
     */
    public int getY() {
        return y;
    }

    /**
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return
     */
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Updates the narration output with the current narration information.
     *
     * @param narrationElementOutput the output to update with narration information.
     */
    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }
}
