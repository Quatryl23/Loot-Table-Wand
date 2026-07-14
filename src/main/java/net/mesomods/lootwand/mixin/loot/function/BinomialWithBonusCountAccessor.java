package net.mesomods.lootwand.mixin.loot.function;

import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.ApplyBonusCount.BinomialWithBonusCount.class)
public interface BinomialWithBonusCountAccessor {
    @Accessor
    int getExtraRounds();

    @Mutable
    @Accessor
    void setExtraRounds(int extraRounds);

    @Accessor
    float getProbability();

    @Mutable
    @Accessor
    void setProbability(float probability);
}
