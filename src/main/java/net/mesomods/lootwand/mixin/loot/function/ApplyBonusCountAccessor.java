package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ApplyBonusCount.class)
public interface ApplyBonusCountAccessor {
    @Accessor("formula")
    ApplyBonusCount.Formula getFormula();
    @Accessor("formula")
    void setFormula(ApplyBonusCount.Formula formula);
    @Accessor
    Enchantment getEnchantment();
    @Accessor
    void setEnchantment(Enchantment enchantment);
}
