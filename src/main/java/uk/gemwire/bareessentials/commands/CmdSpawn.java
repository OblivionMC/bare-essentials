package uk.gemwire.bareessentials.commands;

import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class CmdSpawn {

    //TODO Cool down timer?
    //TODO Check for lava or things that can cause harm
    public static int execute(CommandSourceStack player) {
        ServerLevel level = player.getServer().getLevel(Level.OVERWORLD);
        if (player.getPlayer() != null) {
            if (level == null) {
                return 0;
            }

            player.sendSystemMessage(Component.translatable(Language.getInstance()
                .getOrDefault("bareessentials.spawn.tospawn")));
                player.getPlayer().moveTo(level.getSharedSpawnPos().getX() + 0.5, level.getSharedSpawnPos().getY(),
                level.getSharedSpawnPos().getZ() + 0.5);
        }
        return Command.SINGLE_SUCCESS;
    }
}
