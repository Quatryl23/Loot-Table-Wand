package net.mesomods.lootwand.loot;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.mesomods.lootwand.capabilities.LootTableWandPlayerDataManager;
import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.client.gui.LootTableViewMode;
import net.mesomods.lootwand.loot.lifoc.RenderedCondition;
import net.mesomods.lootwand.loot.lifoc.RenderedFunction;
import net.mesomods.lootwand.loot.numbers.NumberProvider;
import net.mesomods.lootwand.mixin.loot.LootPoolAccessor;
import net.mesomods.lootwand.mixin.loot.LootTableAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.ToDoubleFunction;

public class RenderedLootTable extends ObjectSelectionList<RenderedLootTable.Pool> implements LifocParent {
    public static final ResourceLocation SORTING_MOST = ResourceLocation.parse("loot_table_wand:textures/gui/sorting_most.png");
    public static final ResourceLocation SORTING_LEAST = ResourceLocation.parse("loot_table_wand:textures/gui/sorting_least.png");
    protected LootItemFunction[] vanillaFunctions;
    protected List<RenderedFunction> functions = new ArrayList<>();
    protected ResourceLocation randomSequence;
    protected ResourceLocation tableLocation;
    protected int functionsTotalHeight = 0;
    protected List<Integer> rowTops = new ArrayList<>();
    protected NumberProvider count = null;
    protected boolean initialized;
    protected long creationTimeMs;
    protected Pool hovered;
    protected FixedScrollTarget fixedScrollTarget;
    protected LootTableViewMode viewMode;
    protected SortingMode sortingMode;
    protected float playerLuck;

    public RenderedLootTable(Minecraft minecraft, int width, int height, int y0, int y1, ResourceLocation tableLocation, LootTableViewMode viewMode) {
        super(minecraft, width, height, y0, y1, 1);
        this.initialized = false;
        this.creationTimeMs = System.currentTimeMillis();
        this.updateRowTops();
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
        this.tableLocation = tableLocation;
        this.viewMode = viewMode;
        this.playerLuck = minecraft.player != null ? LootTableWandPlayerDataManager.getLuck(minecraft.player) : 0F;
        this.sortingMode = SortingMode.DEFAULT;
    }

    public void init(LootTableAccessor lootTable) {
        this.clearEntries();
        List<RenderedLootPool> renderedLootPools = new ArrayList<>();
        for (LootPool pool : lootTable.getPools()) {
            renderedLootPools.add(RenderedLootPool.fromVanilla(this, (LootPoolAccessor) pool, viewMode, playerLuck));
        }
        this.children().addAll(renderedLootPools);
        this.vanillaFunctions = lootTable.getFunctions();
        this.functions = Arrays.stream(vanillaFunctions).map(RenderedFunction::fromVanilla).toList();
        this.randomSequence = lootTable.getRandomSequence();
        if (this.randomSequence != null && this.randomSequence.equals(this.tableLocation)) this.randomSequence = null;
        this.initialized = true;
        if (viewMode == LootTableViewMode.LIST) {
            this.createCumulatedPool(renderedLootPools);
        }
        this.updateRowTops();
    }

    public void updateFunctionsHeight() {
        this.functionsTotalHeight = 0;
        if (hasShownFunctions() && renderFunctions()) {
            this.functionsTotalHeight = 12;
            for (RenderedFunction function : functions) {
                if (!function.isHidden())
                    functionsTotalHeight += function.getHeight();
            }
        }
    }

    public boolean hasShownFunctions() {
        for (RenderedFunction function : this.functions) {
            if (!function.isHidden()) {
                return true;
            }
        }
        return false;
    }

    public void prepareRowTopUpdate() {
        Pool pool = this.getEntryAtY(y0 + 3.0);
        int poolIndex = this.children().indexOf(pool);
        Pair<Integer,Double> fixedEntry;
        if (pool != null) {
            fixedEntry = pool.getFirstShownEntry(getChildScrollAmount(pool));
            if (fixedEntry == null) {
                if (poolIndex < this.children().size() - 1) {
                    pool = this.children().get(poolIndex + 1);
                    fixedEntry = pool.getFirstShownEntry(getChildScrollAmount(pool));
                }
                if (fixedEntry != null) {
                    this.fixedScrollTarget = new FixedScrollTarget(pool, fixedEntry.getFirst(), fixedEntry.getSecond());
                }
            }
        }
    }

