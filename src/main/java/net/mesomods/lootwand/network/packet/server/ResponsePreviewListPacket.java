package net.mesomods.lootwand.network.packet.server;

import com.mojang.datafixers.util.Pair;
import net.mesomods.lootwand.client.gui.screen.LootTableDataScreen;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.util.PreviewItemCycleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ResponsePreviewListPacket extends ItemPreviewListPacket {
    private final ResourceLocation id;
    private final boolean isTag;

    public ResponsePreviewListPacket(ResourceLocation id, int[] times, ListTag items, boolean isTag) {
        super(times, items);
        this.id = id;
        this.isTag = isTag;
    }

    public static void encode(ResponsePreviewListPacket packet, FriendlyByteBuf buf) {
        buf.writeResourceLocation(packet.id);
        buf.writeBoolean(packet.isTag);
        ItemPreviewListPacket.encodeList(packet, buf);
    }

    public static ResponsePreviewListPacket decode(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        boolean isTag = buf.readBoolean();
        ItemPreviewListPacket packet = ItemPreviewListPacket.decodeList(buf);
        return new ResponsePreviewListPacket(id, packet.times, packet.items, isTag);
    }

    public static void handle(ResponsePreviewListPacket packet, Minecraft mc) {
        mc.execute(() -> {
            Screen activeScreen = mc.screen;
            if (activeScreen == null) return;
            if (activeScreen instanceof LootTableDataScreen dataScreen) {
                dataScreen.acceptPreviewList(packet.id, packet.times, packet.items, packet.isTag);
            }
        });
    }

    public static void sendLootTablePreview(ServerPlayer player, ResourceLocation lootTableLocation) {
        if (player == null) return;
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

        LootTableNetwork.sendToClient(player, new ResponsePreviewListPacket(lootTableLocation, previewTimes, previewItems, false));

    }

    public static void sendTagPreview(ServerPlayer player, ResourceLocation tagLocation) {
        if (player == null) return;
        Pair<int[], ListTag> previewList = PreviewItemCycleManager.getTagPreviewList(player, tagLocation);

        int[] previewTimes;
        ListTag previewItems;
        if (previewList == null) {
            previewTimes = new int[0];
            previewItems = new ListTag();
        } else {
            previewTimes = previewList.getFirst();
            previewItems = previewList.getSecond();
        }

        LootTableNetwork.sendToClient(player, new ResponsePreviewListPacket(tagLocation, previewTimes, previewItems, true));

    }
}
