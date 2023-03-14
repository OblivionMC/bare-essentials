package uk.gemwire.bareessentials.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CmdGod {

    public static int executeOnSelf(CommandContext<CommandSourceStack> cmd) {
        return execute(cmd, cmd.getSource().getPlayer());
    }

    public static int executeOnOther(CommandContext<CommandSourceStack> cmd) throws CommandSyntaxException {
        return execute(cmd, EntityArgument.getPlayer(cmd, "user"));
    }

    public static int execute(CommandContext<CommandSourceStack> cmd, ServerPlayer player) {
        if (player != null) {
            if (!player.getAbilities().invulnerable) {
                player.getAbilities().invulnerable = true;
                player.onUpdateAbilities();
                player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                    "bareessentials.godmode.enabled"), Component.translatable(Language.getInstance().getOrDefault("bareessentials.targetyou"))));

                if (cmd.getSource().getPlayer() != null && cmd.getSource().getPlayer() != player)
                    cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                        "bareessentials.godmode.enabled"), player.getDisplayName().getString()));
            } else {
                player.getAbilities().invulnerable = false;
                player.onUpdateAbilities();
                player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                    "bareessentials.godmode.disabled"), Component.translatable(Language.getInstance().getOrDefault("bareessentials.targetyou"))));

                if (cmd.getSource().getPlayer() != null && cmd.getSource().getPlayer() != player)
                    cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                        "bareessentials.godmode.disabled"), player.getDisplayName().getString()));
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
