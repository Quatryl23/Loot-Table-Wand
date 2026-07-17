package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(ApplyBonusCount.UniformBonusCount.class)
public interface UniformBonusCountAccessor {
    @Accessor
    int getBonusMultiplier();

    @Mutable
    @Accessor
    void setBonusMultiplier(int bonusMultiplier);
}
