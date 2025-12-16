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
package uk.gemwire.bareessentials.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.scores.ScoreHolder;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import uk.gemwire.bareessentials.BareEssentials;
import uk.gemwire.bareessentials.commands.CmdBank;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static uk.gemwire.bareessentials.BareEssentials.BANK_ACCOUNT_SORTED_OBJECTIVE;
import static uk.gemwire.bareessentials.BareEssentials.DAILY_INCOME;
import static uk.gemwire.bareessentials.BareEssentials.STARTING_BALANCE;

public class Bank extends SavedData {
    public static final SavedDataType<Bank> TYPE = new SavedDataType<>(
        "bank",
        Bank::new,
        Bank.BankData.CODEC.xmap(Bank::new, Bank::getData),
        DataFixTypes.SAVED_DATA_SCOREBOARD
    );

    public record BankData (Map<UUID, Long> accounts) {
        public static final Bank.BankData EMPTY = new Bank.BankData(new HashMap<>());
        public static final Codec<Bank.BankData> CODEC = RecordCodecBuilder.create(
            p_401439_ -> p_401439_.group(
                    Codec.unboundedMap(UUIDUtil.CODEC, Codec.LONG)
                        .optionalFieldOf("accounts", new HashMap<>())
                        .forGetter(Bank.BankData::accounts)
                )
                .apply(p_401439_, Bank.BankData::new)
        );
    }

    BankData data;

    private Bank() {
        this(Bank.BankData.EMPTY);
    }

    public Bank(Bank.BankData p_455071_) {
        this.data = p_455071_;
    }

    public Bank.BankData getData() {
        return this.data;
    }

    public void setData(Bank.BankData p_454945_) {
        if (!p_454945_.equals(this.data)) {
            this.data = p_454945_;
            this.setDirty();
        }
    }

    public static Bank getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    @Override
    public void setDirty() {
        super.setDirty();

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        for (var acc : data.accounts.entrySet()) {
            setPlayerBankScore(acc.getKey(), server);
        }
    }

    public long getUserBalance(ServerPlayer p) {
        if (!hasUser(p)) {
            data.accounts.put(p.getUUID(), (long) p.level().getServer().overworld().getGameRules().get(STARTING_BALANCE));
            this.setDirty();
            return data.accounts.get(p.getUUID());
        }

        for (var acc : data.accounts.entrySet()) {
            if (acc.getKey().equals(p.getUUID())) {
                return acc.getValue();
            }
        }

        return 0;
    }

    public boolean playerHasEnough(ServerPlayer p, long amount) {
        if (amount == 0) return true;

        if (!hasUser(p) || getUserBalance(p) < amount) {
            p.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault("bareessentials.balance.insufficient"), CmdBank.getCurrencySymbol(p.level()), amount));
            return false;
        }

        return true;
    }

    public boolean chargePlayer(ServerPlayer p, long amount) {
        if (amount == 0) return true;
        if (!playerHasEnough(p, amount)) return false;

        setUserBalance(p, getUserBalance(p) - amount);
        return true;
    }

    public void setUserBalance(ServerPlayer p, long b) {
        if (!hasUser(p)) {
            data.accounts.put(p.getUUID(), b);
            setDirty();
            return;
        }

        for (var acc : data.accounts.entrySet()) {
            if (acc.getKey().equals(p.getUUID())) {
                acc.setValue(b);
                setDirty();
            }
        }
    }

    public boolean hasUser(ServerPlayer player) {
        return data.accounts.containsKey(player.getUUID());
    }

    public void updateBalances(MinecraftServer s) {
        BareEssentials.LOGGER.info("Granting the " + s.overworld().getGameRules().get(DAILY_INCOME) + " daily income to all players.");
        for (var acct : data.accounts.entrySet()) {
            acct.setValue(acct.getValue() + s.overworld().getGameRules().get(DAILY_INCOME));
        }
        setDirty();
    }

    public void setPlayerBankScore(UUID player, MinecraftServer server) {
        GameProfile profile = server.services().profileResolver().fetchById(player).get();
        server.getScoreboard().getOrCreatePlayerScore(ScoreHolder.fromGameProfile(profile), BANK_ACCOUNT_SORTED_OBJECTIVE).set(Math.toIntExact(data.accounts.get(player)));
    }

}
