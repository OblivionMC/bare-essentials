package uk.gemwire.bareessentials.data;

import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import uk.gemwire.bareessentials.BareEssentials;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cooldowns extends SavedData {

    // The list of all users with pending cooldowns
    // User -> { Feature -> Game Time }
    public Map<UUID, Map<String, Long>> cooldowns;

    public Cooldowns(Map<UUID, Map<String, Long>> cooldowns) { this.cooldowns = cooldowns; }
    public Cooldowns() { cooldowns = new HashMap<>(); }

    private static final SavedData.Factory<Cooldowns> cooldownsFactory
        = new SavedData.Factory<>(Cooldowns::new, Cooldowns::load, null);

    @Override
    public @NotNull CompoundTag save(final @NotNull CompoundTag pCompoundTag) {
        // Do not save or load cooldowns, they only exist temporarily.
        return new CompoundTag();
    }

    public static Cooldowns load(CompoundTag tag) {
        // Do not save or load cooldowns, they only exist temporarily.
        return new Cooldowns();
    }

    public static Cooldowns getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(cooldownsFactory, "be_cooldowns");
    }

    public long getCooldownFor(ServerPlayer p, String feature) {
        return cooldowns.containsKey(p.getUUID()) ?
                    cooldowns.get(p.getUUID()).getOrDefault(feature, 0L)
              : 0L;
    }

    public long getRemainingTimeFor(ServerPlayer p, String feature) {
        return cooldowns.get(p.getUUID()).get(feature) - p.level().getGameTime();
    }

    public void setCooldownFor(ServerPlayer p, String feature, long gametime) {
        if (!hasPendingCooldown(p)) {
            Map<String, Long> data = new HashMap<>();
            data.put(feature, gametime);
            cooldowns.put(p.getUUID(), data);
            return;
        }

        if (!hasPendingCooldownFor(p, feature)) {
            cooldowns.get(p.getUUID()).put(feature, gametime);
            return;
        }

        for (var acc : cooldowns.entrySet()) {
            if (acc.getKey().equals(p.getUUID())) {
                for (var ftr : acc.getValue().entrySet()) {
                    if (ftr.equals(feature)) {
                        ftr.setValue(gametime);
                    }
                }
                setDirty();
            }
        }
    }

    public boolean hasPendingCooldown(ServerPlayer player) {
        if (player.hasPermissions(Commands.LEVEL_ADMINS) && player.level().getGameRules().getBoolean(BareEssentials.OP_OVERRIDES_COOLDOWN)) return false;
        return cooldowns.containsKey(player.getUUID());
    }

    public boolean hasPendingCooldownFor(ServerPlayer player, String feature) {
        return cooldowns.get(player.getUUID()).containsKey(feature);
    }

    public boolean isCooldownExpired(ServerPlayer player, String feature) {
        if (!hasPendingCooldown(player) || !hasPendingCooldownFor(player, feature)) return true;
        return getCooldownFor(player, feature) < player.level().getGameTime();
    }

}
