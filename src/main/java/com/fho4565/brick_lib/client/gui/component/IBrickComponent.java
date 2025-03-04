package com.fho4565.brick_lib.client.gui.component;

public interface IBrickComponent {
    void setX(int x);

    void setY(int y);

    int x();

    int y();

    int width();

    int height();

    void setWidth(int width);

    void setHeight(int height);

    default void setBounds(int x, int y, int width, int height){
        this.setX(x);
        this.setY(y);
        this.setWidth(width);
        this.setHeight(height);
    }

    default void setBounds(Bound bound) {
        setBounds(bound.x(), bound.y(), bound.width(), bound.height());
    }

    default void setXY(int x, int y) {
        this.setX(x);
        this.setY(y);
    }
}
