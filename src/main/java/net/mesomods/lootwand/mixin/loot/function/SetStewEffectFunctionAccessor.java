package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.SetStewEffectFunction.class)
public interface SetStewEffectFunctionAccessor {
    @Accessor
    Map<MobEffect, NumberProvider> getEffectDurationMap();

    @Mutable
    @Accessor
    void setEffectDurationMap(Map<MobEffect, NumberProvider> effectDurationMap);
}
