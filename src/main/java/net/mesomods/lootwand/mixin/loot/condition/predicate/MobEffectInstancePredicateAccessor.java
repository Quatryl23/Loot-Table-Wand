package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.MinMaxBounds;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.MobEffectsPredicate.MobEffectInstancePredicate.class)
public interface MobEffectInstancePredicateAccessor {
    @Accessor
    MinMaxBounds.Ints getAmplifier();

    @Mutable
    @Accessor
    void setAmplifier(MinMaxBounds.Ints amplifier);

    @Accessor
    MinMaxBounds.Ints getDuration();

    @Mutable
    @Accessor
    void setDuration(MinMaxBounds.Ints duration);

    @Accessor
    Boolean getAmbient();

    @Mutable
    @Accessor
    void setAmbient(Boolean ambient);

    @Accessor
    Boolean getVisible();

    @Mutable
    @Accessor
    void setVisible(Boolean visible);
}
