
package br.com.pereiraeng.drawing.drawutils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.pereiraeng.core.ColorUtils;
import br.com.pereiraeng.core.StringUtils;
import br.com.pereiraeng.html.HTML;
import br.com.pereiraeng.math.Scale2Dm;
import br.com.pereiraeng.math.geometry.Elipse;

/**
 * Classe das <strong>L</strong>istas de <strong>I</strong>nstruções de
 * <strong>D</strong>esenho
 * <p>
 * O vetor de instruções possui tamanho variável, sendo que o conteúdo de cada
 * uma das posições está indicado na tabela abaixo:
 * </p>
 * 
 * <p>
 * Ao se alterar os campos, também alterar XMLcompReader
 * </p>
 * 
 * <table border="1">
 * <tr>
 * <th>0</th>  
 * <th>1</th>  
 * <th>2</th>  
 * <th>3</th>  
 * <th>4</th>  
 * <th>5</th>  
 * <th>6</th>  
 * <th>7</th>  
 * </tr>
 * <tr>
 * <td>rect</td>  
 * <td>x</td>  
 * <td>y</td>  
 * <td>width</td>  
 * <td>height</td>  
 * <td>stroke</td>  
 * <td>fill</td>  
 * <td>stroke-dasharray</td>  
 * </tr>
 * <tr>
 * <td>line</td>  
 * <td>x1</td>  
 * <td>y1</td>  
 * <td>x2</td>  
 * <td>y2</td>  
 * <td>stroke</td>  
 * <td>stroke-dasharray</td>  
 * </tr>
 * <tr>
 * <td>text</td>  
 * <td>x</td>  
 * <td>y</td>  
 * <td>fill</td>  
 * <td>font-size</td>  
 * <td><i>text</i></td>  
 * </tr>
 * <tr>
 * <td>path</td>  
 * <td>visibility</td>  
 * <td>d</td>  
 * <td>stroke</td>  
 * <td>fill</td>  
 * </tr>
 * <tr>
 * <td>circle</td>  
 * <td>cx</td>  
 * <td>cy</td>  
 * <td>r</td>  
 * <td>stroke</td>  
 * <td>fill</td>  
 * </tr>
 * <tr>
 * <td>ellipse</td>  
 * <td>cx</td>  
 * <td>cy</td>  
 * <td>rx</td>  
 * <td>ry</td>  
 * <td>stroke</td>  
 * <td>fill</td>  
 * </tr>
 * <tr>
 * <td>polygon</td>  
 * <td>points</td>  
 * <td>stroke</td>  
 * <td>fill</td>  
 * </tr>
 * <tr>
 * <td>polyline</td>  
 * <td>points</td>  
 * <td>stroke</td>  
 * </tr>
 * </table>
 * 
 * @author Philipe PEREIRA
 */
public class LID extends LinkedList<Object[]> {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>
	 * Enumeração dos tipos de comandos de desenho.
	 * </p>
	 * 
	 * <p>
	 * Ao se alterar os campos, também alterar XMLcompReader
	 * </p>
	 * 
	 * @author Philipe PEREIRA
	 */
	public enum DrawAction {
		RECT("x", "y", "width", "height", "stroke", "fill", "stroke-dasharray"),
		LINE("x1", "y1", "x2", "y2", "stroke", "stroke-dasharray"), TEXT("x", "y", "fill", "font-size", null),
		PATH("visibility", "d", "stroke", "fill"), CIRCLE("cx", "cy", "r", "stroke", "fill"),
		ELLIPSE("cx", "cy", "rx", "ry", "stroke", "fill"), POLYGON("points", "stroke", "fill"),
		POLYLINE("points", "stroke");

		private String[] fields;

		/**
		 * Construtor do item da enumeração
		 * 
		 * @param fields            nome do campo (se for <code>null</code>, não é
		 *               campo, mas            conteúdo)
		 */
		private DrawAction(String... fields) {
			this.fields = fields;
		}

