package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.JPanel;

import logika.Igra;
import logika.Polje;
import logika.Poteza;

/**
 * @author Gasper
 * Na objekt razreda Platno se narise igra reversija.
 */
@SuppressWarnings("serial")
public class Platno extends JPanel implements MouseListener, MouseMotionListener {
	
	/**
	 * JFrame, ki bo prikazoval vsebino platna.
	 */
	private GlavnoOkno master;
	
	/**
	 * Dovoljena poteza na katero kazemo z misko; default null.
	 */
	private Poteza oznacena;
	
	/**
	 * Relativna sirina crte.
	 */
	private final static double LINE_WIDTH = 0.05;
	
	/**
	 * Relativni prostor med plosckom in robom kvadrata.
	 */
	private final static double PADDING = 0.1;
	
	/**
	 * Ustvari novo platno.
	 * @param master
	 */
	public Platno(GlavnoOkno master) {
		super();
		this.master = master;
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	/**
	 * @return dolzina stranice kvadrata polja
	 */
	private double stKvadrata() {
		return Math.min(getWidth(), getHeight()) / Igra.N; // Platno razdeli na N delov(igra ima plosco NxN).
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(600, 600);
	}
	
	/**
	 * Na platno narise nov ploscek.
	 * @param g2
	 * @param i
	 * @param j
	 * @param igralec - barva ploscka
	 */
	private void paintFigure(Graphics2D g2, int i, int j, Color bIgralca) {
		double s = stKvadrata();
		double r = s * (1.0 - LINE_WIDTH - 2.0 * PADDING);
		double x = s * (i + 0.5 * LINE_WIDTH + PADDING);
		double y = s * (j + 0.5 * LINE_WIDTH + PADDING);
		g2.setColor(bIgralca);
		g2.setStroke(new BasicStroke((float) (s * LINE_WIDTH)));
		g2.fillOval((int)x+1, (int)y+1, (int)r, (int)r);
		g2.setColor(Color.DARK_GRAY); // Obroba ploscka.
		g2.setStroke(new BasicStroke(2));
		g2.drawOval((int)x+1, (int)y+1, (int)r, (int)r);
	}
	
	/**
	 * Na polje narise manjsi kvadrat - za oznacitev dovoljenih polj.
	 * @param g2
	 * @param i
	 * @param j
	 */
	private void paintPossible(Graphics2D g2, int i, int j, Color b) {
		double s = stKvadrata();
		double r = s * (1.0 - LINE_WIDTH - 2.0 * PADDING);
		double x = s * (i + 0.5 * LINE_WIDTH + PADDING);
		double y = s * (j + 0.5 * LINE_WIDTH + PADDING);
		g2.setColor(b);
		g2.setStroke(new BasicStroke((float) (s * LINE_WIDTH / 2)));
		g2.fillRect((int)x+1, (int)y+1, (int)r, (int)r);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		double s = stKvadrata();
		g2.setColor(new Color(34,139,34));
		g2.fillRect(0, 0, (int)(Igra.N*s), (int)(Igra.N*s)); // Pobarvamo plosco zeleno.
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke((float) (s * LINE_WIDTH))); // Debelina crte za risanje, prilagaja se velikosti platna.
		for (int i = 0; i < Igra.N + 1; i++) {
			// Navpicne crte. Narisali bomo N+2 crt, 2 dodatni za rob.
			g2.drawLine((int)(i*s),
				    (int)(0),
				    (int)(i*s),
				    (int)(Igra.N*s));
			// Vodoravne crte.
			g2.drawLine((int)(0),
				    (int)(i*s),
				    (int)(Igra.N*s),
				    (int)(i*s));
		}
		Polje[][] plosca = master.getPlosca();
		if (plosca != null) {
			for (int i = 0; i < Igra.N; i++) {
				for (int j = 0; j < Igra.N; j++) {
					switch (plosca[i][j]) { // V odvisnosti od barve polja na igralni plosci, narise ploscek na polje na platnu.
					case CRNO: paintFigure(g2, i, j, Color.BLACK); break;
					case BELO: paintFigure(g2, i, j, Color.WHITE); break;
					default: break;
					}
				}
				
			}
		}
		Color a = new Color(0, 100, 0);
		Color b = new Color(50, 205, 50);
		List<Poteza> dovoljene = master.seznamDovoljenih();
		if (master.aktivniClovek()) { // Polja kamor lahko igramo potezo izpostavimo - le ce je aktivni igralec clovek
			for(Poteza p: dovoljene) {
				int potezaX = p.getStolpec();
				int potezaY = p.getVrstica();
				// Polje kamor lahko igramo potezo in nanj kazemo z misko se obarva drugace.
				if (oznacena != null && p.getStolpec() == oznacena.getStolpec() && p.getVrstica() == oznacena.getVrstica()) {
					paintPossible(g2, potezaY, potezaX, a);
				} else {
					paintPossible(g2, potezaY, potezaX, b);
				}
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int w = (int)stKvadrata(); // Dolzina stranice kvadrata.
		int i = x / w; // Index stolpca kamor smo kliknili (1, 2,...)
		double di = (x % w) / stKvadrata(); // Delez koliko znotraj kvadrata smo kliknili na x koordinati.
		int j = y / w;
		double dj = (y % w) / stKvadrata();
		if (0 <= i && i < Igra.N && // Pogoj, da kliknem znotraj igralnega polja.
			// Moramo klikniti vsaj toliko znotraj kvadrata, da ne kliknemo na crte.
			0.5 * LINE_WIDTH < di && di < 1.0 - 0.5 * LINE_WIDTH &&
			0 <= j && j < Igra.N &&
			0.5 * LINE_WIDTH < dj && dj < 1.0 - 0.5 * LINE_WIDTH) {
			master.klikniPolje(i, j);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		oznacena = null;
		int x = e.getX();
		int y = e.getY();
		int w = (int)stKvadrata();
		int i = x / w;
		int j = y / w;
		List<Poteza> dovoljene = master.seznamDovoljenih();
		for (Poteza d : dovoljene) { // Ce je mozno izvesti potezo v polje na trenutni polozaj miske, nastavi to potezo za oznaceno.
			if (d.getStolpec() == j && d.getVrstica() == i) {
				oznacena = d;
			}
		}
		repaint();		
	}
}
