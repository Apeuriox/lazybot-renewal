package me.aloic.lazybot.controller;

import jakarta.annotation.Resource;
import me.aloic.lazybot.graphics.render.RendererDistributor;
import me.aloic.lazybot.osu.service.PlayerService;
import me.aloic.lazybot.parameter.CardMoelleuxParameter;
import me.aloic.lazybot.service.CardService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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
        byte[] image = RendererDistributor.renderMMoelleuxCardTrimmed(playerService.cardMoelleux(new CardMoelleuxParameter(playerId,hue,version)),1);
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
            @RequestParam(value = "hue", required = false) Integer hue,
            @RequestParam(value = "scale", required = false, defaultValue = "1")Integer scale) throws Exception
    {
//        if (!BETA_USER.contains(playerId))
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        if (scale>3) scale=3;
        if (scale<1) scale=1;
        byte[] image = RendererDistributor.renderMMoelleuxCardTrimmed(playerService.cardMoelleuxTrimmed(new CardMoelleuxParameter(playerId,hue,0)), scale);
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.maxAge(24, TimeUnit.HOURS).cachePublic());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }
}
