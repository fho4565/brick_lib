package com.fho4565.brick_lib.client.gui.layout;

import com.fho4565.brick_lib.client.gui.component.BComponent;

/**
 * <p>流式布局类，可以设置水平，竖直方向和左，右对齐方式</p>
 * <p>默认是水平方向和左对齐</p>
 * */
public class FlowLayout extends Layout {
    Alignment alignment;
    LayoutDirection direction;

    public FlowLayout(int x, int y, int width, int height) {
        this(x, y, width, height,Alignment.Left,LayoutDirection.Horizontal);
    }

    public FlowLayout(int x, int y, int width, int height, Alignment alignment, LayoutDirection direction) {
        super(x, y, width, height);
        this.alignment = alignment;
        this.direction = direction;
    }

    public FlowLayout(int x, int y, int width, int height, Alignment alignment) {
        this(x, y, width, height,alignment,LayoutDirection.Horizontal);
    }

    public Alignment alignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public LayoutDirection direction() {
        return direction;
    }

    public void setDirection(LayoutDirection direction) {
        this.direction = direction;
    }

    @Override
    public void placeComponents(BComponent... widgets) {
        int x = this.x;
        int y = this.y;
        int currentRowWidth = 0;
        int currentRowHeight = 0;

        for (BComponent widget : widgets) {
            int widgetWidth = widget.getWidth();
            int widgetHeight = widget.getHeight();

            switch (direction) {
                case Horizontal:
                    if (x + widgetWidth > this.x + this.width) {
                        x = this.x;
                        y += currentRowHeight + vGap;
                        currentRowWidth = 0;
                        currentRowHeight = 0;
                    }
                    break;
                case Vertical:
                    if (y + widgetHeight > this.y + this.height) {
                        y = this.y;
                        x += currentRowWidth + hGap;
                        currentRowWidth = 0;
                        currentRowHeight = 0;
                    }
                    break;
            }

            switch (alignment) {
                case Left:
                    widget.setX(x);
                    widget.setY(y);
                    break;
                case Right:
                    widget.setX(this.x + this.width - widgetWidth - x);
                    widget.setY(y);
                    break;
            }

            switch (direction) {
                case Horizontal:
                    currentRowWidth += widgetWidth + hGap;
                    currentRowHeight = Math.max(currentRowHeight, widgetHeight);
                    x += widgetWidth + hGap;
                    break;
                case Vertical:
                    currentRowHeight += widgetHeight + vGap;
                    currentRowWidth = Math.max(currentRowWidth, widgetWidth);
                    y += widgetHeight + vGap;
                    break;
            }
        }
    }

    public enum LayoutDirection {
        Horizontal,
        Vertical
    }

    public enum Alignment {
        Left,
        Right
    }

    @Override
    public String toString() {
        return "FlowLayout{" +
                "alignment=" + alignment +
                ", direction=" + direction +
                ", height=" + height +
                ", hGap=" + hGap +
                ", vGap=" + vGap +
                ", width=" + width +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}