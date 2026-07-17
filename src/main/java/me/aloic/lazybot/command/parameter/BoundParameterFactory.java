package me.aloic.lazybot.command.parameter;

import me.aloic.lazybot.command.core.CommandRequest;
import me.aloic.lazybot.command.identity.BoundOsuIdentity;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.osu.enums.OsuMode;
import me.aloic.lazybot.parameter.BplistParameter;
import me.aloic.lazybot.parameter.GeneralParameter;
import me.aloic.lazybot.parameter.ProfileParameter;
import org.springframework.stereotype.Component;

/** Applies binding defaults and shared command modifiers to typed command parameters. */
@Component
public class BoundParameterFactory {
    public GeneralParameter general(CommandRequest request, BoundOsuIdentity identity) {
        GeneralParameter params = GeneralParameter.analyzeParameter(request.positionalArguments());
        request.arguments().string("user")
                .filter(value -> !value.isBlank())
                .ifPresent(params::setPlayerName);
        params.setPlayerId(identity.playerId());
        params.setMode(resolveMode(request, identity));
        params.setVersion(resolveVersion(request));
        params.validateParams();
        return params;
    }

    public ProfileParameter profile(CommandRequest request, BoundOsuIdentity identity) {
        ProfileParameter params = ProfileParameter.analyzeParameter(request.positionalArguments());
        request.arguments().string("user")
                .filter(value -> !value.isBlank())
                .ifPresent(params::setPlayerName);
        params.setPlayerId(identity.playerId());
        params.setMode(resolveMode(request, identity));
        params.setLazybotId(identity.lazybotUserId());
        params.validateParams();
        return params;
    }

    public BplistParameter bpList(CommandRequest request, BoundOsuIdentity identity) {
        BplistParameter params;
        if (request.arguments().hasNamed("from") || request.arguments().hasNamed("to")) {
            int from = request.arguments().integer("from")
                    .orElseThrow(() -> new LazybotRuntimeException("缺少参数: from"));
            int to = request.arguments().integer("to")
                    .orElseThrow(() -> new LazybotRuntimeException("缺少参数: to"));
            String playerName = request.arguments().string("user")
                    .filter(value -> !value.isBlank())
                    .orElse(null);
            params = new BplistParameter(playerName, identity.playerId(), resolveMode(request, identity), from, to);
        }
        else {
            params = BplistParameter.analyzeParameter(request.positionalArguments());
        }
        params.setPlayerId(identity.playerId());
        params.setMode(resolveMode(request, identity));
        params.validateParams();
        return params;
    }

    private static String resolveMode(CommandRequest request, BoundOsuIdentity identity) {
        return request.arguments().string("mode")
                .map(OsuMode::getMode)
                .map(OsuMode::getDescribe)
                .orElseGet(() -> request.command().osuMode() == null
                        ? identity.defaultMode()
                        : request.command().osuMode().getDescribe());
    }

    private static int resolveVersion(CommandRequest request) {
        return request.arguments().integer("version")
                .orElse(request.command().scorePanelVersion());
    }
}
