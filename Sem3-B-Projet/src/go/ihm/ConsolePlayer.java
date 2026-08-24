package go.ihm;

import go.Coord;
import go.Goban;
import go.IPlayer;

public class ConsolePlayer implements IPlayer {

	@Override
	public Coord getMove(Goban goban) {
		throw new RuntimeException();
	}

	@Override
	public String getKind() {
		return "ConsolePlayer";
	}
}