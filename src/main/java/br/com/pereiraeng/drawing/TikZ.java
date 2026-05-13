package br.com.pereiraeng.drawing;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.pereiraeng.core.ColorUtils;
import br.com.pereiraeng.core.Direction;
import br.com.pereiraeng.core.ExtendedMath;
import br.com.pereiraeng.core.Orientation;
import br.com.pereiraeng.latex.LaTeX;
import br.com.pereiraeng.math.Complex;
import br.com.pereiraeng.math.Multiplicador;
import br.com.pereiraeng.math.Vec;
import br.com.pereiraeng.math.swing.chart.Chart;
import br.com.pereiraeng.math.swing.chart.ChartPoint;
import br.com.pereiraeng.math.swing.chart.Cloud;
import br.com.pereiraeng.math.swing.chart.Curve;
import br.com.pereiraeng.math.swing.chart.CurveFamily;
import br.com.pereiraeng.math.swing.chart.Plotable;
import br.com.pereiraeng.math.swing.chart.grid.axis.NumAxis;
import br.com.pereiraeng.swing.LeafOM;
import br.com.pereiraeng.swing.interfaces.WL;

public class TikZ {

	public static final String LINE_COMM = "\\draw (%f,%f) -- (%f,%f);\n",
			CIRC_COMM = "\\draw (%f,%f) circle (%fcm);\n", RECT_COMM = "\\draw (%f,%f) rectangle (%f,%f);\n",
			ARC_COMM = "\\draw (%f,%f) arc (%d:%d:%fcm);\n", ARC_E_COMM = "\\draw (%f,%f) arc (%d:%d:%fcm and %fcm);\n",
			TEXT_COMM = "\\draw (%f,%f) node {%s};\n";

	public static final float TWO_COLUMN_WIDTH = 0.182f, TWO_COLUMN_HEIGHT = 0.220f, ONE_COLUMN_WIDTH = 0.0889f,
			ONE_COLUMN_HEIGHT = 0.1075f;

	/**
	 * Tabela de dispersão com alguns valores sugeridos de altura e largura para os
	 * desenhos TikZ
	 */
	public static final Map<String, Float> A4_COLUMNS;
	static {
		Map<String, Float> map = new LinkedHashMap<>();
		map.put("Uma coluna", new Point2D.Float(ONE_COLUMN_WIDTH, ONE_COLUMN_HEIGHT));
		map.put("Duas colunas", new Point2D.Float(TWO_COLUMN_WIDTH, TWO_COLUMN_HEIGHT));
		A4_COLUMNS = Collections.unmodifiableMap(map);
	}

	/**
	 * Distância máxima entre dois pontos do gráfico para se acrescentar um novo
	 * ponto no gráfico, em metros
	 */
	private static final float RESOLUTION = .0001f;

	private static final boolean BORDER = false;

	public static final String START = "\\begin{tikzpicture}\n", END = "\\end{tikzpicture}";

	/**
	 * Função que gera um código TikZ com os desenhos de um conjunto de objeto
	 * {@link ExtDrawable externamente desenháveis}
	 * 
	 * @param objs       objetos a serem desenhados
	 * @param scale      fator de escala a ser aplicada em todo o desenho
	 * @param foreground código TikZ do desenho que será posto à frente dos objetos
	 * @param background código TikZ do desenho que será posto atrás dos objetos
	 * @return código TikZ
	 */
	public static String toTikz(Collection<? extends ExtDrawable> objs, float scale, String foreground,
			String background) {
		StringBuilder out = new StringBuilder(START);
		out.append("[scale=");
		out.append(scale);
		out.append("]\n");

		// primeiro o que vai atrás
		if (background != null) {
			out.append("%% background \n");
			out.append(background);
		}

		// objetos
		for (ExtDrawable obj : objs)
			out.append(obj.getTikz());

		// por último o que vai na frente
		if (foreground != null) {
			out.append("%% foreground \n");
			out.append(foreground);
		}

		out.append("\n");
		out.append(END);

		return out.toString();
	}

