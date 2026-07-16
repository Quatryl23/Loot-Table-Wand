package net.mesomods.lootwand.client.gui.screen;

import net.mesomods.lootwand.attachments.LootTableWandPlayerDataManager;
import net.mesomods.lootwand.client.gui.LootTableSelectionList;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.client.RequestLootTablesPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LootTableBrowsingScreen extends Screen {
	private final LootTableWandScreen backgroundGui;
	private final Player player;
	private List<ResourceLocation> allLootTables;
	private EditBox searchBox;
	private String searchedText;
	private RespondingCheckbox searchToggle;
	private String viewedResourceLocation;
    private String selectedLootTable;
	private double scrollAmount;
	private Component lootTableCountMessage;
	private boolean emptyLocation;
	private LootTableSelectionList selectionList;
	private List<Button> buttons = new ArrayList<>();
	private List<Integer> betweenButtonSpaces = new ArrayList<>();
    private Button showDataButton;
	private Button changedParentButton = null;
	private long buttonHideTime = 0;
    private boolean searchToggleSelected;

	public LootTableBrowsingScreen(LootTableWandScreen gui, Player p) {
		super(Component.translatable("gui.loot_table_wand.loot_table_browsing"));
		this.backgroundGui = gui;
		this.player = p;
		this.lootTableCountMessage = Component.translatable("gui.loot_table_wand.loot_table_browsing.loading");
        this.searchToggleSelected = false;
		this.searchedText = "";
		this.scrollAmount = 0;
        viewedResourceLocation = LootTableWandPlayerDataManager.getSavedLocation(player);
	}

	@Override
	protected void init() {
		searchBox = new EditBox(this.font, this.width / 2 - 80, 8, 160, 20, Component.literal(searchedText));
		searchBox.setValue(searchedText);
		searchBox.setResponder(s -> {
			this.searchedText = s;
			this.reloadResourceLocation();
		});
		searchToggle = new RespondingCheckbox(this.width / 2 + 90, 8, 20, 20, Component.translatable("gui.loot_table_wand.loot_table_browsing.show_all"), searchToggleSelected);
		searchToggle.setResponder(
                s -> this.reloadResourceLocation());
		selectionList = new LootTableSelectionList(Minecraft.getInstance(), this.width, 32, 32, this.height - 32, 20, this::lootTableSelected);
		selectionList.setRenderBackground(false);
		selectionList.setRenderTopAndBottom(false);
        showDataButton = Button.builder(Component.translatable("gui.loot_table_wand.loot_table_browsing.show_loot"), (button) -> {
			if (selectedLootTable == null) return;
			String selectedLootTableLocation = buildNewLocation(selectedLootTable);
            if (ResourceLocation.isValidResourceLocation(selectedLootTableLocation)) {
                ResourceLocation location = new ResourceLocation(selectedLootTableLocation);
                LootTableWandPlayerDataManager.saveLocation(player, viewedResourceLocation);
				this.scrollAmount = selectionList.getScrollAmount();
                minecraft.setScreen(new LootTableDataScreen(location, this, player));
            }
        }).size(100, 20).pos(this.width - 105, this.height - 25).build();
		if (allLootTables == null) {
			LootTableNetwork.sendToServer(new RequestLootTablesPacket());
		} else {
			reloadResourceLocation();
		}
		selectionList.setScrollAmount(this.scrollAmount);
		this.addRenderableWidget(searchBox);
		this.addRenderableWidget(searchToggle);
		this.addRenderableWidget(selectionList);
	}

	public void initWithLootTableData(List<ResourceLocation> recievedList) {
		recievedList.sort((rl1, rl2) -> {
			boolean firstVanilla = rl1.getNamespace().equals("minecraft");
			boolean secondVanilla = rl2.getNamespace().equals("minecraft");
			if (firstVanilla && !secondVanilla) return -1;
			if (!firstVanilla && secondVanilla) return 1;
			return rl1.getNamespace().compareTo(rl2.getNamespace());
		});
		allLootTables = recievedList;
		this.reloadResourceLocation();
	}

	public void reloadResourceLocation() {
		for (Button button : buttons) {
			this.removeWidget(button);
		}
		buttons = new ArrayList<>();
		betweenButtonSpaces = new ArrayList<>();
		emptyLocation = true;
		List<String> availablePaths = new ArrayList<>();
		List<ResourceLocation> lootTables;
		String namespace = null;
		String path = null;
		if (viewedResourceLocation.isEmpty()) {
            for (ResourceLocation lootTable : getFilteredLootTables(null, null)) {
				String lootTableNamespace = lootTable.getNamespace();
				if (searchToggle.selected()) {
					availablePaths.add(lootTable.getNamespace() + ":" + lootTable.getPath().substring(12));
				} else {
					if (!availablePaths.contains(lootTableNamespace)) {
					availablePaths.add(lootTableNamespace);
					}
				}
			}
		} else if (!viewedResourceLocation.contains(":")) {
			namespace = viewedResourceLocation;
			String finalNamespace = namespace;
			lootTables = getFilteredLootTables(finalNamespace, "loot_tables/");
			for (ResourceLocation lootTable : lootTables) {
				String subPath = lootTable.getPath();
				subPath = subPath.substring(12);
				String availablePath = subPath;
				if (subPath.contains("/")) {
					availablePath = subPath.substring(0, subPath.indexOf('/'));
				}
				if (!subPath.equals(availablePath) && searchToggle.selected()) {
					availablePaths.add(subPath);
				} else {
					if (!availablePaths.contains(availablePath)) {
						availablePaths.add(availablePath);
					}
				}
			}
		} else {
			String[] splitLocation = viewedResourceLocation.split(":");
			namespace = splitLocation[0];
			String finalNamespace = namespace;
			path = splitLocation[1];
			lootTables = getFilteredLootTables(finalNamespace, "loot_tables/" + path + '/');
			for (ResourceLocation lootTable : lootTables) {
				String subPath = lootTable.getPath();
				subPath = subPath.substring(path.length() + 13);
				String availablePath = subPath;
				if (subPath.contains("/")) {
					availablePath = subPath.substring(0, subPath.indexOf('/'));

				}
				if (!subPath.equals(availablePath) && searchToggle.selected()) {
					availablePaths.add(subPath);
				} else {
					if (!availablePaths.contains(availablePath)) {
						availablePaths.add(availablePath);
					}
				}
			}
		}
		selectionList.removeAllEntries();
        selectedLootTable = null;
        this.removeWidget(showDataButton);
		for (String availablePath : availablePaths) {
			selectionList.addEntry(availablePath, this.font, this::clickedOnString);
			this.emptyLocation = false;
		}
		selectionList.setScrollAmount(0.0);
		List<String> locationComponents = new ArrayList<>();
		locationComponents.add("data");
		if (namespace != null) {
			locationComponents.add(namespace);
			if (path != null) {
				locationComponents.addAll(List.of(path.split("/")));
			}
		}
		for (String locationComponent : locationComponents) {
			int buttonStartPos = betweenButtonSpaces.isEmpty() ? 10 : betweenButtonSpaces.get(betweenButtonSpaces.size() - 1) + 5;
			int buttonSize = this.font.width(locationComponent) + 12;
			buttons.add(Button.builder(Component.literal(locationComponent), btn -> this.clickedOnParentFolder(btn.getMessage().getString())).pos(buttonStartPos, this.height - 25).size(buttonSize, 18).build());
			betweenButtonSpaces.add(buttonStartPos + buttonSize + 5);
		}
		for (Button button : buttons) {
			this.addRenderableWidget(button);
		}
	}

	public void clickedOnParentFolder(String string) {
		if (allLootTables == null) return;
		if (string.equals("data")) {
			this.viewedResourceLocation = "";
		} else {
			this.viewedResourceLocation = this.viewedResourceLocation.substring(0, viewedResourceLocation.indexOf(string) + string.length());
		}
		this.reloadResourceLocation();
	}

	public void clickedOnString(String string) {
		if (allLootTables == null) return;
		if (string.endsWith(".json")) {
			backgroundGui.setLootTable(buildNewLocation(string));
			this.onClose();
			return;
		} else if (viewedResourceLocation.isEmpty()) {
			this.viewedResourceLocation = string;
		} else if (!viewedResourceLocation.contains(":")) {
			this.viewedResourceLocation = this.viewedResourceLocation + ":" + string;
		} else {
			this.viewedResourceLocation = this.viewedResourceLocation + "/" + string;
		}
		this.reloadResourceLocation();
	}

	public List<ResourceLocation> getFilteredLootTables(String namespace, String path) {
		List<ResourceLocation> filteredLootTables = new ArrayList<>();
        List<String> searchKeywords = List.of(searchBox.getValue().split(" "));
        lootTableLoop:
		for (ResourceLocation lootTable : allLootTables) {
			if (namespace != null && !lootTable.getNamespace().equals(namespace))
				continue;
			if (path != null && !lootTable.getPath().startsWith(path))
				continue;
            String location = lootTable.toString().replace("loot_tables/", "");
			for (String keyword : searchKeywords) {
                if (!location.contains(keyword)) {
                    continue lootTableLoop;
                }
			}
			filteredLootTables.add(lootTable);
		}
		lootTableCountMessage = filteredLootTables.size() == 1 ? Component.translatable("gui.loot_table_wand.loot_table_browsing.loot_table_count_1") : Component.translatable("gui.loot_table_wand.loot_table_browsing.loot_table_count", filteredLootTables.size())
;
		return filteredLootTables;
	}

	@Override
	public boolean keyPressed(int key, int x, int y) {
        if (super.keyPressed(key, x, y)) {
            return true;
        }
        if (key > 47 && key < 58) {
			LootTableSelectionList.Entry selectedEntry = selectionList.getSelected();
			if (selectedEntry == null || selectedEntry.isFolder())
				return false;
			String selectedLootTable = buildNewLocation(selectedEntry.getString());
			int slot = key == 48 ? 10 : key - 48;
			int i = (slot - 1) % 5;
			int j = (slot - 1) == i ? 0 : 1;
			String shortLocation = LootTableWandScreen.getShortenedLocation(selectedLootTable);
			if (changedParentButton != null) {
				this.removeWidget(changedParentButton);
			}
			this.changedParentButton = Button.builder(Component.literal(shortLocation), (btn) -> {
			}).pos(this.width / 2 + 5 + (210 * (j - 1)), (i + 3) * 25 + 30).size(200, 20).build();
			backgroundGui.setLootTable(slot - 1, selectedLootTable);
			this.addRenderableWidget(changedParentButton);
			buttonHideTime = System.nanoTime() + 300_000_000L;
			return true;
		}
		return false;
	}

	public String buildNewLocation(String string) {
		String stringWithoutFileExtension = string.endsWith(".json") ? string.substring(0, string.length() - 5) : string;
		if (viewedResourceLocation.isEmpty()) return stringWithoutFileExtension;
		char locationSeparator = viewedResourceLocation.contains(":") ? '/' : ':';
		return viewedResourceLocation + locationSeparator + stringWithoutFileExtension;
	}

    public void lootTableSelected(LootTableSelectionList.Entry entry) {
        if (!entry.isFolder()) {
            this.selectedLootTable = entry.getString();
            if (!this.children().contains(showDataButton)) {
                this.addRenderableWidget(showDataButton);
            }

        } else {
            this.selectedLootTable = entry.getString();
            this.removeWidget(showDataButton);
        }
    }

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		if (this.emptyLocation) {
			guiGraphics.drawCenteredString(this.font, Component.translatable("gui.loot_table_wand.loot_table_browsing.no_tables_found"), this.width / 2, 50, 0xFFFFFF);
		}
		guiGraphics.drawCenteredString(this.font, lootTableCountMessage, Math.max(2 + this.font.width(lootTableCountMessage) / 2, this.width / 2 - 160), 13, 0xFFFFFF);
		String separatorChar = ":";
		int i = -1;
		for (int betweenButtonSpace : betweenButtonSpaces) {
			guiGraphics.drawCenteredString(this.font, separatorChar, betweenButtonSpace, this.height - 20, 0xFFFFFF);
			separatorChar = "/";
			i = betweenButtonSpace + 5;
		}
		if (i != -1 && i < this.width - 110) {
			String selectedLootTable = this.selectedLootTable;
			if (i + font.width(selectedLootTable) > this.width - 115) {
				selectedLootTable = font.plainSubstrByWidth(selectedLootTable, this.width - 110 - i);
				selectedLootTable += "...";
			}
			guiGraphics.drawString(this.font, selectedLootTable, i, this.height - 20, 0xFFFFFF);
		}
		if (changedParentButton != null && System.nanoTime() > this.buttonHideTime) {
			this.removeWidget(changedParentButton);
			changedParentButton = null;
		}
	}

	@Override
	public void onClose() {
		super.onClose();
		LootTableWandPlayerDataManager.saveLocation(player, viewedResourceLocation);
		minecraft.setScreen(backgroundGui);
	}

    @Override
    protected void clearWidgets() {
        this.searchToggleSelected = searchToggle.selected();
        super.clearWidgets();
    }

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	public static class RespondingCheckbox extends Checkbox {
		private Consumer<Boolean> responder = null;

		public RespondingCheckbox(int i1, int i2, int i3, int i4, Component comp, boolean b) {
			super(i1, i2, i3, i4, comp, b);
		}

		@Override
		public void onPress() {
			super.onPress();
			if (responder != null) {
				responder.accept(this.selected());
			}
		}

		public void setResponder(Consumer<Boolean> consumer) {
			this.responder = consumer;
		}
	}
}
