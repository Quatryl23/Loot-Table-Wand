package net.mesomods.lootwand.client.gui;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum LootTableViewMode implements StringRepresentable {
    RAW_LIST(true, "raw_list"),
    LIST(true, "list");

    final boolean isList;
    final String name;

    public static final Codec<LootTableViewMode> CODEC = StringRepresentable.fromEnum(LootTableViewMode::values);

    LootTableViewMode(boolean isList, String name) {
        this.isList = isList;
        this.name = name;
    }

    public boolean isList() {
        return isList;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
