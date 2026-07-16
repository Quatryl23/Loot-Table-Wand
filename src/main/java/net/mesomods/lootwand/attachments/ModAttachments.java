package net.mesomods.lootwand.attachments;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.mesomods.lootwand.LootWandMod;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.UpdateLootTableWandPlayerDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;
import java.util.function.Function;

public class ModAttachments {
    public static final AttachmentType<LootTableWandPlayerData> LOOT_TABLE_WAND_PLAYER_DATA = AttachmentRegistry.<LootTableWandPlayerData>builder()
            .initializer(() -> LootTableWandPlayerData.DEFAULT_INSTANCE)
            .persistent(LootTableWandPlayerData.CODEC)
            .copyOnDeath()
            .buildAndRegister(new ResourceLocation(LootWandMod.MODID, "player_data"));

    public static void initialize() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            if (joined) {
                LootTableWandPlayerData d = player.getAttachedOrCreate(ModAttachments.LOOT_TABLE_WAND_PLAYER_DATA);
                LootTableNetwork.sendToClient(player, new UpdateLootTableWandPlayerDataPacket(
                        d.getKeybindsShown(),
                        d.getEmptyTargetContainer(),
                        d.getSynchronizeWands(),
                        d.getPreviewTime(),
                        d.getLootTableViewMode(),
                        d.getNumberProviderTooltipMode(),
                        d.getCountPreview(),
                        d.getHideDefaults(),
                        d.getLuck()
                ));
            }
        });
    }

    public static <T> T getPlayerData(Player player, Function<LootTableWandPlayerData, T> getter, T defaultValue) {
        LootTableWandPlayerData data = player.getAttachedOrCreate(ModAttachments.LOOT_TABLE_WAND_PLAYER_DATA);
        if (data == null) return defaultValue;
        T result = getter.apply(data);
        if (result == null) return defaultValue;
        return result;
    }

    public static void setPlayerData(Player player, Consumer<LootTableWandPlayerData> modifier) {
        LootTableWandPlayerData data = player.getAttachedOrCreate(ModAttachments.LOOT_TABLE_WAND_PLAYER_DATA);
        if (data == null) return;
        modifier.accept(data);
        player.setAttached(ModAttachments.LOOT_TABLE_WAND_PLAYER_DATA, data);
    }
}
