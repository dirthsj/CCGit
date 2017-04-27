package org.kingofgamesyami.ccgit;

import org.squiddev.cctweaks.api.lua.IExtendedComputerAccess;
import org.squiddev.cctweaks.api.lua.ILuaAPI;
import org.squiddev.cctweaks.api.lua.ILuaAPIFactory;
import org.squiddev.cctweaks.api.lua.ILuaEnvironment;

import javax.annotation.Nonnull;

/**
 * Created by Steven on 11/25/2016.
 */
public class Register {
    public static void init(ILuaEnvironment environment) {
        environment.registerAPI(new ILuaAPIFactory() {
            @Override
            public ILuaAPI create(@Nonnull IExtendedComputerAccess computer) {
                return new CCGit( computer );
            }

            @Nonnull
            @Override
            public String[] getNames() {
                return new String[]{"ccgit"};
            }
        });
    }
}
