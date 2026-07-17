package me.aloic.lazybot.controller;

import me.aloic.lazybot.command.core.CommandContext;
import me.aloic.lazybot.component.CommandGateway;
import me.aloic.lazybot.entity.WebResult;
import me.aloic.lazybot.http.dev.DevCommandArtifactStore;
import me.aloic.lazybot.http.dev.DevCommandResultMapper;
import me.aloic.lazybot.util.ResultUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Development-only HTTP adapter for manually exercising the command bus. */
@RestController
@RequestMapping("/test")
public class TestCaseController {
    private final CommandGateway commandGateway;
    private final DevCommandResultMapper resultMapper;
    private final DevCommandArtifactStore artifactStore;
    private final Long identity;
    private final Boolean testEnabled;

    public TestCaseController(
            CommandGateway commandGateway,
            DevCommandResultMapper resultMapper,
            DevCommandArtifactStore artifactStore,
            @Value("${lazybot.test.identity}") Long identity,
            @Value("${lazybot.test.enabled}") Boolean testEnabled
    ) {
        this.commandGateway = commandGateway;
        this.resultMapper = resultMapper;
        this.artifactStore = artifactStore;
        this.identity = identity;
        this.testEnabled = testEnabled;
    }

    @PostMapping("/command")
    public WebResult testCommand(@RequestBody TestCommandRequest request) {
        if (!Boolean.TRUE.equals(testEnabled)) {
            return ResultUtil.error("test not enabled");
        }

        String userId = request.userId() == null || request.userId().isBlank()
                ? String.valueOf(identity)
                : request.userId();
        String channelId = request.channelId() == null || request.channelId().isBlank()
                ? "local-dev"
                : request.channelId();
        try {
            CommandContext context = CommandContext.http(userId, channelId);
            return ResultUtil.success(resultMapper.map(commandGateway.execute(context, request.command())));
        }
        catch (Exception e) {
            return ResultUtil.error(e.getMessage());
        }
    }

    /** Compatibility endpoint for the previous manual workflow. Prefer POST /test/command. */
    @Deprecated
    @GetMapping("/command")
    public WebResult testCommandLegacy(@RequestParam("command") String command) {
        return testCommand(new TestCommandRequest(command, null, null));
    }

    @GetMapping("/artifacts/{artifactId}")
    public ResponseEntity<byte[]> getArtifact(@PathVariable String artifactId) {
        if (!Boolean.TRUE.equals(testEnabled)) {
            return ResponseEntity.notFound().build();
        }
        return artifactStore.find(artifactId)
                .map(artifact -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.parseMediaType(artifact.contentType()));
                    headers.setContentDisposition(ContentDisposition.inline().filename(artifact.fileName()).build());
                    headers.setCacheControl(CacheControl.noStore());
                    return ResponseEntity.ok().headers(headers).body(artifact.content());
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record TestCommandRequest(String command, String userId, String channelId) {}
}
