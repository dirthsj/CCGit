package org.kingofgamesyami.ccgit;

import dan200.computercraft.api.peripheral.IComputerAccess;
import org.squiddev.cctweaks.api.CCTweaksAPI;
import org.squiddev.cctweaks.api.lua.ILuaAPI;
import org.squiddev.cctweaks.api.lua.ILuaAPIFactory;
import org.squiddev.cctweaks.api.lua.ILuaEnvironment;

/**
 * Created by Steven on 11/25/2016.
 */
public class Register {
    public static void init() {
        ILuaEnvironment environment = CCTweaksAPI.instance().luaEnvironment();

        environment.registerAPI(new ILuaAPIFactory() {
            @Override
            public ILuaAPI create(IComputerAccess computer) {
                return new CCGit( computer );
            }

            @Override
            public String[] getNames() {
                return new String[]{"ccgit"};
            }
        });
    }
}
