package uk.gemwire.bareessentials.data;

import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PendingTeleports {
    private static Logger logger = LogManager.getLogger();

    public record TeleportRequest(ServerPlayer sender, ServerPlayer receiver, boolean pending) {
    }

    public static final List<TeleportRequest> PENDING = new ArrayList<>();

    public static TeleportRequest getRequestFrom(ServerPlayer sender) {
        for (TeleportRequest request : PENDING) {
            if (request.sender == sender) {
                return request;
            }
        }

        return null;
    }

    public static TeleportRequest getRequestFor(ServerPlayer target) {
        logger.info("Finding a pending request that teleports to {}", target.getDisplayName().getString());
        for (TeleportRequest request : PENDING) {
            logger.info("Considering Request from {} to {}.", request.sender.getDisplayName().getString(), request.receiver.getDisplayName().getString());
            if (request.receiver == target) {
                logger.info("Request valid, target found.");
                return request;
            }
        }

        return null;
    }

    public static void removeRequestFrom(ServerPlayer sender) {
        logger.info("Removing a teleport request from {}", sender.getDisplayName().getString());
        PENDING.remove(getRequestFrom(sender));
    }
}
