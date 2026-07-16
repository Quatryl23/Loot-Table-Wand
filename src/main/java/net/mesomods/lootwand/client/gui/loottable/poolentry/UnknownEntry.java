package net.mesomods.lootwand.client.gui.loottable.poolentry;

import net.mesomods.lootwand.client.gui.loottable.lifoc.RenderedCondition;
import net.mesomods.lootwand.client.gui.loottable.lifoc.RenderedFunction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class UnknownEntry extends RenderedSingletonEntry {
    public static final ItemStack SYMBOL = new ItemStack(Items.BARRIER);

    public UnknownEntry(boolean inLifoc) {
        super(new LootItemCondition[]{}, new LootItemFunction[]{}, -1, 0, inLifoc);
        this.description = Component.translatable("gui.loot_table_wand.loot_pool.unknown_entry");
        super.updateDescription();
    }

    public void render(GuiGraphics graphics, int top, int left, int poolLeft, int width, int transparency, int mouseX, int mouseY, List<RenderedCondition> additionalConditions, List<RenderedFunction> additionalFunctions) {
        super.render(graphics, top, poolLeft, width, transparency, null, SYMBOL, mouseX, mouseY, List.of(), List.of());
    }

    @Override
    public boolean isNothing() {
        return true;
    }

    @Override
    public LootPoolEntryContainer toVanilla() {
        return null;
    }

    @Override
    protected double calculateAverageCount() {
        return 0;
    }
}
