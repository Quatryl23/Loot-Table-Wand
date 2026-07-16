package net.mesomods.lootwand.client.gui.screen;

import net.mesomods.lootwand.ModItems;
import net.mesomods.lootwand.attachments.LootTableWandPlayerDataManager;
import net.mesomods.lootwand.client.LootTableWandRenderer;
import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.client.tooltip.AdvancedTooltipAbstractWidget;
import net.mesomods.lootwand.item.LootTableWandItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LootTableWandScreen extends Screen {
    private static final Component DEFAULT_MESSAGE = Component.translatable("gui.loot_table_wand.no_table_set");
    private static final Component DEFAULT_TOOLTIP = Component.translatable("gui.loot_table_wand.set_loot_table");
    public Checkbox emptyContainerCheckbox;
    public Checkbox synchronizeWandsCheckbox;
    public PreviewDurationSlider previewSlider;
    private List<RightClickableButton> lootTableButtons;
    private Button lastClickedButton;
    private long lastClickedButtonTime;
    public int inventorySlot;
    private final Player player;
    private int activeIndex;

    public LootTableWandScreen(Player player, ItemStack item) {
        super(Component.translatable("gui.loot_table_wand.loot_table_wand"));
        this.player = player;
        this.inventorySlot = player.getInventory().items.indexOf(item);
    }

    public void validateItem() {
        if (!player.getInventory().getItem(inventorySlot).is(ModItems.LOOT_TABLE_WAND)) {
            this.onClose();
        }
    }

    public ItemStack getItem() {
        return player.getInventory().getItem(inventorySlot);
    }

    @Override
    protected void init() {
        if (!player.getInventory().getItem(inventorySlot).is(ModItems.LOOT_TABLE_WAND)) super.onClose();
        this.validateItem();
        activeIndex = LootTableWandItem.getActiveLootTableIndex(this.getItem());
        lootTableButtons = new ArrayList<>();
        this.emptyContainerCheckbox = new Checkbox(this.width / 2 - 148, 40, 20, 20, Component.translatable("gui.loot_table_wand.empty_container"), LootTableWandPlayerDataManager.shouldEmptyTargetContainer(player));
        emptyContainerCheckbox.setTooltip(Tooltip.create(Component.translatable("gui.loot_table_wand.empty_container_description")));
        this.synchronizeWandsCheckbox = new Checkbox(this.width / 2 + 32, 40, 20, 20, Component.translatable("gui.loot_table_wand.synchronize_wands"), LootTableWandPlayerDataManager.shouldSynchronizeWands(player));
        synchronizeWandsCheckbox.setTooltip(Tooltip.create(Component.translatable("gui.loot_table_wand.synchronize_wands_description")));
        this.previewSlider = new PreviewDurationSlider(this.width / 2 - 150, 70, 300, 20, Component.translatable("gui.loot_table_wand.preview_duration").append(Component.literal(" ")), Component.translatable("gui.loot_table_wand.preview_disabled"), Component.translatable("gui.loot_table_wand.preview_fixed"), Component.translatable("gui.loot_table_wand.preview_description"), LootTableWandPlayerDataManager.getPreviewTime(player));
        ScreenUtils.enableAdvancedTooltips(emptyContainerCheckbox, synchronizeWandsCheckbox, previewSlider);
        this.addRenderableWidget(emptyContainerCheckbox);
        this.addRenderableWidget(synchronizeWandsCheckbox);
        this.addRenderableWidget(previewSlider);
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 5; i++) {
                int lootTableIndex = (5 * j) + i;
                ResourceLocation lootTableLocation = LootTableWandItem.getLootTable(this.getItem(), lootTableIndex);
                Component lootTableString = lootTableLocation == null ? DEFAULT_MESSAGE : Component.literal(lootTableLocation.toString());
                RightClickableButton lootTableButton = new RightClickableButton(this.width / 2 + 5 + (210 * (j - 1)), (i + 3) * 25 + 30, 200, 20, getShortenedLocation(lootTableString), button -> {
                    // double click functionality
                    if (lastClickedButton == button) {
                        long timeSinceLastClick = System.nanoTime() - lastClickedButtonTime;
                        if (timeSinceLastClick > 500_000_000L) {
                            lastClickedButtonTime = System.nanoTime();
                            return;
                        }
                        lastClickedButton = null;
                        Minecraft.getInstance().setScreen(new LootTableBrowsingScreen(this, player));
                    } else {
                        lastClickedButton = button;
                        lastClickedButtonTime = System.nanoTime();
                        int newIndex = lootTableButtons.indexOf((RightClickableButton) button);
                        this.updateActiveLootTable(newIndex);
                        // shift clicking closes the screen after selection
                        if (hasShiftDown()) {
                            this.onClose();
                        }
                    }
                }, button -> {
                    int index = lootTableButtons.indexOf((RightClickableButton) button);
                    this.updateActiveLootTable(index);
                    this.removeLootTable(index);
                });
                lootTableButton.setTooltip(Tooltip.create(lootTableLocation == null ? DEFAULT_TOOLTIP : lootTableString));
                ScreenUtils.enableAdvancedTooltips(lootTableButton);
                this.addRenderableWidget(lootTableButton);
                lootTableButtons.add(lootTableButton);
            }
        }
        activateLootTableButton(this.activeIndex);
    }

    public void updateActiveLootTable(int newIndex) {
        this.validateItem();
        deactivateLootTableButton(this.activeIndex);
        this.activeIndex = newIndex;
        LootTableWandPlayerDataManager.updateWandPreferences(this.player, this.emptyContainerCheckbox.selected(), this.synchronizeWandsCheckbox.selected(), this.previewSlider.getCycleTime());
        LootTableWandItem.setActiveLootTableIndexClient(this.getItem(), this.activeIndex, this.player);
        activateLootTableButton(this.activeIndex);
    }

    public void deactivateLootTableButton(int index) {
        if (index == -1)
            return;
        RightClickableButton button = lootTableButtons.get(index);
        ((AdvancedTooltipAbstractWidget)button).lootmod$setStringColor(-1);
        button.setFocused(false);
    }

    public void activateLootTableButton(int index) {
        if (index == -1)
            return;
        RightClickableButton button = lootTableButtons.get(index);
        ((AdvancedTooltipAbstractWidget)button).lootmod$setStringColor(5635925); // ChatFormatting.GREEN
        button.setFocused(true);
    }

    public void setLootTable(String location) {
        this.setLootTable(activeIndex, location);
    }

    public void setLootTable(int index, String location) {
        this.validateItem();
        LootTableWandItem.setLootTable(player, this.getItem(), index, location, true);
        RightClickableButton button = lootTableButtons.get(index);
        button.setMessage(Component.literal(getShortenedLocation(location)));
        button.setTooltip(Tooltip.create(Component.literal(location)));
    }

    public void removeLootTable(int index) {
        this.validateItem();
        LootTableWandItem.removeLootTable(this.player, this.getItem(), index, true);
        RightClickableButton button = lootTableButtons.get(index);
        button.setMessage(DEFAULT_MESSAGE);
        button.setTooltip(Tooltip.create(DEFAULT_TOOLTIP));
    }

    public static Component getShortenedLocation(Component component) {
        return Component.literal(LootTableWandItem.getShortenedLocation(component.getString()));
    }

    public static String getShortenedLocation(String string) {
        return LootTableWandItem.getShortenedLocation(string);
    }

    @Override
    public boolean keyPressed(int key, int x, int y) {
        if (!super.keyPressed(key, x, y)) {
            if (key != 261) return false;
            this.removeLootTable(this.activeIndex);
            return true;
        }
        return true;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, Component.translatable("gui.loot_table_wand.loot_table_wand"), this.width / 2, 16, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        LootTableWandPlayerDataManager.updateWandPreferences(player, this.emptyContainerCheckbox.selected(), this.synchronizeWandsCheckbox.selected(), this.previewSlider.getCycleTime());
        super.onClose();

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    static class RightClickableButton extends Button {
        private final Button.OnPress secondaryOnPress;

        public RightClickableButton(int x, int y, int width, int height, Component message, Button.OnPress onLeftClick, Button.OnPress onRightClick) {
            super(x, y, width, height, message, onLeftClick, RightClickableButton.DEFAULT_NARRATION);
            this.secondaryOnPress = onRightClick;
        }

        @Override
        public boolean mouseClicked(double x, double y, int key) {
            if (!super.mouseClicked(x, y, key) && this.active && this.visible && key == 1 && this.clicked(x, y)) {
                secondaryOnPress.onPress(this);
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                return false;
            }
            return false;
        }
    }

    public static class PreviewDurationSlider extends AbstractSliderButton {
        Component defaultMessage;
        Component leftEndMessage;
        Component rightEndMessage;
        int cycleTime;

        public PreviewDurationSlider(int x, int y, int width, int height, Component defaultMessage, Component leftEndMessage, Component rightEndMessage, Component tooltipMessage, int cycleTime) {
            super(x, y, width, height, constructMessage(defaultMessage, getTimeString(cycleTime)), calculateCycleTimeReversed(cycleTime));
            this.cycleTime = cycleTime;
            this.defaultMessage = defaultMessage;
            this.leftEndMessage = leftEndMessage;
            this.rightEndMessage = rightEndMessage;
            this.setTooltip(Tooltip.create(tooltipMessage));
            this.updateMessage();

        }

        public static Component constructMessage(Component defaultMessage, String string) {
            return defaultMessage.copy().append(string);
        }

        public static String getTimeString(int timeInTicks) {
            if (timeInTicks < 0) {
                return "";
            } else if (timeInTicks < 4800) {
                return timeInTicks / 20 + "s";
            } else {
                return timeInTicks / 1200 + "m";
            }
        }

        public static int calculateCycleTime(double value) {
            if (value == 0) {
                return -1;
            } else if (value <= 0.25) {
                return (int) Math.round(800 * value);
            } else if (value <= 0.5) {
                return (int) Math.round(4000 * value) - 800;
            } else if (value <= 0.75) {
                return (int) Math.round(12000 * value) - 4800;
            } else if (value < 1) {
                return (int) Math.round(48000 * value) - 32400;
            } else {
                return -2;
            }
        }

        public static float calculateCycleTimeReversed(int cycleTime) {
            if (cycleTime == -1) {
                return 0;
            } else if (cycleTime == -2) {
                return 1;
            } else if (cycleTime <= 200) {
                return (float) (cycleTime) / 800;
            } else if (cycleTime <= 1200) {
                return (float) (cycleTime + 800) / 4000;
            } else if (cycleTime <= 4200) {
                return (float) (cycleTime + 4800) / 12000;
            } else {
                return (float) (cycleTime + 32400) / 48000;
            }
        }

        public int getCycleTime() {
            return cycleTime;
        }

        @Override
        public void updateMessage() {
            if (cycleTime == -1) {
                this.setMessage(leftEndMessage);
            } else if (cycleTime == -2) {
                this.setMessage(rightEndMessage);
            } else {
                this.setMessage(constructMessage(defaultMessage, getTimeString(cycleTime)));
            }
        }

        public void updateCycleTime() {
            double value = this.value;
            this.cycleTime = calculateCycleTime(value);
        }

        @Override
        public void applyValue() {
            this.value = Math.round(this.value / 0.025) * 0.025;
            this.updateCycleTime();
            LootTableWandRenderer.PREVIEW_CYCLE_TIME = this.cycleTime;
        }

        @Override
        public boolean keyPressed(int key, int x, int y) {
            if (key == 262) {
                this.value += 0.025;
                this.onValueChange();
                return true;
            } else if (key == 263) {
                this.value -= 0.025;
                this.onValueChange();
                return true;
            }
            return false;
        }

        // should mirror updating in private method setValue(double)
        public void onValueChange() {
            this.value = Mth.clamp(this.value, 0.0F, 1.0F);
            this.applyValue();
            this.updateMessage();
        }
    }
}
