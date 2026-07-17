package me.aloic.lazybot.http.dev;

import me.aloic.lazybot.command.core.CommandResult;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/** Converts internal results into lightweight development-HTTP payloads. */
@Component
public class DevCommandResultMapper {
    private final DevCommandArtifactStore artifactStore;

    public DevCommandResultMapper(DevCommandArtifactStore artifactStore) {
        this.artifactStore = artifactStore;
    }

    public Object map(CommandResult result) {
        if (result instanceof CommandResult.Text text) {
            return new TextPayload(text.getType(), text.content());
        }
        if (result instanceof CommandResult.Image image) {
            DevCommandArtifactStore.Artifact artifact = artifactStore.store(
                    image.content(), image.contentType(), image.fileName()
            );
            return new ImagePayload(
                    image.getType(),
                    artifact.id(),
                    "/test/artifacts/" + artifact.id(),
                    artifact.contentType(),
                    artifact.fileName(),
                    image.caption(),
                    artifact.content().length,
                    artifact.expiresAt()
            );
        }
        if (result instanceof CommandResult.Composite composite) {
            return new CompositePayload(
                    composite.getType(),
                    composite.results().stream().map(this::map).toList()
            );
        }
        if (result instanceof CommandResult.LegacySideEffect legacy) {
            return new LegacyPayload(legacy.getType(), legacy.message());
        }
        return new EmptyPayload(result.getType());
    }

    public record TextPayload(String type, String content) {}

    public record ImagePayload(
            String type,
            String artifactId,
            String artifactUrl,
            String contentType,
            String fileName,
            String caption,
            int size,
            Instant expiresAt
    ) {}

    public record CompositePayload(String type, List<Object> results) {}

    public record LegacyPayload(String type, String message) {}

    public record EmptyPayload(String type) {}
}
