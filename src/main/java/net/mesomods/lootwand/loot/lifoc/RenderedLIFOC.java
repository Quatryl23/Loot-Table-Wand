package net.mesomods.lootwand.loot.lifoc;

import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.client.tooltip.AdvancedTooltipScreen;
import net.mesomods.lootwand.loot.Clickable;
import net.mesomods.lootwand.loot.RenderedLootPool;
import net.mesomods.lootwand.loot.RenderedLootTable;
import net.mesomods.lootwand.loot.lifoc.parameters.*;
import net.mesomods.lootwand.loot.numbers.ConstantNumberProvider;
import net.mesomods.lootwand.loot.numbers.NumberProvider;
import net.mesomods.lootwand.loot.numbers.NumberProviderSum;
import net.mesomods.lootwand.loot.poolentry.RenderedItemEntry;
import net.mesomods.lootwand.loot.poolentry.RenderedTagEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class RenderedLIFOC implements ParameterNest, Clickable {
    public static final int X_SHIFT = 6;
    protected final PreviewEffect previewEffect;
    protected RenderableParameter descriptionParameter;
    protected final boolean overrideDescription;
    protected Component description;
    protected Tooltip tooltip;
    protected int descriptionWidth;
    protected final List<RenderableParameter> parameters;
    protected boolean renderOutline;
    protected final boolean hiddenByDefault;
    protected boolean hiddenThroughDefault;
    protected boolean hiddenThroughPreviewEffect = false;
    protected boolean hidden;
    protected int height;
    protected Runnable heightUpdater = () -> {};

    public RenderedLIFOC(PreviewEffect previewEffect, Component description, @Nullable RenderableParameter descriptionParameter, List<RenderableParameter> parameters, boolean renderOutline, boolean hiddenByDefault, boolean hasConditions) {
        this.description = description;
        updateDescription();
        this.previewEffect = previewEffect;
        this.renderOutline = renderOutline;
        this.descriptionParameter = descriptionParameter;
        this.overrideDescription = false;
        this.parameters = parameters;
        this.hiddenByDefault = !hasConditions && hiddenByDefault;
        this.hiddenThroughDefault = hiddenByDefault;
        this.updateHideState();
    }

    public void updateDescription() {
        this.descriptionWidth = description == null ? 0 : ScreenUtils.FONT.width(description);
        if (description == null) {
            this.tooltip = null;
        } else {
            Component descriptionWithoutPrefix = Component.literal(description.getString().substring(getDescriptionPrefix().getString().length()));
            this.tooltip = Tooltip.create(descriptionWithoutPrefix);
        }
    }

    public abstract void updateHeight(boolean updateParent);

    public void updateParameterHeight(boolean updateParent) {
        this.height = ScreenUtils.TEXT_HEIGHT + (renderOutline ? 4 : 1);
        if (descriptionParameter != null && descriptionParameter.getHeight() != ScreenUtils.TEXT_HEIGHT) {
            this.height += descriptionParameter.getHeight() - ScreenUtils.TEXT_HEIGHT;
        }
        if (hasVisibleParameters() && !(getFirstVisibleParameter() instanceof ListParameter)) this.height += 2;
        for (RenderableParameter parameter : this.parameters) {
            if (!parameter.isHidden()) {
                this.height += parameter.getHeight();
            }
        }
        if (updateParent)
            heightUpdater.run();

    }

    public void disableOutline() {
        this.renderOutline = false;
        this.updateParameterHeight(false);
    }

    @Override
    public boolean mouseClicked(final double x, final double y, int key, int renderLeft, int renderTop) {
        int y0 = renderTop + (renderOutline ? 4 : 0);
        int x0 = renderLeft + X_SHIFT;
        if (descriptionParameter != null) {
            x0 += ScreenUtils.FONT.width(overrideDescription ? getDescriptionPrefix() : description);
            if (y0 < y && y < y0 + descriptionParameter.getHeight() && x0 < x && (x < x0 + descriptionParameter.getWidth() || descriptionParameter.getWidth() == -1)) {
                return descriptionParameter.mouseClicked(x, y, key, renderLeft, renderTop);
            }
            y0 += descriptionParameter.getHeight();
        } else {
            y0 += ScreenUtils.TEXT_HEIGHT;
        }
        x0 = renderLeft + X_SHIFT + ScreenUtils.FONT.width(getDescriptionPrefix()) + 1;
        y0 += 4;
        if (!hasVisibleParameters() && hasConditions() || getFirstVisibleParameter() instanceof ListParameter)
            y0 -= 2;
        int y1;
        for (RenderableParameter parameter : this.parameters) {
            if (parameter.isHidden()) continue;
            y1 = y0 + parameter.getHeight();
            if (y0 < y && y < y1 && x0 < x && (x < x0 + parameter.getWidth() || parameter.getWidth() == -1)) {
                return parameter.mouseClicked(x, y, key, x0, y0);
            }
            y0 = y1;
        }
        return this.mouseClickedAdditional(x, y, key, x0, y0);
    }

    public abstract boolean mouseClickedAdditional(double x, double y, int key, int renderLeft, int renderTop);

    public abstract Component getDescriptionPrefix();

    public abstract boolean hasConditions();

    public abstract void render(GuiGraphics graphics, int x0, int x1, int y, int transparency, boolean skipUpperOutlinePart, int mouseX, int mouseY);

    public int renderParameters(GuiGraphics graphics, int x0, int x1, int y, int transparency, boolean skipUpperOutlinePart, int mouseX, int mouseY) {
        if (renderOutline) {
            ScreenUtils.renderOutline(graphics, x0, y, x1 - x0, height + 1, transparency, skipUpperOutlinePart);
            y += 4;
        }
        int x = x0 + X_SHIFT;
        if (descriptionParameter != null) {
            if (!overrideDescription) {
                graphics.drawString(ScreenUtils.FONT, description, x, y + 1, 0xFFFFFF);
                renderTooltip(x0, x1, y, y + ScreenUtils.TEXT_HEIGHT, mouseX, mouseY);
                x += ScreenUtils.FONT.width(description);
            } else {
                graphics.drawString(ScreenUtils.FONT, getDescriptionPrefix(), x, y + 1, 0xFFFFFF);
                x += ScreenUtils.FONT.width(getDescriptionPrefix());
            }
            descriptionParameter.render(graphics, x, x1, y + 1, transparency + 1, mouseX, mouseY);
            y += descriptionParameter.getHeight();
        } else {
            graphics.drawString(ScreenUtils.FONT, description, x, y + 1, 0xFFFFFF);
            renderTooltip(x0, x1, y, y + ScreenUtils.TEXT_HEIGHT, mouseX, mouseY);
            y += ScreenUtils.TEXT_HEIGHT;
        }
        x = x0 + X_SHIFT + ScreenUtils.FONT.width(getDescriptionPrefix());
        if (!hasVisibleParameters() && hasConditions() || getFirstVisibleParameter() instanceof ListParameter)
            y -= 2;
        for (RenderableParameter parameter : parameters) {
            if (parameter.isHidden())
                continue;
            parameter.render(graphics, x + 1, x1, y + 4, transparency + 1, mouseX, mouseY);
            y += parameter.getHeight();
        }
        return y;
    }

    protected void renderTooltip(int x0, int x1, int y0, int y1, int mouseX, int mouseY) {
        if ((x1 - x0) < descriptionWidth && mouseX > x0 && mouseY > y0 && mouseY < y1) {
            Screen screen = Minecraft.getInstance().screen;
            if (tooltip != null && screen instanceof AdvancedTooltipScreen s)
                s.lootmod$setNoGapTooltipForNextRenderPass(tooltip, DefaultTooltipPositioner.INSTANCE);
        }
    }

    protected RenderableParameter getFirstVisibleParameter() {
        for (RenderableParameter parameter : this.parameters) {
            if (!parameter.isHidden()) {
                return parameter;
            }
        }
        return null;
    }

    public boolean hasVisibleParameters() {
        for (RenderableParameter parameter : this.parameters) {
            if (!parameter.isHidden()) {
                return true;
            }
        }
        return false;
    }

    public void setHeightUpdater(Runnable heightUpdater) {
        this.heightUpdater = heightUpdater;
    }

    public List<RenderableParameter> getAllParameters() {
        return new ArrayList<>(this.parameters);
    }

    @Override
    public void toggleDefaultParameters(boolean hidden) {
        if (hiddenByDefault) {
            this.hiddenThroughDefault = hidden;
            this.updateHideState();
        }
        ParameterNest.super.toggleDefaultParameters(hidden);
    }

    @Override
    public List<RenderableParameter> getNestedParameters() {
        return getAllParameters();
    }

    @Override
    public List<RenderableParameter> getNestedHideableParameters() {
        return this.parameters;
    }

    public void applyPreviewEffect(RenderedLootTable table, boolean enabled) {
        if (!hasConditions()) this.hiddenThroughPreviewEffect = this.previewEffect.apply(table, enabled);
        this.updateHideState();
    }

    public void applyPreviewEffect(RenderedLootPool pool, boolean enabled) {
        if (!hasConditions()) this.hiddenThroughPreviewEffect = this.previewEffect.apply(pool, enabled);
        this.updateHideState();
    }

    public void applyPreviewEffect(RenderedLootPool.Entry entry, boolean enabled) {
        if (!hasConditions()) this.hiddenThroughPreviewEffect = this.previewEffect.apply(entry, enabled);
        this.updateHideState();
    }

    public void acceptPreviewList(ResourceLocation location, int[] previewTimes, ListTag previewItems, boolean isTag) {
        getAllParameters().forEach(param -> {
            if (param instanceof ContentsParameter c) {
                c.acceptPreviewList(location, previewTimes, previewItems, isTag);
            } else if (param instanceof NestedConditionParameter c) {
                c.acceptPreviewList(location, previewTimes, previewItems, isTag);
            } else if (param instanceof NestedFunctionParameter f) {
                f.acceptPreviewList(location, previewTimes, previewItems, isTag);
            }
        });
    }

    protected void updateHideState() {
        this.hidden = hiddenThroughDefault || hiddenThroughPreviewEffect;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public int getHeight() {
        return this.height;
    }

    public static abstract class PreviewEffect {
        public static PreviewEffect NONE = new NoPreviewEffect();

        abstract boolean apply(RenderedLootTable table, boolean enabled);

        abstract boolean apply(RenderedLootPool pool, boolean enabled);

        abstract boolean apply(RenderedLootPool.Entry entry, boolean enabled);
    }

    public static class NoPreviewEffect extends PreviewEffect {
        @Override
        boolean apply(RenderedLootTable table, boolean enabled) {
            return false;
        }

        @Override
        boolean apply(RenderedLootPool pool, boolean enabled) {
            return false;
        }

        @Override
        boolean apply(RenderedLootPool.Entry entry, boolean enabled) {
            return false;
        }
    }

    public static class SetCountPreviewEffect extends PreviewEffect {
        final BooleanParameter add;
        final NumberProviderParameter count;

        public SetCountPreviewEffect(BooleanParameter add, NumberProviderParameter count) {
            this.add = add;
            this.count = count;
        }

        protected NumberProvider apply(NumberProvider oldCount, boolean enabled) {
            return enabled ? (add.getValue() ? new NumberProviderSum(this.count.getValue(), oldCount) : this.count.getValue()) : new ConstantNumberProvider(1);
        }


        @Override
        boolean apply(RenderedLootTable table, boolean enabled) {
            for (RenderedLootPool pool : table.getRenderedLootPools()) {
                apply(pool, enabled);
            }
            return enabled;
        }

        @Override
        boolean apply(RenderedLootPool pool, boolean enabled) {
            for (RenderedLootPool.Entry entry : pool.getEntries()) {
                apply(entry, enabled);
            }
            return enabled;
        }

        @Override
        boolean apply(RenderedLootPool.Entry entry, boolean enabled) {
            if (entry instanceof RenderedItemEntry itemEntry) {
                itemEntry.count = apply(itemEntry.count, enabled);
            } else if (entry instanceof RenderedTagEntry tagEntry) {
                tagEntry.count = apply(tagEntry.count, enabled);
            }
            return enabled;
        }
    }
}
