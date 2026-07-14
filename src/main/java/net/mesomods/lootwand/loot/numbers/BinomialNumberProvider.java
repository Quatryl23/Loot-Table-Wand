package net.mesomods.lootwand.loot.numbers;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import net.mesomods.lootwand.mixin.numbers.BinomialDistributionGeneratorAccessor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class BinomialNumberProvider extends NumberProvider {
    public final NumberProvider n;
    public final NumberProvider p;

    public BinomialNumberProvider(NumberProvider n, NumberProvider p) {
        this.n = n;
        this.p = p;
        this.averageNumber = n.averageNumber * p.averageNumber;
    }

    @Override
    public void calculateIntProbabilities() {
        Int2DoubleMap probabilities = new Int2DoubleOpenHashMap();
        for (Int2DoubleMap.Entry nEntry : n.getIntProbabilities().int2DoubleEntrySet()) {
            int n = nEntry.getIntKey();
            double probability1 = nEntry.getDoubleValue();
            for (Int2DoubleMap.Entry pEntry : p.getFloatProbabilities().int2DoubleEntrySet()) {
                float p = (float) pEntry.getIntKey() / FLOAT_PRECISION;
                double probability2 = pEntry.getDoubleValue();
                for (int i = 0; i <= n; i++) {
                    double probability = probability1 * probability2 * binomialProbability(n, p, i);
                    probabilities.put(i, probabilities.get(i) + probability);
                }
            }
        }
        this.intProbabilities = probabilities;
    }

    @Override
    public void calculateFloatProbabilities() {
        Int2DoubleMap probabilities = new Int2DoubleOpenHashMap();
        for (Int2DoubleMap.Entry nEntry : n.getIntProbabilities().int2DoubleEntrySet()) {
            int n = nEntry.getIntKey();
            double probability1 = nEntry.getDoubleValue();
            for (Int2DoubleMap.Entry pEntry : p.getFloatProbabilities().int2DoubleEntrySet()) {
                float p = (float) pEntry.getIntKey() / FLOAT_PRECISION;
                double probability2 = pEntry.getDoubleValue();
                for (int i = 0; i <= n; i++) {
                    double probability = probability1 * probability2 * binomialProbability(n, p, i);
                    int j = i * FLOAT_PRECISION;
                    probabilities.put(j, probabilities.get(j) + probability);
                }
            }
        }
        this.floatProbabilities = probabilities;
    }

    public static double binomialProbability(int n, float p, int k) {
        return BigDecimal.valueOf(binomialCoefficient(n, k).longValue()).multiply(BigDecimal.valueOf(p).pow(k)).multiply(BigDecimal.valueOf(1 - p).pow(n - k)).doubleValue();
    }

    public static BigInteger binomialCoefficient(int n, int k) {

        if (2 * k > n) k = n - k;
        BigInteger i = BigInteger.ONE;
        for (int j = 1; j <= k; j++) {
            i = i.multiply(BigInteger.valueOf(n + 1 - j)).divide(BigInteger.valueOf(j));
        }
        return i;
    }

    public boolean isConstant() {
        return false;
    }

    @Override
    public boolean isUniform() {
        return false;
    }

    @Override
    public boolean isScoreboardDependent() {
        return n.isScoreboardDependent() || p.isScoreboardDependent();
    }

    @Override
    public Set<ScoreboardNumberProvider> getScoreDependencies() {
        Set<ScoreboardNumberProvider> set = new HashSet<>();
        set.addAll(n.getScoreDependencies());
        set.addAll(p.getScoreDependencies());
        return set;
    }

    @Override
    public net.minecraft.world.level.storage.loot.providers.number.NumberProvider toVanilla() {
        return BinomialDistributionGeneratorAccessor.create(this.n.toVanilla(), this.p.toVanilla());
    }

    @Override
    public float getAbsoluteMin() {
        return 0;
    }

    @Override
    public float getAbsoluteMax() {
        return n.getAbsoluteMax();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BinomialNumberProvider b) {
            return (this.n.equals(b.n) && this.p.equals(b.p));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(n, p);
    }

}
