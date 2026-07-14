package net.mesomods.lootwand.loot.lifoc;

import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.loot.lifoc.parameters.MultiParameter;
import net.mesomods.lootwand.loot.lifoc.parameters.NestedConditionParameter;
import net.mesomods.lootwand.loot.lifoc.parameters.RenderableParameter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RenderedCondition extends RenderedLIFOC {
    public static final MutableComponent DESCRIPTION_PREFIX = Component.literal("- ");
    public static final int PREFIX_WIDTH = ScreenUtils.FONT.width(DESCRIPTION_PREFIX);
    public final Component normalDescription;
    public final Component invertedDescription;
    public final RenderableParameter normalDescriptionParameter;
    public final RenderableParameter invertedDescriptionParameter;
    public boolean inverted = false;

    RenderedCondition(Component description, Component invertedDescription, List<RenderableParameter> parameters, PreviewEffect previewEffect, boolean hiddenByDefault) {
        this(description, invertedDescription, null, null, parameters, previewEffect, hiddenByDefault);
    }

    RenderedCondition(Component description, Component invertedDescription, @Nullable RenderableParameter descriptionParameter, @Nullable RenderableParameter invertedDescriptionParameter, List<RenderableParameter> parameters, PreviewEffect previewEffect, boolean hiddenByDefault) {
        this(description, invertedDescription, descriptionParameter, invertedDescriptionParameter, parameters, previewEffect, true, hiddenByDefault);
    }

    RenderedCondition(Component description, Component invertedDescription, @Nullable RenderableParameter descriptionParameter, @Nullable RenderableParameter invertedDescriptionParameter, List<RenderableParameter> parameters, PreviewEffect previewEffect, boolean renderOutline, boolean hiddenByDefault) {
        super(previewEffect, DESCRIPTION_PREFIX.copy().append(description), descriptionParameter, parameters, renderOutline, hiddenByDefault, false);
        this.normalDescription = DESCRIPTION_PREFIX.copy().append(description);
        this.invertedDescription = DESCRIPTION_PREFIX.copy().append(invertedDescription);
        this.normalDescriptionParameter = descriptionParameter;
        this.invertedDescriptionParameter = invertedDescriptionParameter;
        getAllParameters().forEach(parameter -> {
            parameter.setHeightUpdater(() -> {
                this.updateHeight(true);
            });
        });
    }

    RenderedCondition(@Nullable RenderableParameter descriptionParameter, @Nullable RenderableParameter invertedDescriptionParameter, List<RenderableParameter> parameters, PreviewEffect previewEffect, boolean hiddenByDefault) {
        this(Component.empty(), Component.empty(), descriptionParameter, invertedDescriptionParameter, parameters, previewEffect, hiddenByDefault);
    }

    public static RenderedCondition fromVanilla(LootItemCondition condition) {
        ResourceLocation rl = BuiltInRegistries.LOOT_CONDITION_TYPE.getKey(condition.getType());
        if (rl == null) return null;
        ConditionDefinition<?> definition = LootItemConditionDefinitions.get(rl);
        if (definition == null) {
            Component unknown = Component.translatable("gui.loot_table_wand.condition.unknown").append(" ").append(Component.literal(rl.toString()).withStyle(ChatFormatting.GRAY));
            return new RenderedCondition(unknown, unknown, List.of(), PreviewEffect.NONE, false);
        }
        return definition.buildRenderedCondition(condition);
    }

    public static RenderedCondition alwaysTrue() {
        return new RenderedCondition(Component.translatable("gui.loot_table_wand.condition.true"), Component.translatable("gui.loot_table_wand.condition.false"), List.of(), PreviewEffect.NONE, true);
    }

    public void invert() {
        this.inverted = !this.inverted;
        this.descriptionParameter = inverted ? invertedDescriptionParameter : normalDescriptionParameter;
        this.description = inverted ? invertedDescription : normalDescription;
        updateDescription();
        for (RenderableParameter parameter : getAllParameters()) {
            if (parameter instanceof NestedConditionParameter c && this.description.equals(this.invertedDescription)) {
                c.invertCondition();
            } else if (parameter instanceof MultiParameter m) {
                m.setMergeDescriptions(!inverted);
            }
        }
    }

    @Override
    public List<RenderableParameter> getAllParameters() {
        List<RenderableParameter> parameters = super.getAllParameters();
        if (normalDescriptionParameter != null) parameters.add(normalDescriptionParameter);
        if (invertedDescriptionParameter != null) parameters.add(invertedDescriptionParameter);
        return parameters;
    }

    @Override
    public void updateHeight(boolean updateParent) {
        updateParameterHeight(updateParent);
    }

    @Override
    public boolean mouseClickedAdditional(double x, double y, int key, int renderLeft, int renderTop) {
        return false;
    }

    @Override
    public Component getDescriptionPrefix() {
        return DESCRIPTION_PREFIX;
    }

    @Override
    public boolean hasConditions() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int x0, int x1, int y, int transparency, boolean skipUpperOutlinePart, int mouseX, int mouseY) {
        renderParameters(graphics, x0, x1, y, transparency, skipUpperOutlinePart, mouseX, mouseY);
    }

}
