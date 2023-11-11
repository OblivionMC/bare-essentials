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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import uk.gemwire.bareessentials.BareEssentials;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static uk.gemwire.bareessentials.BareEssentials.DAILY_INCOME;
import static uk.gemwire.bareessentials.BareEssentials.STARTING_BALANCE;

public class Bank extends SavedData {

    // The list of all Accounts currently loaded
    public Map<UUID, Long> accounts;

    public Bank(Map<UUID, Long> accts) { accounts = accts; }
    public Bank() { accounts = new HashMap<>(); }

    @Override
    public @NotNull CompoundTag save(final @NotNull CompoundTag pCompoundTag) {
        CompoundTag tag = new CompoundTag();
        for (var acc : accounts.entrySet()) {
            tag.putLong(acc.getKey().toString(), acc.getValue());
        }

        pCompoundTag.put("accounts", tag);
        return pCompoundTag;
    }

    public static Bank load(CompoundTag tag) {
        CompoundTag accts = tag.getCompound("accounts");
        Map<UUID, Long> accounts = new HashMap<>();
        for (String key : accts.getAllKeys())
            accounts.put(UUID.fromString(key), ((LongTag) accts.get(key)).getAsLong());

        return new Bank(accounts);
    }

    public static Bank getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(Bank::new, Bank::load, null),
            "be_bank");
    }

    public long getUserBalance(ServerPlayer p) {
        if (!hasUser(p)) { accounts.put(p.getUUID(), (long) p.getServer().getGameRules().getInt(STARTING_BALANCE)); return accounts.get(p.getUUID()); }
        for (var acc : accounts.entrySet()) {
            if (acc.getKey().equals(p.getUUID())) {
                return acc.getValue();
            }
        }

        return 0;
    }

    public void setUserBalance(ServerPlayer p, long b) {
        if (!hasUser(p)) { accounts.put(p.getUUID(), (long) p.getServer().getGameRules().getInt(STARTING_BALANCE)); return; }
        for (var acc : accounts.entrySet()) {
            if (acc.getKey().equals(p.getUUID())) {
                acc.setValue(b);
                setDirty();
            }
        }
    }

    public boolean hasUser(ServerPlayer player) {
        return accounts.containsKey(player.getUUID());
    }

    public void updateBalances(MinecraftServer s) {
        BareEssentials.LOGGER.info("Granting the " + s.getGameRules().getInt(DAILY_INCOME) + " daily income to all players.");
        for (var acct : accounts.entrySet()) {
            acct.setValue(acct.getValue() + s.getGameRules().getInt(DAILY_INCOME));
        }
    }

}
