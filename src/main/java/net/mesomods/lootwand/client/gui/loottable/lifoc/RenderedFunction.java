package net.mesomods.lootwand.client.gui.loottable.lifoc;

import net.mesomods.lootwand.client.gui.loottable.lifoc.parameters.RenderableParameter;
import net.mesomods.lootwand.mixin.loot.function.LootItemConditionalFunctionAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RenderedFunction extends RenderedLIFOC {
    public static final MutableComponent DESCRIPTION_PREFIX = Component.literal("• ");
    final List<RenderedCondition> conditions;

    RenderedFunction(Component description, List<RenderableParameter> parameters, PreviewEffect previewEffect, List<RenderedCondition> conditions, boolean hiddenByDefault) {
        this(description, null, parameters, previewEffect, conditions, hiddenByDefault);
    }

    RenderedFunction(Component description, @Nullable RenderableParameter descriptionParameter, List<RenderableParameter> parameters, PreviewEffect previewEffect, List<RenderedCondition> conditions, boolean hiddenByDefault) {
        this(description, descriptionParameter, parameters, previewEffect, conditions, true, hiddenByDefault);
    }

    RenderedFunction(Component description, @Nullable RenderableParameter descriptionParameter, List<RenderableParameter> parameters, PreviewEffect previewEffect, List<RenderedCondition> conditions, boolean renderOutline, boolean hiddenByDefault) {
        super(previewEffect, DESCRIPTION_PREFIX.copy().append(description), descriptionParameter, parameters, renderOutline, hiddenByDefault, !conditions.isEmpty());
        this.conditions = conditions;
        getAllParameters().forEach(parameter -> parameter.setHeightUpdater(() -> this.updateHeight(true)));
    }

    RenderedFunction(RenderableParameter descriptionParameter, List<RenderableParameter> parameters, PreviewEffect previewEffect, List<RenderedCondition> conditions, boolean hiddenByDefault) {
        this(Component.empty(), descriptionParameter, parameters, previewEffect, conditions, hiddenByDefault);
    }

    public static RenderedFunction fromVanilla(LootItemFunction function) {
        ResourceLocation rl = BuiltInRegistries.LOOT_FUNCTION_TYPE.getKey(function.getType());
        List<RenderedCondition> conditions = function instanceof LootItemConditionalFunctionAccessor f ? Arrays.stream(f.getPredicates()).map(RenderedCondition::fromVanilla).toList() : List.of();
        if (rl == null) return null;
        FunctionDefinition<?> definition = LootItemFunctionDefinitions.get(rl);
        if (definition == null) {
            return new RenderedFunction(Component.translatable("gui.loot_table_wand.function.unknown").append(" ").append(Component.literal(rl.toString()).withStyle(ChatFormatting.GRAY)), List.of(), PreviewEffect.NONE, conditions, false);
        }
        return definition.buildRenderedFunction(function, conditions);
    }

    public static RenderedFunction doesNothing() {
        return new RenderedFunction(Component.translatable("gui.loot_table_wand.condition.true"), List.of(), PreviewEffect.NONE, List.of(), true);
    }

    @Override
    public List<RenderableParameter> getAllParameters() {
        List<RenderableParameter> parameters = super.getAllParameters();
        if (descriptionParameter != null) parameters.add(descriptionParameter);
        return parameters;
    }

    @Override
    public void toggleDefaultParameters(boolean hidden) {
        for (RenderedCondition condition : conditions) {
            condition.toggleDefaultParameters(hidden);
        }
        super.toggleDefaultParameters(hidden);
    }

    @Override
    public void updateHeight(boolean updateParent) {
        updateParameterHeight(updateParent);
        if (hasVisibleConditions()) height += 3;
        for (RenderedCondition condition : conditions) {
            if (!condition.isHidden())
                height += condition.getHeight();
        }
    }

    @Override
    public boolean mouseClickedAdditional(double x, double y, int key, int renderLeft, int renderTop) {
        int y0 = renderTop + 3;
        int y1;
        for (RenderedCondition condition : conditions) {
            y1 = y0 + condition.getHeight();
            if (y0 < y && y < y1 && renderLeft < x) {
                return condition.mouseClicked(x, y, key, renderLeft, y0);
            }
            y0 = y1;
        }
        return false;
    }

    public boolean hasVisibleConditions() {
        for (RenderedCondition condition : conditions) {
            if (!condition.isHidden()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Component getDescriptionPrefix() {
        return DESCRIPTION_PREFIX;
    }

    @Override
    public boolean hasConditions() {
        return !conditions.isEmpty();
    }

    @Override
    public void render(GuiGraphics graphics, int x0, int x1, int y, int transparency, boolean skipUpperOutlinePart, int mouseX, int mouseY) {
        y = renderParameters(graphics, x0, x1, y, transparency, skipUpperOutlinePart, mouseX, mouseY) + 3;
        boolean isFirst = true;
        for (RenderedCondition condition : conditions) {
            if (!condition.isHidden()) {
                condition.render(graphics, x0 + 2, x1 - 2, y, transparency + 1, !isFirst, mouseX, mouseY);
                y += condition.getHeight();
                isFirst = false;
            }
        }
    }

}
