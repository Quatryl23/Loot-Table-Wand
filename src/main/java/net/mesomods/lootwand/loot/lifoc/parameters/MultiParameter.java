package net.mesomods.lootwand.loot.lifoc.parameters;

import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.loot.lifoc.LIFOCDefinition;
import net.mesomods.lootwand.loot.lifoc.ParameterNest;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.mesomods.lootwand.client.ScreenUtils.FONT;

public class MultiParameter extends RenderableParameter implements ParameterNest {
    DescriptionComponent description;
    boolean renderSingleEntry = false;
    boolean hideDefaultParameters = false;
    boolean mergeDescriptions = true;
    RenderableParameter singleEntry;
    public static final int INDENT_Y = (ScreenUtils.TEXT_HEIGHT - ScreenUtils.FONT_HEIGHT) / 2;

    public boolean isIndented() {
        return indented;
    }

    public Component getDescription() {
        return description == null ? null : description.component;
    }

    public Map<Integer, DescriptionMerger<?>> getDescriptionMergers() {
        return descriptionMergers;
    }

    final Map<Integer, DescriptionMerger<?>> descriptionMergers;
    final List<? extends RenderableParameter> children;
    protected final boolean indented;
    protected int height;
    protected int width;

    public MultiParameter(List<? extends RenderableParameter> list, Component description, boolean indented) {
        this(list, description, (Map<Integer, DescriptionMerger<?>>) null, indented);
    }

    public MultiParameter(List<? extends RenderableParameter> list, Component description, DescriptionMerger<?> descriptionMerger, boolean indented) {
        this(list, description, Map.of(-23, descriptionMerger), indented);
    }

    public MultiParameter(List<? extends RenderableParameter> list, Component description, Map<Integer, DescriptionMerger<?>> descriptionMergers, boolean indented) {
        this.indented = indented;
        this.children = list;
        this.description = DescriptionComponent.of(description);
        this.descriptionMergers = descriptionMergers;
    }

