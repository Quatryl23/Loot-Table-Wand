package net.mesomods.lootwand.network.packet.server;

import com.mojang.datafixers.util.Pair;
import net.mesomods.lootwand.LootWandMod;
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
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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

    public static void handle(WandItemPreviewPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isServer()) return;
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                ItemStack stack = player.getInventory().getItem(packet.slot);
                if (stack.getItem() != LootWandMod.LOOT_TABLE_WAND.get()) return;
                LootTableWandItem.setPreviewList(stack, packet.times, packet.items);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void sendEmptyPreviewListUpdate(int slot, NetworkEvent.Context context) {
        sendEmptyPreviewListUpdate(slot, context.getSender());
    }

    public static void sendEmptyPreviewListUpdate(int slot, ServerPlayer player) {
        LootTableWandItem.setPreviewList(player.getInventory().getItem(slot), new int[0], new ListTag());
        LootTableNetwork.CHANNEL.sendTo(new WandItemPreviewPacket(slot, new int[0], new ListTag()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendPreviewListUpdate(ServerPlayer player) {
        NonNullList<ItemStack> inventory = player.getInventory().items;
        inventory.forEach(item -> {
            if (item.is(LootWandMod.LOOT_TABLE_WAND.get())) {
                sendPreviewListUpdate(inventory.indexOf(item), LootTableWandItem.getActiveLootTable(item), player);
            }
        });
    }

    public static void sendPreviewListUpdate(int slot, ResourceLocation lootTableLocation, NetworkEvent.Context context) {
        sendPreviewListUpdate(slot, lootTableLocation, context.getSender());
    }

    public static void sendPreviewListUpdate(int slot, ResourceLocation lootTableLocation, ServerPlayer player) {
        if (player == null) return;
        ItemStack wand = player.getInventory().getItem(slot);
        if (wand.getItem() != LootWandMod.LOOT_TABLE_WAND.get()) return;
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
        LootTableNetwork.CHANNEL.sendTo(new WandItemPreviewPacket(slot, previewTimes, previewItems), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
