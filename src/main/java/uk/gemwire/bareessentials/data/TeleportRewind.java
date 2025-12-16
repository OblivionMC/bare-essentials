package uk.gemwire.bareessentials.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.function.Function;

public class TeleportRewind extends SavedData {
    public static final SavedDataType<TeleportRewind> TYPE = new SavedDataType<>(
        "rewind",
        TeleportRewind::new,
        TeleportRewind.TeleportRewindData.CODEC.xmap(TeleportRewind::new, TeleportRewind::getData),
        DataFixTypes.SAVED_DATA_SCOREBOARD
    );

    public record TeleportRewindData (Map<UUID, Stack<TeleportEvent>> playerBacks) {
        public static final TeleportRewind.TeleportRewindData EMPTY = new TeleportRewind.TeleportRewindData(new HashMap<>());
        public static final Codec<TeleportRewind.TeleportRewindData> CODEC = RecordCodecBuilder.create(
            p_401439_ -> p_401439_.group(
                    Codec.unboundedMap(UUIDUtil.STRING_CODEC, TeleportEvent.CODEC.listOf().xmap(l -> { Stack<TeleportEvent> s = new Stack<>(); s.addAll(l); return s; }, Function.identity()))
                        .optionalFieldOf("backs", new HashMap<>())
                        .forGetter(TeleportRewindData::playerBacks)
                )
                .apply(p_401439_, TeleportRewind.TeleportRewindData::new)
        );
    }

    TeleportRewind.TeleportRewindData data;

    private TeleportRewind() {
        this(TeleportRewind.TeleportRewindData.EMPTY);
    }

    public TeleportRewind(TeleportRewind.TeleportRewindData p_455071_) {
        this.data = p_455071_;
    }

    public TeleportRewind.TeleportRewindData getData() {
        return this.data;
    }

    public void setData(TeleportRewind.TeleportRewindData p_454945_) {
        if (!p_454945_.equals(this.data)) {
            this.data = p_454945_;
            this.setDirty();
        }
    }

    public static TeleportRewind getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public Stack<TeleportEvent> getBacksFor(ServerPlayer player) {
        if (!data.playerBacks.containsKey(player.getUUID())) {
            data.playerBacks.put(player.getUUID(), new Stack<>());
            setDirty();
        }

        return data.playerBacks.get(player.getUUID());
    }

    /**
     * Teleport Events are a record of Type,Source,Destination
     */
    public enum EventType {
        DEATH("DEATH"),         // Death moves you to spawn / bed
        PORTAL("PORTAL"),       // End/Nether/Modded Portal; onTravelToDimension via Entity#changeDimension
        COMMAND("COMMAND");     // Entity#moveto, via /tp. /tpa

        public static MapCodec<EventType> CODEC = Codec.STRING.fieldOf("name").xmap(EventType::fromString, EventType::getName);

        String name;
        EventType(String name) { this.name = name; }

        public String getName() { return name; }

        public static EventType fromString(String name) {
            return switch (name) {
                case "DEATH" -> DEATH;
                case "PORTAL" -> PORTAL;
                case "COMMAND" -> COMMAND;
                default -> throw new IllegalStateException("Unexpected value: " + name);
            };
        }
    }

    public record TeleportEvent(EventType type, BlockPos origin, BlockPos destination) {
        public static Codec<TeleportEvent> CODEC = RecordCodecBuilder.create(
            i -> i.group(
                EventType.CODEC.fieldOf("type").forGetter(TeleportEvent::type),
                BlockPos.CODEC.fieldOf("origin").forGetter(TeleportEvent::origin),
                BlockPos.CODEC.fieldOf("destination").forGetter(TeleportEvent::destination)
            ).apply(i, TeleportEvent::new)
        );
    }
}
