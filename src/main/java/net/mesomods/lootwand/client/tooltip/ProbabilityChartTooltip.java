package net.mesomods.lootwand.client.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

public abstract class ProbabilityChartTooltip implements TooltipComponent {
    protected double maxProbability;
    private final boolean romanize;

    public ProbabilityChartTooltip(boolean romanize) {
        this.romanize = romanize;
    }

    public boolean romanize() {
        return romanize;
    }

    public double getMaxProbability() {
        return maxProbability;
    }
}