    private MultiParameter(List<? extends RenderableParameter> list, DescriptionComponent description, DescriptionMerger<?> descriptionMerger, boolean indented) {
        this.indented = indented;
        this.children = list;
        this.description = description;
        this.descriptionMergers = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            this.descriptionMergers.put(i, descriptionMerger);
        }
        this.updateHeightWidth();
    }

    private MultiParameter(List<? extends RenderableParameter> list, DescriptionComponent description, Map<Integer, DescriptionMerger<?>> descriptionMergers, boolean indented) {
        this.indented = indented;
        this.children = list;
        this.description = description;
        this.descriptionMergers = descriptionMergers;
        this.updateHeightWidth();
    }

    public static MultiParameter unset(Component description) {
        return new MultiParameter(List.of(), description == null ? null : description.copy().append(Component.literal(" ").append(Parameter.Description.NULL_VALUE)), false);
    }

    public MultiParameter unset() {
        return new MultiParameter(List.of(), description == null ? null : description.component.copy().append(Component.literal(" ").append(Parameter.Description.NULL_VALUE)), false);
    }

    public <T> MultiParameter build(List<LIFOCDefinition.BuildableParameterDefinition<T, ?>> definitions, T rawFunction) {
        return build(definitions, this.description, this.descriptionMergers, this.indented, rawFunction);
    }

    public <T> MultiParameter build(List<LIFOCDefinition.BuildableParameterDefinition<T, ?>> definitions, DescriptionComponent description, Map<Integer, DescriptionMerger<?>> descriptionMergers, boolean indented, T rawFunction) {
        List<RenderableParameter> builtChildren = new ArrayList<>();
        for (LIFOCDefinition.BuildableParameterDefinition<T, ?> definition : definitions) {
            builtChildren.add(definition.build(rawFunction));
        }
        return descriptionMergers != null && descriptionMergers.containsKey(-23) ? new MultiParameter(builtChildren, description,  descriptionMergers.get(-23), indented) : new MultiParameter(builtChildren, description,  descriptionMergers, indented);
    }

    public void updateHeightWidth() {
        if (renderSingleEntry) {
            this.height = singleEntry.getHeight();
        } else {
            this.height = description != null ? ScreenUtils.TEXT_HEIGHT : 0;
            for (RenderableParameter param : children) {
                if (param.isHidden()) continue;
                this.height += param.getHeight();
            }
        }
        if (renderSingleEntry) {
            this.width = singleEntry.getWidth();
        } else {
            this.width = 2;
            for (RenderableParameter param : children) {
                if (param.isHidden()) continue;
                this.width = Math.max(width, param.getWidth() + 2);
                if (param.getWidth() == -1) {
                    this.width = -1;
                    break;
                }
            }
        }
        this.tooltip = description == null ? null : Tooltip.create(getComponent());
    }

    public int getSingleEntryIndex() {
        return singleEntry != null && children.contains(singleEntry) ? children.indexOf(singleEntry) : -1;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    public void prepareSingleEntry() {
        if (descriptionMergers != null && mergeDescriptions) {
            for (int i = 0; i < children.size(); i++) {
                RenderableParameter param = children.get(i);
                if (!param.isHidden() && descriptionMergers.containsKey(i)) {
                    singleEntry = param;
                    singleEntry.mergeDescriptions(descriptionMergers.get(i), description == null ? Component.empty() : description.component);
                    renderSingleEntry = true;
                }
            }
            this.updateHeightWidth();
        }
    }

    public void prepareMultipleEntries() {
        if (singleEntry != null) {
            singleEntry.mergeDescriptions(DescriptionMerger.UNDO, description == null ? Component.empty() : description.component);
        }
        singleEntry = null;
        renderSingleEntry = false;
        this.updateHeightWidth();
    }

    public void setMergeDescriptions(boolean mergeDescriptions) {
        this.mergeDescriptions = mergeDescriptions;
    }

    @Override
    public void mergeDescriptions(DescriptionMerger<?> merger, Component component) {
        this.description = ((DescriptionMerger<DescriptionComponent>) merger).apply(component, this.description);
        this.updateHeightWidth();
    }

    @Override
    public @NotNull Component getComponent() {
        return description == null ? Component.empty() : description.component;
    }

    public int getVisibleEntryCount() {
        int i = 0;
        for (RenderableParameter param : children) {
            if (!param.isHidden()) i++;
        }
        return i;
    }

    @Override
    public boolean isDefault() {
        return this.getVisibleEntryCount() == 0;
    }

    @Override
    public void toggleDefaultParameters(boolean hidden) {
        this.hideDefaultParameters = hidden;
        ParameterNest.super.toggleDefaultParameters(hidden);
        if (getVisibleEntryCount() == 1) {
            prepareSingleEntry();
        } else {
            prepareMultipleEntries();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int x, int x1, int y, int transparency, int mouseX, int mouseY) {
        if (renderSingleEntry) {
            this.renderSingleEntry(graphics, x, x1, y, transparency, mouseX, mouseY);
            return;
        }
        if (description != null) {
            this.renderTooltip(FONT.width(description.component), x, x1, y, FONT.lineHeight, mouseX, mouseY);
        }
        if (description != null) {
            graphics.drawString(Parameter.FONT, description.component, x, y, Parameter.WHITE);
            y += ScreenUtils.TEXT_HEIGHT;
        }
        int y0 = y;
        if (indented) {
            x += 4;
            transparency += 1;
        }
        for (RenderableParameter param : children) {
            if (param.isHidden()) continue;
            param.render(graphics, x, x1, y, transparency, mouseX, mouseY);
            y += param.getHeight();
        }
        if (indented) {
            graphics.fill(x - 3, y0 - INDENT_Y, x - 2, y - INDENT_Y - 1, ScreenUtils.getOutlineColor(transparency));
        }
    }

    public void renderSingleEntry(GuiGraphics graphics, int x, int x1, int y, int transparency, int mouseX, int mouseY) {
        this.singleEntry.render(graphics, x, x1, y, transparency, mouseX, mouseY);
    }

    @Override
    public List<RenderableParameter> getNestedParameters() {
        return this.children.stream().map((param) -> (RenderableParameter) param).toList();
    }

    @Override
    public void updateHeight(boolean updateParents) {
        this.updateHeightWidth();
        if (updateParents) this.updateLifocHeight();
    }

}
