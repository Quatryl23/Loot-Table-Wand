package net.mesomods.lootwand.mixin.loot.condition;

import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition.class)
public interface BonusLevelTableConditionAccessor {
    @Accessor
    Enchantment getEnchantment();

    @Mutable
    @Accessor
    void setEnchantment(Enchantment enchantment);

    @Accessor
    float[] getValues();

    @Mutable
    @Accessor
    void setValues(float[] values);
}
