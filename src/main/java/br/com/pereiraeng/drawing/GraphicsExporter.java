package br.com.pereiraeng.drawing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import br.com.pereiraeng.io.IOutils;
import br.com.pereiraeng.math.swing.chart.Chart;
import br.com.pereiraeng.math.swing.chart.CurveFamily;
import br.com.pereiraeng.math.swing.chart.Plotable;
import br.com.pereiraeng.physics.swing.RealDimensionInput;
import br.com.pereiraeng.swing.HTMLselection;
import br.com.pereiraeng.swing.input.cod.MarkupInput;
import br.com.pereiraeng.swing.input.file.FileChooser;

public class GraphicsExporter {

	// O código é gerado nos elementos

	/**
	 * Função que gera ou o código ou o arquivo com o código descritivo do desenho
	 * de uma série de objetos. O código pode ser SVG ou TikZ, à escolha do usuário.
	 * 
	 * @param parent        janela que conterá as caixas de diálogo que serão
	 *                      abertas
	 * @param elems         objetos a serem desenhados
	 * @param dx            largura total do desenho com todos os objetos, nas
	 *                      unidades próprias dos objetos
	 * @param dy            altura total do desenho com todos os objetos, nas
	 *                      unidades próprias dos objetos
	 * @param foregroundSVG código SVG do desenho que será posto à frente dos
	 *                      objetos. Esse código pode conter certos erros (e.g.,
	 *                      texto com acentos ou outros símbolos unicode)
	 * @param backgroundSVG código SVG do desenho que será posto atrás dos objetos.
	 *                      Esse código pode conter certos erros (e.g., texto com
	 *                      acentos ou outros símbolos unicode)
	 * @param comment       comentário a ser adicionado
	 */
	public static void exportElements(Container parent, Collection<? extends ExtDrawable> elems, float dx, float dy,
			String foregroundSVG, String backgroundSVG, String comment) {
		Object[] data = getExportFormat(parent, false);
		if (data != null) {
			GraphicsFormat format = (GraphicsFormat) data[0];

			String code = null;
			switch (format) {
			case TIKZ:
				Point2D.Float size = (Point2D.Float) data[2];
				code = TikZ.toTikz(elems, Math.min(size.x * 100 / dx, size.y * 100 / dy),
						TikZ.getTikz(SVG.svg2insts(foregroundSVG), 0.1f),
						TikZ.getTikz(SVG.svg2insts(backgroundSVG), 0.1f));
				if (comment != null)
					code = "% " + comment + "\r\n" + code;
				break;
			case SVG:
				code = SVG.toSVG(elems, foregroundSVG, backgroundSVG);
				break;
			case VML:
				code = VML.toVML(elems, VML.getVML(SVG.svg2insts(foregroundSVG)),
						VML.getVML(SVG.svg2insts(backgroundSVG)));
				break;
			default:
				System.err.printf("Não é possível exportar elementos para o formato %s\n", format);
				break;
			}

			File file = (File) data[1];
			output(parent, format, code, file);
		}
	}

	// O código é gerado aqui

	public static void exportChart(Component parent, Chart<?> chart) {
		Object[] data = getExportFormat(parent, true);
		if (data != null) {
			GraphicsFormat format = (GraphicsFormat) data[0];

			String code = null;
			switch (format) {
			case TIKZ:
				Point2D.Float size = (Point2D.Float) data[2];
				code = TikZ.toTikz(chart, 100f * size.x, 100f * size.y);
				break;
			case SVG:
				// TODO
				break;
			case MATLAB:
				ArrayList<double[][]> points = new ArrayList<>();
				ArrayList<String> legenda = new ArrayList<>();
				for (Plotable p : chart.getList()) {
					if (p instanceof CurveFamily) {
						CurveFamily cf = (CurveFamily) p;
						for (int i = 0; i < cf.size(); i++) {
							cf.setIndex(i);
							points.add(cf.getCoordinates());
							legenda.add(cf.getDescription());
						}
					} else {
						points.add(p.getCoordinates());
						legenda.add(p.getDescription());
					}
				}
				code = Matlab.generateMatlabCode(points, false, false, 1, "", chart.getxLabel(), chart.getyLabel(),
						legenda.toArray(new String[legenda.size()]));
				break;
			case VML:
				// TODO
				break;
			case EXCEL: // TODO
//				File f = (File) data[1];
//				if (f != null)
//					Office.export(f, chart);
				break;
			}

			if (code != null)
				output(parent, format, code, (File) data[1]);
			else {
				File f = (File) data[1];
				if (f != null)
					FileChooser.openCreatedFile(f, "Planilha criada", false);
			}

		}
	}

