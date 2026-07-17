package net.mesomods.lootwand.util;

import com.mojang.datafixers.util.Pair;
import net.mesomods.lootwand.client.gui.loottable.numbers.NumberProvider;
import net.mesomods.lootwand.mixin.loot.LootPoolAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PreviewItemCycleManager {
    @Nullable
    public static Pair<int[], ListTag> getLootTablePreviewList(ServerPlayer player, ResourceLocation lootTableLocation) {
        if (player == null) return null;
        ServerLevel level = (ServerLevel) player.level();
        LootTable table = level.getServer().getLootData().getElement(LootDataType.TABLE, lootTableLocation);
        if (table == null) {
            return null;
        }
        LootPool[] pools = table.pools;
        LootContext lootContext = new LootContext.Builder(LootContextManager.buildFakeParams(level, table, player)).create(null);
        List<Integer> previewTimes = new ArrayList<>();
        ListTag previewItems = new ListTag();
        int totalCycleTime = 0;
        List<Integer> cumulativePoolRolls = new ArrayList<>();
        for (LootPool pool : pools) {
            int rollSum = cumulativePoolRolls.isEmpty() ? 0 : cumulativePoolRolls.get(cumulativePoolRolls.size() - 1);
            NumberProvider rolls = NumberProvider.fromVanilla(pool.rolls);
            cumulativePoolRolls.add(Math.round(rollSum + (rolls == null ? pool.rolls.getInt(lootContext) : rolls.getAverage())));
        }
        if (cumulativePoolRolls.isEmpty()) {
            return null;
        }
        int totalRolls = cumulativePoolRolls.get(cumulativePoolRolls.size() - 1);
        float ticksPerRoll = (float) 1_000_000 / (float) totalRolls;
        int[] cumulativePoolTicks = cumulativePoolRolls.stream().mapToInt(rolls -> Math.round(rolls * ticksPerRoll)).toArray();
        int i = -1;
        for (LootPool pool : pools) {
            i++;
            LootPoolEntryContainer[] entries = ((LootPoolAccessor) pool).getEntries();
            AtomicInteger totalWeight = new AtomicInteger();
            HashMap<Integer, ResourceLocation> itemMap = new LinkedHashMap<>();
            for (LootPoolEntryContainer entryContainer : entries) {
                entryContainer.expand(lootContext, (entry) -> {
                    entry.createItemStack((stack) -> {
                        totalWeight.addAndGet(entry.getWeight(0));
                        itemMap.put(totalWeight.get(), BuiltInRegistries.ITEM.getKey(stack.getItem()));
                    }, lootContext);
                });
            }
            if (totalWeight.get() == 0) continue;
            int poolTicks = cumulativePoolTicks[i] - totalCycleTime;
            float ticksPerWeight = (float) poolTicks / (float) totalWeight.get();
            for (HashMap.Entry<Integer, ResourceLocation> entry : itemMap.entrySet()) {
                int poolLocalPreviewTime = Math.round(entry.getKey() * ticksPerWeight);
                previewTimes.add(totalCycleTime + poolLocalPreviewTime);
                previewItems.add(StringTag.valueOf(entry.getValue().toString()));
            }
            totalCycleTime += cumulativePoolTicks[i];
        }
        return Pair.of(previewTimes.stream().mapToInt(Integer::intValue).toArray(), previewItems);
    }

    @Nullable
    public static Pair<int[], ListTag> getTagPreviewList(ServerPlayer player, ResourceLocation tagLocation) {
        if (player == null) return null;
        Optional<HolderSet.Named<Item>> tag = BuiltInRegistries.ITEM.getTag(TagKey.create(BuiltInRegistries.ITEM.key(), tagLocation));
        if (tag.isEmpty()) return null;
        List<Item> items = tag.get().stream().map(Holder::value).toList();
        if (items.isEmpty()) return null;
        float ticksPerItem = (float) 1_000_000 / (float) items.size();
        List<Integer> previewTimes = new ArrayList<>();
        ListTag previewItems = new ListTag();
        int i = 0;
        for (Item item : items) {
            i++;
            previewTimes.add(Math.round(i * ticksPerItem));
            ResourceLocation itemLocation = BuiltInRegistries.ITEM.getKey(item);
            previewItems.add(StringTag.valueOf(itemLocation.toString()));
        }
        return Pair.of(previewTimes.stream().mapToInt(Integer::intValue).toArray(), previewItems);
    }
}
