package net.mesomods.lootwand.loot.lifoc.parameters;

import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.loot.lifoc.LIFOCDefinition;
import net.mesomods.lootwand.loot.lifoc.ParameterNest;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static net.mesomods.lootwand.client.ScreenUtils.FONT;

public class InlineMultiParameter extends RenderableParameter implements ParameterNest {
    List<? extends RenderableParameter> children;
    List<MutableComponent> descriptionParts = new ArrayList<>();
    List<RenderedPart> renderedParts;
    protected boolean renderChildrenTooltips;
    protected int height;
    protected int width;

    public InlineMultiParameter(RenderableParameter... parameters) {
        this(List.of(parameters), (String) null);
    }


    public InlineMultiParameter(List<? extends RenderableParameter> list, @Nullable String translationKey) {
        if (translationKey != null) {
            MutableComponent component = Component.translatable(translationKey);
            String string = component.getString();
            if (!string.endsWith(Parameter.Description.PLACEHOLDER)) {
                string += " ";
            }
            String[] stringParts = string.split(Parameter.Description.PLACEHOLDER);
            for (String stringPart : stringParts) {
                descriptionParts.add(Component.literal(stringPart));
            }
        }
        this.children = list;
        if (descriptionParts.isEmpty()) {
            renderedParts = children.stream().map((param) -> (RenderedPart) new ParameterPart(param)).toList();
        } else {
            int maxLength = Math.max(descriptionParts.size(), children.size());
            renderedParts = new ArrayList<>();
            for (int i = 0; i < maxLength; i++) {
                if (descriptionParts.size() > i) {
                    MutableComponent component = descriptionParts.get(i);
                    if (i == maxLength - 1 && children.size() <= i)
                        component = component.append(Component.literal(" "));
                    RenderedPart part = new DescriptionPart(component);
                    renderedParts.add(part);
                }
                if (children.size() > i) {
                    RenderedPart part = new ParameterPart(children.get(i));
                    renderedParts.add(part);
                }
            }
        }
    }

    protected InlineMultiParameter(List<? extends RenderableParameter> list, List<RenderedPart> renderedParts) {
        this.children = list;
        int i = 0;
        for (RenderedPart part : renderedParts) {
            if (part instanceof ParameterPart paramPart) {
                paramPart.updateParameter(list.get(i));
                i++;
            }
        }
        this.renderedParts = renderedParts;
        this.updateHeightWidth();
    }

    public void updateHeightWidth() {
        this.height = descriptionParts.isEmpty() ? 0 : ScreenUtils.TEXT_HEIGHT;
        for (RenderableParameter param : children) {
            if (param.isHidden()) continue;
            this.height = Math.max(this.height, param.getHeight());
        }
        this.width = 0;
        for (RenderedPart part : renderedParts) {
            if (part instanceof ParameterPart p && p.parameter.isHidden()) continue;
            width += part.getWidth();
            if (part instanceof ParameterPart) {
                width += 1;
            }
        }
        this.renderChildrenTooltips = this.height > Parameter.TEXT_HEIGHT;
        this.tooltip = renderChildrenTooltips ? null : Tooltip.create(getComponent());
    }

    public <T> InlineMultiParameter build(List<LIFOCDefinition.BuildableParameterDefinition<T, ?>> definitions, T rawFunction) {
        List<RenderableParameter> builtChildren = new ArrayList<>();
        for (LIFOCDefinition.BuildableParameterDefinition<T, ?> definition : definitions) {
            RenderableParameter builtParameter = definition.build(rawFunction);
            builtChildren.add(builtParameter);
        }
        return new InlineMultiParameter(builtChildren, renderedParts.stream().map(RenderedPart::copyOf).toList());
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int x0, int x1, int y, int transparency, int mouseX, int mouseY) {
        int x = x0;
        for (RenderedPart part : renderedParts) {
            if (part instanceof ParameterPart p && p.parameter.isHidden()) continue;
            part.render(graphics, x, x1, y, transparency, renderChildrenTooltips ? mouseX : -mouseX, renderChildrenTooltips ? mouseY : -mouseY);
            x += part.getWidth();
            if (part instanceof ParameterPart) {
                x += 1;
            }
        }
        if (!renderChildrenTooltips) this.renderTooltip(width, x0, x1, y, FONT.lineHeight, mouseX, mouseY);
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

    @Override
    public void mergeDescriptions(DescriptionMerger<?> merger, Component component) {
        for (RenderedPart part : renderedParts) {
            if (part instanceof DescriptionPart d) {
                d.text = ((DescriptionMerger<DescriptionComponent>) merger).apply(component, d.text);
                d.width = FONT.width(d.text.component);
            }
        }
        this.updateHeightWidth();
    }

    @Override
    public @NotNull Component getComponent() {
        MutableComponent component = Component.empty();
        for (RenderedPart part : renderedParts) {
            if (part instanceof ParameterPart p) {
                component.append(p.parameter.getComponent());
            } else if (part instanceof DescriptionPart d) {
                component.append(d.text.component);
            }
        }
        return component;
    }

    protected interface RenderedPart {
        void render(GuiGraphics graphics, int x, int x1, int y, int transparency, int mouseX, int mouseY);
        RenderedPart copyOf();
        int getWidth();
    }

    static class ParameterPart implements RenderedPart {
        RenderableParameter parameter;

        public ParameterPart(RenderableParameter parameter) {
            this.parameter = parameter;
        }

        @Override
        public void render(GuiGraphics graphics, int x, int x1, int y, int transparency, int mouseX, int mouseY) {
            parameter.render(graphics, x, x1, y, transparency, mouseX, mouseY);
        }

        @Override
        public ParameterPart copyOf() {
            return new ParameterPart(parameter);
        }

        public void updateParameter(RenderableParameter parameter) {
            this.parameter = parameter;
        }

        @Override
        public int getWidth() {
            return parameter.getWidth();
        }
    }

    static class DescriptionPart implements RenderedPart {
        DescriptionComponent text;
        int width;

        public DescriptionPart(Component text) {
            this.text = DescriptionComponent.of(text);
            this.width = FONT.width(text);
        }

        @Override
        public void render(GuiGraphics graphics, int x, int x1, int y, int transparency, int mouseX, int mouseY) {
            graphics.drawString(FONT, text.component, x, y, Parameter.WHITE);
        }

        @Override
        public DescriptionPart copyOf() {
            return new DescriptionPart(text.component);
        }

        @Override
        public int getWidth() {
            return width;
        }
    }
}
