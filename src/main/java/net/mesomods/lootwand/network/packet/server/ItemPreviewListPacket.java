package net.mesomods.lootwand.network.packet.server;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Items;

public class ItemPreviewListPacket {
    protected final int[] times;
    protected final ListTag items;

    public ItemPreviewListPacket(int[] times, ListTag items) {
        this.times = times;
        this.items = items;
    }

    public static void encodeList(ItemPreviewListPacket packet, FriendlyByteBuf buf) {
        buf.writeVarIntArray(packet.times);
        buf.writeInt(packet.items.size());
        for (Tag tag : packet.items) {
            if (tag instanceof StringTag stringTag) {
                buf.writeUtf(stringTag.getAsString());
            } else {
                buf.writeUtf(BuiltInRegistries.ITEM.getKey(Items.AIR).toString());
            }
        }
    }

    public static ItemPreviewListPacket decodeList(FriendlyByteBuf buf) {
        int[] times = buf.readVarIntArray();
        int size = buf.readInt();
        ListTag items = new ListTag();
        for (int i = 0; i < size; i++) {
            items.add(StringTag.valueOf(buf.readUtf()));
        }
        return new ItemPreviewListPacket(times, items);
    }
}
