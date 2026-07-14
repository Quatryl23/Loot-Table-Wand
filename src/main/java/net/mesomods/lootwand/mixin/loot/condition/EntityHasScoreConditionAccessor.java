package net.mesomods.lootwand.mixin.loot.condition;

import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.predicates.EntityHasScoreCondition.class)
public interface EntityHasScoreConditionAccessor {
    @Accessor
    Map<String, IntRange> getScores();

    @Mutable
    @Accessor
    void setScores(Map<String, IntRange> scores);

    @Accessor
    LootContext.EntityTarget getEntityTarget();

    @Mutable
    @Accessor
    void setEntityTarget(LootContext.EntityTarget entityTarget);
}
