package net.mesomods.lootwand.client.tooltip;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import net.mesomods.lootwand.client.ScreenUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class ClientIntProbabilityChartTooltip extends ClientProbabilityChartTooltip {
    final Int2DoubleMap probabilities;
    final int min;
    final int max;

    public ClientIntProbabilityChartTooltip(IntProbabilityChartTooltip chartTooltip) {
        super(chartTooltip.getMaxProbability(), chartTooltip.romanize());
        this.probabilities = chartTooltip.getProbabilities();
        this.min = chartTooltip.getMin();
        this.max = chartTooltip.getMax();
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        this.renderFrame(graphics, x, y);
        x += 8;
        y += 8;
        int barNumber = max - min + 1;
        double barWidth = CHART_WIDTH / barNumber;
        double barEdgeWidth = barNumber < 15 ? 0.1 * barWidth : 0;
        double heightFactor = (CHART_HEIGHT - 15) / maxProbability;
        double numberedBarsRatio = barNumber < 17 ? 1 : 8.0 / (barNumber - 1);
        float stringScale = 1.0f / Math.max(1, ScreenUtils.formatFloat(max, romanize).length());
        for (Int2DoubleMap.Entry entry : probabilities.int2DoubleEntrySet()) {
            int barIndex = entry.getIntKey() - min;
            int x0 = (int) Math.floor(x + (barIndex * barWidth) + barEdgeWidth);
            int x1 = (int) Math.ceil(x + ((barIndex + 1) * barWidth) - barEdgeWidth);
            int y0 = y + CHART_HEIGHT - 10 - (int) Math.round(entry.getDoubleValue() * heightFactor);
            int y1 = y + CHART_HEIGHT - 10;
            graphics.fill(x0, y0, x1, y1, 0xffbbbbbb);
            if (Math.floor(barIndex * numberedBarsRatio) != Math.floor((barIndex - 1) * numberedBarsRatio)) {
                String string = ScreenUtils.formatFloat(entry.getIntKey(), romanize);
                ScreenUtils.drawScaledString(graphics, string, x0 + Math.round(barWidth / 2 - barEdgeWidth), y1 + 2, 0xffffffff, stringScale, true, false);
            }
        }
        if (probabilities.isEmpty()) {
            renderUnknownText(graphics, font, x, y);
        }
    }
}
