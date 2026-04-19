package you.jass.flowtagger.settings;

public enum Toggle {
    TAG("tag", true),
    TAB("tab", true),
    CHAT("chat", false);

    private final String key;
    private final boolean defaultValue;

    Toggle(String key, boolean defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String key() {
        return key;
    }

    public boolean defaultValue() {
        return defaultValue;
    }

    public boolean toggled() {
        return Boolean.parseBoolean(Settings.get(key));
    }

    public boolean toggle() {
        return Settings.toggle(key);
    }
}