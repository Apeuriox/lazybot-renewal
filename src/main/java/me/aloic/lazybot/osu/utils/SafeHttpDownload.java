package me.aloic.lazybot.osu.utils;

import me.aloic.lazybot.exception.LazybotRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import java.util.Set;

/**
 * Downloads for user-supplied URLs (profile BG, etc.).
 * https-only, blocks private/loopback/link-local/CGNAT after DNS,
 * limited redirects with re-validation, streamed size cap.
 */
public final class SafeHttpDownload
{
    private static final Logger logger = LoggerFactory.getLogger(SafeHttpDownload.class);

    private static final long DEFAULT_MAX_BYTES = 8L * 1024 * 1024;
    private static final int DEFAULT_MAX_REDIRECTS = 3;
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);

    private static final Set<String> BLOCKED_HOSTS = Set.of(
            "localhost",
            "metadata.google.internal"
    );

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NEVER)
            .connectTimeout(CONNECT_TIMEOUT)
            .build();

    private SafeHttpDownload() {}

    public static void downloadHttpsToFile(String targetUrl, String desiredLocalPath)
    {
        downloadHttpsToFile(targetUrl, desiredLocalPath, DEFAULT_MAX_BYTES, DEFAULT_MAX_REDIRECTS);
    }

    public static void downloadHttpsToFile(String targetUrl, String desiredLocalPath, long maxBytes, int maxRedirects)
    {
        Path out = Path.of(desiredLocalPath);
        Path tmp = out.resolveSibling(out.getFileName() + ".part");
        try
        {
            Files.createDirectories(out.getParent() == null ? Path.of(".") : out.getParent());
            String current = targetUrl;
            for (int hop = 0; hop <= maxRedirects; hop++)
            {
                URI uri = validateHttpsPublicUri(current);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .timeout(REQUEST_TIMEOUT)
                        .GET()
                        .header("User-Agent", "lazybot-safe-download/1.0")
                        .build();

                HttpResponse<InputStream> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
                int status = response.statusCode();

                if (status >= 300 && status < 400)
                {
                    String location = response.headers().firstValue("Location").orElse(null);
                    try { response.body().close(); } catch (Exception ignored) {}
                    if (location == null || location.isBlank())
                        throw new LazybotRuntimeException("重定向缺少 Location");
                    current = uri.resolve(location).toString();
                    continue;
                }

                if (status != 200)
                {
                    try { response.body().close(); } catch (Exception ignored) {}
                    throw new LazybotRuntimeException("HTTP 状态码：" + status);
                }

                long contentLength = response.headers().firstValueAsLong("Content-Length").orElse(-1L);
                if (contentLength > maxBytes)
                {
                    try { response.body().close(); } catch (Exception ignored) {}
                    throw new LazybotRuntimeException("文件过大，上限 " + maxBytes + " 字节");
                }

                long written = 0L;
                try (InputStream in = response.body(); OutputStream os = Files.newOutputStream(tmp))
                {
                    byte[] buf = new byte[8192];
                    int n;
                    while ((n = in.read(buf)) >= 0)
                    {
                        written += n;
                        if (written > maxBytes)
                            throw new LazybotRuntimeException("文件过大，上限 " + maxBytes + " 字节");
                        os.write(buf, 0, n);
                    }
                }

                if (written == 0)
                    throw new LazybotRuntimeException("下载内容为空");

                assertLooksLikeImage(tmp);
                Files.move(tmp, out, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                logger.info("安全下载成功：{} -> {} ({} bytes)", uri, out, written);
                return;
            }
            throw new LazybotRuntimeException("重定向次数过多");
        }
        catch (LazybotRuntimeException e)
        {
            cleanupQuietly(tmp);
            throw e;
        }
        catch (Exception e)
        {
            cleanupQuietly(tmp);
            logger.warn("安全下载失败: {}", e.getMessage());
            throw new LazybotRuntimeException("指定图片链接无法下载: " + e.getMessage());
        }
    }

    static URI validateHttpsPublicUri(String raw)
    {
        if (raw == null || raw.isBlank())
            throw new LazybotRuntimeException("超链接为空");
        URI uri;
        try
        {
            uri = URI.create(raw.trim());
        }
        catch (Exception e)
        {
            throw new LazybotRuntimeException("超链接无效");
        }
        if (!"https".equalsIgnoreCase(uri.getScheme()))
            throw new LazybotRuntimeException("仅允许 https 链接");
        if (uri.getHost() == null || uri.getHost().isBlank())
            throw new LazybotRuntimeException("超链接缺少主机名");

        String host = uri.getHost().toLowerCase(Locale.ROOT);
        if (BLOCKED_HOSTS.contains(host) || host.endsWith(".local") || host.endsWith(".localhost"))
            throw new LazybotRuntimeException("不允许访问该主机");

        try
        {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            for (InetAddress addr : addresses)
            {
                if (isBlockedAddress(addr))
                    throw new LazybotRuntimeException("不允许访问内网或本机地址");
            }
        }
        catch (LazybotRuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new LazybotRuntimeException("无法解析主机名: " + host);
        }
        return uri;
    }

    private static boolean isBlockedAddress(InetAddress addr)
    {
        return addr.isAnyLocalAddress()
                || addr.isLoopbackAddress()
                || addr.isLinkLocalAddress()
                || addr.isSiteLocalAddress()
                || addr.isMulticastAddress()
                || isUniqueLocalIpv6(addr)
                || isCgnat(addr);
    }

    private static boolean isUniqueLocalIpv6(InetAddress addr)
    {
        byte[] b = addr.getAddress();
        // fc00::/7
        return b.length == 16 && (b[0] & 0xfe) == 0xfc;
    }

    private static boolean isCgnat(InetAddress addr)
    {
        byte[] b = addr.getAddress();
        // 100.64.0.0/10
        return b.length == 4 && (b[0] & 0xff) == 100 && ((b[1] & 0xff) >= 64 && (b[1] & 0xff) <= 127);
    }

    private static void assertLooksLikeImage(Path file) throws IOException
    {
        byte[] head = Files.readAllBytes(file);
        if (head.length < 4)
            throw new LazybotRuntimeException("不是有效图片");
        boolean png = head[0] == (byte) 0x89 && head[1] == 0x50 && head[2] == 0x4E && head[3] == 0x47;
        boolean jpg = (head[0] & 0xff) == 0xFF && (head[1] & 0xff) == 0xD8;
        boolean webp = head.length >= 12
                && head[0] == 'R' && head[1] == 'I' && head[2] == 'F' && head[3] == 'F'
                && head[8] == 'W' && head[9] == 'E' && head[10] == 'B' && head[11] == 'P';
        if (!(png || jpg || webp))
            throw new LazybotRuntimeException("不是有效图片（仅支持 JPEG/PNG/WebP）");
    }

    private static void cleanupQuietly(Path tmp)
    {
        try
        {
            Files.deleteIfExists(tmp);
        }
        catch (Exception ignored) {}
    }
}
