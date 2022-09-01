package uk.gemwire.mmdessentials.commands;

import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.RegisterCommandsEvent;
import static net.minecraft.commands.Commands.literal;

public class ModCommands {
    public static void registerCommands(RegisterCommandsEvent e) {
        e.getDispatcher().register(
                literal("fastworldspawn")
                        .requires(s -> s.hasPermission(Commands.LEVEL_ADMINS))
                        .executes((s) -> WorldSpawnCommand.execute(s.getSource(), new BlockPos(s.getSource().getPosition()), 0.0F))
        );

        e.getDispatcher().register(
                literal("tpa")
                        .then(Commands.argument("user", EntityArgument.player())
                                .executes(TeleportRequestCommand::tpa)
                        )
                        .then(Commands.literal("accept")
                                .executes(TeleportRequestCommand::accept)
                        )
                        .then(Commands.literal("deny")
                                .executes(TeleportRequestCommand::deny)
                        )
        );
    }
}
