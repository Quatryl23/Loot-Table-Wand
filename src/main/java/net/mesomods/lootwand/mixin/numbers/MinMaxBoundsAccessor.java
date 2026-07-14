package net.mesomods.lootwand.mixin.numbers;

import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.MinMaxBounds.class)
public interface MinMaxBoundsAccessor<T extends Number> {

    @Accessor
    T getMin();

    @Mutable
    @Accessor
    void setMin(T min);

    @Accessor
    T getMax();

    @Mutable
    @Accessor
    void setMax(T max);
}
