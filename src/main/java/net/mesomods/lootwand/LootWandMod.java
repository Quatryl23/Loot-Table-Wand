package net.mesomods.lootwand;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.mesomods.lootwand.attachments.ModAttachments;
import net.mesomods.lootwand.item.LootTableWandItem;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LootWandMod implements ModInitializer {
    public static final String MODID = "loot_table_wand";

    public static final SimpleParticleType ADD_LOOT_TABLE_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType REMOVE_LOOT_TABLE_PARTICLE = FabricParticleTypes.simple();

    private static final Set<Thread> probabilityCalculationThreads = new HashSet<>();
    private static ExecutorService probabilityCalculationExecutor;

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(MODID, "loot_table_add"), ADD_LOOT_TABLE_PARTICLE);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(MODID, "loot_table_remove"), REMOVE_LOOT_TABLE_PARTICLE);
        ModAttachments.initialize();
        ModItems.initialize();
        LootTableNetwork.registerPackets();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            probabilityCalculationExecutor = Executors.newCachedThreadPool(s -> {
                Thread thread = new Thread(s);
                thread.setDaemon(true);
                thread.setName("Loot Table Number Provider Probability Calculation (" + thread.getId() + ")");
                probabilityCalculationThreads.add(thread);
                return thread;
            });
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            probabilityCalculationExecutor.shutdown();
        });
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.is(ModItems.LOOT_TABLE_WAND)) {
                    return LootTableWandItem.leftClickOn(player, level, pos, InteractionHand.MAIN_HAND);
                }
            }
            return InteractionResult.PASS;
        });

    }

    public static void addProbabilityCalculation(Runnable calculation) {
        if (probabilityCalculationExecutor == null || probabilityCalculationExecutor.isShutdown()) return;
        probabilityCalculationExecutor.submit(calculation);
    }

    public static boolean isOnProbabilityCalculationThread() {
        return probabilityCalculationThreads.contains(Thread.currentThread());
    }
}
