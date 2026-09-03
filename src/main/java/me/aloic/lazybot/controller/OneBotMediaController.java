package me.aloic.lazybot.controller;

import me.aloic.lazybot.shiro.media.OneBotImagePublisher;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/lazybot/media")
public class OneBotMediaController
{
    private static final Pattern TOKEN = Pattern.compile("^[a-f0-9]{32}$");

    private final OneBotImagePublisher publisher;

    public OneBotMediaController(OneBotImagePublisher publisher)
    {
        this.publisher = publisher;
    }

    @GetMapping("/{token}")
    public ResponseEntity<byte[]> get(@PathVariable String token)
    {
        if (token == null || !TOKEN.matcher(token).matches())
            return ResponseEntity.notFound().build();

        OneBotImagePublisher.StoredImage image = publisher.find(token);
        if (image == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentType(image.contentType())
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(2)).cachePrivate())
                .body(image.bytes());
    }
}
