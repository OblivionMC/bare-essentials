package uk.gemwire.bareessentials.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        return level.getDataStorage().computeIfAbsent(Bank::load, Bank::new, "be_bank");
    }

    public long getUserBalance(ServerPlayer p) {
        for (var acc : accounts.entrySet()) {
            if (acc.getKey().equals(p.getUUID())) {
                return acc.getValue();
            }
        }

        return 0;
    }

    public void setUserBalance(ServerPlayer p, long b) {
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

}
