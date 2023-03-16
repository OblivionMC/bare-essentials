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
package uk.gemwire.bareessentials;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.gemwire.bareessentials.commands.BareCommands;
import uk.gemwire.bareessentials.data.Bank;
import uk.gemwire.bareessentials.data.Homes;

@Mod("bareessentials")
public class BareEssentials {

    public static GameRules.Key<GameRules.IntegerValue> STARTING_BALANCE = GameRules.register("bankStartingBalance", GameRules.Category.PLAYER, GameRules.IntegerValue.create(500));
    public static GameRules.Key<GameRules.IntegerValue> DAILY_INCOME = GameRules.register("bankDailyIncome", GameRules.Category.PLAYER, GameRules.IntegerValue.create(10));
    public static Logger LOGGER = LogManager.getLogger(BareEssentials.class);

    public BareEssentials() {
        IEventBus forge = MinecraftForge.EVENT_BUS;
        forge.addListener(BareCommands::registerCommands);
    }

    @Mod.EventBusSubscriber(modid="bareessentials", bus= Mod.EventBusSubscriber.Bus.FORGE)
    static class Events {
        @SubscribeEvent
        public static void started(ServerStartedEvent e) {
            // Load bank details into the static map.
            Bank accts = Bank.getOrCreate(e.getServer().overworld());
            LOGGER.info("Loaded " + accts.accounts.size() + " bank accounts.");
            Homes homes = Homes.getOrCreate(e.getServer().overworld());
            LOGGER.info("Loaded " + homes.homes.size() + " user homes.");
        }

        @SubscribeEvent
        public static void login(PlayerEvent.PlayerLoggedInEvent e) {
            if (e.getEntity().getLevel().isClientSide) return;
            // Ensure the new player has a bank account so they receive income while offline.
            Bank accts = Bank.getOrCreate(e.getEntity().getServer().overworld());
            accts.getUserBalance((ServerPlayer) e.getEntity());
        }

        @SubscribeEvent
        public static void tick(TickEvent.ServerTickEvent e) {
            if (e.getServer().overworld().getDayTime() == 0)
                Bank.getOrCreate(e.getServer().overworld()).updateBalances(e.getServer());
        }
    }
}
