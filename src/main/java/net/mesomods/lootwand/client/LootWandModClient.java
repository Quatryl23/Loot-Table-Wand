package net.mesomods.lootwand.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.mesomods.lootwand.LootWandMod;
import net.mesomods.lootwand.ModItems;
import net.mesomods.lootwand.attachments.LootTableWandPlayerDataManager;
import net.mesomods.lootwand.client.gui.screen.LootTableDataScreen;
import net.mesomods.lootwand.client.tooltip.*;
import net.mesomods.lootwand.container.RandomizableContainerBlockEntityAccessor;
import net.mesomods.lootwand.item.LootTableWandItem;
import net.mesomods.lootwand.particle.LootTableParticle;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lwjgl.glfw.GLFW;

public class LootWandModClient implements ClientModInitializer {
    public static final KeyMapping INSPECT_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.loot_table_wand.inspect_loot_table", GLFW.GLFW_KEY_K, "key.categories.loot_table_wand"));
    public static final KeyMapping HIDE_KEYBINDS_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.loot_table_wand.toggle_keybind_info", GLFW.GLFW_KEY_J, "key.categories.loot_table_wand"));
    public static final KeyMapping NEXT_LOOT_TABLE_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.loot_table_wand.next_loot_table", GLFW.GLFW_KEY_RIGHT, "key.categories.loot_table_wand"));
    public static final KeyMapping PREVIOUS_LOOT_TABLE_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.loot_table_wand.previous_loot_table", GLFW.GLFW_KEY_LEFT, "key.categories.loot_table_wand"));

    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance().register(LootWandMod.ADD_LOOT_TABLE_PARTICLE, LootTableParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(LootWandMod.REMOVE_LOOT_TABLE_PARTICLE, LootTableParticle.Provider::new);

        ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register((be, level) -> {
            if (be instanceof RandomizableContainerBlockEntityAccessor r) {
                r.lootmod$onLoad();
            }
        });

        TooltipComponentCallback.EVENT.register((data) -> {
            if (data instanceof FloatProbabilityChartTooltip f) {
                return new ClientFloatProbabilityChartTooltip(f);
            } else if (data instanceof IntProbabilityChartTooltip i) {
                return new ClientIntProbabilityChartTooltip(i);
            } else if (data instanceof LoadingProbabilityChartTooltip) {
                return new ClientLoadingProbabilityChartTooltip();
            } else if (data instanceof ScoreDependentProbabilityChartTooltip s) {
                return new ClientScoreDependentProbabilityChartTooltip(s);
            }
            return null;
        });

        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.LOOT_TABLE_WAND, LootTableWandRenderer.INSTANCE);

        ClientTickEvents.END_CLIENT_TICK.register((mc) -> {
            if (mc.level == null || mc.player == null) return;
            while (INSPECT_KEY.consumeClick()) {
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
            while (HIDE_KEYBINDS_KEY.consumeClick()) {
                if (mc.player != null) {
                    LootTableWandPlayerDataManager.toggleKeybindsShown(mc.player);
                }
            }
            while (PREVIOUS_LOOT_TABLE_KEY.consumeClick()) {
                if (mc.player != null) {
                    ItemStack stack = mc.player.getMainHandItem();
                    if (!stack.is(ModItems.LOOT_TABLE_WAND)) return;
                    LootTableWandItem.decreaseLootTableIndex(stack, mc.player);
                }
            }
            while (NEXT_LOOT_TABLE_KEY.consumeClick()) {
                if (mc.player != null) {
                    ItemStack stack = mc.player.getMainHandItem();
                    if (!stack.is(ModItems.LOOT_TABLE_WAND)) return;
                    LootTableWandItem.increaseLootTableIndex(stack, mc.player);
                }
            }
        });
    }
}
