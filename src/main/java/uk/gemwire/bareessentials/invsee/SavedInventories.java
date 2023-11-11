package uk.gemwire.bareessentials.invsee;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SavedInventories extends SavedData {

    // The list of all Inventories currently loaded
    public Map<UUID, List<CompoundTag>> invs;

    public SavedInventories(Map<UUID, List<CompoundTag>> invs) { this.invs = invs; }
    public SavedInventories() { invs = new HashMap<>(); }

    @Override
    public @NotNull CompoundTag save(final @NotNull CompoundTag pCompoundTag) {
        CompoundTag tag = new CompoundTag();
        for (var acc : invs.entrySet()) {
            ListTag invs = new ListTag();
            invs.addAll(acc.getValue());

            tag.put(acc.getKey().toString(), invs);
        }

        pCompoundTag.put("invs", tag);
        return pCompoundTag;
    }

    public static SavedInventories load(CompoundTag tag) {
        CompoundTag invs = tag.getCompound("invs");

        Map<UUID, List<CompoundTag>> saves = new HashMap<>();
        for (String key : invs.getAllKeys()) {
            ListTag list = (ListTag) invs.get(key);
            List<CompoundTag> tags = new ArrayList<>();
            list.forEach(t -> tags.add((CompoundTag) t));
            saves.put(UUID.fromString(key), tags);
        }

        return new SavedInventories(saves);
    }


    public static SavedInventories getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(SavedInventories::new,
            SavedInventories::load, null), "be_invs");
    }
}
