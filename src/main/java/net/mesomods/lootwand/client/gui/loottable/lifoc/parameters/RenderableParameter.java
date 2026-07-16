package net.mesomods.lootwand.client.gui.loottable.lifoc.parameters;

import net.mesomods.lootwand.client.gui.loottable.Clickable;
import net.mesomods.lootwand.client.tooltip.AdvancedTooltipScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public abstract class RenderableParameter implements Clickable {
    boolean hidden = false;
    Runnable heightUpdater = () -> {
    };
    Tooltip tooltip;

    public abstract int getHeight();

    public abstract int getWidth();

    public abstract boolean isDefault();

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setHeightUpdater(Runnable heightUpdater) {
        this.heightUpdater = heightUpdater;
    }

    public void updateLifocHeight() {
        this.heightUpdater.run();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int key, int renderLeft, int renderTop) {
        return false;
    }

    public abstract void render(GuiGraphics graphics, int x, int x1, int y, int transparency, int mouseX, int mouseY);

    public abstract void mergeDescriptions(DescriptionMerger<?> merger, Component component);

    public boolean isHidden() {
        return hidden;
    }

    @Nonnull
    public abstract Component getComponent();

    public void renderTooltip(int textWidth, int x0, int x1, int y, int height, int mouseX, int mouseY) {
        if (x1 - x0 < textWidth && mouseX > x0 && mouseX < x1 && mouseY > y - 2 && mouseY < y - 2 + height) {
            Screen screen = Minecraft.getInstance().screen;
            if (screen instanceof AdvancedTooltipScreen s && tooltip != null) {
                s.lootmod$setNoGapTooltipForNextRenderPass(tooltip, DefaultTooltipPositioner.INSTANCE);
            }
        }
    }
}