		/**
		 * Função que a partir dos objetos obtidos da partição de um dos comandos do
		 * código SVG contrói um vetor da {@link DrawAction lista de instruções de
		 * desenho}
		 * 
		 * @param drawAction            nome do comando
		 * @param atts                  tabela de dispersão com os atributos
		 * @param content               conteúdo (se houver)
		 * @return vetor da lista de instruções
		 */
		public static Object[] getArray(String drawAction, Map<String, String> atts, String content) {
			DrawAction da = DrawAction.valueOf(drawAction.toUpperCase());
			Object[] out = new Object[da.fields.length + 1];
			out[0] = drawAction;
			for (int i = 0; i < da.fields.length; i++) {
				if (da.fields[i] == null)
					out[i + 1] = content;
				else {
					String s = atts.get(da.fields[i]);
					Object o = null;
					switch (da.fields[i]) {
					case "x":
					case "y":
					case "x1":
					case "y1":
					case "x2":
					case "y2":
					case "width":
					case "height":
						o = Integer.parseInt(s);
						break;
					case "font-size":
						o = Float.parseFloat(s);
						break;
					case "stroke":
					case "fill":
					case "stroke-dasharray":
						o = s;
						break;
					default:
						System.err.println("Campo SVG desconhecido " + da.fields[i]);
						o = s;
						break;
					}
					out[i + 1] = o;
				}
			}
			return out;
		}

		/**
		 * Função que a partir de um vetor da {@link DrawAction lista de instruções de
		 * desenho} gera um dos comandos do código SVG
		 * 
		 * @param objs            vetor da lista de instruções
		 * @return um dos comando do código SVG
		 */
		public static String getSVG(Object[] objs) {
			DrawAction da = DrawAction.valueOf(((String) objs[0]).toUpperCase());
			String out = "<" + da.name().toLowerCase();
			String end = "/>\n";
			for (int i = 0; i < da.fields.length; i++) {
				Object o = objs[i + 1];
				if (o != null) {
					if (o instanceof Object[])
						o = StringUtils.addSeparator((Object[]) o, " ");
					if (da.fields[i] == null) // se tem conteúdo, muda o final
						end = String.format(">%s</%s>\n", HTML.accent2HTML2((String) o), da.name().toLowerCase());
					else
						out += " " + da.fields[i] + "=\"" + o + "\"";
				}
			}
			return out + end;
		}

		/**
		 * Função que a partir de um vetor da {@link DrawAction lista de instruções de
		 * desenho} gera um dos comandos do código TikZ
		 * 
		 * @param inst             vetor da lista de instruções
		 * @param scale            fator de escala
		 * @return um dos comando do código TikZ
		 */
		public static String getTikZ(Object[] inst, float scale) {
			String out = "";
			DrawAction da = DrawAction.valueOf(((String) inst[0]).toUpperCase());
			switch (da) {
			case LINE:
				out += String.format(Locale.US, "\\draw (%f,%f) -- (%f,%f)", (int) inst[1] * scale,
						(int) inst[2] * -scale, (int) inst[3] * scale, (int) inst[4] * -scale);
				break;
			case RECT:
				float x = (int) inst[1] * scale, y = (int) inst[2] * -scale;
				out += String.format(Locale.US, "\\draw (%f,%f) rectangle (%f,%f)", x, y, (int) inst[3] * scale + x,
						(int) inst[4] * -scale + y);
				break;
			case PATH:
				if ("visible".equals(inst[1])) {
					// TODO impor as restrições (tem de ser somente M-A)
					String[] c = ((String) inst[2]).trim().split("\\s+");

					int rx = Integer.parseInt(c[4]);
					int ry = Integer.parseInt(c[5]);

					double[] arcData = Elipse.getArc(Integer.parseInt(c[1]), Integer.parseInt(c[2]),
							Integer.parseInt(c[9]), Integer.parseInt(c[10]), rx, ry, Integer.parseInt(c[6]),
							"1".equals(c[7]), "1".equals(c[8]));

					int startAng = (int) Math.toDegrees(arcData[2]);
					// TODO a primeira coordenada é do ponto inicial, não do
					// centro
					// do círculo/elipse
					out += String.format(Locale.US, "\\draw (%f,%f) arc (%d:%d:%fcm and %fcm)",
							(arcData[0] - rx) * scale, arcData[1] * -scale, startAng,
							startAng + (int) Math.toDegrees(arcData[3]), rx * scale, ry * scale);
				}
				break;
			case TEXT:
				out += String.format(Locale.US, "\\draw (%f,%f) node {%s}", (int) inst[1] * scale,
						(int) inst[2] * -scale, inst[5]);
				break;
			case CIRCLE:
				out += String.format(Locale.US, "\\draw (%f,%f) circle (%fcm)", (int) inst[1] * scale,
						(int) inst[2] * -scale, (int) inst[3] * scale);
				break;
			case ELLIPSE:
				out += String.format(Locale.US, "\\draw (%f,%f) ellipse (%fcm and %fcm)", (int) inst[1] * scale,
						(int) inst[2] * -scale, (int) inst[3] * scale, (int) inst[4] * scale);
				break;
			case POLYGON:
				out = " -- cycle";
			case POLYLINE:
				String[] points = (String[]) inst[1];
				for (int i = points.length - 1; i >= 0; i--) {
					String[] xy = points[i].split(",");
					out = String.format(Locale.US, " -- (%f,%f)", Integer.parseInt(xy[0]) * scale,
							Integer.parseInt(xy[1]) * -scale) + out;
				}
				out = "\\draw " + out.substring(4);
				break;
			}
			return out + ";\n";
		}

