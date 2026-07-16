package net.mesomods.lootwand.client.gui.screen;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.mesomods.lootwand.attachments.LootTableWandPlayerDataManager;
import net.mesomods.lootwand.attachments.NumberProviderTooltipMode;
import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.client.gui.LootTableViewMode;
import net.mesomods.lootwand.client.gui.loottable.RenderedCumulatedLootPool;
import net.mesomods.lootwand.client.gui.loottable.RenderedLootTable;
import net.mesomods.lootwand.client.tooltip.MinYTooltipPositioner;
import net.mesomods.lootwand.mixin.gui.ImageButtonAccessor;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.client.RequestLootTableDataPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

public class LootTableDataScreen extends Screen {
    protected Player player;
    public static final int LOOT_TABLE_Y0 = 20;
    public static final Tooltip TOOLTIP_CLOSE = Tooltip.create(Component.translatable("gui.loot_table_wand.loot_table_editor.close"));
    public static final Tooltip TOOLTIP_RAW_LIST_VIEW = Tooltip.create(Component.translatable("gui.loot_table_wand.loot_table_editor.raw_list_mode"));
    public static final Tooltip TOOLTIP_LIST_VIEW = Tooltip.create(Component.translatable("gui.loot_table_wand.loot_table_editor.list_mode"));
    public static final Tooltip TOOLTIP_NUMBER_PROVIDER_TOOLTIPS_ENABLED = Tooltip.create(Component.translatable("gui.loot_table_wand.loot_table_editor.number_provider_tooltips_enabled"));
    public static final Tooltip TOOLTIP_NUMBER_PROVIDER_TOOLTIPS_UNIFORM_DISABLED = Tooltip.create(Component.translatable("gui.loot_table_wand.loot_table_editor.number_provider_tooltips_uniform_disabled"));
    public static final Tooltip TOOLTIP_NUMBER_PROVIDER_TOOLTIPS_ALL_DISABLED = Tooltip.create(Component.translatable("gui.loot_table_wand.loot_table_editor.number_provider_tooltips_all_disabled"));
    public static final Tooltip TOOLTIP_COUNT_PREVIEW_ENABLED = Tooltip.create(Component.translatable("gui.loot_table_wand.loot_table_editor.count_preview_enabled"));
    public static final Tooltip TOOLTIP_COUNT_PREVIEW_DISABLED = Tooltip.create(Component.translatable("gui.loot_table_wand.loot_table_editor.count_preview_disabled"));
    public static final Tooltip TOOLTIP_HIDE_DEFAULTS_ENABLED = Tooltip.create(Component.translatable("gui.loot_table_wand.loot_table_editor.hide_defaults_enabled"));
    public static final Tooltip TOOLTIP_HIDE_DEFAULTS_DISABLED = Tooltip.create(Component.translatable("gui.loot_table_wand.loot_table_editor.hide_defaults_disabled"));
    public static final ClientTooltipPositioner TOOLTIP_POSITIONER = new MinYTooltipPositioner(LootTableDataScreen.LOOT_TABLE_Y0 + 5);
    protected ResourceLocation location;
    protected RenderedLootTable lootTable;
    protected Button closeButton;
    protected Button viewModeToggle;
    protected Button numberProviderTooltipModeToggle;
    protected Button countPreviewToggle;
    protected Button hideDefaultsToggle;
    protected RenderedCumulatedLootPool.LuckSlider luckSlider;
    protected LootTableViewMode viewMode;
    protected NumberProviderTooltipMode numberProviderTooltipMode;
    protected boolean countPreview;
    protected boolean hideDefaults;
    @Nullable
    protected Screen backgroundScreen;
    protected double scrollAmount;

    public LootTableDataScreen(ResourceLocation location, @Nullable Screen screen, Player p) {
        super(Component.literal(location.toString()));
        this.player = p;
        this.location = location;
        this.backgroundScreen = screen;
        this.viewMode = LootTableWandPlayerDataManager.getLootTableViewMode(player);
        this.numberProviderTooltipMode = LootTableWandPlayerDataManager.getNumberProviderTooltipMode(player);
        this.countPreview = LootTableWandPlayerDataManager.shouldShowCountPreview(player);
        this.hideDefaults = LootTableWandPlayerDataManager.shouldHideDefaultParameters(player);
        this.scrollAmount = 0.0;
    }

