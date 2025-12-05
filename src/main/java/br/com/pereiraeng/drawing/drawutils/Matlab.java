package br.com.pereiraeng.drawing.drawutils;

import java.awt.Color;
import java.util.List;
import java.util.Locale;

public class Matlab {

	/**
	 * Função que escreve o código em MatLab que gera um gráfico 2D
	 * 
	 * @param data    matriz com os dados a serem plotados, sendo que a primeira
	 *                linha são as abscissas e cada uma das demais linhas são as
	 *                ordenadas de diferentes curvas
	 * @param logx    se <code>true</code> o eixo x estará em escala logaritmica
	 * @param logy    se <code>true</code> o eixo x estará em escala logaritmica
	 * @param figure  númeração da figura do MatLab
	 * @param title   título do gráfico
	 * @param xlabel  título do eixo das abscissas
	 * @param ylabel  título do eixo das ordenadas
	 * @param legenda vetor com os títulos da legenda
	 * @return código em Matlab para se plotar o gráfico com as características
	 *         dadas nos argumentos acima listados
	 */
	public static String generateMatlabCode(double[][] data, boolean logx, boolean logy, int figure, String title,
			String xlabel, String ylabel, String[] legenda) {
		return generateMatlabCode(data, logx, logy, null, figure, title, xlabel, ylabel, Double.NaN, Double.NaN,
				legenda, true, Float.NaN, null, -1);
	}

	/**
	 * Função que escreve o código em MatLab que gera um gráfico 2D
	 * 
	 * @param data       matriz com os dados a serem plotados, sendo que a primeira
	 *                   linha são as abscissas e cada uma das demais linhas são as
	 *                   ordenadas de diferentes curvas
	 * @param logx       se <code>true</code> o eixo x estará em escala logaritmica
	 * @param logy       se <code>true</code> o eixo x estará em escala logaritmica
	 * @param modifiers  vetor com as alterações de forma e cor de cada uma das
	 *                   linhas do gráfico (se for for null, a linha será contínua e
	 *                   a cor será definida pela Matlab)
	 * @param figure     númeração da figura do MatLab
	 * @param title      título do gráfico
	 * @param xlabel     título do eixo das abscissas
	 * @param ylabel     título do eixo das ordenadas
	 * @param xmin       valor mínimo do eixo das abscissas
	 * @param xmax       valor máximo do eixo das abscissas
	 * @param legenda    vetor com os títulos da legenda
	 * @param grid       se <code>true</code> a grade do gráfico será mostrada
	 * @param lineWidth  espessura da linha do gráfico
	 * @param background cor de fundo da figura
	 * @return código em Matlab para se plotar o gráfico com as características
	 *         dadas nos argumentos acima listados
	 */
	public static String generateMatlabCode(double[][] data, boolean logx, boolean logy, String[] modifiers, int figure,
			String title, String xlabel, String ylabel, double xmin, double xmax, String[] legenda, boolean grid,
			float lineWidth, Color background, int fontsize) {
		String out = "% Gráfico " + figure + "\n\n";

		// conteúdo

		// abscissa
		out += "x = [";
		for (int i = 0; i < data[0].length; i++)
			out += String.format(Locale.US, "%.6g ", data[0][i]);
		out += "];\n\n";

		// ordenadas
		for (int i = 1; i < data.length; i++) {
			out += "y" + i + " = [";
			for (int j = 0; j < data[i].length; j++)
				out += String.format(Locale.US, "%.6g ", data[i][j]);
			out += "];\n\n";
		}

		// plotagem
		out += "figure(" + figure + ");\n";

		if (logx) {
			if (logy) {
				out += "loglog(";
			} else {
				out += "semilogx(";
			}
		} else {
			if (logy) {
				out += "semilogy(";
			} else {
				out += "plot(";
			}
		}
		out += getVector(true, data.length - 1, modifiers);

		if (!Float.isNaN(lineWidth))
			out += ",'LineWidth'," + lineWidth;

		out += ");\n";

		// ================================================================

		return out += format(fontsize, title, legenda, xlabel, ylabel, xmin, xmax, background, grid) + "\n\n\n";
	}

	/**
	 * Função que escreve o código em MatLab que gera um gráfico 2D
	 * 
	 * @param data    matriz com os dados a serem plotados (primeiro índice: curva;
	 *                segundo: x ou y; terceiro: cada um dos pontos)
	 * @param logx    se <code>true</code> o eixo x estará em escala logaritmica
	 * @param logy    se <code>true</code> o eixo x estará em escala logaritmica
	 * @param figure  númeração da figura do MatLab
	 * @param title   título do gráfico
	 * @param xlabel  título do eixo das abscissas
	 * @param ylabel  título do eixo das ordenadas
	 * @param legenda vetor com os títulos da legenda
	 * @return código em Matlab para se plotar o gráfico com as características
	 *         dadas nos argumentos acima listados
	 */
	public static String generateMatlabCode(List<double[][]> data, boolean logx, boolean logy, int figure, String title,
			String xlabel, String ylabel, String[] legenda) {
		return generateMatlabCode(data, logx, logy, null, figure, title, xlabel, ylabel, Double.NaN, Double.NaN,
				legenda, true, Float.NaN, null, -1);
	}

