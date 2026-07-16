package net.mesomods.lootwand.client.gui.loottable.lifoc.parameters;

import net.mesomods.lootwand.client.gui.loottable.lifoc.RenderedCondition;
import net.mesomods.lootwand.client.gui.loottable.lifoc.RenderedLIFOC;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class NestedConditionParameter extends RenderableParameter {
    final LootItemCondition rawCondition;
    final RenderedCondition condition;
    final boolean isWholeCondition;

    public NestedConditionParameter(boolean isWholeCondition) {
        this.isWholeCondition = isWholeCondition;
        rawCondition = null;
        condition = RenderedCondition.alwaysTrue();
    }

    public NestedConditionParameter(LootItemCondition rawCondition, RenderedCondition condition, boolean isWholeCondition) {
        this.isWholeCondition = isWholeCondition;
        this.rawCondition = rawCondition;
        this.condition = condition;
    }

    @Override
    public int getHeight() {
        return condition.getHeight();
    }

    @Override
    public int getWidth() {
        return -1;
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    public void toggleDefaultParameters(boolean hidden) {
        condition.toggleDefaultParameters(hidden);
        this.updateLifocHeight();
    }

    public void applyFunctionPreviewEffects(boolean enabled) {
        condition.applyFunctionPreviewEffects(enabled);
        this.updateLifocHeight();
    }

    public void acceptPreviewList(ResourceLocation location, int[] previewTimes, ListTag previewItems, boolean isTag) {
        condition.acceptPreviewList(location, previewTimes, previewItems, isTag);
    }

    public NestedConditionParameter build(LootItemCondition rawCondition, Consumer<LootItemCondition> consumer, boolean inverted) {
        RenderedCondition condition = RenderedCondition.fromVanilla(rawCondition);
        if (condition == null) {
            condition = RenderedCondition.alwaysTrue();
        }
        if (inverted) condition.invert();
        condition.disableOutline();
        condition.setHeightUpdater(() -> {
            consumer.accept(rawCondition);
            this.updateLifocHeight();
        });
        return new NestedConditionParameter(rawCondition, condition, isWholeCondition);
    }

    public void invertCondition() {
        this.condition.invert();
    }

    @Override
    public void render(GuiGraphics graphics, int x, int x1, int y, int transparency, int mouseX, int mouseY) {
        if (isWholeCondition) {
            y -= 1;
            x -= RenderedLIFOC.X_SHIFT + RenderedCondition.PREFIX_WIDTH;
        }
        condition.render(graphics, x, x1, y, transparency, false, mouseX, mouseY);
    }

    @Override
    public void mergeDescriptions(DescriptionMerger<?> merger, Component component) {
    }

    @Override
    public @NotNull Component getComponent() {
        return null;
    }
}
