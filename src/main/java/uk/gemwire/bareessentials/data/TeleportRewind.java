package uk.gemwire.bareessentials.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

public class TeleportRewind extends SavedData {

    // Every player has a list of things they can undo.
    public Map<UUID, Stack<TeleportEvent>> playerBacks = new HashMap<>();

    private static final Factory<TeleportRewind> rewindFactory
        = new Factory<>(TeleportRewind::new, TeleportRewind::load, null);

    public TeleportRewind() {
    }

    public TeleportRewind(Map<UUID, TeleportEvent> firstBacks) {
        for (Map.Entry<UUID, TeleportEvent> tele : firstBacks.entrySet()) {
            playerBacks.put(tele.getKey(), new Stack<>());
            playerBacks.get(tele.getKey()).add(tele.getValue());
        }
    }

    @Override
    public CompoundTag save(final CompoundTag tag, final HolderLookup.Provider registries) {
        CompoundTag map = new CompoundTag();
        for (var tele : playerBacks.entrySet()) {
            TeleportEvent event = tele.getValue().pop();
            CompoundTag entry = new CompoundTag();
            CompoundTag source = new CompoundTag();
            source.putInt("x", event.origin.getX());
            source.putInt("y", event.origin.getX());
            source.putInt("z", event.origin.getX());
            CompoundTag destination = new CompoundTag();
            destination.putInt("x", event.destination.getX());
            destination.putInt("y", event.destination.getX());
            destination.putInt("z", event.destination.getX());
            entry.put("source", source);
            entry.put("destination", destination);
            entry.putString("type", event.type.getName());
            map.put(tele.getKey().toString(), entry);
        }
        tag.put("be_rewinds", map);
        return tag;
    }

    public static TeleportRewind load(CompoundTag tag, final HolderLookup.Provider prov) {
        CompoundTag map = tag.getCompound("be_rewinds");
        Map<UUID, TeleportEvent> teleports = new HashMap<>();
        for (String key : map.getAllKeys()) {
            UUID player = UUID.fromString(key);
            CompoundTag entry = (CompoundTag) map.get(key);
            CompoundTag source = (CompoundTag) entry.get("source");
            BlockPos sourcePos = new BlockPos(source.getInt("x"), source.getInt("y"), source.getInt("z"));
            CompoundTag destination = (CompoundTag) entry.get("destination");
            BlockPos destPos = new BlockPos(destination.getInt("x"), destination.getInt("y"), destination.getInt("z"));
            TeleportEvent evt = new TeleportEvent(EventType.fromString(entry.getString("type")), sourcePos, destPos);

            teleports.put(player,evt);
        }

        return new TeleportRewind(teleports);
    }

    public static TeleportRewind getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(rewindFactory, "be_back");
    }


    /**
     * Teleport Events are a record of Type,Source,Destination
     */
    public enum EventType {
        DEATH("DEATH"),         // Death moves you to spawn / bed
        PORTAL("PORTAL"),       // End/Nether/Modded Portal; onTravelToDimension via Entity#changeDimension
        COMMAND("COMMAND");     // Entity#moveto, via /tp. /tpa

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
    }
}
