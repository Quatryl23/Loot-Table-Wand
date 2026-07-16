package net.mesomods.lootwand.client.gui.loottable.poolentry;

import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.client.gui.LootTableViewMode;
import net.mesomods.lootwand.client.gui.loottable.LifocParent;
import net.mesomods.lootwand.client.gui.loottable.RenderedLootPool;
import net.mesomods.lootwand.client.gui.loottable.RenderedLootTable;
import net.mesomods.lootwand.client.gui.loottable.lifoc.RenderedCondition;
import net.mesomods.lootwand.client.gui.loottable.lifoc.RenderedFunction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class RenderedCompositeEntry extends RenderedLootPool.Entry implements LifocParent {
    public int totalHeight;
    protected List<RenderedLootPool.Entry> children;
    protected List<RenderedLootPool.Entry> childrenOriginalOrder;
    protected List<RenderedCondition> conditions;
    protected final LootItemCondition[] vanillaConditions;

    public RenderedCompositeEntry(LootPoolEntryContainer[] children, LootItemCondition[] conditions, boolean inLifoc) {
        super(inLifoc);
        this.children = new ArrayList<>(Arrays.stream(children).map(entry -> RenderedLootPool.Entry.fromVanilla(entry, inLifoc)).toList());
        this.vanillaConditions = conditions;
        this.conditions = Arrays.stream(conditions).map(RenderedCondition::fromVanilla).toList();
        setFunctionHeightUpdater();
    }


    public abstract int renderChildren(GuiGraphics graphics, int top, int left, int poolLeft, int width, int transparency, int mouseX, int mouseY, List<RenderedFunction> additionalFunctions);

    @Override
    public void render(GuiGraphics graphics, int top, int left, int poolLeft, int width, int transparency, int mouseX, int mouseY, List<RenderedCondition> additionalConditions, List<RenderedFunction> additionalFunctions) {
        ScreenUtils.renderOutline(graphics, left + 2, top, width - 5, getChildrenHeight(additionalFunctions) + 2, transparency, false);
        int renderTop = renderChildren(graphics, top + 3, left, poolLeft, width, transparency, mouseX, mouseY, additionalFunctions) - 1;
        for (RenderedCondition condition : additionalConditions) {
            if (condition.isHidden())
                continue;
            condition.render(graphics, left + 2, left + width - 3, renderTop, transparency, true, mouseX, mouseY);
            renderTop += condition.getHeight();
        }
        for (RenderedCondition condition : conditions) {
            if (condition.isHidden())
                continue;
            condition.render(graphics, left + 2, left + width - 3, renderTop, transparency, true, mouseX, mouseY);
            renderTop += condition.getHeight();
        }
    }

    public abstract int mouseClickedIterateChildren(double x, double y, int key, int renderTop, int renderLeft, List<RenderedFunction> additionalFunctions);

    @Override
    public boolean mouseClicked(double x, double y, int key, int renderLeft, int renderTop) {
        return this.mouseClicked(x, y, key, renderLeft, renderTop, List.of(), List.of());
    }

    public boolean mouseClicked(double x, double y, int key, int renderLeft, int renderTop, List<RenderedCondition> additionalConditions, List<RenderedFunction> additionalFunctions) {
        int y0 = renderTop + 3;
        int y1;
        int i = mouseClickedIterateChildren(x, y, key, y0, renderLeft, additionalFunctions);
        if (i == -1) return true;
        if (i == -2) return false;
        y0 = i - 1;
        for (RenderedCondition condition : additionalConditions) {
            if (condition.isHidden()) continue;
            y1 = y0 + condition.getHeight();
            if (y1 > y && y > y0)
                return condition.mouseClicked(x, y, key, renderLeft + 2, y0);
            y0 = y1;
        }
        for (RenderedCondition condition : this.conditions) {
            if (condition.isHidden()) continue;
            y1 = y0 + condition.getHeight();
            if (y1 > y && y > y0)
                return condition.mouseClicked(x, y, key, renderLeft + 2, y0);
            y0 = y1;
        }
        return false;
    }

    @Override
    public List<RenderedFunction> getFunctions() {
        return List.of();
    }

    @Override
    public List<RenderedCondition> getConditions() {
        return conditions;
    }

    @Override
    public void acceptPreviewList(ResourceLocation location, int[] previewTimes, ListTag previewItems, boolean isTag) {
        for (RenderedLootPool.Entry child : children) {
            child.acceptPreviewList(location, previewTimes, previewItems, isTag);
        }
    }

    @Override
    public void toggleDefaultParameters(boolean hidden) {
        for (RenderedLootPool.Entry child : children) {
            child.toggleDefaultParameters(hidden);
        }
        LifocParent.super.toggleDefaultParameters(hidden);
        updateHeight(false);
    }

    public void applyFunctionPreviewEffects(boolean enabled) {
        for (RenderedLootPool.Entry child: children) {
            child.applyFunctionPreviewEffects(enabled);
        }
        updateHeight(false);
    }

    public abstract int getChildrenHeight(List<RenderedFunction> additionalFunctions);

    public void updateHeight(boolean updatePool) {
        this.totalHeight = 4 + getChildrenHeight(List.of());
        for (RenderedCondition condition : conditions) {
            if (!condition.isHidden()) {
                totalHeight += condition.getHeight();
            }
        }
        if (!conditions.isEmpty()) totalHeight += 1;
        super.updateHeight(updatePool);
    }

    @Override
    public void setViewMode(LootTableViewMode mode) {
        this.children.forEach(entry -> entry.setViewMode(mode));
    }

    public void calculateAdditionalData(int totalWeight, double averageRolls, float luck) {
        for (RenderedLootPool.Entry entry : children) {
            entry.calculateAdditionalData(totalWeight, averageRolls, luck);
        }
    }

    @Override
    public int getHeight(List<RenderedCondition> additionalConditions, List<RenderedFunction> additionalFunctions) {
        int i = 4 + getChildrenHeight(additionalFunctions);
        for (RenderedCondition condition : additionalConditions) {
            if (!condition.isHidden()) {
                i += condition.getHeight();
            }
        }
        for (RenderedCondition condition : conditions) {
            if (!condition.isHidden()) {
                i += condition.getHeight();
            }
        }
        if (!conditions.isEmpty() || !additionalConditions.isEmpty()) i += 1;
        return i;
    }

    public void sortChildren(RenderedLootTable.SortingMode sortingMode, boolean useRawData) {
    }

    @Override
    public boolean isConditional() {
        return !conditions.isEmpty();
    }

    @Override
    public boolean isNothing() {
        return false;
    }

    @Override
    public int getHeight() {
        return totalHeight;
    }

    @Override
    public int getWeight() {
        int weight = 0;
        for (RenderedLootPool.Entry child: children) {
            weight += child.getWeight();
        }
        return weight;
    }

    @Override
    public int getQuality() {
        int quality = 0;
        for (RenderedLootPool.Entry child: children) {
            quality += child.getQuality();
        }
        return quality;
    }

    @Override
    public int getQualityWeight(double luck) {
        int quality = 0;
        for (RenderedLootPool.Entry child: children) {
            quality += child.getQualityWeight(luck);
        }
        return quality;
    }

    @Override
    public double getAverageCount() {
        double d = 0;
        for (RenderedLootPool.Entry entry : children) {
            double average = entry.getAverageCount();
            if (average > d) d = average;
        }
        return d;
    }

    @Override
    public double getChance() {
        double d = 0;
        for (RenderedLootPool.Entry entry : children) {
            double chance = entry.getChance();
            if (chance > d) d = chance;
        }
        return d;
    }

    @Override
    public double getAveragePerTry() {
        double d = 0;
        for (RenderedLootPool.Entry entry : children) {
            double average = entry.getAveragePerTry();
            if (average > d) d = average;
        }
        return d;
    }

    @Override
    public double getAverageTriesToGet() {
        double d = 0;
        for (RenderedLootPool.Entry entry : children) {
            double average = entry.getAverageTriesToGet();
            if (average > d) d = average;
        }
        return d;
    }
}
