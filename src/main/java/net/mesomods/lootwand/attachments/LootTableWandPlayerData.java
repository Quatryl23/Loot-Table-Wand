package net.mesomods.lootwand.attachments;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

    public static final Codec<LootTableWandPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("keybinds_shown").forGetter(LootTableWandPlayerData::getKeybindsShown),
            LootTableViewMode.CODEC.fieldOf("loot_table_view_mode").forGetter(LootTableWandPlayerData::getLootTableViewMode),
            NumberProviderTooltipMode.CODEC.fieldOf("number_provider_tooltip_mode").forGetter(LootTableWandPlayerData::getNumberProviderTooltipMode),
            Codec.BOOL.fieldOf("count_preview").forGetter(LootTableWandPlayerData::getCountPreview),
            Codec.BOOL.fieldOf("hide_defaults").forGetter(LootTableWandPlayerData::getHideDefaults),
            Codec.BOOL.fieldOf("empty_target_container").forGetter(LootTableWandPlayerData::getEmptyTargetContainer),
            Codec.BOOL.fieldOf("synchronize_wands").forGetter(LootTableWandPlayerData::getSynchronizeWands),
            Codec.INT.fieldOf("preview_time").forGetter(LootTableWandPlayerData::getPreviewTime),
            Codec.STRING.fieldOf("saved_location").forGetter(LootTableWandPlayerData::getSavedLocation),
            Codec.FLOAT.fieldOf("luck").forGetter(LootTableWandPlayerData::getLuck)
            ).apply(instance, LootTableWandPlayerData::new));

    public static final LootTableWandPlayerData DEFAULT_INSTANCE = new LootTableWandPlayerData(
            KEYBINDS_SHOWN_DEFAULT, LOOT_TABLE_VIEW_MODE_DEFAULT, NUMBER_PROVIDER_TOOLTIP_MODE_DEFAULT,
            COUNT_PREVIEW_DEFAULT, HIDE_DEFAULTS_DEFAULT, EMPTY_TARGET_CONTAINER_DEFAULT, SYNCHRONIZE_WANDS_DEFAULT,
            PREVIEW_TIME_DEFAULT, SAVED_LOCATION_DEFAULT, LUCK_DEFAULT
    );

    private boolean keybindsShown; // synced
    private LootTableViewMode lootTableViewMode; // synced
    private NumberProviderTooltipMode numberProviderTooltipMode; // synced
    private boolean countPreview; // synced
    private boolean hideDefaults; // synced
    private boolean emptyTargetContainer; // synced
    private boolean synchronizeWands; // synced
    private int previewTime; // synced
    private String savedLocation; // is not synced to the server or across world unload/load
    private float luck; // synced

    public LootTableWandPlayerData(boolean keybindsShown, LootTableViewMode lootTableViewMode, NumberProviderTooltipMode numberProviderTooltipMode, boolean countPreview, boolean hideDefaults, boolean emptyTargetContainer, boolean synchronizeWands, int previewTime, String savedLocation, float luck) {
        this.keybindsShown = keybindsShown;
        this.lootTableViewMode = lootTableViewMode;
        this.numberProviderTooltipMode = numberProviderTooltipMode;
        this.countPreview = countPreview;
        this.hideDefaults = hideDefaults;
        this.emptyTargetContainer = emptyTargetContainer;
        this.synchronizeWands = synchronizeWands;
        this.previewTime = previewTime;
        this.savedLocation = savedLocation;
        this.luck = luck;
    }

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
