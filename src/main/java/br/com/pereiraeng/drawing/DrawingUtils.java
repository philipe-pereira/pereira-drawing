package br.com.pereiraeng.drawing;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.SwingConstants;

import br.com.pereiraeng.core.Direction;

/**
 * Classe com funções que fazem desenhos
 * 
 * @author Philipe PEREIRA
 *
 */
public class DrawingUtils {

	public static int getSwingConstants(Direction direction) {
		switch (direction) {
		case DOWN:
			return SwingConstants.SOUTH;
		case DOWN_RIGHT:
			return SwingConstants.SOUTH_EAST;
		case DOWN_LEFT:
			return SwingConstants.SOUTH_WEST;
		case LEFT:
			return SwingConstants.WEST;
		case RIGHT:
			return SwingConstants.EAST;
		case UP_RIGHT:
			return SwingConstants.NORTH_EAST;
		case UP_LEFT:
			return SwingConstants.NORTH_WEST;
		case UP:
			return SwingConstants.NORTH;
		case CENTER:
			return SwingConstants.CENTER;
		}
		return 0;
	}

	// -----------------------------------------------------------
	// --------------------- MUNDO DAS SETAS ---------------------
	// -----------------------------------------------------------

	/**
	 * Função que desenha uma seta em alguma das direção cardeais
	 * 
	 * @param g         objeto gráfico
	 * @param x         abscissa da ponta da seta
	 * @param y         ordenada da ponta da seta
	 * @param direction {@link Direction direção cardeal}
	 * @param alt       altura da seta, em pixels
	 * @param larg      metade da base da seta, em pixels
	 */
	public static void drawArrow(Graphics g, int x, int y, Direction direction, int alt, int larg) {
		int[] abs = null;
		int[] ord = null;

		switch (direction) {
		case DOWN:
			abs = new int[] { x, x + larg, x - larg };
			ord = new int[] { y, y - alt, y - alt };
			break;
		case LEFT:
			abs = new int[] { x, x + alt, x + alt };
			ord = new int[] { y, y + larg, y - larg };
			break;
		case RIGHT:
			abs = new int[] { x, x - alt, x - alt };
			ord = new int[] { y, y + larg, y - larg };
			break;
		case UP:
			abs = new int[] { x, x + larg, x - larg };
			ord = new int[] { y, y + alt, y + alt };
			break;
		case DOWN_LEFT:
			abs = new int[] { x, x + alt, x + larg };
			ord = new int[] { y, y - larg, y - alt };
			break;
		case DOWN_RIGHT:
			abs = new int[] { x, x - alt, x - larg };
			ord = new int[] { y, y - larg, y - alt };
			break;
		case UP_LEFT:
			abs = new int[] { x, x + alt, x + larg };
			ord = new int[] { y, y + larg, y + alt };
			break;
		case UP_RIGHT:
			abs = new int[] { x, x - alt, x - larg };
			ord = new int[] { y, y + larg, y + alt };
			break;
		default:
			break;
		}
		g.fillPolygon(abs, ord, 3);
	}

	/**
	 * Função que desenha uma seta em alguma das direção cardeais
	 * 
	 * @param g         objeto gráfico
	 * @param x         abscissa da base ou da ponta da seta
	 * @param y         ordenada da base ou da ponta da seta
	 * @param direction {@link Direction direção cardeal}
	 */
	public static void drawArrow(Graphics g, int x, int y, Direction direction) {
		drawArrow(g, x, y, direction, 10, 4);
	}

	/**
	 * Função que desenha uma seta em alguma das direção cardeais
	 * 
	 * @param x         abscissa da base ou da ponta da seta
	 * @param y         ordenada da base ou da ponta da seta
	 * @param base      <code>true</code> para que as coordenadas x e y seja da base
	 *                  da seta, <code>false</code> para que seja a ponta
	 * @param direction {@link Direction direção cardeal}
	 * @param g         objeto gráfico
	 */
	public static void drawArrow(int x, int y, boolean base, Direction direction, Graphics g) {
		drawArrow(g, x + (base ? (direction == Direction.RIGHT ? 10 : 0) - (direction == Direction.LEFT ? 10 : 0) : 0),
				y + (base ? +(direction == Direction.DOWN ? 10 : 0) - (direction == Direction.UP ? 10 : 0) : 0),
				direction);
	}

	/**
	 * Função que desenha uma seta
	 * 
	 * @param x   abscissa da ponta da seta
	 * @param y   ordenada da ponta da seta
	 * @param ang ângulo para o qual aponta a seta, em radianos
	 * @param g   objeto gráfico
	 */
	public static void drawArrow(int x, int y, double ang, Graphics g) {
		drawArrow(x, y, false, ang, true, g);
	}

	/**
	 * Função que desenha uma seta
	 * 
	 * @param x    abscissa da ponta da seta
	 * @param y    ordenada da ponta da seta
	 * @param ang  ângulo para o qual aponta a seta, em radianos
	 * @param fill <code>true</code> para preencher a seta
	 * @param g    objeto gráfico
	 */
	public static void drawArrow(int x, int y, double ang, boolean fill, Graphics g) {
		drawArrow(x, y, false, ang, fill, g);
	}

