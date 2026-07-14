package net.mesomods.lootwand.loot.poolentry;

import net.mesomods.lootwand.loot.lifoc.RenderedCondition;
import net.mesomods.lootwand.loot.lifoc.RenderedFunction;
import net.mesomods.lootwand.mixin.loot.entry.DynamicLootAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class DynamicLootEntry extends RenderedSingletonEntry {
    public ItemStack symbolicItem;
    public ResourceLocation dynamicLootId;

    public DynamicLootEntry(ResourceLocation dynamicLootId, int weight, int quality, LootItemCondition[] conditions, LootItemFunction[] functions, boolean inLifoc) {
        super(conditions, functions, weight, quality, inLifoc);
        this.dynamicLootId = dynamicLootId;
        this.updateDescription();
        this.updateHeight(false);
    }

    @Override
    protected void updateDescription() {
        if (dynamicLootId.equals(ShulkerBoxBlock.CONTENTS)) {
            description = Component.translatable("gui.loot_table_wand.dynamic_loot.shulker_box");
            symbolicItem = new ItemStack(Items.SHULKER_BOX);
        } else if (dynamicLootId.equals(DecoratedPotBlock.SHERDS_DYNAMIC_DROP_ID)) {
            description = Component.translatable("gui.loot_table_wand.dynamic_loot.decorated_pot");
            symbolicItem = new ItemStack(Items.BRICK);
        } else {
            description = Component.translatable("gui.loot_table_wand.dynamic_loot.unknown").append(" ").append(Component.literal(dynamicLootId.toString()).withStyle(ChatFormatting.GRAY));
            symbolicItem = ItemStack.EMPTY;
        }
        super.updateDescription();
    }

    @Override
    protected double calculateAverageCount() {
        return Double.NaN;
    }

    public static DynamicLootEntry fromVanilla(DynamicLootAccessor entry, boolean inLifoc) {
        return new DynamicLootEntry(entry.getName(), entry.getWeight(), entry.getQuality(), entry.getConditions(), entry.getFunctions(), inLifoc);
    }

    @Override
    public void render(GuiGraphics graphics, int top, int left, int poolLeft, int width, int transparency, int mouseX, int mouseY, List<RenderedCondition> additionalConditions, List<RenderedFunction> additionalFunctions) {
        super.render(graphics, top, poolLeft, width, transparency, null, symbolicItem, mouseX, mouseY, additionalConditions, additionalFunctions);
    }

    @Override
    public boolean isNothing() {
        return false;
    }

    @Override
    public LootPoolEntryContainer toVanilla() {
        return DynamicLootAccessor.createDynamicLoot(dynamicLootId, weight, quality, this.vanillaConditions, this.vanillaFunctions);
    }
}
