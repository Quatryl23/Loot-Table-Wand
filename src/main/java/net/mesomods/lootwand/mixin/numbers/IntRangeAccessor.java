package net.mesomods.lootwand.mixin.numbers;

import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.IntRange.class)
public interface IntRangeAccessor {
    @Accessor
    NumberProvider getMin();

    @Mutable
    @Accessor
    void setMin(NumberProvider min);

    @Accessor
    NumberProvider getMax();

    @Mutable
    @Accessor
    void setMax(NumberProvider max);
}
