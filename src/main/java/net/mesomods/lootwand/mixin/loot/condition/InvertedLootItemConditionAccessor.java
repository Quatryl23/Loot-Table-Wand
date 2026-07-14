package net.mesomods.lootwand.mixin.loot.condition;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition.class)
public interface InvertedLootItemConditionAccessor {
    @Accessor
    LootItemCondition getTerm();

    @Mutable
    @Accessor
    void setTerm(LootItemCondition term);
}
