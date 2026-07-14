package net.mesomods.lootwand.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;

public class LootTableParticle extends TextureSheetParticle {
    protected LootTableParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, TextureAtlasSprite sprite) {
        super(world, x, y, z, vx, vy, vz);
        this.lifetime = 20;
        this.sprite = sprite;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {
        @Override
            public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
                return new LootTableParticle(level, x, y, z, vx, vy, vz, spriteSet.get(0, 1));
            }
        }
}
