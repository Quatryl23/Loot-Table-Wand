package net.mesomods.lootwand.network.packet.client;

import net.mesomods.lootwand.container.RandomizableContainerBlockEntityAccessor;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.server.SyncClientContainerLootTablePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record RequestClientContainerLootTableSyncPacket(BlockPos pos) {

	public static void encode(RequestClientContainerLootTableSyncPacket pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
	}

	public static RequestClientContainerLootTableSyncPacket decode(FriendlyByteBuf buf) {
		return new RequestClientContainerLootTableSyncPacket(buf.readBlockPos());
	}

	public static void handle(RequestClientContainerLootTableSyncPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();
			if (player != null) {
				BlockPos pos = pkt.pos;
				BlockEntity be = player.level().getBlockEntity(pos);
				if (be instanceof RandomizableContainerBlockEntityAccessor container) {
					LootTableNetwork.CHANNEL.sendTo(new SyncClientContainerLootTablePacket(pos, container.getLootTable(), container.getLootTableSeed()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
