package net.mesomods.lootwand.loot.lifoc.parameters;

import net.mesomods.lootwand.loot.lifoc.RenderedFunction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class NestedFunctionParameter extends RenderableParameter {
    final LootItemFunction rawFunction;
    final RenderedFunction function;

    public NestedFunctionParameter() {
        rawFunction = null;
        function = RenderedFunction.doesNothing();
    }

    public NestedFunctionParameter(LootItemFunction rawFunction, RenderedFunction function) {
        this.rawFunction = rawFunction;
        this.function = function;
    }

    @Override
    public int getHeight() {
        return function.getHeight();
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
        function.toggleDefaultParameters(hidden);
        this.updateLifocHeight();
    }

    public void applyFunctionPreviewEffects(boolean enabled) {
        function.applyFunctionPreviewEffects(enabled);
        this.updateLifocHeight();
    }

    public void acceptPreviewList(ResourceLocation location, int[] previewTimes, ListTag previewItems, boolean isTag) {
        function.acceptPreviewList(location, previewTimes, previewItems, isTag);
    }

    public NestedFunctionParameter build(LootItemFunction rawFunction, Consumer<LootItemFunction> consumer) {
        RenderedFunction function = RenderedFunction.fromVanilla(rawFunction);
        if (function == null) {
            function = RenderedFunction.doesNothing();
        }
        function.disableOutline();
        function.setHeightUpdater(() -> {
            consumer.accept(rawFunction);
            this.updateLifocHeight();
        });
        return new NestedFunctionParameter(rawFunction, function);
    }

    @Override
    public void render(GuiGraphics graphics, int x, int x1, int y, int transparency, int mouseX, int mouseY) {
        function.render(graphics, x, x1, y, transparency, false, mouseX, mouseY);
    }

    @Override
    public void mergeDescriptions(DescriptionMerger<?> merger, Component component) {
    }

    @Override
    public @NotNull Component getComponent() {
        return null;
    }
}
