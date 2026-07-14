package net.mesomods.lootwand.mixin.loot.entry;

import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.entries.AlternativesEntry.class)
public interface AlternativesEntryAccessor extends CompositeEntryBaseAccessor {
    @Invoker("<init>")
    static AlternativesEntry createAlternativesEntry(LootPoolEntryContainer[] p_79384_, LootItemCondition[] p_79385_) {
        throw new UnsupportedOperationException();
    }
}
