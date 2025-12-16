package uk.gemwire.bareessentials.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Market extends SavedData {
    public static final SavedDataType<Market> TYPE = new SavedDataType<>(
        "cooldowns",
        Market::new,
        Market.MarketData.CODEC.xmap(Market::new, Market::getData),
        DataFixTypes.SAVED_DATA_SCOREBOARD
    );

    public record MarketData (Map<Identifier, Long> itemValues) {
        public static final Market.MarketData EMPTY = new Market.MarketData(Map.of());
        public static final Codec<Market.MarketData> CODEC = RecordCodecBuilder.create(
            p_401439_ -> p_401439_.group(
                    Codec.unboundedMap(Identifier.CODEC, Codec.LONG)
                        .optionalFieldOf("market", Map.of())
                        .forGetter(Market.MarketData::itemValues)
                )
                .apply(p_401439_, Market.MarketData::new)
        );
    }

    Market.MarketData data;

    private Market() {
        this(Market.MarketData.EMPTY);
    }

    public Market(Market.MarketData p_455071_) {
        this.data = p_455071_;
    }

    public Market.MarketData getData() {
        return this.data;
    }

    public void setData(Market.MarketData p_454945_) {
        if (!p_454945_.equals(this.data)) {
            this.data = p_454945_;
            this.setDirty();
        }
    }

    public static Market getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public void updateItemValue(@NonNull Identifier item, long value) {
        data.itemValues.put(item, value);
    }

    public long getItemValue(Identifier item) {
        return data.itemValues.get(item);
    }

    public void computeIfItemHasValue(@NonNull Identifier itm, @Nullable BiConsumer<Identifier, Long> present, @Nullable Consumer<Identifier> absent) {
        if (data.itemValues.containsKey(itm)) {
            present.accept(itm, data.itemValues.get(itm));
        } else {
            absent.accept(itm);
        }
    }

}
