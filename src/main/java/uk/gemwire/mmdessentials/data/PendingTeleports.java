package uk.gemwire.mmdessentials.data;

import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class PendingTeleports {
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
        for (TeleportRequest request : PENDING) {
            if (request.receiver == target)
                return request;
        }

        return null;
    }
}
