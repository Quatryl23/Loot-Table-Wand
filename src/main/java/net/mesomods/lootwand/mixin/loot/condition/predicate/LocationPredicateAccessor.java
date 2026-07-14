package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.FluidPredicate;
import net.minecraft.advancements.critereon.LightPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.LocationPredicate.class)
public interface LocationPredicateAccessor {
    @Accessor
    MinMaxBounds.Doubles getX();

    @Mutable
    @Accessor
    void setX(MinMaxBounds.Doubles x);

    @Accessor
    MinMaxBounds.Doubles getY();

    @Mutable
    @Accessor
    void setY(MinMaxBounds.Doubles y);

    @Accessor
    MinMaxBounds.Doubles getZ();

    @Mutable
    @Accessor
    void setZ(MinMaxBounds.Doubles z);

    @Accessor
    ResourceKey<Biome> getBiome();

    @Mutable
    @Accessor
    void setBiome(ResourceKey<Biome> biome);

    @Accessor
    ResourceKey<Structure> getStructure();

    @Mutable
    @Accessor
    void setStructure(ResourceKey<Structure> structure);

    @Accessor
    ResourceKey<Level> getDimension();

    @Mutable
    @Accessor
    void setDimension(ResourceKey<Level> dimension);

    @Accessor
    Boolean getSmokey();

    @Mutable
    @Accessor
    void setSmokey(Boolean smokey);

    @Accessor
    LightPredicate getLight();

    @Mutable
    @Accessor
    void setLight(LightPredicate light);

    @Accessor
    BlockPredicate getBlock();

    @Mutable
    @Accessor
    void setBlock(BlockPredicate block);

    @Accessor
    FluidPredicate getFluid();

    @Mutable
    @Accessor
    void setFluid(FluidPredicate fluid);
}
