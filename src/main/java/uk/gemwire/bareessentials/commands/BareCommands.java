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

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.RegisterCommandsEvent;

import static net.minecraft.commands.Commands.literal;

public class BareCommands {

    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            literal("setspawn")
                .requires(s -> s.hasPermission(Commands.LEVEL_ADMINS))
                .executes((s) -> CmdSetWorldSpawn.execute(s.getSource(),
                    new BlockPos(s.getSource().getPosition()), 0.0F))
        );

        event.getDispatcher().register(
            literal("tpa")
                .then(Commands.argument("user", EntityArgument.player())
                    .executes(CmdTeleportRequest::tpa)
                )
                .then(Commands.literal("accept")
                    .executes(CmdTeleportRequest::accept)
                )
                .then(Commands.literal("deny")
                    .executes(CmdTeleportRequest::deny)
                )
        );

        event.getDispatcher().register(
            literal("spawn")
                .executes((s) -> CmdSpawn.execute(s.getSource()))
        );

        event.getDispatcher().register(
            literal("fly")
                .requires(s -> s.hasPermission(Commands.LEVEL_ADMINS))
                .executes(CmdFly::executeOnSelf)

                .then(Commands.argument("user", EntityArgument.player())
                    .executes(CmdFly::executeOnOther)
                )
        );

        event.getDispatcher().register(
            literal("balance")
                .requires(s -> s.hasPermission(Commands.LEVEL_ALL))
                .executes(CmdBalance::executeOnSelf) // /balance

                .then(Commands.argument("user", EntityArgument.player())
                    .requires(s -> s.hasPermission(Commands.LEVEL_MODERATORS))
                    .executes(CmdBalance::executeOnOther) // /balance <user>
                )

                .then(Commands.literal("give")
                    .requires(s -> s.hasPermission(Commands.LEVEL_MODERATORS))

                    .then(Commands.argument("amount", IntegerArgumentType.integer())
                        .executes(CmdBalance.Give::executeOnSelf) // /balance give <amount>
                    )

                    .then(Commands.argument("user", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                            .executes(CmdBalance.Give::executeOnOther) // /balance give <user> <amount>
                        )
                    )

                )

                .then(Commands.literal("set")
                    .requires(s -> s.hasPermission(Commands.LEVEL_MODERATORS))

                    .then(Commands.argument("amount", IntegerArgumentType.integer())
                        .executes(CmdBalance.Set::executeOnSelf) // /balance set <amount>
                    )

                    .then(Commands.argument("user", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                            .executes(CmdBalance.Set::executeOnOther) // /balance set <user> <amount>
                        )
                    )
                )

                .then(Commands.literal("remove")
                    .requires(s -> s.hasPermission(Commands.LEVEL_MODERATORS))

                    .then(Commands.argument("amount", IntegerArgumentType.integer())
                        .executes(CmdBalance.Remove::executeOnSelf) // /balance remove <amount>
                    )

                    .then(Commands.argument("user", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                            .executes(CmdBalance.Remove::executeOnOther) // /balance remove <user> <amount>
                        )
                    )
                )
        );

        event.getDispatcher().register(
            literal("god")
                .requires(s -> s.hasPermission(Commands.LEVEL_ADMINS))
                .executes(CmdGod::executeOnSelf)

                .then(Commands.argument("user", EntityArgument.player())
                    .executes(CmdGod::executeOnOther)
                )
        );


        event.getDispatcher().register(
            literal("heal")
                .requires(s -> s.hasPermission(Commands.LEVEL_ADMINS))
                .executes(CmdHeal::executeOnSelf)

                .then(Commands.argument("user", EntityArgument.player())
                    .executes(CmdHeal::executeOnOther)
                )
        );

        event.getDispatcher().register(
            literal("feed")
                .requires(s -> s.hasPermission(Commands.LEVEL_ADMINS))
                .executes(CmdHeal.Feed::executeOnSelf)

                .then(Commands.argument("user", EntityArgument.player())
                    .executes(CmdHeal.Feed::executeOnOther)
                )
        );

        event.getDispatcher().register(
            literal("sethome")
                .executes(CmdHomes.Set::execute)
        );

        event.getDispatcher().register(
            literal("home")
                .executes(CmdHomes::execute)
        );
    }
}
