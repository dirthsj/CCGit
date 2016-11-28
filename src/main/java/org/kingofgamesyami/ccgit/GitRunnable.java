package org.kingofgamesyami.ccgit;

import net.minecraftforge.fml.common.FMLLog;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.util.*;

/**
 * Created by Steven on 11/27/2016.
 */
public class GitRunnable implements Runnable {
    private volatile List<GitRequest> gitRequests = new ArrayList<GitRequest>();

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        FMLLog.info( "Git Thread Started" );
        while( !Thread.currentThread().isInterrupted() ){
            while( gitRequests.isEmpty() ){
                Thread.yield();
            }
            GitRequest gitRequest = gitRequests.get( 0 );
            FMLLog.info( "Starting on request " + gitRequest.getComputer().getID() + ":" + gitRequest.getIdentifier() );
            try {
                gitRequest.getGitCommand().call();
            } catch (GitAPIException e) {
                FMLLog.info( "Request " + gitRequest.getComputer().getID() + ":" + gitRequest.getIdentifier() + " Failed with message " + e.getMessage() );
                gitRequest.getComputer().queueEvent( "ccgit", new Object[]{gitRequest.getIdentifier(), false, e.getMessage()} );
            } finally {
                FMLLog.info( "Request " + gitRequest.getComputer().getID() + ":" + gitRequest.getIdentifier() + "Succeeded" );
                gitRequest.getComputer().queueEvent( "ccgit", new Object[]{gitRequest.getIdentifier(), true} );
            }
            Thread.yield();
        }
    }

    public synchronized void queue(GitRequest gitRequest ){
        FMLLog.info( "Queued git request " + gitRequest.getComputer().getID() + ":" + gitRequest.getIdentifier() );
        gitRequests.add( gitRequest );
    }
}
