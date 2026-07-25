package net.mesomods.lootwand.client.gui.loottable;

import com.mojang.datafixers.util.Pair;
import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.client.gui.LootTableViewMode;
import net.mesomods.lootwand.client.gui.loottable.lifoc.RenderedCondition;
import net.mesomods.lootwand.client.gui.loottable.lifoc.RenderedFunction;
import net.mesomods.lootwand.client.gui.loottable.numbers.NumberProvider;
import net.mesomods.lootwand.client.gui.loottable.poolentry.*;
import net.mesomods.lootwand.mixin.loot.LootItemAccessor;
import net.mesomods.lootwand.mixin.loot.LootPoolAccessor;
import net.mesomods.lootwand.mixin.loot.entry.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenderedLootPool extends RenderedLootTable.Pool implements LifocParent {
    protected int totalHeight;
    protected NumberProvider count = null;
    protected List<Entry> entries;
    protected List<Entry> entriesOriginalOrder;
    protected LootItemFunction[] vanillaFunctions;
    protected LootItemCondition[] vanillaConditions;
    protected List<RenderedFunction> functions;
    protected List<RenderedCondition> conditions;
    protected NumberProvider rolls;
    protected NumberProvider bonusRolls;
    protected LootTableViewMode viewMode;
    protected float luck;

    public RenderedLootPool(RenderedLootTable table, List<Entry> entries, LootItemFunction[] functions, LootItemCondition[] conditions, NumberProvider rolls, NumberProvider bonusRolls, LootTableViewMode viewMode, float luck) {
        super(table);
        this.entries = new ArrayList<>(entries);
        this.entriesOriginalOrder = new ArrayList<>(entries);
        this.vanillaFunctions = functions;
        this.vanillaConditions = conditions;
        this.functions = Arrays.stream(vanillaFunctions).map(RenderedFunction::fromVanilla).toList();
        this.conditions = Arrays.stream(vanillaConditions).map(RenderedCondition::fromVanilla).toList();
        this.rolls = rolls;
        this.bonusRolls = bonusRolls;
        if (this.rolls != null) {
            rolls.getIntProbabilities();
        }
        if (this.bonusRolls != null) {
            bonusRolls.getFloatProbabilities();
        }
        this.viewMode = viewMode;
        this.luck = luck;
        onEntryUpdate();
    }

    public static RenderedLootPool fromVanilla(RenderedLootTable table, LootPoolAccessor pool, LootTableViewMode viewMode, float luck) {
        net.fabricmc.fabric.mixin.loot.LootPoolAccessor fabricPool = (net.fabricmc.fabric.mixin.loot.LootPoolAccessor) pool;
        return new RenderedLootPool(table, Arrays.stream(fabricPool.fabric_getEntries()).map(entry -> Entry.fromVanilla(entry, false)).toList(), fabricPool.fabric_getFunctions(), fabricPool.fabric_getConditions(), NumberProvider.fromVanilla(fabricPool.fabric_getRolls()), NumberProvider.fromVanilla(fabricPool.fabric_getBonusRolls()), viewMode, luck);
    }

    public void onEntryUpdate() {
        int totalWeight = 0;
        for (Entry entry : entries) {
            entry.setViewMode(viewMode);
            totalWeight += entry.getQualityWeight(luck);
        }
        for (Entry entry : entries) {
            entry.calculateAdditionalData(totalWeight, rolls.getAverage(), luck);
        }
    }

    public void sortEntries() {
        boolean isRaw = viewMode == LootTableViewMode.RAW_LIST;
        this.entries.forEach((entry) -> {
            if (entry instanceof RenderedCompositeEntry composite) {
                composite.sortChildren(this.getSortingMode(), isRaw);
            }
        });
        if (this.getSortingMode() == RenderedLootTable.SortingMode.DEFAULT) {
            this.entries.clear();
            this.entries.addAll(this.entriesOriginalOrder);
        } else {
            if (isRaw) {
                this.getSortingMode().sortEntriesByRawData(entries);
            } else {
                this.getSortingMode().sortEntries(entries);
            }
        }
    }

    @Override
    public void toggleDefaultParameters(boolean hidden) {
        LifocParent.super.toggleDefaultParameters(hidden);
        this.entries.forEach(entry -> entry.toggleDefaultParameters(hidden));
        updateHeight(false);
    }

    public void applyFunctionPreviewEffects(boolean enabled) {
        this.functions.forEach((function) -> {
            function.applyPreviewEffect(this, enabled);
        });
        this.conditions.forEach((condition) -> {
            condition.applyPreviewEffect(this, enabled);
        });
        this.entries.forEach((entry) -> {
            if (entry != null) { // entry instanceof RenderedItemEntry) {
                entry.applyFunctionPreviewEffects(enabled);
            }
        });
        updateHeight(false);
        onEntryUpdate();
    }

    public Component getNarration() {
        return Component.empty();
    }

    @Nullable
    public Pair<Integer, Double> getFirstShownEntry(double heightIntoPool) {
        if (heightIntoPool < 0) {
            return new Pair<>(-1, heightIntoPool);
        }
        int totalHeight = 25;
        for (int i = 0; i < this.entries.size(); i++) {
            if (heightIntoPool < totalHeight) {
                return new Pair<>(i, heightIntoPool - totalHeight);
            }
            totalHeight += this.entries.get(i).getHeight();
        }
        if (heightIntoPool < totalHeight) {
            return new Pair<>(-2, heightIntoPool - totalHeight);
        }
        return null;
    }


    public int getEntryStartY(int targetEntry) {
        if (targetEntry == -1) {
            return 0;
        } else {
            int totalHeight = 25;
            for (int i = 0; i < this.entries.size(); i++) {
                if (i == targetEntry) {
                    return totalHeight;
                }
                totalHeight += this.entries.get(i).getHeight();
            }
            return totalHeight;
        }
    }

    public int getHeight() {
        return totalHeight;
    }

    public void renderTopBar(GuiGraphics graphics, int top, int left, int width, int height, int mouseX, int mouseY) {
        int x = left + 5;
        Font FONT = Minecraft.getInstance().font;
        graphics.drawString(FONT, Component.translatable("gui.loot_table_wand.loot_pool.rolls"), x, top + 2, 0xFFFFFF);
        x += FONT.width(Component.translatable("gui.loot_table_wand.loot_pool.rolls")) + 5;
        ScreenUtils.renderNumberProvider(graphics, rolls, false, x, top + 2, 0xFFFFFF, false, mouseX, mouseY);
        x += 50;
        if (bonusRolls.getAverage() != 0) {
            graphics.drawString(FONT, Component.translatable("gui.loot_table_wand.loot_pool.bonus_rolls"), x, top + 2, 0xFFFFFF);
            x += FONT.width(Component.translatable("gui.loot_table_wand.loot_pool.bonus_rolls")) + 5;
            ScreenUtils.renderNumberProvider(graphics, bonusRolls, true, x, top + 2, 0xFFFFFF, false, mouseX, mouseY);
        }
        graphics.renderOutline(left, top + 13, width - 1, 9, 0xFF888888);
        boolean isRaw = (viewMode == LootTableViewMode.RAW_LIST);
        int i;
        i = ScreenUtils.drawScaledString(graphics, isRaw ? Component.translatable("gui.loot_table_wand.loot_pool.weight") : Component.translatable("gui.loot_table_wand.loot_pool.chance"), left + (isRaw ? 17 : 22), top + 15, 0xFF888888, 0.5F, true);
        table.renderSortingArrow(graphics, i, top + 15, 0);
        i = ScreenUtils.drawScaledString(graphics, isRaw ? Component.translatable("gui.loot_table_wand.loot_pool.quality") : Component.translatable("gui.loot_table_wand.loot_pool.average_number"), left + (isRaw ? 47 : 62), top + 15, 0xFF888888, 0.5F, true);
        table.renderSortingArrow(graphics, i, top + 15, 1);
        i = ScreenUtils.drawScaledString(graphics, isRaw ? Component.translatable("gui.loot_table_wand.loot_pool.count") : Component.translatable("gui.loot_table_wand.loot_pool.average_to_find"), left + (isRaw ? 82 : 102), top + 15, 0xFF888888, 0.5F, true);
        table.renderSortingArrow(graphics, i, top + 15, 2);
    }

    public boolean mouseClicked(double x, double y, int key, int renderTop, int renderLeft) {
        if (y > renderTop + 15 && y < renderTop + 25) return table.clickedOnInfoBar(x, y, key);
        int y0 = renderTop + 25;
        int y1;
        for (Entry entry : this.entries) {
            y1 = y0 + entry.getHeight();
            if (y1 > y && y > y0)
                return entry.mouseClicked(x, y, key, renderLeft, y0);
            y0 = y1;
        }
        for (RenderedCondition condition : this.conditions) {
            y1 = y0 + condition.getHeight();
            if (y1 > y && y > y0)
                return condition.mouseClicked(x, y, key, renderLeft, y0);
            y0 = y1;
        }
        for (RenderedFunction function : this.functions) {
            y1 = y0 + function.getHeight();
            if (y1 > y && y > y0)
                return function.mouseClicked(x, y, key, renderLeft, y0);
            y0 = y1;
        }
        return false;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovered, float partialTicks) {
        this.renderTopBar(graphics, top, left, width, height, mouseX, mouseY);
        graphics.renderOutline(left, top - 4, width - 1, height - 2, 0xFF888888);
        int renderTop = top + 25;
        for (Entry entry : entries) {
            entry.render(graphics, renderTop, left, left, width, 0, mouseX, mouseY, List.of(), List.of());
            renderTop += entry.getHeight();
        }
        for (RenderedCondition condition : conditions) {
            if (condition.isHidden())
                continue;
            condition.render(graphics, left, left + width - 1, renderTop, 0, false, mouseX, mouseY);
            renderTop += condition.getHeight();
        }
        for (RenderedFunction function : functions) {
            if (function.isHidden())
                continue;
            function.render(graphics, left, left + width - 1, renderTop, 0, false, mouseX, mouseY);
            renderTop += function.getHeight();
        }
    }

    @Override
    public List<RenderedFunction> getFunctions() {
        return functions;
    }

    @Override
    public List<RenderedCondition> getConditions() {
        return conditions;
    }

    @Override
    public void updateHeight(boolean updateTable) {
        totalHeight = 28;
        for (Entry entry : entries) {
            totalHeight += entry.getHeight();
        }
        for (RenderedCondition condition : conditions) {
            if (!condition.isHidden()) {
                totalHeight += condition.getHeight();
            }
        }
        for (RenderedFunction function : functions) {
            if (!function.isHidden())
                totalHeight += function.getHeight();
        }
    }

    public void acceptPreviewList(ResourceLocation location, int[] previewTimes, ListTag previewItems, boolean isTag) {
        this.entries.forEach(entry -> entry.acceptPreviewList(location, previewTimes, previewItems, isTag));
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setViewMode(LootTableViewMode viewMode) {
        this.viewMode = viewMode;
        for (Entry entry : entries) {
            entry.setViewMode(viewMode);
        }
    }

    public void setLuck(float luck) {
        this.luck = luck;
        this.onEntryUpdate();
    }

    public abstract static class Entry implements Clickable {

        final protected boolean inLifoc;

        protected Entry(boolean inLifoc) {
            this.inLifoc = inLifoc;
        }

        protected Runnable heightUpdater = () -> {
        };

        public static Entry fromVanilla(LootPoolEntryContainer entry, boolean inLifoc) {
            if (entry instanceof LootItemAccessor item) {
                return RenderedItemEntry.fromVanilla(item, inLifoc);
            } else if (entry instanceof DynamicLootAccessor dynamic) {
                return DynamicLootEntry.fromVanilla(dynamic, inLifoc);
            } else if (entry instanceof LootTableReferenceAccessor lootTable) {
                return LootTableReferenceEntry.fromVanilla(lootTable, inLifoc);
            } else if (entry instanceof EmptyLootItem && entry instanceof LootPoolSingletonContainerAccessor singleton) {
                return RenderedItemEntry.empty(singleton, inLifoc);
            } else if (entry instanceof TagEntryAccessor tag) {
                return RenderedTagEntry.fromVanilla(tag, inLifoc);
            } else if (entry instanceof EntryGroupAccessor group) {
                return RenderedGroupEntry.fromVanilla(group, inLifoc);
            } else if (entry instanceof AlternativesEntryAccessor alts) {
                return RenderedAlternativesEntry.fromVanilla(alts, inLifoc);
            } else if (entry instanceof SequentialEntryAccessor sequence) {
                return RenderedSequenceEntry.fromVanilla(sequence, inLifoc);
            }
            return new UnknownEntry(inLifoc);
        }

        public abstract boolean mouseClicked(double x, double y, int key, int renderLeft, int renderTop, List<RenderedCondition> additionalConditions, List<RenderedFunction> additionalFunctions);

        public void acceptPreviewList(ResourceLocation location, int[] previewTimes, ListTag previewItems, boolean isTag) {
        }

        public abstract void toggleDefaultParameters(boolean hidden);

        public abstract void applyFunctionPreviewEffects(boolean enabled);

        public abstract LootPoolEntryContainer toVanilla();

        public abstract void render(GuiGraphics graphics, int top, int left, int poolLeft, int width, int transparency, int mouseX, int mouseY, List<RenderedCondition> additionalConditions, List<RenderedFunction> additionalFunctions);

        public void setHeightUpdater(Runnable heightUpdater) {
            this.heightUpdater = heightUpdater;
        }

        public void updateHeight(boolean updatePool) {
            if (updatePool)
                heightUpdater.run();
        }

        public abstract boolean isConditional();

        public abstract boolean isNothing();

        public abstract int getWeight();

        public abstract int getQuality();

        public abstract int getQualityWeight(double luck);

        public abstract double getChance();

        public abstract double getAverageCount();

        public abstract double getAveragePerTry();

        public abstract double getAverageTriesToGet();

        public abstract void calculateAdditionalData(int totalWeight, double averageRolls, float luck);

        public abstract void setViewMode(LootTableViewMode viewMode);

        public abstract int getHeight();

        public abstract int getHeight(List<RenderedCondition> additionalConditions, List<RenderedFunction> additionalFunctions);
    }
}
