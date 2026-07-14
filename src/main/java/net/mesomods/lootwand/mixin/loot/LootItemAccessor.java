package net.mesomods.lootwand.mixin.loot;

import net.mesomods.lootwand.mixin.loot.entry.LootPoolSingletonContainerAccessor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.entries.LootItem.class)
public interface LootItemAccessor extends LootPoolSingletonContainerAccessor {
    @Accessor
    Item getItem();
    @Invoker("<init>")
    static LootItem create(Item item, int weight, int quality, LootItemCondition[] conditions, LootItemFunction[] functions) {
        throw new AssertionError();
    }

}
