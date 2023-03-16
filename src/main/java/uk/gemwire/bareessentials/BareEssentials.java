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

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.gemwire.bareessentials.commands.BareCommands;
import uk.gemwire.bareessentials.data.Bank;

@Mod("bareessentials")
public class BareEssentials {
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
            Bank.getAccounts();
            LOGGER.info("Loaded " + Bank.ACCOUNTS.size() + " bank accounts.");
        }

        @SubscribeEvent
        public static void login(PlayerEvent.PlayerLoggedInEvent e) {
            if (!Bank.ACCOUNTS.containsKey(e.getEntity().getUUID())) {
                LOGGER.info("Generating empty bank account for " + e.getEntity().getDisplayName().getString());
                Bank.ACCOUNTS.put(e.getEntity().getUUID(), 0L);
            }
        }
    }
}
