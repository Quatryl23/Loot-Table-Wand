package net.mesomods.lootwand.client.tooltip;

import net.mesomods.lootwand.client.ScreenUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class ClientScoreDependentProbabilityChartTooltip extends ClientProbabilityChartTooltip {
    public final Component scoreDependencyMessage;
    public final Component scaleMessage;
    public final List<FormattedCharSequence> cachedTooltip;

    public ClientScoreDependentProbabilityChartTooltip(ScoreDependentProbabilityChartTooltip tooltip) {
        super(0, false);
        this.scoreDependencyMessage = tooltip.getMessage();
        List<FormattedCharSequence> split = Minecraft.getInstance().font.split(scoreDependencyMessage, 150);
        this.cachedTooltip = new ArrayList<>(split);
        if (tooltip.getScale() != 1f) {
            this.scaleMessage = Component.translatable("gui.loot_table_wand.numbers.scoreboard.scale", tooltip.getScale());
        } else {
            this.scaleMessage = Component.empty();
        }
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        this.renderFrame(graphics, x, y);
        this.renderUnknownText(graphics, font, x + 8, y - 12);
        x = x + 8 + (int) CHART_WIDTH / 2;
        y = y + 8 + (CHART_HEIGHT - font.lineHeight) / 2;
        for (FormattedCharSequence line : cachedTooltip) {
            graphics.drawCenteredString(font, line, x, y, 0xffffffff);
            y += 10;
        }
        ScreenUtils.drawScaledString(graphics, scaleMessage, x, y + 2, 0xffffffff, 0.5f, true);
    }
}
