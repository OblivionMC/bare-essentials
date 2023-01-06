package uk.gemwire.bareessentials.commands;

import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public class CmdGod {

    public static int execute(CommandSourceStack player) {
        if (player.getPlayer() != null) {
            if (!player.getPlayer().getAbilities().invulnerable) {
                player.getPlayer().getAbilities().invulnerable = true;
                player.getPlayer().onUpdateAbilities();
                player.getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                    "bareessentials.godmode.enabled")));
            } else {
                player.getPlayer().getAbilities().invulnerable = false;
                player.getPlayer().onUpdateAbilities();
                player.getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                    "bareessentials.godmode.disabled")));
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
