package you.jass.flowtagger.ui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.input.KeyInput;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class UIDropdown implements UIElement {
    public final int x, y, width, height, gap;
    public final TextRenderer textRenderer;
    public final List<String> options;
    public int selected;
    public boolean open;
    public final Consumer<Integer> onChange;
    public final UITheme theme;
    public final boolean gradient;

    public UIDropdown(int x, int y, int width, int height, int gap, TextRenderer textRenderer, List<String> options, UITheme theme, boolean gradient, int initial, Consumer<Integer> onChange) {
        this.x = x;
        this.y = y - 7;
        this.width = width;
        this.height = height;
        this.gap = gap;
        this.textRenderer = textRenderer;
        this.options = options;
        this.selected = Math.max(0, Math.min(initial, options.size() - 1));
        this.onChange = onChange;
        this.theme = theme;
        this.gradient = gradient;
    }

    @Override
    public void render(Object renderer, int mx, int my) {
        boolean hovered = isHovered(mx, my);

        Color baseText = hovered || open ? theme.highlighted() : theme.text();
        Color bg = theme.background();
        Color border = theme.border();

        String current = options.isEmpty() ? "" : options.get(selected);
        int textY = y + (height - textRenderer.fontHeight) / 2 + 1;

        if (!open) {
            UIUtils.drawRectangle(renderer, x, y, width, height, bg);
            UIUtils.drawBorder(renderer, x, y, width, height, border);

            if (!current.isEmpty() && gradient && !hovered) {
                UIUtils.drawGradientText(renderer, textRenderer, current, x + 6, textY, baseText.brighter(), baseText.darker(), false);
            } else {
                UIUtils.drawText(renderer, textRenderer, current, x + 6, textY, baseText, false);
            }

            String arrow = "^";
            int arrowWidth = textRenderer.getWidth(arrow);
            UIUtils.drawText(renderer, textRenderer, arrow, x + width - arrowWidth - 6, textY + 2, baseText, false);

            return;
        }

        int totalHeight = height * (options.size() + 1);

        UIUtils.drawRectangle(renderer, x, y, width, totalHeight, bg);
        UIUtils.drawBorder(renderer, x, y, width, totalHeight, border);

        if (!current.isEmpty() && gradient) {
            UIUtils.drawGradientText(renderer, textRenderer, current, x + 6, textY, baseText.brighter(), baseText.darker(), false);
        } else {
            UIUtils.drawText(renderer, textRenderer, current, x + 6, textY, baseText, false);
        }

        String arrow = "^";
        int arrowWidth = textRenderer.getWidth(arrow);
        UIUtils.drawText(renderer, textRenderer, arrow, x + width - arrowWidth - 6, textY + 2, baseText, false);

        for (int i = 0; i < options.size(); i++) {
            String option = options.get(i);
            int optionY = y + height + (i * height);
            boolean optionHovered = mx >= x && mx <= x + width && my >= optionY && my <= optionY + height;

            Color optionText = optionHovered ? theme.highlighted() : theme.text();

            if (gradient && !optionHovered) {
                UIUtils.drawGradientText(renderer, textRenderer, option, x + 6, optionY + (height - textRenderer.fontHeight) / 2 - 1, optionText.brighter(), optionText.darker(), false);
            } else {
                UIUtils.drawText(renderer, textRenderer, option, x + 6, optionY + (height - textRenderer.fontHeight) / 2 - 1, optionText, false);
            }
        }
    }

    private boolean isHovered(double mx, double my) {
        int optionHeight = open ? height * (options.size() + 1) : height;
        return mx >= x && mx <= x + width && my >= y && my <= y + optionHeight;
    }

    private int hoveredOption(double mx, double my) {
        if (!open) return -1;

        int optionY = y + height;
        for (int i = 0; i < options.size(); i++) {
            int adjusted = optionY + (i * height);
            if (mx >= x && mx <= x + width && my >= adjusted && my <= adjusted + height) return i;
        }
        return -1;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button != 0) return false;

        if (isHovered(mx, my) && !open && !UIUtils.isFocused) {
            open = true;
            UIUtils.isFocused = true;
            playSound();
            return true;
        }

        if (open) {
            int option = hoveredOption(mx, my);
            if (option != -1) {
                selected = option;
                onChange.accept(selected);
                open = false;
                UIUtils.isFocused = false;
                playSound();
                return true;
            }

            open = false;
            UIUtils.isFocused = false;
        }

        return false;
    }

    @Override public boolean keyPressed(KeyInput input) {return false;}
    @Override public boolean mouseDragged(double mx, double my, int button, double dx, double dy) { return false; }
    @Override public boolean mouseReleased(double mx, double my, int button) { return false; }
}