    @Override
    public void init() {
        if (luckSlider != null) {
            this.removeLuckSlider(luckSlider);
        }
        lootTable = new RenderedLootTable(minecraft, this.width, this.height, LOOT_TABLE_Y0, this.height, location, viewMode);
        LootTableNetwork.sendToServer(new RequestLootTableDataPacket(location));
        closeButton = new HoverableImageButton(Pair.of(new ResourceLocation("loot_table_wand:textures/gui/close_screen_button.png"), new ResourceLocation("loot_table_wand:textures/gui/close_screen_button_hovered.png")),
                b -> this.onClose(), TOOLTIP_CLOSE, 0, 2, 30, 16);
        viewModeToggle = new HoverableImageButton(getViewModeImages(), (button) -> {
            toggleViewMode();
            ((HoverableImageButton) button).setImages(getViewModeImages());
            button.setTooltip(getViewModeTooltip());
        }, getViewModeTooltip(), 35, 2, 30, 16);
        numberProviderTooltipModeToggle = new HoverableImageButton(getNumberProviderTooltipModeImages(), (button) -> {
            toggleNumberProviderTooltipMode();
            ((HoverableImageButton) button).setImages(getNumberProviderTooltipModeImages());
            button.setTooltip(getNumberProviderTooltipModeTooltip());
        }, getNumberProviderTooltipModeTooltip(), this.width - 105, 2, 30, 16);
        countPreviewToggle = new HoverableImageButton(getCountPreviewImages(), (button) -> {
            toggleCountPreview();
            ((HoverableImageButton) button).setImages(getCountPreviewImages());
            button.setTooltip(getCountPreviewTooltip());
        }, getCountPreviewTooltip(), this.width - 70, 2, 30, 16);
        hideDefaultsToggle = new HoverableImageButton(getHideDefaultsImages(), (button) -> {
            toggleHideDefaults();
            ((HoverableImageButton) button).setImages(getHideDefaultsImages());
            button.setTooltip(getHideDefaultsTooltip());
        }, getHideDefaultsTooltip(), this.width - 35, 2, 30, 16);
        ScreenUtils.enableAdvancedTooltips(closeButton, viewModeToggle, numberProviderTooltipModeToggle, countPreviewToggle, hideDefaultsToggle);
        this.addRenderableWidget(lootTable);
        this.addRenderableWidget(closeButton);
        this.addRenderableWidget(viewModeToggle);
        this.addRenderableWidget(numberProviderTooltipModeToggle);
        this.addRenderableWidget(countPreviewToggle);
        this.addRenderableWidget(hideDefaultsToggle);
    }

    public void initWithJson(JsonObject json) {
        LootTable table = Deserializers.createLootTableSerializer().create().fromJson(json, LootTable.class);
        this.lootTable.init(table);
        this.lootTable.toggleDefaultParameters(hideDefaults);
        this.lootTable.applyFunctionPreviewEffects(countPreview);
        this.lootTable.setScrollAmount(this.scrollAmount);
    }

    public void addLuckSlider(RenderedCumulatedLootPool.LuckSlider slider) {
        this.luckSlider = slider;
        this.addRenderableWidget(slider);
    }

    public void removeLuckSlider(RenderedCumulatedLootPool.LuckSlider slider) {
        this.luckSlider = null;
        this.removeWidget(slider);
    }

    public void acceptPreviewList(ResourceLocation location, int[] previewTimes, ListTag previewItems, boolean isTag) {
        this.lootTable.acceptPreviewList(location, previewTimes, previewItems, isTag);
    }

    public void clickedOnLootTableReference(ResourceLocation newLootTable) {
        this.scrollAmount = this.lootTable.getScrollAmount();
        if (minecraft != null && player != null)
            minecraft.setScreen(new LootTableDataScreen(newLootTable, this, this.player));
    }

    public void onClose() {
        LootTableWandPlayerDataManager.updateLootTableScreenPreferences(player, viewMode, countPreview, hideDefaults, lootTable.getCurrentLuck());
        if (minecraft != null && backgroundScreen != null) {
            minecraft.setScreen(backgroundScreen);
        } else {
            super.onClose();
        }
    }

    public void toggleViewMode() {
        switch (viewMode) {
            case RAW_LIST -> viewMode = LootTableViewMode.LIST;
            case LIST -> viewMode = LootTableViewMode.RAW_LIST;
        }
        ;
        lootTable.setViewMode(viewMode);
    }

    public void toggleNumberProviderTooltipMode() {
        numberProviderTooltipMode = switch (numberProviderTooltipMode) {
            case ENABLED -> NumberProviderTooltipMode.UNIFORM_DISABLED;
            case UNIFORM_DISABLED -> NumberProviderTooltipMode.ALL_DISABLED;
            case ALL_DISABLED -> NumberProviderTooltipMode.ENABLED;
        };
        LootTableWandPlayerDataManager.setNumberProviderTooltipMode(player, numberProviderTooltipMode);
    }

    public void toggleHideDefaults() {
        this.hideDefaults = !this.hideDefaults;
        this.lootTable.toggleDefaultParameters(hideDefaults);
    }

    public void toggleCountPreview() {
        this.countPreview = !this.countPreview;
        this.lootTable.applyFunctionPreviewEffects(countPreview);
    }

