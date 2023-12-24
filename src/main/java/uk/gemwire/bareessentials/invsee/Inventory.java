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

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.NetworkHooks;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import uk.gemwire.bareessentials.BareEssentials;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public class Inventory {

    private static final Field pds = ObfuscationReflectionHelper.findField(MinecraftServer.class, "playerDataStorage");
    static { pds.setAccessible(true); }
    private static final Field pbn = ObfuscationReflectionHelper.findField(GameProfileCache.class, "profilesByName");
    static { pbn.setAccessible(true); }
    private static final Field pDir = ObfuscationReflectionHelper.findField(PlayerDataStorage.class, "playerDir");
    static { pDir.setAccessible(true); }

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_USERS = (context, builder) -> {
        try {
            return SharedSuggestionProvider.suggest(
                ((Map<String, Object>) pbn.get((context.getSource().getServer().getProfileCache()))).keySet(), builder);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    };

    public static int openInventoryOf(ServerPlayer inspector, String username) {

        BareEssentials.LOGGER.info(inspector.getDisplayName().getString() + " is opening the inventory of " + username);
        var optionalPlayer = ServerLifecycleHooks.getCurrentServer().getProfileCache().get(username);
        BareEssentials.LOGGER.info("Opening inventory for " + username + ", user " + (optionalPlayer.isPresent() ? "is present." : "is empty."));
        optionalPlayer.ifPresent(gameProfile -> NetworkHooks.openScreen(inspector, new SimpleMenuProvider((a, b, c) -> {
            try {
                return MiniInventoryMenu.fourRows(a, b, gameProfile.getId());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }, Component.literal("Inventory"))));

        return Command.SINGLE_SUCCESS;
    }

    public static class MiniInventoryMenu extends ChestMenu {
        UUID id;
        net.minecraft.world.entity.player.Inventory targetInv;

        public MiniInventoryMenu(MenuType<?> pType, int pContainerId, net.minecraft.world.entity.player.Inventory pPlayerInventory, Container pContainer, int pRows) {
            super(pType, pContainerId, pPlayerInventory, pContainer, pRows);
        }

        private MiniInventoryMenu(MenuType<?> pType, int pContainerId, net.minecraft.world.entity.player.Inventory pPlayerInventory, int pRows, UUID id) throws IllegalAccessException {
            this(pType, pContainerId, pPlayerInventory, new SimpleContainer(9 * pRows), pRows);

            this.id = id;

            ServerPlayer sp = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(id);
            if (sp != null) {
                // Player online
                BareEssentials.LOGGER.info("Player online; synchronizing inventory");
                targetInv = sp.getInventory();
            } else {
                // Player offline
                BareEssentials.LOGGER.info("Player offline; reading inventory from NBT");
                targetInv = new OfflinePlayerInventory(id);
            }

            // Copy data to the menu
            for (int i = 0; i < getContainer().getContainerSize(); i++) {
                getContainer().setItem(i, targetInv.getItem(i));
            }
        }

        public static MiniInventoryMenu fourRows(int pContainerId, net.minecraft.world.entity.player.Inventory pPlayerInventory, UUID id) throws IllegalAccessException {
            return new MiniInventoryMenu(MenuType.GENERIC_9x4, pContainerId, pPlayerInventory, 4, id);
        }

        @Override
        public void removed(final Player pPlayer) {
            super.removed(pPlayer);

            if (targetInv instanceof OfflinePlayerInventory opi) {
                CompoundTag data = opi.getUserData();

                ListTag listtag = data.getList("Inventory", 10);

                // Save to compound
                for (int i = 0; i < 9 * 4; i++) {
                    CompoundTag compoundtag = listtag.getCompound(i);
                    int j = compoundtag.getByte("Slot") & 255;
                    if (i == j) {
                        if (listtag.size() > i)
                            listtag.set(i, getContainer().getItem(j).serializeNBT());
                        else
                            listtag.add(getContainer().getItem(j).serializeNBT());
                    }
                }

                data.put("Inventory", listtag);

                try {
                    File playerDataFolder = getPlayerDataFolderFor(id);
                    File file1 = File.createTempFile(id + "-", ".dat", playerDataFolder);
                    NbtIo.writeCompressed(data, file1);
                    File file2 = new File(playerDataFolder, id + ".dat");
                    File file3 = new File(playerDataFolder, id + ".dat_old");
                    Util.safeReplaceFile(file2, file1, file3);
                } catch (IOException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            } else {
                // Copy data back to the owner of the inventory we're reading.
                for (int i = 0; i < getContainer().getContainerSize(); i++) {
                    targetInv.setItem(i, getContainer().getItem(i));
                }
            }
        }
        private static File getPlayerDataFolderFor(UUID id) throws IllegalAccessException {
            pds.setAccessible(true);
            PlayerDataStorage playerData = (PlayerDataStorage) pds.get(ServerLifecycleHooks.getCurrentServer());
            pDir.setAccessible(true);
            return (File) pDir.get(playerData);
        }
    }

    public static class OfflinePlayerInventory extends net.minecraft.world.entity.player.Inventory {
        private CompoundTag userData;

        public OfflinePlayerInventory(final UUID target) {
            super(new FakePlayer(ServerLifecycleHooks.getCurrentServer().overworld(), new GameProfile(UUID.fromString("bc27afd7-6889-4811-97c9-135ee46cdabc"), "invsee")));

            try {
                File playerDataFolder = MiniInventoryMenu.getPlayerDataFolderFor(target);
                File datafile = new File(playerDataFolder, target + ".dat");
                BareEssentials.LOGGER.info("Trying to read data for player " + ServerLifecycleHooks.getCurrentServer().getProfileCache().get(target).get().getName() + " from " + datafile.getPath());
                if (datafile.exists() && datafile.isFile()) {
                    // Player offline but has data
                    try {
                        userData = NbtIo.readCompressed(datafile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    ListTag listtag = userData.getList("Inventory", 10);

                    for (int i = 0; i < listtag.size(); ++i) {
                        CompoundTag compoundtag = listtag.getCompound(i);
                        int j = compoundtag.getByte("Slot") & 255;
                        ItemStack itemstack = ItemStack.of(compoundtag);
                        if (!itemstack.isEmpty() && j <= getContainerSize()) {
                            setItem(j, itemstack);
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        public CompoundTag getUserData() { return userData; }
    }
}
