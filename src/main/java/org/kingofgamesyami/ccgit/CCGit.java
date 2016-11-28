package org.kingofgamesyami.ccgit;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.server.proxy.ComputerCraftProxyServer;
import net.minecraft.server.MinecraftServer;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.squiddev.cctweaks.api.lua.ILuaAPI;
import org.squiddev.cctweaks.api.lua.IMethodDescriptor;

import java.io.File;

/**
 * Created by Steven on 11/25/2016.
 */
public class CCGit implements ILuaAPI, IMethodDescriptor {
    private final File computerDir;
    private final IComputerAccess computer;
    private int identifier = 0;

    public CCGit( IComputerAccess computer ){
        this.computer = computer;
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
                int thisRequest = identifier++;
                // Git.cloneRepository().setURI( (String)arguments[ 0 ] ).setDirectory( getAbsoluteDir( (String)arguments[1] ) ) )
                Main.gitRunnable.queue( new GitRequest( computer, thisRequest, Git.cloneRepository().setURI( (String)arguments[ 0 ] ).setDirectory( getAbsoluteDir( (String)arguments[1] ) ) ) );
                while(true){
                    Object[] event = context.pullEvent( "ccgit" );
                    if( event[ 1 ] instanceof Integer && (Integer)event[ 1 ] == thisRequest ){
                        return new Object[]{event[2], event[3]};
                    }
                }
        }
        return new Object[0];
    }

    @Override
    public boolean willYield(int i) {
        return true;
    }

    private File getAbsoluteDir( String localDir ) throws LuaException {
        File result = new File( this.computerDir, localDir );
        File temp = result;
        while( !temp.getAbsolutePath().equals( this.computerDir.getAbsolutePath() ) ){
            temp = temp.getParentFile();
            if( temp.equals( null ) ){
                throw new LuaException( "Attempt to break sandbox with path " + result.getAbsolutePath() );
            }
        }
        return result;
    }
}
