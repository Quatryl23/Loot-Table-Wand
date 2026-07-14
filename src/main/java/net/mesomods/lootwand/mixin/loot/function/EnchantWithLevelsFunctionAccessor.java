package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction.class)
public interface EnchantWithLevelsFunctionAccessor {
    @Accessor
    NumberProvider getLevels();

    @Mutable
    @Accessor
    void setLevels(NumberProvider levels);

    @Accessor
    boolean isTreasure();

    @Mutable
    @Accessor
    void setTreasure(boolean treasure);
}
