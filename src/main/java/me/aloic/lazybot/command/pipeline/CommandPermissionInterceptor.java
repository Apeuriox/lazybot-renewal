package me.aloic.lazybot.command.pipeline;

import me.aloic.lazybot.command.core.CommandPlatform;
import me.aloic.lazybot.command.core.CommandRequest;
import me.aloic.lazybot.command.core.CommandResult;
import me.aloic.lazybot.command.core.PlatformIndependentCommand;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.service.PermissionService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Order(1)
public class CommandPermissionInterceptor implements CommandInterceptor {
    private static final Set<String> ADMIN_BYPASS = Set.of("1524185356");

    private final PermissionService permissionService;

    public CommandPermissionInterceptor(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public CommandResult intercept(
            CommandRequest request,
            PlatformIndependentCommand command,
            CommandInterceptorChain chain
    ) throws Exception {
        if (request.context().platform() == CommandPlatform.QQ
                && ADMIN_BYPASS.contains(request.context().userId())) {
            return chain.proceed(request, command);
        }

        int version = request.command().scorePanelVersion();
        String permissionKey = command.permissionKey();
        if (request.context().platform() == CommandPlatform.HTTP_DEV
                || request.context().platform() == CommandPlatform.LOCAL_TEST) {
            requirePermission("TEST", 0L, permissionKey, version, "[TEST]权限检查失败，已停止执行");
        }
        else {
            long channelId = parseNumericId(request.context().channelId(), "频道ID");
            requirePermission("CHANNEL", channelId, permissionKey, version, "此指令已在本频道禁用");
            requirePermission("GLOBAL", 0L, permissionKey, version, "此指令已被开发者禁用");
        }
        return chain.proceed(request, command);
    }

    private void requirePermission(String type, long id, String commandKey, int version, String message) {
        try {
            if (!permissionService.checkPermission(type, id, commandKey, version)) {
                throw new LazybotRuntimeException(message);
            }
        }
        catch (LazybotRuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new LazybotRuntimeException("权限检查失败，已停止执行", e);
        }
    }

    private static long parseNumericId(String value, String label) {
        try {
            return Long.parseLong(value);
        }
        catch (NumberFormatException e) {
            throw new LazybotRuntimeException(label + "不是有效数字");
        }
    }
}
