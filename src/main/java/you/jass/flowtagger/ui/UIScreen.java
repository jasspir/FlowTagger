package you.jass.flowtagger.ui;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import you.jass.flowtagger.settings.Settings;
import you.jass.flowtagger.settings.Toggle;
import you.jass.flowtagger.utility.Format;
import you.jass.flowtagger.utility.MultiVersion;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UIScreen extends Screen {
    private List<UIElement> widgets = new ArrayList<>();

    public UIScreen() {
        super(Text.of("Custom Settings"));
    }

    @Override
    protected void init() {
        super.init();

        if (Settings.getBoolean("tutorial")) Settings.set("tutorial", "false");
        Settings.load();

        UIUtils.isFocused = false;
        widgets.clear();

        int panelWidthCenter = width / 2;
        int panelHeightCenter = height / 2;
        int panelWidth = 300;
        int panelHeight = 245;
        int halfPanelWidth = panelWidth / 2;
        int halfPanelHeight = panelHeight / 2;
        int column1Start = 145;
        int rowStart = 125;
        int verticalGap = 14;
        int descriptionStart = 45;

        List<String> gamemodes = List.of("⚔ Automatic", "⚔ Best Elo", "⚔ Best Rank", "⭐ Global", "🗡 Sword", "🪓 Axe", "🪣 UHC", "☀ Vanilla", "☄ Mace", "⚗ DPot", "☠ NOP", "🌧 DSMP", "☁ NSMP");
        List<String> tagPositions = List.of("Above", "Above+", "Prefix", "Suffix");
        List<String> otherPositions = List.of("Prefix", "Suffix");

        Color background = new Color(0, 0, 0, 230);
        Color border = new Color(142, 142, 142, 255);
        Color text = new Color(255, 255, 255, 255);
        Color hovered = new Color(160, 241, 255, 255);
        Color highlighted = new Color(105, 219, 242, 255);
        UITheme theme = new UITheme(background, border, text, hovered, highlighted);
        UITheme panel = new UITheme(background, background, background, background, background);
        UITheme header = new UITheme(highlighted, highlighted, highlighted, highlighted, highlighted);
        UITheme description = new UITheme(border, border, border, border, border);
        UITheme footer = new UITheme(border.darker(), border.darker(), border.darker(), border.darker(), border.darker());

        widgets.add(new UIPanel(panelWidthCenter - halfPanelWidth, panelHeightCenter - halfPanelHeight, panelWidth, panelHeight, panel, false));

        widgets.add(new UILabel(
                panelWidthCenter,
                panelHeightCenter - halfPanelHeight + 10,
                textRenderer, "FlowTagger v1.0.0",
                header, true, true
        ));

        widgets.add(new UILabel(
                panelWidthCenter,
                panelHeightCenter + halfPanelHeight - 10,
                textRenderer, "Made by Jass • Modrinth.com/mod/flowtagger • " + MultiVersion.getVersion(),
                footer, true, true
        ));

        widgets.add(new UICheckbox(
                panelWidthCenter - column1Start,
                panelHeightCenter - rowStart + verticalGap * 2,
                10, 280, textRenderer, "Tag",
                theme, false, Toggle.TAG.toggled(), checked -> Toggle.TAG.toggle()
        ));

        widgets.add(new UIDropdown(
                panelWidthCenter - column1Start + 28,
                panelHeightCenter - rowStart + verticalGap * 2,
                78, 12, 10, textRenderer, gamemodes, theme, false,
                gamemodes.indexOf(Format.getIcon(Settings.get("tag_gamemode")) + " " + Settings.get("tag_gamemode")),
                selected -> Settings.set("tag_gamemode", gamemodes.get(selected).replaceFirst("^\\S+\\s", ""))
        ));

        widgets.add(new UIDropdown(
                panelWidthCenter - column1Start + 108,
                panelHeightCenter - rowStart + verticalGap * 2,
                55, 12, 10, textRenderer, tagPositions, theme, false,
                tagPositions.indexOf(Settings.get("tag_position")),
                selected -> Settings.set("tag_position", tagPositions.get(selected))
        ));

        widgets.add(new UITextbox(
                panelWidthCenter - column1Start + 165,
                panelHeightCenter - rowStart + verticalGap * 2,
                110, 12, textRenderer, theme, false,
                Settings.get("tag_format"), "?icon !tier &7(!elo&7)", 100,
                typed -> Settings.set("tag_format", typed)
        ));

        widgets.add(new UICheckbox(
                panelWidthCenter - column1Start,
                panelHeightCenter - rowStart + verticalGap * 3,
                10, 280, textRenderer, "Tab",
                theme, false, Toggle.TAB.toggled(), checked -> Toggle.TAB.toggle()
        ));

        widgets.add(new UIDropdown(
                panelWidthCenter - column1Start + 28,
                panelHeightCenter - rowStart + verticalGap * 3,
                78, 12, 10, textRenderer, gamemodes, theme, false,
                gamemodes.indexOf(Format.getIcon(Settings.get("tab_gamemode")) + " " + Settings.get("tab_gamemode")),
                selected -> Settings.set("tab_gamemode", gamemodes.get(selected).replaceFirst("^\\S+\\s", ""))
        ));

        widgets.add(new UIDropdown(
                panelWidthCenter - column1Start + 108,
                panelHeightCenter - rowStart + verticalGap * 3,
                55, 12, 10, textRenderer, otherPositions, theme, false,
                otherPositions.indexOf(Settings.get("tab_position")),
                selected -> Settings.set("tab_position", otherPositions.get(selected))
        ));

        widgets.add(new UITextbox(
                panelWidthCenter - column1Start + 165,
                panelHeightCenter - rowStart + verticalGap * 3,
                110, 12, textRenderer,
                theme, false,
                Settings.get("tab_format"), "?icon !tier &7(!elo&7)", 100,
                typed -> Settings.set("tab_format", typed)
        ));

        widgets.add(new UICheckbox(
                panelWidthCenter - column1Start,
                panelHeightCenter - rowStart + verticalGap * 4,
                10, 280, textRenderer, "Chat",
                theme, false, Toggle.CHAT.toggled(), checked -> Toggle.CHAT.toggle()
        ));

        widgets.add(new UIDropdown(
                panelWidthCenter - column1Start + 28,
                panelHeightCenter - rowStart + verticalGap * 4,
                78, 12, 10, textRenderer, gamemodes, theme, false,
                gamemodes.indexOf(Format.getIcon(Settings.get("chat_gamemode")) + " " + Settings.get("chat_gamemode")),
                selected -> Settings.set("chat_gamemode", gamemodes.get(selected).replaceFirst("^\\S+\\s", ""))
        ));

        widgets.add(new UIDropdown(
                panelWidthCenter - column1Start + 108,
                panelHeightCenter - rowStart + verticalGap * 4,
                55, 12, 10, textRenderer, otherPositions, theme, false,
                otherPositions.indexOf(Settings.get("chat_position")),
                selected -> Settings.set("chat_position", otherPositions.get(selected))
        ));

        widgets.add(new UITextbox(
                panelWidthCenter - column1Start + 165,
                panelHeightCenter - rowStart + verticalGap * 4,
                110, 12, textRenderer, theme, false,
                Settings.get("chat_format"), "?icon !tier &7(!elo&7)", 100,
                typed -> Settings.set("chat_format", typed)
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start,
                panelHeightCenter - rowStart + verticalGap * 5,
                textRenderer, "tier",
                theme, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start,
                panelHeightCenter - rowStart + verticalGap * 6,
                textRenderer, "elo",
                theme, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start,
                panelHeightCenter - rowStart + verticalGap * 7,
                textRenderer, "rank",
                theme, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start,
                panelHeightCenter - rowStart + verticalGap * 8,
                textRenderer, "mode",
                theme, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start,
                panelHeightCenter - rowStart + verticalGap * 9,
                textRenderer, "icon",
                theme, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start,
                panelHeightCenter - rowStart + verticalGap * 10,
                textRenderer, "streak",
                theme, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start,
                panelHeightCenter - rowStart + verticalGap * 11,
                textRenderer, "matches",
                theme, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start,
                panelHeightCenter - rowStart + verticalGap * 12,
                textRenderer, "wins",
                theme, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start,
                panelHeightCenter - rowStart + verticalGap * 13,
                textRenderer, "losses",
                theme, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start,
                panelHeightCenter - rowStart + verticalGap * 14,
                textRenderer, "?",
                theme, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start,
                panelHeightCenter - rowStart + verticalGap * 15,
                textRenderer, "!",
                theme, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start,
                panelHeightCenter - rowStart + verticalGap * 16,
                textRenderer, "&",
                theme, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start + descriptionStart,
                panelHeightCenter - rowStart + verticalGap * 5,
                textRenderer, "\"Emerald III\" for example, not including color",
                description, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start + descriptionStart,
                panelHeightCenter - rowStart + verticalGap * 6,
                textRenderer, "the score rating of the player",
                description, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start + descriptionStart,
                panelHeightCenter - rowStart + verticalGap * 7,
                textRenderer, "the player's rank on the leaderboard",
                description, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start + descriptionStart,
                panelHeightCenter - rowStart + verticalGap * 8,
                textRenderer, "the name of the gamemode",
                description, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start + descriptionStart,
                panelHeightCenter - rowStart + verticalGap * 9,
                textRenderer, "the icon of the gamemode",
                description, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start + descriptionStart,
                panelHeightCenter - rowStart + verticalGap * 10,
                textRenderer, "the player's current win streak",
                description, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start + descriptionStart,
                panelHeightCenter - rowStart + verticalGap * 11,
                textRenderer, "the amount of matches the player has played",
                description, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start + descriptionStart,
                panelHeightCenter - rowStart + verticalGap * 12,
                textRenderer, "the amount of matches the player has won",
                description, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start + descriptionStart,
                panelHeightCenter - rowStart + verticalGap * 13,
                textRenderer, "the amount of matches the player has lost",
                description, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start + descriptionStart,
                panelHeightCenter - rowStart + verticalGap * 14,
                textRenderer, "the color of the gamemode, use &f to stop it",
                description, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start + descriptionStart,
                panelHeightCenter - rowStart + verticalGap * 15,
                textRenderer, "the color of the players tier, use &f to stop it",
                description, false, false
        ));

        widgets.add(new UILabel(
                panelWidthCenter - column1Start + descriptionStart,
                panelHeightCenter - rowStart + verticalGap * 16,
                textRenderer, "a standard minecraft color code",
                description, false, false
        ));

        //to make the dropdowns not cover each other
        Collections.reverse(widgets.subList(2, widgets.size()));
    }

    @Override
    public void close() {
        Format.reset();
        super.close();
    }

    //version 1.19.4
//    @Override
//    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
//        for (UIElement element : widgets) {
//            element.render(matrixStack, mouseX, mouseY);
//        }
//        super.render(matrixStack, mouseX, mouseY, delta);
//    }

    //version 1.20+
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        for (UIElement element : widgets) {
            element.render(ctx, mouseX, mouseY);
        }
        super.render(ctx, mouseX, mouseY, delta);
    }

    //version 1.19.4
