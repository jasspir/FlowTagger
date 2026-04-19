package you.jass.flowtagger.ui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.input.KeyInput;

import java.awt.*;

public class UILabel implements UIElement {
    public final int x, y;
    public final TextRenderer textRenderer;
    public final String text;
    public final boolean centered;
    public final UITheme theme;
    public final boolean gradient;

    public UILabel(int x, int y, TextRenderer textRenderer, String text, UITheme theme, boolean gradient, boolean centered) {
        this.x = x;
        this.y = y;
        this.textRenderer = textRenderer;
        this.text = text;
        this.centered = centered;
        this.theme = theme;
        this.gradient = gradient;
    }

    @Override
    public void render(Object renderer, int mx, int my) {
        Color base = theme.text();
        if (gradient) UIUtils.drawGradientText(renderer, textRenderer, text, x, y - 4, base.brighter(), base.darker(), centered);
        else UIUtils.drawText(renderer, textRenderer, text, x, y - 4, base, centered);
    }

    @Override public boolean keyPressed(KeyInput input) {return false;}
    @Override public boolean mouseClicked(double mx, double my, int button) { return false; }
    @Override public boolean mouseDragged(double mx, double my, int button, double dx, double dy) { return false; }
    @Override public boolean mouseReleased(double mx, double my, int button) { return false; }
}
