package net.mesomods.lootwand.particle;

import net.mesomods.lootwand.LootWandMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.mesomods.lootwand.LootWandMod.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class LootTableParticleRegisterSubscriber {
    @SubscribeEvent
    public static void onParticleProviderRegisterEvent(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(LootWandMod.ADD_LOOT_TABLE_PARTICLE.get(), LootTableParticle.Provider::new);
        event.registerSpriteSet(LootWandMod.REMOVE_LOOT_TABLE_PARTICLE.get(), LootTableParticle.Provider::new);
    }
}
