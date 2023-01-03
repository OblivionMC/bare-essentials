package uk.gemwire.bareessentials;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import uk.gemwire.bareessentials.commands.BareCommands;

@Mod("bareessentials")
public class BareEssentials {

    public BareEssentials() {
        IEventBus forge = MinecraftForge.EVENT_BUS;
        forge.addListener(BareCommands::registerCommands);
    }
}
