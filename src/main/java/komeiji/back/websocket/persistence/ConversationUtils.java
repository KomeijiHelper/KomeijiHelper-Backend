package komeiji.back.websocket.persistence;

import komeiji.back.websocket.session.SessionToken;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class ConversationUtils {
    private ConversationUtils(){}
    private static final Logger logger = LoggerFactory.getLogger(ConversationUtils.class);

    public static SessionToken min(@NotNull SessionToken a, @NotNull SessionToken b) {
        return a.toString().compareTo(b.toString()) < 0 ? a : b;
    }

    public static SessionToken max(@NotNull SessionToken a, @NotNull SessionToken b) {
        return a.toString().compareTo(b.toString()) > 0 ? a : b;
    }
    /**
     * 使用java.util.UUID来生成一个唯一的标识符
     * 基于session1和session2两个SessionToken
     * 该函数是对称的，即f(a,b)=f(b,a)
     * @param session1 session1的SessionToken
     * @param session2 session2的SessionToken
     * @return 根据两个sessionToken生成的UUID
     */
    public static UUID sessionTokens2CID(SessionToken session1, SessionToken session2) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(min(session1, session2));
        stringBuilder.append(max(session1, session2));

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));

            ByteBuffer buffer = ByteBuffer.wrap(hash);
            long mostSigBits = buffer.getLong();
            long leastSigBits = buffer.getLong();

            mostSigBits &= ~(0xF000L);
            mostSigBits |= (4L << 12);
            leastSigBits &= ~(0xC000000000000000L);
            leastSigBits |= (0x8000000000000000L);

            return new UUID(mostSigBits, leastSigBits);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }}
