package net.mesomods.lootwand.loot.poolentry;

import net.mesomods.lootwand.loot.lifoc.RenderedCondition;
import net.mesomods.lootwand.loot.lifoc.RenderedFunction;
import net.mesomods.lootwand.loot.numbers.NumberProvider;
import net.mesomods.lootwand.mixin.loot.entry.TagEntryAccessor;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.client.RequestPreviewListPacket;
import net.minecraft.client.gui.GuiGraphics;
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
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.List;

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
        LootTableNetwork.CHANNEL.sendToServer(new RequestPreviewListPacket(tagLocation, true));
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
            this.previewItems = previewItems.stream().map(tag -> ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(tag.getAsString()))).map(item -> item == null ? ItemStack.EMPTY : item.getDefaultInstance()).toList();
            this.previewReady = true;
        }
    }

    @Override
    public int getWeight() {
        if (expand) {
            ITagManager<Item> tagManager = ForgeRegistries.ITEMS.tags();
            if (tagManager == null) return super.getWeight();
            return tagManager.getTag(tagManager.createTagKey(tagLocation)).size() * super.getWeight();
        } else {
           return super.getWeight();
        }
    }

    @Override
    public int getQuality() {
        if (expand) {
            ITagManager<Item> tagManager = ForgeRegistries.ITEMS.tags();
            if (tagManager == null) return super.getQuality();
            return tagManager.getTag(tagManager.createTagKey(tagLocation)).size() * super.getQuality();
        } else {
            return super.getQuality();
        }
    }

    @Override
    public int getQualityWeight(double luck) {
        if (expand) {
            ITagManager<Item> tagManager = ForgeRegistries.ITEMS.tags();
            if (tagManager == null) return super.getQualityWeight(luck);
            return tagManager.getTag(tagManager.createTagKey(tagLocation)).size() * super.getQualityWeight(luck);
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
