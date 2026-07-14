package net.mesomods.lootwand.mixin.loot.function;

import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.ApplyBonusCount.UniformBonusCount.class)
public interface UniformBonusCountAccessor {
    @Accessor
    int getBonusMultiplier();

    @Mutable
    @Accessor
    void setBonusMultiplier(int bonusMultiplier);
}
