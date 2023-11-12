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

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Homes extends SavedData {

    // The list of all Homes currently loaded
    public Map<UUID, BlockPos> homes;

    public Homes(Map<UUID, BlockPos> homes) { this.homes = homes; }
    public Homes() { homes = new HashMap<>(); }

    private static final SavedData.Factory<Homes> homesFactory
        = new SavedData.Factory<>(Homes::new, Homes::load, null);

    @Override
    public @NotNull CompoundTag save(final @NotNull CompoundTag pCompoundTag) {
        CompoundTag tag = new CompoundTag();
        for (var acc : homes.entrySet()) {
            CompoundTag pos = new CompoundTag();
            pos.putInt("x", acc.getValue().getX());
            pos.putInt("y", acc.getValue().getY());
            pos.putInt("z", acc.getValue().getZ());
            tag.put(acc.getKey().toString(), pos);
        }

        pCompoundTag.put("homes", tag);
        return pCompoundTag;
    }

    public static Homes load(CompoundTag tag) {
        CompoundTag accts = tag.getCompound("homes");
        Map<UUID, BlockPos> homes = new HashMap<>();
        for (String key : accts.getAllKeys()) {
            CompoundTag pos = (CompoundTag) accts.get(key);
            BlockPos bp = new BlockPos(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"));
            homes.put(UUID.fromString(key), bp);
        }

        return new Homes(homes);
    }

    public static Homes getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(homesFactory, "be_homes");
    }

    public BlockPos getUserHome(ServerPlayer p) {
        for (var acc : homes.entrySet()) {
            if (acc.getKey().equals(p.getUUID())) {
                return acc.getValue();
            }
        }

        return null;
    }

    public void setUserHome(ServerPlayer p, BlockPos b) {
        if (!hasUserHome(p)) { homes.put(p.getUUID(), b); return; }

        for (var acc : homes.entrySet()) {
            if (acc.getKey().equals(p.getUUID())) {
                acc.setValue(b);
                setDirty();
            }
        }
    }

    public boolean hasUserHome(ServerPlayer player) {
        return homes.containsKey(player.getUUID());
    }

}
