package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction.class)
public interface ExplorationMapFunctionAccessor {
    @Accessor
    TagKey<Structure> getDestination();

    @Mutable
    @Accessor
    void setDestination(TagKey<Structure> destination);

    @Accessor
    MapDecoration.Type getMapDecoration();

    @Mutable
    @Accessor
    void setMapDecoration(MapDecoration.Type mapDecoration);

    @Accessor
    byte getZoom();

    @Mutable
    @Accessor
    void setZoom(byte zoom);

    @Accessor
    int getSearchRadius();

    @Mutable
    @Accessor
    void setSearchRadius(int searchRadius);

    @Accessor("skipKnownStructures")
    boolean skipKnownStructures();

    @Mutable
    @Accessor
    void setSkipKnownStructures(boolean skipKnownStructures);
}
