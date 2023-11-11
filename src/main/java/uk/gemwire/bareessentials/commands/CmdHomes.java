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
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import uk.gemwire.bareessentials.data.Bank;
import uk.gemwire.bareessentials.data.Homes;

public class CmdHomes {

    public static int execute(CommandContext<CommandSourceStack> cmd) {
        ServerPlayer player = cmd.getSource().getPlayer();

        Homes home = Homes.getOrCreate(cmd.getSource().getLevel());

        if (player.level().dimension() != ServerLevel.OVERWORLD) {
            player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                "bareessentials.home.wrongdimension")));
            return 0;
        }

        if (home.hasUserHome(player)) {
            player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                "bareessentials.home.teleporting")));

            BlockPos pos = home.getUserHome(player);
            player.teleportTo(pos.getX(), pos.getY(), pos.getZ());

            //player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                //"bareessentials.home.success")));
        } else {
            player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                "bareessentials.home.nohome")));
        }

        return Command.SINGLE_SUCCESS;
    }

    public class Set {

        public static int execute(CommandContext<CommandSourceStack> cmd) {
            ServerPlayer player = cmd.getSource().getPlayer();
            Homes home = Homes.getOrCreate(cmd.getSource().getLevel());

            if (player.level().dimension() != ServerLevel.OVERWORLD) {
                player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                    "bareessentials.home.wrongdimension")));
                return 0;
            }

            boolean overwrite = home.hasUserHome(player);
            home.setUserHome(player, BlockPos.containing(player.getPosition(0)));

            cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                "bareessentials.sethome" + (overwrite ? ".overwrite" : ""))));

            return Command.SINGLE_SUCCESS;
        }
    }
}
