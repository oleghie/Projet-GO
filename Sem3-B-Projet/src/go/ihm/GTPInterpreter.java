package go.ihm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import go.Goban;
import go.IPlayer;
import go.players.RandomPlayer;
import go.Color;
import go.Coord;

public class GTPInterpreter {
	private final int MIN_SIZE = 2;
	private final int MAX_SIZE = 25;
	private String id = "";
	private Goban goban;
	private List<String> args;
	private Map<Color, IPlayer> players;

	public GTPInterpreter() {
		players = new HashMap<>();
	}

	private void boardsize() {
		try {
			int newSize = Integer.parseInt(args.get(0));
			if (newSize >= MIN_SIZE && newSize <= MAX_SIZE) {
				goban.setSize(newSize);
			} else {
				error("unsupported board size");
			}
		} catch (Exception e) {
			error("not an Integer");
		}
	}

	private Coord parseCoord(String xyStr) {
		try {
			int x = xyStr.charAt(0) - 'A';
			int y = Integer.parseInt(xyStr.substring(1)) - 1;
			return new Coord(x, y);
		} catch (Exception e) {
			return null;
		}
	}

	private void play() {
		if (args.size() != 2) {
			error("syntax error");
			return;
		}
		String color = args.get(0);
		String xyStr = args.get(1).toUpperCase();
		Coord xy = parseCoord(xyStr);
		if (xy == null) {
			error("invalid color or coordinate");
			return;
		}
		play(color, xy);
	}

	private void play(String color, Coord xy) {
		int prises = goban.play(color, xy.getX(), xy.getY());
		if (prises == -1) {
			error("invalid color or coordinate");
		} else if (prises == -2) {
			error("illegal move");
		}
		repondre("");
		System.out.println(goban.showboard());
	}

	private String getNumber(String s) {
		String Id = "";
		for (char caractere : s.toCharArray()) {
			if (Character.isDigit(caractere))
				Id += caractere;
			else
				break;
		}
		return Id;
	}

	private String parseCommand(String ligne) {
		ligne = ligne.trim().toLowerCase();
		String command = "";
		if (ligne.isEmpty()) {
			return command;
		}
		int iCommand = 0;
		String[] tokens = ligne.split("\\s+");
		id = getNumber(tokens[0]);
		if (tokens[0].matches("\\d+")) {
			if (tokens.length == 1) {
				error("syntax error");
				return command;
			}
			id = tokens[0];
			iCommand++;
			command = tokens[iCommand];
		} else {
			command = tokens[iCommand].substring(id.length());
		}
		args = Arrays.asList(tokens).subList(iCommand + 1, tokens.length);
		return command;

	}

	private IPlayer currentPlayer() {
		return players.get(goban.toMove());
	}

	public void session() throws IOException {
		goban = new Goban();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		players.put(Color.WHITE, new ConsolePlayer());
		players.put(Color.BLACK, new ConsolePlayer());
		boolean continuer = true;
		String commande;
		while (continuer && !goban.isFinished()) {
			if (currentPlayer().getKind().equals("RandomPlayer")) {
				Coord xy = currentPlayer().getMove(goban);
				if (xy != null) {
					int x = xy.getX();
					int y = xy.getY();
					System.out.println(goban.toMove().toString() + " : " + x + " " + y);
					goban.play(x, y);
					System.out.println(goban.showboard());
				} else {
					System.out.println(goban.toMove().toString() + " : skip");
					goban.skip();
				}
				continue;
			}
			String ligne = reader.readLine();
			if (ligne == null) {
				continue;
			}
			commande = parseCommand(ligne);
			switch (commande) {
			case "boardsize":
				boardsize();
				repondre("");
				break;
			case "clear_board":
				goban.clearBoard();
				repondre("");
				break;
			case "play":
				play();
				break;
			case "skip":
				goban.skip();
				repondre("");
				break;
			case "showboard":
				repondre("\n" + goban.showboard());
				break;
			case "liberties":
				liberties();
				break;
			case "player":
				player();
				break;
			case "quit":
				continuer = false;
				repondre("");
				break;
			default:
				error("unknown command");
			}
		}
	}

	private static final Map<String, IPlayer> Players = new HashMap<>();
	static {
		Players.put("console", new ConsolePlayer());
		Players.put("random", new RandomPlayer());
	}

	private void player() {
		if (args.size() != 2) {
			error("syntax error");
			return;
		}
		try {
			String colorStr = args.get(0).toUpperCase();
			Color color = Color.valueOf(colorStr);
			if (color == null) {
				return;
			}
			String type = args.get(1).toLowerCase();
			IPlayer p = Players.getOrDefault(type, null);
			if (p == null) {
				error("unknown player kind");
				return;
			}
			players.put(color, p);
			repondre(colorStr.toString() + " : " + p.getKind());
		} catch (Exception e) {
			error("illegal color");
		}
	}

	private void liberties() {
		if (args.size() != 1) {
			error("syntax error");
			return;
		}
		String xyStr = args.get(0).toUpperCase();
		Coord xy = parseCoord(xyStr);
		if (xy == null) {
			return;
		}
		int nb = goban.getNbLiberties(xy);
		if (nb != 0) {
			repondre("" + nb);
		} else {
			error("illegal intersection");
		}
	}

	private void repondre(String r) {
		if (id.isEmpty()) {
			System.out.println("= " + r + "\n");
		} else {
			System.out.println("=" + id + " " + r + "\n");
		}
	}

	private void error(String e) {
		if (id.isEmpty()) {
			System.out.println("? " + e + "\n");
		} else {
			System.out.println("?" + id + " " + e + "\n");
		}
	}
}
