package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction.class)
public interface LootItemConditionalFunctionAccessor {
    @Accessor
    LootItemCondition[] getPredicates();

    @Mutable
    @Accessor
    void setPredicates(LootItemCondition[] predicates);

    @Invoker
    ItemStack callRun(ItemStack p_80679_, LootContext p_80680_);
}
