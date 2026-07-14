package net.mesomods.lootwand.loot.numbers;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;

import java.util.Objects;
import java.util.Set;

public class MultipliedNumberProvider extends NumberProvider {
    public final NumberProvider base;
    public final int multiplier;

    public MultipliedNumberProvider(NumberProvider base, int multiplier) {
        this.base = base;
        this.multiplier = multiplier;
        this.averageNumber = base.getAverage() *  multiplier;
    }

    @Override
    public void calculateIntProbabilities() {
        Int2DoubleMap probabilities = new Int2DoubleOpenHashMap();
        base.getIntProbabilities().int2DoubleEntrySet().forEach(entry -> probabilities.put(entry.getIntKey() * multiplier, entry.getDoubleValue()));
        this.intProbabilities = probabilities;
    }

    @Override
    public void calculateFloatProbabilities() {
        Int2DoubleMap probabilities = new Int2DoubleOpenHashMap();
        base.getFloatProbabilities().int2DoubleEntrySet().forEach(entry -> probabilities.put(entry.getIntKey() * multiplier, entry.getDoubleValue()));
        this.floatProbabilities = probabilities;
    }

    @Override
    public boolean isConstant() {
        return base.isConstant();
    }

    @Override
    public boolean isUniform() {
        return base.isUniform();
    }

    @Override
    public boolean isScoreboardDependent() {
        return base.isScoreboardDependent();
    }

    @Override
    public Set<ScoreboardNumberProvider> getScoreDependencies() {
        return base.getScoreDependencies();
    }

    @Override
    public net.minecraft.world.level.storage.loot.providers.number.NumberProvider toVanilla() {
        return base.toVanilla();
    }

    @Override
    public float getAbsoluteMin() {
        return base.getAbsoluteMin() *  multiplier;
    }

    @Override
    public float getAbsoluteMax() {
        return base.getAbsoluteMax() * multiplier;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MultipliedNumberProvider m) {
            return m.multiplier == this.multiplier && this.base.equals(m.base);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, multiplier);
    }
}
