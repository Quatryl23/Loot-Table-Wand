package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction.class)
public interface SetEnchantmentsFunctionAccessor {
    @Accessor
    Map<Enchantment, NumberProvider> getEnchantments();

    @Mutable
    @Accessor
    void setEnchantments(Map<Enchantment, NumberProvider> enchantments);

    @Accessor
    boolean isAdd();

    @Mutable
    @Accessor
    void setAdd(boolean add);
}
