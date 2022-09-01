package uk.gemwire.mmdessentials.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import uk.gemwire.mmdessentials.data.PendingTeleports;

public class TeleportRequestCommand {

    public static int tpa(CommandContext<CommandSourceStack> pSource) throws CommandSyntaxException {
        var target = EntityArgument.getPlayer(pSource, "user");
        var sender =  pSource.getSource().getPlayer();

        if (PendingTeleports.getRequestFrom(sender) != null) {
            sender.sendSystemMessage(Component.translatable("mmdessentials.tpa.toomanyrequests"));
            return 0;
        }

        target.sendSystemMessage(Component.translatable("mmdessentials.tpa.incoming", sender.getDisplayName()));
        PendingTeleports.PENDING.add(new PendingTeleports.TeleportRequest(target, sender, true));
        sender.sendSystemMessage(Component.translatable("mmdessentials.tpa.sent", target.getDisplayName()));

        return Command.SINGLE_SUCCESS;
    }

    public static int accept(CommandContext<CommandSourceStack> pSource) {
        var target =  pSource.getSource().getPlayer();
        var request = PendingTeleports.getRequestFor(target);

        if (request == null) {
            target.sendSystemMessage(Component.translatable("mmdessentials.tpa.norequests"));
            return 0;
        }

        request.sender().sendSystemMessage(Component.translatable("mmdessentials.tpa.inprogress"));
        target.sendSystemMessage(Component.translatable("mmdessentials.tpa.inprogress"));
        request.sender().teleportTo(target.getX(), target.getY(), target.getZ());

        return Command.SINGLE_SUCCESS;
    }

    public static int deny(CommandContext<CommandSourceStack> pSource) {
        var target =  pSource.getSource().getPlayer();
        var request = PendingTeleports.getRequestFor(target);

        if (request == null) {
            target.sendSystemMessage(Component.translatable("mmdessentials.tpa.norequests"));
            return 0;
        }

        PendingTeleports.PENDING.remove(request);
        request.sender().sendSystemMessage(Component.translatable("mmdessentials.tpa.denied", target.getDisplayName()));
        target.sendSystemMessage(Component.translatable("mmdessentials.tpa.denysuccess"));

        return Command.SINGLE_SUCCESS;
    }
}
