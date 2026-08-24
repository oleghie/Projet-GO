package go.ihm;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		if (args.length == 1 && args[0].toLowerCase().equals("gtp")) {
			new GTPInterpreter().session();
		} else {
			System.out.println("Arguments de la ligne de commandes incorrects : " + args);
		}
	}
}
