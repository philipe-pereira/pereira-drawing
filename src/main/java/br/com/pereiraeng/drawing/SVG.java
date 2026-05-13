package br.com.pereiraeng.drawing;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.pereiraeng.html.HTML;
import br.com.pereiraeng.math.Scale2Dm;

public class SVG {

	public static final String START = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\"",
			END = "</svg>";

	private static final Pattern SVGpattern = Pattern.compile("<\\p{Lower}+ .+?/\\p{Lower}*>", Pattern.DOTALL);

	/**
	 * Função que gera um código SVG com os desenhos de um conjunto de objeto
	 * {@link ExtDrawable externamente desenháveis}
	 * 
	 * @param objs       objetos a serem desenhados
	 * @param foreground código SVG do desenho que será posto à frente dos objetos.
	 *                   Esse código pode conter certos erros (e.g., texto com
	 *                   acentos ou outros símbolos unicode) que serão corrigidos
	 *                   (ver {@link SVG#repairSVG(String)})
	 * @param background código SVG do desenho que será posto atrás dos objetos.
	 *                   Esse código pode conter certos erros (e.g., texto com
	 *                   acentos ou outros símbolos unicode) que serão corrigidos
	 *                   (ver {@link SVG#repairSVG(String)})
	 * @return código SVG
	 */
	public static String toSVG(Collection<? extends ExtDrawable> objs, String foreground, String background) {
		return toSVG(objs, foreground, background, null);
	}

	/**
	 * Função que gera um código SVG com os desenhos de um conjunto de objeto
	 * {@link ExtDrawable externamente desenháveis}
	 * 
	 * @param objs       objetos a serem desenhados
	 * @param foreground código SVG do desenho que será posto à frente dos objetos.
	 *                   Esse código pode conter certos erros (e.g., texto com
	 *                   acentos ou outros símbolos unicode) que serão corrigidos
	 *                   (ver {@link SVG#repairSVG(String)})
	 * @param background código SVG do desenho que será posto atrás dos objetos.
	 *                   Esse código pode conter certos erros (e.g., texto com
	 *                   acentos ou outros símbolos unicode) que serão corrigidos
	 *                   (ver {@link SVG#repairSVG(String)})
	 * @param d          dimensões do desenho
	 * @return código SVG
	 */
	public static String toSVG(Collection<? extends ExtDrawable> objs, String foreground, String background,
			Dimension d) {
		StringBuilder out = new StringBuilder(START);
		if (d != null)
			out.append(String.format(" width=\"%d\" height=\"%d\"", d.width, d.height));
		out.append(">\n");

		// primeiro o que vai atrás
		if (background != null) {
			out.append("<!-- background -->\n");
			out.append(SVG.repairSVG(background));
		}

		// objetos
		for (ExtDrawable obj : objs)
			out.append(obj.getSVG());

		// por último o que vai na frente
		if (foreground != null) {
			out.append("<!-- foreground -->\n");
			out.append(SVG.repairSVG(foreground));
		}

		out.append(END);
		return out.toString();
	}

	/**
	 * Função que corrige certos erros que podem estar nos códigos SVG (e.g., texto
	 * com acentos ou outros símbolos unicode)
	 * 
	 * @param svg código SVG
	 * @return código SVG corrigido
	 */
	public static String repairSVG(String svg) {
		Matcher m = Pattern.compile(">[^<]*?</", Pattern.DOTALL).matcher(svg);
		StringBuffer sb = new StringBuffer(svg.length());
		while (m.find()) {
			String s = m.group();
			s = HTML.accent2HTML2(s);
			m.appendReplacement(sb, Matcher.quoteReplacement(s));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Função que desenha através do {@link Graphics2D objeto gráfico} aquilo que é
	 * representado por um código SVG
	 * 
	 * @param g      objeto gráfico
	 * @param svg    código SVG
	 * @param scale  fator de escala a ser aplicado em todas as dimensões
	 * @param x0     offset horizontal
	 * @param y0     offset vertical
	 * @param width  fator de escala horizontal
	 * @param height fator de escala vertical
	 */
	public static void draw(Graphics2D g, String svg, float scale, int x0, int y0, int width, int height) {
		LID.draw(g, svg2insts(svg), x0, y0, new Scale2Dm(scale, width, height));
	}

	// ==================== LISTA DE INSTRUÇÕES DE DESENHO ====================

	// Instruções <-> SVG

	/**
	 * Função que gera, a partir de um código SVG, uma {@link LID.DrawAction lista
	 * de instruções de desenho}. É a função inversa de {@link SVG#getSVG(LID)}.
	 * 
	 * @param svg código SVG
	 * @return lista de instruções
	 */
	public static LID svg2insts(String svg) {
		if (svg == null)
			return null;

		LID out = new LID();

		Matcher m = SVGpattern.matcher(svg);
		while (m.find()) {
			String s = m.group(), content = null;
			if (s.endsWith("/>"))
				s = s.substring(1, s.length() - 2);
			else {
				String[] ss = s.split(">");
				s = ss[0].substring(1);
				content = ss[1].split("<")[0].trim();
			}

			String[] parts = s.split("[ ]+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

			HashMap<String, String> map = new HashMap<>();
			for (int i = 1; i < parts.length; i++) {
				String[] keyValue = parts[i].split("=");
				map.put(keyValue[0], keyValue[1].substring(1, keyValue[1].length() - 1));
			}

			out.add(LID.DrawAction.getArray(parts[0], map, content));
		}
		return out;
	}

	/**
	 * Função que gera, a partir de uma {@link LID.DrawAction lista de instruções de
	 * desenho}, o código SVG correspondente. É a função inversa de
	 * {@link SVG#svg2insts(String)}.
	 * 
	 * @param insts lista de instruções
	 * @return código SVG
	 */
	public static String getSVG(List<Object[]> insts) {
		StringBuilder out = new StringBuilder();
		for (Object[] d : insts)
			out.append(LID.DrawAction.getSVG(d));
		return out.toString();
	}
}
