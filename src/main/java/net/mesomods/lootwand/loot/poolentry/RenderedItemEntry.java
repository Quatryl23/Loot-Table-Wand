package net.mesomods.lootwand.loot.poolentry;

import net.mesomods.lootwand.loot.lifoc.RenderedCondition;
import net.mesomods.lootwand.loot.lifoc.RenderedFunction;
import net.mesomods.lootwand.loot.numbers.NumberProvider;
import net.mesomods.lootwand.mixin.loot.LootItemAccessor;
import net.mesomods.lootwand.mixin.loot.entry.LootPoolSingletonContainerAccessor;
import net.mesomods.lootwand.util.LootContextManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class RenderedItemEntry extends RenderedSingletonEntry {
    final ItemStack stack;
    ItemStack modifiedStack;
    final Item item;
    String itemId;
    public NumberProvider count = NumberProvider.constant(1);


    public RenderedItemEntry(Item item, int weight, int quality, LootItemCondition[] conditions, LootItemFunction[] functions, BiFunction<ItemStack, LootContext, ItemStack> compositeFunction, boolean inLifoc) {
        super(conditions, functions, weight, quality, inLifoc);
        this.item = item;
        this.itemId = ForgeRegistries.ITEMS.getKey(this.item).toString();
        this.stack = new ItemStack(item);
        this.description = item == Items.AIR ? Component.translatable("gui.loot_table_wand.item_entry.nothing") : Component.translatable(stack.getDescriptionId());
        this.modifiedStack = new ItemStack(item);
        LootContextManager.simulateLootItemFunctionsClient(stack, Arrays.asList(functions), this);
        this.updateHeight(false);
        super.updateDescription();
    }

    public static RenderedItemEntry fromVanilla(LootItemAccessor itemEntry, boolean inLifoc) {
        return new RenderedItemEntry(itemEntry.getItem(), itemEntry.getWeight(), itemEntry.getQuality(), itemEntry.getConditions(), itemEntry.getFunctions(), itemEntry.getCompositeFunction(), inLifoc);
    }

    public static RenderedItemEntry empty(LootPoolSingletonContainerAccessor singleton, boolean inLifoc) {
        return new RenderedItemEntry(Items.AIR, singleton.getWeight(), singleton.getQuality(), singleton.getConditions(), singleton.getFunctions(), singleton.getCompositeFunction(), inLifoc);
    }

    public void setModifiedStack(ItemStack modifiedStack) {
        this.modifiedStack = modifiedStack;
        this.description = modifiedStack.getItem() == Items.AIR ? Component.translatable("gui.loot_table_wand.item_entry.nothing") : Component.translatable(modifiedStack.getDescriptionId());
    }

    @Override
    public void render(GuiGraphics graphics, int top, int left, int poolLeft, int width, int transparency, int mouseX, int mouseY, List<RenderedCondition> additionalConditions, List<RenderedFunction> additionalFunctions) {
        super.render(graphics, top, poolLeft, width, transparency, count, modifiedStack, mouseX, mouseY, additionalConditions, additionalFunctions);
    }

    @Override
    public boolean isNothing() {
        return item == Items.AIR;
    }

    @Override
    public LootPoolEntryContainer toVanilla() {
        return LootItemAccessor.create(item, weight, quality, this.vanillaConditions, this.vanillaFunctions);
    }

    @Override
    protected double calculateAverageCount() {
        return count.getAverage();
    }
}