	// O gráfico é gerado na classe que implementa {@link InterfaceGE}

	/**
	 * 
	 * @param parent
	 * @param ige
	 * @param chart
	 */
	public static void exportGraphics(Component parent, InterfaceGE ige, boolean chart) {
		Object[] data = getExportFormat(parent, chart);
		GraphicsFormat format = (GraphicsFormat) data[0];
		File file = (File) data[1];

		String code = ige.getCode(format);

		output(parent, format, code, file);
	}

	// =================================================================================

	private static final String[] EXPORT_OPTIONS = { "Gerar arquivo", "Gerar código" };

	private static Object[] getExportFormat(Component parent, boolean chart) {
		JComboBox<GraphicsFormat> cb = new JComboBox<>(
				chart ? GraphicsFormat.values() : GraphicsFormat.valuesNotChart());
		int option = JOptionPane.showConfirmDialog(parent, cb, "Formato para exportação", JOptionPane.DEFAULT_OPTION);
		GraphicsFormat format = null;
		if (option == JOptionPane.OK_OPTION)
			format = (GraphicsFormat) cb.getSelectedItem();

		Object[] out = null;

		if (format == null)
			return null;

		if (format == GraphicsFormat.TIKZ) {
			out = new Object[3];

			RealDimensionInput rdi = new RealDimensionInput(TikZ.A4_COLUMNS);
			JOptionPane.showMessageDialog(parent, rdi, "Defina o tamanho do desenho", JOptionPane.QUESTION_MESSAGE);
			out[2] = rdi.get();
		} else
			out = new Object[2];

		if (format == GraphicsFormat.EXCEL)
			out[1] = FileChooser.fileChooserSave("", format.getExtension());
		else if (format != GraphicsFormat.VML) {
			// se for TikZ ou SVG, gera-se um código que vai para um arquivo ou caixa de
			// texto
			int f = JOptionPane.showOptionDialog(parent, "Como gostaria que o gráfico seja exportado?",
					"Modo de exportar", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					EXPORT_OPTIONS, EXPORT_OPTIONS[0]);
			if (f == JOptionPane.CLOSED_OPTION)
				return null;
			else {
				if (f == 0) // se for para gerar um arquivo...
					out[1] = FileChooser.fileChooserSave("", format.getExtension());
			}
		}

		out[0] = format;
		return out;

	}

	private static void output(Component parent, GraphicsFormat format, String code, File file) {
		if (code != null) {
			if (file != null) {
				// escrever arquivo
				IOutils.writeFile2(file, code);
			} else {
				if (format == GraphicsFormat.VML) {
					// para área de trabalho
					JOptionPane.showMessageDialog(parent,
							"<html>Os desenhos foram enviados<br>para a área de trabalho.</html>",
							"Para a área de trabalho", JOptionPane.INFORMATION_MESSAGE);
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new HTMLselection(code), null);
				} else {
					// mostrar tela
					MarkupInput mi = new MarkupInput(format.getSintax());
					mi.setPreferredSize(new Dimension(800, 600));
					mi.setEditable(false);
					mi.set(code);
					JOptionPane.showMessageDialog(parent, mi, "Código gerado", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}
}
