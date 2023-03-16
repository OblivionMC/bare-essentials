package uk.gemwire.bareessentials.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import uk.gemwire.bareessentials.data.Bank;

public class CmdHeal {

    public static int executeOnSelf(CommandContext<CommandSourceStack> cmd) {
        return execute(cmd, cmd.getSource().getPlayer());
    }

    public static int executeOnOther(CommandContext<CommandSourceStack> cmd) throws CommandSyntaxException {
        return execute(cmd, EntityArgument.getPlayer(cmd, "user"));
    }


    public static int execute(CommandContext<CommandSourceStack> cmd, ServerPlayer player) {

        player.setHealth(player.getMaxHealth());

        cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
            "bareessentials.heal"), player.getDisplayName().getString()));

        return Command.SINGLE_SUCCESS;
    }

}