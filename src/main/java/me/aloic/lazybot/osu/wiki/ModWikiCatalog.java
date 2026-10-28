package me.aloic.lazybot.osu.wiki;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class ModWikiCatalog
{
    private static final Logger log = LoggerFactory.getLogger(ModWikiCatalog.class);

    private final Map<String, ModWikiDocument> pages;

    public ModWikiCatalog()
    {
        this.pages = load(new ObjectMapper());
        log.info("Loaded {} mod wiki pages", pages.size());
    }

    public boolean contains(String acronym)
    {
        return find(acronym) != null;
    }

    public ModWikiDocument require(String acronym)
    {
        ModWikiDocument page = find(acronym);
        if (page == null) {
            throw new LazybotRuntimeException("要么你输入的Mod名有问题，要么此Mod的页面还未创建");
        }
        return page;
    }

    private ModWikiDocument find(String acronym)
    {
        if (acronym == null || acronym.isBlank()) {
            return null;
        }
        return pages.get(acronym.trim().toUpperCase(Locale.ROOT));
    }

    private static Map<String, ModWikiDocument> load(ObjectMapper objectMapper)
    {
        Map<String, ModWikiDocument> loaded = new HashMap<>();
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:static/mods/wiki/*.json");
            for (Resource resource : resources) {
                ModWikiDocument page;
                try (InputStream in = resource.getInputStream()) {
                    JsonNode tree = objectMapper.readTree(in);
                    page = ModWikiJson.read(tree);
                }
                catch (Exception e) {
                    throw new IllegalStateException("无法解析 " + resource.getFilename(), e);
                }
                page = withAssets(page, resolveShots(page));
                String key = page.acronym().toUpperCase(Locale.ROOT);
                if (loaded.put(key, page) != null) {
                    throw new IllegalStateException("重复的 Mod 页面: " + page.acronym());
                }
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("无法读取 mod wiki", e);
        }
        return Map.copyOf(loaded);
    }

    private static ModWikiDocument withAssets(ModWikiDocument page, List<ModWikiDocument.Shot> shots)
    {
        return new ModWikiDocument(
                page.name(),
                page.acronym(),
                page.rank(),
                page.modes(),
                page.unrankedModes(),
                page.type(),
                page.multipliers(),
                page.hotkey(),
                page.effects(),
                shots,
                page.incompatible(),
                page.extra(),
                page.icon());
    }

    private static List<ModWikiDocument.Shot> resolveShots(ModWikiDocument page)
    {
        List<ModWikiDocument.Shot> resolved = new ArrayList<>();
        for (ModWikiDocument.Shot shot : page.shots()) {
            if (shot.src() == null || shot.src().isBlank()) {
                continue;
            }
            resolved.add(resolveShot(page.acronym(), shot, "截图"));
        }
        return List.copyOf(resolved);
    }

    private static ModWikiDocument.Shot resolveShot(String acronym, ModWikiDocument.Shot shot, String label)
    {
        String href = shotHref(shot.src());
        ClassPathResource file = new ClassPathResource("static/" + href);
        if (!file.exists()) {
            throw new IllegalStateException(acronym + " 的" + label + "不存在: " + href);
        }
        int width = shot.width() == null ? 0 : shot.width();
        int height = shot.height() == null ? 0 : shot.height();
        if (width <= 0 || height <= 0) {
            try (InputStream in = file.getInputStream()) {
                BufferedImage image = ImageIO.read(in);
                if (image == null) {
                    throw new IllegalStateException(acronym + " 的" + label + "无法读取: " + href);
                }
                width = image.getWidth();
                height = image.getHeight();
            }
            catch (IOException e) {
                throw new IllegalStateException(acronym + " 的" + label + "无法读取: " + href, e);
            }
        }
        return new ModWikiDocument.Shot(href, shot.label(), width, height);
    }

    private static String shotHref(String src)
    {
        String value = src.trim().replace('\\', '/');
        if (value.contains("..") || value.startsWith("/") || value.contains(":")) {
            throw new IllegalStateException("Mod截图路径无效: " + src);
        }
        if (!value.startsWith("mods/")) {
            value = "mods/shots/" + value;
        }
        return value;
    }
}
