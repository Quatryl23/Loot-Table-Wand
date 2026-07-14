package net.mesomods.lootwand.client;

import net.mesomods.lootwand.LootWandMod;
import net.mesomods.lootwand.capabilities.LootTableWandPlayerDataManager;
import net.mesomods.lootwand.client.gui.screen.LootTableDataScreen;
import net.mesomods.lootwand.container.RandomizableContainerBlockEntityAccessor;
import net.mesomods.lootwand.item.LootTableWandItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LootWandMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientTicker {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.phase == TickEvent.Phase.END && mc.player != null && mc.level != null && mc.screen == null && mc.player.getMainHandItem().is(LootWandMod.LOOT_TABLE_WAND.get())) {
            while (LootWandMod.INSPECT_KEY.get().consumeClick()) {
                BlockEntity be = mc.hitResult == null ? null : mc.level.getBlockEntity(BlockPos.containing(mc.hitResult.getLocation()));
                if (be instanceof RandomizableContainerBlockEntityAccessor container && !mc.player.isSecondaryUseActive()) {
                    ResourceLocation lootTable = container.getLootTable();
                    if (lootTable != null)
                        mc.setScreen(new LootTableDataScreen(lootTable, null, mc.player));
                } else {
                    ResourceLocation lootTable = LootTableWandItem.getActiveLootTable(mc.player.getMainHandItem());
                    if (lootTable != null)
                        mc.setScreen(new LootTableDataScreen(lootTable, null, mc.player));
                }
            }
            while (LootWandMod.HIDE_KEYBINDS_KEY.get().consumeClick()) {
                if (mc.player != null) {
                    LootTableWandPlayerDataManager.toggleKeybindsShown(mc.player);
                }
            }
            while (LootWandMod.PREVIOUS_LOOT_TABLE_KEY.get().consumeClick()) {
                if (mc.player != null) {
                    ItemStack stack = mc.player.getMainHandItem();
                    if (!stack.is(LootWandMod.LOOT_TABLE_WAND.get())) return;
                    LootTableWandItem.decreaseLootTableIndex(stack, mc.player);
                }
            }
            while (LootWandMod.NEXT_LOOT_TABLE_KEY.get().consumeClick()) {
                if (mc.player != null) {
                    ItemStack stack = mc.player.getMainHandItem();
                    if (!stack.is(LootWandMod.LOOT_TABLE_WAND.get())) return;
                    LootTableWandItem.increaseLootTableIndex(stack, mc.player);
                }
            }
        }
    }
}
