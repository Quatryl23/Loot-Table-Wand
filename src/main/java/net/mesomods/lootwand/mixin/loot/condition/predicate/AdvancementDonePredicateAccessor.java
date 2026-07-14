package net.mesomods.lootwand.mixin.loot.condition.predicate;

import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.PlayerPredicate.AdvancementDonePredicate.class)
public interface AdvancementDonePredicateAccessor {
    @Accessor
    boolean isState();

    @Mutable
    @Accessor
    void setState(boolean state);
}
