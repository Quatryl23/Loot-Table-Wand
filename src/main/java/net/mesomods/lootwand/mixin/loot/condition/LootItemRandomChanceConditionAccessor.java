package net.mesomods.lootwand.mixin.loot.condition;

import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition.class)
public interface LootItemRandomChanceConditionAccessor {
    @Accessor
    float getProbability();

    @Mutable
    @Accessor
    void setProbability(float probability);
}
