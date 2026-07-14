package net.mesomods.lootwand.network.packet.client;

import net.mesomods.lootwand.network.packet.server.ResponsePreviewListPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @param isTag true: tag, false: loot table
 */
public record RequestPreviewListPacket(ResourceLocation target, boolean isTag) {

	public static void encode(RequestPreviewListPacket pkt, FriendlyByteBuf buf) {
		buf.writeResourceLocation(pkt.target);
		buf.writeBoolean(pkt.isTag);
	}

	public static RequestPreviewListPacket decode(FriendlyByteBuf buf) {
		return new RequestPreviewListPacket(buf.readResourceLocation(), buf.readBoolean());
	}

	public static void handle(RequestPreviewListPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();
			if (player != null) {
				if (pkt.isTag) {
					ResponsePreviewListPacket.sendTagPreview(player, pkt.target);
				} else {
					ResponsePreviewListPacket.sendLootTablePreview(player, pkt.target);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
