package net.mesomods.lootwand.network.packet.client;

import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.server.ResponseLootTablesPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.Map;

public record RequestLootTablesPacket() {
	public static void encode(RequestLootTablesPacket pkt, FriendlyByteBuf buf) {
	}

	public static RequestLootTablesPacket decode(FriendlyByteBuf buf) {
		return new RequestLootTablesPacket();
	}

	public static void handle(RequestLootTablesPacket pkt, MinecraftServer server, ServerPlayer player) {
		server.execute(() -> {
			if (player != null) {
				ResourceManager manager = server.getResourceManager();
				Map<ResourceLocation, Resource> resources = manager.listResources("loot_tables", rl -> rl.getPath().endsWith(".json"));
				LootTableNetwork.sendToClient(player, new ResponseLootTablesPacket(new ArrayList<>(resources.keySet())));
			}
		});

	}
}
