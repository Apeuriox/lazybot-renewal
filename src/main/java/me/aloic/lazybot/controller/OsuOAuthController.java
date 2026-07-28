package me.aloic.lazybot.controller;

import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.service.OsuOAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth/osu")
public class OsuOAuthController
{
    private final OsuOAuthService oauthService;

    public OsuOAuthController(OsuOAuthService oauthService)
    {
        this.oauthService = oauthService;
    }

    @GetMapping(value = "/callback", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> callback(
            @RequestParam String code,
            @RequestParam String state)
    {
        try
        {
            PlayerInfoDTO player = oauthService.completeAuthorization(code, state);
            return ResponseEntity.ok("""
                    <!doctype html>
                    <html lang="zh-CN"><meta charset="utf-8">
                    <title>Lazybot 绑定成功</title>
                    <body><h1>绑定成功</h1><p>已验证并绑定 osu! 用户：%s</p>
                    <p>现在可以关闭此页面。</p></body></html>
                    """.formatted(escapeHtml(player.getUsername())));
        }
        catch (LazybotRuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("""
                    <!doctype html>
                    <html lang="zh-CN"><meta charset="utf-8">
                    <title>Lazybot 绑定失败</title>
                    <body><h1>绑定失败</h1><p>%s</p></body></html>
                    """.formatted(escapeHtml(e.getMessage())));
        }
    }

    private static String escapeHtml(String value)
    {
        if (value == null)
            return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
