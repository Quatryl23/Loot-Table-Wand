package net.mesomods.lootwand.loot.numbers;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import net.mesomods.lootwand.mixin.numbers.UniformGeneratorAccessor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UniformNumberProvider extends NumberProvider {
    public final NumberProvider minNumber;
    public final NumberProvider maxNumber;

    public UniformNumberProvider(NumberProvider min, NumberProvider max) {
        this.minNumber = min;
        this.maxNumber = max;
        this.averageNumber = (minNumber.averageNumber + maxNumber.averageNumber) / 2;
    }

    @Override
    public void calculateIntProbabilities() {
        Int2DoubleMap probabilities = new Int2DoubleOpenHashMap();
        for (Int2DoubleMap.Entry minEntry : minNumber.getIntProbabilities().int2DoubleEntrySet()) {
            int min = minEntry.getIntKey();
            double probability1 = minEntry.getDoubleValue();
            for (Int2DoubleMap.Entry maxEntry : maxNumber.getIntProbabilities().int2DoubleEntrySet()) {
                int max = maxEntry.getIntKey();
                double probability2 = maxEntry.getDoubleValue();
                double probabilityEach = probability1 * probability2 * (1.0 / (max - min + 1));
                if (min > max) {
                    double probability = probability1 * probability2;
                    probabilities.put(min, probabilities.containsKey(min) ? probabilities.get(min) + probability : probability);
                } else {
                    for (int i = min; i <= max; i++) {
                        probabilities.put(i, probabilities.containsKey(i) ? probabilities.get(i) + probabilityEach : probabilityEach);
                    }
                }
            }
        }
        this.intProbabilities = probabilities;
    }

    @Override
    public void calculateFloatProbabilities() {
        Int2DoubleMap probabilities = new Int2DoubleOpenHashMap();
        for (Int2DoubleMap.Entry minEntry : minNumber.getFloatProbabilities().int2DoubleEntrySet()) {
            int min = minEntry.getIntKey();
            double probability1 = minEntry.getDoubleValue();
            for (Int2DoubleMap.Entry maxEntry : maxNumber.getFloatProbabilities().int2DoubleEntrySet()) {
                int max = maxEntry.getIntKey();
                double probability2 = maxEntry.getDoubleValue();
                double probabilityEach = probability1 * probability2 * (1.0 / (max - min + 1));
                if (min > max) {
                    double probability = probability1 * probability2;
                    probabilities.put(min, probabilities.containsKey(min) ? probabilities.get(min) + probability : probability);
                } else {
                    for (int f = min; f <= max; f++) {
                        probabilities.put(f, probabilities.containsKey(f) ? probabilities.get(f) + probabilityEach : probabilityEach);
                    }
                }
            }
        }
        this.floatProbabilities = probabilities;
    }

    public boolean isConstant() {
        return false;
    }

    @Override
    public boolean isUniform() {
        return minNumber.isConstant() && maxNumber.isConstant();
    }

    @Override
    public boolean isScoreboardDependent() {
        return minNumber.isScoreboardDependent() || maxNumber.isScoreboardDependent();
    }

    @Override
    public Set<ScoreboardNumberProvider> getScoreDependencies() {
        Set<ScoreboardNumberProvider> set = new HashSet<>();
        set.addAll(minNumber.getScoreDependencies());
        set.addAll(maxNumber.getScoreDependencies());
        return set;
    }

    @Override
    public net.minecraft.world.level.storage.loot.providers.number.NumberProvider toVanilla() {
        return UniformGeneratorAccessor.create(minNumber.toVanilla(), maxNumber.toVanilla());
    }

    @Override
    public float getAbsoluteMin() {
        return minNumber.getAbsoluteMin();
    }

    @Override
    public float getAbsoluteMax() {
        return maxNumber.getAbsoluteMax();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UniformNumberProvider u) {
            return (this.minNumber.equals(u.minNumber) && this.maxNumber.equals(u.maxNumber));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minNumber, maxNumber);
    }
}
