package net.mesomods.lootwand.network.packet.server;

import net.mesomods.lootwand.client.gui.screen.LootTableBrowsingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record ResponseLootTablesPacket(List<ResourceLocation> lootTables) {

	public static void encode(ResponseLootTablesPacket pkt, FriendlyByteBuf buf) {
		buf.writeCollection(pkt.lootTables, FriendlyByteBuf::writeResourceLocation);
	}

	public static ResponseLootTablesPacket decode(FriendlyByteBuf buf) {
		return new ResponseLootTablesPacket(buf.readList(FriendlyByteBuf::readResourceLocation));
	}

	public static void handle(ResponseLootTablesPacket pkt, Minecraft mc) {
		mc.execute(() -> {
			if (mc.screen instanceof LootTableBrowsingScreen screen) {
				screen.initWithLootTableData(pkt.lootTables);
			}
		});
	}
}
