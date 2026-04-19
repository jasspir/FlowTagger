package you.jass.flowtagger.settings;

import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Settings {
    private static final Path settings = FabricLoader.getInstance().getConfigDir().resolve("flowtagger.properties");
    private static final Properties properties = new Properties();
    private static final Properties defaults = new Properties();
    private static final Map<String, String> categories = new HashMap<>();

    static {
        for (Toggle toggle : Toggle.values()) {
            categories.put(toggle.key(), "toggle");
            defaults.setProperty(toggle.key(), String.valueOf(toggle.defaultValue()));
        }

        categories.put("tag_format", "configure");
        categories.put("chat_format", "configure");
        categories.put("tab_format", "configure");
        categories.put("tag_gamemode", "configure");
        categories.put("chat_gamemode", "configure");
        categories.put("tab_gamemode", "configure");
        categories.put("tag_position", "configure");
        categories.put("chat_position", "configure");
        categories.put("tab_position", "configure");

        defaults.setProperty("tag_format", "?icon !tier");
        defaults.setProperty("tab_format", "?icon !tier");
        defaults.setProperty("chat_format", "?icon !tier");
        defaults.setProperty("tag_gamemode", "Automatic");
        defaults.setProperty("chat_gamemode", "Automatic");
        defaults.setProperty("tab_gamemode", "Automatic");
        defaults.setProperty("tag_position", "Above");
        defaults.setProperty("chat_position", "Prefix");
        defaults.setProperty("tab_position", "Prefix");

        categories.put("tutorial", "tutorial");
        defaults.setProperty("tutorial", "true");

        properties.putAll(defaults);
        load();
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static void set(String key, String value) {
        properties.setProperty(key, value);
        save();
    }

    public static boolean toggle(String key) {
        boolean toggled = !getBoolean(key);
        setBoolean(key, toggled);
        return toggled;
    }

    public static void load() {
        if (!Files.exists(settings)) {
            save();
            return;
        }

        try (InputStream input = Files.newInputStream(settings)) {
            properties.load(input);
            for (String key : defaults.stringPropertyNames()) {
                if (!properties.containsKey(key)) properties.setProperty(key, defaults.getProperty(key));
            }
        } catch (IOException e) {
            System.err.println("Couldn't load file: " + e.getMessage());
            properties.putAll(defaults);
        }
    }

    public static void save() {
        try {
            Files.createDirectories(settings.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(settings)) {
                writer.write("#Flow Tagger Settings");
                writer.newLine();
                writer.newLine();

                Map<String, List<String>> grouped = new HashMap<>();
                for (String key : properties.stringPropertyNames()) {
                    String cat = categories.get(key);
                    if (cat == null) cat = "Other";
                    grouped.computeIfAbsent(cat, k -> new ArrayList<>()).add(key);
                }

                for (List<String> list : grouped.values()) {
                    list.sort(String.CASE_INSENSITIVE_ORDER);
                }

                writeCategory(writer, "Configure", grouped.get("configure"));
                writeCategory(writer, "Render", grouped.get("render"));
                writeCategory(writer, "UI", grouped.get("ui"));
                writeCategory(writer, "Tracked", grouped.get("tracked"));
                writeCategory(writer, "Toggle", grouped.get("toggle"));
                writeCategory(writer, "Tutorial", grouped.get("tutorial"));
            }
        } catch (IOException e) {
            System.err.println("Couldn't save file: " + e.getMessage());
        }
    }

    public static void writeCategory(BufferedWriter writer, String name, List<String> keys) throws IOException {
        if (keys == null || keys.isEmpty()) return;
        writer.write("#" + name);
        writer.newLine();
        for (String key : keys) {
            writer.write(key + "=" + properties.getProperty(key));
            writer.newLine();
        }
        writer.newLine();
    }

    public static String getString(String key) {
        String v = get(key);
        if (v == null || v.trim().isEmpty()) {
            String d = defaults.getProperty(key);
            if (d != null && !d.trim().isEmpty()) v = d;
            else v = "0";
            set(key, d);
        } return v;
    }

    public static boolean getBoolean(String key) {
        String v = get(key);
        if (v == null || v.trim().isEmpty()) { String d = defaults.getProperty(key);
            if (d != null && !d.trim().isEmpty()) {
                set(key, d);
                return Boolean.parseBoolean(d.trim());
            }

            set(key, "false");
            return false;
        }
        return Boolean.parseBoolean(v.trim());
    }

    public static int getInt(String key) {
        String v = get(key);
        if (v != null) try { return Integer.parseInt(v.trim()); } catch (NumberFormatException ignored) {}
        String d = defaults.getProperty(key);
        if (d != null) try {
            set(key, d);
            return Integer.parseInt(d.trim());
        } catch (NumberFormatException ignored) {}

        set(key, "0");
        return 0;
    }

    public static long getLong(String key) {
        String v = get(key);
        if (v != null) try { return Long.parseLong(v.trim()); } catch (NumberFormatException ignored) {}
        String d = defaults.getProperty(key);
        if (d != null) try {
            set(key, d);
            return Long.parseLong(d.trim());
        } catch (NumberFormatException ignored) {}

        set(key, "0");
        return 0L;
    }

    public static double getDouble(String key) {
        String v = get(key);
        if (v != null) try { return Double.parseDouble(v.trim()); } catch (NumberFormatException ignored) {}
        String d = defaults.getProperty(key);
        if (d != null) try {
            set(key, d);
            return Double.parseDouble(d.trim());
        } catch (NumberFormatException ignored) {}

        set(key, "0");
        return 0d;
    }

    public static float getFloat(String key) {
        String v = get(key);
        if (v != null) try { return Float.parseFloat(v.trim()); } catch (NumberFormatException ignored) {}
        String d = defaults.getProperty(key);
        if (d != null) try {
            set(key, d);
            return Float.parseFloat(d.trim());
        } catch (NumberFormatException ignored) {}

        set(key, "0");
        return 0f;
    }

    public static short getShort(String key) {
        String v = get(key);
        if (v != null) try { return Short.parseShort(v.trim()); } catch (NumberFormatException ignored) {}
        String d = defaults.getProperty(key);
        if (d != null) try {
            set(key, d);
            return Short.parseShort(d.trim());
        } catch (NumberFormatException ignored) {}

        set(key, "0");
        return (short) 0;
    }

    public static byte getByte(String key) {
        String v = get(key);
        if (v != null) try { return Byte.parseByte(v.trim()); } catch (NumberFormatException ignored) {}
        String d = defaults.getProperty(key);
        if (d != null) try {
            set(key, d);
            return Byte.parseByte(d.trim());
        } catch (NumberFormatException ignored) {}

        set(key, "0");
        return (byte) 0;
    }

    public static void setString(String key, String value) {
        set(key, String.valueOf(value));
    }

    public static void setBoolean(String key, boolean value) {
        set(key, String.valueOf(value));
    }

    public static void setInt(String key, int value) {
        set(key, String.valueOf(value));
    }

    public static void setLong(String key, long value) {
        set(key, String.valueOf(value));
    }

    public static void setDouble(String key, double value) {
        set(key, String.valueOf(value));
    }

    public static void setFloat(String key, float value) {
        set(key, String.valueOf(value));
    }

    public static void setShort(String key, short value) {
        set(key, String.valueOf(value));
    }

    public static void setByte(String key, byte value) {
        set(key, String.valueOf(value));
    }
}