	/**
	 * Função que exporta o {@link Chart gráfico} para TikZ
	 * 
	 * @param chart   gráfico
	 * @param largura largura, em cm
	 * @param altura  altura, em cm
	 * @return código TikZ
	 */
	public static String toTikz(final Chart<?> chart, final float largura, final float altura) {
		StringBuilder out = new StringBuilder(START + "\n");

		WL wl = new WL() {

			@Override
			public float getX0() {
				return chart.getX0();
			}

			@Override
			public float getY0() {
				return chart.getY0();
			}

			@Override
			public float getDx() {
				return chart.getDx();
			}

			@Override
			public float getDy() {
				return chart.getDy();
			}

			@Override
			public float getPPH() {
				return largura / getDx();
			}

			@Override
			public float getPPV() {
				return altura / getDy();
			}
		};

		// definição do espaço entre as linhas
		float stepX = NumAxis.getGridStep(wl.getDx());
		float stepY = NumAxis.getGridStep(wl.getDy());

		// primeiros eixos do gráfico
		float x0 = wl.getX0();
		float x1 = x0 - ExtendedMath.mod(x0, stepX);

		float y0 = wl.getY0();
		float y1 = y0 - ExtendedMath.mod(y0, stepY);

		// coordenadas, em pixels, do primeiro dia
		Point2D.Float xy0 = LeafOM.getTranformedPointF(x1, y1, wl);
		// eixos principais
		Point2D.Float xyO = LeafOM.getTranformedPointF(0, 0, wl);
		xyO.y = altura - xyO.y;
		// passo, em pixels
		Point2D.Float step = LeafOM.getTranformedPointF(0f, 0f, stepX, stepY, wl);
		step.y = -step.y;
		// coordenadas, em pixels, do último dia dia
		Point2D.Float xyA = LeafOM.getTranformedPointF(0, 0, wl.getDx(), wl.getDy(), wl);
		xyA.y = -xyA.y;

		// número de passos
		int sx = (int) ((xyA.x - xy0.x) / step.x), sy = (int) ((xyA.y - xy0.y) / step.y);

		// grade
		double xg0 = xyO.x < 0 || xyO.x > largura ? xy0.x : xyO.x;
		double yg0 = xyO.y < 0 || xyO.y > altura ? xy0.y : xyO.y;
		out.append(String.format(Locale.US,
				"%% grade\n\\draw[help lines,shift={(%f,%f)}] (%f,%f) " + "grid [xstep=%f,ystep=%f] (%f,%f);\n\n", xg0,
				yg0, -xg0, -yg0, step.getX(), step.getY(), xyA.getX() - xg0, xyA.getY() - yg0));

		// eixo horizontal
		String text = chart.getxLabel();
		if (text != null)
			out.append(String.format(Locale.US,
					"%% eixo horizontal\n\\draw[->] (0,%f) -- (%f,%f) node[anchor=north west] {$%s$};\n\n",
					xyO.y < 0 || xyO.y > altura ? 0 : xyO.y, xyA.getX(), xyO.y < 0 || xyO.y > altura ? 0 : xyO.y,
					text));

		out.append("%% etiquetas do eixo horizontal\n");
		out.append(String.format(Locale.US,
				"\\foreach \\i [evaluate=\\i as \\v using %f+\\i*%f] in {1,2,...,%d}{ \\draw (%f+%f*\\i,%f) node[anchor=north] {\\pgfmathprintnumber{\\v}}; }\n\n",
				x1, stepX, sx, xy0.getX(), step.getX(), xyO.y < 0 || xyO.y > altura ? 0 : xyO.y));

		// eixo vertical
		text = chart.getyLabel();
		if (text != null)
			out.append(String.format(Locale.US,
					"%% eixo vertical\n\\draw[->] (%f,0) -- (%f,%f) node[anchor=south east] {$%s$};\n\n",
					xyO.x < 0 || xyO.x > largura ? 0 : xyO.x, xyO.x < 0 || xyO.x > largura ? 0 : xyO.x, xyA.getY(),
					text));
		float ys = -(sy * stepY) + y1;
		float yS = (float) (ys < 0 ? xyO.getY() + (ys / stepY) * Math.abs(step.getY()) : xyO.getY());
		out.append(String.format(Locale.US,
				"%% etiquetas do eixo vertical\n\\foreach \\i [evaluate=\\i as \\v using %f+\\i*%f] in {1,2,...,%d}{ \\draw (%f,%f+%f*\\i) node[anchor=east] {\\pgfmathprintnumber{\\v}}; }\n\n",
				ys, stepY, sy, xyO.x < 0 || xyO.x > largura ? 0 : xyO.x, yS, Math.abs(step.getY())));

		// título
		text = chart.getTitle();
		if (text != null)
			out.append(String.format(Locale.US, "%% título\n\\draw (%f,%f) node[anchor=south] {%s};\n\n",
					xyA.getX() / 2, xyA.getY(), LaTeX.greek2latex(text)));

		out.append("%% curvas\n");
		for (Plotable p : chart.getList()) {
			if (p instanceof ChartPoint) {
				ChartPoint cp = (ChartPoint) p;

				Point2D.Float pt = LeafOM.getTranformedPointF(cp.x, cp.y, wl);
				System.err.println("Ponto:\t" + pt);
			} else if (p instanceof Cloud) {
				Cloud cl = (Cloud) p;
				float[][] xy = LeafOM.getPointsF(cl, wl, RESOLUTION);

				out.append("\\draw[" + LaTeX.color2tex(cl.getOuter()) + "]");
				for (int j = 0; j < xy[0].length; j++) {
					float x = xy[0][j], y = xy[1][j];
					if (x >= 0 && x <= largura)
						out.append(String.format(Locale.US, " (%f,%f) --", x, altura - y));
				}
				out.setLength(out.length() - 3);
				out.append(";\n\n");
			} else if (p instanceof CurveFamily) {
				CurveFamily cf = (CurveFamily) p;

				int i = 0;
				for (Curve c : cf) {
					float[][] xy = LeafOM.getPointsF(c, wl, RESOLUTION);

					out.append("\\draw[" + LaTeX.color2tex(ColorUtils.getColor(i++)) + "]");
					for (int j = 0; j < xy[0].length; j++) {
						float x = xy[0][j], y = xy[1][j];
						if (x >= 0 && x <= largura)
							out.append(String.format(Locale.US, " (%f,%f) --", x, altura - y));
					}
					out.setLength(out.length() - 3);
					out.append(";\n\n");
				}
			}
		}

		// bordas
		if (BORDER)
			out.append(String.format(Locale.US, "\\draw[red] (0,0) -- (0,%f) -- (%f,%f) -- (%f,0) -- (0,0);\n", altura,
					largura, altura, largura));

		out.append("\n");
		out.append(END);
		return out.toString();
	}

