package org.kingofgamesyami.ccgit;

import dan200.computercraft.api.peripheral.IComputerAccess;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Created by Steven on 11/27/2016.
 */
public class GitRequest {
    private GitCommand gitCommand;
    private CommitCommand commitCommand;
    private InitCommand initCommand;
    private IComputerAccess computer;
    private int identifier;

    private GitRequest( IComputerAccess computer, int identifier ){
        this.computer = computer;
        this.identifier = identifier;
    }

    public GitRequest( IComputerAccess computer, int identifier, GitCommand gitCommand ){
        this( computer, identifier );
        this.gitCommand = gitCommand;
    }

    public GitRequest( IComputerAccess computer, int identifier, InitCommand command ){
        this( computer, identifier );
        this.initCommand = command;
    }

    public GitRequest(IComputerAccess computer, int identifier, CommitCommand command ){
        this( computer, identifier );
        this.commitCommand = command;
    }

    public void call() throws GitAPIException {
        if( gitCommand != null ){
            gitCommand.call();
        }else if( commitCommand != null ){
            commitCommand.call();
        }else{
            initCommand.call();
        }
    }

    public IComputerAccess getComputer() {
        return computer;
    }

    public int getIdentifier() {
        return identifier;
    }
}
