package net.mesomods.lootwand.network.packet;

import net.mesomods.lootwand.capabilities.ModCapabilities;
import net.mesomods.lootwand.capabilities.NumberProviderTooltipMode;
import net.mesomods.lootwand.client.LootTableWandRenderer;
import net.mesomods.lootwand.client.gui.LootTableViewMode;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class UpdateLootTableWandPlayerDataPacket {
	private final Boolean keybindsShown;
	private final Boolean emptyTargetContainer;
	private final Boolean synchronizeWands;
	private final LootTableViewMode lootTableViewMode;
	private final NumberProviderTooltipMode numberProviderTooltipMode;
	private final Boolean countPreview;
	private final Boolean hideDefaults;
    private final Integer previewTime;
    private final Float luck;

	public UpdateLootTableWandPlayerDataPacket(@Nullable Boolean keybindsShown, @Nullable Boolean emptyTargetContainer, @Nullable Boolean synchronizeWands, @Nullable Integer previewTime, @Nullable LootTableViewMode lootTableViewMode, @Nullable NumberProviderTooltipMode numberProviderTooltipMode, @Nullable Boolean countPreview, @Nullable Boolean hideDefaults, Float luck) {
		this.keybindsShown = keybindsShown;
		this.emptyTargetContainer = emptyTargetContainer;
		this.synchronizeWands = synchronizeWands;
		this.lootTableViewMode = lootTableViewMode;
        this.numberProviderTooltipMode = numberProviderTooltipMode;
        this.previewTime = previewTime;
		this.countPreview = countPreview;
		this.hideDefaults = hideDefaults;
        this.luck = luck;
    }

	public static void encode(UpdateLootTableWandPlayerDataPacket pkt, FriendlyByteBuf buf) {
		buf.writeBoolean(pkt.keybindsShown != null);
		if (pkt.keybindsShown != null)
			buf.writeBoolean(pkt.keybindsShown);
		buf.writeBoolean(pkt.emptyTargetContainer != null);
		if (pkt.emptyTargetContainer != null)
			buf.writeBoolean(pkt.emptyTargetContainer);
		buf.writeBoolean(pkt.synchronizeWands != null);
		if (pkt.synchronizeWands != null)
			buf.writeBoolean(pkt.synchronizeWands);
		buf.writeBoolean(pkt.previewTime != null);
		if (pkt.previewTime != null)
			buf.writeInt(pkt.previewTime);
		buf.writeBoolean(pkt.lootTableViewMode != null);
		if (pkt.lootTableViewMode != null)
			buf.writeEnum(pkt.lootTableViewMode);
		buf.writeBoolean(pkt.numberProviderTooltipMode != null);
		if (pkt.numberProviderTooltipMode != null)
			buf.writeEnum(pkt.numberProviderTooltipMode);
		buf.writeBoolean(pkt.countPreview != null);
		if (pkt.countPreview != null)
			buf.writeBoolean(pkt.countPreview);
		buf.writeBoolean(pkt.hideDefaults != null);
		if (pkt.hideDefaults != null)
			buf.writeBoolean(pkt.hideDefaults);
		buf.writeBoolean(pkt.luck != null);
		if (pkt.luck != null)
			buf.writeFloat(pkt.luck);
	}

	public static UpdateLootTableWandPlayerDataPacket decode(FriendlyByteBuf buf) {
		return new UpdateLootTableWandPlayerDataPacket(buf.readBoolean() ? buf.readBoolean() : null, buf.readBoolean() ? buf.readBoolean() : null, buf.readBoolean() ? buf.readBoolean() : null, buf.readBoolean() ? buf.readInt() : null, buf.readBoolean() ? buf.readEnum(LootTableViewMode.class) : null, buf.readBoolean() ? buf.readEnum(NumberProviderTooltipMode.class) : null, buf.readBoolean() ? buf.readBoolean() : null, buf.readBoolean() ? buf.readBoolean() : null, buf.readBoolean() ? buf.readFloat() : null);
	}

	public static void handle(UpdateLootTableWandPlayerDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
            boolean isServer = ctx.get().getDirection().getReceptionSide().isServer();
			Player player = isServer ? ctx.get().getSender() : Minecraft.getInstance().player;
			if (player != null)
				ModCapabilities.setPlayerData(player, (data -> {
					if (packet.keybindsShown != null)
						data.setKeybindsShown(packet.keybindsShown);
					if (packet.emptyTargetContainer != null)
						data.setEmptyTargetContainer(packet.emptyTargetContainer);
					if (packet.synchronizeWands != null)
						data.setSynchronizeWands(packet.synchronizeWands);
					if (packet.previewTime != null)
						data.setPreviewTime(packet.previewTime);
					if (packet.lootTableViewMode != null)
						data.setLootTableViewMode(packet.lootTableViewMode);
					if (packet.numberProviderTooltipMode != null)
						data.setNumberProviderTooltipMode(packet.numberProviderTooltipMode);
					if (packet.countPreview != null)
						data.setCountPreview(packet.countPreview);
					if (packet.hideDefaults != null)
						data.setHideDefaults(packet.hideDefaults);
					if (packet.luck != null)
						data.setLuck(packet.luck);
					if (!isServer && packet.previewTime != null) {
						LootTableWandRenderer.PREVIEW_CYCLE_TIME = packet.previewTime;
					}
				}));
		});
		ctx.get().setPacketHandled(true);
	}
}