    public void updateRowTops() {
        rowTops.clear();
        rowTops.add(this.headerHeight);
        int j = this.getItemCount();
        for (int i = 0; i < j; i++) {
            rowTops.add(this.rowTops.get(i) + this.getEntry(i).getHeight() + 4);
        }
        if (fixedScrollTarget != null) {
            Pool pool = fixedScrollTarget.pool();
            if (this.children().contains(pool)) {
                this.setScrollAmount(rowTops.get(this.children().indexOf(fixedScrollTarget.pool())) + pool.getEntryStartY(fixedScrollTarget.entryId()) + fixedScrollTarget.scrollInEntry);
            } else {
                this.scroll(0);
            }
            fixedScrollTarget = null;
        } else {
            this.scroll(0);
        }
    }

    public double getChildScrollAmount(Pool pool) {
        return this.getScrollAmount() - this.rowTops.get(this.children().indexOf(pool));
    }

    @Override
    public int getRowWidth() {
        return this.width - 30;
    }

    @Override
    public int getScrollbarPosition() {
        return this.width - 7;
    }

    @Override
    public int addEntry(@NotNull Pool entry) {
        int i = super.addEntry(entry);
        this.updateRowTops();
        return i;
    }

    @Override
    public void addEntryToTop(@NotNull Pool entry) {
        super.addEntryToTop(entry);
        this.updateRowTops();
    }

    @Override
    public boolean removeEntry(@NotNull Pool entry) {
        boolean b = super.removeEntry(entry);
        this.updateRowTops();
        return b;
    }

    @Override
    public void clearEntries() {
        super.clearEntries();
        rowTops.clear();
    }

    @Override
    public void replaceEntries(@NotNull Collection<Pool> collection) {
        super.replaceEntries(collection);
        this.updateRowTops();
    }

    public void createCumulatedPool(List<RenderedLootPool> pools) {
        this.clearEntries();
        RenderedCumulatedLootPool cumulated = new RenderedCumulatedLootPool(this, pools, this.functions, playerLuck);
        if (fixedScrollTarget != null && fixedScrollTarget.pool instanceof RenderedLootPool pool) {
            RenderedLootPool.Entry targetEntry = pool.entries.get(fixedScrollTarget.entryId);
            int targetId = cumulated.indexOf(targetEntry);
            fixedScrollTarget = new FixedScrollTarget(cumulated, targetId, fixedScrollTarget.scrollInEntry);
        }
        this.children().add(cumulated);
    }

    public void unpackCumulatedPool(RenderedCumulatedLootPool cumulated) {
        this.clearEntries();
        cumulated.prepareUnpacking();
        this.children().addAll(cumulated.cumulatedPools);
        if (fixedScrollTarget != null && fixedScrollTarget.pool == cumulated) {
            RenderedLootPool.Entry targetEntry = cumulated.cumulatedEntries.get(fixedScrollTarget.entryId).entry();
            for (RenderedLootPool pool : cumulated.cumulatedPools) {
                if (pool.entries.contains(targetEntry)) {
                    fixedScrollTarget = new FixedScrollTarget(pool, pool.entries.indexOf(targetEntry), fixedScrollTarget.scrollInEntry);
                    break;
                }
            }
        }
    }

    @Override
    public int getRowTop(int index) {
        return this.y0 + 4 - (int) this.getScrollAmount() + rowTops.get(index);
    }

    @Override
    public int getRowBottom(int index) {
        return this.y0 + 4 - (int) this.getScrollAmount() + rowTops.get(index + 1);
    }

    public int getItemHeight(int index) {
        return (this.rowTops.get(index + 1) - this.rowTops.get(index));
    }

    @Override
    public int getMaxPosition() {
        if (rowTops.isEmpty()) {
            return randomSequence == null ? functionsTotalHeight : functionsTotalHeight + 20;
        } else {
            int i = functionsTotalHeight;
            i += this.rowTops.get(rowTops.size() - 1);
            if (isEmptyLootTable()) i += 50;
            if (this.randomSequence != null) i += 20;
            return i;
        }
    }

