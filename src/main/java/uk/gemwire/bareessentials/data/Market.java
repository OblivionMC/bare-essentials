package uk.gemwire.bareessentials.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Market extends SavedData {

    private Map<ResourceLocation, Long> itemValues;

    public Market() { itemValues = new HashMap<>(); }

    public Market(Map<ResourceLocation, Long> values) {
        itemValues = values;
    }

    private static final SavedData.Factory<Market> marketFactory
        = new SavedData.Factory<>(Market::new, Market::load, null);


    public static Market getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(marketFactory, "be_item_market");
    }

    @Override
    public CompoundTag save(final CompoundTag tag, final HolderLookup.Provider registries) {
        CompoundTag vals = new CompoundTag();
        for (var i : itemValues.entrySet()) {
            vals.putLong(i.getKey().toString(), i.getValue());
        }

        tag.put("itemValues", vals);
        return tag;
    }

    public static Market load(CompoundTag tag, final HolderLookup.Provider prov) {
        CompoundTag vals = tag.getCompound("itemValues");
        Map<ResourceLocation, Long> values = new HashMap<>();
        for (String key : vals.getAllKeys()) {
            long value = vals.getLong(key);
            values.put(ResourceLocation.parse(key), value);
        }

        return new Market(values);
    }

    public void updateItemValue(ResourceLocation item, long value) {
        itemValues.put(item, value);
    }

    public long getItemValue(ResourceLocation item) {
        return itemValues.get(item);
    }

    public void computeIfItemHasValue(ResourceLocation itm, @Nullable BiConsumer<ResourceLocation, Long> present, @Nullable Consumer<ResourceLocation> absent) {
        if (itemValues.containsKey(itm)) {
            present.accept(itm, itemValues.get(itm));
        } else {
            absent.accept(itm);
        }
    }

}
