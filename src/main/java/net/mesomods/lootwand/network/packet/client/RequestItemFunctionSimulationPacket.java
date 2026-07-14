package net.mesomods.lootwand.network.packet.client;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.server.ResponseItemFunctionSimulationPacket;
import net.mesomods.lootwand.util.LootContextManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record RequestItemFunctionSimulationPacket(int id, ItemStack stack, List<LootItemFunction> functions) {
    public static final Gson FUNCTION_SERIALIZER = Deserializers.createFunctionSerializer().create();

    public static void encode(RequestItemFunctionSimulationPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.id);
        buf.writeItem(packet.stack);
        buf.writeInt(packet.functions.size());
        for (LootItemFunction function : packet.functions) {
            try {
                buf.writeUtf(FUNCTION_SERIALIZER.toJson(function));
            } catch (NullPointerException e) {
                buf.writeUtf(FUNCTION_SERIALIZER.toJson(LootItemFunctions.IDENTITY));
            }
        }
    }

    public static RequestItemFunctionSimulationPacket decode(FriendlyByteBuf buf) {
        int id = buf.readInt();
        ItemStack stack = buf.readItem();
        int size = buf.readInt();
        List<LootItemFunction> functions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            try {
                functions.add(FUNCTION_SERIALIZER.fromJson(buf.readUtf(), LootItemFunction.class));
            } catch (NullPointerException | JsonSyntaxException ignored) {
            }
        }
        return new RequestItemFunctionSimulationPacket(id, stack, functions);
    }

    public static void handle(RequestItemFunctionSimulationPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> LootTableNetwork.CHANNEL.sendTo(new ResponseItemFunctionSimulationPacket(packet.id, LootContextManager.simulateLootItemFunctionsServer(packet.stack, packet.functions)), context.getNetworkManager(), context.getDirection().reply()));
        context.setPacketHandled(true);
    }
}
