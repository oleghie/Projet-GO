package go;

public class Intersection {
	private char stone = '.';

	public void setStone(Color color) {
		switch (color) {
		case BLACK -> stone = 'X';
		case WHITE -> stone = 'O';
		}
		;
	}

	public boolean isOccupied() {
		return stone != '.';
	}

	public char toChar() {
		return stone;
	}
}
