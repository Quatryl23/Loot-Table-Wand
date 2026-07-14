package net.mesomods.lootwand.client.gui;

public enum LootTableViewMode {
    RAW_LIST(true),
    LIST(true);

    final boolean isList;

    LootTableViewMode(boolean isList) {
        this.isList = isList;
    }

    public boolean isList() {
        return isList;
    }
}
