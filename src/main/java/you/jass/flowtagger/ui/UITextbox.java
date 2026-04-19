package you.jass.flowtagger.ui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.input.KeyInput;

import java.awt.*;
import java.util.function.Consumer;

public class UITextbox implements UIElement {
    public final int x, y, width, height;
    public final TextRenderer textRenderer;
    public String text;
    public String placeholder;
    public boolean focused;
    public int maxLength;
    public final Consumer<String> onChange;
    public final UITheme theme;
    public final boolean gradient;

    private static final int PADDING = 6;

    public UITextbox(int x, int y, int width, int height, TextRenderer textRenderer, UITheme theme, boolean gradient, String initial, String placeholder, int maxLength, Consumer<String> onChange) {
        this.x = x;
        this.y = y - 7;
        this.width = width;
        this.height = height;
        this.textRenderer = textRenderer;
        this.theme = theme;
        this.gradient = gradient;
        this.text = initial == null ? "" : initial;
        this.placeholder = placeholder == null ? "" : placeholder;
        this.maxLength = Math.max(1, maxLength);
        this.onChange = onChange;
    }

    @Override
    public void render(Object renderer, int mx, int my) {
        boolean hovered = isHovered(mx, my);

        Color textColor = theme.text();
        if (text.isEmpty() && !focused) textColor = theme.border();
        else if (hovered || focused) textColor = theme.highlighted();

        UIUtils.drawRectangle(renderer, x, y, width, height, theme.background());
        UIUtils.drawBorder(renderer, x, y, width, height, theme.border());

        int textY = y + (height - textRenderer.fontHeight) / 2 + 1;
        int innerWidth = width - (PADDING * 2);

        if (text.isEmpty() && !focused) UIUtils.drawText(renderer, textRenderer, placeholder, x + PADDING, textY, theme.border(), false);
        else {
            String visibleText = getVisibleText(innerWidth);

            if (gradient && !focused && !text.isEmpty()) UIUtils.drawGradientText(renderer, textRenderer, visibleText, x + PADDING, textY, textColor.brighter(), textColor.darker(), false);
            else UIUtils.drawText(renderer, textRenderer, visibleText, x + PADDING, textY, textColor, false);

            if (focused && (System.currentTimeMillis() / 500L) % 2L == 0L) {
                int cursorX = x + PADDING + textRenderer.getWidth(visibleText);
                if (cursorX > x + width - PADDING) cursorX = x + width - PADDING;
                UIUtils.drawRectangle(renderer, cursorX, y + 2, 1, height - 4, textColor);
            }
        }
    }

    private String getVisibleText(int innerWidth) {
        if (textRenderer.getWidth(text) <= innerWidth) {
            return text;
        }

        int start = text.length();
        while (start > 0) {
            String candidate = text.substring(start - 1);
            if (textRenderer.getWidth(candidate) > innerWidth) {
                break;
            }
            start--;
        }

        return text.substring(start);
    }

    private boolean isHovered(double mx, double my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            focused = isHovered(mx, my);
            if (focused) playSound();
            return focused;
        }

        return false;
    }

    public boolean keyPressed(KeyInput input) {
        if (!focused) return false;

        int key = input.key();

        if (key == 259) {
            if (!text.isEmpty()) {
                text = text.substring(0, text.length() - 1);
                onChange.accept(text);
            }
            return true;
        }

        if (key == 261) {
            text = "";
            onChange.accept(text);
            return true;
        }

        if (key == 257 || key == 335 || key == 256) {
            focused = false;
            return true;
        }

        char chr = keyToChar(input);
        if (chr != 0 && text.length() < maxLength) {
            text += chr;
            onChange.accept(text);
            return true;
        }

        return false;
    }

    private char keyToChar(KeyInput input) {
        int key = input.key();
        boolean shift = input.hasShift();

        if (key >= 65 && key <= 90) {
            char c = (char) key;
            return shift ? c : Character.toLowerCase(c);
        }

        if (key >= 48 && key <= 57) {
            return switch (key) {
                case 48 -> shift ? ')' : '0';
                case 49 -> shift ? '!' : '1';
                case 50 -> shift ? '@' : '2';
                case 51 -> shift ? '#' : '3';
                case 52 -> shift ? '$' : '4';
                case 53 -> shift ? '%' : '5';
                case 54 -> shift ? '^' : '6';
                case 55 -> shift ? '&' : '7';
                case 56 -> shift ? '*' : '8';
                case 57 -> shift ? '(' : '9';
                default -> 0;
            };
        }

        return switch (key) {
            case 32 -> ' ';
            case 44 -> shift ? '<' : ',';
            case 45 -> shift ? '_' : '-';
            case 46 -> shift ? '>' : '.';
            case 47 -> shift ? '?' : '/';
            case 59 -> shift ? ':' : ';';
            case 61 -> shift ? '+' : '=';
            case 39 -> shift ? '"' : '\'';
            case 91 -> shift ? '{' : '[';
            case 92 -> shift ? '|' : '\\';
            case 93 -> shift ? '}' : ']';
            case 96 -> shift ? '~' : '`';
            default -> 0;
        };
    }

    @Override public boolean mouseDragged(double mx, double my, int button, double dx, double dy) { return false; }
    @Override public boolean mouseReleased(double mx, double my, int button) { return false; }
}