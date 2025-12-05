package br.com.pereiraeng.drawing.drawutils;

import java.util.LinkedList;

import br.com.pereiraeng.swing.input.cod.Sintax;

/**
 * Enumeração dos possível formatos gráficos para os quais é possível exportar
 * 
 * @author Philipe PEREIRA
 *
 */
public enum GraphicsFormat {
	TIKZ("TikZ", "tex", false, Sintax.TEX), SVG("Scalable Vector Graphics", "svg", false, Sintax.XML),
	VML("Vector Markup Language", null, false, Sintax.XML), EXCEL("Excel", "xlsx", true, null),
	MATLAB("MatLab", "m", true, Sintax.M);

	private String format;

	private String extension;

	private boolean chartOnly;

	private Sintax sintax;

	private GraphicsFormat(String format, String extension, boolean chartOnly, Sintax sintax) {
		this.format = format;
		this.extension = extension;
		this.chartOnly = chartOnly;
		this.sintax = sintax;
	}

	@Override
	public String toString() {
		return format + (extension != null ? "(." + extension + ")" : "");
	}

	public String getExtension() {
		return extension;
	}

	/**
	 * Função que indica se este formato serve só para gráficos ou desenhos em geral
	 * 
	 * @return <code>true</code> se o formato é só para gráficos, <code>false</code>
	 *         para qualquer desenho
	 */
	public boolean isChartOnly() {
		return chartOnly;
	}

	/**
	 * Função que retorna a relação de formatos que podem ser aplicados a qualquer
	 * tipo de desenho
	 * 
	 * @return vetor com os formatos que podem ser usados para qualquer tipo de
	 *         desenho
	 */
	public static GraphicsFormat[] valuesNotChart() {
		LinkedList<GraphicsFormat> out = new LinkedList<>();
		for (GraphicsFormat gf : values())
			if (!gf.isChartOnly())
				out.add(gf);
		return out.toArray(new GraphicsFormat[out.size()]);
	}

	public Sintax getSintax() {
		return sintax;
	}
}
