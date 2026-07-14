package net.mesomods.lootwand.mixin.loot.entry;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.entries.TagEntry.class)
public interface TagEntryAccessor extends LootPoolSingletonContainerAccessor {
    @Invoker("<init>")
    static TagEntry createTagEntry(TagKey<Item> p_205078_, boolean p_205079_, int p_205080_, int p_205081_, LootItemCondition[] p_205082_, LootItemFunction[] p_205083_) {
        throw new UnsupportedOperationException();
    }

    @Accessor
    TagKey<Item> getTag();

    @Accessor
    boolean isExpand();
}
