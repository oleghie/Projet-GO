package go.players;

import java.util.Random;

import go.Coord;
import go.Goban;
import go.IPlayer;

public class RandomPlayer implements IPlayer {

	@Override
	public Coord getMove(Goban g) {
		if (g.toMove().otherColor().haveSkip() && g.toMove().getScore() > g.toMove().otherColor().getScore()) {
			return null;
		}
		Random r = new Random();
		int size = g.getBoardSize();
		int boardplace = size * size;
		int xy = r.nextInt(boardplace - 1);
		for (int i = 0; i < boardplace; i++) {
			int x = ((xy + i) / size) % size;
			int y = (xy + i) % size;
			Coord coord = new Coord(x, y);
			if (!g.isOccupied(x, y) && !g.toMove().ko(coord) && (g.capture(x, y) > 0 || !g.isSuicide(x, y))) {
				return coord;
			}
		}
		return null;
	}

	@Override
	public String getKind() {
		return "RandomPlayer";
	}

}
