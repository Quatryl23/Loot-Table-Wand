package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(CopyBlockState.class)
public interface CopyBlockStateAccessor {
    @Accessor
    Block getBlock();

    @Accessor
    void setBlock(Block block);

    @Accessor
    Set<Property<?>> getProperties();

    @Accessor
    void setProperties(Set<Property<?>> properties);


}
