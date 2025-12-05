package br.com.pereiraeng.drawing.drawutils;

/**
 * Interface Graphics Exporter
 * 
 * @author Philipe PEREIRA
 *
 */
public interface InterfaceGE {

	/**
	 * Função que gera os códigos dos gráficos em MatLab, em Latex/TikZ, em Excel ou
	 * SVG
	 * 
	 * @param format formato do código
	 * @return código gerador do gráfico
	 */
	public String getCode(GraphicsFormat format);
}
