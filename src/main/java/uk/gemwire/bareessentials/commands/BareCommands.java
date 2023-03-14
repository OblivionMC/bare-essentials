package uk.gemwire.bareessentials.commands;

import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.RegisterCommandsEvent;

import static net.minecraft.commands.Commands.literal;

public class BareCommands {

    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            literal("setspawn")
                .requires(s -> s.hasPermission(Commands.LEVEL_ADMINS))
                .executes((s) -> CmdSetWorldSpawn.execute(s.getSource(),
                    new BlockPos(s.getSource().getPosition()), 0.0F))
        );

        event.getDispatcher().register(
            literal("tpa")
                .then(Commands.argument("user", EntityArgument.player())
                    .executes(CmdTeleportRequest::tpa)
                )
                .then(Commands.literal("accept")
                    .executes(CmdTeleportRequest::accept)
                )
                .then(Commands.literal("deny")
                    .executes(CmdTeleportRequest::deny)
                )
        );

        event.getDispatcher().register(
            literal("spawn")
                .executes((s) -> CmdSpawn.execute(s.getSource()))
        );

        event.getDispatcher().register(
            literal("fly")
                .requires(s -> s.hasPermission(Commands.LEVEL_ADMINS))
                .executes(CmdFly::executeOnSelf)

                .then(Commands.argument("user", EntityArgument.player())
                    .executes(CmdFly::executeOnOther)
                )
        );

        event.getDispatcher().register(
            literal("god")
                .requires(s -> s.hasPermission(Commands.LEVEL_ADMINS))
                .executes(CmdGod::executeOnSelf)

                .then(Commands.argument("user", EntityArgument.player())
                    .executes(CmdGod::executeOnOther)
                )
        );
    }
}
