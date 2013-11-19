package games.blockade;

import core.Config;
import core.Director;

public class Main {
	public static void main (String[] args) {
		System.out.println("Blockade started");
		Config config = new Config();
		config.parseArgs(args);
		config.port = 12317;
		new Director(new PlayerFactory(), new GameFactory()).run(config);
	}
}
