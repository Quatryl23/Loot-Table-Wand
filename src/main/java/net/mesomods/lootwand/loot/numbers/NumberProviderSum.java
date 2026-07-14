package net.mesomods.lootwand.loot.numbers;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;

import java.util.*;

public class NumberProviderSum extends NumberProvider {
    private final List<NumberProvider> summands;
    private float min;
    private float max;
    private boolean isConstant;

    public NumberProviderSum(List<NumberProvider> summands) {
        this.summands = new ArrayList<>(summands);
        this.updateMinMaxAverage();
    }

    public NumberProviderSum(NumberProvider first, NumberProvider second) {
        this(List.of(first, second));
    }

    public void add(NumberProvider newSummand) {
        this.summands.add(newSummand);
        this.updateMinMaxAverage();
        this.intProbabilities = null;
        this.floatProbabilities = null;
    }

    public void updateMinMaxAverage() {
        this.averageNumber = 0;
        this.min = 0;
        this.max = 0;
        this.isConstant = true;
        summands.forEach(provider -> {
            this.averageNumber += provider.getAverage();
            this.min += provider.getAbsoluteMin();
            this.max += provider.getAbsoluteMax();
            if (!provider.isConstant()) this.isConstant = false;
        });
    }

    @Override
    public void calculateIntProbabilities() {
        Int2DoubleMap probabilities = new Int2DoubleOpenHashMap();
        boolean isFirst = true;
        for (NumberProvider provider : summands) {
            if (isFirst) {
                probabilities.putAll(provider.getIntProbabilities());
                isFirst = false;
                continue;
            }
            Int2DoubleMap newProbabilities = new Int2DoubleOpenHashMap();
            for (Int2DoubleMap.Entry entry1 : provider.getIntProbabilities().int2DoubleEntrySet()) {
                int int1 = entry1.getIntKey();
                double probability1 = entry1.getDoubleValue();
                for (Int2DoubleMap.Entry entry2 : probabilities.int2DoubleEntrySet()) {
                    int int2 = entry2.getIntKey();
                    double probability2 = entry2.getDoubleValue();
                    newProbabilities.put(int1 + int2, newProbabilities.containsKey(int1 + int2) ? newProbabilities.get(int1 + int2) + probability1 * probability2 : probability1 * probability2);
                }
            }
            probabilities.clear();
            probabilities.putAll(newProbabilities);
        }
        this.intProbabilities = probabilities;
    }

    @Override
    public void calculateFloatProbabilities() {
        Int2DoubleMap probabilities = new Int2DoubleOpenHashMap();
        boolean isFirst = true;
        for (NumberProvider provider : summands) {
            if (isFirst) {
                probabilities.putAll(provider.getFloatProbabilities());
                isFirst = false;
                continue;
            }
            Int2DoubleMap newProbabilities = new Int2DoubleOpenHashMap();
            for (Int2DoubleMap.Entry entry1 : provider.getFloatProbabilities().int2DoubleEntrySet()) {
                int int1 = entry1.getIntKey();
                double probability1 = entry1.getDoubleValue();
                for (Int2DoubleMap.Entry entry2 : probabilities.int2DoubleEntrySet()) {
                    int int2 = entry2.getIntKey();
                    double probability2 = entry2.getDoubleValue();
                    newProbabilities.put(int1 + int2, probability1 * probability2);
                }
            }
            probabilities.clear();
            probabilities.putAll(newProbabilities);
        }
        this.floatProbabilities = probabilities;
    }

    @Override
    public boolean isConstant() {
        return isConstant;
    }

    @Override
    public boolean isUniform() {
        return false;
    }

    @Override
    public boolean isScoreboardDependent() {
        for (NumberProvider provider : summands) {
            if (provider.isScoreboardDependent()) return true;
        }
        return false;
    }

    @Override
    public Set<ScoreboardNumberProvider> getScoreDependencies() {
        Set<ScoreboardNumberProvider> set = new HashSet<>();
        for (NumberProvider provider : summands) {
            set.addAll(provider.getScoreDependencies());
        }
        return set;
    }

    @Override
    public net.minecraft.world.level.storage.loot.providers.number.NumberProvider toVanilla() {
        return null;
    }

    @Override
    public float getAbsoluteMin() {
        return min;
    }

    @Override
    public float getAbsoluteMax() {
        return max;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NumberProviderSum s) {
            return this.summands.equals(s.summands);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(summands);
    }
}
