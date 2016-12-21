package org.kingofgamesyami.ccgit;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.server.MinecraftServer;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.URIish;
import org.squiddev.cctweaks.api.lua.ILuaAPI;
import org.squiddev.cctweaks.api.lua.IMethodDescriptor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

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
        return new String[]{"commit", "pull", "push", "clone", "init", "addRemote", "getRemoteNames"};
    }

    @Override
    public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
        switch( method ){
            case 0: // commit <gitDir> <message>
                if( arguments.length < 2 || !(arguments[ 0 ] instanceof String && arguments[ 1 ] instanceof String )){
                    throw new LuaException( "Expected String, String" );
                }
                RepositoryBuilder builder = new RepositoryBuilder();
                builder.setGitDir( getAbsoluteDir( (String)arguments[0] ) );
                try {
                    Repository repo = builder.build();
                    Git git = new Git( repo );
                    return sendToGitThread( context, git.commit().setMessage( (String)arguments[ 1 ]) );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 1: //pull <gitDir> <remote>
                FileRepositoryBuilder fileRepositoryBuilder2 = new FileRepositoryBuilder();
                fileRepositoryBuilder2.setGitDir( getAbsoluteDir( (String)arguments[0] ) );
                try {
                    Git git2 = new Git( fileRepositoryBuilder2.build() );
                    return sendToGitThread( context, git2.pull().setRemote( (String)arguments[0] ) );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new Object[]{false};
            case 2: //push <gitDir> <remote
                if( arguments.length < 2 || !( arguments[ 0 ] instanceof String && arguments[ 1 ] instanceof String ) ){
                    throw new LuaException( "Expected String, String" );
                }
                FileRepositoryBuilder fileRepositoryBuilder1 = new FileRepositoryBuilder();
                fileRepositoryBuilder1.setGitDir( getAbsoluteDir( (String)arguments[0] ) );
                try {
                    Git git = new Git( fileRepositoryBuilder1.build() );
                    return sendToGitThread( context, git.push().setRemote( (String)arguments[1] ) );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new Object[]{false};
            case 3: //clone <remote> <directory>
                if( arguments.length < 2 || !( arguments[ 0 ] instanceof String && arguments[ 1 ] instanceof String ) ){
                    throw new LuaException( "Expected String, String" );
                }
                return sendToGitThread( context, Git.cloneRepository().setURI( (String)arguments[ 0 ] ).setDirectory( getAbsoluteDir( (String)arguments[1] ) ) );
            case 4: // init <directory>
                if( arguments.length < 1 || !( arguments[ 0 ] instanceof String ) ){
                    throw new LuaException( "Expected String" );
                }
                return sendToGitThread( context, Git.init().setDirectory( getAbsoluteDir( (String)arguments[ 0 ] ) ) );
            case 5: // addRemote <gitDir> <name> <uri>
                if( arguments.length < 3 || !( arguments[ 0 ] instanceof String && arguments[ 1 ] instanceof String && arguments[ 2 ] instanceof String ) ){
                    throw new LuaException( "Expected String, String, String" );
                }
                try {
                    StoredConfig config = (new RepositoryBuilder()).setGitDir( getAbsoluteDir( (String)arguments[ 0 ] )).build().getConfig();
                    config.setString( "remote", (String)arguments[1], "url", (String)arguments[2] );
                    config.save();
                    return new Object[]{true};
                } catch (IOException e) {
                    return new Object[]{false, e.getMessage()};
                }
            case 6: //getRemoteNames <gitDir>
                if( arguments.length < 1 || !(arguments[0] instanceof String ) ){
                    throw new LuaException( "Expected String" );
                }
                try {
                    FileRepositoryBuilder fileRepositoryBuilder = new FileRepositoryBuilder();
                    fileRepositoryBuilder.setGitDir( getAbsoluteDir((String)arguments[ 0 ]) );
                    return fileRepositoryBuilder.build().getRemoteNames().toArray();
                } catch (IOException e) {
                    return new Object[]{false, e.getMessage()};
                }
        }
        return new Object[0];
    }

    private Object[] sendToGitThread( ILuaContext context, GitCommand command ) throws LuaException, InterruptedException {
        int thisRequest = identifier++;
        Main.gitRunnable.queue( new GitRequest( computer, thisRequest, command ) );
        while(true){
            Object[] event = context.pullEvent( "ccgit" );
            if( event.length > 2 && event[1] instanceof Double && (Double)event[ 1 ] == thisRequest ){
                return new Object[]{event[2], (event.length > 3) ? event[3] : null};
            }
        }
    }

    private Object[] sendToGitThread( ILuaContext context, InitCommand command ) throws LuaException, InterruptedException {
        int thisRequest = identifier++;
        Main.gitRunnable.queue( new GitRequest( computer, thisRequest, command ) );
        while(true){
            Object[] event = context.pullEvent( "ccgit" );
            if( event.length > 2 && event[1] instanceof Double && (Double)event[ 1 ] == thisRequest ){
                return new Object[]{event[2], (event.length > 3) ? event[3] : null};
            }
        }
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
