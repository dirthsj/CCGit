package org.kingofgamesyami.ccgit;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.squiddev.cctweaks.api.CCTweaksAPI;
import org.squiddev.cctweaks.api.lua.CCTweaksPlugin;
import org.squiddev.cctweaks.api.lua.ILuaEnvironment;

/**
 * Created by Steven on 11/25/2016.
 */
@Mod( modid="ccgit", version = "0.1")
public class Main {
    private final LogHandler logger = new LogHandler.FMLLogger();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        Register.init(CCTweaksAPI.instance().luaEnvironment());
        GitRunnable.instance = new GitRunnable( logger );
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartedEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            logger.info( "Attempting to start Git Thread" );
            GitRunnable.instance.start();
        }
    }

    @Mod.EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            logger.info( "Stopping Git Thread" );
            GitRunnable.instance.interrupt();
        }
    }

    public static class CCGitPlugin extends CCTweaksPlugin {
        @Override
        public void register(ILuaEnvironment environment) {
            Register.init(environment);
            GitRunnable.instance = new GitRunnable( new LogHandler.BasicLogger() );
            GitRunnable.instance.start();
        }
    }
}
