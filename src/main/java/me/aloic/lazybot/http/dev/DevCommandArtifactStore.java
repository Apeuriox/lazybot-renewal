package me.aloic.lazybot.http.dev;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** Short-lived in-memory artifacts produced by the development command adapter. */
@Component
public class DevCommandArtifactStore {
    private static final Duration ARTIFACT_TTL = Duration.ofMinutes(15);
    private static final int MAX_ARTIFACTS = 32;
    private static final int MAX_ARTIFACT_SIZE_BYTES = 20 * 1024 * 1024;

    private final ConcurrentMap<String, Artifact> artifacts = new ConcurrentHashMap<>();

    public Artifact store(byte[] content, String contentType, String fileName) {
        if (content != null && content.length > MAX_ARTIFACT_SIZE_BYTES) {
            throw new IllegalArgumentException("开发命令产物超过20MB限制");
        }
        Instant now = Instant.now();
        removeExpired(now);
        evictOldestIfFull();
        String id = UUID.randomUUID().toString();
        Artifact artifact = new Artifact(
                id,
                content,
                contentType == null || contentType.isBlank() ? "application/octet-stream" : contentType,
                fileName == null || fileName.isBlank() ? "lazybot-artifact.bin" : fileName,
                now.plus(ARTIFACT_TTL)
        );
        artifacts.put(id, artifact);
        return artifact;
    }

    public Optional<Artifact> find(String id) {
        Artifact artifact = artifacts.get(id);
        if (artifact == null) {
            return Optional.empty();
        }
        if (artifact.expiresAt().isBefore(Instant.now())) {
            artifacts.remove(id, artifact);
            return Optional.empty();
        }
        return Optional.of(artifact);
    }

    private void removeExpired(Instant now) {
        artifacts.entrySet().removeIf(entry -> entry.getValue().expiresAt().isBefore(now));
    }

    private void evictOldestIfFull() {
        if (artifacts.size() < MAX_ARTIFACTS) {
            return;
        }
        artifacts.values().stream()
                .min(java.util.Comparator.comparing(Artifact::expiresAt))
                .ifPresent(oldest -> artifacts.remove(oldest.id(), oldest));
    }

    public record Artifact(
            String id,
            byte[] content,
            String contentType,
            String fileName,
            Instant expiresAt
    ) {
        public Artifact {
            content = content == null ? new byte[0] : content.clone();
        }

        @Override
        public byte[] content() {
            return content.clone();
        }
    }
}