		public static String getVML(Object[] objs) {
			String out = "";
			DrawAction da = DrawAction.valueOf(((String) objs[0]).toUpperCase());
			switch (da) {
			case LINE:
				out += String.format(
						"<v:line id=\"Conector_x0020_reto_x0020_39\" o:spid=\"_x0000_s1028\" style='position:absolute;visibility:visible;mso-wrap-style:square' from=\"%d,%d\" to=\"%d,%d\" o:connectortype=\"straight\" strokecolor=\"black\"/>",
						(int) objs[1] * VML.PT_VML, (int) objs[2] * VML.PT_VML, (int) objs[3] * VML.PT_VML,
						(int) objs[4] * VML.PT_VML);
				break;
			case RECT:
				out += String.format(
						"<v:rect id=\"Retangulo_x0020_24\" o:spid=\"_x0000_s1030\" style='position:absolute;left:%d;top:%d;width:%d;height:%d;visibility:visible;mso-wrap-style:square;v-text-anchor:middle' filled=\"f\" strokecolor=\"black\" strokeweight=\"1pt\"/>",
						(int) objs[1] * VML.PT_VML, (int) objs[2] * VML.PT_VML, (int) objs[3] * VML.PT_VML,
						(int) objs[4] * VML.PT_VML);
				break;
			case PATH:
				// TODO
				break;
			case TEXT:
				out += String.format(
						"<v:shapetype id=\"_x0000_t202\" coordsize=\"21600,21600\" o:spt=\"202\"\n  path=\"m,l,21600r21600,l21600,xe\">\n"
								+ "<v:stroke joinstyle=\"miter\"/>\n<v:path gradientshapeok=\"t\" o:connecttype=\"rect\"/>\n</v:shapetype>\n"
								+ "<v:shape id=\"Caixa_x0020_de_x0020_texto_x0020_44\" o:spid=\"_x0000_s1028\"\ntype=\"#_x0000_t202\" style='position:absolute;left:%d;top:%d;width:4876;\n"
								+ "height:9144;visibility:visible;mso-wrap-style:none;v-text-anchor:top' filled=\"f\" stroked=\"f\" strokeweight=\".5pt\">\n"
								+ "<v:textbox>\n<![if !mso]>\n<table cellpadding=0 cellspacing=0 width=\"100%%\">\n<tr>\n<td><![endif]>\n<div>\n<table class=MsoNormalTable border=0 cellspacing=0 cellpadding=0\nwidth=\"100%%\" style='width:100.0%%;mso-cellspacing:0cm;mso-yfti-tbllook:1184;mso-padding-alt:0cm 0cm 0cm 0cm'>\n<tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes;mso-yfti-lastrow:yes'>\n<td style='padding:0cm 0cm 0cm 0cm'>\n"
								+ "<p class=MsoNormal>%s</p>\n"
								+ "</td>\n</tr>\n</table>\n</div>\n<![if !mso]></td>\n</tr>\n</table>\n<![endif]></v:textbox>\n</v:shape>",
						(int) objs[1] * VML.PT_VML, (int) objs[2] * VML.PT_VML, objs[5]);
				break;
			case ELLIPSE:
				out += String.format(
						"<v:oval id=\"Elipse_x0020_41\" o:spid=\"_x0000_s1030\" style='position:absolute; left:%d;top:%d;width:%d;height:%d;visibility:visible; mso-wrap-style:square;v-text-anchor:middle' fillcolor=\"white\" strokecolor=\"black\" strokeweight=\"2pt\"/>",
						(int) objs[1] * VML.PT_VML, (int) objs[2] * VML.PT_VML, (int) objs[3] * VML.PT_VML,
						(int) objs[4] * VML.PT_VML);
				break;
			case CIRCLE:
				int r = (int) objs[3] * VML.PT_VML;
				out += String.format(
						"<v:oval id=\"Elipse_x0020_41\" o:spid=\"_x0000_s1030\" style='position:absolute; left:%d;top:%d;width:%d;height:%d;visibility:visible; mso-wrap-style:square;v-text-anchor:middle' fillcolor=\"white\" strokecolor=\"black\" strokeweight=\"2pt\"/>",
						(int) objs[1] * VML.PT_VML, (int) objs[2] * VML.PT_VML, r, r);
				break;
			case POLYGON:
				// TODO
				break;
			case POLYLINE:
				// TODO
				break;
			}
			return out + "\n";
		}
	}

