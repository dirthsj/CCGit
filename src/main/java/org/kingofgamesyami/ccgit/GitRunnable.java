package org.kingofgamesyami.ccgit;

import dan200.computercraft.api.lua.LuaException;
import net.minecraftforge.fml.common.FMLLog;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Steven on 11/27/2016.
 */
public class GitRunnable extends Thread {
    private volatile ArrayBlockingQueue<GitRequest> gitRequests = new ArrayBlockingQueue<GitRequest>( 100 );

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
            GitRequest gitRequest;
            try {
                gitRequest = gitRequests.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                FMLLog.bigWarning( "Git thread interrupted!" );
                return;
            }
            FMLLog.info( "Starting on request " + gitRequest.getComputer().getID() + ":" + gitRequest.getIdentifier() );
            try {
                gitRequest.call();
            } catch (GitAPIException e) {
                FMLLog.info( "Request " + gitRequest.getComputer().getID() + ":" + gitRequest.getIdentifier() + " Failed with message " + e.getMessage() );
                gitRequest.getComputer().queueEvent( "ccgit", new Object[]{gitRequest.getIdentifier(), false, e.getMessage()} );
            } finally {
                FMLLog.info( "Request " + gitRequest.getComputer().getID() + ":" + gitRequest.getIdentifier() + " Succeeded" );
                gitRequest.getComputer().queueEvent( "ccgit", new Object[]{gitRequest.getIdentifier(), true} );
            }
            Thread.yield();
        }
    }

    public synchronized void queue( GitRequest gitRequest ) throws LuaException {
        FMLLog.info("Queued git request " + gitRequest.getComputer().getID() + ":" + gitRequest.getIdentifier());
        FMLLog.info("The git queue capacity is now: " + gitRequests.remainingCapacity());
        try {
            gitRequests.add(gitRequest);
        } catch (IllegalStateException e) {
            throw new LuaException("No room in queue");
        }
    }
}
