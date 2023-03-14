package uk.gemwire.bareessentials.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * Allow the player to fly creative style without being in creative mode.
 */
public class CmdFly {


    public static int executeOnSelf(CommandContext<CommandSourceStack> cmd) {
        return execute(cmd, cmd.getSource().getPlayer());
    }

    public static int executeOnOther(CommandContext<CommandSourceStack> cmd) throws CommandSyntaxException {
        return execute(cmd, EntityArgument.getPlayer(cmd, "user"));
    }

    public static int execute(CommandContext<CommandSourceStack> cmd, ServerPlayer player) {
        if (player != null) {
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
                player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                    "bareessentials.fly.enabled"), Component.translatable(Language.getInstance().getOrDefault("bareessentials.targetyou"))));

                if (cmd.getSource().getPlayer() != null && cmd.getSource().getPlayer() != player)
                    cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                        "bareessentials.fly.enabled"), player.getDisplayName().getString()));
            } else {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 5));

                player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                    "bareessentials.fly.disabled"), Component.translatable(Language.getInstance().getOrDefault("bareessentials.targetyou"))));

                if (cmd.getSource().getPlayer() != null && cmd.getSource().getPlayer() != player)
                    cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                        "bareessentials.fly.disabled"), player.getDisplayName().getString()));
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
