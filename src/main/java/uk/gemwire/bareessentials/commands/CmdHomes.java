package uk.gemwire.bareessentials.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import uk.gemwire.bareessentials.data.Bank;
import uk.gemwire.bareessentials.data.Homes;

public class CmdHomes {

    public static int execute(CommandContext<CommandSourceStack> cmd) {
        ServerPlayer player = cmd.getSource().getPlayer();

        Homes home = Homes.getOrCreate(cmd.getSource().getLevel());

        if (player.level().dimension() != ServerLevel.OVERWORLD) {
            player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                "bareessentials.home.wrongdimension")));
            return 0;
        }

        if (home.hasUserHome(player)) {
            player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                "bareessentials.home.teleporting")));

            BlockPos pos = home.getUserHome(player);
            player.teleportTo(pos.getX(), pos.getY(), pos.getZ());

            //player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                //"bareessentials.home.success")));
        } else {
            player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                "bareessentials.home.nohome")));
        }

        return Command.SINGLE_SUCCESS;
    }

    public class Set {

        public static int execute(CommandContext<CommandSourceStack> cmd) {
            ServerPlayer player = cmd.getSource().getPlayer();
            Homes home = Homes.getOrCreate(cmd.getSource().getLevel());

            if (player.level().dimension() != ServerLevel.OVERWORLD) {
                player.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                    "bareessentials.home.wrongdimension")));
                return 0;
            }

            boolean overwrite = home.hasUserHome(player);
            home.setUserHome(player, BlockPos.containing(player.getPosition(0)));

            cmd.getSource().getPlayer().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault(
                "bareessentials.sethome" + (overwrite ? ".overwrite" : ""))));

            return Command.SINGLE_SUCCESS;
        }
    }
}