	/**
	 * Função que, a partir de uma {@link DrawAction lista de instruções de
	 * desenho}, invoca os métodos de desenho no {@link Graphics2D objeto gráfico}
	 * 
	 * @param g               objeto gráfico
	 * @param list            lista de instruções
	 * @param x0              offset horizontal
	 * @param y0              offset vertical
	 * @param s               fator de escala
	 */
	public static void draw(Graphics2D g, List<Object[]> list, int x0, int y0, Scale2Dm s) {
		for (Object[] inst : list) {
			DrawAction da = DrawAction.valueOf(((String) inst[0]).toUpperCase());
			switch (da) {
			case LINE:
				if (inst[6] != null)
					g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f,
							StringUtils.parseFloats(((String) inst[6]).split(",")), 0f));

				int x1 = (int) inst[1], y1 = (int) inst[2], w = (int) inst[3], h = (int) inst[4];
				g.drawLine((int) ((x0 + x1 * s.getScale()) * s.getWidth()),
						(int) ((y0 + y1 * s.getScale()) * s.getHeight()),
						(int) ((x0 + w * s.getScale()) * s.getWidth()),
						(int) ((y0 + h * s.getScale()) * s.getHeight()));

				if (inst[6] != null)
					g.setStroke(new BasicStroke());
				break;
			case RECT:
				if (inst.length > 7 ? inst[7] != null : false)
					g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f,
							StringUtils.parseFloats(((String) inst[7]).split(",")), 0f));

				x1 = (int) ((x0 + ((int) inst[1]) * s.getScale()) * s.getWidth());
				y1 = (int) ((y0 + ((int) inst[2]) * s.getScale()) * s.getHeight());
				w = (int) ((((int) inst[3]) * s.getScale()) * s.getWidth());
				h = (int) ((((int) inst[4]) * s.getScale()) * s.getHeight());

				Color c = ColorUtils.html2color((String) inst[6]);
				if (c != null) {
					g.setColor(c);
					g.fillRect(x1, y1, w, h);
				}
				g.setColor(ColorUtils.html2color((String) inst[5]));
				g.drawRect(x1, y1, w, h);

				if (inst[7] != null)
					g.setStroke(new BasicStroke());
				break;
			case PATH: // impor as restrições (tem de ser somente M-A)
				if ("visible".equals(inst[1])) {
					String[] d = ((String) inst[2]).trim().split("\\s+");

					int rx = Integer.parseInt(d[4]);
					int ry = Integer.parseInt(d[5]);

					double[] arcData = Elipse.getArc(Integer.parseInt(d[1]), Integer.parseInt(d[2]),
							Integer.parseInt(d[9]), Integer.parseInt(d[10]), rx, ry, Integer.parseInt(d[6]),
							"1".equals(d[7]), "1".equals(d[8]));

					c = ColorUtils.html2color((String) inst[3]);
					Color co = null;
					if (c != null) {
						co = g.getColor();
						g.setColor(c);
					}

					g.drawArc((int) (((arcData[0] - rx) * s.getScale() + x0) * s.getWidth()),
							(int) (((arcData[1] - ry) * s.getScale() + y0) * s.getHeight()),
							(int) (2 * rx * s.getScale() * s.getWidth()), (int) (2 * ry * s.getScale() * s.getHeight()),
							(int) Math.round(Math.toDegrees(arcData[2])), (int) Math.round(Math.toDegrees(arcData[3])));

					if (co != null)
						g.setColor(co);
				}
				break;
			case TEXT:
				if (inst[inst.length - 1] != null) {
					String content = inst[inst.length - 1].toString();
					x1 = (int) inst[1];
					y1 = (int) inst[2];

					c = ColorUtils.html2color((String) inst[3]);
					Color co = null;
					if (c != null) {
						co = g.getColor();
						g.setColor(c);
					}

					Font f = g.getFont();
					g.setFont(f.deriveFont((float) inst[4]));
					g.drawString(content, (int) ((x0 + x1 * s.getScale()) * s.getWidth()),
							(int) ((y0 + y1 * s.getScale()) * s.getHeight()));
					g.setFont(f);

					if (co != null)
						g.setColor(co);
				}
				break;
			case CIRCLE:
				x1 = (int) inst[1];
				y1 = (int) inst[2];
				int r = (int) ((((int) inst[3]) * s.getScale()) * s.getWidth());

				c = ColorUtils.html2color((String) inst[5]);
				if (c != null) {
					g.setColor(c);
					g.fillOval((int) ((x0 + (x1 - r) * s.getScale()) * s.getWidth()),
							(int) ((y0 + (y1 - r) * s.getScale()) * s.getHeight()), 2 * r, 2 * r);
				}
				g.setColor(ColorUtils.html2color((String) inst[4]));
				g.drawOval((int) ((x0 + (x1 - r) * s.getScale()) * s.getWidth()),
						(int) ((y0 + (y1 - r) * s.getScale()) * s.getHeight()), 2 * r, 2 * r);
				break;
			case ELLIPSE:
				int rx = (int) (((int) inst[3]) * s.getScale()) * s.getWidth();
				int ry = (int) (((int) inst[4]) * s.getScale()) * s.getHeight();
				g.drawOval((int) ((x0 + ((int) inst[1] - rx) * s.getScale()) * s.getWidth()),
						(int) ((y0 + ((int) inst[2] - ry) * s.getScale()) * s.getHeight()), 2 * rx, 2 * ry);
				break;
			case POLYGON:
				String[] points = (String[]) inst[1];
				int[][] xys = new int[2][points.length];
				for (int i = 0; i < points.length; i++) {
					String[] xy = points[i].split(",");
					xys[0][i] = (int) ((x0 + (Integer.parseInt(xy[0])) * s.getScale()) * s.getWidth());
					xys[1][i] = (int) ((y0 + (Integer.parseInt(xy[1])) * s.getScale()) * s.getHeight());
				}
				g.setColor(ColorUtils.html2color((String) inst[3]));
				g.fillPolygon(xys[0], xys[1], points.length);
				g.setColor(ColorUtils.html2color((String) inst[2]));
				g.drawPolygon(xys[0], xys[1], points.length);
				break;
			case POLYLINE:
				points = (String[]) inst[1];
				xys = new int[2][points.length];
				for (int i = 0; i < points.length; i++) {
					String[] xy = points[i].split(",");
					xys[0][i] = (int) ((x0 + (Integer.parseInt(xy[0])) * s.getScale()) * s.getWidth());
					xys[1][i] = (int) ((y0 + (Integer.parseInt(xy[1])) * s.getScale()) * s.getHeight());
				}
				g.drawPolyline(xys[0], xys[1], points.length);
				break;
			}
		}
	}
}