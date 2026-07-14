package net.mesomods.lootwand.loot.numbers;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import net.mesomods.lootwand.LootWandMod;
import net.mesomods.lootwand.client.tooltip.FloatProbabilityChartTooltip;
import net.mesomods.lootwand.client.tooltip.IntProbabilityChartTooltip;
import net.mesomods.lootwand.client.tooltip.ProbabilityChartTooltip;
import net.mesomods.lootwand.client.tooltip.ScoreDependentProbabilityChartTooltip;
import net.mesomods.lootwand.mixin.numbers.BinomialDistributionGeneratorAccessor;
import net.mesomods.lootwand.mixin.numbers.ConstantValueAccessor;
import net.mesomods.lootwand.mixin.numbers.ScoreboardValueAccessor;
import net.mesomods.lootwand.mixin.numbers.UniformGeneratorAccessor;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.Optional;
import java.util.Set;

public abstract class NumberProvider {
    public static int FLOAT_PRECISION = 100;
    protected ProbabilityChartTooltip floatTooltip;
    protected ProbabilityChartTooltip intTooltip;
    protected float averageNumber;
    protected Int2DoubleMap intProbabilities;
    protected Int2DoubleMap floatProbabilities;

    public static ConstantNumberProvider constant(float f) {
        return new ConstantNumberProvider(f);
    }

    public static NumberProvider fromVanilla(net.minecraft.world.level.storage.loot.providers.number.NumberProvider provider) {
        if (provider instanceof BinomialDistributionGeneratorAccessor binomial) {
            return new BinomialNumberProvider(NumberProvider.fromVanilla(binomial.getN()), NumberProvider.fromVanilla(binomial.getP()));
        } else if (provider instanceof ConstantValueAccessor constant) {
            return new ConstantNumberProvider(constant.getValue());
        } else if (provider instanceof UniformGeneratorAccessor uniform) {
            return new UniformNumberProvider(NumberProvider.fromVanilla(uniform.getMin()), NumberProvider.fromVanilla(uniform.getMax()));
        } else if (provider instanceof ScoreboardValueAccessor score) {
            return new ScoreboardNumberProvider(score.getTarget(), score.getScore(), score.getScale());
        }
        return null;
    }

    public static NumberProvider fromVanillaTimes100(net.minecraft.world.level.storage.loot.providers.number.NumberProvider provider) {
        try {
            return new MultipliedNumberProvider(NumberProvider.fromVanilla(provider), 100);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Int2DoubleMap getIntProbabilities() {
        if (this.intProbabilities == null) {
            if (LootWandMod.isOnProbabilityCalculationThread()) {
                this.calculateIntProbabilities();
            } else {
                this.intProbabilities = NumberProviderProbabilitiesCache.getIntProbabilities(this);
            }

        }
        return intProbabilities;
    }

    public Int2DoubleMap getFloatProbabilities() {
        if (this.floatProbabilities == null) {
            if (LootWandMod.isOnProbabilityCalculationThread()) {
                this.calculateFloatProbabilities();
            } else {
                this.floatProbabilities = NumberProviderProbabilitiesCache.getFloatProbabilities(this);
            }
        }
        return floatProbabilities;
    }

    public abstract void calculateIntProbabilities();

    public abstract void calculateFloatProbabilities();

    public abstract boolean isConstant();

    public abstract boolean isUniform();

    public abstract boolean isScoreboardDependent();

    public abstract Set<ScoreboardNumberProvider> getScoreDependencies();

    public abstract net.minecraft.world.level.storage.loot.providers.number.NumberProvider toVanilla();

    public Optional<TooltipComponent> getTooltip(boolean useFloat, boolean romanized) {
        if (useFloat) {
            if (this.floatTooltip == null) {
                Set<ScoreboardNumberProvider> scoreDependencies = getScoreDependencies();
                if (scoreDependencies.isEmpty()) {
                    this.floatTooltip = FloatProbabilityChartTooltip.create(getFloatProbabilities(), romanized);
                } else {
                    ScoreboardNumberProvider first = scoreDependencies.iterator().next();
                    this.floatTooltip = ScoreDependentProbabilityChartTooltip.create(first.getScale(), first.getDependencyInfo(scoreDependencies.size() - 1));
                }
            }
            return Optional.ofNullable(floatTooltip);
        } else {
            if (this.intTooltip == null) {
                Set<ScoreboardNumberProvider> scoreDependencies = getScoreDependencies();
                if (scoreDependencies.isEmpty()) {
                    this.intTooltip = IntProbabilityChartTooltip.create(getIntProbabilities(), romanized);
                } else {
                    ScoreboardNumberProvider first = scoreDependencies.iterator().next();
                    this.intTooltip = ScoreDependentProbabilityChartTooltip.create(first.getScale(), first.getDependencyInfo(scoreDependencies.size() - 1));
                }
            }
            return Optional.ofNullable(intTooltip);
        }
    }

    public abstract float getAbsoluteMin();

    public abstract float getAbsoluteMax();

    public float getAverage() {
        return this.averageNumber;
    }

    public abstract boolean equals(Object o);

    public abstract int hashCode();
}
