package zagon;

import gui.GlavnoOkno;

public class Reversi {

	public static void main(String[] args) {
		GlavnoOkno okno = new GlavnoOkno();
		okno.pack();
		okno.setVisible(true);
	}
	// Platno mouseClicked(e): master.klikniPolje(i, j) -> GlavnoOkno klikniPolje(x, y): strateg.klik(x, y) ->
	// Strateg klik(x, y): master.odigraj(new Poteza(x, y)) -> GlavnoOkno odigraj(Poteza p): igra.izvediPotezo(p) -> logika: Igra izvediPotezo(Poteza p)
}
