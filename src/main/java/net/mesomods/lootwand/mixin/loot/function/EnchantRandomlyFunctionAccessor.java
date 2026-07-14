package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction.class)
public interface EnchantRandomlyFunctionAccessor {
    @Accessor
    List<Enchantment> getEnchantments();

    @Mutable
    @Accessor
    void setEnchantments(List<Enchantment> enchantments);
}
