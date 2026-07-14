package net.mesomods.lootwand.network;

import net.mesomods.lootwand.network.packet.UpdateLootTableWandPlayerDataPacket;
import net.mesomods.lootwand.network.packet.client.*;
import net.mesomods.lootwand.network.packet.server.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class LootTableNetwork {
    public static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath("loot_table_wand", "network"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static void registerPackets(FMLCommonSetupEvent event) {
		CHANNEL.registerMessage(0, RequestLootTablesPacket.class, RequestLootTablesPacket::encode, RequestLootTablesPacket::decode, RequestLootTablesPacket::handle);
		CHANNEL.registerMessage(1, ResponseLootTablesPacket.class, ResponseLootTablesPacket::encode, ResponseLootTablesPacket::decode, ResponseLootTablesPacket::handle);
		CHANNEL.registerMessage(2, UpdateLootTableWandPacket.class, UpdateLootTableWandPacket::encode, UpdateLootTableWandPacket::decode, UpdateLootTableWandPacket::handle);
		CHANNEL.registerMessage(3, UpdateLootTableWandPlayerDataPacket.class, UpdateLootTableWandPlayerDataPacket::encode, UpdateLootTableWandPlayerDataPacket::decode, UpdateLootTableWandPlayerDataPacket::handle);
        CHANNEL.registerMessage(4, WandItemPreviewPacket.class, WandItemPreviewPacket::encode, WandItemPreviewPacket::decode, WandItemPreviewPacket::handle);
        CHANNEL.registerMessage(5, RequestLootTableDataPacket.class, RequestLootTableDataPacket::encode, RequestLootTableDataPacket::decode, RequestLootTableDataPacket::handle);
        CHANNEL.registerMessage(6, ResponseLootTableDataPacket.class, ResponseLootTableDataPacket::encode, ResponseLootTableDataPacket::decode, ResponseLootTableDataPacket::handle);
		CHANNEL.registerMessage(7, RequestPreviewListPacket.class, RequestPreviewListPacket::encode, RequestPreviewListPacket::decode, RequestPreviewListPacket::handle);
		CHANNEL.registerMessage(8, ResponsePreviewListPacket.class, ResponsePreviewListPacket::encode, ResponsePreviewListPacket::decode, ResponsePreviewListPacket::handle);
    	CHANNEL.registerMessage(9, SyncClientContainerLootTablePacket.class, SyncClientContainerLootTablePacket::encode, SyncClientContainerLootTablePacket::decode, SyncClientContainerLootTablePacket::handle);
		CHANNEL.registerMessage(10, RequestClientContainerLootTableSyncPacket.class, RequestClientContainerLootTableSyncPacket::encode, RequestClientContainerLootTableSyncPacket::decode, RequestClientContainerLootTableSyncPacket::handle);
		CHANNEL.registerMessage(11, RequestItemFunctionSimulationPacket.class, RequestItemFunctionSimulationPacket::encode, RequestItemFunctionSimulationPacket::decode, RequestItemFunctionSimulationPacket::handle);
		CHANNEL.registerMessage(12, ResponseItemFunctionSimulationPacket.class, ResponseItemFunctionSimulationPacket::encode, ResponseItemFunctionSimulationPacket::decode, ResponseItemFunctionSimulationPacket::handle);
	}
}
