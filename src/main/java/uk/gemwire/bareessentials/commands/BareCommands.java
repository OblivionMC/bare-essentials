/*
 * MIT License
 * Bare Essentials - https://github.com/OblivionMC/bare-essentials/
 * Copyright (C) 2022-2025 Curle
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
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContext;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContextKey;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import uk.gemwire.bareessentials.invsee.Inventory;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.minecraft.commands.Commands.LEVEL_ADMINS;
import static net.minecraft.commands.Commands.literal;

public class BareCommands {

    private static Predicate<CommandSourceStack> hasPermissionNode(PermissionNode<Boolean>... node) {
        return (CommandSourceStack s) -> !s.isPlayer() || Arrays.stream(node).allMatch(p -> PermissionAPI.getPermission(s.getPlayer(), p, null));
    }

    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            literal("spawn")
                .then(Commands.literal("set")
                    .requires(hasPermissionNode(PermissionNodes.SPAWN_SET))
                    .executes((s) -> CmdSetWorldSpawn.execute(s.getSource(),
                        BlockPos.containing(s.getSource().getPosition()), 0.0F))
                )
                .then(Commands.literal("find")
                    .requires(hasPermissionNode(PermissionNodes.SPAWN_FIND))
                    .executes(s -> CmdSpawnFind.execute(s.getSource()))
                )
                .requires(hasPermissionNode(PermissionNodes.SPAWN_GOTO))
                .executes((s) -> CmdSpawn.execute(s.getSource()))
        );

        event.getDispatcher().register(
            literal("tpa")
                .then(Commands.argument("user", EntityArgument.player())
                    .requires(hasPermissionNode(PermissionNodes.TPA))
                    .executes(CmdTeleportRequest::tpa)
                )
                .then(Commands.literal("accept")
                    .requires(hasPermissionNode(PermissionNodes.TPA_ACCEPT))
                    .executes(CmdTeleportRequest::accept)
                )
                .then(Commands.literal("deny")
                    .executes(CmdTeleportRequest::deny)
                )
        );

        event.getDispatcher().register(
            literal("tpahere")
                .then(Commands.argument("user", EntityArgument.player())
                    .executes(CmdTeleportRequest::tpahere)
                )
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
                .then(literal("overwrite")
                    .executes(CmdHomes.Overwrite::execute)
                )
                .then(literal("none")
                    .executes(CmdHomes.Remove::execute)
                )
        );

        event.getDispatcher().register(
            literal("home")
                .executes(CmdHomes::execute)
        );

        event.getDispatcher().register(
            literal("invsee")
                .requires(s -> s.hasPermission(LEVEL_ADMINS))
                .then(Commands.argument("user", StringArgumentType.word())
                    .executes(c -> Inventory.openInventoryOf(c.getSource().getPlayer(), StringArgumentType.getString(c, "user")))
                    .suggests(Inventory.SUGGEST_USERS)
                )
        );
    }
}
