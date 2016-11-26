package org.kingofgamesyami.ccgit;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.filesystem.IWritableMount;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.server.MinecraftServer;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.squiddev.cctweaks.api.lua.ILuaAPI;
import org.squiddev.cctweaks.api.lua.IMethodDescriptor;

import java.io.File;

/**
 * Created by Steven on 11/25/2016.
 */
public class CCGit implements ILuaAPI, IMethodDescriptor, IComputerAccess {
    private IComputerAccess computer;
    private File computerDir;

    public CCGit( IComputerAccess computer ){
        this.computerDir = new File( ComputerCraft.getWorldDir( MinecraftServer.getServer().getEntityWorld() ), "computer/" + computer.getID() );
    }

    @Override
    public void startup() {
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void advance(double v) {

    }

    @Override
    public String[] getMethodNames() {
        return new String[]{"commit", "pull", "push", "clone"};
    }

    @Override
    public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
        switch( method ){
            case 0: // commit
                try{
                    Git git = Git.init().setGitDir( this.computerDir ).call();
                    git.commit();
                } catch( GitAPIException e ){
                    throw new LuaException( "Git API Failure" );
                }
                break;
            case 1: //pull

                break;
            case 2: //push

                break;
            case 3: //clone <remote> <directory>
                if( arguments.length < 2 || !( arguments[ 0 ] instanceof String && arguments[ 1 ] instanceof String ) ){
                    throw new LuaException( "Expected String, String" );
                }
                try{
                    Git.cloneRepository().setDirectory( new File( this.computerDir, (String)arguments[ 1 ] ) ).setRemote( (String)arguments[0] ).call();
                } catch( InvalidRemoteException e ){
                    throw new LuaException( "Invalid Remote" );
                } catch( GitAPIException e ){
                    throw new LuaException( "A Problem Occurred" );
                }
        }
        return new Object[0];
    }

    @Override
    public boolean willYield(int i) {
        return false;
    }

    @Override
    public String mount(String s, IMount iMount) {
        return null;
    }

    @Override
    public String mount(String s, IMount iMount, String s1) {
        return null;
    }

    @Override
    public String mountWritable(String s, IWritableMount iWritableMount) {
        return null;
    }

    @Override
    public String mountWritable(String s, IWritableMount iWritableMount, String s1) {
        return null;
    }

    @Override
    public void unmount(String s) {

    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void queueEvent(String s, Object[] objects) {

    }

    @Override
    public String getAttachmentName() {
        return null;
    }
}
