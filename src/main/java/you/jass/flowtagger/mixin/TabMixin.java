package you.jass.flowtagger.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import you.jass.flowtagger.settings.Settings;
import you.jass.flowtagger.settings.Toggle;
import you.jass.flowtagger.utility.Format;

@Mixin(PlayerListEntry.class)
public abstract class TabMixin {
    @Shadow public abstract GameProfile getProfile();

    @ModifyReturnValue(method = "getDisplayName", at = @At("RETURN"))
    public Text format(Text text) {
        if (text == null) return null;
        if (!Toggle.TAB.toggled()) return text;
        Text formatted = Format.getTab(getProfile());
        if (formatted == null || formatted.equals(Text.empty())) return text;
        return Settings.get("tab_position").equals("Prefix") ? formatted.copy().append(" ").append(text) : text.copy().append(" ").append(formatted);
    }
}