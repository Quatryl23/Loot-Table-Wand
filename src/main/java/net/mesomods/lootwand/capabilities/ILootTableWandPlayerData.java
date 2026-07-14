package net.mesomods.lootwand.capabilities;

import net.mesomods.lootwand.client.gui.LootTableViewMode;

public interface ILootTableWandPlayerData {
    boolean getKeybindsShown();

    void setKeybindsShown(boolean b);

    boolean getSynchronizeWands();

    void setSynchronizeWands(boolean b);

    boolean getEmptyTargetContainer();

    void setEmptyTargetContainer(boolean b);

    LootTableViewMode getLootTableViewMode();

    void setLootTableViewMode(LootTableViewMode mode);

    NumberProviderTooltipMode getNumberProviderTooltipMode();

    void setNumberProviderTooltipMode(NumberProviderTooltipMode mode);

    boolean getHideDefaults();

    void setHideDefaults(boolean b);

    boolean getCountPreview();

    void setCountPreview(boolean b);

    int getPreviewTime();

    void setPreviewTime(int i);

    String getSavedLocation();

    void setSavedLocation(String s);

    float getLuck();

    void setLuck(float i);
}
