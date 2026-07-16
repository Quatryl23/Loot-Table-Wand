package net.mesomods.lootwand.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mesomods.lootwand.LootWandMod;
import net.mesomods.lootwand.network.packet.UpdateLootTableWandPlayerDataPacket;
import net.mesomods.lootwand.network.packet.client.*;
import net.mesomods.lootwand.network.packet.server.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class LootTableNetwork {
	public static final ResourceLocation REQUEST_LOOT_TABLES = new ResourceLocation(LootWandMod.MODID, "request_loot_tables_packet");
	public static final ResourceLocation RESPONSE_LOOT_TABLES = new ResourceLocation(LootWandMod.MODID, "response_loot_tables_packet");
	public static final ResourceLocation UPDATE_LOOT_TABLE_WAND = new ResourceLocation(LootWandMod.MODID, "update_loot_table_wand_packet");
	public static final ResourceLocation UPDATE_LOOT_TABLE_WAND_PLAYER_DATA = new ResourceLocation(LootWandMod.MODID, "update_loot_table_wand_player_data");
	public static final ResourceLocation WAND_ITEM_PREVIEW = new ResourceLocation(LootWandMod.MODID, "wand_item_preview");
	public static final ResourceLocation REQUEST_LOOT_TABLE_DATA = new ResourceLocation(LootWandMod.MODID, "request_loot_table_data");
	public static final ResourceLocation RESPONSE_LOOT_TABLE_DATA = new ResourceLocation(LootWandMod.MODID, "response_loot_table_data");
	public static final ResourceLocation REQUEST_PREVIEW_LIST = new ResourceLocation(LootWandMod.MODID, "request_preview_list");
	public static final ResourceLocation RESPONSE_PREVIEW_LIST = new ResourceLocation(LootWandMod.MODID, "response_preview_list");
	public static final ResourceLocation SYNC_CLIENT_CONTAINER_LOOT_TABLE = new ResourceLocation(LootWandMod.MODID, "sync_client_container_loot_table");
	public static final ResourceLocation REQUEST_CLIENT_CONTAINER_LOOT_TABLE_SYNC = new ResourceLocation(LootWandMod.MODID, "request_client_container_loot_table_sync");
	public static final ResourceLocation REQUEST_ITEM_FUNCTION_SIMULATION = new ResourceLocation(LootWandMod.MODID, "request_item_function_simulation");
	public static final ResourceLocation RESPONSE_ITEM_FUNCTION_SIMULATION = new ResourceLocation(LootWandMod.MODID, "response_item_function_simulation");

	public static final PacketRegistry packetTypes = new PacketRegistry();

	public static <T> void sendToClient(ServerPlayer target, T packet) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		PacketType<T> packetType = (PacketType<T>) packetTypes.get(packet.getClass());
		packetType.encoder().accept(packet, buf);
		ServerPlayNetworking.send(target, packetType.id(), buf);
	}

	public static <T> void sendToServer(T packet) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		PacketType<T> packetType = (PacketType<T>) packetTypes.get(packet.getClass());
		packetType.encoder().accept(packet, buf);
		ClientPlayNetworking.send(packetType.id(), buf);
	}


    public static <T> void registerClientPacketType(ResourceLocation id, Class<T> clazz, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, TriConsumer<T, MinecraftServer, ServerPlayer> serverHandler) {
        registerPacketType(id, clazz, encoder, decoder, serverHandler, null);
    }

    public static <T> void registerServerPacketType(ResourceLocation id, Class<T> clazz, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Minecraft> clientHandler) {
        registerPacketType(id, clazz, encoder, decoder, null, clientHandler);
    }

    public static <T> void registerPacketType(ResourceLocation id, Class<T> clazz, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, TriConsumer<T, MinecraftServer, ServerPlayer> serverHandler, BiConsumer<T, Minecraft> clientHandler) {
		PacketType<T> type = new PacketType<>(encoder, decoder, serverHandler, clientHandler, id);
		packetTypes.register(clazz, type);
		if (serverHandler != null) {
			ServerPlayNetworking.registerGlobalReceiver(id, (server, player, packetListener, buf, sender) -> {
				T packet = decoder.apply(buf);
				serverHandler.accept(packet, server, player);
			});
		}
		if (clientHandler != null) {
			ClientPlayNetworking.registerGlobalReceiver(id, (minecraft, packetListener, buf, sender) -> {
				T packet = decoder.apply(buf);
				clientHandler.accept(packet, minecraft);
			});
		}
	}

	public static void registerPackets() {
		registerClientPacketType(REQUEST_CLIENT_CONTAINER_LOOT_TABLE_SYNC, RequestClientContainerLootTableSyncPacket.class, RequestClientContainerLootTableSyncPacket::encode, RequestClientContainerLootTableSyncPacket::decode, RequestClientContainerLootTableSyncPacket::handle);
		registerClientPacketType(REQUEST_ITEM_FUNCTION_SIMULATION, RequestItemFunctionSimulationPacket.class, RequestItemFunctionSimulationPacket::encode, RequestItemFunctionSimulationPacket::decode, RequestItemFunctionSimulationPacket::handle);
		registerClientPacketType(REQUEST_LOOT_TABLE_DATA, RequestLootTableDataPacket.class, RequestLootTableDataPacket::encode, RequestLootTableDataPacket::decode, RequestLootTableDataPacket::handle);
		registerClientPacketType(REQUEST_LOOT_TABLES, RequestLootTablesPacket.class, RequestLootTablesPacket::encode, RequestLootTablesPacket::decode, RequestLootTablesPacket::handle);
		registerClientPacketType(REQUEST_PREVIEW_LIST, RequestPreviewListPacket.class, RequestPreviewListPacket::encode, RequestPreviewListPacket::decode, RequestPreviewListPacket::handle);
		registerClientPacketType(UPDATE_LOOT_TABLE_WAND, UpdateLootTableWandPacket.class, UpdateLootTableWandPacket::encode, UpdateLootTableWandPacket::decode, UpdateLootTableWandPacket::handle);
		registerPacketType(UPDATE_LOOT_TABLE_WAND_PLAYER_DATA, UpdateLootTableWandPlayerDataPacket.class, UpdateLootTableWandPlayerDataPacket::encode, UpdateLootTableWandPlayerDataPacket::decode, UpdateLootTableWandPlayerDataPacket::handleServer, UpdateLootTableWandPlayerDataPacket::handleClient);
		registerServerPacketType(RESPONSE_ITEM_FUNCTION_SIMULATION, ResponseItemFunctionSimulationPacket.class, ResponseItemFunctionSimulationPacket::encode, ResponseItemFunctionSimulationPacket::decode, ResponseItemFunctionSimulationPacket::handle);
		registerServerPacketType(RESPONSE_LOOT_TABLE_DATA, ResponseLootTableDataPacket.class, ResponseLootTableDataPacket::encode, ResponseLootTableDataPacket::decode, ResponseLootTableDataPacket::handle);
		registerServerPacketType(RESPONSE_LOOT_TABLES, ResponseLootTablesPacket.class, ResponseLootTablesPacket::encode, ResponseLootTablesPacket::decode, ResponseLootTablesPacket::handle);
		registerServerPacketType(RESPONSE_PREVIEW_LIST, ResponsePreviewListPacket.class, ResponsePreviewListPacket::encode, ResponsePreviewListPacket::decode, ResponsePreviewListPacket::handle);
		registerServerPacketType(SYNC_CLIENT_CONTAINER_LOOT_TABLE, SyncClientContainerLootTablePacket.class, SyncClientContainerLootTablePacket::encode, SyncClientContainerLootTablePacket::decode, SyncClientContainerLootTablePacket::handle);
		registerServerPacketType(WAND_ITEM_PREVIEW, WandItemPreviewPacket.class, WandItemPreviewPacket::encode, WandItemPreviewPacket::decode, WandItemPreviewPacket::handle);
	}

	public record PacketType<T>(BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, TriConsumer<T, MinecraftServer, ServerPlayer> serverHandler, BiConsumer<T, Minecraft> clientHandler, ResourceLocation id) {
	}

	public static class PacketRegistry {
		private final Map<Class<?>, PacketType<?>> packetTypes = new HashMap<>();

		public <T> void register(Class<T> clazz, PacketType<T> type) {
			packetTypes.put(clazz, type);
		}

		public <T> PacketType<T> get(Class<T> type) {
			return (PacketType<T>) packetTypes.get(type);
		}
	}
}
