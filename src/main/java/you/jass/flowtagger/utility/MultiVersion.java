package you.jass.flowtagger.utility;

//version 1.19.4
//import net.minecraft.client.gui.DrawableHelper;

//version 1.20.4-
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.particle.DefaultParticleType;

//version 1.20.5-
//import net.minecraft.nbt.NbtElement;

//version 1.20+
//import net.minecraft.client.gui.DrawContext;

//version 1.20.5+
//import net.minecraft.particle.SimpleParticleType;

//version 1.21 - 1.21.4
//import net.minecraft.client.render.VertexFormat;

//version 1.21.5 - 1.21.10
//import com.mojang.blaze3d.buffers.GpuBuffer;
//import com.mojang.blaze3d.vertex.VertexFormat;

//version 1.21.11
import net.minecraft.client.gui.DrawContext;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.*;

import you.jass.flowtagger.ui.UIUtils;

import java.awt.*;
import java.util.UUID;

import static you.jass.flowtagger.FlowTagger.client;

public class MultiVersion {
    public static String getVersion() {
        //version 1.19.4
        //return "1.19.4";

        //version 1.20
        //return "1.20";

        //version 1.20.1
        //return "1.20.1";

        //version 1.20.2
        //return "1.20.2";

        //version 1.20.3
        //return "1.20.3";

        //version 1.20.4
        //return "1.20.4";

        //version 1.20.5
        //return "1.20.5";

        //version 1.20.6
        //return "1.20.6";

        //version 1.21
        //return "1.21";

        //version 1.21.1
        //return "1.21.1";

        //version 1.21.2
        //return "1.21.2";

        //version 1.21.3
        //return "1.21.3";

        //version 1.21.4
        //return "1.21.4";

        //version 1.21.5
        //return "1.21.5";

        //version 1.21.6
        //return "1.21.6";

        //version 1.21.7
        //return "1.21.7";

        //version 1.21.8
        //return "1.21.8";

        //version 1.21.9
        //return "1.21.9";

        //version 1.21.10
        //return "1.21.10";

        //version 1.21.11
        return "1.21.11";
    }

    public static void message(String message, String command) {
        if (client.player == null) return;
        message = message.replace("&", "§");

        Text hoverText = Text.of("FlowTagger message");
        //version 1.21.4-
//        ClickEvent clickEvent = new ClickEvent(!settingHitreg ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND, command);
//        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText);

        //version 1.21.5+
        ClickEvent clickEvent = new ClickEvent.RunCommand(command);
        HoverEvent hoverEvent = new HoverEvent.ShowText(hoverText);

        Text text = Text.literal("FlowTagger §8|§r " + message).setStyle(Style.EMPTY
                .withColor(TextColor.fromRgb(0x69DBF2))
                .withClickEvent(clickEvent)
                .withHoverEvent(hoverEvent));

         client.player.sendMessage(text, false);
    }

    public static UUID getId(GameProfile profile) {
        //version 1.21.8-
        //return profile.getId();

        //version 1.21.9+
        return profile.id();
    }

    public static String getName(GameProfile profile) {
        //version 1.21.8-
        //return profile.getName();

        //version 1.21.9+
        return profile.name();
    }

    public static void drawRectangle(Object renderer, int x, int y, int w, int h, Color c) {
        if (w <= 0 || h <= 0 || c == null) return;

        //version 1.19.4
        //MatrixStack ms = (MatrixStack) renderer;
        //DrawableHelper.fill(ms, x, y, x + w, y + h, c.getRGB());

        //version 1.20+
        DrawContext ctx = (DrawContext) renderer;
        ctx.fill(x, y, x + w, y + h, c.getRGB());
    }

    public static void drawGradientRectangle(Object renderer, int x, int y, int w, int h, Color start, Color end) {
        if (w <= 0 || h <= 0 || start == null || end == null) return;

        //version 1.19.4
        //MatrixStack ms = (MatrixStack) renderer;
        //for (int i = 0; i < h; i++) {
        //float t = (h > 1) ? (float) i / (h - 1) : 0f;
        //Color blended = UIUtils.blend(start, end, t);
        //DrawableHelper.fill(ms, x, y + i, x + w, y + i + 1, blended.getRGB());
        //}

        //version 1.20+
        DrawContext ctx = (DrawContext) renderer;
        for (int i = 0; i < h; i++) {
            float t = (h > 1) ? (float) i / (h - 1) : 0f;
            Color blended = UIUtils.blend(start, end, t);
            ctx.fill(x, y + i, x + w, y + i + 1, blended.getRGB());
        }
    }

