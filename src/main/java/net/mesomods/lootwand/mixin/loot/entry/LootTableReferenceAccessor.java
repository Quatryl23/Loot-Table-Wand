package net.mesomods.lootwand.mixin.loot.entry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.entries.LootTableReference.class)
public interface LootTableReferenceAccessor extends LootPoolSingletonContainerAccessor {
    @Invoker("<init>")
    static LootTableReference createLootTableReference(ResourceLocation p_79756_, int p_79757_, int p_79758_, LootItemCondition[] p_79759_, LootItemFunction[] p_79760_) {
        throw new UnsupportedOperationException();
    }

    @Accessor
    ResourceLocation getName();
}
