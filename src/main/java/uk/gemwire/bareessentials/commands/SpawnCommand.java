package uk.gemwire.bareessentials.commands;

import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class SpawnCommand {

    //TODO Figure out and fix the issue causing "%s moved too quickly!" when using this command.
    //TODO Cool down timer?
    public static int execute(CommandSourceStack player) {
        ServerLevel level = player.getServer().getLevel(Level.OVERWORLD);
        if (player.getPlayer() != null) {
            if (level == null) {
                return 0;
            }
            player.getPlayer().teleportTo(level.getSharedSpawnPos().getX(), level.getSharedSpawnPos().getY(),
                level.getSharedSpawnPos().getZ());
        }
        return Command.SINGLE_SUCCESS;
    }
}
