package com.fho4565.brick_lib.client.gui.layout;

import com.fho4565.brick_lib.client.gui.component.BComponent;
import net.minecraft.client.Minecraft;
/**
 * 网格布局类，会修改组件大小，当组件总数超过网格定义的位置则会在最下方新增一行
 * */
public class GridLayout extends Layout {
    int row = 9;
    final int col;
    final int minCellWidth = 60;
    final int minCellHeight = 20;
    public GridLayout(int x, int y, int width, int height) {
        super(x, y, width, height);
        col = Math.round(9 * (float)Minecraft.getInstance().getWindow().getGuiScaledHeight()/Minecraft.getInstance().getWindow().getGuiScaledWidth());
    }
    public GridLayout(int x, int y, int width, int height, int row, int col) {
        super(x, y, width, height);
        this.row = row;
        this.col = col;
    }

    @Override
    public void placeComponents(BComponent... widgets) {
        int cellWidth = Math.max((width - (col - 1) * hGap) / col, minCellWidth);
        int cellHeight = Math.max((height - (row - 1) * vGap) / row, minCellHeight);
        for (int i = 0; i < widgets.length; i++) {
            int rowIndex = i / col;
            int colIndex = i % col;
            int x = this.x + colIndex * (cellWidth + hGap);
            int y = this.y + rowIndex * (cellHeight + vGap);
            widgets[i].setX(x);
            widgets[i].setY(y);
            widgets[i].setWidth(cellWidth);
            widgets[i].setHeight(cellHeight);
        }
    }

    @Override
    public String toString() {
        return "GridLayout{" +
                "col=" + col +
                ", row=" + row +
                ", minCellWidth=" + minCellWidth +
                ", minCellHeight=" + minCellHeight +
                ", height=" + height +
                ", hGap=" + hGap +
                ", vGap=" + vGap +
                ", width=" + width +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
