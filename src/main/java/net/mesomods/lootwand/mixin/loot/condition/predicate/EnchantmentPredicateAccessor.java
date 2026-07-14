package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.EnchantmentPredicate.class)
public interface EnchantmentPredicateAccessor {
    @Accessor
    Enchantment getEnchantment();

    @Mutable
    @Accessor
    void setEnchantment(Enchantment enchantment);

    @Accessor
    MinMaxBounds.Ints getLevel();

    @Mutable
    @Accessor
    void setLevel(MinMaxBounds.Ints level);
}
