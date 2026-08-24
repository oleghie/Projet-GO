package go;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GobanTest {

	@Test
	void test() {
		Goban g = new Goban(6, "bb ab ac aa");
		System.out.println(g);
		assertEquals(2, g.getNbLiberties(0, 2));
		assertEquals(1, g.getNbLiberties(0, 1));
		assertEquals(1, g.getNbLiberties(0, 0));
		assertEquals(3, g.getNbLiberties(1, 1));
		g.play(1, 0);
		System.out.println(g);

	}

}