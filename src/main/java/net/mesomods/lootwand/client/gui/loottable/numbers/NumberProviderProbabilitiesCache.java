package net.mesomods.lootwand.client.gui.loottable.numbers;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import net.mesomods.lootwand.LootWandMod;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NumberProviderProbabilitiesCache {
    private static final ConcurrentHashMap<NumberProvider, Int2DoubleMap> intProbabilitiesCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<NumberProvider, Int2DoubleMap> floatProbabilitiesCache = new ConcurrentHashMap<>();
    private static final Set<NumberProvider> intCalculated = new HashSet<>();
    private static final Set<NumberProvider> floatCalculated = new HashSet<>();

    @Nullable
    public static Int2DoubleMap getIntProbabilities(NumberProvider provider) {
        if (!intCalculated.contains(provider)) {
            intCalculated.add(provider);
            LootWandMod.addProbabilityCalculation(() -> {
                provider.calculateIntProbabilities();
                intProbabilitiesCache.put(provider, provider.getIntProbabilities());
            });
        }
        return intProbabilitiesCache.get(provider);
    }
    @Nullable
    public static Int2DoubleMap getFloatProbabilities(NumberProvider provider) {
        if (!floatCalculated.contains(provider)) {
            floatCalculated.add(provider);
            LootWandMod.addProbabilityCalculation(() -> {
                provider.calculateFloatProbabilities();
                floatProbabilitiesCache.put(provider, provider.getFloatProbabilities());
            });
        }
        return floatProbabilitiesCache.get(provider);
    }
}
