package net.mesomods.lootwand.mixin.loot.condition;

import net.minecraft.world.level.storage.loot.IntRange;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.predicates.TimeCheck.class)
public interface TimeCheckAccessor {
    @Accessor
    Long getPeriod();

    @Mutable
    @Accessor
    void setPeriod(Long period);

    @Accessor
    IntRange getValue();

    @Mutable
    @Accessor
    void setValue(IntRange value);
}
