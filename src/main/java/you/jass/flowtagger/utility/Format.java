package you.jass.flowtagger.utility;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import you.jass.flowtagger.FlowTagger;
import you.jass.flowtagger.network.API;
import you.jass.flowtagger.settings.Settings;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Format {
    public static final Map<GameProfile, Text> tagCache = new ConcurrentHashMap<>();
    public static final Map<GameProfile, Text> chatCache = new ConcurrentHashMap<>();
    public static final Map<GameProfile, Text> tabCache  = new ConcurrentHashMap<>();
    private static final String[] LADDERS = {"SWORD", "AXE", "UHC", "VANILLA", "MACE", "DIAMOND_POT", "NETHERITE_OP", "SMP", "DIAMOND_SMP"};
    private static String automatic = "GLOBAL";

    public static Text getTag(GameProfile profile) {
        Text cached = tagCache.get(profile);
        if (cached != null) return cached;
        if (API.isMissing(profile)) return null;
        Text formatted = format(profile, Settings.get("tag_format"), Settings.get("tag_gamemode"));
        if (formatted != null) tagCache.put(profile, formatted);
        return formatted;
    }

    public static Text getTab(GameProfile profile) {
        Text cached = tabCache.get(profile);
        if (cached != null) return cached;
        if (API.isMissing(profile)) return null;
        Text formatted = format(profile, Settings.get("tab_format"), Settings.get("tab_gamemode"));
        if (formatted != null) tabCache.put(profile, formatted);
        return formatted;
    }

    public static Text getChat(GameProfile profile) {
        Text cached = chatCache.get(profile);
        if (cached != null) return cached;
        if (API.isMissing(profile)) return null;
        Text formatted = format(profile, Settings.get("chat_format"), Settings.get("chat_gamemode"));
        if (formatted != null) chatCache.put(profile, formatted);
        return formatted;
    }

    public static void reset() {
        tagCache.clear();
        chatCache.clear();
        tabCache.clear();
    }

    public static Text format(GameProfile profile, String format, String gamemode) {
        if (format == null) return Text.empty();
        if (API.get(profile, "lastKnownName") == null) return null;

        String mode = convertGamemode(profile, gamemode);
        String result = format;

        if (mode.equals("global")) {
            result = replace(result, "tier", getTier(profile, mode));
            result = replace(result, "elo", getString(profile, "globalElo"));
            result = replace(result, "rank", getString(profile, "globalPosition"));
            result = replace(result, "streak", String.valueOf(getHighestStreak(profile)));
            result = replace(result, "matches", String.valueOf(getGlobalWins(profile) + getGlobalLosses(profile)));
            result = replace(result, "wins", String.valueOf(getGlobalWins(profile)));
            result = replace(result, "losses", String.valueOf(getGlobalLosses(profile)));
        } else {
            String base = "perLadder." + mode + ".";
            result = replace(result, "tier", getTier(profile, mode));
            result = replace(result, "elo", getString(profile, base + "totalRating"));
            result = replace(result, "rank", getString(profile, base + "position"));
            result = replace(result, "streak", getString(profile, base + "currentStreak"));
            result = replace(result, "matches", String.valueOf(getInt(profile, base + "wins") + getInt(profile, base + "losses")));
            result = replace(result, "wins", getString(profile, base + "wins"));
            result = replace(result, "losses", getString(profile, base + "losses"));
        }

        result = result.replace("icon", getIcon(mode));
        result = result.replace("mode", getModeName(mode));
        result = result.replace("?", getModeColor(mode));
        return formatTierColor(result.replace('&', '§'), profile, mode);
    }

    private static String replace(String input, String placeholder, String value) {
        if (input == null) return "";
        return input.replace(placeholder, value == null ? "" : value);
    }

    private static String getString(GameProfile profile, String key) {
        String value = API.get(profile, key);
        return value == null ? "" : value;
    }

    private static int getInt(GameProfile profile, String key) {
        String value = API.get(profile, key);
        if (value == null || value.isEmpty()) return 0;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private static int getGlobalWins(GameProfile profile) {
        int total = 0;
        for (String ladder : LADDERS) total += getInt(profile, "perLadder." + ladder + ".wins");
        return total;
    }

    private static int getGlobalLosses(GameProfile profile) {
        int total = 0;
        for (String ladder : LADDERS) total += getInt(profile, "perLadder." + ladder + ".losses");
        return total;
    }

    private static String getHighestEloTier(GameProfile profile) {
        int highest = 0;
        String tier = "GLOBAL";

        for (String ladder : LADDERS) {
            int rating = getInt(profile, "perLadder." + ladder + ".totalRating");
            if (rating > highest) {
                highest = rating;
                tier = ladder;
            }
        }

        return tier;
    }

    private static String getHighestRankTier(GameProfile profile) {
        int highest = 0;
        String tier = "GLOBAL";

        for (String ladder : LADDERS) {
            int rank = getInt(profile, "perLadder." + ladder + ".position");
            if (rank > highest) {
                highest = rank;
                tier = ladder;
            }
        }

        return tier;
    }

    private static int getHighestStreak(GameProfile profile) {
        int highest = 0;
        for (String ladder : LADDERS) {
            int streak = getInt(profile, "perLadder." + ladder + ".currentStreak");
            if (streak > highest) highest = streak;
        }
        return highest;
    }

    public static String convertGamemode(GameProfile profile, String gamemode) {
        if (gamemode == null) return null;
        return switch (gamemode) {
            case "Automatic" -> !automatic.equals("BEST") ? automatic : getHighestEloTier(profile);
            case "Best Elo" -> getHighestEloTier(profile);
            case "Best Rank" -> getHighestRankTier(profile);
            case "Global" -> "GLOBAL";
            case "Sword" -> "SWORD";
            case "Axe" -> "AXE";
            case "UHC" -> "UHC";
            case "Vanilla" -> "VANILLA";
            case "Mace" -> "MACE";
            case "DPot" -> "DIAMOND_POT";
            case "DSMP" -> "DIAMOND_SMP";
            case "NOP" -> "NETHERITE_OP";
            case "NSMP" -> "SMP";
            default -> null;
        };
    }

    private static String getTier(GameProfile profile, String gamemode) {
        int elo = gamemode.equals("GLOBAL") ? getInt(profile, "globalElo") : getInt(profile, "perLadder." + gamemode + ".totalRating");
        boolean first = gamemode.equals("GLOBAL") ? getInt(profile, "globalPosition") == 1 : getInt(profile, "perLadder." + gamemode + ".position") == 1;
        if (first) return "Grandmaster";
        else if (elo >= 2100) return "Netherite";
        else if (elo >= 1900) return "Diamond III";
        else if (elo >= 1900 - 125 * 1) return "Diamond II";
        else if (elo >= 1900 - 125 * 2) return "Diamond I";
        else if (elo >= 1900 - 125 * 3) return "Emerald III";
        else if (elo >= 1900 - 125 * 4) return "Emerald II";
        else if (elo >= 1900 - 125 * 5) return "Emerald I";
        else if (elo >= 1900 - 125 * 6) return "Gold III";
        else if (elo >= 1900 - 125 * 7) return "Gold II";
        else if (elo >= 1900 - 125 * 8) return "Gold I";
        else if (elo >= 1900 - 125 * 9) return "Iron III";
        else if (elo >= 1900 - 125 * 10) return "Iron II";
        else if (elo >= 1900 - 125 * 11) return "Iron I";
        else return "Coal";
    }

    private static TextColor getTierColor(GameProfile profile, String gamemode) {
        String tier = getTier(profile, gamemode);
        if (tier.startsWith("Grandmaster")) return TextColor.fromRgb(0xFF3F42);
        if (tier.startsWith("Netherite")) return TextColor.fromRgb(0xAF3FFF);
        if (tier.startsWith("Diamond")) return TextColor.fromRgb(0x3FE0FF);
        if (tier.startsWith("Emerald")) return TextColor.fromRgb(0x50C878);
        if (tier.startsWith("Gold")) return TextColor.fromRgb(0xFFD700);
        if (tier.startsWith("Iron")) return TextColor.fromRgb(0xFFFFFF);
        return TextColor.fromRgb(0x808080);
    }

    private static Text formatTierColor(String result, GameProfile profile, String mode) {
        MutableText text = Text.empty();
        int start = 0, pos;

        while ((pos = result.indexOf('!', start)) != -1) {
            if (pos > start) text.append(Text.literal(result.substring(start, pos)));
            start = pos + 1;
            int next = result.indexOf('!', start);
            String colored = next == -1 ? result.substring(start) : result.substring(start, next);
            if (!colored.isEmpty()) text.append(Text.literal(colored).setStyle(Style.EMPTY.withColor(getTierColor(profile, mode))));
            start = next == -1 ? result.length() : next;
        }

        if (start < result.length()) text.append(Text.literal(result.substring(start)));
        return text;
    }

    public static void updatePlayersKit() {
        if (!Settings.get("tag_gamemode").equals("Automatic") && !Settings.get("tab_gamemode").equals("Automatic") && !Settings.get("chat_gamemode").equals("Automatic")) return;
        ClientPlayerEntity player = FlowTagger.client.player;
        if (player == null) return;
        PlayerInventory inv = player.getInventory();
        boolean hasMace = false, hasCrystal = false, hasPlanks = false, hasNetheriteArmor = false;
        boolean hasGoldenApples = false, hasShield = false, hasDiamondAxe = false;
        boolean hasEnchantedDiamondSword = false, hasNonEnchantedDiamondSword = false;
        for (ItemStack stack : inv) {
            if (stack.isEmpty()) continue;
            if (!hasMace) hasMace = stack.isOf(Items.MACE);
            if (!hasCrystal) hasCrystal = stack.isOf(Items.END_CRYSTAL);
            if (!hasPlanks) hasPlanks = stack.isOf(Items.OAK_PLANKS);
            if (!hasGoldenApples) hasGoldenApples = stack.isOf(Items.GOLDEN_APPLE);
            if (!hasShield) hasShield = stack.isOf(Items.SHIELD);
            if (!hasDiamondAxe) hasDiamondAxe = stack.isOf(Items.DIAMOND_AXE);
            if (!hasNetheriteArmor) hasNetheriteArmor = stack.isOf(Items.NETHERITE_HELMET) || stack.isOf(Items.NETHERITE_CHESTPLATE) || stack.isOf(Items.NETHERITE_LEGGINGS) || stack.isOf(Items.NETHERITE_BOOTS);
            if (!hasEnchantedDiamondSword && !hasNonEnchantedDiamondSword) {
                if (stack.isOf(Items.DIAMOND_SWORD)) {
                    boolean hasSharpness = false;
                    for (RegistryEntry<Enchantment> enchantment : stack.getEnchantments().getEnchantments()) {
                        if (enchantment.getIdAsString().equalsIgnoreCase("minecraft:sharpness")) {
                            hasSharpness = true;
                            break;
                        }
                    }

                    if (hasSharpness) hasEnchantedDiamondSword = true;
                    else hasNonEnchantedDiamondSword = true;
                }
            }
        }

        String gamemode;
        if (hasMace) gamemode = "MACE";
        else if (hasCrystal) gamemode = "VANILLA";
        else if (hasPlanks) gamemode = "UHC";
        else if (hasNetheriteArmor && hasGoldenApples && hasShield) gamemode = "SMP";
        else if (hasNetheriteArmor && hasGoldenApples) gamemode = "NETHERITE_OP";
        else if (hasGoldenApples && hasShield) gamemode = "DIAMOND_SMP";
        else if (hasDiamondAxe) gamemode = "AXE";
        else if (hasEnchantedDiamondSword) gamemode = "DIAMOND_POT";
        else if (hasNonEnchantedDiamondSword) gamemode = "SWORD";
        else gamemode = "BEST";
        automatic = gamemode;
    }

    public static String getIcon(String ladder) {
        if (ladder == null) return null;
        return switch (ladder.toUpperCase()) {
            case "SWORD" -> "🗡";
            case "AXE" -> "🪓";
            case "UHC" -> "🪣";
            case "VANILLA" -> "☀";
            case "MACE" -> "☄";
            case "DIAMOND_POT" -> "⚗";
            case "NETHERITE_OP" -> "☠";
            case "SMP" -> "☁";
            case "DIAMOND_SMP" -> "🌧";
            case "GLOBAL" -> "⭐";
            default -> "⚔";
        };
    }

    public static String getModeColor(String ladder) {
        if (ladder == null) return null;
        return switch (ladder.toUpperCase()) {
            case "SWORD" -> "&b";
            case "AXE" -> "&9";
            case "UHC" -> "&6";
            case "VANILLA" -> "&d";
            case "MACE" -> "&5";
            case "DIAMOND_POT" -> "&c";
            case "NETHERITE_OP" -> "&8";
            case "SMP" -> "&2";
            case "DIAMOND_SMP" -> "&a";
            case "GLOBAL" -> "&e";
            default -> "&r";
        };
    }

    public static String getModeName(String ladder) {
        if (ladder == null) return null;
        return switch (ladder.toUpperCase()) {
            case "SWORD" -> "Sword";
            case "AXE" -> "Axe";
            case "UHC" -> "UHC";
            case "VANILLA" -> "Vanilla";
            case "MACE" -> "Mace";
            case "DIAMOND_POT" -> "Dia Pot";
            case "NETHERITE_OP" -> "Neth OP";
            case "SMP" -> "Neth SMP";
            case "DIAMOND_SMP" -> "Dia SMP";
            case "GLOBAL" -> "Global";
            default -> "";
        };
    }
}