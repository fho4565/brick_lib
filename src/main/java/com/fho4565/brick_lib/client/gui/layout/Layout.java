package com.fho4565.brick_lib.client.gui.layout;

import com.fho4565.brick_lib.client.gui.component.BComponent;

import java.util.Collection;
/**
 * 用于放置组件的布局类
 * */
public abstract class Layout {
    protected int x = 0;
    protected int y = 0;
    protected int width = 0;
    protected int height = 0;
    protected final int hGap = 2;
    protected final int vGap = 2;

    public Layout(int x, int y, int width, int height) {
        this.height = height;
        this.width = width;
        this.x = x;
        this.y = y;
    }

    public int height() {
        return height;
    }

    public int width() {
        return width;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public abstract void placeComponents(BComponent...widgets);
    public void placeComponents(Collection<BComponent> widgets){
        this.placeComponents(widgets.toArray(new BComponent[0]));
    }

    @Override
    public String toString() {
        return "Layout{" +
                "height=" + height +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", hGap=" + hGap +
                ", vGap=" + vGap +
                '}';
    }
}
