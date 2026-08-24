package go;

public enum Color {
	BLACK, WHITE;

	private int score = 0;
	private Coord lastTake;
	private boolean skip = false;

	public int getScore() {
		return score;
	}

	char getSymbol() {
		return switch (this) {
		case BLACK -> 'X';
		case WHITE -> 'O';
		};
	}

	public Color otherColor() {
		return switch (this) {
		case BLACK -> WHITE;
		case WHITE -> BLACK;
		};
	}

	void addScore(int score) {
		this.score += score;
	}

	void skip() {
		skip = true;
	}

	void havePlay() {
		skip = false;
	}

	public boolean haveSkip() {
		return skip;
	}

	void play(Coord c) {
		lastTake = c;
	}

	public boolean ko(Coord c) {
		Coord coord = this.otherColor().lastTake;
		return coord != null && coord.equals(c);
	}

	public Coord getLT() {
		return lastTake;
	}
}
