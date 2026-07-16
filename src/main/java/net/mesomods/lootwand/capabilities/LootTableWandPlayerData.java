package net.mesomods.lootwand.capabilities;


import net.mesomods.lootwand.client.gui.LootTableViewMode;

public class LootTableWandPlayerData implements ILootTableWandPlayerData {
    public static final boolean KEYBINDS_SHOWN_DEFAULT = true;
    public static final boolean EMPTY_TARGET_CONTAINER_DEFAULT = false;
    public static final boolean SYNCHRONIZE_WANDS_DEFAULT = false;
    public static final LootTableViewMode LOOT_TABLE_VIEW_MODE_DEFAULT = LootTableViewMode.LIST;
    public static final NumberProviderTooltipMode NUMBER_PROVIDER_TOOLTIP_MODE_DEFAULT = NumberProviderTooltipMode.UNIFORM_DISABLED;
    public static final boolean HIDE_DEFAULTS_DEFAULT = true;
    public static final boolean COUNT_PREVIEW_DEFAULT = true;
    public static final int PREVIEW_TIME_DEFAULT = 1200;
    public static final String SAVED_LOCATION_DEFAULT = "";
    public static final float LUCK_DEFAULT = 0;

    private boolean keybindsShown = KEYBINDS_SHOWN_DEFAULT; // synced
    private LootTableViewMode lootTableViewMode = LOOT_TABLE_VIEW_MODE_DEFAULT; // synced
    private NumberProviderTooltipMode numberProviderTooltipMode = NUMBER_PROVIDER_TOOLTIP_MODE_DEFAULT; // synced
    private boolean countPreview = COUNT_PREVIEW_DEFAULT; // synced
    private boolean hideDefaults = HIDE_DEFAULTS_DEFAULT; // synced
    private boolean emptyTargetContainer = EMPTY_TARGET_CONTAINER_DEFAULT; // synced
    private boolean synchronizeWands = SYNCHRONIZE_WANDS_DEFAULT; // synced
    private int previewTime = PREVIEW_TIME_DEFAULT; // synced
    private String savedLocation = SAVED_LOCATION_DEFAULT; // is not synced to the server or across world unload/load
    private float luck = LUCK_DEFAULT; // synced

    @Override
    public boolean getKeybindsShown() {
        return this.keybindsShown;
    }

    @Override
    public void setKeybindsShown(boolean b) {
        this.keybindsShown = b;
    }

    @Override
    public boolean getEmptyTargetContainer() {
        return this.emptyTargetContainer;
    }

    @Override
    public void setEmptyTargetContainer(boolean b) {
        this.emptyTargetContainer = b;
    }

    @Override
    public LootTableViewMode getLootTableViewMode() {
        return this.lootTableViewMode;
    }

    @Override
    public void setLootTableViewMode(LootTableViewMode mode) {
        this.lootTableViewMode = mode;
    }

    @Override
    public NumberProviderTooltipMode getNumberProviderTooltipMode() {
        return this.numberProviderTooltipMode;
    }

    @Override
    public void setNumberProviderTooltipMode(NumberProviderTooltipMode mode) {
        this.numberProviderTooltipMode = mode;
    }

    @Override
    public boolean getHideDefaults() {
        return hideDefaults;
    }

    @Override
    public void setHideDefaults(boolean b) {
        this.hideDefaults = b;
    }

    @Override
    public boolean getCountPreview() {
        return countPreview;
    }

    @Override
    public void setCountPreview(boolean b) {
        this.countPreview = b;
    }

    @Override
    public boolean getSynchronizeWands() {
        return this.synchronizeWands;
    }

    @Override
    public void setSynchronizeWands(boolean b) {
        this.synchronizeWands = b;
    }

    @Override
    public int getPreviewTime() {
        return this.previewTime;
    }

    @Override
    public void setPreviewTime(int i) {
        this.previewTime = i;
    }

    @Override
    public String getSavedLocation() {
        return this.savedLocation;
    }

    @Override
    public void setSavedLocation(String s) {
        this.savedLocation = s;
    }

    @Override
    public float getLuck() {
        return this.luck;
    }

    @Override
    public void setLuck(float f) {
        this.luck = f;
    }
}
