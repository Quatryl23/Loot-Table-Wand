package net.mesomods.lootwand.network.packet.server;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

public record SyncClientContainerLootTablePacket(BlockPos containerPos, ResourceLocation lootTable, long seed) {

    public static void encode(SyncClientContainerLootTablePacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.containerPos);
        buf.writeBoolean(pkt.lootTable == null);
        if (pkt.lootTable != null) buf.writeResourceLocation(pkt.lootTable);
        buf.writeLong(pkt.seed);
    }

    public static SyncClientContainerLootTablePacket decode(FriendlyByteBuf buf) {
        return new SyncClientContainerLootTablePacket(buf.readBlockPos(), buf.readBoolean() ? null : buf.readResourceLocation(), buf.readLong());
    }

    public static void handle(SyncClientContainerLootTablePacket pkt, Minecraft mc) {
        mc.execute(() -> {
            ClientLevel level = mc.level;
            if (level == null) return;
            BlockEntity be = level.getBlockEntity(pkt.containerPos);
            if (be instanceof RandomizableContainerBlockEntity container) {
                container.setLootTable(pkt.lootTable, pkt.seed);
                container.setChanged();
            }
        });
    }
}
