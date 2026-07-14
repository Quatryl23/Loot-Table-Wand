package net.mesomods.lootwand.mixin.loot.condition;

import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.predicates.ValueCheckCondition.class)
public interface ValueCheckConditionAccessor {
    @Accessor
    NumberProvider getProvider();

    @Mutable
    @Accessor
    void setProvider(NumberProvider provider);

    @Accessor
    IntRange getRange();

    @Mutable
    @Accessor
    void setRange(IntRange range);
}
