package net.mesomods.lootwand.client.gui.loottable.poolentry;

import net.mesomods.lootwand.client.gui.loottable.RenderedLootPool;
import net.mesomods.lootwand.mixin.loot.entry.SequentialEntryAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.SequentialEntry;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class RenderedSequenceEntry extends RenderedIterationCompositeEntry {
    public static final Component DESCRIPTION = Component.translatable("gui.loot_table_wand.loot_pool.sequence_entry");
    public RenderedSequenceEntry(LootPoolEntryContainer[] children, LootItemCondition[] conditions, boolean inLifoc) {
        super(children, conditions, inLifoc);
    }

    @Override
    public Component getDescription() {
        return DESCRIPTION;
    }

    public static RenderedSequenceEntry fromVanilla(SequentialEntryAccessor group, boolean inLifoc) {
        return new RenderedSequenceEntry(group.getChildren(), group.getConditions(), inLifoc);
    }

    @Override
    public SequentialEntry toVanilla() {
        return SequentialEntryAccessor.createSequentialEntry((LootPoolEntryContainer[]) children.stream().map(RenderedLootPool.Entry::toVanilla).toArray(), vanillaConditions);
    }
}
