package net.mesomods.lootwand.network.packet.client;

import net.mesomods.lootwand.container.RandomizableContainerBlockEntityAccessor;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.server.SyncClientContainerLootTablePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public record RequestClientContainerLootTableSyncPacket(BlockPos pos) {

	public static void encode(RequestClientContainerLootTableSyncPacket pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
	}

	public static RequestClientContainerLootTableSyncPacket decode(FriendlyByteBuf buf) {
		return new RequestClientContainerLootTableSyncPacket(buf.readBlockPos());
	}

	public static void handle(RequestClientContainerLootTableSyncPacket pkt, MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
			if (player != null) {
				BlockPos pos = pkt.pos;
				BlockEntity be = player.level().getBlockEntity(pos);
				if (be instanceof RandomizableContainerBlockEntityAccessor container) {
					LootTableNetwork.sendToClient(player, new SyncClientContainerLootTablePacket(pos, container.getLootTable(), container.getLootTableSeed()));
				}
			}
		});
	}
}
