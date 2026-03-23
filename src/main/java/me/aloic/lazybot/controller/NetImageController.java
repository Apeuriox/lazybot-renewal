package me.aloic.lazybot.controller;

import jakarta.annotation.Resource;
import me.aloic.lazybot.graphics.render.RendererDistributor;
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

    @GetMapping(value = "/card", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> renderMoelleuxCard(
            @RequestParam(value = "id", required = true) Integer playerId,
            @RequestParam(value = "hue", required = false) Integer hue,
            @RequestParam(value = "version", required = false) Integer version) throws Exception
    {
        if (version==null)
            version = 0;
        byte[] image = RendererDistributor.renderMMoelleuxCardTrimmed(playerService.cardMoelleux(new CardMoelleuxParameter(playerId,hue,version)));
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.maxAge(24, TimeUnit.HOURS).cachePublic());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }



    @GetMapping(value = "/trim", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> renderMoelleuxCardTrimmed(
            @RequestParam(value = "id", required = true) Integer playerId,
            @RequestParam(value = "hue", required = false) Integer hue) throws Exception
    {
//        if (!BETA_USER.contains(playerId))
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        byte[] image = RendererDistributor.renderMMoelleuxCardTrimmed(playerService.cardMoelleuxTrimmed(new CardMoelleuxParameter(playerId,hue,0)));
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.maxAge(24, TimeUnit.HOURS).cachePublic());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }
}
