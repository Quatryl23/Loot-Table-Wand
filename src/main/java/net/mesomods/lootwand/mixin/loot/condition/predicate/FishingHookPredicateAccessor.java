package net.mesomods.lootwand.mixin.loot.condition.predicate;

import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.FishingHookPredicate.class)
public interface FishingHookPredicateAccessor {
    @Accessor
    boolean isInOpenWater();

    @Mutable
    @Accessor
    void setInOpenWater(boolean inOpenWater);
}
