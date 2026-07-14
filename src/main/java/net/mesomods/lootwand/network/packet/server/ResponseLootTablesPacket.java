package net.mesomods.lootwand.network.packet.server;

import net.mesomods.lootwand.client.gui.screen.LootTableBrowsingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public record ResponseLootTablesPacket(List<ResourceLocation> lootTables) {

	public static void encode(ResponseLootTablesPacket pkt, FriendlyByteBuf buf) {
		buf.writeCollection(pkt.lootTables, FriendlyByteBuf::writeResourceLocation);
	}

	public static ResponseLootTablesPacket decode(FriendlyByteBuf buf) {
		return new ResponseLootTablesPacket(buf.readList(FriendlyByteBuf::readResourceLocation));
	}

	public static void handle(ResponseLootTablesPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().screen instanceof LootTableBrowsingScreen screen) {
				screen.initWithLootTableData(pkt.lootTables);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