    public Pair<ResourceLocation, ResourceLocation> getViewModeImages() {
        switch (viewMode) {
            case RAW_LIST -> {
                return Pair.of(new ResourceLocation("loot_table_wand:textures/gui/raw_list_mode.png"), new ResourceLocation("loot_table_wand:textures/gui/raw_list_mode_hovered.png"));
            }
            case LIST -> {
                return Pair.of(new ResourceLocation("loot_table_wand:textures/gui/advanced_list_mode.png"), new ResourceLocation("loot_table_wand:textures/gui/advanced_list_mode_hovered.png"));
            }
            default -> {
                return null;
            }
        }
    }

    public Pair<ResourceLocation, ResourceLocation> getNumberProviderTooltipModeImages() {
        switch (numberProviderTooltipMode) {
            case ENABLED -> {
                return Pair.of(new ResourceLocation("loot_table_wand:textures/gui/charts_enabled.png"), new ResourceLocation("loot_table_wand:textures/gui/charts_enabled_hovered.png"));
            }
            case UNIFORM_DISABLED -> {
                return Pair.of(new ResourceLocation("loot_table_wand:textures/gui/charts_no_uniform.png"), new ResourceLocation("loot_table_wand:textures/gui/charts_no_uniform_hovered.png"));
            }
            case ALL_DISABLED -> {
                return Pair.of(new ResourceLocation("loot_table_wand:textures/gui/charts_disabled.png"), new ResourceLocation("loot_table_wand:textures/gui/charts_disabled_hovered.png"));
            }
            default -> {
                return null;
            }
        }
    }

    public Pair<ResourceLocation, ResourceLocation> getHideDefaultsImages() {
        return hideDefaults ? Pair.of(new ResourceLocation("loot_table_wand:textures/gui/hide_defaults_true.png"), new ResourceLocation("loot_table_wand:textures/gui/hide_defaults_true_hovered.png")) : Pair.of(new ResourceLocation("loot_table_wand:textures/gui/hide_defaults_false.png"), new ResourceLocation("loot_table_wand:textures/gui/hide_defaults_false_hovered.png"));
    }

    public Pair<ResourceLocation, ResourceLocation> getCountPreviewImages() {
        return countPreview ? Pair.of(new ResourceLocation("loot_table_wand:textures/gui/count_preview_true.png"), new ResourceLocation("loot_table_wand:textures/gui/count_preview_true_hovered.png")) : Pair.of(new ResourceLocation("loot_table_wand:textures/gui/count_preview_false.png"), new ResourceLocation("loot_table_wand:textures/gui/count_preview_false_hovered.png"));
    }

    public Tooltip getViewModeTooltip() {
        switch (viewMode) {
            case RAW_LIST -> {
                return TOOLTIP_RAW_LIST_VIEW;
            }
            case LIST -> {
                return TOOLTIP_LIST_VIEW;
            }
            default -> {
                return Tooltip.create(Component.empty());
            }
        }
    }

    public Tooltip getNumberProviderTooltipModeTooltip() {
        switch (numberProviderTooltipMode) {
            case ENABLED -> {
                return TOOLTIP_NUMBER_PROVIDER_TOOLTIPS_ENABLED;
            }
            case UNIFORM_DISABLED -> {
                return TOOLTIP_NUMBER_PROVIDER_TOOLTIPS_UNIFORM_DISABLED;
            }
            case ALL_DISABLED -> {
                return TOOLTIP_NUMBER_PROVIDER_TOOLTIPS_ALL_DISABLED;
            }
            default -> {
                return Tooltip.create(Component.empty());
            }
        }
    }

    public Tooltip getHideDefaultsTooltip() {
        return hideDefaults ? TOOLTIP_HIDE_DEFAULTS_ENABLED : TOOLTIP_HIDE_DEFAULTS_DISABLED;
    }

    public Tooltip getCountPreviewTooltip() {
        return countPreview ? TOOLTIP_COUNT_PREVIEW_ENABLED : TOOLTIP_COUNT_PREVIEW_DISABLED;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawCenteredString(minecraft.font, this.title, width / 2, 8, 0xFFFFFF);
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    public static class HoverableImageButton extends ImageButton {
        ResourceLocation hoveredResourceLocation;

        HoverableImageButton(Pair<ResourceLocation, ResourceLocation> images, Button.OnPress onPress, Tooltip tooltip, int x, int y, int width, int height) {
            super(x, y, width, height, 0, 0, height, images.getFirst(), width, height, onPress);
            this.hoveredResourceLocation = images.getSecond();
            this.setTooltip(tooltip);
        }

        public void setImages(Pair<ResourceLocation, ResourceLocation> images) {
            ((ImageButtonAccessor) this).setResourceLocation(images.getFirst());
            this.hoveredResourceLocation = images.getSecond();
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int x, int y, float partialTick) {
            this.renderTexture(graphics, this.isHovered ? this.hoveredResourceLocation : this.resourceLocation, this.getX(), this.getY(), this.xTexStart, this.yTexStart, this.yDiffTex, this.width, this.height, this.textureWidth, this.textureHeight);
        }
    }
}
