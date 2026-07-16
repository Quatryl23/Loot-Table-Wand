package net.mesomods.lootwand.mixin.numbers;

import net.minecraft.advancements.critereon.MinMaxBounds;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.MinMaxBounds.Doubles.class)
public interface MinMaxBoundsDoublesAccessor {
    @Invoker("<init>")
    static MinMaxBounds.Doubles createDoubles(@Nullable Double p_154784_, @Nullable Double p_154785_) {
        throw new UnsupportedOperationException();
    }
}
