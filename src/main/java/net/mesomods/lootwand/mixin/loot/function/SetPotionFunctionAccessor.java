package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.world.item.alchemy.Potion;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.SetPotionFunction.class)
public interface SetPotionFunctionAccessor {
    @Accessor
    Potion getPotion();

    @Mutable
    @Accessor
    void setPotion(Potion potion);
}
