package net.mesomods.lootwand;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mesomods.lootwand.item.LootTableWandItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final Item LOOT_TABLE_WAND = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(LootWandMod.MODID, "loot_table_wand"), new LootTableWandItem());

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.OP_BLOCKS).register((group) -> {
            if (Minecraft.getInstance().options.operatorItemsTab().get()) group.accept(LOOT_TABLE_WAND);
        });
    }
}
