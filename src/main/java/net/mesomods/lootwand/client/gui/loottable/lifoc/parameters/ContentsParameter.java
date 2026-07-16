package net.mesomods.lootwand.client.gui.loottable.lifoc.parameters;

import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.client.gui.loottable.RenderedLootPool;
import net.mesomods.lootwand.client.gui.loottable.poolentry.RenderedItemEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ContentsParameter extends Parameter<List<? extends RenderedLootPool.Entry>> {
    boolean showCounts = false;
    public ContentsParameter() {
        super(null, UnaryOperator.identity() , new Description<>((list) -> Component.empty()));
    }
    protected ContentsParameter(@Nullable List<? extends RenderedLootPool.Entry> defaultValue, UnaryOperator<List<? extends RenderedLootPool.Entry>> validator, Description<List<? extends RenderedLootPool.Entry>> description, Supplier<List<? extends RenderedLootPool.Entry>> supplier, Consumer<List<? extends RenderedLootPool.Entry>> valueSaver) {
        super(defaultValue, validator, description, supplier, valueSaver);
        this.value.forEach(entry -> entry.setHeightUpdater(this.heightUpdater));
        updateCountNeed();
    }

    @Override
    public int getHeight() {
        int totalHeight = 2;
        for (RenderedLootPool.Entry entry : this.value) {
            totalHeight += entry.getHeight();
        }
        return totalHeight;
    }

    @Override
    public int getWidth() {
        return -1;
    }

    @Override
    public Parameter<List<? extends RenderedLootPool.Entry>> build(Supplier<List<? extends RenderedLootPool.Entry>> supplier, Consumer<List<? extends RenderedLootPool.Entry>> valueSaver) {
        return new ContentsParameter(defaultValue, validator, description, supplier, valueSaver);
    }

    @Override
    public void onDescriptionUpdate() {
    }

    public void updateCountNeed() {
        showCounts = false;
        for (RenderedLootPool.Entry entry : this.value) {
            if (entry instanceof RenderedItemEntry) {
                showCounts = true;
                return;
            }
        }
    }

    public void acceptPreviewList(ResourceLocation location, int[] previewTimes, ListTag previewItems, boolean isTag) {
        if (value == null) return;
        for (RenderedLootPool.Entry entry : this.value) {
            entry.acceptPreviewList(location, previewTimes, previewItems, isTag);
        }
    }

    public void toggleDefaultParameters(boolean hidden) {
        if (value == null) return;
        for (RenderedLootPool.Entry entry : this.value) {
            entry.toggleDefaultParameters(hidden);
        }
        updateCountNeed();
        this.updateLifocHeight();
    }

    public void applyFunctionPreviewEffects(boolean enabled) {
        if (value == null) return;
        for (RenderedLootPool.Entry entry : this.value) {
            entry.applyFunctionPreviewEffects(enabled);
        }
        updateCountNeed();
        this.updateLifocHeight();
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int x1, int y, int transparency, int mouseX, int mouseY) {
        if (value == null) return;
        if (showCounts) ScreenUtils.drawScaledString(graphics, Component.translatable("gui.loot_table_wand.loot_pool.count"), x + 27, y - 5, ScreenUtils.getOutlineColor(transparency - 1), 0.5F, true);
        for (RenderedLootPool.Entry lootPoolEntry : this.value) {
              lootPoolEntry.render(graphics, y, x, x, x1 - x + 1, transparency, mouseX, mouseY, List.of(), List.of());
              y += lootPoolEntry.getHeight();
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int key, int renderLeft, int renderTop) {
        int y0 = renderTop;
        int y1;
        if (value == null) return false;
        for (RenderedLootPool.Entry entry : this.value) {
            y1 = y0 + entry.getHeight();
            if (y0 < y && y < y1 && renderLeft < x) {
                return entry.mouseClicked(x, y, key, renderLeft, y0);
            }
            y0 = y1;
        }
        return false;
    }

    @Override
    public @NotNull Component getComponent() {
        return null;
    }
}
