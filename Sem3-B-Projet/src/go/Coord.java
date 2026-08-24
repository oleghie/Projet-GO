package go;

public class Coord {
	private int x;
	private int y;

	public Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Coord other = (Coord) obj;
		return x == other.x && y == other.y;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[x = ").append(x).append(" : y = ").append(y).append("]");
		return sb.toString();
	}
}