	/**
	 * Função que escreve o código em MatLab que gera um gráfico 2D
	 * 
	 * @param data       matriz com os dados a serem plotados (primeiro índice:
	 *                   curva; segundo: x ou y; terceiro: cada um dos pontos)
	 * @param logx       se <code>true</code> o eixo x estará em escala logaritmica
	 * @param logy       se <code>true</code> o eixo x estará em escala logaritmica
	 * @param modifiers  vetor com as alterações de forma e cor de cada uma das
	 *                   linhas do gráfico (se for for null, a linha será contínua e
	 *                   a cor será definida pela Matlab)
	 * @param figure     númeração da figura do MatLab
	 * @param title      título do gráfico
	 * @param xlabel     título do eixo das abscissas
	 * @param ylabel     título do eixo das ordenadas
	 * @param xmin       valor mínimo do eixo das abscissas
	 * @param xmax       valor máximo do eixo das abscissas
	 * @param legenda    vetor com os títulos da legenda
	 * @param grid       se <code>true</code> a grade do gráfico será mostrada
	 * @param lineWidth  espessura da linha do gráfico
	 * @param background cor de fundo da figura
	 * @return código em Matlab para se plotar o gráfico com as características
	 *         dadas nos argumentos acima listados
	 */
	public static String generateMatlabCode(List<double[][]> data, boolean logx, boolean logy, String[] modifiers,
			int figure, String title, String xlabel, String ylabel, double xmin, double xmax, String[] legenda,
			boolean grid, float lineWidth, Color background, int fontsize) {
		String out = "% Gráfico " + figure + "\n\n";

		// conteúdo

		// abscissa
		for (int i = 0; i < data.size(); i++) {
			out += "x" + (i + 1) + " = [";
			double[][] cs = data.get(i);
			for (int j = 0; j < cs[0].length; j++)
				out += String.format(Locale.US, "%.6g ", cs[0][j]);
			out += "];\n\n";
		}

		// ordenadas
		for (int i = 0; i < data.size(); i++) {
			out += "y" + (i + 1) + " = [";
			double[][] cs = data.get(i);
			for (int j = 0; j < cs[1].length; j++)
				out += String.format(Locale.US, "%.6g ", cs[1][j]);
			out += "];\n\n";
		}

		// plotagem
		out += "figure(" + figure + ");\n";

		if (logx) {
			if (logy) {
				out += "loglog(";
			} else {
				out += "semilogx(";
			}
		} else {
			if (logy) {
				out += "semilogy(";
			} else {
				out += "plot(";
			}
		}
		out += getVector(false, data.size(), modifiers);

		if (!Float.isNaN(lineWidth))
			out += ",'LineWidth'," + lineWidth;

		out += ");\n";

		// ================================================================

		return out += format(fontsize, title, legenda, xlabel, ylabel, xmin, xmax, background, grid) + "\n\n\n";
	}

	private static String format(int fontsize, String title, String[] legenda, String xlabel, String ylabel,
			double xmin, double xmax, Color background, boolean grid) {
		String out = "";

		boolean changeSize = fontsize != 10 && fontsize > 0;

		// título
		out += (changeSize ? "hTitle=" : "") + "title('" + title + "');\n";

		// tamanho das letras
		if (changeSize)
			out += String.format("set(hTitle,'FontSize',%1$d);\nset(gca,'FontSize',%1$d);\n", fontsize);

		// legenda
		out += "legend(";
		for (int i = 0; i < legenda.length; i++)
			out += "'" + legenda[i] + "',";
		out = out.substring(0, out.length() - 1);
		out += ");\n";

		// eixo x
		out += "xlabel('" + xlabel + "');\n";
		if (!Double.isNaN(xmin) && !Double.isNaN(xmax))
			out += "xlim([" + xmin + " " + xmax + "]);\n";

		// eixo y
		out += "ylabel('" + ylabel + "');\n";

		// cor de fundo
		if (background != null)
			out += "set(gcf, 'color', [" + String.format("%f %f %f", background.getRed() / 255f,
					background.getGreen() / 255f, background.getBlue() / 255f).replace(',', '.') + "]);\n";

		// grade
		if (grid)
			out += "grid on;";

		return out;
	}

	private static String getVector(boolean sameX, int curves, String[] modifiers) {
		String out = "";
		String format = sameX ? "x,y%d," : "x%1$d,y%1$d,";
		for (int j = 1; j <= curves; j++) {
			out += String.format(format, j);
			if (modifiers != null)
				out += (modifiers[j - 1] != null ? "'" + modifiers[j - 1] + "'," : "");
		}
		return out.substring(0, out.length() - 1);
	}
}