	// ========================== LISTA DE INSTRUÇÕES ==========================

	// Instruções -> TikZ

	/**
	 * Função que gera, a partir de uma {@link LID.DrawAction lista de instruções de
	 * desenho}, o código TikZ correspondente.
	 * 
	 * @param insts lista de instruções
	 * @param scale fator de escala
	 * @return código SVG
	 */
	public static String getTikz(List<Object[]> insts, float scale) {
		if (insts == null)
			return null;
		StringBuilder out = new StringBuilder();
		for (Object[] d : insts)
			out.append(LID.DrawAction.getTikZ(d, scale));
		return out.toString();
	}

	// ========================== CIRCUIT ==========================

	/**
	 * Comprimento, em cm, dos componentes (distância de um terminal a outro do
	 * resistor, capacitor, indutor, gerador, etc.)
	 */
	public static final float COMP_LENGTH = 5f;

	public static String drawResistor(float x, float y, Orientation orient, String label, double value) {
		StringBuilder out = new StringBuilder();

		// etiqueta com o nome e valor
		boolean l = label != null ? !"".equals(label) : false;
		boolean va = value >= 0 && !Double.isNaN(value);
		if (va || l)
			out.append(String.format(Locale.US, TEXT_COMM, x + 1.5f, y - 1f, String.format("$%s%s%s$", l ? label : "",
					va && l ? "=" : "",
					va ? LaTeX.greek2latex(Multiplicador.getMult(value, 3, Multiplicador.POW3) + "\u03A9") : "")));

		// rodar 90 ao se desenhar no vertical
		boolean v = orient == Orientation.VERTICAL;
		if (v) {
			float aux = x;
			x = y;
			y = aux;
		}

		float x1 = x + .7f;
		float y1 = -y + 1f;

		out.append(String.format(Locale.US, "\\draw (%f,%f) -- (%f,%f) -- (%f,%f)", v ? y : x, -(v ? x : y),
				v ? y : x + .5f, -(v ? x + .5f : y), v ? y1 : x1, -(v ? x1 : y1)));

		// zig-zag
		for (int i = 3; i < 10; i++) {
			// passo em x
			float x2 = x1 + .5f;
			// alterna em y
			float y2 = 0f;
			if (y1 > y)
				y2 = y1 - 2f;
			else
				y2 = y1 + 2f;
			out.append(String.format(Locale.US, " -- (%f,%f)", v ? y2 : x2, -(v ? x2 : y2)));
			x1 = x2;
			y1 = y2;
		}

		out.append(String.format(Locale.US, " -- (%f,%f) -- (%f,%f);\n", v ? y : x1 + .3f, -(v ? x1 + .3f : y),
				v ? y : x1 + .8, -(v ? x1 + .8 : y)));
		return out.toString();
	}

