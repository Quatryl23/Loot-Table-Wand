package net.mesomods.lootwand.network.packet.client;

import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.server.ResponseLootTablesPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

public record RequestLootTablesPacket() {
	public static void encode(RequestLootTablesPacket pkt, FriendlyByteBuf buf) {
	}

	public static RequestLootTablesPacket decode(FriendlyByteBuf buf) {
		return new RequestLootTablesPacket();
	}

	public static void handle(RequestLootTablesPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();
			if (player != null) {
				ResourceManager manager = player.getServer().getResourceManager();
				Map<ResourceLocation, Resource> resources = manager.listResources("loot_tables", rl -> rl.getPath().endsWith(".json"));
				LootTableNetwork.CHANNEL.sendTo(new ResponseLootTablesPacket(new ArrayList<>(resources.keySet())), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
