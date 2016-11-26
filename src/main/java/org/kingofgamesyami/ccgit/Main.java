package org.kingofgamesyami.ccgit;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Created by Steven on 11/25/2016.
 */
@Mod( modid="ccgit", version = "0.1")
public class Main {

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        Register.init();
    }

}
