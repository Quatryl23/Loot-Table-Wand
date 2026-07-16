package net.mesomods.lootwand.client.gui.loottable.poolentry;

import net.mesomods.lootwand.client.gui.loottable.lifoc.RenderedCondition;
import net.mesomods.lootwand.client.gui.loottable.lifoc.RenderedFunction;
import net.mesomods.lootwand.client.gui.loottable.numbers.NumberProvider;
import net.mesomods.lootwand.mixin.loot.entry.TagEntryAccessor;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.client.RequestPreviewListPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Optional;

public class RenderedTagEntry extends PreviewCycleSingletonEntry {
    public static final ItemStack DEFAULT_SYMBOL = new ItemStack(Items.NAME_TAG);
    TagKey<Item> tag;
    ResourceLocation tagLocation;
    public NumberProvider count = NumberProvider.constant(1);
    final boolean expand;

    public RenderedTagEntry(TagKey<Item> tag, boolean expand, int weight, int quality, LootItemCondition[] conditions, LootItemFunction[] functions, boolean inLifoc) {
        super(conditions, functions, weight, quality, inLifoc);
        this.tag = tag;
        this.tagLocation = tag.location();
        this.expand = expand;
        this.updateDescription();
        this.updateHeight(false);
        LootTableNetwork.sendToServer(new RequestPreviewListPacket(tagLocation, true));
    }

    @Override
    protected void updateDescription() {
        MutableComponent preTagName = expand ? Component.translatable("gui.loot_table_wand.tag_entry.expand_true") : Component.translatable("gui.loot_table_wand.tag_entry.expand_false");
        description = preTagName.append(" ").append(Component.literal(tag.location().toString()));
        super.updateDescription();
    }

    @Override
    public void acceptPreviewList(ResourceLocation location, int[] previewTimes, ListTag previewItems, boolean isTag) {
        if (isTag && location.equals(this.tagLocation)) {
            this.previewTimes = previewTimes;
            this.previewItems = previewItems.stream().map(tag -> BuiltInRegistries.ITEM.get(new ResourceLocation(tag.getAsString()))).map(Item::getDefaultInstance).toList();
            this.previewReady = true;
        }
    }

    @Override
    public int getWeight() {
        if (expand) {
            TagKey<Item> key = TagKey.create(BuiltInRegistries.ITEM.key(), tagLocation);
            Optional<HolderSet.Named<Item>> optional = BuiltInRegistries.ITEM.getTag(key);
            return optional.map(holders -> holders.size() * super.getWeight()).orElseGet(super::getWeight);
        } else {
           return super.getWeight();
        }
    }

    @Override
    public int getQuality() {
        if (expand) {
            TagKey<Item> key = TagKey.create(BuiltInRegistries.ITEM.key(), tagLocation);
            Optional<HolderSet.Named<Item>> optional = BuiltInRegistries.ITEM.getTag(key);
            return optional.map(holders -> holders.size() * super.getQuality()).orElseGet(super::getQuality);
        } else {
            return super.getQuality();
        }
    }

    @Override
    public int getQualityWeight(double luck) {
        if (expand) {
            TagKey<Item> key = TagKey.create(BuiltInRegistries.ITEM.key(), tagLocation);
            Optional<HolderSet.Named<Item>> optional = BuiltInRegistries.ITEM.getTag(key);
            return optional.map(holders -> holders.size() * super.getQualityWeight(luck)).orElseGet(() -> super.getQualityWeight(luck));
        } else {
            return super.getQualityWeight(luck);
        }
    }

    @Override
    protected double calculateAverageCount() {
        return expand ? 1 : count.getAverage();
    }

    public static RenderedTagEntry fromVanilla(TagEntryAccessor entry, boolean inLifoc) {
        return new RenderedTagEntry(entry.getTag(), entry.isExpand(), entry.getWeight(), entry.getQuality(), entry.getConditions(), entry.getFunctions(), inLifoc);
    }

    @Override
    public void render(GuiGraphics graphics, int top, int left, int poolLeft, int width, int transparency, int mouseX, int mouseY, List<RenderedCondition> additionalConditions, List<RenderedFunction> additionalFunctions) {
        super.render(graphics, top, poolLeft, width, transparency, expand ? null : count, getPreviewItem(), mouseX, mouseY, additionalConditions, additionalFunctions);
    }

    @Override
    public LootPoolEntryContainer toVanilla() {
        return TagEntryAccessor.createTagEntry(tag, expand, weight, quality, this.vanillaConditions, this.vanillaFunctions);
    }

    @Override
    public ItemStack getDefaultSymbol() {
        return DEFAULT_SYMBOL;
    }
}
