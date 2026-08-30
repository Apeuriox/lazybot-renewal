package me.aloic.lazybot.tencent.command;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import me.aloic.lazybot.command.registry.LazybotSlashCommandRegistry;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.tencent.api.TencentOpenApiClient;
import me.aloic.lazybot.util.HelpFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class TencentCommandPanelRegistrar
{
    private static final Logger logger = LoggerFactory.getLogger(TencentCommandPanelRegistrar.class);
    private static final String PANEL_REMARK = "lazybot-commands";
    private static final int NAME_LIMIT = 14;
    private static final int DESC_LIMIT = 30;
    private static final int ITEMS_PER_PANEL = 20;
    private static final List<String> SCOPES = List.of("group", "c2c");
    private static final List<String> PRIORITY_COMMANDS = List.of(
            "help", "link", "unlink", "setmode", "bp", "card", "pr", "score",
            "profile", "bplist", "c", "m", "todaybp", "nochoke", "ppp",
            "f", "customize", "setpanel", "tips", "check");

    private final TencentOpenApiClient apiClient;
    private final LazybotSlashCommandRegistry commandRegistry;
    private final String commandPrefix;

    public TencentCommandPanelRegistrar(
            TencentOpenApiClient apiClient,
            LazybotSlashCommandRegistry commandRegistry,
            @Value("${lazybot.prefix:/}") String commandPrefix)
    {
        this.apiClient = apiClient;
        this.commandRegistry = commandRegistry;
        this.commandPrefix = commandPrefix == null ? "/" : commandPrefix;
    }

    public void sync()
    {
        JSONArray items = buildItems();
        if (items.isEmpty()) {
            logger.info("Tencent 指令面板没有可注册的命令");
            return;
        }
        for (String scope : SCOPES) {
            try {
                syncScope(scope, items);
            }
            catch (Exception e) {
                logger.warn("同步 Tencent {} 指令面板失败: {}", scope, e.getMessage());
            }
        }
    }

    private void syncScope(String scope, JSONArray items)
    {
        List<JSONObject> reusable = findReusablePanels(scope);
        JSONObject panel = new JSONObject();
        panel.put("items", items);
        panel.put("remark", PANEL_REMARK);

        if (!reusable.isEmpty()) {
            JSONObject body = new JSONObject();
            body.put("panel", panel);
            String panelId = reusable.get(0).getString("panel_id");
            apiClient.updateCommandPanel(panelId, body);
            logger.info("已更新 Tencent {} 指令面板 {} ({} 条)", scope, panelId, items.size());
            for (int i = 1; i < reusable.size(); i++) {
                String extraId = reusable.get(i).getString("panel_id");
                try {
                    apiClient.deleteCommandPanel(extraId);
                    logger.info("已删除多余的 Tencent {} 指令面板 {}", scope, extraId);
                }
                catch (Exception e) {
                    logger.warn("删除多余 Tencent {} 指令面板 {} 失败: {}", scope, extraId, e.getMessage());
                }
            }
            return;
        }

        JSONObject body = new JSONObject();
        body.put("scope", scope);
        body.put("target_type", "all");
        body.put("panel", panel);
        try {
            String panelId = apiClient.createCommandPanel(body);
            logger.info("已创建 Tencent {} 指令面板 {} ({} 条)", scope, panelId, items.size());
        }
        catch (LazybotRuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("40030013")) {
                logger.warn("Tencent {} 指令面板已达数量上限，尝试复用已有全局面板", scope);
                reuseAnyGlobalPanel(scope, panel);
                return;
            }
            throw e;
        }
    }

    private void reuseAnyGlobalPanel(String scope, JSONObject panel)
    {
        JSONArray records = apiClient.listCommandPanels(scope);
        for (int i = 0; i < records.size(); i++) {
            JSONObject record = records.getJSONObject(i);
            if (record == null || !"all".equalsIgnoreCase(record.getString("target_type"))) {
                continue;
            }
            String panelId = record.getString("panel_id");
            JSONObject body = new JSONObject();
            body.put("panel", panel);
            apiClient.updateCommandPanel(panelId, body);
            logger.info("已复用并更新 Tencent {} 指令面板 {}", scope, panelId);
            return;
        }
        throw new LazybotRuntimeException("Tencent " + scope + " 指令面板超出数量限制，且没有可复用的全局面板");
    }

    private List<JSONObject> findReusablePanels(String scope)
    {
        List<JSONObject> matched = new ArrayList<>();
        List<JSONObject> globals = new ArrayList<>();
        JSONArray records = apiClient.listCommandPanels(scope);
        logger.info("Tencent {} 指令面板现有 {} 个", scope, records.size());
        for (int i = 0; i < records.size(); i++) {
            JSONObject record = records.getJSONObject(i);
            if (record == null || !"all".equalsIgnoreCase(record.getString("target_type"))) {
                continue;
            }
            globals.add(record);
            JSONObject panel = record.getJSONObject("panel");
            String remark = panel == null ? null : panel.getString("remark");
            if (remark != null && remark.startsWith(PANEL_REMARK)) {
                matched.add(record);
            }
        }
        matched.sort(Comparator.comparing(left -> String.valueOf(panelRemark(left))));
        if (!matched.isEmpty()) {
            return matched;
        }
        return globals;
    }

    private static String panelRemark(JSONObject record)
    {
        JSONObject panel = record.getJSONObject("panel");
        return panel == null ? "" : String.valueOf(panel.getString("remark"));
    }

    private JSONArray buildItems()
    {
        Map<String, LazybotSlashCommandRegistry.NamedCommand> byName = new LinkedHashMap<>();
        for (LazybotSlashCommandRegistry.NamedCommand named : commandRegistry.listUniqueCommands()) {
            byName.put(named.primaryName().toLowerCase(Locale.ROOT), named);
        }
        List<LazybotSlashCommandRegistry.NamedCommand> ordered = new ArrayList<>();
        for (String priority : PRIORITY_COMMANDS) {
            LazybotSlashCommandRegistry.NamedCommand named = byName.remove(priority);
            if (named != null) {
                ordered.add(named);
            }
        }
        List<LazybotSlashCommandRegistry.NamedCommand> rest = new ArrayList<>(byName.values());
        rest.sort(Comparator.comparing(LazybotSlashCommandRegistry.NamedCommand::primaryName));
        ordered.addAll(rest);

        JSONArray items = new JSONArray();
        for (LazybotSlashCommandRegistry.NamedCommand named : ordered) {
            if (items.size() >= ITEMS_PER_PANEL) {
                break;
            }
            JSONObject item = new JSONObject();
            item.put("type", "command");
            item.put("name", truncate(commandPrefix + named.primaryName(), NAME_LIMIT));
            item.put("desc", HelpFormatter.panelDescription(named.command().getHelp(), DESC_LIMIT));
            items.add(item);
        }
        int omitted = ordered.size() - items.size();
        if (omitted > 0) {
            logger.info("Tencent 指令面板每面板最多 {} 条，已省略 {} 个命令", ITEMS_PER_PANEL, omitted);
        }
        return items;
    }

    private static String truncate(String value, int maxChars)
    {
        if (value == null) {
            return "";
        }
        return value.length() <= maxChars ? value : value.substring(0, maxChars);
    }
}
