package you.jass.flowtagger.network;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import you.jass.flowtagger.FlowTagger;
import you.jass.flowtagger.utility.MultiVersion;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class API {
    private static final URI BASE = URI.create("https://flowpvp.gg/api/ranked/");
    private static final Duration TIMEOUT = Duration.ofSeconds(15);
    private static final long REQUEST_DELAY = 100;
    private static final long RETRY_DELAY = 1000;

    public static final Map<String, GameProfile> PROFILE_CACHE = new ConcurrentHashMap<>();

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NEVER)
            .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_NONE))
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(Thread.ofVirtual().name("flowtagger", 0).factory());

    private static final ConcurrentHashMap<GameProfile, Map<String, String>> cache = new ConcurrentHashMap<>();
    private static final Set<GameProfile> missing = ConcurrentHashMap.newKeySet();
    private static final Set<GameProfile> queuedOrProcessing = ConcurrentHashMap.newKeySet();
    private static final Deque<GameProfile> requestQueue = new ConcurrentLinkedDeque<>();
    private static final AtomicBoolean processing = new AtomicBoolean(false);

    public static String get(GameProfile profile, String key) {
        if (profile == null) return null;

        Map<String, String> values = cache.get(profile);
        if (values != null) return values.get(key);

        if (!missing.contains(profile)) enqueue(profile);
        return null;
    }

    public static boolean isMissing(GameProfile profile) {
        return profile != null && missing.contains(profile);
    }

    public static void cacheProfile(GameProfile profile) {
        if (profile == null || profile.name() == null || profile.name().isBlank()) return;
        PROFILE_CACHE.put(profile.name(), profile);
    }

    private static void enqueue(GameProfile profile) {
        if (profile == null || profile.id() == null) return;

        if (queuedOrProcessing.add(profile)) {
            requestQueue.addFirst(profile);
            startProcessing();
        } else if (requestQueue.remove(profile)) {
            requestQueue.addFirst(profile);
        }
    }

    private static void startProcessing() {
        if (processing.compareAndSet(false, true)) processNext();
    }

    private static void processNext() {
        GameProfile profile = requestQueue.pollFirst();
        if (profile == null) {
            processing.set(false);
            return;
        }

        if (profile.id() == null) {
            queuedOrProcessing.remove(profile);
            scheduler.schedule(API::processNext, REQUEST_DELAY, TimeUnit.MILLISECONDS);
            return;
        }

        HttpRequest request = HttpRequest.newBuilder(BASE.resolve(profile.id().toString()))
                .GET()
                .timeout(TIMEOUT)
                .header("Accept", "application/json")
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).whenComplete((response, error) -> {
            if (error != null || response.statusCode() != 200) {
                scheduleRetry(profile);
            } else {
                try {
                    JsonElement rootElement = JsonParser.parseString(response.body());
                    if (rootElement == null || rootElement.isJsonNull() || !rootElement.isJsonObject()) {
                        missing.add(profile);
                        queuedOrProcessing.remove(profile);
                    } else {
                        Map<String, String> flattened = new HashMap<>(64);
                        flatten(rootElement.getAsJsonObject(), "", flattened);
                        cache.put(profile, Map.copyOf(flattened));
                        missing.remove(profile);
                        queuedOrProcessing.remove(profile);
                        cacheProfile(profile);
                    }
                } catch (JsonParseException exception) {
                    scheduleRetry(profile);
                }
            }

            scheduler.schedule(API::processNext, REQUEST_DELAY, TimeUnit.MILLISECONDS);
        });
    }

    private static void scheduleRetry(GameProfile profile) {
        if (missing.contains(profile)) {
            queuedOrProcessing.remove(profile);
            return;
        }

        scheduler.schedule(() -> {
            if (!cache.containsKey(profile) && !missing.contains(profile)) requestQueue.addFirst(profile);
            startProcessing();
        }, RETRY_DELAY, TimeUnit.MILLISECONDS);
    }

    private static void flatten(JsonObject object, String prefix, Map<String, String> output) {
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            JsonElement value = entry.getValue();
            if (value == null || value.isJsonNull()) continue;
            if (value.isJsonObject()) flatten(value.getAsJsonObject(), key, output);
            else if (value.isJsonArray()) output.put(key, value.toString());
            else output.put(key, value.getAsString());
        }
    }

    public static void shutdown() {
        scheduler.shutdownNow();
        processing.set(false);
    }
}