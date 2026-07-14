package net.mesomods.lootwand.client.tooltip;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.mesomods.lootwand.client.ScreenUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class ClientFloatProbabilityChartTooltip extends ClientProbabilityChartTooltip {
    final Int2IntMap graphPoints;
    final float min;
    final float max;

    public ClientFloatProbabilityChartTooltip(FloatProbabilityChartTooltip chartTooltip) {
        super(chartTooltip.getMaxProbability(), chartTooltip.romanize());
        this.graphPoints = chartTooltip.getGraphPoints();
        this.min = chartTooltip.getMin();
        this.max = chartTooltip.getMax();
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        this.renderFrame(graphics, x, y);
        y += 8;
        int x0 = x + 8;
        int x1 = x + 152;
        int y1 = y + CHART_HEIGHT - 10;
        float widthRatio = (max - min) / (float) ClientProbabilityChartTooltip.CHART_WIDTH;
        for (x = x0; x <= x1; x++) {
            int xx = x - x0;
            if (graphPoints.containsKey(xx)) {
                int yy = y + graphPoints.get(xx);
                graphics.fill(x, yy, x + 1, yy + 1, 0xffbbbbbb);
            }
            if ((x - x0) % 18 == 0) {
                String string = ScreenUtils.formatFloat((x - x0) * widthRatio + min, romanize);
                ScreenUtils.drawScaledString(graphics, string, x + 0.5f, y1 + 2f, 0xffffffff, 0.5f, true, false);
            }
        }
        if (graphPoints.isEmpty()) {
            renderUnknownText(graphics, font, x0, y);
        }
    }
}
