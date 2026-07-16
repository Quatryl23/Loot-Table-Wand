package net.mesomods.lootwand.client.gui.loottable.numbers;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import net.mesomods.lootwand.mixin.numbers.ConstantValueAccessor;

import java.util.Set;

public class ConstantNumberProvider extends NumberProvider {
    public final float value;

    public ConstantNumberProvider(float f) {
        this.value = f;
        this.averageNumber = f;
    }

    @Override
    public void calculateIntProbabilities() {
        Int2DoubleMap probabilities = new Int2DoubleOpenHashMap();
        probabilities.put(Math.round(this.value), 1.0);
        this.intProbabilities = probabilities;
    }

    @Override
    public void calculateFloatProbabilities() {
        Int2DoubleMap probabilities = new Int2DoubleOpenHashMap();
        probabilities.put(Math.round(this.value * FLOAT_PRECISION), 1.0);
        this.floatProbabilities = new Int2DoubleOpenHashMap(probabilities);
    }

    public boolean isConstant() {
        return true;
    }

    @Override
    public boolean isUniform() {
        return false;
    }

    @Override
    public boolean isScoreboardDependent() {
        return false;
    }

    @Override
    public Set<ScoreboardNumberProvider> getScoreDependencies() {
        return Set.of();
    }

    @Override
    public net.minecraft.world.level.storage.loot.providers.number.NumberProvider toVanilla() {
        return ConstantValueAccessor.create(value);
    }

    @Override
    public float getAbsoluteMin() {
        return value;
    }

    @Override
    public float getAbsoluteMax() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ConstantNumberProvider c) {
            return this.value == c.value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Float.hashCode(value);
    }
}
