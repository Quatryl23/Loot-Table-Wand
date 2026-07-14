package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.MinMaxBounds;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.DistancePredicate.class)
public interface DistancePredicateAccessor {
    @Accessor
    MinMaxBounds.Doubles getX();

    @Mutable
    @Accessor
    void setX(MinMaxBounds.Doubles x);

    @Accessor
    MinMaxBounds.Doubles getY();

    @Mutable
    @Accessor
    void setY(MinMaxBounds.Doubles y);

    @Accessor
    MinMaxBounds.Doubles getZ();

    @Mutable
    @Accessor
    void setZ(MinMaxBounds.Doubles z);

    @Accessor
    MinMaxBounds.Doubles getHorizontal();

    @Mutable
    @Accessor
    void setHorizontal(MinMaxBounds.Doubles horizontal);

    @Accessor
    MinMaxBounds.Doubles getAbsolute();

    @Mutable
    @Accessor
    void setAbsolute(MinMaxBounds.Doubles absolute);
}
