package net.mesomods.lootwand.attachments;

import net.mesomods.lootwand.client.gui.LootTableViewMode;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.UpdateLootTableWandPlayerDataPacket;
import net.minecraft.world.entity.player.Player;

public class LootTableWandPlayerDataManager {
    public static boolean shouldEmptyTargetContainer(Player player) {
        return ModAttachments.getPlayerData(player, ILootTableWandPlayerData::getEmptyTargetContainer, LootTableWandPlayerData.EMPTY_TARGET_CONTAINER_DEFAULT);
    }

    public static boolean shouldSynchronizeWands(Player player) {
        return ModAttachments.getPlayerData(player, ILootTableWandPlayerData::getSynchronizeWands, LootTableWandPlayerData.SYNCHRONIZE_WANDS_DEFAULT);
    }

    public static LootTableViewMode getLootTableViewMode(Player player) {
        return ModAttachments.getPlayerData(player, ILootTableWandPlayerData::getLootTableViewMode, LootTableWandPlayerData.LOOT_TABLE_VIEW_MODE_DEFAULT);
    }

    public static float getLuck(Player player) {
        return ModAttachments.getPlayerData(player, ILootTableWandPlayerData::getLuck, LootTableWandPlayerData.LUCK_DEFAULT);
    }

    public static NumberProviderTooltipMode getNumberProviderTooltipMode(Player player) {
        return ModAttachments.getPlayerData(player, ILootTableWandPlayerData::getNumberProviderTooltipMode, LootTableWandPlayerData.NUMBER_PROVIDER_TOOLTIP_MODE_DEFAULT);
    }

    public static boolean shouldShowCountPreview(Player player) {
        return ModAttachments.getPlayerData(player, ILootTableWandPlayerData::getCountPreview, LootTableWandPlayerData.COUNT_PREVIEW_DEFAULT);
    }

    public static boolean shouldHideDefaultParameters(Player player) {
        return ModAttachments.getPlayerData(player, ILootTableWandPlayerData::getHideDefaults, LootTableWandPlayerData.HIDE_DEFAULTS_DEFAULT);
    }

    public static int getPreviewTime(Player player) {
        return ModAttachments.getPlayerData(player, ILootTableWandPlayerData::getPreviewTime, LootTableWandPlayerData.PREVIEW_TIME_DEFAULT);
    }

    public static String getSavedLocation(Player player) {
        return ModAttachments.getPlayerData(player, ILootTableWandPlayerData::getSavedLocation, LootTableWandPlayerData.SAVED_LOCATION_DEFAULT);
    }

    public static void saveLocation(Player player, String location) {
        ModAttachments.setPlayerData(player, data -> data.setSavedLocation(location));
    }

    public static boolean shouldShowKeybinds(Player player) {
        return ModAttachments.getPlayerData(player, ILootTableWandPlayerData::getKeybindsShown, LootTableWandPlayerData.KEYBINDS_SHOWN_DEFAULT);
    }

    public static void updateWandPreferences(Player player, boolean emptyTargetContainer, boolean synchronizeWands, int previewTime) {
        ModAttachments.setPlayerData(player, (data) -> {
            data.setEmptyTargetContainer(emptyTargetContainer);
            data.setSynchronizeWands(synchronizeWands);
            data.setPreviewTime(previewTime);
        });
        LootTableNetwork.sendToServer(new UpdateLootTableWandPlayerDataPacket(null, emptyTargetContainer, synchronizeWands, previewTime, null, null, null, null, null));
    }

    public static void setNumberProviderTooltipMode(Player player, NumberProviderTooltipMode numberProviderTooltipMode) {
        ModAttachments.setPlayerData(player, (data -> data.setNumberProviderTooltipMode(numberProviderTooltipMode)));
        LootTableNetwork.sendToServer(new UpdateLootTableWandPlayerDataPacket(null, null, null, null, null, numberProviderTooltipMode, null, null, null));
    }

    public static void updateLootTableScreenPreferences(Player player, LootTableViewMode viewMode, boolean countPreview, boolean hideDefaults, float luck) {
        ModAttachments.setPlayerData(player, (data) -> {
            data.setLootTableViewMode(viewMode);
            data.setCountPreview(countPreview);
            data.setHideDefaults(hideDefaults);
            data.setLuck(luck);
        });
        LootTableNetwork.sendToServer(new UpdateLootTableWandPlayerDataPacket(null, null, null, null, viewMode, null, countPreview, hideDefaults, luck));
    }

    public static void toggleKeybindsShown(Player player) {
        LootTableWandPlayerData data = player.getAttachedOrCreate(ModAttachments.LOOT_TABLE_WAND_PLAYER_DATA);
        data.setKeybindsShown(!data.getKeybindsShown());
        player.setAttached(ModAttachments.LOOT_TABLE_WAND_PLAYER_DATA, data);
        LootTableNetwork.sendToServer(new UpdateLootTableWandPlayerDataPacket(data.getKeybindsShown(), null, null, null, null, null, null, null, null));
    }
}
