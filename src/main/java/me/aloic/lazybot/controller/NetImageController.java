package me.aloic.lazybot.controller;

import jakarta.annotation.Resource;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.CardMoelleuxParameter;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


@CrossOrigin
@RestController
@RequestMapping("/lazybot")
public class NetImageController
{
    @Resource
    private PlayerService playerService;
    private static final List<Integer> BETA_USER = Arrays.asList(11232623,9037287);


    @GetMapping(value = "/card", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> renderMoelleuxCard(
            @RequestParam(value = "id", required = true) Integer playerId,
            @RequestParam(value = "hue", required = false) Integer hue,
            @RequestParam(value = "version", required = false) Integer version) throws Exception
    {

        if (!BETA_USER.contains(playerId))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        if (version==null)
            version = 0;
        byte[] image = playerService.cardMoelleux(new CardMoelleuxParameter(playerId,hue,version));
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.maxAge(8, TimeUnit.HOURS).cachePublic());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }
}