	public static String drawIndutor(float x, float y, Orientation orientation, String label, double value) {
		return drawIndutor(x, y, Orientation.VERTICAL.equals(orientation) ? Direction.DOWN : Direction.LEFT, label,
				value);
	}

	public static String drawIndutor(float x, float y, Direction direction, String label, double value) {
		StringBuilder out = new StringBuilder();
		switch (direction) {
		case LEFT:
		case RIGHT:
			out.append(String.format(Locale.US, LINE_COMM, x, y, x + .5f, y));
			out.append(String.format(Locale.US,
					"\\foreach \\x in {1,...,4}\n\t\\draw (%fcm+1cm*\\x,%f) arc (%d:%d:%fcm and %fcm);\n", x + .5, y, 0,
					(Direction.LEFT.equals(direction) ? 1 : -1) * 180, 0.5, 1.0));
			out.append(String.format(Locale.US, LINE_COMM, x + 4.5f, y, x + COMP_LENGTH, y));
			break;
		case UP:
		case DOWN:
			out.append(String.format(Locale.US, LINE_COMM, x, y, x, y - .5f));
			out.append(String.format(Locale.US,
					"\\foreach \\x in {1,...,4}\n\t\\draw (%f,%fcm-1cm*\\x) arc (%d:%d:%fcm and %fcm);\n", x, y + .5,
					90, 90 + (Direction.UP.equals(direction) ? 1 : -1) * 180, 1.0, 0.5));
			out.append(String.format(Locale.US, LINE_COMM, x, y - 4.5, x, y - COMP_LENGTH));
			break;
		default:
			break;
		}

		// etiqueta com o nome e valor
		boolean l = label != null ? !"".equals(label) : false;
		boolean va = value >= 0 && !Double.isNaN(value);
		if (va || l)
			out.append(String.format(Locale.US, TEXT_COMM, x + 1.5f, y - 1f,
					String.format("$%s%s%s$", l ? label : "", va && l ? "=" : "",
							va ? LaTeX.greek2latex(Multiplicador.getMult(value, 3, Multiplicador.POW3) + "H") : "")));

		return out.toString();
	}

