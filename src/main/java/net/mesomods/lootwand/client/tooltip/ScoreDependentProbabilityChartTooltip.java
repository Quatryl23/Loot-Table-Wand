package net.mesomods.lootwand.client.tooltip;


import net.minecraft.network.chat.Component;

public class ScoreDependentProbabilityChartTooltip extends ProbabilityChartTooltip {
    private final Component message;
    private final float scale;

    private ScoreDependentProbabilityChartTooltip(float scale, Component message) {
        super(false);
        this.scale = scale;
        this.message = message;
    }

    public static ScoreDependentProbabilityChartTooltip create(float scale, Component message) {
        return new ScoreDependentProbabilityChartTooltip(scale, message);
    }

    public float getScale() {
        return scale;
    }

    public Component getMessage() {
        return message;
    }
}
