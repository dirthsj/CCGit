package org.kingofgamesyami.ccgit;

import dan200.computercraft.api.lua.LuaException;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Steven on 11/27/2016.
 */
public class GitRunnable extends Thread {
    public static GitRunnable instance;

    private final LogHandler logger;
    private volatile ArrayBlockingQueue<GitRequest> gitRequests = new ArrayBlockingQueue<GitRequest>( 100 );

    public GitRunnable (LogHandler logger) {
        this.logger = logger;
        setName( "Git Thread" );
        setDaemon(true);
    }

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
        logger.info( "Git Thread Started" );
        while( !Thread.currentThread().isInterrupted() ){
            GitRequest gitRequest;
            try {
                gitRequest = gitRequests.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.warning( "Git thread interrupted!" );
                return;
            }
            logger.info( "Starting on request " + gitRequest.getComputer().getID() + ":" + gitRequest.getIdentifier() );
            try {
                gitRequest.call();
            } catch (GitAPIException e) {
                logger.info( "Request " + gitRequest.getComputer().getID() + ":" + gitRequest.getIdentifier() + " Failed with message " + e.getMessage() );
                gitRequest.getComputer().queueEvent( "ccgit", new Object[]{gitRequest.getIdentifier(), false, e.getMessage()} );
            } finally {
                logger.info( "Request " + gitRequest.getComputer().getID() + ":" + gitRequest.getIdentifier() + " Succeeded" );
                gitRequest.getComputer().queueEvent( "ccgit", new Object[]{gitRequest.getIdentifier(), true} );
            }
            Thread.yield();
        }
    }

    public synchronized void queue( GitRequest gitRequest ) throws LuaException {
        logger.info("Queued git request " + gitRequest.getComputer().getID() + ":" + gitRequest.getIdentifier());
        logger.info("The git queue capacity is now: " + gitRequests.remainingCapacity());
        try {
            gitRequests.add(gitRequest);
        } catch (IllegalStateException e) {
            throw new LuaException("No room in queue");
        }
    }
}
