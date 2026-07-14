package net.mesomods.lootwand.network.packet.server;

import net.mesomods.lootwand.util.LootContextManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ResponseItemFunctionSimulationPacket(int id, ItemStack stack) {

    public static void encode(ResponseItemFunctionSimulationPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.id);
        buf.writeItem(packet.stack);
    }

    public static ResponseItemFunctionSimulationPacket decode(FriendlyByteBuf buf) {
        return new ResponseItemFunctionSimulationPacket(buf.readInt(), buf.readItem());
    }

    public static void handle(ResponseItemFunctionSimulationPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            LootContextManager.respondModifiedItem(packet.id, packet.stack);
        });
        context.setPacketHandled(true);
    }
}
