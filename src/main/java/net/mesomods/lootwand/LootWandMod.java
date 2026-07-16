package net.mesomods.lootwand;

import com.mojang.blaze3d.platform.InputConstants;
import net.mesomods.lootwand.client.LootTableWandRenderer;
import net.mesomods.lootwand.client.gui.LootTableWandDetailsOverlay;
import net.mesomods.lootwand.client.tooltip.*;
import net.mesomods.lootwand.item.LootTableWandItem;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mod(LootWandMod.MODID)
public class LootWandMod {
    public static final String MODID = "loot_table_wand";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> LOOT_TABLE_WAND = ITEMS.register("loot_table_wand", LootTableWandItem::new);

    public static final ResourceLocation WAND_MODEL = ResourceLocation.fromNamespaceAndPath(MODID, "item/loot_table_wand_default");

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
    public static final RegistryObject<SimpleParticleType> ADD_LOOT_TABLE_PARTICLE = PARTICLES.register("loot_table_add", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> REMOVE_LOOT_TABLE_PARTICLE = PARTICLES.register("loot_table_remove", () -> new SimpleParticleType(false));

    public static final Lazy<KeyMapping> INSPECT_KEY = Lazy.of(() -> new KeyMapping("key.loot_table_wand.inspect_loot_table", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, "key.categories.loot_table_wand"));
    public static final Lazy<KeyMapping> HIDE_KEYBINDS_KEY = Lazy.of(() -> new KeyMapping("key.loot_table_wand.toggle_keybind_info", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, "key.categories.loot_table_wand"));
    public static final Lazy<KeyMapping> NEXT_LOOT_TABLE_KEY = Lazy.of(() -> new KeyMapping("key.loot_table_wand.next_loot_table", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT, "key.categories.loot_table_wand"));
    public static final Lazy<KeyMapping> PREVIOUS_LOOT_TABLE_KEY = Lazy.of(() -> new KeyMapping("key.loot_table_wand.previous_loot_table", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT, "key.categories.loot_table_wand"));

    private static final Set<Thread> probabilityCalculationThreads = new HashSet<>();
    private static ExecutorService probabilityCalculationExecutor;

    private static LootTableWandRenderer renderer;

    public LootWandMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        ITEMS.register(modEventBus);
        PARTICLES.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LootTableNetwork.registerPackets(event);
    }

    public static LootTableWandRenderer getRenderer() {
        return renderer;
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.OP_BLOCKS) event.accept(LOOT_TABLE_WAND);
    }

    public static void addProbabilityCalculation(Runnable calculation) {
        if (probabilityCalculationExecutor == null || probabilityCalculationExecutor.isShutdown()) return;
        probabilityCalculationExecutor.submit(calculation);
    }

    public static boolean isOnProbabilityCalculationThread() {
        return probabilityCalculationThreads.contains(Thread.currentThread());
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            renderer = new LootTableWandRenderer();
        }

        @SubscribeEvent
        public static void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(IntProbabilityChartTooltip.class, ClientIntProbabilityChartTooltip::new);
            event.register(FloatProbabilityChartTooltip.class, ClientFloatProbabilityChartTooltip::new);
            event.register(LoadingProbabilityChartTooltip.class, (tooltip) -> new ClientLoadingProbabilityChartTooltip());
            event.register(ScoreDependentProbabilityChartTooltip.class, ClientScoreDependentProbabilityChartTooltip::new);
        }

        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerBelow(VanillaGuiOverlay.CHAT_PANEL.id(), "loot_table_wand_details", new LootTableWandDetailsOverlay());
        }

        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            event.register(INSPECT_KEY.get());
            event.register(HIDE_KEYBINDS_KEY.get());
            event.register(NEXT_LOOT_TABLE_KEY.get());
            event.register(PREVIOUS_LOOT_TABLE_KEY.get());
        }

        @SubscribeEvent
        public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
            event.register(WAND_MODEL);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ModEvents {
        @SubscribeEvent
        public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
            if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.START) {
                ItemStack stack = event.getItemStack();
                if (stack.is(LOOT_TABLE_WAND.get())) {
                    if (((LootTableWandItem) stack.getItem()).leftClickOn(event.getEntity(), event.getLevel(), event.getPos(), event.getHand()).consumesAction()) {
                        event.setCanceled(true);
                    }
                }
            }
        }
        @SubscribeEvent
        public static void onServerStarting(ServerStartingEvent event) {
            probabilityCalculationExecutor = Executors.newCachedThreadPool(s -> {
                Thread thread = new Thread(s);
                thread.setDaemon(true);
                thread.setName("Loot Table Number Provider Probability Calculation (" + thread.getId() + ")");
                probabilityCalculationThreads.add(thread);
                return thread;
            });
        }

        @SubscribeEvent
        public static void onServerStopping(ServerStoppingEvent event) {
            probabilityCalculationExecutor.shutdown();
        }
    }
}
