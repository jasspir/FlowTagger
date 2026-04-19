package you.jass.flowtagger.utility;

import com.mojang.authlib.GameProfile;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import you.jass.flowtagger.FlowTagger;
import you.jass.flowtagger.network.API;
import you.jass.flowtagger.settings.Settings;

import java.util.Comparator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ChatFormatter {
    public static Text format(Text input) {
        if (input == null || FlowTagger.client == null || API.PROFILE_CACHE.isEmpty()) return input;

        Pattern pattern = Pattern.compile(API.PROFILE_CACHE.keySet().stream().sorted(Comparator.comparingInt(String::length).reversed()).map(Pattern::quote).collect(Collectors.joining("|")));
        MutableText output = Text.empty();

        input.visit((Style style, String segment) -> {
            output.append(rewriteSegment(segment, style, pattern));
            return Optional.empty();
        }, Style.EMPTY);

        return output;
    }

    private static MutableText rewriteSegment(String segment, Style style, Pattern pattern) {
        Matcher matcher = pattern.matcher(segment);
        MutableText result = Text.empty();

        int lastIndex = 0;

        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                result.append(Text.literal(segment.substring(lastIndex, matcher.start())).fillStyle(style));
            }

            String name = matcher.group();
            GameProfile profile = API.PROFILE_CACHE.get(name);
            Text chatText = Format.getChat(profile);

            if (chatText != null) {
                if (Settings.get("chat_position").equals("Prefix")) {
                    result.append(chatText.copy());
                    result.append(" ");
                    result.append(Text.literal(name).fillStyle(style));
                } else {
                    result.append(Text.literal(name).fillStyle(style));
                    result.append(" ");
                    result.append(chatText.copy());
                }
            } else {
                result.append(Text.literal(name).fillStyle(style));
            }

            lastIndex = matcher.end();
        }

        if (lastIndex < segment.length()) result.append(Text.literal(segment.substring(lastIndex)).fillStyle(style));
        return result;
    }
}