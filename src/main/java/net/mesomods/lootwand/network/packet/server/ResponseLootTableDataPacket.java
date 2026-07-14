package net.mesomods.lootwand.network.packet.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mesomods.lootwand.client.gui.screen.LootTableDataScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ResponseLootTableDataPacket(JsonObject json) {

    public static void encode(ResponseLootTableDataPacket packet, FriendlyByteBuf buf) {
        boolean validJson = packet.json != null;
        buf.writeBoolean(validJson);
        if (validJson) {
            buf.writeUtf(packet.json.toString());
        }
    }

    public static ResponseLootTableDataPacket decode(FriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            return new ResponseLootTableDataPacket(JsonParser.parseString(buf.readUtf()).getAsJsonObject());
        } else {
            return new ResponseLootTableDataPacket(null);
        }
    }

    public static void handle(ResponseLootTableDataPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (Minecraft.getInstance().screen instanceof LootTableDataScreen screen) {
                screen.initWithJson(packet.json);
            }
        });
        context.get().setPacketHandled(true);
    }


}
