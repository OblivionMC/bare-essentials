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
package uk.gemwire.bareessentials;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.FixedFormat;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.gemwire.bareessentials.commands.BareCommands;
import uk.gemwire.bareessentials.commands.PermissionNodes;
import uk.gemwire.bareessentials.data.Bank;
import uk.gemwire.bareessentials.data.Homes;


/**
 * Provides the following commands
 *
 * For OP:
 *  setspawn
 *  editsign set/clear
 *  repair
 *  tp random/all/offline/toggle/here
 *  bank set/value set
 *  speed
 *  move top/up/down/bottom/forward
 *  editbook title/author/name/text
 *  more
 *  sleep
 *  warp set/remove
 *  break
 *  broadcast
 *  lightning
 *  invsee ender
 *  enchant
 *  fly
 *  vanish
 *  pos
 *  god
 *  infinite
 *  item lore/name
 *  burn
 *  xp get/set/give
 *  feed
 *  heal
 *  kittycannon
 *  beezooka
 *  tempban
 *  tempbanip
 *  unbanip
 *
 *
 * For players:
 *  bank get/value get/top/pay get/offer/toggle/accept/deny
 *  nick set/get
 *  whois
 *  condense
 *  mail read/clear/send/sendtemp
 *  home set/get/goto
 *  warp list/goto
 *  near
 *  tp back/deny/accept auto/ask cancel
 *  seen
 *  pos
 *  ping
 *  afk
 *  list
 *  r/reply
 *  playtime
 *  spawn
 */

@Mod("bareessentials")
public class BareEssentials {

    // Bank is enabled by default, but we'll set it off if we recognize an economy mod loading alongside us *cough* OblivionEconomy
    public static GameRules.Key<GameRules.BooleanValue> BANK_ENABLED = GameRules.register("be.bankEnabled", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    // Item market is disabled by default.
    public static GameRules.Key<GameRules.BooleanValue> MARKET_ENABLED = GameRules.register("e.marketEnabled", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));

    public static GameRules.Key<GameRules.IntegerValue> CURRENCY_SYMBOL = GameRules.register("be.currencySymbol", GameRules.Category.CHAT, GameRules.IntegerValue.create(0));
    public static GameRules.Key<GameRules.IntegerValue> STARTING_BALANCE = GameRules.register("be.bankStartingBalance", GameRules.Category.PLAYER, GameRules.IntegerValue.create(500));
    public static GameRules.Key<GameRules.IntegerValue> DAILY_INCOME = GameRules.register("be.bankDailyIncome", GameRules.Category.PLAYER, GameRules.IntegerValue.create(10));
    public static GameRules.Key<GameRules.IntegerValue> MAX_HOMES = GameRules.register("be.maxHomes", GameRules.Category.PLAYER, GameRules.IntegerValue.create(1));
    public static GameRules.Key<GameRules.IntegerValue> TPA_COST = GameRules.register("be.tpaCost", GameRules.Category.PLAYER, GameRules.IntegerValue.create(0));
    public static GameRules.Key<GameRules.IntegerValue> SPAWN_COST = GameRules.register("be.spawnCost", GameRules.Category.PLAYER, GameRules.IntegerValue.create(0));
    public static GameRules.Key<GameRules.IntegerValue> TPA_COOLDOWN = GameRules.register("be.tpaCooldown", GameRules.Category.PLAYER, GameRules.IntegerValue.create(15 * 20));
    public static GameRules.Key<GameRules.IntegerValue> SPAWN_COOLDOWN = GameRules.register("be.spawnCooldown", GameRules.Category.PLAYER, GameRules.IntegerValue.create(5 * 20 * 60));

    public static GameRules.Key<GameRules.BooleanValue> OP_OVERRIDES_COOLDOWN = GameRules.register("be.opOverridesCooldowns", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));

    public static ObjectiveCriteria BANK_ACCOUNT_VALUE = ObjectiveCriteria.registerCustom("bank_value");
    public static Objective BANK_ACCOUNT_SORTED_OBJECTIVE;

    public static Logger LOGGER = LogManager.getLogger(BareEssentials.class);


    public BareEssentials() {
        IEventBus forge = NeoForge.EVENT_BUS;
        forge.addListener(BareCommands::registerCommands);
        forge.addListener(PermissionNodes::registerPermissions);
    }

    @EventBusSubscriber(modid = "bareessentials", bus = EventBusSubscriber.Bus.MOD)
    static class Events {
        @SubscribeEvent
        public static void started(ServerStartedEvent e) {

            e.getServer().getScoreboard().addObjective("be_banks", BANK_ACCOUNT_VALUE, Component.literal("Bank Accounts"), ObjectiveCriteria.RenderType.INTEGER, true, null);

            // Load bank details into the static map.
            Bank accts = Bank.getOrCreate(e.getServer().overworld());
            LOGGER.info("Loaded " + accts.accounts.size() + " bank accounts.");


            Homes homes = Homes.getOrCreate(e.getServer().overworld());
            LOGGER.info("Loaded " + homes.homes.size() + " user homes.");
        }

        @SubscribeEvent
        public static void login(PlayerEvent.PlayerLoggedInEvent e) {
            if (e.getEntity().level().isClientSide) return;
            // Ensure the new player has a bank account so they receive income while offline.
            Bank accts = Bank.getOrCreate(e.getEntity().getServer().overworld());
            accts.getUserBalance((ServerPlayer) e.getEntity());
        }

        @SubscribeEvent
        public static void tick(ServerTickEvent e) {
            if (e.getServer().overworld().getDayTime() == 0)
                Bank.getOrCreate(e.getServer().overworld()).updateBalances(e.getServer());
        }
    }
}
