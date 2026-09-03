package me.aloic.lazybot.shiro.media;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Weigher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;

/**
 * Publishes rendered images for OneBot as a short-lived HTTP URL.
 *
 * <p>The OneBot client (NapCat / Lagrange / etc.) downloads the URL itself and
 * uploads it to QQ. The URL only needs to be reachable from that client, not
 * from phones. {@code file://} local paths are not used.</p>
 */
@Component
public class OneBotImagePublisher
{
    private static final Logger log = LoggerFactory.getLogger(OneBotImagePublisher.class);
    private static final long MAX_STORE_BYTES = 64L * 1024 * 1024;

    private static volatile OneBotImagePublisher instance;

    private final boolean httpMode;
    private final String publicBaseUrl;
    private final Cache<String, StoredImage> store;

    public OneBotImagePublisher(@Value("${lazybot.onebot.image-mode:http}") String imageMode,
                                @Value("${lazybot.onebot.public-base-url:http://127.0.0.1:${server.port:9001}}") String publicBaseUrl,
                                @Value("${lazybot.onebot.image-ttl-seconds:120}") long ttlSeconds)
    {
        this.httpMode = "http".equals(imageMode) && !publicBaseUrl.isBlank();
        this.publicBaseUrl = publicBaseUrl;
        this.store = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(Math.max(15, ttlSeconds)))
                .maximumWeight(MAX_STORE_BYTES)
                .weigher((Weigher<String, StoredImage>) (key, value) -> Math.max(1, value.bytes().length))
                .build();
        instance = this;
        if (this.httpMode) {
            log.info("OneBot images will be served at {}/lazybot/media/<token>", publicBaseUrl);
        }
        else {
            log.info("OneBot images will be sent as base64 on the WebSocket");
        }
    }

    public static String imageReferenceLink(byte[] image)
    {
        OneBotImagePublisher publisher = instance;
        if (publisher == null)
            return transformToBase64(image);
        return publisher.chooseWhichMode(image);
    }

    public StoredImage find(String token)
    {
        if (token == null || token.isBlank())
            return null;
        return store.getIfPresent(token.toLowerCase(Locale.ROOT));
    }

    private String chooseWhichMode(byte[] image)
    {
        if (image == null || image.length == 0)
            throw new IllegalArgumentException("image is empty");
        if (!httpMode)
            return transformToBase64(image);

        String token = UUID.randomUUID().toString().replace("-", "");
        store.put(token, new StoredImage(image, getMediaExtension(getMediaExtension(image))));
        return publicBaseUrl + "/lazybot/media/" + token;
    }

    private static String transformToBase64(byte[] image)
    {
        return "base64://" + Base64.getEncoder().encodeToString(image);
    }

    //read the goddamn metadata
    private static String getMediaExtension(byte[] image)
    {
        if (image.length >= 4
                && image[0] == (byte) 0x89
                && image[1] == 0x50
                && image[2] == 0x4E
                && image[3] == 0x47)
            return "png";
        return "jpg";
    }

    private static MediaType getMediaExtension(String extension)
    {
        return "png".equals(extension) ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;
    }


    public record StoredImage(byte[] bytes, MediaType contentType) { }
}
