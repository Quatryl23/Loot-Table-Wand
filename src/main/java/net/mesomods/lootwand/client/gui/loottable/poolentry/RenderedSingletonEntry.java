package net.mesomods.lootwand.client.gui.loottable.poolentry;

import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.client.gui.LootTableViewMode;
import net.mesomods.lootwand.client.gui.loottable.LifocParent;
import net.mesomods.lootwand.client.gui.loottable.RenderedLootPool;
import net.mesomods.lootwand.client.gui.loottable.lifoc.RenderedCondition;
import net.mesomods.lootwand.client.gui.loottable.lifoc.RenderedFunction;
import net.mesomods.lootwand.client.gui.loottable.numbers.NumberProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public abstract class RenderedSingletonEntry extends RenderedLootPool.Entry implements LifocParent {
    public static final ResourceLocation ITEM_SLOT = new ResourceLocation("textures/gui/container/stats_icons.png");
    public static final Font FONT = Minecraft.getInstance().font;
    protected int totalHeight;
    protected List<RenderedFunction> functions;
    protected final LootItemFunction[] vanillaFunctions;
    protected List<RenderedCondition> conditions;
    protected final LootItemCondition[] vanillaConditions;
    protected int weight;
    protected int quality;
    protected double chance;
    protected double averageCount;
    protected double averagePerTry;
    protected double averageTriesToGet;
    protected Component description = Component.empty();
    protected Tooltip tooltip = null;
    protected LootTableViewMode viewMode;

    public RenderedSingletonEntry(LootItemCondition[] conditions, LootItemFunction[] functions, int weight, int quality, boolean inLifoc) {
        super(inLifoc);
        this.vanillaFunctions = functions;
        this.functions = Arrays.stream(functions).map(RenderedFunction::fromVanilla).toList();
        this.vanillaConditions = conditions;
        this.conditions = Arrays.stream(conditions).map(RenderedCondition::fromVanilla).toList();
        this.weight = weight;
        this.quality = quality;
        setFunctionHeightUpdater();
    }

    public void render(GuiGraphics graphics, int top, int left, int width, int transparency, @Nullable NumberProvider count, ItemStack stack, int mouseX, int mouseY, List<RenderedCondition> additionalConditions, List<RenderedFunction> additionalFunctions) {
        int shift = 0;
        boolean isRaw = viewMode == LootTableViewMode.RAW_LIST;
        if (isRaw) {
            if (!inLifoc) {
                if (weight != -1) {
                    graphics.drawCenteredString(FONT, String.valueOf(weight), left + 17, top + 6, 0xFFFFFF);
                }
                if (quality != 0) {
                    graphics.drawCenteredString(FONT, String.valueOf(quality), left + 47, top + 6, 0xFFFFFF);
                }
                shift += 60;
            }
            if (count != null) ScreenUtils.renderNumberProvider(graphics, count, false, left + shift + 22, top + 6, 0xFFFFFF, true, mouseX, mouseY);
        } else {
            if (!inLifoc) {
                if (!Double.isNaN(chance)) {
                    graphics.drawCenteredString(FONT, ScreenUtils.PERCENT_FORMAT.format(chance), left + 22, top + 6, 0xFFFFFF);
                }
                if (!Double.isNaN(averagePerTry)) {
                    graphics.drawCenteredString(FONT, ScreenUtils.DOUBLE_FORMAT.format(averagePerTry), left + 62, top + 6, 0xFFFFFF);
                }
                if (!Double.isNaN(averageTriesToGet)) {
                    graphics.drawCenteredString(FONT, ScreenUtils.DOUBLE_FORMAT.format(averageTriesToGet), left + 102, top + 6, 0xFFFFFF);
                }
                shift += 85;
            } else {
                if (!Double.isNaN(averageCount)) {
                    graphics.drawCenteredString(FONT, ScreenUtils.DOUBLE_FORMAT.format(averageCount), left + 27, top + 6, 0xFFFFFF);
                }
                shift += 10;
            }
        }
        graphics.blit(ITEM_SLOT, left + shift + 47, top + 1, 0, 0, 0, 18, 18, 128, 128);
        graphics.renderItem(stack, left + shift + 48, top + 2, 20, 20);
        graphics.drawString(FONT, description, left + shift + 72, top + 6, 0xFFFFFF);
        if (FONT.width(description) > width - shift - 72 && mouseX > left + shift + 72 && mouseY > top + 6 && mouseY < top + 6 + FONT.lineHeight) {
            Screen screen = Minecraft.getInstance().screen;
            if (tooltip != null && screen != null)
                screen.setTooltipForNextRenderPass(tooltip, DefaultTooltipPositioner.INSTANCE, true);
        }
        int renderTop = top + 21;
        if (!inLifoc) {
            shift += 50;
        }
        boolean isFirst = true;
        for (RenderedCondition condition : additionalConditions) {
            if (condition.isHidden())
                continue;
            condition.render(graphics, left + 7 + shift, left + width - 3, renderTop, transparency + 1, !isFirst, mouseX, mouseY);
            renderTop += condition.getHeight();
            isFirst = false;
        }
        for (RenderedCondition condition : conditions) {
            if (condition.isHidden())
                continue;
            condition.render(graphics, left + 7 + shift, left + width - 3, renderTop, transparency + 1, !isFirst, mouseX, mouseY);
            renderTop += condition.getHeight();
            isFirst = false;
        }
        if (!conditions.isEmpty() || !additionalConditions.isEmpty()) renderTop += 3;
        isFirst = true;
        for (RenderedFunction function : additionalFunctions) {
            if (function.isHidden())
                continue;
            function.render(graphics, left + 7 + shift, left + width - 3, renderTop, transparency + 1, !isFirst, mouseX, mouseY);
            renderTop += function.getHeight();
            isFirst = false;
        }
        for (RenderedFunction function : functions) {
            if (function.isHidden()) {
                continue;
            }
            function.render(graphics, left + 7 + shift, left + width - 3, renderTop, transparency + 1, !isFirst, mouseX, mouseY);
            renderTop += function.getHeight();
            isFirst = false;
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int key, int renderLeft, int renderTop) {
        return mouseClicked(x, y, key, renderLeft, renderTop, List.of(), List.of());
    }

    public boolean mouseClicked(double x, double y, int key, int renderLeft, int renderTop, List<RenderedCondition> additionalConditions, List<RenderedFunction> additionalFunctions) {
        int shift = inLifoc ? 0 : 110;
        int y0 = renderTop + 21;
        int y1;
        for (RenderedCondition condition : additionalConditions) {
            if (condition.isHidden()) continue;
            y1 = y0 + condition.getHeight();
            if (y1 > y && y > y0)
                return condition.mouseClicked(x, y, key, renderLeft + 7 + shift, y0);
            y0 = y1;
        }
        for (RenderedCondition condition : this.conditions) {
            if (condition.isHidden()) continue;
            y1 = y0 + condition.getHeight();
            if (y1 > y && y > y0)
                return condition.mouseClicked(x, y, key, renderLeft + 7 + shift, y0);
            y0 = y1;
        }
        if (!this.conditions.isEmpty() || !additionalConditions.isEmpty()) y0 += 3;
        for (RenderedFunction function : additionalFunctions) {
            if (function.isHidden()) continue;
            y1 = y0 + function.getHeight();
            if (y1 > y && y > y0)
                return function.mouseClicked(x, y, key, renderLeft + 7 + shift, y0);
            y0 = y1;
        }
        for (RenderedFunction function : this.functions) {
            if (function.isHidden()) continue;
            y1 = y0 + function.getHeight();
            if (y1 > y && y > y0)
                return function.mouseClicked(x, y, key, renderLeft + 7 + shift, y0);
            y0 = y1;
        }
        return false;

    }

    protected void updateDescription() {
        this.tooltip = Tooltip.create(description);
    }

    @Override
    public List<RenderedFunction> getFunctions() {
        return functions;
    }

    @Override
    public List<RenderedCondition> getConditions() {
        return conditions;
    }

    @Override
    public void toggleDefaultParameters(boolean hidden) {
        LifocParent.super.toggleDefaultParameters(hidden);
        updateHeight(false);
    }

    public void applyFunctionPreviewEffects(boolean enabled) {
        for (RenderedFunction function : functions) {
            function.applyPreviewEffect(this, enabled);
            function.applyFunctionPreviewEffects(enabled);
        }
        for (RenderedCondition condition : conditions) {
            condition.applyPreviewEffect(this, enabled);
            condition.applyFunctionPreviewEffects(enabled);
        }
        this.updateHeight(false);
    }

    @Override
    public void acceptPreviewList(ResourceLocation location, int[] previewTimes, ListTag previewItems, boolean isTag) {
        for (RenderedFunction function : functions) {
            function.acceptPreviewList(location, previewTimes, previewItems, isTag);
        }
        for (RenderedCondition condition : conditions) {
            condition.acceptPreviewList(location, previewTimes, previewItems, isTag);
        }
    }

    public void updateHeight(boolean updatePool) {
        this.totalHeight = 24;
        for (RenderedCondition condition : conditions) {
            if (!condition.isHidden()) {
                totalHeight += condition.getHeight();
            }
        }
        for (RenderedFunction function : functions) {
            if (!function.isHidden()) {
                totalHeight += function.getHeight();
            }
        }
        if (!conditions.isEmpty() && !functions.isEmpty()) totalHeight += 3;
        super.updateHeight(updatePool);
    }

    @Override
    public boolean isConditional() {
        return !conditions.isEmpty();
    }

    protected abstract double calculateAverageCount();

    @Override
    public void setViewMode(LootTableViewMode mode) {
        this.viewMode = mode;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public int getQuality() {
        return quality;
    }

    @Override
    public int getQualityWeight(double luck) {
        return Math.max(Mth.floor(weight + luck * quality), 0);
    }

    @Override
    public double getAverageCount() {
        return averageCount;
    }

    @Override
    public double getChance() {
        return chance;
    }

    @Override
    public double getAveragePerTry() {
        return averagePerTry;
    }

    @Override
    public double getAverageTriesToGet() {
        return averageTriesToGet;
    }

    @Override
    public void calculateAdditionalData(int totalWeight, double averageRolls, float luck) {
        double qualityWeight = Math.max(Mth.floor(weight + luck * quality), 0);
        this.chance = 1 - Math.pow(1 - qualityWeight / totalWeight, averageRolls);
        this.averageCount = calculateAverageCount();
        this.averagePerTry = averageCount * chance;
        this.averageTriesToGet = 1 / chance;
    }

    @Override
    public int getHeight() {
        return totalHeight;
    }

    @Override
    public int getHeight(List<RenderedCondition> additionalConditions, List<RenderedFunction> additionalFunctions) {
        int i = 24;
        for (RenderedCondition condition : additionalConditions) {
            if (!condition.isHidden()) {
                i += condition.getHeight();
            }
        }
        for (RenderedCondition condition : conditions) {
            if (!condition.isHidden()) {
                i += condition.getHeight();
            }
        }
        for (RenderedFunction function : additionalFunctions) {
            if (!function.isHidden()) {
                i += function.getHeight();
            }
        }
        for (RenderedFunction function : functions) {
            if (!function.isHidden()) {
                i += function.getHeight();
            }
        }
        if ((!conditions.isEmpty() || !additionalConditions.isEmpty()) && (!functions.isEmpty() || !additionalFunctions.isEmpty())) i += 3;
        return i;
    }
}
