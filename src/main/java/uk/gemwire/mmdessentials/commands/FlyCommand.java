package uk.gemwire.mmdessentials.commands;

import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class FlyCommand {

    public static int execute(CommandSourceStack player) {
        //TODO Figure out why it doesn't let the user fly yet changes the values.
        if (!player.getPlayer().getAbilities().mayfly) {
            player.getPlayer().getAbilities().mayfly = true;
            player.getPlayer().sendSystemMessage(Component.translatable("mmdessentials.fly.enabled"));
        } else {
            player.getPlayer().getAbilities().mayfly = false;
            player.getPlayer().getAbilities().flying = false;
            player.getPlayer().sendSystemMessage(Component.translatable("mmdessentials.fly.disabled"));
        }
        return Command.SINGLE_SUCCESS;
    }
}
