package you.jass.flowtagger.mixin;

import net.minecraft.client.render.entity.DisplayEntityRenderer;
import net.minecraft.client.render.entity.state.TextDisplayEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import you.jass.flowtagger.FlowTagger;
import you.jass.flowtagger.settings.Settings;
import you.jass.flowtagger.settings.Toggle;
import you.jass.flowtagger.utility.Format;
import you.jass.flowtagger.utility.MultiVersion;

import java.util.ArrayList;
import java.util.List;

@Mixin(DisplayEntityRenderer.TextDisplayEntityRenderer.class)
public abstract class TextMixin {
    @Inject(method="updateRenderState(Lnet/minecraft/entity/decoration/DisplayEntity$TextDisplayEntity;Lnet/minecraft/client/render/entity/state/TextDisplayEntityRenderState;F)V", at=@At("TAIL"))
    private void cache(DisplayEntity.TextDisplayEntity entity, TextDisplayEntityRenderState state, float f, CallbackInfo ci) {
        if (!Toggle.TAG.toggled()) return;
        if (state.textLines == null) return;

        PlayerEntity player = findRiddenPlayer(entity);
        if (player == null) return;

        boolean prefix = Settings.get("tag_position").equals("Prefix");
        boolean suffix = !prefix && Settings.get("tag_position").equals("Suffix");
        boolean above = !prefix && !suffix && Settings.get("tag_position").equals("Above");
        boolean above2 = !prefix && !suffix && !above && Settings.get("tag_position").equals("Above+");

        Text tag = Format.getTag(player.getGameProfile());
        if (tag == null || tag.equals(Text.empty())) return;

        boolean edit = false;
        int width = 0;

        final List<DisplayEntity.TextDisplayEntity.TextLine> lines = new ArrayList<>();
        for (DisplayEntity.TextDisplayEntity.TextLine line : state.textLines.lines()) {
            if (!toText(line.contents()).getString().contains(MultiVersion.getName(player.getGameProfile()))) {
                lines.add(line);
                width += line.width();
                continue;
            }

            if (above || above2) {
                DisplayEntity.TextDisplayEntity.TextLine extra = new DisplayEntity.TextDisplayEntity.TextLine(tag.asOrderedText(), FlowTagger.client.textRenderer.getWidth(tag));
                lines.add(extra);
                if (width < extra.width()) width = extra.width();
                if (above2) lines.add(new DisplayEntity.TextDisplayEntity.TextLine(OrderedText.EMPTY, 0));
                edit = true;
            }

            if (prefix || suffix) {
                Text updated = prefix ? tag.copy().append(" ").append(toText(line.contents())) : toText(line.contents()).copy().append(" ").append(tag);
                DisplayEntity.TextDisplayEntity.TextLine edited = new DisplayEntity.TextDisplayEntity.TextLine(updated.asOrderedText(), FlowTagger.client.textRenderer.getWidth(updated));
                lines.add(edited);
                if (width < edited.width()) width = edited.width();
                edit = true;
            } else {
                lines.add(line);
                if (width < line.width()) width = line.width();
            }
        }

        if (edit) state.textLines = new DisplayEntity.TextDisplayEntity.TextLines(lines, width);
    }

    @Unique
    private static PlayerEntity findRiddenPlayer(Entity entity) {
        Entity current = entity.getVehicle();

        while (current != null) {
            if (current instanceof PlayerEntity player) return player;
            current = current.getVehicle();
        }

        return null;
    }

    @Unique
    private static MutableText toText(OrderedText orderedText) {
        MutableText result = Text.empty();

        orderedText.accept((index, style, codePoint) -> {
            result.append(Text.literal(Character.toString(codePoint)).setStyle(style));
            return true;
        });

        return result;
    }
}