//    @Override
//    public void renderBackground(MatrixStack matrixStack) {}

    //version 1.20 - 1.20.1
//    @Override
//    public void renderBackground(DrawContext context) {}

    //version 1.20.2+
    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {}

    //version 1.21.8-
//    @Override
//    public boolean mouseClicked(double mx, double my, int button) {
//        for (UIElement element : widgets) if (element.mouseClicked(mx, my, button)) return true;
//        return super.mouseClicked(mx, my, button);
//    }

    //version 1.21.8-
//    @Override
//    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
//        for (UIElement element : widgets) if (element.mouseDragged(mx, my, button, dx, dy)) return true;
//        return false;
//    }

    //version 1.21.8-
//    @Override
//    public boolean mouseReleased(double mx, double my, int button) {
//        for (UIElement element : widgets) if (element.mouseReleased(mx, my, button)) return true;
//        return super.mouseReleased(mx, my, button);
//    }

    //version 1.21.9+
    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        for (UIElement element : widgets) if (element.mouseClicked(click.x(), click.y(), click.button())) return true;
        return super.mouseClicked(click, doubled);
    }

    //version 1.21.9+
    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        for (UIElement element : widgets) if (element.mouseDragged(click.x(), click.y(), click.button(), offsetX, offsetY)) return true;
        return super.mouseDragged(click, offsetX, offsetY);
    }

    //version 1.21.9+
    @Override
    public boolean mouseReleased(Click click) {
        for (UIElement element : widgets) if (element.mouseReleased(click.x(), click.y(), click.button())) return true;
        return super.mouseReleased(click);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        for (UIElement element : widgets) if (element instanceof UITextbox textbox && textbox.keyPressed(input)) return true;
        return super.keyPressed(input);
    }
}