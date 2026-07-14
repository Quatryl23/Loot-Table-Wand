package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@org.spongepowered.asm.mixin.Mixin(targets = "net.minecraft.world.level.storage.loot.functions.SetAttributesFunction$Modifier")
public interface SetAttributesFunctionModifierAccessor {
    @Accessor
    String getName();

    @Mutable
    @Accessor
    void setName(String name);

    @Accessor
    Attribute getAttribute();

    @Mutable
    @Accessor
    void setAttribute(Attribute attribute);

    @Accessor
    AttributeModifier.Operation getOperation();

    @Mutable
    @Accessor
    void setOperation(AttributeModifier.Operation operation);

    @Accessor
    NumberProvider getAmount();

    @Mutable
    @Accessor
    void setAmount(NumberProvider amount);

    @Accessor
    UUID getId();

    @Mutable
    @Accessor
    void setId(UUID id);

    @Accessor
    EquipmentSlot[] getSlots();

    @Mutable
    @Accessor
    void setSlots(EquipmentSlot[] slots);
}
