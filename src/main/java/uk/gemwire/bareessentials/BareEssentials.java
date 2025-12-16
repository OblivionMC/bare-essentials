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
package uk.gemwire.bareessentials;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRules;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.gemwire.bareessentials.commands.BareCommands;
import uk.gemwire.bareessentials.data.Bank;
import uk.gemwire.bareessentials.data.Homes;

@Mod("bareessentials")
public class BareEssentials {

    public BareEssentials() {
        IEventBus forge = NeoForge.EVENT_BUS;
        forge.addListener(BareCommands::registerCommands);
    }

    public static GameRule<Integer> CURRENCY_SYMBOL;
    public static GameRule<Integer> STARTING_BALANCE;
    public static GameRule<Integer> DAILY_INCOME;
    public static GameRule<Integer> MAX_HOMES;
    public static GameRule<Integer> TPA_COST;
    public static GameRule<Integer> SPAWN_COST;
    public static GameRule<Integer> TPA_COOLDOWN;
    public static GameRule<Integer> SPAWN_COOLDOWN;

    public static GameRule<Boolean> OP_OVERRIDES_COOLDOWN;

    public static Logger LOGGER = LogManager.getLogger(BareEssentials.class);

    @EventBusSubscriber(modid="bareessentials")
    static class Events {
        @SubscribeEvent
        public static void started(ServerStartedEvent e) {
            // Load bank details into the static map.
            Bank accts = Bank.getOrCreate(e.getServer().overworld());
            LOGGER.info("Loaded " + accts.getData().accounts().size() + " bank accounts.");
            Homes homes = Homes.getOrCreate(e.getServer().overworld());
            LOGGER.info("Loaded " + homes.getData().homes().size() + " user homes.");
        }

        @SubscribeEvent
        public static void login(PlayerEvent.PlayerLoggedInEvent e) {
            if (e.getEntity().level().isClientSide()) return;
            // Ensure the new player has a bank account so they receive income while offline.
            Bank accts = Bank.getOrCreate(e.getEntity().level().getServer().overworld());
            accts.getUserBalance((ServerPlayer) e.getEntity());
        }

        @SubscribeEvent
        public static void tick(ServerTickEvent.Post e) {
            if (e.getServer().overworld().getDayTime() == 0)
                Bank.getOrCreate(e.getServer().overworld()).updateBalances(e.getServer());
        }

        @SubscribeEvent
        public static void register(RegisterEvent e) {
            if (e.getRegistry() == BuiltInRegistries.GAME_RULE) {
                CURRENCY_SYMBOL = GameRules.registerInteger("be.currency_symbol", GameRuleCategory.CHAT, 0, 0);
                STARTING_BALANCE = GameRules.registerInteger("be.bank_starting_balance", GameRuleCategory.PLAYER, 500, 0);
                DAILY_INCOME = GameRules.registerInteger("be.bank_daily_income", GameRuleCategory.PLAYER, 10, 0);
                MAX_HOMES = GameRules.registerInteger("be.max_homes", GameRuleCategory.PLAYER, 1, 0);
                TPA_COST = GameRules.registerInteger("be.tpa_cost", GameRuleCategory.PLAYER, 0, 0);
                SPAWN_COST = GameRules.registerInteger("be.spawn_cost", GameRuleCategory.PLAYER, 0, 0);
                TPA_COOLDOWN = GameRules.registerInteger("be.tpa_cooldown", GameRuleCategory.PLAYER, 15 * 20, 0);
                SPAWN_COOLDOWN = GameRules.registerInteger("be.spawn_cooldown", GameRuleCategory.PLAYER, 5 * 20 * 60, 0);
                OP_OVERRIDES_COOLDOWN = GameRules.registerBoolean("be.op_overrides_cooldowns", GameRuleCategory.PLAYER, false);
            }
        }
    }
}
