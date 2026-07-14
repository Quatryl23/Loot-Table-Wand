package net.mesomods.lootwand.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mesomods.lootwand.capabilities.LootTableWandPlayerDataManager;
import net.mesomods.lootwand.capabilities.NumberProviderTooltipMode;
import net.mesomods.lootwand.client.gui.screen.LootTableDataScreen;
import net.mesomods.lootwand.client.tooltip.AdvancedTooltipAbstractWidget;
import net.mesomods.lootwand.client.tooltip.AdvancedTooltipGuiGraphics;
import net.mesomods.lootwand.loot.numbers.NumberProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;


@OnlyIn(Dist.CLIENT)
public class ScreenUtils {
    public static final Font FONT = Minecraft.getInstance().font;
    public static final DecimalFormat NUMBER_PROVIDER_FORMAT;
    public static final DecimalFormat DOUBLE_FORMAT;
    public static final DecimalFormat PERCENT_FORMAT;
    public static final int FONT_HEIGHT = FONT.lineHeight;
    public static final int TEXT_HEIGHT = FONT_HEIGHT + 4;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setMinusSign('\u00AD');
        NUMBER_PROVIDER_FORMAT = new DecimalFormat("#.#", symbols);
        DOUBLE_FORMAT = new DecimalFormat("#.##", symbols);
        PERCENT_FORMAT = new DecimalFormat("#.#", symbols);
        PERCENT_FORMAT.setMultiplier(100);
        PERCENT_FORMAT.setPositiveSuffix("%");
    }

    public static int getOutlineColor(int transparency) {
        return switch (transparency) {
            case -1, 0 -> 0xFF888888;
            case 1 -> 0xBB888888;
            case 2 -> 0x99888888;
            case 3 -> 0x66888888;
            case 4 -> 0x44888888;
            case 5 -> 0x33888888;
            default -> 0x22888888;
        };
    }

    public static int getRenderWidth(Font font, NumberProvider provider, boolean romanize) {
        if (provider.isConstant()) {
            return font.width(formatFloat(provider.getAverage(), romanize));
        } else if (provider.isUniform()) {
            return font.width(formatFloat(provider.getAbsoluteMin(), romanize) + "-" + formatFloat(provider.getAbsoluteMax(), romanize));
        } else {
            return Math.max(FONT.width(formatFloat(provider.getAbsoluteMin()) + "-" + formatFloat(provider.getAbsoluteMax())), FONT.width("~ " + formatFloat(provider.getAverage()))) / 2;
        }
    }

    public static int drawScaledString(GuiGraphics graphics, Component component, int x, int y, int color, float scale, boolean centered) {
        return drawScaledString(graphics, component, x, y, color, scale, centered, true);
    }

    public static int drawScaledString(GuiGraphics graphics, Component component, float x, float y, int color, float scale, boolean centered, boolean shadow) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.scale(scale, scale, 1);
        int scaledX = Math.round(x / scale);
        int scaledY = Math.round(y / scale);
        int i;
        if (centered) {
            i = graphics.drawString(FONT, component, scaledX - FONT.width(component) / 2, scaledY, color, shadow);
        } else {
            i = graphics.drawString(FONT, component, scaledX, scaledY, color, shadow);
        }
        pose.popPose();
        return Math.round(i * scale);
    }


    public static int drawScaledString(GuiGraphics graphics, String string, int x, int y, int color, float scale, boolean centered) {
        return drawScaledString(graphics, string, x, y, color, scale, centered, false);
    }

    public static int drawScaledString(GuiGraphics graphics, String string, float x, float y, int color, float scale, boolean centered, boolean outline) {
        return drawScaledString(graphics, Component.literal(string), x, y, color, scale, centered, outline);
    }

    public static String formatNumber(Number number, boolean romanized) {
        if (number instanceof Integer i) {
            return formatInt(i, romanized);
        } else if (number instanceof Long l) {
            return formatInt(l.intValue(), romanized);
        } else if (number instanceof  Float f) {
            return formatFloat(f, romanized);
        } else if (number instanceof Double d) {
            return formatFloat(d.floatValue(), romanized);
        }
        return NUMBER_PROVIDER_FORMAT.format(number);
    }

    public static String formatInt(int i, boolean romanized) {
        return formatFloat((float) i, romanized);
    }

    public static String formatFloat(float f, boolean romanized) {
        if (romanized) {
            if (f == 1.0) {
                return "I";
            } else if (f == 2.0) {
                return "II";
            } else if (f == 3.0) {
                return "III";
            } else if (f == 4.0) {
                return "IV";
            } else if (f == 5.0) {
                return "V";
            }
        }
        return formatFloat(f);
    }

    public static String formatFloat(float f) {
        if (Float.isNaN(f)) {
            return "?";
        } else {
            return NUMBER_PROVIDER_FORMAT.format(f);
        }
    }

    public static void drawDashedLine(GuiGraphics graphics, int x, int y, int width, int dashWidth, int transparency) {
        int color = getOutlineColor(transparency);
        int remainder = (width % (2 * dashWidth)) + dashWidth;
        if (remainder > 2 * dashWidth + 2) remainder -= 2 * dashWidth;
        int r1 = (int) Math.ceil(remainder / 2.0);
        int r2 = (int) Math.floor(remainder / 2.0);
        graphics.fill(x, y, x + r1, y + 1, color);
        for (int i = x + r1 + dashWidth; i < x + width - r2; i += 2 * dashWidth) {
            graphics.fill(i, y, i + dashWidth, y + 1, color);
        }
        graphics.fill(x + width - r2, y, x + width, y + 1, color);
    }

    public static void renderOutline(GuiGraphics graphics, int x0, int y0, int width, int height, int transparency, boolean skipUpperPart) {
        int color = getOutlineColor(transparency);
        if (skipUpperPart) {
            graphics.fill(x0, y0 + 1, x0 + 1, y0 + height - 1, color);
            graphics.fill(x0 + width - 1, y0 + 1, x0 + width, y0 + height - 1, color);
            graphics.fill(x0, y0 + height - 1, x0 + width, y0 + height, color);
        } else {
            graphics.renderOutline(x0, y0, width, height, color);
        }
    }

    public static int renderNumberProvider(GuiGraphics graphics, NumberProvider provider, boolean useFloats, int x, int y, int color, boolean centered, int mouseX, int mouseY) {
        return renderNumberProvider(graphics, provider, useFloats, x, y, color, centered, false, mouseX, mouseY);
    }

    public static int renderNumberProvider(GuiGraphics graphics, NumberProvider provider, boolean useFloats, int x, int y, int color, boolean centered, boolean romanize, int mouseX, int mouseY) {
        String string;
        if (provider.isConstant()) {
            string = formatFloat(provider.getAverage(), romanize);
            if (centered) {
                graphics.drawCenteredString(FONT, string, x, y, color);
                renderNumberProviderTooltip(graphics, provider, useFloats, romanize, x - (FONT.width(string) / 2), x + (FONT.width(string) / 2), y, y + FONT_HEIGHT, mouseX, mouseY);
            } else {
                graphics.drawString(FONT, string, x, y, color);
                renderNumberProviderTooltip(graphics, provider, useFloats, romanize, x, x + FONT.width(string), y, y + FONT_HEIGHT, mouseX, mouseY);
            }
            return FONT.width(string);
        } else if (provider.isUniform()) {
            string = formatFloat(provider.getAbsoluteMin(), romanize) + "-" + formatFloat(provider.getAbsoluteMax(), romanize);
            if (centered) {
                graphics.drawCenteredString(FONT, string, x, y, color);
                renderNumberProviderTooltip(graphics, provider, useFloats, romanize, x - (FONT.width(string) / 2), x + (FONT.width(string) / 2), y, y + FONT_HEIGHT, mouseX, mouseY);
            } else {
                graphics.drawString(FONT, string, x, y, color);
                renderNumberProviderTooltip(graphics, provider, useFloats, romanize, x, x + FONT.width(string), y, y + FONT_HEIGHT, mouseX, mouseY);
            }
            return FONT.width(string);
        } else {
            string = formatFloat(provider.getAbsoluteMin()) + "-" + formatFloat(provider.getAbsoluteMax());
            ScreenUtils.drawScaledString(graphics, string, x + 1, y, color, 0.5F, centered);
            String average = formatFloat(provider.getAverage());
            int left = centered ? x - 3 - FONT.width(average) / 4 : x + 4 - FONT.width(average) / 4;
            ScreenUtils.drawScaledString(graphics, "~", left, y + 7, color, 0.5F, false);
            ScreenUtils.drawScaledString(graphics, average, left + 4, y + 5, color, 0.5F, false);
            int width = Math.max(FONT.width(string), FONT.width("~ " + average)) / 2;
            int height = 5 + (FONT_HEIGHT / 2);
            if (centered) x -= width / 2;
            renderNumberProviderTooltip(graphics, provider, useFloats, romanize, x + 1, x + width + 1, y, y + height, mouseX, mouseY);
            return width;
        }
    }

    public static void renderNumberProviderTooltip(GuiGraphics graphics, NumberProvider provider, boolean useFloats, boolean romanize, int x0, int x1, int y0, int y1, int x, int y) {
        x = Math.abs(x);
        y = Math.abs(y);
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        NumberProviderTooltipMode tooltipMode = LootTableWandPlayerDataManager.getNumberProviderTooltipMode(Minecraft.getInstance().player);
        if (x0 < x && x < x1 && y0 < y && y < y1 && !provider.isConstant() && ((tooltipMode.showUniformDistribution() && provider.isUniform()) || (tooltipMode.showNonUniformDistribution() && !provider.isUniform()) || (tooltipMode.showScoreboardDependentDistribution() && provider.isScoreboardDependent()))) {
            if (graphics instanceof AdvancedTooltipGuiGraphics guiGraphics) {
                guiGraphics.lootmod$setTooltipGap(false);
                guiGraphics.lootmod$renderNumberProviderTooltip(FONT, List.of(Component.translatable("gui.loot_table_wand.numbers.tooltip")), provider.getTooltip(useFloats, romanize), x, y, LootTableDataScreen.TOOLTIP_POSITIONER);
                guiGraphics.lootmod$setTooltipGap(true);
            }
        }
    }

    public static Component numberProviderToComponent(NumberProvider provider, boolean romanize) {
        if (provider.isConstant()) {
            return Component.literal(formatFloat(provider.getAverage(), romanize));
        } else {
            return Component.literal(formatFloat(provider.getAbsoluteMin(), romanize) + "-" + formatFloat(provider.getAbsoluteMax(), romanize));
        }
    }

    public static void enableAdvancedTooltips(AbstractWidget... widgets) {
        for (AbstractWidget widget : widgets) {
            if (widget instanceof AdvancedTooltipAbstractWidget a) {
                a.lootmod$enableAdvancedTooltips();
            }
        }
    }
}
