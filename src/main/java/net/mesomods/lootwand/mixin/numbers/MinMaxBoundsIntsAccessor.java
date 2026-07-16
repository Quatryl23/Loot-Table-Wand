package net.mesomods.lootwand.mixin.numbers;

import net.minecraft.advancements.critereon.MinMaxBounds;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.MinMaxBounds.Ints.class)
public interface MinMaxBoundsIntsAccessor {
    @Invoker("<init>")
    static MinMaxBounds.Ints createInts(@Nullable Integer p_55369_, @Nullable Integer p_55370_) {
        throw new UnsupportedOperationException();
    }
}