    public static void drawHorizontalGradient(Object renderer, int x, int y, int w, int h, Color leftColor, Color rightColor) {
        if (w <= 0 || h <= 0 || leftColor == null || rightColor == null) return;

        //version 1.19.4
        //MatrixStack ms = (MatrixStack) renderer;
        //for (int i = 0; i < w; i++) {
        //float t = (w > 1) ? (float) i / (w - 1) : 0f;
        //Color blended = UIUtils.blend(leftColor, rightColor, t);
        //DrawableHelper.fill(ms, x + i, y, x + i + 1, y + h, blended.getRGB());
        //}

        //version 1.20+
        DrawContext ctx = (DrawContext) renderer;
        for (int i = 0; i < w; i++) {
            float t = (w > 1) ? (float) i / (w - 1) : 0f;
            Color blended = UIUtils.blend(leftColor, rightColor, t);
            ctx.fill(x + i, y, x + i + 1, y + h, blended.getRGB());
        }
    }

    public static void drawBorder(Object renderer, int x, int y, int w, int h, Color c) {
        if (w <= 0 || h <= 0 || c == null) return;

        //version 1.19.4
        //MatrixStack ms = (MatrixStack) renderer;
        //DrawableHelper.drawBorder(ms, x, y, w, h, c.getRGB());

        //version 1.20 - 1.21.8
//        DrawContext ctx = (DrawContext) renderer;
//        ctx.drawBorder(x, y, w, h, c.getRGB());

        //version 1.21.9+
        DrawContext ctx = (DrawContext) renderer;
        ctx.drawStrokedRectangle(x, y, w, h, c.getRGB());
    }

    public static void drawGradientBorder(Object renderer, int x, int y, int w, int h, Color start, Color end) {
        if (w <= 0 || h <= 0 || start == null || end == null) return;

        //version 1.19.4
        //MatrixStack ms = (MatrixStack) renderer;
        //DrawableHelper.enableScissor(x, y, x + w, y + 1);
        //drawGradientRectangle(ms, x, y, w, h, start, end);
        //DrawableHelper.disableScissor();
        //DrawableHelper.enableScissor(x, y, x + 1, y + h);
        //drawGradientRectangle(ms, x, y, w, h, start, end);
        //DrawableHelper.disableScissor();
        //DrawableHelper.enableScissor(x, y + h - 1, x + w, y + h);
        //drawGradientRectangle(ms, x, y, w, h, start, end);
        //DrawableHelper.disableScissor();
        //DrawableHelper.enableScissor(x + w - 1, y, x + w, y + h);
        //drawGradientRectangle(ms, x, y, w, h, start, end);
        //DrawableHelper.disableScissor();

        //version 1.20+
        DrawContext ctx = (DrawContext) renderer;
        ctx.enableScissor(x, y, x + w, y + 1);
        drawGradientRectangle(ctx, x, y, w, h, start, end);
        ctx.disableScissor();
        ctx.enableScissor(x, y, x + 1, y + h);
        drawGradientRectangle(ctx, x, y, w, h, start, end);
        ctx.disableScissor();
        ctx.enableScissor(x, y + h - 1, x + w, y + h);
        drawGradientRectangle(ctx, x, y, w, h, start, end);
        ctx.disableScissor();
        ctx.enableScissor(x + w - 1, y, x + w, y + h);
        drawGradientRectangle(ctx, x, y, w, h, start, end);
        ctx.disableScissor();
    }

    public static void drawText(Object renderer, TextRenderer tr, String s, int x, int y, Color c, boolean center) {
        if (s == null || tr == null || c == null) return;
        if (center) x -= tr.getWidth(s) / 2;

        //version 1.19.4
        //MatrixStack ms = (MatrixStack) renderer;
        //tr.drawWithShadow(ms, s, x, y, c.getRGB());

        //version 1.20+
        DrawContext ctx = (DrawContext) renderer;
        ctx.drawTextWithShadow(tr, s, x, y, c.getRGB());
    }

    public static void drawGradientText(Object renderer, TextRenderer tr, String s, int x, int y, Color start, Color end, boolean center) {
        if (s == null || tr == null || start == null || end == null) return;

        if (center) x -= tr.getWidth(s) / 2;
        final int last = s.length() - 1;
        int cx = x;

        //version 1.19.4
//        MatrixStack ms = (MatrixStack) renderer;
//        for (int i = 0; i <= last; i++) {
//            float t = (last > 0) ? (float) i / (float) last : 0f;
//            float shiftedT = (float) ((t - UIUtils.getShift()) % 1d);
//            if (shiftedT < 0f) shiftedT += 1f;
//            Color col = UIUtils.blend(start, end, 1f - Math.abs(2f * shiftedT - 1f));
//            String ch = s.substring(i, i + 1);
//            tr.drawWithShadow(ms, ch, cx, y, col.getRGB());
//            cx += tr.getWidth(ch);
//        }

        //version 1.20+
        DrawContext ctx = (DrawContext) renderer;
        for (int i = 0; i <= last; i++) {
            float t = (last > 0) ? (float) i / (float) last : 0f;
            float shiftedT = (float) ((t - UIUtils.getShift()) % 1d);
            if (shiftedT < 0f) shiftedT += 1f;
            Color col = UIUtils.blend(start, end, 1f - Math.abs(2f * shiftedT - 1f));
            String ch = s.substring(i, i + 1);
            ctx.drawTextWithShadow(tr, ch, cx, y, col.getRGB());
            cx += tr.getWidth(ch);
        }
    }
}