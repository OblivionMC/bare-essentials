/*
 * MIT License
 * Bare Essentials - https://github.com/OblivionMC/bare-essentials/
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

public class CmdHeal {

    public static int executeOnSelf(CommandContext<CommandSourceStack> cmd) {
        return executeHeal(cmd, cmd.getSource().getPlayer());
    }

    public static int executeOnOther(CommandContext<CommandSourceStack> cmd) throws CommandSyntaxException {
        return executeHeal(cmd, EntityArgument.getPlayer(cmd, "user"));
    }

    public static int executeHeal(CommandContext<CommandSourceStack> cmd, ServerPlayer player) {
        player.setHealth(player.getMaxHealth());

        cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
            "bareessentials.heal"), player.getDisplayName().getString()));

        return Command.SINGLE_SUCCESS;
    }

    public class Feed {

        public static int executeOnSelf(CommandContext<CommandSourceStack> cmd) {
            return executeHeal(cmd, cmd.getSource().getPlayer());
        }

        public static int executeOnOther(CommandContext<CommandSourceStack> cmd) throws CommandSyntaxException {
            return executeHeal(cmd, EntityArgument.getPlayer(cmd, "user"));
        }

        public static int executeHeal(CommandContext<CommandSourceStack> cmd, ServerPlayer player) {
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(20);
            player.getFoodData().setExhaustion(0);

            cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                "bareessentials.feed"), player.getDisplayName().getString()));

            return Command.SINGLE_SUCCESS;
        }

    }


}
