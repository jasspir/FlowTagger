package you.jass.flowtagger.mixin;


import com.mojang.authlib.GameProfile;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;

import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import you.jass.flowtagger.FlowTagger;
import you.jass.flowtagger.settings.Settings;
import you.jass.flowtagger.settings.Toggle;
import you.jass.flowtagger.utility.Format;

@Mixin(PlayerEntityRenderer.class)
public abstract class TagMixin {
    @Unique
    GameProfile lastPlayer;

    @Inject(method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitLabel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Vec3d;ILnet/minecraft/text/Text;ZIDLnet/minecraft/client/render/state/CameraRenderState;)V",
            ordinal = 1, shift = At.Shift.BEFORE))
    protected void renderNametag(PlayerEntityRenderState playerEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (!Toggle.TAG.toggled()) return;
        if (FlowTagger.client.world == null) return;
        Entity entity = FlowTagger.client.world.getEntityById(playerEntityRenderState.id);
        if (entity == null) return;
        if (!(entity instanceof PlayerEntity player)) return;
        lastPlayer = player.getGameProfile();
        boolean above = Settings.get("tag_position").equals("Above");
        boolean above2 = Settings.get("tag_position").equals("Above+");
        if (!above && !above2) return;
        Text tag = Format.getTag(lastPlayer);
        if (tag == null || tag.equals(Text.empty())) return;
        double height = 0;
        if (above) height = 0.275;
        else height = 0.50;
        orderedRenderCommandQueue.submitLabel(matrixStack, playerEntityRenderState.nameLabelPos.add(0, height, 0), playerEntityRenderState.extraEars ? -10 : 0, tag, !playerEntityRenderState.sneaking, playerEntityRenderState.light, playerEntityRenderState.squaredDistanceToCamera, cameraRenderState);
    }

    @ModifyArg(method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitLabel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Vec3d;ILnet/minecraft/text/Text;ZIDLnet/minecraft/client/render/state/CameraRenderState;)V",
            ordinal = 1), index = 3)
    private Text modifyNametag(Text displayName) {
        if (!Toggle.TAG.toggled()) return displayName;
        boolean prefix = Settings.get("tag_position").equals("Prefix");
        boolean suffix = Settings.get("tag_position").equals("Suffix");
        if (!prefix && !suffix) return displayName;
        Text tag = Format.getTag(lastPlayer);
        if (tag == null) return displayName;
        if (prefix) return tag.copy().append(" ").append(displayName);
        return displayName.copy().append(" ").append(tag);
    }
}