package net.mesomods.lootwand.network.packet.server;

import com.mojang.datafixers.util.Pair;
import net.mesomods.lootwand.ModItems;
import net.mesomods.lootwand.item.LootTableWandItem;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.util.PreviewItemCycleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class WandItemPreviewPacket extends ItemPreviewListPacket {
    private final int slot;

    public WandItemPreviewPacket(int slot, int[] times, ListTag items) {
        super(times, items);
        this.slot = slot;
    }

    public static void encode(WandItemPreviewPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.slot);
        ItemPreviewListPacket.encodeList(packet, buf);
    }

    public static WandItemPreviewPacket decode(FriendlyByteBuf buf) {
        int slot = buf.readInt();
        ItemPreviewListPacket packet = ItemPreviewListPacket.decodeList(buf);
        return new WandItemPreviewPacket(slot, packet.times, packet.items);
    }

    public static void handle(WandItemPreviewPacket packet, Minecraft mc) {
        mc.execute(() -> {
            Player player = mc.player;
            if (player != null) {
                ItemStack stack = player.getInventory().getItem(packet.slot);
                if (stack.getItem() != ModItems.LOOT_TABLE_WAND) return;
                LootTableWandItem.setPreviewList(stack, packet.times, packet.items);
            }
        });
    }

    public static void sendEmptyPreviewListUpdate(int slot, ServerPlayer player) {
        LootTableWandItem.setPreviewList(player.getInventory().getItem(slot), new int[0], new ListTag());
        LootTableNetwork.sendToClient(player, new WandItemPreviewPacket(slot, new int[0], new ListTag()));
    }

    public static void sendPreviewListUpdate(ServerPlayer player) {
        NonNullList<ItemStack> inventory = player.getInventory().items;
        inventory.forEach(item -> {
            if (item.is(ModItems.LOOT_TABLE_WAND)) {
                sendPreviewListUpdate(inventory.indexOf(item), LootTableWandItem.getActiveLootTable(item), player);
            }
        });
    }

    public static void sendPreviewListUpdate(int slot, ResourceLocation lootTableLocation, ServerPlayer player) {
        if (player == null) return;
        ItemStack wand = player.getInventory().getItem(slot);
        if (wand.getItem() != ModItems.LOOT_TABLE_WAND) return;
        Pair<int[], ListTag> previewList = PreviewItemCycleManager.getLootTablePreviewList(player, lootTableLocation);

        int[] previewTimes;
        ListTag previewItems;
        if (previewList == null) {
            previewTimes = new int[0];
            previewItems = new ListTag();
        } else {
            previewTimes = previewList.getFirst();
            previewItems = previewList.getSecond();
        }

        LootTableWandItem.setPreviewList(wand, previewTimes, previewItems);
        LootTableNetwork.sendToClient(player, new WandItemPreviewPacket(slot, previewTimes, previewItems));
    }
}
