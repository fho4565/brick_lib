package com.arc_studio.brick_lib.api.tools;

import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ColorUtils {
    public static List<Color> createGradientColor(Color start, Color end, int steps) {
        List<Color> gradient = new ArrayList<>();
        float rStep = (float) (end.getRed() - start.getRed()) / (steps - 1);
        float gStep = (float) (end.getGreen() - start.getGreen()) / (steps - 1);
        float bStep = (float) (end.getBlue() - start.getBlue()) / (steps - 1);
        float aStep = (float) (end.getAlpha() - start.getAlpha()) / (steps - 1);
        for (int i = 0; i < steps; i++) {
            float r = start.getRed() + rStep * i;
            float g = start.getGreen() + gStep * i;
            float b = start.getBlue() + bStep * i;
            float a = start.getAlpha() + aStep * i;
            int rInt = Math.max(0, Math.min(255, (int) r));
            int gInt = Math.max(0, Math.min(255, (int) g));
            int bInt = Math.max(0, Math.min(255, (int) b));
            int aInt = Math.max(0, Math.min(255, (int) a));
            gradient.add(new Color(rInt, gInt, bInt, aInt));
        }
        return gradient;
    }

    public static List<Integer> createGradientColor(int start, int end, int steps) {
        List<Integer> gradient = new ArrayList<>();
        Color s = toColor(start);
        Color e = toColor(end);
        double rStep = (double) (e.getRed() - s.getRed()) / (steps - 1);
        double gStep = (double) (e.getGreen() - s.getGreen()) / (steps - 1);
        double bStep = (double) (e.getBlue() - s.getBlue()) / (steps - 1);
        double aStep = (double) (e.getAlpha() - s.getAlpha()) / (steps - 1);

        for (int i = 0; i < steps; i++) {
            int r = (int) (s.getRed() + rStep * i);
            int g = (int) (s.getGreen() + gStep * i);
            int b = (int) (s.getBlue() + bStep * i);
            int a = (int) (s.getAlpha() + aStep * i);

            r = Math.max(0, Math.min(255, r));
            g = Math.max(0, Math.min(255, g));
            b = Math.max(0, Math.min(255, b));
            a = Math.max(0, Math.min(255, a));

            gradient.add(toIntColor(r, g, b, a));
        }
        return gradient;
    }

    public static int toIntColor(Color color) {
        return color.getRGB();
    }

    public static int toIntColor(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }

    public static int toIntColor(int r, int g, int b) {
        return new Color(r, g, b).getRGB();
    }

    public static Color toColor(int r, int g, int b, int a) {
        return new Color(r, g, b, a);
    }

    public static Color toColor(int r, int g, int b) {
        return new Color(r, g, b);
    }

    public static Color toColor(int color) {
        return new Color(color);
    }

    /**
     * <p>可以添加多个节点并指定过渡颜色个数的渐变颜色</p>
     * <p>颜色的过渡是平滑的</p>
     */
    public static class GradientColor {
        Color start;
        LinkedList<Pair<Color, Integer>> points = new LinkedList<>();

        public List<Color> createGradient() {
            List<Color> gradient = new ArrayList<>(createGradientColor(start, points.get(0).getLeft(), points.get(0).getRight()));
            for (int i = 1; i < points.size(); i++) {
                Pair<Color, Integer> point = points.get(i);
                gradient.addAll(createGradientColor(points.get(i - 1).getLeft(), point.getLeft(), point.getRight()));
            }
            gradient.addAll(createGradientColor(points.get(points.size() - 1).getLeft(), points.get(points.size() - 1).getLeft(), points.get(points.size() - 1).getRight()));
            return gradient;
        }

        public static class Builder {
            GradientColor gradientColor = new GradientColor();

            public Builder(Color start) {
                this.gradientColor.start = start;
            }

            public Builder(int start) {
                this.gradientColor.start = new Color(start, true);
            }

            public static Builder create(Color start) {
                return new Builder(start);
            }

            public static Builder create(int start) {
                return new Builder(start);
            }

            public Builder addPoint(Color color, int stepsToThis) {
                this.gradientColor.points.add(Pair.of(color, stepsToThis));
                return this;
            }

            public Builder addPoint(int color, int stepsToThis) {
                this.gradientColor.points.add(Pair.of(new Color(color, true), stepsToThis));
                return this;
            }

            public GradientColor end() {
                return this.gradientColor;
            }

        }
    }
}
