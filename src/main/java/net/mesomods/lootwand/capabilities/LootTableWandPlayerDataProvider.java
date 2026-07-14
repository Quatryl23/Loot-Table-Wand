package net.mesomods.lootwand.capabilities;

import net.mesomods.lootwand.client.gui.LootTableViewMode;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class LootTableWandPlayerDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final LootTableWandPlayerData data = new LootTableWandPlayerData();
    private final LazyOptional<ILootTableWandPlayerData> optional = LazyOptional.of(() -> data);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
        return capability == ModCapabilities.LOOT_TABLE_WAND_PLAYER_DATA ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("emptyTargetContainer", data.getEmptyTargetContainer());
        nbt.putBoolean("synchronizeWands", data.getSynchronizeWands());
        nbt.putInt("previewTime", data.getPreviewTime());
        nbt.putString("savedLocation", data.getSavedLocation());
        nbt.putBoolean("keybindsShown", data.getKeybindsShown());
        nbt.putString("lootTableViewMode", data.getLootTableViewMode().name());
        nbt.putString("numberProviderTooltipMode", data.getNumberProviderTooltipMode().name());
        nbt.putBoolean("previewCounts", data.getCountPreview());
        nbt.putBoolean("hideDefaults", data.getHideDefaults());
        nbt.putFloat("luck", data.getLuck());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        data.setEmptyTargetContainer((nbt.contains("emptyTargetContainer") && nbt.getBoolean("emptyTargetContainer")) ^ LootTableWandPlayerData.EMPTY_TARGET_CONTAINER_DEFAULT);
        data.setSynchronizeWands((nbt.contains("synchronizeWands") && nbt.getBoolean("synchronizeWands")) ^ LootTableWandPlayerData.SYNCHRONIZE_WANDS_DEFAULT);
        data.setPreviewTime(nbt.contains("previewTime") ? nbt.getInt("previewTime") : LootTableWandPlayerData.PREVIEW_TIME_DEFAULT);
        data.setSavedLocation(nbt.contains("savedLocation") ? nbt.getString("savedLocation") : LootTableWandPlayerData.SAVED_LOCATION_DEFAULT);
        data.setKeybindsShown(nbt.contains("keybindsShown") ? nbt.getBoolean("keybindsShown") : LootTableWandPlayerData.KEYBINDS_SHOWN_DEFAULT);
        data.setLootTableViewMode(nbt.contains("lootTableViewMode") ? LootTableViewMode.valueOf(nbt.getString("lootTableViewMode")) : LootTableWandPlayerData.LOOT_TABLE_VIEW_MODE_DEFAULT);
        data.setNumberProviderTooltipMode(nbt.contains("numberProviderTooltipMode") ? NumberProviderTooltipMode.valueOf(nbt.getString("numberProviderTooltipMode")) : LootTableWandPlayerData.NUMBER_PROVIDER_TOOLTIP_MODE_DEFAULT);
        data.setCountPreview(nbt.contains("previewCounts") ? nbt.getBoolean("previewCounts") : LootTableWandPlayerData.COUNT_PREVIEW_DEFAULT);
        data.setHideDefaults(nbt.contains("hideDefaults") ? nbt.getBoolean("hideDefaults") : LootTableWandPlayerData.HIDE_DEFAULTS_DEFAULT);
        data.setLuck(nbt.contains("luck") ? nbt.getFloat("luck") : LootTableWandPlayerData.LUCK_DEFAULT);
    }
}
