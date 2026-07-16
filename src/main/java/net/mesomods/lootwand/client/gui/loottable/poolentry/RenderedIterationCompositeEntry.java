package net.mesomods.lootwand.client.gui.loottable.poolentry;

import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.client.gui.loottable.RenderedLootPool;
import net.mesomods.lootwand.client.gui.loottable.lifoc.RenderedFunction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public abstract class RenderedIterationCompositeEntry extends RenderedCompositeEntry {
    public RenderedIterationCompositeEntry(LootPoolEntryContainer[] children, LootItemCondition[] conditions, boolean inLifoc) {
        super(children, conditions, inLifoc);
    }

    @Override
    public int renderChildren(GuiGraphics graphics, int top, int left, int poolLeft, int width, int transparency, int mouseX, int mouseY, List<RenderedFunction> additionalFunctions) {
        boolean isFirst = true;
        int i = width % 2 == 1 ? 3 : 4;
        for (RenderedLootPool.Entry child : children) {
            if (!isFirst) {
                graphics.drawCenteredString(ScreenUtils.FONT, getDescription(), left + width / 2, top + 2, ScreenUtils.getOutlineColor(transparency));
                int halfTextWidth = 3 + ScreenUtils.FONT.width(getDescription()) / 2;
                ScreenUtils.drawDashedLine(graphics, left + 3, top + 2 + ScreenUtils.FONT_HEIGHT / 2, width / 2 - halfTextWidth - 3, 10, transparency);
                ScreenUtils.drawDashedLine(graphics, left + width / 2 + halfTextWidth, top + 2 + ScreenUtils.FONT_HEIGHT / 2, width / 2 - halfTextWidth - i, 10, transparency);
                top += ScreenUtils.TEXT_HEIGHT;
            }
            child.render(graphics, top, left + 2, poolLeft, width - 4, transparency, mouseX, mouseY, List.of(), additionalFunctions);
            top += child.getHeight(List.of(), additionalFunctions);
            isFirst = false;
        }
        return top;
    }

    @Override
    public int mouseClickedIterateChildren(double x, double y, int key, int renderTop, int renderLeft, List<RenderedFunction> additionalFunctions) {
        int y0 = renderTop;
        int y1;
        boolean isFirst = true;
        for (RenderedLootPool.Entry entry : this.children) {
            if (!isFirst) {
                y0 += ScreenUtils.TEXT_HEIGHT;
            }
            isFirst = false;
            y1 = y0 + entry.getHeight();
            if (y1 > y && y > y0) {
                if (entry.mouseClicked(x, y, key, renderLeft, y0, List.of(), additionalFunctions)) {
                    return -1;
                } else {
                    return -2;
                }
            }
            y0 = y1;
        }
        return y0;
    }

    @Override
    public boolean isConditional() {
        return !this.children.isEmpty() || super.isConditional();
    }

    public abstract Component getDescription();

    @Override
    public int getChildrenHeight(List<RenderedFunction> additionalFunctions) {
        int i = 1;
        for (RenderedLootPool.Entry child : children) {
            if (i != 1) i += ScreenUtils.TEXT_HEIGHT;
            i += child.getHeight(List.of(), additionalFunctions);
        }
        return i;
    }
}
