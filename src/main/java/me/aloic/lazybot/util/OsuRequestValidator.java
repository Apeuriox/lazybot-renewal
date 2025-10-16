package me.aloic.lazybot.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

//ai code
public class OsuRequestValidator {

    private static final List<String> OSU_USER_AGENTS = List.of(
            "osu!", "ppy.sh"
    );

    public static boolean isFromOsuServer(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String referer   = request.getHeader("Referer");
        String ip        = request.getRemoteAddr();

        // 条件 1: UA 包含 osu! 的特征
        boolean uaMatch = userAgent != null && 
                          OSU_USER_AGENTS.stream().anyMatch(userAgent::contains);

        // 条件 2: Referer 来自 osu!
        boolean refererMatch = referer != null && referer.contains("osu.ppy.sh");

        // 条件 3: IP 在你记录的 osu!/CDN 范围内（需要自己观察 Nginx/日志）
        boolean ipMatch = ip != null && (ip.startsWith("104.") || ip.startsWith("172."));
        // Cloudflare 常见段，举例而已

        return uaMatch || refererMatch || ipMatch;
    }
}
