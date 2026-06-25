package uk.gemwire.bareessentials.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.Commands;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import uk.gemwire.bareessentials.BareEssentials;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cooldowns extends SavedData {
    public static final SavedDataType<Cooldowns> TYPE = new SavedDataType<>(
        Identifier.parse("cooldowns"),
        Cooldowns::new,
        Cooldowns.CooldownData.CODEC.xmap(Cooldowns::new, Cooldowns::getData),
        DataFixTypes.SAVED_DATA_SCOREBOARD
    );

    public record CooldownData (Map<UUID, Map<String, Long>> cooldowns) {
        public static final Cooldowns.CooldownData EMPTY = new Cooldowns.CooldownData(new HashMap<>());
        public static final Codec<Cooldowns.CooldownData> CODEC = RecordCodecBuilder.create(
            p_401439_ -> p_401439_.group(
                    Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.unboundedMap(Codec.STRING, Codec.LONG))
                        .optionalFieldOf("cooldowns", new HashMap<>())
                        .forGetter(Cooldowns.CooldownData::cooldowns)
                )
                .apply(p_401439_, Cooldowns.CooldownData::new)
        );
    }

    Cooldowns.CooldownData data;

    private Cooldowns() {
        this(Cooldowns.CooldownData.EMPTY);
    }

    public Cooldowns(Cooldowns.CooldownData p_455071_) {
        this.data = p_455071_;
    }

    public Cooldowns.CooldownData getData() {
        return this.data;
    }

    public void setData(Cooldowns.CooldownData p_454945_) {
        if (!p_454945_.equals(this.data)) {
            this.data = p_454945_;
            this.setDirty();
        }
    }

    public static Cooldowns getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public long getCooldownFor(ServerPlayer p, String feature) {
        return data.cooldowns.containsKey(p.getUUID()) ?
               data.cooldowns.get(p.getUUID()).getOrDefault(feature, 0L)
               : 0L;
    }

    public long getRemainingTimeFor(ServerPlayer p, String feature) {
        return data.cooldowns.get(p.getUUID()).get(feature) - p.level().getGameTime();
    }

    public void setCooldownFor(ServerPlayer p, String feature, long gametime) {
        if (!hasPendingCooldown(p)) {
            Map<String, Long> d = new HashMap<>();
            d.put(feature, gametime);
            data.cooldowns.put(p.getUUID(), d);
            setDirty();
            return;
        }

        if (!hasPendingCooldownFor(p, feature)) {
            data.cooldowns.get(p.getUUID()).put(feature, gametime);
            return;
        }

        for (var acc : data.cooldowns.entrySet()) {
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
        if (Commands.hasPermission(Commands.LEVEL_ADMINS).test().check(player.permissions()) && player.level().getGameRules().get(BareEssentials.OP_OVERRIDES_COOLDOWN)) return false;
        return data.cooldowns.containsKey(player.getUUID());
    }

    public boolean hasPendingCooldownFor(ServerPlayer player, String feature) {
        return data.cooldowns.get(player.getUUID()).containsKey(feature);
    }

    public boolean isCooldownExpired(ServerPlayer player, String feature) {
        if (!hasPendingCooldown(player) || !hasPendingCooldownFor(player, feature)) return true;
        return getCooldownFor(player, feature) < player.level().getGameTime();
    }

}
