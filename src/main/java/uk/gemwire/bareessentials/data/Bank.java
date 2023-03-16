package uk.gemwire.bareessentials.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Bank {

    // A Map relation of UUID->Bank Balance
    public record Currency(UUID player, long amount) {}

    // The list of all Accounts currently loaded
    public static final Map<UUID, Long> ACCOUNTS = new HashMap<>();

    // A class used to persist bank details across world saves.
    public static class BankSavedData extends SavedData {
        @Override
        public @NotNull CompoundTag save(final @NotNull CompoundTag pCompoundTag) {
            CompoundTag accounts = new CompoundTag();
            for (var acc : ACCOUNTS.entrySet()) {
                accounts.putLong(acc.getKey().toString(), acc.getValue());
            }

            pCompoundTag.put("accounts", accounts);
            return pCompoundTag;
        }

        public static BankSavedData load(CompoundTag tag) {
            CompoundTag accounts = tag.getCompound("accounts");
            for (String key : accounts.getAllKeys())
                ACCOUNTS.put(UUID.fromString(key), ((LongTag) accounts.get(key)).getAsLong());

            return new BankSavedData();
        }

        public BankSavedData() {}
    }

    public static BankSavedData getAccounts() {
        return ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage().computeIfAbsent(BankSavedData::load, BankSavedData::new, "be_bank");
    }

    public static long getUserBalance(ServerPlayer p) {
        for (var acc : ACCOUNTS.entrySet()) {
            if (acc.getKey().equals(p.getUUID())) {
                return acc.getValue();
            }
        }

        return 0;
    }

    public static void setUserBalance(ServerPlayer p, long b) {
        for (var acc : ACCOUNTS.entrySet()) {
            if (acc.getKey().equals(p.getUUID())) {
                acc.setValue(b);
            }
        }
    }

}