	public static String drawCapacitor(float x, float y, Orientation orient, String label, double value) {
		StringBuilder out = new StringBuilder();

		switch (orient) {
		case HORIZONTAL:
			out.append(String.format(Locale.US, LINE_COMM, x, y, x + 2f, y));
			out.append(String.format(Locale.US, LINE_COMM, x + 2f, y - 1.5f, x + 2f, y + 1.5f));
			out.append(String.format(Locale.US, LINE_COMM, x + 3f, y - 1.5f, x + 3f, y + 1.5f));
			out.append(String.format(Locale.US, LINE_COMM, x + 3f, y, x + COMP_LENGTH, y));
			break;
		case VERTICAL:
			out.append(String.format(Locale.US, LINE_COMM, x, y, x, y - 2f));
			out.append(String.format(Locale.US, LINE_COMM, x - 1.5f, y - 2f, x + 1.5f, y - 2f));
			out.append(String.format(Locale.US, LINE_COMM, x - 1.5f, y - 3f, x + 1.5f, y - 3f));
			out.append(String.format(Locale.US, LINE_COMM, x, y - 3f, x, y - COMP_LENGTH));
			break;
		}

		// etiqueta com o nome e valor
		boolean l = label != null ? !"".equals(label) : false;
		boolean va = value >= 0 && !Double.isNaN(value);
		if (va || l)
			out.append(String.format(Locale.US, TEXT_COMM, x + 1.5f, y - 1f,
					String.format("$%s%s%s$", l ? label : "", va && l ? "=" : "",
							va ? LaTeX.greek2latex(Multiplicador.getMult(value, 3, Multiplicador.POW3) + "F") : "")));

		return out.toString();
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param wf
	 *                   <ol start="0">
	 *                   <li>GENERAL;</i>
	 *                   <li>DC;</i>
	 *                   <li>AC;</i>
	 *                   <li>FOURIER;</i>
	 *                   <li>LAPLACE.</i>
	 *                   </ol>
	 * @param voltageSrc
	 * @param direction
	 * @param label
	 * @param value
	 * @return
	 */
	public static String drawGerador(float x, float y, int wf, boolean voltageSrc, Direction direction, String label,
			Object value) {
		StringBuilder out = new StringBuilder();

		// etiqueta com o nome e valor
		if (label != null) // TODO y-2.5f (gerador de corrente na vertical)
			out.append(String.format(Locale.US, TEXT_COMM, x + 2.5f, y + 2.5f, "$" + label + "$"));

		if (value != null) {
			String str;
			if (value instanceof Number) {
				double dp;
				Number n = (Number) value;
				if (n instanceof Complex)
					dp = ((Complex) n).getMod();
				else
					dp = n.doubleValue();
				str = Multiplicador.getMult(dp, 3, Multiplicador.POW3) + (voltageSrc ? "V" : "A");
			} else
				str = value.toString();
			out.append(String.format(Locale.US, TEXT_COMM, x + 2.5f, y + 4f, str));
		}

		switch (wf) {
		case 0:
			if (voltageSrc) {
				if (direction.isVertical()) {
					out.append(String.format(Locale.US, TEXT_COMM, x, y - 1f, "$+$"));
					out.append(String.format(Locale.US, CIRC_COMM, x, y - 2.5f, 1.5f));
					out.append(String.format(Locale.US, LINE_COMM, x, y, x, y - 1f));
					out.append(String.format(Locale.US, LINE_COMM, x, y - 4f, x, y - COMP_LENGTH));
					out.append(String.format(Locale.US, TEXT_COMM, x, y - 4f, "$-$"));
				} else {
					out.append(String.format(Locale.US, TEXT_COMM, x + 1f, y, "$+$"));
					out.append(String.format(Locale.US, CIRC_COMM, x + 2.5f, y, 1.5f));
					out.append(String.format(Locale.US, LINE_COMM, x, y, x + 1f, y));
					out.append(String.format(Locale.US, LINE_COMM, x + 4f, y, x + COMP_LENGTH, y));
					out.append(String.format(Locale.US, TEXT_COMM, x + 4f, y, "$-$"));
				}
				break;
			} else { // corrente
				// TODO triangulos, ver CircuitDrawer
			}
			break;
		case 1:
			if (voltageSrc) {
				switch (direction) {
				case RIGHT:
					out.append(String.format(Locale.US, LINE_COMM, x, y, x + 2f, y));
					out.append(String.format(Locale.US, LINE_COMM, x + 3f, y - 2.5f, x + 3f, y + 2.5f));
					out.append(String.format(Locale.US, LINE_COMM, x + 2f, y - 1.5f, x + 2f, y + 1.5f));
					out.append(String.format(Locale.US, LINE_COMM, x + 3f, y, x + COMP_LENGTH, y));
					break;
				case UP:
					out.append(String.format(Locale.US, LINE_COMM, x, y, x, y - 2f));
					out.append(String.format(Locale.US, LINE_COMM, x - 2.5f, y - 2f, x + 2.5f, y - 2f));
					out.append(String.format(Locale.US, LINE_COMM, x - 1.5f, y - 3f, x + 1.5f, y - 3f));
					out.append(String.format(Locale.US, LINE_COMM, x, y - 3f, x, y - COMP_LENGTH));
					break;
				case LEFT:
					out.append(String.format(Locale.US, LINE_COMM, x, y, x + 2f, y));
					out.append(String.format(Locale.US, LINE_COMM, x + 2f, y - 2.5f, x + 2f, y + 2.5f));
					out.append(String.format(Locale.US, LINE_COMM, x + 3f, y - 1.5f, x + 3f, y + 1.5f));
					out.append(String.format(Locale.US, LINE_COMM, x + 3f, y, x + COMP_LENGTH, y));
					break;
				case DOWN:
					out.append(String.format(Locale.US, LINE_COMM, x, y, x, y - 2f));
					out.append(String.format(Locale.US, LINE_COMM, x - 1.5f, y - 2f, x + 1.5f, y - 2f));
					out.append(String.format(Locale.US, LINE_COMM, x - 2.5f, y - 3f, x + 2.5f, y - 3f));
					out.append(String.format(Locale.US, LINE_COMM, x, y - 3f, x, y - COMP_LENGTH));
					break;
				default:
					break;
				}
			} else { // corrente
				switch (direction) {
				case RIGHT:
					float x0 = x + 1f, y0 = y + 1.25f;
					out.append(String.format(Locale.US, RECT_COMM, x0, y0, x0 + 3f, y0 - 2.5f));
					out.append(String.format(Locale.US, "\\draw [->] (%f,%f) -- (%f,%f);\n", x, y, x + 4f, y));
					out.append(String.format(Locale.US, LINE_COMM, x + 4f, y, x + COMP_LENGTH, y));
					break;
				case UP:
					x0 = x - 1.25f;
					y0 = y - 1f;
					out.append(String.format(Locale.US, RECT_COMM, x0, y0, x0 + 2.5f, y0 - 3f));
					out.append(String.format(Locale.US, "\\draw [<-] (%f,%f) -- (%f,%f);\n", x, y - 1f, x,
							y - COMP_LENGTH));
					out.append(String.format(Locale.US, LINE_COMM, x, y, x, y - 1f));
					break;
				case LEFT:
					x0 = x + 1f;
					y0 = y + 1.25f;
					out.append(String.format(Locale.US, RECT_COMM, x0, y0, x0 + 3f, y0 - 2.5f));
					out.append(String.format(Locale.US, "\\draw [<-] (%f,%f) -- (%f,%f);\n", x + 1f, y, x + COMP_LENGTH,
							y));
					out.append(String.format(Locale.US, LINE_COMM, x, y, x + 1f, y));
					break;
				case DOWN:
					x0 = x - 1.25f;
					y0 = y - 1f;
					out.append(String.format(Locale.US, RECT_COMM, x0, y0, x0 + 2.5f, y0 - 3f));
					out.append(String.format(Locale.US, "\\draw [->] (%f,%f) -- (%f,%f);\n", x, y, x, y - 4f));
					out.append(String.format(Locale.US, LINE_COMM, x, y - 4f, x, y - COMP_LENGTH));
					break;
				default:
					break;
				}
			}
			break;
		case 2:
		case 3:
		case 4:
			if (voltageSrc) {
				if (direction.isVertical()) {
					out.append(String.format(Locale.US, CIRC_COMM, x, y - 2.5f, 1.5f));
					out.append(String.format(Locale.US, ARC_COMM, x, y - 2.5f, 0, 180, .5f));
					out.append(String.format(Locale.US, ARC_COMM, x, y - 2.5f, 180, 360, .5f));

					out.append(String.format(Locale.US, LINE_COMM, x, y, x, y - 1f));
					out.append(String.format(Locale.US, LINE_COMM, x, y - 4f, x, y - COMP_LENGTH));
				} else {
					out.append(String.format(Locale.US, CIRC_COMM, x + 2.5f, y, 1.5f));
					out.append(String.format(Locale.US, ARC_COMM, x + 2.5, y, 0, 180, .5f));
					out.append(String.format(Locale.US, ARC_COMM, x + 2.5, y, 180, 360, .5f));

					out.append(String.format(Locale.US, LINE_COMM, x, y, x + 1f, y));
					out.append(String.format(Locale.US, LINE_COMM, x + 4f, y, x + COMP_LENGTH, y));
				}
			} else { // corrente
				switch (direction) {
				case RIGHT:
					out.append(String.format(Locale.US, CIRC_COMM, x + 2.5f, y, 1.5f));
					out.append(String.format(Locale.US, "\\draw [-> 90] (%f,%f) -- (%f,%f);\n", x, y, x + 4f, y));
					out.append(String.format(Locale.US, LINE_COMM, x + 4f, y, x + COMP_LENGTH, y));
					break;
				case UP:
					out.append(String.format(Locale.US, CIRC_COMM, x, y - 2.5f, 1.5f));
					out.append(String.format(Locale.US, "\\draw [<-] (%f,%f) -- (%f,%f);\n", x, y - 1f, x,
							y - COMP_LENGTH));
					out.append(String.format(Locale.US, LINE_COMM, x, y, x, y - 1f));
					break;
				case LEFT:
					out.append(String.format(Locale.US, CIRC_COMM, x + 2.5f, y, 1.5f));
					out.append(String.format(Locale.US, "\\draw [<-] (%f,%f) -- (%f,%f);\n", x + 1f, y, x + COMP_LENGTH,
							y));
					out.append(String.format(Locale.US, LINE_COMM, x, y, x + 1f, y));
					break;
				case DOWN:
					out.append(String.format(Locale.US, CIRC_COMM, x, y - 2.5f, 1.5f));
					out.append(String.format(Locale.US, "\\draw [->] (%f,%f) -- (%f,%f);\n", x, y, x, y - 4f));
					out.append(String.format(Locale.US, LINE_COMM, x, y - 4f, x, y - COMP_LENGTH));
					break;
				default:
					break;
				}
			}
			break;
		}
		return out.toString();
	}

	public static String drawTerra(float x, float y) {
		StringBuilder out = new StringBuilder();

		out.append(String.format(Locale.US, LINE_COMM, x, y, x, y - 1f));
		out.append(String.format(Locale.US, LINE_COMM, x - 2.1f, y - 1f, x + 2.1f, y - 1f));
		out.append(String.format(Locale.US, LINE_COMM, x - 1.3f, y - 1.5f, x + 1.3f, y - 1.5f));
		out.append(String.format(Locale.US, LINE_COMM, x - .5f, y - 2f, x + .5f, y - 2f));

		return out.toString();
	}

	private static final float BLOCK_WIDTH = 1.5f;

	public static String drawSwitch(float x, float y, boolean carryOnlyForward, int block, Direction direction,
			String label) {
		StringBuilder out = new StringBuilder();

		// vertical ou horizontal
		boolean v = direction.isVertical();
		// no sentido decrescente dos pixels
		boolean f = Direction.DOWN.equals(direction) || Direction.LEFT.equals(direction);

		if (label != null) // TODO y-2.5f (gerador de corrente na vertical)
			out.append(String.format(Locale.US, TEXT_COMM, x + 2.5f, y + 2.5f, "$" + label + "$"));

		// conector
		switch (direction) {
		case UP:
			out.append(String.format(Locale.US, LINE_COMM, x, y, x, y + 2f));
			out.append(String.format(Locale.US, LINE_COMM, x, y + 3f, x, y + COMP_LENGTH));
			break;
		case DOWN:
			out.append(String.format(Locale.US, LINE_COMM, x, y, x, y - 2f));
			out.append(String.format(Locale.US, LINE_COMM, x, y - 3f, x, y - COMP_LENGTH));
			break;
		case RIGHT:
			out.append(String.format(Locale.US, LINE_COMM, x, y, x + 2f, y));
			out.append(String.format(Locale.US, LINE_COMM, x + 3f, y, x + COMP_LENGTH, y));
			break;
		case LEFT:
			out.append(String.format(Locale.US, LINE_COMM, x, y, x - 2f, y));
			out.append(String.format(Locale.US, LINE_COMM, x - 3f, y, x - COMP_LENGTH, y));
			break;
		default:
			break;
		}

		float[] y0 = new float[] { 3f, 2f, 3f }, y1 = new float[] { 2f, 3f, 2f };

		// condução de corrente
		if (carryOnlyForward) {
			float[] xs = new float[] { -.6f, .0f, .6f };

			out.append("\\draw ");

			float[] x0s = Vec.shift(v ? xs : (f ? y0 : y1), x - (direction == Direction.LEFT ? COMP_LENGTH : 0));
			float[] y0s = Vec.shift(v ? (f ? y0 : y1) : xs, y - (direction == Direction.DOWN ? COMP_LENGTH : 0));

			for (int i = 0; i < x0s.length; i++) {
				float xt = x0s[i];
				float yt = y0s[i];
				out.append(String.format(Locale.US, "(%f,%f) -- ", xt, yt));
			}

			out.append(" cycle;\n");

		} else {
			float[] xs = new float[] { -.9f, -.3f, .3f };

			out.append("\\draw ");

			float[] x0s = Vec.shift(v ? xs : (f ? y0 : y1), x - (direction == Direction.LEFT ? COMP_LENGTH : 0));
			float[] y0s = Vec.shift(v ? (f ? y0 : y1) : xs, y - (direction == Direction.UP ? COMP_LENGTH : 0));

			for (int i = 0; i < x0s.length; i++) {
				float xt = x0s[i];
				float yt = y0s[i];
				out.append(String.format(Locale.US, "(%f,%f) -- ", xt, yt));
			}

			out.append(" cycle;\n");

			xs = new float[] { -.3f, .3f, .9f };

			out.append("\\draw ");

			x0s = Vec.shift(v ? xs : (f ? y1 : y0), x - (direction == Direction.LEFT ? COMP_LENGTH : 0));
			y0s = Vec.shift(v ? (f ? y1 : y0) : xs, y - (direction == Direction.UP ? COMP_LENGTH : 0));

			for (int i = 0; i < x0s.length; i++) {
				float xt = x0s[i];
				float yt = y0s[i];
				out.append(String.format(Locale.US, "(%f,%f) -- ", xt, yt));
			}

			out.append(" cycle;\n");
		}

		// bloqueio de tensão
		switch (block) {
		case 0:
			switch (direction) {
			case UP:
				out.append(String.format(Locale.US, LINE_COMM, x - BLOCK_WIDTH, y + 3f, x + BLOCK_WIDTH, y + 3f));
				break;
			case DOWN:
				out.append(String.format(Locale.US, LINE_COMM, x - BLOCK_WIDTH, y - 3f, x + BLOCK_WIDTH, y - 3f));
				break;
			case RIGHT:
				out.append(String.format(Locale.US, LINE_COMM, x + 3f, y - BLOCK_WIDTH, x + 3f, y + BLOCK_WIDTH));
				break;
			case LEFT:
				out.append(String.format(Locale.US, LINE_COMM, x - 3f, y - BLOCK_WIDTH, x - 3f, y + BLOCK_WIDTH));
				break;
			default:
				break;
			}
			break;
		case 1:
			switch (direction) {
			case UP:
				out.append(String.format(Locale.US, LINE_COMM, x - BLOCK_WIDTH, y + 2f, x + BLOCK_WIDTH, y + 2f));
				break;
			case DOWN:
				out.append(String.format(Locale.US, LINE_COMM, x - BLOCK_WIDTH, y - 2f, x + BLOCK_WIDTH, y - 2f));
				break;
			case RIGHT:
				out.append(String.format(Locale.US, LINE_COMM, x + 2f, y - BLOCK_WIDTH, x + 2f, y + BLOCK_WIDTH));
				break;
			case LEFT:
				out.append(String.format(Locale.US, LINE_COMM, x - 2f, y - BLOCK_WIDTH, x - 2f, y + BLOCK_WIDTH));
				break;
			default:
				break;
			}
			break;
		case 2:
			switch (direction) {
			case UP:
				out.append(String.format(Locale.US, LINE_COMM, x - BLOCK_WIDTH, y + 2f, x + BLOCK_WIDTH, y + 2f));
				out.append(String.format(Locale.US, LINE_COMM, x - BLOCK_WIDTH, y + 3f, x + BLOCK_WIDTH, y + 3f));
				break;
			case DOWN:
				out.append(String.format(Locale.US, LINE_COMM, x - BLOCK_WIDTH, y - 2f, x + BLOCK_WIDTH, y - 2f));
				out.append(String.format(Locale.US, LINE_COMM, x - BLOCK_WIDTH, y - 3f, x + BLOCK_WIDTH, y - 3f));
				break;
			case RIGHT:
				out.append(String.format(Locale.US, LINE_COMM, x + 2f, y - BLOCK_WIDTH, x + 2f, y + BLOCK_WIDTH));
				out.append(String.format(Locale.US, LINE_COMM, x + 3f, y - BLOCK_WIDTH, x + 3f, y + BLOCK_WIDTH));
				break;
			case LEFT:
				out.append(String.format(Locale.US, LINE_COMM, x - 2f, y - BLOCK_WIDTH, x - 2f, y + BLOCK_WIDTH));
				out.append(String.format(Locale.US, LINE_COMM, x - 3f, y - BLOCK_WIDTH, x - 3f, y + BLOCK_WIDTH));
				break;
			default:
				break;
			}
		}

		return out.toString();
	}
}
