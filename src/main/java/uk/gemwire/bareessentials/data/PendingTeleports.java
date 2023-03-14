/*
 * MIT License
 * bareessentials - https://github.com/OblivionMC/bare-essentials
 * Copyright (C) 2022-2023 Curle
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
