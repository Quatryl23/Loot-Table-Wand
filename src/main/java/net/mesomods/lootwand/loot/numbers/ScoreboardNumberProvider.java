package net.mesomods.lootwand.loot.numbers;

import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import net.mesomods.lootwand.mixin.numbers.ContextScoreboardNameProviderAccessor;
import net.mesomods.lootwand.mixin.numbers.ScoreboardValueAccessor;
import net.mesomods.lootwand.util.LootContextManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.providers.score.FixedScoreboardNameProvider;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProvider;

import java.util.Objects;
import java.util.Set;

public class ScoreboardNumberProvider extends NumberProvider {
    ScoreboardNameProvider target;
    String score;
    float scale;

    public ScoreboardNumberProvider(ScoreboardNameProvider target, String score, float scale) {
        this.target = target;
        this.score = score;
        this.scale = scale;
        this.averageNumber = Float.NaN;
    }

    public Component getDependencyInfo(int otherDependencies) {
        return otherDependencies == 0 ? Component.translatable("gui.loot_table_wand.numbers.scoreboard", score, getScoreboardName()) : Component.translatable("gui.loot_table_wand.numbers.scoreboard.multiple", score, getScoreboardName(), otherDependencies);
    }

    public float getScale() {
        return scale;
    }

    private String getScoreboardName() {
        if (target instanceof ContextScoreboardNameProviderAccessor c) {
            return LootContextManager.getEntityTargetTranslation(c.getTarget()).getString();
        } else if (target instanceof FixedScoreboardNameProvider f) {
            return f.getName();
        }
        return Component.translatable("gui.loot_table_wand.loot_context.unknown").getString();
    }

    @Override
    public void calculateIntProbabilities() {
        this.intProbabilities = new Int2DoubleOpenHashMap();
    }

    @Override
    public void calculateFloatProbabilities() {
        this.floatProbabilities = new Int2DoubleOpenHashMap();
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
        return true;
    }

    @Override
    public Set<ScoreboardNumberProvider> getScoreDependencies() {
        return Set.of(this);
    }

    @Override
    public net.minecraft.world.level.storage.loot.providers.number.NumberProvider toVanilla() {
        return ScoreboardValueAccessor.create(target, score, scale);
    }

    @Override
    public float getAbsoluteMin() {
        return Float.NaN;
    }

    @Override
    public float getAbsoluteMax() {
        return Float.NaN;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ScoreboardNumberProvider s) {
            return target.equals((s.target)) && score.equals(s.score) && scale == s.scale;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, score, scale);
    }
}
