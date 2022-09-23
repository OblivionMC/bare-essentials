package uk.gemwire.mmdessentials.commands;

import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

/**
 * Allow the player to fly creative style when used.
 */
public class FlyCommand {

    public static int execute(CommandSourceStack player) {
        if (player.getPlayer() != null) {
            if (!player.getPlayer().getAbilities().mayfly) {
                player.getPlayer().getAbilities().mayfly = true;
                player.getPlayer().onUpdateAbilities();
                player.getPlayer().sendSystemMessage(Component.translatable("Flying enabled!"));
            } else {
                player.getPlayer().getAbilities().mayfly = false;
                player.getPlayer().onUpdateAbilities();
                player.getPlayer().sendSystemMessage(Component.translatable("Flying disabled!"));
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
