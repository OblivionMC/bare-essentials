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
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import uk.gemwire.bareessentials.BareEssentials;
import uk.gemwire.bareessentials.data.Bank;
import uk.gemwire.bareessentials.data.Market;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CmdBank {

    public static String getCurrencySymbol(ServerLevel level) {
        return switch (level.getGameRules().getRule(BareEssentials.CURRENCY_SYMBOL).get()) {
            default -> "£";
            case 1 -> "$";
            case 2 -> "€";
            case 3 -> "¥";
            case 4 -> "¢";
            case 5 -> "₽";
        };
    }

    public static int executeOnSelf(CommandContext<CommandSourceStack> cmd) {
        return execute(cmd, cmd.getSource().getPlayer());
    }

    public static int executeOnOther(CommandContext<CommandSourceStack> cmd) throws CommandSyntaxException {
        return execute(cmd, EntityArgument.getPlayer(cmd, "user"));
    }


    public static int execute(CommandContext<CommandSourceStack> cmd, ServerPlayer player) {

        Bank accts = Bank.getOrCreate(cmd.getSource().getLevel());

        if (accts.hasUser(player)) {
            cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                "bareessentials.bank.balance"), player.getDisplayName().getString(), getCurrencySymbol(player.serverLevel()), accts.getUserBalance(player)));
        } else {
            cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                "bareessentials.bank.unable"), player.getDisplayName().getString()));
        }

        return Command.SINGLE_SUCCESS;
    }

    public static class Give {
        public static int executeOnSelf(CommandContext<CommandSourceStack> cmd) {
            return execute(cmd, cmd.getSource().getPlayer());
        }

        public static int executeOnOther(CommandContext<CommandSourceStack> cmd) throws CommandSyntaxException {
            return execute(cmd, EntityArgument.getPlayer(cmd, "user"));
        }


        public static int execute(CommandContext<CommandSourceStack> cmd, ServerPlayer player) {
            int amt = IntegerArgumentType.getInteger(cmd, "amount");

            Bank accts = Bank.getOrCreate(cmd.getSource().getLevel());

            if (accts.hasUser(player)) {
                accts.setUserBalance(player, accts.getUserBalance(player) + (long) amt);

                cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                    "bareessentials.bank.give"), getCurrencySymbol(player.serverLevel()), amt, player.getDisplayName().getString(), getCurrencySymbol(player.serverLevel()), accts.getUserBalance(player)));
            } else {
                cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                    "bareessentials.bank.unable"), player.getDisplayName().getString()));
            }

            return Command.SINGLE_SUCCESS;
        }
    }

    public static class Set {
        public static int executeOnSelf(CommandContext<CommandSourceStack> cmd) {
            return execute(cmd, cmd.getSource().getPlayer());
        }

        public static int executeOnOther(CommandContext<CommandSourceStack> cmd) throws CommandSyntaxException {
            return execute(cmd, EntityArgument.getPlayer(cmd, "user"));
        }


        public static int execute(CommandContext<CommandSourceStack> cmd, ServerPlayer player) {
            int amt = IntegerArgumentType.getInteger(cmd, "amount");

            Bank accts = Bank.getOrCreate(cmd.getSource().getLevel());

            if (accts.hasUser(player)) {
                accts.setUserBalance(player, amt);

                cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                    "bareessentials.bank.set"), player.getDisplayName().getString(), getCurrencySymbol(player.serverLevel()), accts.getUserBalance(player)));
            } else {
                cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                    "bareessentials.bank.unable"), player.getDisplayName().getString()));
            }

            return Command.SINGLE_SUCCESS;
        }
    }

    public static class Remove {
        public static int executeOnSelf(CommandContext<CommandSourceStack> cmd) {
            return execute(cmd, cmd.getSource().getPlayer());
        }

        public static int executeOnOther(CommandContext<CommandSourceStack> cmd) throws CommandSyntaxException {
            return execute(cmd, EntityArgument.getPlayer(cmd, "user"));
        }


        public static int execute(CommandContext<CommandSourceStack> cmd, ServerPlayer player) {
            int amt = IntegerArgumentType.getInteger(cmd, "amount");

            Bank accts = Bank.getOrCreate(cmd.getSource().getLevel());
            long balance = accts.getUserBalance(player);

            if (accts.hasUser(player)) {
                accts.setUserBalance(player, balance - (balance - (long) amt <= 0 ? amt = (int) balance : (long) amt));
                // Don't subtract more than they have; cap it at limiting to their balance.
                cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                    "bareessentials.bank.remove"), getCurrencySymbol(player.serverLevel()), amt, player.getDisplayName().getString(), getCurrencySymbol(player.serverLevel()), accts.getUserBalance(player)));
            } else {
                cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                    "bareessentials.bank.unable"), player.getDisplayName().getString()));
            }

            return Command.SINGLE_SUCCESS;
        }
    }

    public static class Clear {
        public static int executeOnSelf(CommandContext<CommandSourceStack> cmd) {
            return execute(cmd, cmd.getSource().getPlayer());
        }

        public static int executeOnOther(CommandContext<CommandSourceStack> cmd) throws CommandSyntaxException {
            return execute(cmd, EntityArgument.getPlayer(cmd, "user"));
        }


        public static int execute(CommandContext<CommandSourceStack> cmd, ServerPlayer player) {
            Bank accts = Bank.getOrCreate(cmd.getSource().getLevel());

            if (accts.hasUser(player)) {
                accts.setUserBalance(player, 0);
                cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                    "bareessentials.bank.clear"), player.getDisplayName().getString(), getCurrencySymbol(player.serverLevel())));
            } else {
                cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                    "bareessentials.bank.unable"), player.getDisplayName().getString()));
            }

            return Command.SINGLE_SUCCESS;
        }
    }

    public static class Top {
        private static List<Map.Entry<UUID, Long>> sortedBanks;

        public static void calculateTopBanks(MinecraftServer server) {
            var accs = Bank.getOrCreate(server.overworld()).accounts;

            sortedBanks = accs.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).toList();

        }

        public static void displayTopBanks(ServerPlayer player) {
            var packets = player.getServer().getScoreboard().getStartTrackingPackets(BareEssentials.BANK_ACCOUNT_SORTED_OBJECTIVE);
            for (Packet<?> packet : packets) {
                player.connection.send(packet);
            }
        }

        public static int execute(CommandContext<CommandSourceStack> cmd) {
            displayTopBanks(cmd.getSource().getPlayer());
            return Command.SINGLE_SUCCESS;
        }
    }

    public static class Value {
        public static int get(CommandContext<CommandSourceStack> cmd) {
            Market mkt = Market.getOrCreate(cmd.getSource().getLevel());
            ItemStack itm = cmd.getSource().getPlayer().getItemInHand(InteractionHand.MAIN_HAND);

            mkt.computeIfItemHasValue(BuiltInRegistries.ITEM.getKey(itm.getItem()),
                (item, value) -> cmd.getSource().sendSystemMessage(Component.translatable("bareessentials.bank.market.itemvalue", itm.getDisplayName(), CmdBank.getCurrencySymbol(cmd.getSource().getLevel()), value)),
                (item) -> cmd.getSource().sendSystemMessage(Component.translatable("bareessentials.bank.market.novalue", itm.getDisplayName()))
            );

            return Command.SINGLE_SUCCESS;
        }

        public static int set(CommandContext<CommandSourceStack> cmd) {
            long amount = cmd.getArgument("amount", Long.class);

            Market mkt = Market.getOrCreate(cmd.getSource().getLevel());
            ItemStack itm = cmd.getSource().getPlayer().getItemInHand(InteractionHand.MAIN_HAND);

            mkt.updateItemValue(BuiltInRegistries.ITEM.getKey(itm.getItem()), amount);

            cmd.getSource().sendSystemMessage(Component.translatable("bareessentials.bank.market.setvalue", itm.getDisplayName(), CmdBank.getCurrencySymbol(cmd.getSource().getLevel()), amount));

            return Command.SINGLE_SUCCESS;
        }

        public static int clear(CommandContext<CommandSourceStack> cmd) {
            Market mkt = Market.getOrCreate(cmd.getSource().getLevel());
            ItemStack itm = cmd.getSource().getPlayer().getItemInHand(InteractionHand.MAIN_HAND);

            mkt.updateItemValue(BuiltInRegistries.ITEM.getKey(itm.getItem()), 0);

            cmd.getSource().sendSystemMessage(Component.translatable("bareessentials.bank.market.clearedvalue", itm.getDisplayName()));

            return Command.SINGLE_SUCCESS;
        }
    }
}











