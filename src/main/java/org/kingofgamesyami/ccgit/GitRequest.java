package org.kingofgamesyami.ccgit;

import dan200.computercraft.api.peripheral.IComputerAccess;
import org.eclipse.jgit.api.GitCommand;

/**
 * Created by Steven on 11/27/2016.
 */
public class GitRequest {
    private GitCommand gitCommand;
    private IComputerAccess computer;
    private int identifier;

    public GitRequest( IComputerAccess computer, int identifier, GitCommand gitCommand ){
        this.gitCommand = gitCommand;
        this.computer = computer;
        this.identifier = identifier;
    }

    public GitCommand getGitCommand() {
        return gitCommand;
    }

    public IComputerAccess getComputer() {
        return computer;
    }

    public int getIdentifier() {
        return identifier;
    }
}
