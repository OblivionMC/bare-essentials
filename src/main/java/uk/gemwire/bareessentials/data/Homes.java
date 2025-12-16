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

import com.mojang.serialization.Codec;
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
import java.util.UUID;

public class Homes extends SavedData {
    public static final SavedDataType<Homes> TYPE = new SavedDataType<>(
        "homes",
        Homes::new,
        Homes.HomeData.CODEC.xmap(Homes::new, Homes::getData),
        DataFixTypes.SAVED_DATA_SCOREBOARD
    );

    public record HomeData (Map<UUID, BlockPos> homes) {
        public static final Homes.HomeData EMPTY = new Homes.HomeData(Map.of());
        public static final Codec<Homes.HomeData> CODEC = RecordCodecBuilder.create(
            p_401439_ -> p_401439_.group(
                    Codec.unboundedMap(UUIDUtil.CODEC, BlockPos.CODEC)
                        .optionalFieldOf("home", Map.of())
                        .forGetter(Homes.HomeData::homes)
                )
                .apply(p_401439_, Homes.HomeData::new)
        );
    }

    Homes.HomeData data;

    private Homes() {
        this(Homes.HomeData.EMPTY);
    }

    public Homes(Homes.HomeData p_455071_) {
        this.data = p_455071_;
    }

    public Homes.HomeData getData() {
        return this.data;
    }

    public void setData(Homes.HomeData p_454945_) {
        if (!p_454945_.equals(this.data)) {
            this.data = p_454945_;
            this.setDirty();
        }
    }

    public static Homes getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public BlockPos getUserHome(ServerPlayer p) {
        for (var acc : data.homes.entrySet()) {
            if (acc.getKey().equals(p.getUUID())) {
                return acc.getValue();
            }
        }

        return null;
    }

    public void setUserHome(ServerPlayer p, BlockPos b) {
        if (!hasUserHome(p)) {
            data.homes.put(p.getUUID(), b);
            setDirty();
            return;
        }

        for (var acc : data.homes.entrySet()) {
            if (acc.getKey().equals(p.getUUID())) {
                acc.setValue(b);
                setDirty();
            }
        }
    }

    public boolean hasUserHome(ServerPlayer player) {
        return data.homes.containsKey(player.getUUID());
    }

}
