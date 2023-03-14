/*
 * MIT License
 * bareessentials - https://github.com/OblivionMC/bare-essentials
 * Copyright (C) 2022-2023 Curle
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