	/**
	 * Função que desenha uma seta
	 * 
	 * @param x    abscissa da base ou da ponta da seta
	 * @param y    ordenada da base ou da ponta da seta
	 * @param base <code>true</code> para que as coordenadas x e y seja da base da
	 *             seta, <code>false</code> para que seja a ponta
	 * @param ang  ângulo para o qual aponta a seta, em radianos
	 * @param fill <code>true</code> para preencher a seta
	 * @param g    objeto gráfico
	 */
	public static void drawArrow(int x, int y, boolean base, double ang, boolean fill, Graphics g) {
		drawArrow(x, y, base, ang, 10, 4, fill, g);
	}

	/**
	 * Função que desenha uma seta
	 * 
	 * @param x    abscissa da base ou da ponta da seta
	 * @param y    ordenada da base ou da ponta da seta
	 * @param base <code>true</code> para que as coordenadas x e y seja da base da
	 *             seta, <code>false</code> para que seja a ponta
	 * @param ang  ângulo para o qual aponta a seta, em radianos
	 * @param alt  altura da seta
	 * @param larg largura da seta
	 * @param fill <code>true</code> para preencher a seta
	 * @param g    objeto gráfico
	 */
	public static void drawArrow(int x, int y, boolean base, double ang, int alt, int larg, boolean fill, Graphics g) {
		double sen = Math.sin(ang), cos = Math.cos(ang);
		if (base) {
			if (fill)
				g.fillPolygon(new int[] { (int) (x + alt * cos), (int) (x - larg * sen), (int) (x + larg * sen) },
						new int[] { (int) (y - alt * sen), (int) (y - larg * cos), (int) (y + larg * cos) }, 3);
			else
				g.drawPolygon(new int[] { (int) (x + alt * cos), (int) (x - larg * sen), (int) (x + larg * sen) },
						new int[] { (int) (y - alt * sen), (int) (y - larg * cos), (int) (y + larg * cos) }, 3);
		} else {
			if (fill)
				g.fillPolygon(new int[] { x, (int) (x - larg * sen - alt * cos), (int) (x + larg * sen - alt * cos) },
						new int[] { y, (int) (y + alt * sen - larg * cos), (int) (y + alt * sen + larg * cos) }, 3);
			else
				g.drawPolygon(new int[] { x, (int) (x - larg * sen - alt * cos), (int) (x + larg * sen - alt * cos) },
						new int[] { y, (int) (y + alt * sen - larg * cos), (int) (y + alt * sen + larg * cos) }, 3);
		}
	}

	/**
	 * Função que desenha um diamante
	 * 
	 * @param x   abscissa da base ou da ponta do diamante
	 * @param y   ordenada da base ou da ponta do diamante
	 * @param ang
	 * @param g
	 */
	public static void drawDiamond(int x, int y, double ang, Graphics2D g) {
		drawDiamond(x, y, 0, ang, 10, 4, g);
	}

	/**
	 * Função que desenha um diamante
	 * 
	 * @param x    abscissa da base, do centro ou da ponta do diamante
	 * @param y    ordenada da base, do centro ou da ponta do diamante
	 * @param base
	 *             <ol start="0">
	 *             <li>para que as coordenadas x e y seja a ponta do diamante;</i>
	 *             <li>para que as coordenadas x e y seja o centro do diamante;</i>
	 *             <li>para que as coordenadas x e y seja a base do diamante.</i>
	 *             </ol>
	 * @param ang  ângulo para o qual aponta o diamante, em radianos
	 * @param alt  altura do diamante
	 * @param larg largura do diamante
	 * @param g    objeto gráfico
	 */
	public static void drawDiamond(int x, int y, int base, double ang, int alt, int larg, Graphics g) {
		double sen = Math.sin(ang), cos = Math.cos(ang);
		if (base == 1)
			g.drawPolygon(
					new int[] { (int) (x + alt * cos), (int) (x - larg * sen), (int) (x - alt * cos),
							(int) (x + larg * sen) },
					new int[] { (int) (y - alt * sen), (int) (y - larg * cos), (int) (y + alt * sen),
							(int) (y + larg * cos) },
					4);
		else if (base == 0)
			g.drawPolygon(
					new int[] { x, (int) (x - larg * sen - alt * cos), (int) (x - 2 * alt * cos),
							(int) (x + larg * sen - alt * cos) },
					new int[] { y, (int) (y + alt * sen - larg * cos), (int) (y + 2 * alt * sen),
							(int) (y + alt * sen + larg * cos) },
					4);
		else if (base == 2)
			g.drawPolygon(
					new int[] { x, (int) (x + larg * sen + alt * cos), (int) (x + 2 * alt * cos),
							(int) (x - larg * sen + alt * cos) },
					new int[] { y, (int) (y - alt * sen + larg * cos), (int) (y - 2 * alt * sen),
							(int) (y - alt * sen - larg * cos) },
					4);
	}
}