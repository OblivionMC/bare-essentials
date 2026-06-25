package uk.gemwire.bareessentials.commands;

import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelData;
import uk.gemwire.bareessentials.BareEssentials;
import uk.gemwire.bareessentials.data.Bank;
import uk.gemwire.bareessentials.data.Cooldowns;
import uk.gemwire.bareessentials.data.TeleportRewind;

public class CmdSpawn {

    public static int executeGoto(CommandSourceStack player) {
        if (player.getPlayer() != null) {
            ServerLevel level = player.getServer().overworld();
            BlockPos preTPPos = player.getPlayer().blockPosition();
            if (level == null) {
                return 0;
            }

            Cooldowns cd = Cooldowns.getOrCreate(level);
            Bank bk = Bank.getOrCreate(level);

            if (cd.isCooldownExpired(player.getPlayer(), "spawn")) {
                if (!bk.playerHasEnough(player.getPlayer(), level.getGameRules().get(BareEssentials.SPAWN_COST)))
                    return 0;
                player.sendSystemMessage(Component.translatable(Language.getInstance()
                    .getOrDefault("bareessentials.spawn.tospawn")));
                // Random teleport = cancel if the destination is unsafe
                if (!player.getPlayer().randomTeleport(level.getRespawnData().pos().getX() + 0.5, level.getRespawnData().pos().getY(), level.getRespawnData().pos().getZ() + 0.5, false))
                    player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault("bareessentials.teleport.unsafe")));

                // Add a /back event for the spawn command
                var tp = TeleportRewind.getOrCreate(level.getServer().overworld());
                var stack = tp.getBacksFor(player.getPlayer());
                stack.add(
                    new TeleportRewind.TeleportEvent(
                        TeleportRewind.EventType.COMMAND,
                        preTPPos,
                        player.getPlayer().blockPosition()
                    )
                );
                tp.setDirty();

                bk.chargePlayer(player.getPlayer(), level.getGameRules().get(BareEssentials.SPAWN_COST));
                cd.setCooldownFor(player.getPlayer(), "spawn", level.getGameTime() + player.getLevel().getGameRules().get(BareEssentials.SPAWN_COOLDOWN));
            } else {
                player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault("bareessentials.cooldown.active"), cd.getRemainingTimeFor(player.getPlayer(), "spawn")/20));
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int executeSet(CommandSourceStack pSource, BlockPos pPos, float pAngle) {
        pSource.getLevel().setRespawnData(LevelData.RespawnData.of(pSource.getLevel().dimension(), pPos, pAngle, 0));
        pSource.getLevel().getGameRules().set(GameRules.RESPAWN_RADIUS, 0, pSource.getServer());
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
