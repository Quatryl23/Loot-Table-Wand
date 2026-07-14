package net.mesomods.lootwand.capabilities;

import net.mesomods.lootwand.client.gui.LootTableViewMode;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.UpdateLootTableWandPlayerDataPacket;
import net.mesomods.lootwand.network.packet.server.WandItemPreviewPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LootTableWandPlayerDataLoginSync {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            LazyOptional<ILootTableWandPlayerData> cap = player.getCapability(ModCapabilities.LOOT_TABLE_WAND_PLAYER_DATA);
            AtomicBoolean synchronizeWands = new AtomicBoolean(LootTableWandPlayerData.SYNCHRONIZE_WANDS_DEFAULT);
            AtomicBoolean emptyTargetContainer = new AtomicBoolean(LootTableWandPlayerData.EMPTY_TARGET_CONTAINER_DEFAULT);
            AtomicInteger previewTime = new AtomicInteger(LootTableWandPlayerData.PREVIEW_TIME_DEFAULT);
            LootTableViewMode[] viewMode = new LootTableViewMode[]{LootTableWandPlayerData.LOOT_TABLE_VIEW_MODE_DEFAULT};
            NumberProviderTooltipMode[] tooltipMode = new NumberProviderTooltipMode[]{LootTableWandPlayerData.NUMBER_PROVIDER_TOOLTIP_MODE_DEFAULT};
            AtomicBoolean countPreview = new AtomicBoolean(LootTableWandPlayerData.COUNT_PREVIEW_DEFAULT);
            AtomicBoolean hideDefaults = new AtomicBoolean(LootTableWandPlayerData.HIDE_DEFAULTS_DEFAULT);
            AtomicBoolean keybindsShown = new AtomicBoolean(LootTableWandPlayerData.KEYBINDS_SHOWN_DEFAULT);
            float[] luck = new float[]{LootTableWandPlayerData.LUCK_DEFAULT};
            cap.ifPresent((data) -> {
                        synchronizeWands.set(data.getSynchronizeWands());
                        emptyTargetContainer.set(data.getEmptyTargetContainer());
                        previewTime.set(data.getPreviewTime());
                        viewMode[0] = data.getLootTableViewMode();
                        tooltipMode[0] = data.getNumberProviderTooltipMode();
                        countPreview.set(data.getCountPreview());
                        hideDefaults.set(data.getHideDefaults());
                        keybindsShown.set(data.getKeybindsShown());
                        luck[0] = (data.getLuck());
                    });
            LootTableNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new UpdateLootTableWandPlayerDataPacket(keybindsShown.get(), emptyTargetContainer.get(), synchronizeWands.get(), previewTime.get(), viewMode[0], tooltipMode[0], countPreview.get(), hideDefaults.get(), luck[0]));
            WandItemPreviewPacket.sendPreviewListUpdate(serverPlayer);
        }
    }
}