    @Override
    protected void centerScrollOn(Pool entry) {
        this.setScrollAmount(this.rowTops.get((this.children().indexOf(entry)) - (this.y1 - this.y0) / 2));
    }

    public void scroll(int i) {
        this.setScrollAmount(this.getScrollAmount() + (double) i);
    }

    @Nullable
    public Pool getEntryAtY(double y) {
        double scroll = this.getScrollAmount();
        y = y - y0 + scroll;
        int j = rowTops.size();
        for (int i = 0; i < j; i++) {
            if (y < rowTops.get(i)) {
                if (i == 0) return null;
                return this.children().get(i - 1);
            }
        }
        return null;
    }

    public List<RenderedLootPool> getRenderedLootPools() {
        List<RenderedLootPool> pools = new ArrayList<>();
        for (Pool pool : this.children()) {
            if (pool instanceof RenderedLootPool renderedPool) {
                pools.add(renderedPool);
            } else if (pool instanceof RenderedCumulatedLootPool cumulatedLootPool) {
                pools.addAll(cumulatedLootPool.cumulatedPools);
            }
        }
        return pools;
    }

    public boolean showInfoBar() {
        double scroll = this.getScrollAmount();
        int j = rowTops.size();
        for (int i = 1; i < j; i++) {
            if (scroll < rowTops.get(i)) {
                return scroll - rowTops.get(i - 1) > 15 && rowTops.get(i) - scroll > 15;
            }
        }
        return false;
    }

    public void enableScissor(GuiGraphics graphics) {
        graphics.enableScissor(x0, y0, x1, y1);
    }

    public void enableInfoBarScissor(GuiGraphics graphics) {
        graphics.enableScissor(x0, y0 + 10, x1, y1);
    }

    @Override
    protected void ensureVisible(@NotNull Pool entry) {
        int index = this.children().indexOf(entry);
        int i = this.getRowTop(index);
        int j = i - this.y0 - 4 - getItemHeight(index);
        if (j < 0) {
            this.scroll(j);
        }
        int k = this.y1 - i - 2 * getItemHeight(index);
        if (k < 0) {
            this.scroll(-k);
        }

    }

    @Override
    public boolean mouseScrolled(double d1, double d2, double d3) {
        this.setScrollAmount(this.getScrollAmount() - d3 * 20);
        return true;
    }

    @Override
    public Pool getHovered() {
        return this.hovered;
    }

    @Override
    public void toggleDefaultParameters(boolean hidden) {
        prepareRowTopUpdate();
        LifocParent.super.toggleDefaultParameters(hidden);
        this.children().forEach(pool -> pool.toggleDefaultParameters(hidden));
        updateHeight(false);
    }

