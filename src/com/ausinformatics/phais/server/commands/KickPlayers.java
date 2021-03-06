package com.ausinformatics.phais.server.commands;

import java.io.PrintStream;

import com.ausinformatics.phais.common.commander.Command;
import com.ausinformatics.phais.server.Director;
import com.ausinformatics.phais.server.interfaces.PersistentPlayer;

public class KickPlayers implements Command {

    private Director d;
    
    public KickPlayers(Director d) {
        this.d = d;
    }   
    
	@Override
	public void execute(PrintStream out, String[] args) {
		if (args.length != 0) {
			for (String name : args) {
				PersistentPlayer toKick = d.getPlayerFromName(name);
				if (toKick == null) {
					out.println("Error: " + name + " is not a connected player.");
				} else {
					d.deregisterPlayer(toKick);
					out.println("Kicked " + name);
				}
			}
		} else {
			out.println("Usage: kick PLAYERS...");
		}
	}

	@Override
	public String shortHelpString() {
		return "Kicks the listed players";
	}

	@Override
	public String detailedHelpString() {
		// TODO Auto-generated method stub
		return null;
	}
}
