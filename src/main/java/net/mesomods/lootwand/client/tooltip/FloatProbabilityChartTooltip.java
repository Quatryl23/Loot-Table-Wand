package net.mesomods.lootwand.client.tooltip;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.mesomods.lootwand.client.gui.loottable.numbers.NumberProvider;

public class FloatProbabilityChartTooltip extends ProbabilityChartTooltip {
    private final Int2IntMap graphPoints;
    private final float min;
    private final float max;

    private FloatProbabilityChartTooltip(Int2DoubleMap probabilities, boolean romanize) {
        super(romanize);
        float min = Float.MAX_VALUE;
        float max = -Float.MAX_VALUE;
        for (float f : probabilities.keySet()) {
            if (f < min) min = f;
            if (f > max) max = f;
        }
        this.min = min / NumberProvider.FLOAT_PRECISION;
        this.max = max / NumberProvider.FLOAT_PRECISION;
        this.maxProbability = 0;
        for (double d : probabilities.values()) {
            if (d > maxProbability) maxProbability = d;
        }
        graphPoints = new Int2IntOpenHashMap();
        double heightFactor = (ClientProbabilityChartTooltip.CHART_HEIGHT - 15) / maxProbability;
        float widthRatio = (max - min) / (float) ClientProbabilityChartTooltip.CHART_WIDTH;
        for (int x = 0; x < ClientProbabilityChartTooltip.CHART_WIDTH; x++) {
            int v0 = (int) Math.ceil(x * widthRatio + min);
            int v1 = (int) Math.ceil((x + 1) * widthRatio + min);
            int i = 0;
            double totalP = 0;
            for (int v = v0; v < v1; v++) {
                if (probabilities.containsKey(v)) {
                    i++;
                    totalP += probabilities.get(v);
                }
            }
            if (i != 0) {
                int y = ClientProbabilityChartTooltip.CHART_HEIGHT - 10 - (int) Math.round((totalP / i) * heightFactor);
                graphPoints.put(x, y);
            }
        }
    }

    public static FloatProbabilityChartTooltip create(Int2DoubleMap probabilities, boolean romanize) {
        if (probabilities == null) return null;
        return new FloatProbabilityChartTooltip(probabilities, romanize);
    }
    
    public Int2IntMap getGraphPoints() {
        return graphPoints;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }
}
