package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.MinMaxBounds;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.DistancePredicate.class)
public interface DistancePredicateAccessor {
    @Accessor("x")
    MinMaxBounds.Doubles getX();

    @Mutable
    @Accessor("x")
    void setX(MinMaxBounds.Doubles x);

    @Accessor("y")
    MinMaxBounds.Doubles getY();

    @Mutable
    @Accessor("y")
    void setY(MinMaxBounds.Doubles y);

    @Accessor("z")
    MinMaxBounds.Doubles getZ();

    @Mutable
    @Accessor("z")
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
