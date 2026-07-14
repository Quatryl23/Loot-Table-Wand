package net.mesomods.lootwand.loot.poolentry;

import net.mesomods.lootwand.LootWandMod;
import net.mesomods.lootwand.client.gui.LootTableViewMode;
import net.mesomods.lootwand.client.gui.screen.LootTableDataScreen;
import net.mesomods.lootwand.loot.lifoc.RenderedCondition;
import net.mesomods.lootwand.loot.lifoc.RenderedFunction;
import net.mesomods.lootwand.mixin.loot.entry.LootTableReferenceAccessor;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.client.RequestPreviewListPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class LootTableReferenceEntry extends PreviewCycleSingletonEntry {
    public static final ItemStack DEFAULT_SYMBOL = new ItemStack(LootWandMod.LOOT_TABLE_WAND.get());
    public static final int HOVERED_COLOR = 0x4694ce;
    public ResourceLocation lootTable;

    public LootTableReferenceEntry(ResourceLocation lootTable, int weight, int quality, LootItemCondition[] conditions, LootItemFunction[] functions, boolean inLifoc) {
        super(conditions, functions, weight, quality, inLifoc);
        this.lootTable = lootTable;
        this.updateDescription();
        this.updateHeight(false);
        LootTableNetwork.CHANNEL.sendToServer(new RequestPreviewListPacket(lootTable, false));
    }

    @Override
    protected void updateDescription() {
        description = Component.literal(lootTable.toString());
        super.updateDescription();
    }

    public static LootTableReferenceEntry fromVanilla(LootTableReferenceAccessor entry, boolean inLifoc) {
        return new LootTableReferenceEntry(entry.getName(), entry.getWeight(), entry.getQuality(), entry.getConditions(), entry.getFunctions(), inLifoc);
    }

    @Override
    public void acceptPreviewList(ResourceLocation location, int[] previewTimes, ListTag previewItems, boolean isTag) {
        if (!isTag && location.equals(this.lootTable)) {
            this.previewTimes = previewTimes;
            this.previewItems = previewItems.stream().map(tag -> ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(tag.getAsString()))).map(item -> item == null ? ItemStack.EMPTY : item.getDefaultInstance()).toList();
            this.previewReady = true;
        }
    }

    @Override
    protected double calculateAverageCount() {
        return 1;
    }

    @Override
    public void render(GuiGraphics graphics, int top, int left, int poolLeft, int width, int transparency, int mouseX, int mouseY, List<RenderedCondition> additionalConditions, List<RenderedFunction> additionalFunctions) {
        super.render(graphics, top, poolLeft, width, transparency, null, getPreviewItem(), mouseX, mouseY, additionalConditions, additionalFunctions);
        boolean isRaw = viewMode == LootTableViewMode.RAW_LIST;
        int shift = inLifoc ? (isRaw ? 0 : 10) : (isRaw ? 60 : 85);
        int descriptionLeft = poolLeft + shift + 72;
        int descriptionRight = poolLeft + shift + 72 + FONT.width(description);
        if (top + 6 <= mouseY && mouseY < top + 6 + FONT.lineHeight && descriptionLeft <= mouseX && mouseX < descriptionRight) {
            graphics.drawString(FONT, description.copy().withStyle(ChatFormatting.UNDERLINE), descriptionLeft, top + 6, HOVERED_COLOR);
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int key, int renderLeft, int renderTop, List<RenderedCondition> additionalConditions, List<RenderedFunction> additionalFunctions) {
        boolean isRaw = viewMode == LootTableViewMode.RAW_LIST;
        int shift = inLifoc ? (isRaw ? 0 : 10) : (isRaw ? 60 : 85);
        if (renderTop + 6 <= y && y < renderTop + 6 + FONT.lineHeight && renderLeft + 72 + shift <= x && x < renderLeft + 72 + shift + FONT.width(description) && key == 0) {
            Screen screen = Minecraft.getInstance().screen;
            if (screen instanceof LootTableDataScreen lootTableScreen) {
                lootTableScreen.clickedOnLootTableReference(lootTable);
                return true;
            }
            return false;
        }
        return super.mouseClicked(x, y, key, renderLeft, renderTop, additionalConditions, additionalFunctions);
    }

    @Override
    public LootPoolEntryContainer toVanilla() {
        return LootTableReferenceAccessor.createLootTableReference(lootTable, weight, quality, this.vanillaConditions, this.vanillaFunctions);
    }

    @Override
    public ItemStack getDefaultSymbol() {
        return DEFAULT_SYMBOL;
    }
}
