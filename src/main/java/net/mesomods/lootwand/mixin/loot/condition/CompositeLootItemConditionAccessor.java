package net.mesomods.lootwand.mixin.loot.condition;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.predicates.CompositeLootItemCondition.class)
public interface CompositeLootItemConditionAccessor {
    @Accessor
    LootItemCondition[] getTerms();

    @Mutable
    @Accessor
    void setTerms(LootItemCondition[] terms);
}
