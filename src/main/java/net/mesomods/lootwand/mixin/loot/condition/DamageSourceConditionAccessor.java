package net.mesomods.lootwand.mixin.loot.condition;

import net.minecraft.advancements.critereon.DamageSourcePredicate;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition.class)
public interface DamageSourceConditionAccessor {
    @Accessor
    DamageSourcePredicate getPredicate();

    @Mutable
    @Accessor
    void setPredicate(DamageSourcePredicate predicate);
}
