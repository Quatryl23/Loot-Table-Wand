package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.LightPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.LightPredicate.class)
public interface LightPredicateAccessor {
    @Invoker("<init>")
    static LightPredicate createLightPredicate(MinMaxBounds.Ints p_51339_) {
        throw new UnsupportedOperationException();
    }

    @Accessor
    MinMaxBounds.Ints getComposite();

    @Mutable
    @Accessor
    void setComposite(MinMaxBounds.Ints composite);
}
