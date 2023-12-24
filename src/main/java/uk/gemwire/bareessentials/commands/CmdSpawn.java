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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class CmdSpawn {

    //TODO Cool down timer?
    //TODO Check for lava or things that can cause harm
    //TODO Unknown issue causing player to not move when "/spawn" is used
    public static int execute(CommandSourceStack player) {
        ServerLevel level = player.getServer().getLevel(Level.OVERWORLD);
        if (player.getPlayer() != null) {
            if (level == null) {
                return 0;
            }

            player.sendSystemMessage(Component.translatable(Language.getInstance()
                .getOrDefault("bareessentials.spawn.tospawn")));
                player.getPlayer().moveTo(level.getSharedSpawnPos().getX() + 0.5, level.getSharedSpawnPos().getY(),
                level.getSharedSpawnPos().getZ() + 0.5);
        }
        return Command.SINGLE_SUCCESS;
    }
}
