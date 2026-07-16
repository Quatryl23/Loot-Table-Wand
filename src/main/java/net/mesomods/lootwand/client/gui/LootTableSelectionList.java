package net.mesomods.lootwand.client.gui;

import net.mesomods.lootwand.client.tooltip.AdvancedTooltipAbstractWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class LootTableSelectionList extends ObjectSelectionList<LootTableSelectionList.Entry> {
    private Consumer<Entry> onSelect;
	public LootTableSelectionList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight, Consumer<Entry> onSelect) {
		super(minecraft, width, height, y0, y1, itemHeight);
        this.onSelect = onSelect;
	}

	public int addEntry(String string, Font font, Consumer<String> action) {
		return super.addEntry(new Entry(0, 0, string, font, action));
	}

    @Override
    public void setSelected(Entry entry) {
        super.setSelected(entry);
        onSelect.accept(entry);
    }

	@Override
	public void render(GuiGraphics graphics, int x, int y, float partialTick) {
		graphics.fill(this.x0, this.y0, this.x1, this.y1, 0xB2000000);
		super.render(graphics, x, y, partialTick);
	}

	public void removeAllEntries() {
		this.clearEntries();
	}

	public class Entry extends ObjectSelectionList.Entry<LootTableSelectionList.Entry> {
		private final Consumer<String> onClick;
		private final StringWidget widget;
		private final boolean isFolder;
		private long lastClickTime = 0L;

		Entry(int x, int y, String string, Font font, Consumer<String> action) {
			this.widget = new StringWidget(Component.literal(string), font);
			this.onClick = action;
			this.isFolder = !string.endsWith(".json");
		}

		public Component getNarration() {
			return widget.getMessage();
		}

		public boolean isFolder() {
			return isFolder;
		}

		public String getString() {
			return widget.getMessage().getString();
		}

		@Override
		public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovered, float partialTicks) {
			if (widget instanceof AdvancedTooltipAbstractWidget w) {
				widget.setX(left);
				widget.setY(top);
				widget.setWidth(width);
				w.lootmod$setHeight(height);
				widget.render(graphics, mouseX, mouseY, partialTicks);
			}
			if (isFolder)
				renderFrame(graphics, top, left, top + height - 1, left + width - 5);
		}

		public void renderFrame(GuiGraphics graphics, int top, int left, int bottom, int right) {
			final int COLOR = 0xFF888888;
			graphics.fill(left, top, right + 1, top + 1, COLOR);
			graphics.fill(left, top, left + 1, bottom + 1, COLOR);
			graphics.fill(left, bottom, right + 1, bottom + 1, COLOR);
			graphics.fill(right, top, right + 1, bottom + 1, COLOR);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int buttonCode) {
			if (System.nanoTime() - lastClickTime > 500_000_000L) {
				lastClickTime = System.nanoTime();
				return true;
			}
			onClick.accept(widget.getMessage().getString());
			return true;
		}
	}
}
