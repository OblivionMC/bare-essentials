package uk.gemwire.bareessentials.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import uk.gemwire.bareessentials.BareEssentials;
import uk.gemwire.bareessentials.data.TeleportRewind;

public class CmdBack {


    public static int execute(CommandContext<CommandSourceStack> cmd) {
        var server = cmd.getSource().getServer();
        var rewinds = TeleportRewind.getOrCreate(server.overworld());
        var player = cmd.getSource().getPlayer().getUUID();
        var splayer = cmd.getSource().getPlayer();
        boolean stack = server.overworld().getGameRules().get(BareEssentials.BACK_STACK);

        // Pop always; if no stacking, just re-insert the last element with source and destination swapped so you get sent back.
        TeleportRewind.TeleportEvent evt = rewinds.getData().playerBacks().get(player).pop();

        if (!stack) {
            BlockPos.MutableBlockPos evtDest = evt.destination().mutable();
            // 0,0,0 means that the event (LivingDeath, MoveToDimension) didn't provide context about where they end up.
            // We update the destination with the location the player is trying to /back from.
            // TODO: If you end up in a different dimension by some method we can't track, then /back from a portal teleport will put you in a dimension other than the one you took the portal from originally.

            if (evt.destination().equals(new BlockPos(0,0,0))) {
                evtDest.set(splayer.blockPosition());
            }
            TeleportRewind.TeleportEvent evtReverse = new TeleportRewind.TeleportEvent(evt.type(), evtDest, evt.origin());
            rewinds.getData().playerBacks().get(player).add(evtReverse);
        }

        if (!splayer.randomTeleport(evt.origin().getX(), evt.origin().getY(), evt.origin().getZ(), false))
            splayer.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault("bareessentials.teleport.unsafe")));


        return Command.SINGLE_SUCCESS;

    }


    public static void commandTeleport(EntityTeleportEvent.TeleportCommand evt) {
        if (evt.getEntity() instanceof ServerPlayer player) {
            var tp = TeleportRewind.getOrCreate(player.level().getServer().overworld());
            var stack = tp.getData().playerBacks().get(player.getUUID());
            stack.add(
                new TeleportRewind.TeleportEvent(
                    TeleportRewind.EventType.COMMAND,
                    player.blockPosition(),
                    new BlockPos(
                        (int) evt.getTargetX(),
                        (int) evt.getTargetY(),
                        (int) evt.getTargetZ()
                    )
                )
            );
            tp.setDirty();
        }
    }

    public static void deathTeleport(LivingDeathEvent evt) {
        if (evt.getEntity() instanceof ServerPlayer player) {
            var tp = TeleportRewind.getOrCreate(player.level().getServer().overworld());
            var stack = tp.getData().playerBacks().get(player.getUUID());
            stack.add(
                new TeleportRewind.TeleportEvent(
                    TeleportRewind.EventType.COMMAND,
                    player.blockPosition(),
                    player.getRespawnConfig() != null && player.level().dimension() == player.getRespawnConfig().respawnData().dimension() ? player.getRespawnConfig().respawnData().pos() : player.level().getRespawnData().pos()
                )
            );
            tp.setDirty();
        }
    }

    public static void portalTeleport(EntityTravelToDimensionEvent evt) {
        if (evt.getEntity() instanceof ServerPlayer player) {
            var tp = TeleportRewind.getOrCreate(player.level().getServer().overworld());
            var stack = tp.getData().playerBacks().get(player.getUUID());
            stack.add(
                new TeleportRewind.TeleportEvent(
                    TeleportRewind.EventType.COMMAND,
                    player.blockPosition(),
                    new BlockPos(0, 0, 0)
                )
            );
            tp.setDirty();
        }
    }

}
