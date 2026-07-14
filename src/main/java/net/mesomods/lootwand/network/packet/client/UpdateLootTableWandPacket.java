package net.mesomods.lootwand.network.packet.client;

import net.mesomods.lootwand.LootWandMod;
import net.mesomods.lootwand.item.LootTableWandItem;
import net.mesomods.lootwand.network.packet.server.WandItemPreviewPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public class UpdateLootTableWandPacket {
    private final UpdateMode mode;
	private final int slot;
    private final int index;
	@Nullable private final String rl;
    private final UpdatePreviewData previewData;

	public UpdateLootTableWandPacket(int slot, int index, UpdateMode mode, @Nullable String rl, UpdatePreviewData data) {
		this.slot = slot;
        this.index = index;
        this.mode = mode;
        this.rl = rl;
        this.previewData = data;
	}
    public UpdateLootTableWandPacket(int slot, int index, UpdateMode mode, @Nullable String rl) {
        this(slot, index, mode, rl, UpdatePreviewData.empty());
    }

    public static UpdateLootTableWandPacket activateTable(int slot, int index) {
        return new UpdateLootTableWandPacket(slot, index, UpdateMode.ACTIVATE_TABLE, null);
    }

    public static UpdateLootTableWandPacket setTable(int slot, int index, String string) {
        return new UpdateLootTableWandPacket(slot, index, UpdateMode.SET_TABLE, string);
    }

    public static UpdateLootTableWandPacket removeTable(int slot, int index) {
        return new UpdateLootTableWandPacket(slot, index, UpdateMode.REMOVE_TABLE, null);
    }

    public static UpdateLootTableWandPacket updatePreview(int slot, int previewStart, int previewEnd, String previewItem) {
        return new UpdateLootTableWandPacket(slot, -1, UpdateMode.UPDATE_PREVIEW, null, new UpdatePreviewData(previewStart, previewEnd, previewItem));
    }

    public static UpdateLootTableWandPacket updatePreview(int slot) {
        return new UpdateLootTableWandPacket(slot, -1, UpdateMode.UPDATE_PREVIEW, null, UpdatePreviewData.empty());
    }

    public static UpdateLootTableWandPacket toggleDeletionMode(int slot) {
        return new UpdateLootTableWandPacket(slot, -1, UpdateMode.TOGGLE_DELETION_MODE, null);
    }

	public static void encode(UpdateLootTableWandPacket pkt, FriendlyByteBuf buf) {
		buf.writeInt(pkt.slot);
        buf.writeInt(pkt.index);
        buf.writeEnum(pkt.mode);
		buf.writeOptional(Optional.ofNullable(pkt.rl), FriendlyByteBuf::writeUtf);
        pkt.previewData.encode(buf);
	}

	public static UpdateLootTableWandPacket decode(FriendlyByteBuf buf) {
		return new UpdateLootTableWandPacket(buf.readInt(), buf.readInt(), buf.readEnum(UpdateMode.class), buf.readOptional(FriendlyByteBuf::readUtf).orElse(null), UpdatePreviewData.decode(buf));
	}

	public static void handle(UpdateLootTableWandPacket packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) return;
			Player player = ctx.get().getSender();
			if (player != null) {
				 ItemStack stack  = player.getInventory().getItem(packet.slot);
                 if (stack.getItem() != LootWandMod.LOOT_TABLE_WAND.get()) return;
                 switch (packet.mode) {
                     case ACTIVATE_TABLE -> {
                         LootTableWandItem.setActiveLootTableWandIndexServer(stack, packet.index, player, ctx.get());
                     }
                     case SET_TABLE -> {
                         if (packet.rl == null) {
                             LootTableWandItem.removeLootTable(player, stack, packet.index, false);
                         } else {
                             LootTableWandItem.setLootTable(player, stack, packet.index, packet.rl, false);
                         }
                         WandItemPreviewPacket.sendPreviewListUpdate(packet.slot, LootTableWandItem.getActiveLootTable(stack), ctx.get());
                     }
                     case REMOVE_TABLE -> {
                         LootTableWandItem.removeLootTable(player, stack, packet.index, false);
                         WandItemPreviewPacket.sendEmptyPreviewListUpdate(packet.slot, ctx.get());
                     }
                     case UPDATE_PREVIEW -> {
                         LootTableWandItem.updatePreview(stack, packet.previewData);
                     }
                     case TOGGLE_DELETION_MODE -> {
                        if (LootTableWandItem.toggleDeletionMode(player, stack, false)) {
                            WandItemPreviewPacket.sendEmptyPreviewListUpdate(packet.slot, ctx.get());
                        } else {
                            WandItemPreviewPacket.sendPreviewListUpdate(packet.slot, LootTableWandItem.getActiveLootTable(stack), ctx.get());
                        }
                     }
                 }
			}
		});
		ctx.get().setPacketHandled(true);
	}

    public enum UpdateMode {
        ACTIVATE_TABLE,
        SET_TABLE,
        REMOVE_TABLE,
        UPDATE_PREVIEW,
        TOGGLE_DELETION_MODE
    }

    public record UpdatePreviewData(int previewStart, int previewEnd, String previewItem) {
        public static UpdatePreviewData empty() {
            return new UpdatePreviewData(0, 0, "");
        }
        public boolean isEmpty() {
            return previewItem.isEmpty();
        }
        public void encode(FriendlyByteBuf buf) {
            buf.writeInt(previewStart);
            buf.writeInt(previewEnd);
            buf.writeUtf(previewItem);
        }
        public static UpdatePreviewData decode(FriendlyByteBuf buf) {
            return new UpdatePreviewData(buf.readInt(), buf.readInt(), buf.readUtf());
        }
    }
}
