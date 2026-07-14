package net.mesomods.lootwand.mixin.loot.entry;

import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.SequentialEntry;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.entries.SequentialEntry.class)
public interface SequentialEntryAccessor extends CompositeEntryBaseAccessor {
    @Invoker("<init>")
    static SequentialEntry createSequentialEntry(LootPoolEntryContainer[] p_79812_, LootItemCondition[] p_79813_) {
        throw new UnsupportedOperationException();
    }
}
