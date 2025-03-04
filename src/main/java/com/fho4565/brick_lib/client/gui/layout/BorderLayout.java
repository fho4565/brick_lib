package com.fho4565.brick_lib.client.gui.layout;

import com.fho4565.brick_lib.client.gui.component.BComponent;
import com.fho4565.brick_lib.client.gui.component.Bound;

/**
 * 边界布局类，只能放置5个组件，不等于5个的组件数量均不会进行布局
 * */
public class BorderLayout extends Layout{
    private static Bound NORTH;
    private static Bound SOUTH;
    private static Bound EAST;
    private static Bound WEST;
    private static Bound CENTER;
    public BorderLayout(int x, int y, int width, int height) {
        super(x, y, width, height);
        NORTH = new Bound(x, y, width, height / 5);
        SOUTH = new Bound(x, y + (height * 4 / 5), width, height / 5);
        EAST = new Bound(x + (width * 4 / 5), y + (height / 5), width / 5, height * 3 / 5);
        WEST = new Bound(x, y + (height / 5), width / 5, height * 3 / 5);
        CENTER = new Bound(x + (width / 5), y + (height / 5), width * 3 / 5, height * 3 / 5);
    }

    @Override
    public void placeComponents(BComponent... widgets) {
        if(widgets.length!=5){
            return;
        }
        widgets[0].setX(x);
        widgets[0].setY(y);
        widgets[0].setWidth(width);
        widgets[0].setHeight(height / 5);

        widgets[1].setX(x);
        widgets[1].setY(y + (height * 4 / 5));
        widgets[1].setWidth(width);
        widgets[1].setHeight(height / 5);

        widgets[2].setX(x + (width * 4 / 5));
        widgets[2].setY(y + (height / 5));
        widgets[2].setWidth(width / 5);
        widgets[2].setHeight(height * 3 / 5);

        widgets[3].setX(x);
        widgets[3].setY(y + (height / 5));
        widgets[3].setWidth(width / 5);
        widgets[3].setHeight(height * 3 / 5);

        widgets[4].setX(x + (width / 5));
        widgets[4].setY(y + (height / 5));
        widgets[4].setWidth(width * 3 / 5);
        widgets[4].setHeight(height * 3 / 5);
    }
}
