package net.mesomods.lootwand.mixin.loot.entry;

import net.minecraft.world.level.storage.loot.entries.EntryGroup;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.entries.EntryGroup.class)
public interface EntryGroupAccessor extends CompositeEntryBaseAccessor {
    @Invoker("<init>")
    static EntryGroup createEntryGroup(LootPoolEntryContainer[] p_79550_, LootItemCondition[] p_79551_) {
        throw new UnsupportedOperationException();
    }
}
