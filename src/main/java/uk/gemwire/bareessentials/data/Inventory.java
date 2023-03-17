package uk.gemwire.bareessentials.data;

import com.mojang.brigadier.Command;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Inventory {

    public static int openInventoryOf(ServerPlayer inspector, UUID player) {
        List<ItemStack> inv = new ArrayList<>();
        CompoundTag pData = new CompoundTag();

        ServerPlayer sp = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(player);
        if (sp != null) {
            // Player online
            net.minecraft.world.entity.player.Inventory pInv = sp.getInventory();
            pInv.items.forEach(i -> inv.add(i.copy()));
            pData = sp.serializeNBT();
        } else {
            File datafile = ServerLifecycleHooks.getCurrentServer().getFile("playerdata/" + player + ".dat");
            if (datafile.exists() && datafile.isFile()) {
                // Player offline but has data
                try {
                    pData = NbtIo.readCompressed(datafile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ListTag listtag = pData.getList("Inventory", 10);

                for(int i = 0; i < listtag.size(); ++i) {
                    CompoundTag compoundtag = listtag.getCompound(i);
                    int j = compoundtag.getByte("Slot") & 255;
                    ItemStack itemstack = ItemStack.of(compoundtag);
                    if (!itemstack.isEmpty()) {
                        if (j >= 0) {
                            inv.set(j, itemstack);
                        }
                    }
                }
            }
        }

        final CompoundTag finalPData = pData;
        NetworkHooks.openScreen(inspector, new SimpleMenuProvider((a, b, c) -> MiniInventoryMenu.fourRows(a, b, inv, finalPData, player), Component.literal("Inventory")));

        return Command.SINGLE_SUCCESS;
    }


    public static class MiniInventoryMenu extends ChestMenu {

        private CompoundTag userData;
        private List<ItemStack> otherInv;
        private UUID id;


        public MiniInventoryMenu(MenuType<?> pType, int pContainerId, net.minecraft.world.entity.player.Inventory pPlayerInventory, Container pContainer, int pRows) {
            super(pType, pContainerId, pPlayerInventory, pContainer, pRows);
        }

        private MiniInventoryMenu(MenuType<?> pType, int pContainerId, net.minecraft.world.entity.player.Inventory pPlayerInventory, int pRows, List<ItemStack> inv, CompoundTag data, UUID id) {
            this(pType, pContainerId, pPlayerInventory, new SimpleContainer(9 * pRows), pRows);
            Container ct = getContainer();

            otherInv = inv;
            userData = data;
            this.id = id;

            for (int i = 0; i < otherInv.size(); i++)
                ct.setItem(i, otherInv.get(i));
        }

        public static MiniInventoryMenu fourRows(int pContainerId, net.minecraft.world.entity.player.Inventory pPlayerInventory, List<ItemStack> inv, CompoundTag data, UUID id) {
            return new MiniInventoryMenu(MenuType.GENERIC_9x4, pContainerId, pPlayerInventory, 4, inv, data, id);
        }

        @Override
        public void removed(final Player pPlayer) {
            super.removed(pPlayer);

            ListTag listtag = userData.getList("Inventory", 10);

            // Save to compound
            for (int i = 0; i < 9*4; i++) {
                CompoundTag compoundtag = listtag.getCompound(i);
                int j = compoundtag.getByte("Slot") & 255;
                if (i == j) {
                    if (listtag.size() > i)
                        listtag.set(i, getContainer().getItem(j).serializeNBT());
                }
            }

            userData.put("Inventory", listtag);

            File f = ServerLifecycleHooks.getCurrentServer().getFile("playerdata/" + id + ".dat");

            try {
                File file1 = File.createTempFile(id + "-", ".dat", f.getParentFile());
                NbtIo.writeCompressed(userData, file1);
                File file2 = new File(f.getParentFile(), id + ".dat");
                File file3 = new File(f.getParentFile(), id + ".dat_old");
                Util.safeReplaceFile(file2, file1, file3);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // if player online, update their inventory immediately

            ServerPlayer oPlayer = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(id);
            if (oPlayer != null) {
                for (int i = 0; i < 9*4; i++) {
                    oPlayer.getInventory().items.set(i, getContainer().getItem(i));
                }

                oPlayer.hurtMarked = true;
            }

        }
    }

    public static class SavedInventories extends SavedData {

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
            return level.getDataStorage().computeIfAbsent(SavedInventories::load, SavedInventories::new, "be_invs");
        }
    }
}
