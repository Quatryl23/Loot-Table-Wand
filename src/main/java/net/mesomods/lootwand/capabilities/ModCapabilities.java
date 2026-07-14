package net.mesomods.lootwand.capabilities;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCapabilities {
	public static Capability<ILootTableWandPlayerData> LOOT_TABLE_WAND_PLAYER_DATA = CapabilityManager.get(new CapabilityToken<>() {
	});

	public static <T> T getPlayerData(Player player, Function<ILootTableWandPlayerData, T> function, T defaultValue) {
		AtomicReference<T> result = new AtomicReference<>(defaultValue);
		player.getCapability(LOOT_TABLE_WAND_PLAYER_DATA).ifPresent((data) -> result.set(function.apply(data)));
		return result.get();
	}

	public static void setPlayerData(Player player, Consumer<ILootTableWandPlayerData> setter) {
		player.getCapability(LOOT_TABLE_WAND_PLAYER_DATA).ifPresent(setter::accept);
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(ILootTableWandPlayerData.class);
	}
}
