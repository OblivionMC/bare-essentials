package uk.gemwire.bareessentials.commands;

import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * Allow the player to fly creative style without being in creative mode.
 */
public class CmdFly {

    public static int execute(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        var abilities = player.getAbilities();
        if (player != null) {
            if (!abilities.mayfly) {
                abilities.mayfly = true;
                abilities.flying = true;
                player.onUpdateAbilities();
                player.sendSystemMessage(Component.translatable(Language.getInstance()
                    .getOrDefault("bareessentials.fly.enabled")));
            } else {
                abilities.mayfly = false;
                abilities.flying = false;
                player.onUpdateAbilities();
                player.sendSystemMessage(Component.translatable(Language.getInstance()
                    .getOrDefault("bareessentials.fly.disabled")));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 5));
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
