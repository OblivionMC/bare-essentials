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
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import uk.gemwire.bareessentials.BareEssentials;
import uk.gemwire.bareessentials.invsee.Inventory;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
                    .executes((s) -> CmdSpawn.executeSet(s.getSource(),
                        BlockPos.containing(s.getSource().getPosition()), 0.0F))
                )
                .then(Commands.literal("find")
                    .requires(hasPermissionNode(PermissionNodes.SPAWN_FIND))
                    .executes(s -> CmdSpawn.executeFind(s.getSource()))
                )
                .requires(hasPermissionNode(PermissionNodes.SPAWN_GOTO))
                .executes((s) -> CmdSpawn.executeGoto(s.getSource()))
        );

        event.getDispatcher().register(
            literal("tpa")
                .then(Commands.argument("user", EntityArgument.player())
                    .requires(hasPermissionNode(PermissionNodes.TPA))
                    .executes(CmdTeleportRequest::tpa)
                )
                .then(Commands.literal("accept")
                    .then(Commands.literal("auto")
                        .requires(hasPermissionNode(PermissionNodes.TPA, PermissionNodes.TPA_ACCEPT_AUTO))
                        .then(Commands.literal("enable")
                            .executes(CmdTeleportRequest::enableAuto)
                        )
                        .then(Commands.literal("disable")
                            .executes(CmdTeleportRequest::disableAuto)
                        )
                    )
                    .requires(hasPermissionNode(PermissionNodes.TPA, PermissionNodes.TPA_ACCEPT))
                    .executes(CmdTeleportRequest::accept)
                )
                .then(Commands.literal("deny")
                    .requires(hasPermissionNode(PermissionNodes.TPA, PermissionNodes.TPA_DENY))
                    .executes(CmdTeleportRequest::deny)
                )
                .then(Commands.literal("here")
                    .then(Commands.argument("user", EntityArgument.player())
                        .requires(hasPermissionNode(PermissionNodes.TPA, PermissionNodes.TPA_HERE))
                        .executes(CmdTeleportRequest::tpahere)
                    )
                )
        );

        event.getDispatcher().register(
            literal("heal")
                .requires(hasPermissionNode(PermissionNodes.HEAL))
                .executes(CmdHeal::executeOnSelf)

                .then(Commands.argument("user", EntityArgument.player())
                    .requires(hasPermissionNode(PermissionNodes.HEAL_OTHERS))
                    .executes(CmdHeal::executeOnOther)
                )
        );

        event.getDispatcher().register(
            literal("feed")
                .requires(hasPermissionNode(PermissionNodes.FEED))
                .executes(CmdHeal.Feed::executeOnSelf)

                .then(Commands.argument("user", EntityArgument.player())
                    .requires(hasPermissionNode(PermissionNodes.FEED_OTHERS))
                    .executes(CmdHeal.Feed::executeOnOther)
                )
        );

        event.getDispatcher().register(
            literal("fly")
                .requires(hasPermissionNode(PermissionNodes.FLY))
                .executes(CmdFly::executeOnSelf)

                .then(Commands.argument("user", EntityArgument.player())
                    .requires(hasPermissionNode(PermissionNodes.FLY_OTHERS))
                    .executes(CmdFly::executeOnOther)
                )
        );

        event.getDispatcher().register(
            literal("bank")
                .requires(bankAnd(hasPermissionNode(PermissionNodes.BANK, PermissionNodes.BANK_GET)))
                .executes(CmdBank::executeOnSelf) // /balance

                .then(Commands.literal("get")
                    .requires(bankAnd(hasPermissionNode(PermissionNodes.BANK, PermissionNodes.BANK_GET)))
                    .executes(CmdBank::executeOnSelf)

                    .then(Commands.argument("user", EntityArgument.player())
                        .requires(bankAnd(hasPermissionNode(PermissionNodes.BANK, PermissionNodes.BANK_GET_OTHERS)))
                        .executes(CmdBank::executeOnOther) // /balance get <user>
                    )
                )

                .then(Commands.argument("user", EntityArgument.player())
                    .requires(bankAnd(hasPermissionNode(PermissionNodes.BANK, PermissionNodes.BANK_GET_OTHERS)))
                    .executes(CmdBank::executeOnOther) // /balance <user>
                )

                .then(Commands.literal("give")
                    .requires(bankAnd(hasPermissionNode(PermissionNodes.BANK, PermissionNodes.BANK_GIVE)))

                    .then(Commands.argument("amount", IntegerArgumentType.integer())
                        .executes(CmdBank.Give::executeOnSelf) // /balance give <amount>
                    )

                    .then(Commands.argument("user", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                            .executes(CmdBank.Give::executeOnOther) // /balance give <user> <amount>
                        )
                    )

                )

                .then(Commands.literal("set")
                    .requires(bankAnd(hasPermissionNode(PermissionNodes.BANK, PermissionNodes.BANK_SET)))

                    .then(Commands.argument("amount", IntegerArgumentType.integer())
                        .executes(CmdBank.Set::executeOnSelf) // /balance set <amount>
                    )

                    .then(Commands.argument("user", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                            .executes(CmdBank.Set::executeOnOther) // /balance set <user> <amount>
                        )
                    )
                )

                .then(Commands.literal("remove")
                    .requires(bankAnd(hasPermissionNode(PermissionNodes.BANK, PermissionNodes.BANK_REMOVE)))

                    .then(Commands.argument("amount", IntegerArgumentType.integer())
                        .executes(CmdBank.Remove::executeOnSelf) // /balance remove <amount>
                    )

                    .then(Commands.argument("user", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                            .executes(CmdBank.Remove::executeOnOther) // /balance remove <user> <amount>
                        )
                    )
                )

                .then(Commands.literal("clear")
                    .requires(bankAnd(hasPermissionNode(PermissionNodes.BANK, PermissionNodes.BANK_CLEAR)))

                    .then(Commands.argument("amount", IntegerArgumentType.integer())
                        .executes(CmdBank.Clear::executeOnSelf) // /balance remove <amount>
                    )

                    .then(Commands.argument("user", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                            .executes(CmdBank.Clear::executeOnOther) // /balance remove <user> <amount>
                        )
                    )
                )

                .then(Commands.literal("top")
                    .requires(bankAnd(hasPermissionNode(PermissionNodes.BANK, PermissionNodes.BANK_TOP)))
                    .executes(CmdBank.Top::execute)
                )

                .then(Commands.literal("value")
                    .requires(bankAnd(marketAnd(hasPermissionNode(PermissionNodes.BANK))))

                    .then(Commands.literal("get")
                        .requires(bankAnd(marketAnd(hasPermissionNode(PermissionNodes.BANK, PermissionNodes.BANK_VALUE_GET))))
                        .executes(CmdBank.Value::get)
                    )

                    .then(Commands.literal("set")
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                            .executes(CmdBank.Value::set) // /balance remove <user> <amount>
                        )
                    )

                    .then(Commands.literal("clear")
                        .requires(bankAnd(marketAnd(hasPermissionNode(PermissionNodes.BANK, PermissionNodes.BANK_VALUE_CLEAR))))
                        .executes(CmdBank.Value::clear)
                    )
                )
        );

        event.getDispatcher().register(
            literal("god")
                .requires(hasPermissionNode(PermissionNodes.GOD))
                .executes(CmdGod::executeOnSelf)

                .then(Commands.argument("user", EntityArgument.player())
                    .requires(hasPermissionNode(PermissionNodes.GOD_OTHERS))
                    .executes(CmdGod::executeOnOther)
                )
        );


        event.getDispatcher().register(
            literal("home")
                .requires(hasPermissionNode(PermissionNodes.HOME, PermissionNodes.HOME_GOTO))
                .executes(CmdHomes::execute)
                .then(Commands.literal("set")
                    .requires(hasPermissionNode(PermissionNodes.HOME, PermissionNodes.HOME_SET))
                    .executes(CmdHomes.Set::execute)
                    .then(literal("overwrite")
                        .executes(CmdHomes.Overwrite::execute)
                    )
                    .then(literal("none")
                        .executes(CmdHomes.Remove::execute)
                    ))
                .then(Commands.literal("get")
                    .requires(hasPermissionNode(PermissionNodes.HOME, PermissionNodes.HOME_GET))
                    .executes(CmdHomes.Get::execute)
                )
        );

        event.getDispatcher().register(
            literal("invsee")
                .requires(hasPermissionNode(PermissionNodes.INVSEE))
                .then(Commands.argument("user", StringArgumentType.word())
                    .executes(c -> Inventory.openInventoryOf(c.getSource().getPlayer(), StringArgumentType.getString(c, "user")))
                    .suggests(Inventory.SUGGEST_USERS)
                )
        );











        // /tp all x y z
        event.getDispatcher().getRoot().getChild("teleport").addChild(
            literal("all")
                .then(
                    Commands.argument("location", Vec3Argument.vec3())
                        .executes(
                            p_139047_ -> TeleportCommand.teleportToPos(
                                p_139047_.getSource(),
                                p_139047_.getSource().getLevel().getPlayers(s -> true),
                                p_139047_.getSource().getLevel(),
                                Vec3Argument.getCoordinates(p_139047_, "location"),
                                null,
                                null
                            )
                        )
                        .then(
                            Commands.argument("rotation", RotationArgument.rotation())
                                .executes(
                                    p_139045_ -> TeleportCommand.teleportToPos(
                                        p_139045_.getSource(),
                                        p_139045_.getSource().getLevel().getPlayers(s -> true),
                                        p_139045_.getSource().getLevel(),
                                        Vec3Argument.getCoordinates(p_139045_, "location"),
                                        RotationArgument.getRotation(p_139045_, "rotation"),
                                        null
                                    )
                                )
                        )
                        .then(
                            Commands.literal("facing")
                                .then(
                                    Commands.literal("entity")
                                        .then(
                                            Commands.argument("facingEntity", EntityArgument.entity())
                                                .executes(
                                                    p_326749_ -> TeleportCommand.teleportToPos(
                                                        p_326749_.getSource(),
                                                        p_326749_.getSource().getLevel().getPlayers(s -> true),
                                                        p_326749_.getSource().getLevel(),
                                                        Vec3Argument.getCoordinates(p_326749_, "location"),
                                                        null,
                                                        new TeleportCommand.LookAtEntity(
                                                            EntityArgument.getEntity(p_326749_, "facingEntity"), EntityAnchorArgument.Anchor.FEET
                                                        )
                                                    )
                                                )
                                                .then(
                                                    Commands.argument("facingAnchor", EntityAnchorArgument.anchor())
                                                        .executes(
                                                            p_326750_ -> TeleportCommand.teleportToPos(
                                                                p_326750_.getSource(),
                                                                p_326750_.getSource().getLevel().getPlayers(s -> true),
                                                                p_326750_.getSource().getLevel(),
                                                                Vec3Argument.getCoordinates(p_326750_, "location"),
                                                                null,
                                                                new TeleportCommand.LookAtEntity(
                                                                    EntityArgument.getEntity(p_326750_, "facingEntity"),
                                                                    EntityAnchorArgument.getAnchor(p_326750_, "facingAnchor")
                                                                )
                                                            )
                                                        )
                                                )
                                        )
                                )
                                .then(
                                    Commands.argument("facingLocation", Vec3Argument.vec3())
                                        .executes(
                                            p_326751_ -> TeleportCommand.teleportToPos(
                                                p_326751_.getSource(),
                                                p_326751_.getSource().getLevel().getPlayers(s -> true),
                                                p_326751_.getSource().getLevel(),
                                                Vec3Argument.getCoordinates(p_326751_, "location"),
                                                null,
                                                new TeleportCommand.LookAtPosition(Vec3Argument.getVec3(p_326751_, "facingLocation"))
                                            )
                                        )
                                )
                        )
                ).build()
        );

        // /tp Curle here

        event.getDispatcher().getRoot().getChild("tp").getChild("targets").addChild(
            literal("here")
                .executes(
                    p_139047_ -> TeleportCommand.teleportToPos(
                        p_139047_.getSource(),
                        EntityArgument.getEntities(p_139047_, "targets"),
                        p_139047_.getSource().getLevel(),
                        WorldCoordinates.absolute(p_139047_.getSource().getPosition().x, p_139047_.getSource().getPosition().y, p_139047_.getSource().getPosition().z),
                        null,
                        null
                    )
                )
                .then(
                    Commands.argument("rotation", RotationArgument.rotation())
                        .executes(
                            p_139045_ -> TeleportCommand.teleportToPos(
                                p_139045_.getSource(),
                                EntityArgument.getEntities(p_139045_, "targets"),
                                p_139045_.getSource().getLevel(),
                                WorldCoordinates.absolute(p_139045_.getSource().getPosition().x, p_139045_.getSource().getPosition().y, p_139045_.getSource().getPosition().z),
                                RotationArgument.getRotation(p_139045_, "rotation"),
                                null
                            )
                        )
                )
                .then(
                    Commands.literal("facing")
                        .then(
                            Commands.literal("entity")
                                .then(
                                    Commands.argument("facingEntity", EntityArgument.entity())
                                        .executes(
                                            p_326749_ -> TeleportCommand.teleportToPos(
                                                p_326749_.getSource(),
                                                EntityArgument.getEntities(p_326749_, "targets"),
                                                p_326749_.getSource().getLevel(),
                                                WorldCoordinates.absolute(p_326749_.getSource().getPosition().x, p_326749_.getSource().getPosition().y, p_326749_.getSource().getPosition().z),
                                                null,
                                                new TeleportCommand.LookAtEntity(
                                                    EntityArgument.getEntity(p_326749_, "facingEntity"), EntityAnchorArgument.Anchor.FEET
                                                )
                                            )
                                        )
                                        .then(
                                            Commands.argument("facingAnchor", EntityAnchorArgument.anchor())
                                                .executes(
                                                    p_326750_ -> TeleportCommand.teleportToPos(
                                                        p_326750_.getSource(),
                                                        EntityArgument.getEntities(p_326750_, "targets"),
                                                        p_326750_.getSource().getLevel(),
                                                        WorldCoordinates.absolute(p_326750_.getSource().getPosition().x, p_326750_.getSource().getPosition().y, p_326750_.getSource().getPosition().z),
                                                        null,
                                                        new TeleportCommand.LookAtEntity(
                                                            EntityArgument.getEntity(p_326750_, "facingEntity"),
                                                            EntityAnchorArgument.getAnchor(p_326750_, "facingAnchor")
                                                        )
                                                    )
                                                )
                                        )
                                )
                        )
                        .then(
                            Commands.argument("facingLocation", Vec3Argument.vec3())
                                .executes(
                                    p_326751_ -> TeleportCommand.teleportToPos(
                                        p_326751_.getSource(),
                                        EntityArgument.getEntities(p_326751_, "targets"),
                                        p_326751_.getSource().getLevel(),
                                        WorldCoordinates.absolute(p_326751_.getSource().getPosition().x, p_326751_.getSource().getPosition().y, p_326751_.getSource().getPosition().z),
                                        null,
                                        new TeleportCommand.LookAtPosition(Vec3Argument.getVec3(p_326751_, "facingLocation"))
                                    )
                                )
                        )
                ).build()
        );

        // /tp Curle random

        // /tp back
        // /back
    }

    private static Predicate<CommandSourceStack> bankAnd(Predicate<CommandSourceStack> p) {
        return c -> p.test(c) && c.getLevel().getGameRules().getRule(BareEssentials.BANK_ENABLED).get();
    }

    private static Predicate<CommandSourceStack> marketAnd(Predicate<CommandSourceStack> p) {
        return c -> p.test(c) && c.getLevel().getGameRules().getRule(BareEssentials.MARKET_ENABLED).get();
    }

}
