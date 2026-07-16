package net.mesomods.lootwand.network.packet.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.server.ResponseLootTableDataPacket;
import net.mesomods.lootwand.util.LootContextManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootTable;

import java.io.IOException;
import java.util.Optional;

public record RequestLootTableDataPacket(ResourceLocation tableId) {
    public static final Gson GSON = new Gson();

    public static void encode(RequestLootTableDataPacket packet, FriendlyByteBuf buf) {
        buf.writeResourceLocation(packet.tableId);
    }

    public static RequestLootTableDataPacket decode(FriendlyByteBuf buf) {
        return new RequestLootTableDataPacket(buf.readResourceLocation());
    }

    public static void handle(RequestLootTableDataPacket packet, MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (player == null) return;
            ResourceLocation fullLocation = new ResourceLocation(packet.tableId.getNamespace(), "loot_tables/" + packet.tableId.getPath() + ".json");
            Optional<Resource> resource = player.server.getResourceManager().getResource(fullLocation);
            if (resource.isPresent()) {
                try {
                    JsonObject json = GsonHelper.fromJson(GSON, resource.get().openAsReader(), JsonElement.class).getAsJsonObject();
                    LootTableNetwork.sendToClient(player, new ResponseLootTableDataPacket(json));
                    LootTable table = Deserializers.createLootTableSerializer().create().fromJson(json, LootTable.class);
                    LootContextManager.updateContext(player.serverLevel(), table, player);
                } catch (IOException | JsonParseException | IllegalArgumentException e) {
                    LootTableNetwork.sendToClient(player, new ResponseLootTableDataPacket(null));
                }
            } else {
                LootTableNetwork.sendToClient(player, new ResponseLootTableDataPacket(null));
            }
        });
    }
}
