package net.mesomods.lootwand.mixin.numbers;

import net.minecraft.advancements.critereon.MinMaxBounds;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.MinMaxBounds.Doubles.class)
public interface MinMaxBoundsDoublesAccessor {
    @Invoker("<init>")
    static MinMaxBounds.Doubles createDoubles(@Nullable Double p_154784_, @Nullable Double p_154785_) {
        throw new UnsupportedOperationException();
    }
}
