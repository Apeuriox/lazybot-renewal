package me.aloic.lazybot.osu.service;

import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.dao.entity.po.OAuthLinkSessionPO;
import me.aloic.lazybot.osu.dao.mapper.OAuthLinkSessionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Service
public class OAuthLinkSessionService
{
    private final OAuthLinkSessionMapper sessionMapper;

    public OAuthLinkSessionService(OAuthLinkSessionMapper sessionMapper)
    {
        this.sessionMapper = sessionMapper;
    }

    @Transactional
    public OAuthLinkSessionPO consume(String state)
    {
        OAuthLinkSessionPO session = sessionMapper.selectByStateHashForUpdate(hash(state));
        LocalDateTime now = LocalDateTime.now();
        if (session == null || session.getConsumed_at() != null) {
            throw new LazybotRuntimeException("OAuth 链接无效或已经使用");
        }
        if (session.getExpires_at() == null || !session.getExpires_at().isAfter(now)) {
            throw new LazybotRuntimeException("OAuth 链接已过期，请重新生成");
        }
        if (sessionMapper.markConsumed(session.getId(), now) != 1) {
            throw new LazybotRuntimeException("OAuth 链接已经使用");
        }
        return session;
    }

    public static byte[] hash(String state)
    {
        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest(state.getBytes(StandardCharsets.UTF_8));
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("JVM 不支持 SHA-256", e);
        }
    }
}
