package net.mesomods.lootwand.client.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ClientLoadingProbabilityChartTooltip extends ClientProbabilityChartTooltip {
    public static final Component LOADING = Component.translatable("gui.loot_table_wand.numbers.tooltip.loading");

    public ClientLoadingProbabilityChartTooltip() {
        super(0, false);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        this.renderFrame(graphics, x, y);
        graphics.drawCenteredString(font, LOADING, x + 8 + (int) CHART_WIDTH / 2, y + 8 + (CHART_HEIGHT - font.lineHeight) / 2, 0xffffffff);
    }
}
