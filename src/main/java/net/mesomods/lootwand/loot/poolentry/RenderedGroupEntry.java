package net.mesomods.lootwand.loot.poolentry;

import net.mesomods.lootwand.loot.RenderedLootPool;
import net.mesomods.lootwand.loot.RenderedLootTable;
import net.mesomods.lootwand.loot.lifoc.RenderedFunction;
import net.mesomods.lootwand.mixin.loot.entry.EntryGroupAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.storage.loot.entries.EntryGroup;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.ArrayList;
import java.util.List;

public class RenderedGroupEntry extends RenderedCompositeEntry {
    public RenderedGroupEntry(LootPoolEntryContainer[] children, LootItemCondition[] conditions, boolean inLifoc) {
        super(children, conditions, inLifoc);
        this.childrenOriginalOrder = new ArrayList<>(this.children);
    }

    public static RenderedGroupEntry fromVanilla(EntryGroupAccessor group, boolean inLifoc) {
        return new RenderedGroupEntry(group.getChildren(), group.getConditions(), inLifoc);
    }

    @Override
    public int renderChildren(GuiGraphics graphics, int top, int left, int poolLeft, int width, int transparency, int mouseX, int mouseY, List<RenderedFunction> additionalFunctions) {
        for (RenderedLootPool.Entry child: children) {
            child.render(graphics, top, left + 2, poolLeft, width - 4, transparency, mouseX, mouseY, List.of(), additionalFunctions);
            top += child.getHeight(List.of(), additionalFunctions);
        }
        return top;
    }

    @Override
    public void sortChildren(RenderedLootTable.SortingMode sortingMode, boolean useRawData) {
        this.children.forEach((entry) -> {
            if (entry instanceof RenderedCompositeEntry composite) {
                composite.sortChildren(sortingMode, useRawData);
            }
        });
        if (sortingMode == RenderedLootTable.SortingMode.DEFAULT) {
            this.children.clear();
            this.children.addAll(this.childrenOriginalOrder);
        } else {
            if (useRawData) {
                sortingMode.sortEntriesByRawData(children);
            } else {
                sortingMode.sortEntries(children);
            }
        }
    }

    @Override
    public int mouseClickedIterateChildren(double x, double y, int key, int renderTop, int renderLeft, List<RenderedFunction> additionalFunctions) {
        int y0 = renderTop;
        int y1;
        for (RenderedLootPool.Entry entry : this.children) {
            y1 = y0 + entry.getHeight();
            if (y1 > y && y > y0) {
                if (entry.mouseClicked(x, y, key, renderLeft, y0, List.of(), additionalFunctions)) {
                    return -1;
                } else {
                    return -2;
                }
            }
            y0 = y1;
        }
        return y0;
    }

    @Override
    public int getChildrenHeight(List<RenderedFunction> additionalFunctions) {
        int i = 1;
        for (RenderedLootPool.Entry child: children) {
            i += child.getHeight(List.of(), additionalFunctions);
        }
        return i;
    }

    @Override
    public EntryGroup toVanilla() {
        return EntryGroupAccessor.createEntryGroup((LootPoolEntryContainer[]) children.stream().map(RenderedLootPool.Entry::toVanilla).toArray(), vanillaConditions);
    }
}
