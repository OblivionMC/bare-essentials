package uk.gemwire.bareessentials.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import uk.gemwire.bareessentials.data.PendingTeleports;

public class TeleportRequestCommand {

    public static int tpa(CommandContext<CommandSourceStack> pSource) throws CommandSyntaxException {
        var target = EntityArgument.getPlayer(pSource, "user");
        var sender = pSource.getSource().getPlayer();

        if (PendingTeleports.getRequestFrom(sender) != null) {
            sender.sendSystemMessage(Component.translatable(
                "You cannot send more than one teleport request at a time!"));
            return 0;
        }

        target.sendSystemMessage(Component.translatable("%s wants to teleport to you.", sender.getDisplayName()));
        PendingTeleports.PENDING.add(new PendingTeleports.TeleportRequest(target, sender, true));
        sender.sendSystemMessage(Component.translatable("Teleport Request sent to %s.", target.getDisplayName()));

        return Command.SINGLE_SUCCESS;
    }

    public static int accept(CommandContext<CommandSourceStack> pSource) {
        var target = pSource.getSource().getPlayer();
        var request = PendingTeleports.getRequestFor(target);

        if (request == null) {
            target.sendSystemMessage(Component.translatable("You have no pending teleport requests!"));
            return 0;
        }

        request.sender().sendSystemMessage(Component.translatable("Teleport Request accepted, teleporting.."));
        target.sendSystemMessage(Component.translatable("Teleport Request accepted, teleporting.."));
        request.sender().teleportTo(target.getX(), target.getY(), target.getZ());

        return Command.SINGLE_SUCCESS;
    }

    public static int deny(CommandContext<CommandSourceStack> pSource) {
        var target = pSource.getSource().getPlayer();
        var request = PendingTeleports.getRequestFor(target);

        if (request == null) {
            target.sendSystemMessage(Component.translatable("You have no pending teleport requests!"));
            return 0;
        }

        PendingTeleports.PENDING.remove(request);
        request.sender().sendSystemMessage(Component.translatable(
            "%s denied your teleport request.", target.getDisplayName()));
        target.sendSystemMessage(Component.translatable("Incoming teleport request denied."));

        return Command.SINGLE_SUCCESS;
    }
}
