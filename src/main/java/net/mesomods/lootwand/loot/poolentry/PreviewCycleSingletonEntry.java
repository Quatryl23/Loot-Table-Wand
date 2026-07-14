package net.mesomods.lootwand.loot.poolentry;

import net.mesomods.lootwand.client.LootTableWandRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public abstract class PreviewCycleSingletonEntry extends RenderedSingletonEntry {
    List<ItemStack> previewItems;
    ItemStack currentItem;
    int previewTimeStart;
    int previewTimeEnd;
    int[] previewTimes;
    boolean previewReady = false;

    public PreviewCycleSingletonEntry(LootItemCondition[] conditions, LootItemFunction[] functions, int weight, int quality, boolean inLifoc) {
        super(conditions, functions, weight, quality, inLifoc);
    }

    public ItemStack getPreviewItem() {
        int previewCycleTime = LootTableWandRenderer.PREVIEW_CYCLE_TIME;
        if (previewCycleTime == -1 || !previewReady) return getDefaultSymbol();
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return getDefaultSymbol();
        int timeInCycle = (int) (System.currentTimeMillis() / 50) % previewCycleTime;
        int timeInCyclePerMillion = (previewCycleTime == -2) ? 999_999 : Math.round(timeInCycle * 1_000_000F / (float) previewCycleTime);
        if (previewItems.isEmpty()) {
            return getDefaultSymbol();
        }
        if (currentItem != null && timeInCyclePerMillion > this.previewTimeStart && timeInCyclePerMillion < this.previewTimeEnd) {
            return currentItem;
        } else {
            for (int i = 0; i < previewTimes.length; i++) {
                if (timeInCyclePerMillion < previewTimes[i]) {
                    int startTime = i == 0 ? 0 : previewTimes[i - 1];
                    int endTime = previewTimes[i];
                    this.previewTimeStart = startTime;
                    this.previewTimeEnd = endTime;
                    this.currentItem = previewItems.get(i);

                    return currentItem;
                }
            }
        }
        return getDefaultSymbol();
    }

    @Override
    public boolean isNothing() {
        return false;
    }

    public abstract ItemStack getDefaultSymbol();
}
