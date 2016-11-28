package org.kingofgamesyami.ccgit;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by Steven on 11/25/2016.
 */
@Mod( modid="ccgit", version = "0.1")
public class Main {

    public static GitRunnable gitRunnable;
    private Thread gitThread;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        Register.init();
        gitRunnable = new GitRunnable();
        gitThread = new Thread( gitThread );
        gitThread.setName( "CCGit" );
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartedEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            FMLLog.info( "Attempting to start Git Thread" );
            gitThread.start();
        }
    }
}
