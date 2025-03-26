package komeiji.back.websocket.persistence;

import komeiji.back.websocket.session.SessionToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class ConversationUtils {
    private ConversationUtils(){}
    private static final Logger logger = LoggerFactory.getLogger(ConversationUtils.class);

    /**
     * 使用java.util.UUID来生成一个唯一的标识符
     * 基于session1和session2两个SessionToken,以及时间戳以及sha256生成uuid
     * 该函数是对称的，即f(a,b)=f(b,a)
     * @param session1 session1的SessionToken
     * @param session2 session2的SessionToken
     * @return 根据两个sessionToken生成的UUID
     */
    public static UUID sessionTokens2CID(SessionToken session1, SessionToken session2) {
        StringBuilder stringBuilder = new StringBuilder();
        long currentTime = System.currentTimeMillis();

        stringBuilder.append(currentTime);
        stringBuilder.append(session1);
        stringBuilder.append(session2);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(stringBuilder.toString().getBytes());

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
