package net.mesomods.lootwand.client.gui.loottable.poolentry;

import net.mesomods.lootwand.client.gui.loottable.RenderedLootPool;
import net.mesomods.lootwand.mixin.loot.entry.AlternativesEntryAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class RenderedAlternativesEntry extends RenderedIterationCompositeEntry {
    public static final Component DESCRIPTION = Component.translatable("gui.loot_table_wand.loot_pool.alternatives_entry");
    public RenderedAlternativesEntry(LootPoolEntryContainer[] children, LootItemCondition[] conditions, boolean inLifoc) {
        super(children, conditions, inLifoc);
    }

    public static RenderedAlternativesEntry fromVanilla(AlternativesEntryAccessor group, boolean inLifoc) {
        return new RenderedAlternativesEntry(group.getChildren(), group.getConditions(), inLifoc);
    }

    @Override
    public Component getDescription() {
        return DESCRIPTION;
    }

    @Override
    public AlternativesEntry toVanilla() {
        return AlternativesEntryAccessor.createAlternativesEntry((LootPoolEntryContainer[]) children.stream().map(RenderedLootPool.Entry::toVanilla).toArray(), vanillaConditions);
    }

    @Override
    public int getWeight() {
        return children.isEmpty() ? 0 : children.get(0).getWeight();
    }

    @Override
    public int getQuality() {
        return children.isEmpty() ? 0 : children.get(0).getQuality();
    }

    @Override
    public int getQualityWeight(double luck) {
        return children.isEmpty() ? 0 : children.get(0).getQualityWeight(luck);
    }

    @Override
    public double getAverageCount() {
        return children.isEmpty() ? 0 : children.get(0).getAverageCount();
    }

    @Override
    public double getChance() {
        return children.isEmpty() ? 0 : children.get(0).getChance();
    }

    @Override
    public double getAveragePerTry() {
        return children.isEmpty() ? 0 : children.get(0).getAveragePerTry();
    }

    @Override
    public double getAverageTriesToGet() {
        return children.isEmpty() ? 0 : children.get(0).getAverageTriesToGet();
    }
}
