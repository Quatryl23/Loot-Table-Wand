package net.mesomods.lootwand.mixin.loot.entry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.entries.DynamicLoot.class)
public interface DynamicLootAccessor extends LootPoolSingletonContainerAccessor {
    @Invoker("<init>")
    static DynamicLoot createDynamicLoot(ResourceLocation p_79465_, int p_79466_, int p_79467_, LootItemCondition[] p_79468_, LootItemFunction[] p_79469_) {
        throw new UnsupportedOperationException();
    }

    @Accessor
    ResourceLocation getName();

    @Mutable
    @Accessor
    void setName(ResourceLocation name);
}
