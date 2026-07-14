package net.mesomods.lootwand.client.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ClientProbabilityChartTooltip implements ClientTooltipComponent {
    public static final double CHART_WIDTH = 144;
    public static final int CHART_HEIGHT = 84;
    public static final Component UNKNOWN = Component.translatable("gui.loot_table_wand.numbers.tooltip.unknown");
    final double maxProbability;
    final boolean romanize;

    public ClientProbabilityChartTooltip(double maxProbability, boolean romanize) {
        this.maxProbability = maxProbability;
        this.romanize = romanize;
    }

    @Override
    public int getHeight() {
        return 100;
    }

    @Override
    public int getWidth(Font font) {
        return 160;
    }

    public void renderFrame(GuiGraphics graphics, int x, int y) {
        graphics.fill(x + 5, y + 5, x + 155, y + 95, 0xff200020);
    }

    protected void renderUnknownText(GuiGraphics graphics, Font font, int x, int y) {
        graphics.drawCenteredString(font, UNKNOWN, x + (int) CHART_WIDTH / 2, y + (CHART_HEIGHT - font.lineHeight) / 2, 0xffffffff);
    }
}
