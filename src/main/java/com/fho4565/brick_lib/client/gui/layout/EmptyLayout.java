package com.fho4565.brick_lib.client.gui.layout;

import com.fho4565.brick_lib.client.gui.component.IBrickComponent;

/**
 * 空布局类，不进行任何组件放置
 * */
public class EmptyLayout extends Layout{
    public EmptyLayout() {
        super(0,0,0,0);
    }

    @Override
    public void placeComponents(IBrickComponent... widgets) {

    }

    @Override
    public String toString() {
        return "EmptyLayout{" +
                "width=" + width +
                ", height=" + height +
                ", hGap=" + hGap +
                ", vGap=" + vGap +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
