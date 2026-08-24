package go;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Goban {
	private int boardSize;
	private Intersection[][] board;
	private Color toMove = Color.BLACK;
	private static final Coord[] voisins = { new Coord(0, 1), new Coord(1, 0), new Coord(-1, 0), new Coord(0, -1) };

	public Goban() {
		this(19);
	}

	public Goban(int size) {
		setSize(size);
	}

	public Goban(int size, String playString) {
		this(size);
		beforeStart(playString);
	}

	private void beforeStart(String playString) {
		try (Scanner scanner = new Scanner(playString)) {
			while (scanner.hasNext()) {
				String coord = scanner.next().toUpperCase();
				if (coord.length() != 2) {
					throw new IllegalArgumentException();
				}
				int x = coord.charAt(0) - 'A';
				int y = coord.charAt(1) - 'A';
				play(x, y);
			}
			scanner.close();
		}
	}

	public int play(String color, int x, int y) {
		return (coordOK(x, y) && color.toUpperCase().equals(toMove.toString())) ? play(x, y) : -1;
	}

	public int play(int x, int y) {
		Coord coord = new Coord(x, y);
		if (board[x][y].isOccupied() || toMove.ko(coord)) {
			return -2;
		}
		int prises = capture(x, y);
		if (prises <= 0 && isSuicide(x, y)) {
			return -2;
		}
		board[x][y].setStone(toMove);
		toMove.havePlay();
		toMove = toMove.otherColor();
		toMove.play(coord);
		return prises;
	}

	public int play(Coord c) {
		return play(c.getX(), c.getY());
	}

	public int capture(int x, int y) {
		int prises = 0;
		List<Coord> takes = new ArrayList<>();
		for (Coord c : getVoisin(x, y)) {
			if (board[c.getX()][c.getY()].toChar() == toMove.otherColor().getSymbol()) {
				List<Coord> liberties = new ArrayList<>();
				liberties.add(c);
				int libertiesCount = getLibertiesRecursive(c, liberties, new ArrayList<>(liberties));
				if (libertiesCount == 1) {
					int pris = liberties.size();
					prises += pris;
					takes.addAll(liberties);
					toMove.addScore(pris);
					for (Coord l : liberties) {
						board[l.getX()][l.getY()] = new Intersection();
					}
				}
			}
		}
		if (takes.size() == 1) {
			toMove.play(takes.get(0));
		}
		return prises;
	}

	public boolean isSuicide(int x, int y) {
		for (Coord c : getVoisin(x, y)) {
			if (!board[c.getX()][c.getY()].isOccupied()) {
				return false;
			} else if (board[c.getX()][c.getY()].toChar() == toMove.getSymbol()) {
				List<Coord> liberties = new ArrayList<>();
				liberties.add(c);
				int libertiesCount = getLibertiesRecursive(c, liberties, new ArrayList<>(liberties));
				if (libertiesCount != 1) {
					return false;
				}
			}
		}
		return true;
	}

	public int getNbLiberties(Coord coord) {
		List<Coord> l = new ArrayList<>();
		l.add(coord);
		int liberty = getLibertiesRecursive(coord, l, new ArrayList<Coord>(l));
		return liberty;
	}

	private int getLibertiesRecursive(Coord coord, List<Coord> l, List<Coord> visited) {
		int liberty = 0;
		for (Coord c : getVoisin(coord.getX(), coord.getY())) {
			if (!visited.contains(c)) {
				char boardChar = board[c.getX()][c.getY()].toChar();
				if (!board[c.getX()][c.getY()].isOccupied()) {
					liberty++;
					visited.add(c);
				} else if (boardChar == board[coord.getX()][coord.getY()].toChar()) {
					l.add(c);
					visited.add(c);
					liberty += getLibertiesRecursive(c, l, visited);
				}
			}
		}
		return liberty;
	}

	public int getNbLiberties(int x, int y) {
		return getNbLiberties(new Coord(x, y));
	}

	private List<Coord> getVoisin(int x, int y) {
		List<Coord> coordList = new ArrayList<>();
		for (Coord c : voisins) {
			int newX = x + c.getX();
			int newY = y + c.getY();
			Coord Co = new Coord(newX, newY);
			if (coordOK(newX, newY)) {
				coordList.add(Co);
			}
		}
		return coordList;
	}

	public boolean coordOK(int x, int y) {
		return x >= 0 && x < boardSize && y >= 0 && y < boardSize;
	}

	public Color toMove() {
		return toMove;
	}

	public void clearBoard() {
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				board[i][j] = new Intersection();
			}
		}
		toMove = Color.BLACK;
	}

	public void setSize(int newSize) {
		boardSize = newSize;
		board = new Intersection[boardSize][boardSize];
		clearBoard();
	}

	public int getBoardSize() {
		return boardSize;
	}

	public boolean isOccupied(int x, int y) {
		return board[x][y].isOccupied();
	}

	public String showboard() {
		return toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int stringLength = String.valueOf(boardSize).length();
		int scorePlace = boardSize > 10 ? boardSize - 10 : 0;
		appendBoardHF(sb, stringLength);
		appendBoard(sb, stringLength, scorePlace);
		appendBoardHF(sb, stringLength);
		return sb.toString();
	}

	private void appendBoardHF(StringBuilder sb, int stringLength) {
		appendSpaces(sb, stringLength == 1 ? 2 : 3);
		for (char i = 0; i < boardSize; i++) {
			sb.append((char) ('A' + i)).append(i < boardSize - 1 ? ' ' : '\n');
		}
	}

	private void appendBoard(StringBuilder sb, int stringLength, int scorePlace) {
		for (int i = boardSize - 1; i >= 0; i--) {
			String row = String.format("%" + stringLength + "d", i + 1);
			sb.append(row).append(' ');
			for (int j = 0; j < boardSize; j++) {
				sb.append(board[j][i].toChar());
				if (j < boardSize) {
					sb.append(' ');
				}
			}
			sb.append(i + 1);
			if (i == scorePlace) {
				sb.append("     BLACK (X) has captured ").append(Color.BLACK.getScore()).append(" stones");
			} else if (i == 1 + scorePlace) {
				sb.append("     WHITE (O) has captured ").append(Color.WHITE.getScore()).append(" stones");
			}
			sb.append('\n');
		}
	}

	private void appendSpaces(StringBuilder sb, int count) {
		for (int i = 0; i < count; i++) {
			sb.append(' ');
		}
	}

	public boolean isFinished() {
		return Color.BLACK.haveSkip() && Color.WHITE.haveSkip();
	}

	public void skip() {
		toMove.skip();
		toMove = toMove.otherColor();
	}
}
