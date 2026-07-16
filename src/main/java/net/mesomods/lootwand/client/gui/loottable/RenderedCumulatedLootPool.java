package net.mesomods.lootwand.client.gui.loottable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.client.gui.LootTableViewMode;
import net.mesomods.lootwand.client.gui.loottable.lifoc.RenderedCondition;
import net.mesomods.lootwand.client.gui.loottable.lifoc.RenderedFunction;
import net.mesomods.lootwand.client.gui.loottable.poolentry.RenderedCompositeEntry;
import net.mesomods.lootwand.client.gui.screen.LootTableDataScreen;
import net.mesomods.lootwand.client.tooltip.AdvancedTooltipScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class RenderedCumulatedLootPool extends RenderedLootTable.Pool {
    public static final Tooltip CONDITION_WARNING = Tooltip.create(Component.translatable("gui.loot_table_wand.loot_table.tooltip.condition_warning"));
    public static final ResourceLocation WARNING_ICON = ResourceLocation.parse("forge:textures/gui/experimental_warning.png");
    protected LuckSlider luckSlider;
    protected List<RenderedLootPool> cumulatedPools = new ArrayList<>();
    protected List<Entry> cumulatedEntries = new ArrayList<>();
    protected final List<Entry> cumulatedEntriesOriginalOrder;
    protected float luck;
    protected final boolean lootDepends;

    public RenderedCumulatedLootPool(RenderedLootTable table, List<RenderedLootPool> pools, List<RenderedFunction> tableFunctions, float defaultLuck) {
        super(table);
        for (RenderedLootPool pool : pools) {
            this.cumulatedPools.add(pool);
            List<RenderedCondition> poolConditions = pool.getConditions();
            List<RenderedFunction> poolFunctions = new ArrayList<>(tableFunctions);
            poolFunctions.addAll(pool.getFunctions());
            for (RenderedLootPool.Entry entry : pool.getEntries()) {
                cumulatedEntries.add(new Entry(entry, entry instanceof RenderedCompositeEntry, poolConditions, poolFunctions));
            }
        }
        this.cumulatedEntriesOriginalOrder = new ArrayList<>(this.cumulatedEntries);
        this.luck = defaultLuck;
        this.luckSlider = new LuckSlider(0, 0, 260, 10);
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof LootTableDataScreen d) {
            d.addLuckSlider(this.luckSlider);
        }
        boolean lootDepends = false;
        for (Entry entry : cumulatedEntries) {
            if (entry.entry.isConditional()) {
                lootDepends = true;
                break;
            }
        }
        this.lootDepends = lootDepends;
    }

    public void sortEntries() {
        this.cumulatedEntries.forEach((e) -> {
            RenderedLootPool.Entry entry = e.entry();
            if (entry instanceof RenderedCompositeEntry composite) {
                composite.sortChildren(this.getSortingMode(), false);
            }
        });
        if (this.getSortingMode() == RenderedLootTable.SortingMode.DEFAULT) {
            this.cumulatedEntries.clear();
            this.cumulatedEntries.addAll(this.cumulatedEntriesOriginalOrder);
        } else {
            this.getSortingMode().sortCumulatedEntries(cumulatedEntries);
        }
    }

    public void renderTopBar(GuiGraphics graphics, int top, int left, int width, int height, int mouseX, int mouseY) {
        Font FONT = Minecraft.getInstance().font;
        graphics.drawString(FONT, Component.translatable("gui.loot_table_wand.loot_table.luck", ScreenUtils.DOUBLE_FORMAT.format(luck)), left + 5, top + 2, 0xFFFFFF);
        this.positionLuckSlider(left + 75, top, width - 100);
        if (lootDepends) {
            graphics.blit(WARNING_ICON, left + width - 20, top - 2, 0, 8, 9, 16, 14, 32, 32);
            if (mouseX >= left + width - 20 && mouseX < left + width - 4 && mouseY >= top - 2 && mouseY < top + 12) {
                Screen screen = Minecraft.getInstance().screen;
                if (screen instanceof AdvancedTooltipScreen s) s.lootmod$setNoGapTooltipForNextRenderPass(CONDITION_WARNING, LootTableDataScreen.TOOLTIP_POSITIONER);
            }
        }
        graphics.renderOutline(left, top + 13, width - 1, 9, 0xFF888888);
        int i;
        i = ScreenUtils.drawScaledString(graphics, Component.translatable("gui.loot_table_wand.loot_pool.chance"), left + 22, top + 15, 0xFF888888, 0.5F, true);
        table.renderSortingArrow(graphics, i, top + 15, 0);
        i = ScreenUtils.drawScaledString(graphics, Component.translatable("gui.loot_table_wand.loot_pool.average_number"), left + 62, top + 15, 0xFF888888, 0.5F, true);
        table.renderSortingArrow(graphics, i, top + 15, 1);
        i = ScreenUtils.drawScaledString(graphics, Component.translatable("gui.loot_table_wand.loot_pool.average_to_find"), left + 102, top + 15, 0xFF888888, 0.5F, true);
        table.renderSortingArrow(graphics, i, top + 15, 2);
    }

    public void render(@NotNull GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovered, float partialTicks) {
        this.renderTopBar(graphics, top, left, width, height, mouseX, mouseY);
        graphics.renderOutline(left, top - 4, width - 1, height - 2, 0xFF888888);
        int renderTop = top + 25;
        for (Entry entry : cumulatedEntries) {
            if (entry.isHidden()) continue;
            entry.entry.render(graphics, renderTop, left, left, width, 0, mouseX, mouseY, entry.conditions, entry.functions);
            renderTop += entry.getHeight();
        }
    }

    public void positionLuckSlider(int x, int y, int width) {
        this.luckSlider.setPosition(x, y);
        this.luckSlider.setWidth(width);
    }

    public int getHeight() {
        int i = 28;
        for (Entry entry : cumulatedEntries) {
            if (entry.isHidden()) continue;
            i += entry.getHeight();
        }
        return i;
    }

    public void setLuck(float luck) {
        this.luck = luck;
        this.cumulatedPools.forEach(pool -> pool.setLuck(luck));
    }

    public void prepareUnpacking() {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof LootTableDataScreen d) {
            d.removeLuckSlider(this.luckSlider);
        }
    }

    @Override
    public void acceptPreviewList(ResourceLocation location, int[] previewTimes, ListTag previewItems, boolean isTag) {
        this.cumulatedPools.forEach(entry -> entry.acceptPreviewList(location, previewTimes, previewItems, isTag));
    }

    @Override
    public void setViewMode(LootTableViewMode mode) {
        this.cumulatedPools.forEach(entry -> entry.setViewMode(mode));
    }

    @Override
    public boolean mouseClicked(double x, double y, int key, int renderTop, int renderLeft) {
        if (y > renderTop + 15 && y < renderTop + 25) return table.clickedOnInfoBar(x, y, key);
        int y0 = renderTop + 25;
        int y1;
        for (Entry entry : this.cumulatedEntries) {
            if (entry.isHidden()) continue;
            y1 = y0 + entry.getHeight();
            if (y1 > y && y > y0)
                return entry.mouseClicked(x, y, key, renderLeft, y0);
            y0 = y1;
        }
        return false;
    }

    @Override
    public void applyFunctionPreviewEffects(boolean enabled) {
        this.cumulatedPools.forEach(entry -> entry.applyFunctionPreviewEffects(enabled));
    }

    @Override
    public void toggleDefaultParameters(boolean hidden) {
        this.cumulatedPools.forEach(entry -> entry.toggleDefaultParameters(hidden));
    }

    @Override
    public int getEntryStartY(int targetEntry) {
        if (targetEntry == -1) {
            return 0;
        } else {
            int totalHeight = 25;
            for (int i = 0; i < this.cumulatedEntries.size(); i++) {
                Entry entry = this.cumulatedEntries.get(i);
                if (entry.isHidden()) continue;
                if (i == targetEntry) {
                    return totalHeight;
                }
                totalHeight += entry.getHeight();
            }
            return totalHeight;
        }
    }

    @Override
    public Pair<Integer, Double> getFirstShownEntry(double heightIntoPool) {
        if (heightIntoPool < 0) {
            return new Pair<>(-1, heightIntoPool);
        }
        int totalHeight = 25;
        for (int i = 0; i < this.cumulatedEntries.size(); i++) {
            Entry entry = this.cumulatedEntries.get(i);
            if (heightIntoPool < totalHeight) {
                if (entry.isHidden()) continue;
                return new Pair<>(i, heightIntoPool - totalHeight);
            }
            totalHeight += entry.getHeight();
        }
        if (heightIntoPool < totalHeight) {
            return new Pair<>(-2, heightIntoPool - totalHeight);
        }
        return null;
    }

    public int indexOf(RenderedLootPool.Entry entry) {
        for (Entry e : cumulatedEntries) {
            if (e.entry == entry) {
                return cumulatedEntries.indexOf(e);
            }
        }
        return -1;
    }

    @Override
    public Component getNarration() {
        return Component.empty();
    }

    public record Entry(RenderedLootPool.Entry entry, boolean isComposite, List<RenderedCondition> conditions, List<RenderedFunction> functions) {

        public int getHeight() {
            return entry.getHeight(conditions, functions);
        }

        public boolean isHidden() {
            return entry.isNothing();
        }

        public boolean mouseClicked(double x, double y, int key, int renderLeft, int y0) {
            return entry.mouseClicked(x, y, key, renderLeft, y0, conditions, functions);
        }
    }

    public class LuckSlider extends AbstractSliderButton {
        public static float STEP_SIZE = 1.0f/560;
        float luck;

        public LuckSlider(int x, int y, int width, int height) {
            super(x, y, width, height, Component.empty(), calculateLuckReversed(RenderedCumulatedLootPool.this.luck));
        }

        public static float calculateLuck(double value) {
            double v = Math.abs(value - 0.5);
            byte s = (byte) (value >= 0.5 ? 1 : -1);
            if (v <= 100 * STEP_SIZE) {
                return s * (float) ((v / STEP_SIZE) / 100);
            } else if (v <= 189 * STEP_SIZE) {
                return s * (float) ((v / STEP_SIZE - 100) / 10 + 1);
            } else {
                return s * (float) ((v / STEP_SIZE - 190) + 10);
            }
        }

        public static float calculateLuckReversed(float luck) {
            float l = Math.abs(luck);
            UnaryOperator<Float> op = luck >= 0 ? (f) -> 0.5f + f : (f) -> 0.5f - f;
            if (l <= 1) {
                return op.apply(l * 100 * STEP_SIZE);
            } else if (l <= 10) {
                return op.apply(((l - 1) * 10 + 100) * STEP_SIZE);
            } else {
                return op.apply((l - 10 + 190) * STEP_SIZE);
            }
        }

        public void updateLuck() {
            double value = this.value;
            this.luck = calculateLuck(value);
        }

        @Override
        public void applyValue() {
            this.value = Math.round(this.value / STEP_SIZE) * STEP_SIZE;
            this.updateLuck();
            RenderedCumulatedLootPool.this.setLuck(luck);
        }

        @Override
        public boolean keyPressed(int key, int x, int y) {
            if (key == 262) {
                this.value += STEP_SIZE;
                this.onValueChange();
                return true;
            } else if (key == 263) {
                this.value -= STEP_SIZE;
                this.onValueChange();
                return true;
            }
            return false;
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float ignored) {
            RenderedCumulatedLootPool.this.table.enableScissor(graphics);
            Minecraft minecraft = Minecraft.getInstance();
            graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            graphics.blitNineSliced(SLIDER_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
            graphics.blitNineSliced(SLIDER_LOCATION, this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 8, 10, 20, 4, 200, 20, 0, this.getHandleTextureY());
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            int i = this.active ? 16777215 : 10526880;
            this.renderScrollingString(graphics, minecraft.font, 2, i | Mth.ceil(this.alpha * 255.0F) << 24);
            graphics.disableScissor();
        }

        @Override
        protected void updateMessage() {
        }

        // should mirror updating in private method setValue(double)
        public void onValueChange() {
            this.value = Mth.clamp(this.value, 0.0F, 1.0F);
            this.applyValue();
            this.updateMessage();
        }
    }
}
