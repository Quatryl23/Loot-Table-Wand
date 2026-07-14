package net.mesomods.lootwand.mixin.loot.condition;

import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition.class)
public interface LootItemRandomChanceWithLootingConditionAccessor {
    @Accessor
    float getLootingMultiplier();

    @Mutable
    @Accessor
    void setLootingMultiplier(float lootingMultiplier);

    @Accessor
    float getPercent();

    @Mutable
    @Accessor
    void setPercent(float percent);
}