    public void applyFunctionPreviewEffects(boolean enabled) {
        prepareRowTopUpdate();
        this.functions.forEach((function) -> {function.applyPreviewEffect(this, enabled);});
        this.children().forEach(pool -> pool.applyFunctionPreviewEffects(enabled));
        updateHeight(false);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        graphics.fill(this.getLeft(), this.getTop(), this.getRight(), this.getBottom(), 0xB2000000);
        int i = this.getScrollbarPosition();
        int j = i + 6;
        // isMouseOver(x, y) is invalid
        // this.hovered = this.isMouseOver(x, y) ? this.getEntryAtPosition(x, y) : null;
        int rowLeft = this.getRowLeft();
        int l = this.y0 + 4 - (int) this.getScrollAmount();
        this.enableScissor(graphics);
        this.renderHeader(graphics, rowLeft, l);
        this.renderList(graphics, mouseX, mouseY, partialTick);
        graphics.disableScissor();
        int maxScroll = this.getMaxScroll();
        if (maxScroll > 0) {
            int j2 = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
            j2 = Mth.clamp(j2, 32, this.y1 - this.y0 - 8);
            int k1 = (int)this.getScrollAmount() * (this.y1 - this.y0 - j2) / maxScroll + this.y0;
            if (k1 < this.y0) {
                k1 = this.y0;
            }
            graphics.fill(i, this.y0, j, this.y1, -16777216);
            graphics.fill(i, k1, j, k1 + j2, -8355712);
            graphics.fill(i, k1, j - 1, k1 + j2 - 1, -4144960);
        }
        this.renderDecorations(graphics, mouseX, mouseY);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean mouseClicked(double x, double y, int key) {
        if (!initialized) return false;
        if (x > this.getScrollbarPosition()) {
            return super.mouseClicked(x, y, key);
        } else if (showInfoBar() && y > y0 && y < y0 + 9) {
            return this.clickedOnInfoBar(x, y, key);
        }
        Pool hoveredPool = this.getEntryAtY(y);
        if (hoveredPool != null) {
            return hoveredPool.mouseClicked(x, y, key, this.rowTops.get(this.children().indexOf(hoveredPool)) + y0 + 4 - (int) getScrollAmount(), getRowLeft() - 17);
        }
        int y0 = this.getItemCount() == 0 ? this.getRowTop(0) + 56 : this.getRowTop(this.getItemCount()) + 6;
        int y1;
        if (renderFunctions()) {
            for (RenderedFunction function : this.functions) {
                if (function.isHidden())
                    continue;
                y1 = y0 + function.getHeight();
                if (y1 > y && y > y0)
                    return function.mouseClicked(x, y, key, this.x0, y0);
                y0 = y1;
            }
        }
        return super.mouseClicked(x, y, key);
    }

    @Override
    protected void renderList(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float f) {
        if (this.showInfoBar()) {
            this.renderInfoBar(graphics);
        }
        if (isEmptyLootTable() && (initialized || System.currentTimeMillis() - creationTimeMs > 1000)) {
            graphics.drawCenteredString(Minecraft.getInstance().font, initialized ? Component.translatable("gui.loot_table_wand.loot_table.empty") : Component.translatable("gui.loot_table_wand.loot_table.loading"), width / 2, 45 - (int) getScrollAmount(), 0xFFFFFF);
        } else {
            int rowLeft = this.getRowLeft();
            int rowWidth = this.getRowWidth();
            int itemCount = this.getItemCount();
            for (int i = 0; i < itemCount; ++i) {
                int rowTop = this.getRowTop(i);
                int rowBottom = this.getRowBottom(i);
                int itemHeight = this.getItemHeight(i);
                if (rowBottom >= this.y0 && rowTop <= this.y1) {
                    this.renderItem(graphics, mouseX, mouseY, f, i, rowLeft - 17, rowTop, rowWidth + 17, itemHeight);
                }
            }
        }
        if (renderFunctions()) {
            this.renderFunctionsList(graphics, mouseX, mouseY);
        }
        if (randomSequence != null) {
            this.renderRandomSequence(graphics);
        }

    }

    public boolean isEmptyLootTable() {
        return this.viewMode == LootTableViewMode.RAW_LIST && getItemCount() == 0 || this.viewMode == LootTableViewMode.LIST && (getItemCount() == 1 && this.children().get(0) instanceof RenderedCumulatedLootPool cumulated && cumulated.cumulatedEntries.isEmpty());
    }

    public boolean renderFunctions() {
        return !this.functions.isEmpty() && this.viewMode == LootTableViewMode.RAW_LIST;
    }

    protected void renderFunctionsList(GuiGraphics graphics, int mouseX, int mouseY) {
        if (rowTops.isEmpty()) return;
        int y = isEmptyLootTable() ? this.getRowTop(0) + 56 : this.getRowTop(this.getItemCount()) + 6;
        for (RenderedFunction function : functions) {
            if (function.isHidden())
                continue;
            function.render(graphics, x0, x0 + getRowWidth() + 16, y, 0, false, mouseX, mouseY);
            y += function.getHeight();
        }
    }

    protected void renderRandomSequence(GuiGraphics graphics) {
        int y = isEmptyLootTable() ? this.getRowTop(0) + 50 : this.getRowTop(this.getItemCount());
        if (renderFunctions()) y += functionsTotalHeight + 6;
        Component randomSequenceDescr = Component.translatable("gui.loot_table_wand.loot_table.random_sequence").append(" ").append(Component.literal(randomSequence.toString()).withStyle(ChatFormatting.GRAY));
        graphics.drawString(Minecraft.getInstance().font, randomSequenceDescr, x0 + 4, y, 0xFFFFFF);
    }

    protected void renderInfoBar(GuiGraphics graphics) {
        boolean isRaw = (viewMode == LootTableViewMode.RAW_LIST);
        int i;
        i = ScreenUtils.drawScaledString(graphics, isRaw ? Component.translatable("gui.loot_table_wand.loot_pool.weight") : Component.translatable("gui.loot_table_wand.loot_pool.chance"), x0 + (isRaw ? 17 : 22), y0 + 2, 0xFF888888, 0.5F, true);
        renderSortingArrow(graphics, i, y0 + 2, 0);
        i = ScreenUtils.drawScaledString(graphics, isRaw ? Component.translatable("gui.loot_table_wand.loot_pool.quality") : Component.translatable("gui.loot_table_wand.loot_pool.average_number"), x0 + (isRaw ? 47 : 62), y0 + 2, 0xFF888888, 0.5F, true);
        renderSortingArrow(graphics, i, y0 + 2, 1);
        i = ScreenUtils.drawScaledString(graphics, isRaw ? Component.translatable("gui.loot_table_wand.loot_pool.count") : Component.translatable("gui.loot_table_wand.loot_pool.average_to_find"), x0 + (isRaw ? 82 : 102), y0 + 2, 0xFF888888, 0.5F, true);
        renderSortingArrow(graphics, i, y0 + 2, 2);
        graphics.renderOutline(x0, y0, getRowWidth() + 16, 9, 0xFF888888);
        graphics.disableScissor();
        this.enableInfoBarScissor(graphics);
    }

    protected boolean clickedOnInfoBar(double x, double y, int key) {
        if (key == 0) {
            int x0 = this.x0 + 2;
            int x1;
            for (int i = 0; i <= 2; i++) {
                x1 = x0 + 40;
                if (x > x0 && x < x1) {
                    this.toggleSortingMode(i);
                    return true;
                }
                x0 = x1;
            }

        }
        return false;
    }

    protected void renderSortingArrow(GuiGraphics graphics, int x, int y, int column) {
        if (this.sortingMode.isColumn(column)) {
            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.scale(1.0f/3, 1.0f/3, 1);
            int scaledX = 3 * x + 1;
            int scaledY = 3 * y + 3;
            ResourceLocation texture = this.sortingMode.isMaxAtTop() ? SORTING_MOST : SORTING_LEAST;
            graphics.blit(texture, scaledX, scaledY, 0, 0, 0, 8, 8, 8, 8);
            pose.popPose();
        }
    }

    @Override
    protected void renderSelection(@NotNull GuiGraphics graphics, int i0, int i1, int i2, int i3, int i4) {
    }

    public void acceptPreviewList(ResourceLocation location, int[] previewTimes, ListTag previewItems, boolean isTag) {
        this.children().forEach(child -> child.acceptPreviewList(location, previewTimes, previewItems, isTag));
    }


    @Override
    public List<RenderedFunction> getFunctions() {
        return functions;
    }

    @Override
    public List<RenderedCondition> getConditions() {
        return List.of();
    }

    @Override
    public void updateHeight(boolean ignored) {
        updateRowTops();
        updateFunctionsHeight();
    }

    public float getCurrentLuck() {
        if (!this.children().isEmpty() && this.children().get(0) instanceof RenderedCumulatedLootPool c) {
            return c.luck;
        } else {
            return playerLuck;
        }
    }

    public void setViewMode(LootTableViewMode mode) {
        this.viewMode = mode;
        for (Pool pool : this.children()) {
            pool.setViewMode(mode);
        }
        if (viewMode == LootTableViewMode.RAW_LIST && !children().isEmpty() && children().get(0) instanceof RenderedCumulatedLootPool cumulated) {
            this.unpackCumulatedPool(cumulated);
        } else if (viewMode == LootTableViewMode.LIST) {
            this.playerLuck = minecraft.player == null ? 0F : minecraft.player.getLuck();
            boolean hasCumulatedPool = false;
            List<RenderedLootPool> renderedPools = new ArrayList<>();
            for (Pool pool : this.children()) {
                if (pool instanceof RenderedLootPool renderedPool) {
                    renderedPools.add(renderedPool);
                } else {
                    hasCumulatedPool = true;
                    break;
                }
            }
            if (!hasCumulatedPool) {
                this.createCumulatedPool(renderedPools);
            }
        }
        this.toggleSortingMode(-1);
        this.updateHeight(false);
    }
    public record FixedScrollTarget(Pool pool, int entryId, double scrollInEntry) {

    }

    public void toggleSortingMode(int clickedColumn) {
        if (clickedColumn == -1) {
            this.sortingMode = SortingMode.DEFAULT;
        } else {
            if (this.sortingMode.isColumn(clickedColumn)) {
                if (this.sortingMode.isMaxAtTop()) {
                    this.sortingMode = SortingMode.get(clickedColumn, false);
                } else {
                    this.sortingMode = SortingMode.DEFAULT;
                }
            } else {
                this.sortingMode = SortingMode.get(clickedColumn, true);
            }
        }
        this.children().forEach(Pool::sortEntries);
    }
    public static abstract class Pool extends ObjectSelectionList.Entry<Pool> {

        protected final RenderedLootTable table;

        protected Pool(RenderedLootTable table) {
            this.table = table;
        }

        public SortingMode getSortingMode() {
            return table.sortingMode;
        }
        public abstract void sortEntries();
        public abstract int getHeight();
        public abstract void acceptPreviewList(ResourceLocation location, int[] previewTimes, ListTag previewItems, boolean isTag);
        public abstract void setViewMode(LootTableViewMode mode);
        public abstract boolean mouseClicked(double x, double y, int key, int renderTop, int renderLeft);
        public abstract void applyFunctionPreviewEffects(boolean enabled);
        public abstract void toggleDefaultParameters(boolean hidden);
        public abstract int getEntryStartY(int targetEntry);
        public abstract Pair<Integer, Double> getFirstShownEntry(double heightIntoPool);

    }

    public enum SortingMode {
        DEFAULT(-1, true, (e) -> 0.0, (e) -> 0.0),
        HIGHEST_CHANCE(0, true, RenderedLootPool.Entry::getChance, RenderedLootPool.Entry::getWeight),
        LOWEST_CHANCE(0, false, RenderedLootPool.Entry::getChance, RenderedLootPool.Entry::getWeight),
        HIGHEST_AVERAGE(1, true, RenderedLootPool.Entry::getAveragePerTry, RenderedLootPool.Entry::getQuality),
        LOWEST_AVERAGE(1, false, RenderedLootPool.Entry::getAveragePerTry, RenderedLootPool.Entry::getQuality),
        MOST_TRIES_TO_GET(2, true, RenderedLootPool.Entry::getAverageTriesToGet, RenderedLootPool.Entry::getAverageCount),
        LEAST_TRIES_TO_GET(2, false, RenderedLootPool.Entry::getAverageTriesToGet, RenderedLootPool.Entry::getAverageCount);
        final int column;
        final boolean maxAtTop;
        final Comparator<RenderedLootPool.Entry> comparator;
        final Comparator<RenderedCumulatedLootPool.Entry> cumulatedComparator;
        final Comparator<RenderedLootPool.Entry> rawDataComparator;
        SortingMode(int column, boolean maxAtTop, ToDoubleFunction<RenderedLootPool.Entry> sortedValue, ToDoubleFunction<RenderedLootPool.Entry> rawSortedValue) {
            this.column = column;
            this.maxAtTop = maxAtTop;
            this.comparator = maxAtTop ? Comparator.comparingDouble(sortedValue).reversed() : Comparator.comparingDouble(sortedValue);
            this.cumulatedComparator = (c1, c2) -> comparator.compare(c1.entry(), c2.entry());
            this.rawDataComparator = maxAtTop ? Comparator.comparingDouble(rawSortedValue).reversed() : Comparator.comparingDouble(rawSortedValue);
        }
        public boolean isColumn(int column) {
            return this.column == column;
        }
        public boolean isMaxAtTop() {
            return this.maxAtTop;
        }
        public static SortingMode get(int column, boolean maxAtTop) {
            for (SortingMode mode : SortingMode.values()) {
                if (mode.isColumn(column) && mode.isMaxAtTop() == maxAtTop) {
                    return mode;
                }
            }
            return DEFAULT;
        }
        public void sortEntries(List<RenderedLootPool.Entry> entries) {
            entries.sort(comparator);
        }
        public void sortCumulatedEntries(List<RenderedCumulatedLootPool.Entry> entries) {
            entries.sort(cumulatedComparator);
        }
        public void sortEntriesByRawData(List<RenderedLootPool.Entry> entries) {
            entries.sort(rawDataComparator);
        }
    }
}
