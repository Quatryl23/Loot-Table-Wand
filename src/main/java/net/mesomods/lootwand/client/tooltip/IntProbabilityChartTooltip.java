package net.mesomods.lootwand.client.tooltip;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;

public class IntProbabilityChartTooltip extends ProbabilityChartTooltip {
    private final Int2DoubleMap probabilities;
    private final int min;
    private final int max;

    private IntProbabilityChartTooltip(Int2DoubleMap probabilities, boolean romanize) {
        super(romanize);
        this.probabilities = probabilities;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int f : probabilities.keySet()) {
            if (f < min) min = f;
            if (f > max) max = f;
        }
        this.min = min;
        this.max = max;
        this.maxProbability = 0;
        for (double d : probabilities.values()) {
            if (d > maxProbability) maxProbability = d;
        }
    }

    public static IntProbabilityChartTooltip create(Int2DoubleMap probabilities, boolean romanize) {
        if (probabilities == null) return null;
        return new IntProbabilityChartTooltip(probabilities, romanize);
    }

    public Int2DoubleMap getProbabilities() {
        return probabilities;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public IntProbabilityChartTooltip romanized() {
        return new IntProbabilityChartTooltip(probabilities, true);
    }
}
