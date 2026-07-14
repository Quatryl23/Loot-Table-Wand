package net.mesomods.lootwand.mixin.loot.entry;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.BiFunction;

@Mixin(LootPoolSingletonContainer.class)
public interface LootPoolSingletonContainerAccessor extends LootPoolEntryContainerAccessor {
    @Accessor
    int getWeight();

    @Accessor
    int getQuality();

    @Accessor
    LootItemFunction[] getFunctions();

    @Accessor
    BiFunction<ItemStack, LootContext, ItemStack> getCompositeFunction();
}
