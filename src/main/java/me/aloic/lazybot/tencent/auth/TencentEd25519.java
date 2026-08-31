package me.aloic.lazybot.tencent.auth;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.NamedParameterSpec;
import java.util.Arrays;
import java.util.HexFormat;

/**
 * Tencent webhook signing: seed is the bot secret repeated until at least
 * 32 bytes, then truncated. Matches the Go SDK {@code ed25519.GenerateKey} flow.
 */
public final class TencentEd25519
{
    private TencentEd25519()
    {
    }

    public static KeyPair keyPairFromSecret(String botSecret)
    {
        if (botSecret == null || botSecret.isBlank()) {
            throw new IllegalArgumentException("Bot Secret 不能为空");
        }
        byte[] seed = expandSeed(botSecret.getBytes(StandardCharsets.UTF_8));
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("Ed25519");
            generator.initialize(NamedParameterSpec.ED25519, new SeededSecureRandom(seed));
            return generator.generateKeyPair();
        }
        catch (Exception e) {
            throw new IllegalStateException("无法从 Bot Secret 派生 Ed25519 密钥", e);
        }
    }

    public static String signHex(PrivateKey privateKey, byte[] message)
    {
        try {
            Signature signature = Signature.getInstance("Ed25519");
            signature.initSign(privateKey);
            signature.update(message);
            return HexFormat.of().formatHex(signature.sign());
        }
        catch (Exception e) {
            throw new IllegalStateException("Ed25519 签名失败", e);
        }
    }

    public static boolean verify(PublicKey publicKey, byte[] message, byte[] signatureBytes)
    {
        try {
            if (signatureBytes == null || signatureBytes.length != 64) {
                return false;
            }
            Signature signature = Signature.getInstance("Ed25519");
            signature.initVerify(publicKey);
            signature.update(message);
            return signature.verify(signatureBytes);
        }
        catch (Exception e) {
            return false;
        }
    }

    static byte[] expandSeed(byte[] secret)
    {
        byte[] seed = secret;
        while (seed.length < 32) {
            byte[] doubled = new byte[seed.length * 2];
            System.arraycopy(seed, 0, doubled, 0, seed.length);
            System.arraycopy(seed, 0, doubled, seed.length, seed.length);
            seed = doubled;
        }
        return Arrays.copyOf(seed, 32);
    }

    private static final class SeededSecureRandom extends SecureRandom
    {
        private final byte[] seed;
        private boolean consumed;

        private SeededSecureRandom(byte[] seed)
        {
            this.seed = seed;
        }

        @Override
        public synchronized void nextBytes(byte[] bytes)
        {
            Arrays.fill(bytes, (byte) 0);
            if (!consumed && bytes.length >= seed.length) {
                System.arraycopy(seed, 0, bytes, 0, seed.length);
                consumed = true;
            }
        }

        @Override
        public byte[] generateSeed(int numBytes)
        {
            byte[] bytes = new byte[numBytes];
            nextBytes(bytes);
            return bytes;
        }

        @Override
        public void setSeed(byte[] seed)
        {
        }

        @Override
        public void setSeed(long seed)
        {
        }
    }
}
