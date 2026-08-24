package go;

public interface IPlayer {

	Coord getMove(Goban g);

	String getKind();
}
