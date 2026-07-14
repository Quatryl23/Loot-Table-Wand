package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.MinMaxBounds;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.SlimePredicate.class)
public interface SlimePredicateAccessor {
    @Accessor
    MinMaxBounds.Ints getSize();

    @Mutable
    @Accessor
    void setSize(MinMaxBounds.Ints size);
}
