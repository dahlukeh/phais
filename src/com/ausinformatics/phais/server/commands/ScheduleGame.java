package com.ausinformatics.phais.server.commands;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import com.ausinformatics.phais.common.commander.Command;
import com.ausinformatics.phais.server.Director;
import com.ausinformatics.phais.server.interfaces.PersistentPlayer;

public class ScheduleGame implements Command {

    private Director d;
    
    public ScheduleGame(Director d) {
        this.d = d;
    }   
    
    
	@Override
	public void execute(PrintStream out, String[] args) {

		List<PersistentPlayer> players = new LinkedList<PersistentPlayer>();

		for (String name : args) {
			PersistentPlayer toAdd = d.getPlayerFromName(name);
			if (toAdd == null) {
				out.println(name + " is not a connected player");
			} else {
				players.add(toAdd);
			}
		}

		if (players.size() != args.length) {
			out.println((args.length - players.size()) + " players not found, try again.");
		} else {
			out.println("Adding game to queue");
			d.addGameToQueue(players);
		}
	}

	@Override
	public String shortHelpString() {
		return "Add a game to the queue of games to be spawned";
	}

	@Override
	public String detailedHelpString() {
		// TODO Auto-generated method stub
		return null;
	}

}
