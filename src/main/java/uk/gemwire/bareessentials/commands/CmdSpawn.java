/*
 * MIT License
 * Bare Essentials - https://github.com/OblivionMC/bare-essentials/
 * Copyright (C) 2022-2025 Curle
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
package uk.gemwire.bareessentials.commands;

import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import uk.gemwire.bareessentials.BareEssentials;
import uk.gemwire.bareessentials.data.Bank;
import uk.gemwire.bareessentials.data.Cooldowns;

public class CmdSpawn {

    public static int executeGoto(CommandSourceStack player) {
        if (player.getPlayer() != null) {
            ServerLevel level = player.getServer().getLevel(Level.OVERWORLD);
            if (level == null) {
                return 0;
            }

            Cooldowns cd = Cooldowns.getOrCreate(level);
            Bank bk = Bank.getOrCreate(level);

            if (cd.isCooldownExpired(player.getPlayer(), "spawn")) {
                if (!bk.playerHasEnough(player.getPlayer(), level.getGameRules().getInt(BareEssentials.SPAWN_COST)))
                    return 0;
                player.sendSystemMessage(Component.translatable(Language.getInstance()
                    .getOrDefault("bareessentials.spawn.tospawn")));
                // Random teleport = cancel if the destination is unsafe
                if (!player.getPlayer().randomTeleport(level.getSharedSpawnPos().getX() + 0.5, level.getSharedSpawnPos().getY(), level.getSharedSpawnPos().getZ() + 0.5, false))
                    player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault("bareessentials.teleport.unsafe")));

                bk.chargePlayer(player.getPlayer(), level.getGameRules().getInt(BareEssentials.SPAWN_COST));
                cd.setCooldownFor(player.getPlayer(), "spawn", level.getGameTime() + player.getLevel().getGameRules().getInt(BareEssentials.SPAWN_COOLDOWN));
            } else {
                player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault("bareessentials.cooldown.active"), cd.getRemainingTimeFor(player.getPlayer(), "spawn")/20));
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int executeSet(CommandSourceStack pSource, BlockPos pPos, float pAngle) {
        pSource.getLevel().setDefaultSpawnPos(pPos, pAngle);
        pSource.getServer().getGameRules().getRule(GameRules.RULE_SPAWN_RADIUS).set(0, pSource.getServer());
        pSource.sendSuccess(() -> Component.translatable(Language.getInstance()
                .getOrDefault("bareessentials.spawn.set.success"),
            pPos.getX(), pPos.getY(), pPos.getZ(), pAngle), true);
        return Command.SINGLE_SUCCESS;
    }

    public static int executeFind(CommandSourceStack player) {
        player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault("bareessentials.spawn.position"), player.getPlayer().getX(), player.getPlayer().getY(), player.getPlayer().getZ()));
        return Command.SINGLE_SUCCESS;
    }
}
