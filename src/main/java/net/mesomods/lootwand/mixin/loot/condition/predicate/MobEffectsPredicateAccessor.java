package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.MobEffectsPredicate.class)
public interface MobEffectsPredicateAccessor {
    @Accessor
    Map<MobEffect, MobEffectsPredicate.MobEffectInstancePredicate> getEffects();

    @Mutable
    @Accessor
    void setEffects(Map<MobEffect, MobEffectsPredicate.MobEffectInstancePredicate> effects);
}
