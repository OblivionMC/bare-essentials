package uk.gemwire.mmdessentials;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import uk.gemwire.mmdessentials.commands.ModCommands;

@Mod("mmdessentials")
public class MMDEssentials {

    public MMDEssentials() {
        IEventBus forge = MinecraftForge.EVENT_BUS;
        forge.addListener(ModCommands::registerCommands);
    }
}
