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
