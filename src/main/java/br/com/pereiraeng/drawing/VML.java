package br.com.pereiraeng.drawing;

import java.util.Collection;
import java.util.List;

public class VML {

	public static final int PT_VML = 100;

	private static final String START = "<html xmlns:v=\"urn:schemas-microsoft-com:vml\"\nxmlns:o=\"urn:schemas-microsoft-com:office:office\"\nxmlns:w=\"urn:schemas-microsoft-com:office:word\"\nxmlns:m=\"http://schemas.microsoft.com/office/2004/12/omml\"\nxmlns=\"http://www.w3.org/TR/REC-html40\">\n"
			+ "<head>\n<meta http-equiv=Content-Type content=\"text/html; charset=utf-8\">\n<meta name=ProgId content=Word.Document>\n<meta name=Generator content=\"Microsoft Word 15\">\n<meta name=Originator content=\"Microsoft Word 15\">\n"
			+ "<!--[if !mso]>\n<style>\nv\\:* {behavior:url(#default#VML);}\no\\:* {behavior:url(#default#VML);}\nw\\:* {behavior:url(#default#VML);}\n.shape {behavior:url(#default#VML);}\n</style>\n<![endif]--><!--[if gte mso 9]><xml>\n"
			+ "<o:OfficeDocumentSettings>\n<o:AllowPNG/>\n</o:OfficeDocumentSettings>\n</xml><![endif]-->\n"
			+ "<style>\n<!--\n/* Font Definitions */\n@font-face\\n{font-family:\"Cambria Math\";\npanose-1:2 4 5 3 5 4 6 3 2 4;\nmso-font-charset:0;\nmso-generic-font-family:roman;\nmso-font-pitch:variable;\nmso-font-signature:-536870145 1107305727 0 0 415 0;}\n@font-face\n{font-family:Calibri;\npanose-1:2 15 5 2 2 2 4 3 2 4;\nmso-font-charset:0;\nmso-generic-font-family:swiss;\nmso-font-pitch:variable;\nmso-font-signature:-536870145 1073786111 1 0 415 0;}\n/* Style Definitions */\np.MsoNormal, li.MsoNormal, div.MsoNormal\n{mso-style-unhide:no;\nmso-style-qformat:yes;\nmso-style-parent:\"\";\nmargin-top:0cm;\nmargin-right:0cm;\nmargin-bottom:10.0pt;\nmargin-left:0cm;\nline-height:115%;\nmso-pagination:widow-orphan;\nfont-size:11.0pt;\nfont-family:\"Calibri\",sans-serif;\nmso-ascii-font-family:Calibri;\nmso-ascii-theme-font:minor-latin;\nmso-fareast-font-family:Calibri;\nmso-fareast-theme-font:minor-latin;\nmso-hansi-font-family:Calibri;\nmso-hansi-theme-font:minor-latin;\nmso-bidi-font-family:\"Times New Roman\";\nmso-bidi-theme-font:minor-bidi;\nmso-fareast-language:EN-US;}\nspan.MsoPlaceholderText\n{mso-style-noshow:yes;\nmso-style-priority:99;\nmso-style-unhide:no;\ncolor:gray;}\n.MsoChpDefault\n{mso-style-type:export-only;\nmso-default-props:yes;\nfont-family:\"Calibri\",sans-serif;\nmso-ascii-font-family:Calibri;\nmso-ascii-theme-font:minor-latin;\nmso-fareast-font-family:Calibri;\nmso-fareast-theme-font:minor-latin;\nmso-hansi-font-family:Calibri;\nmso-hansi-theme-font:minor-latin;\nmso-bidi-font-family:\"Times New Roman\";\nmso-bidi-theme-font:minor-bidi;\nmso-fareast-language:EN-US;}\n.MsoPapDefault\n{mso-style-type:export-only;\nmargin-bottom:10.0pt;\nline-height:115%;}\n@page WordSection1\n{size:612.0pt 792.0pt;\nmargin:70.85pt 3.0cm 70.85pt 3.0cm;\nmso-header-margin:36.0pt;\nmso-footer-margin:36.0pt;\nmso-paper-source:0;}\ndiv.WordSection1\n{page:WordSection1;}\n-->\n</style>\n"
			+ "<style>\n/* Style Definitions */\ntable.MsoNormalTable\n{mso-style-name:\"Tabela normal\";\nmso-tstyle-rowband-size:0;\nmso-tstyle-colband-size:0;\nmso-style-noshow:yes;\nmso-style-priority:99;\nmso-style-parent:\"\";\nmso-padding-alt:0cm 5.4pt 0cm 5.4pt;\nmso-para-margin-top:0cm;\nmso-para-margin-right:0cm;\nmso-para-margin-bottom:10.0pt;\nmso-para-margin-left:0cm;\nline-height:115%;\nmso-pagination:widow-orphan;\nfont-size:11.0pt;\nfont-family:\"Calibri\",sans-serif;\nmso-ascii-font-family:Calibri;\nmso-ascii-theme-font:minor-latin;\nmso-hansi-font-family:Calibri;\nmso-hansi-theme-font:minor-latin;\nmso-fareast-language:EN-US;}\n</style>\n"
			+ "<xml>\n<o:shapedefaults v:ext=\"edit\" spidmax=\"1034\"/>\n</xml>\n</head>\n"
			+ "<body lang=PT-BR style='tab-interval:35.4pt'>\n<v:group id=\"Tela_x0020_38\" o:spid=\"_x0000_s1026\" editas=\"canvas\" style='width:425.2pt;height:248.05pt;mso-position-horizontal-relative:char;mso-position-vertical-relative:line' coordsize=\"54000,31502\">\n<v:shapetype id=\"_x0000_t75\" coordsize=\"21600,21600\" o:spt=\"75\" o:preferrelative=\"t\" path=\"m@4@5l@4@11@9@11@9@5xe\" filled=\"f\" stroked=\"f\">\n<v:stroke joinstyle=\"miter\"/>\n<v:formulas>\n<v:f eqn=\"if lineDrawn pixelLineWidth 0\"/>\n<v:f eqn=\"sum @0 1 0\"/>\n<v:f eqn=\"sum 0 0 @1\"/>\n<v:f eqn=\"prod @2 1 2\"/>\n<v:f eqn=\"prod @3 21600 pixelWidth\"/>\n<v:f eqn=\"prod @3 21600 pixelHeight\"/>\n<v:f eqn=\"sum @0 0 1\"/>\n<v:f eqn=\"prod @6 1 2\"/>\n<v:f eqn=\"prod @7 21600 pixelWidth\"/>\n<v:f eqn=\"sum @8 21600 0\"/>\n<v:f eqn=\"prod @7 21600 pixelHeight\"/>\n<v:f eqn=\"sum @10 21600 0\"/>\n</v:formulas>\n<v:path o:extrusionok=\"f\" gradientshapeok=\"t\" o:connecttype=\"rect\"/>\n<o:lock v:ext=\"edit\" aspectratio=\"t\"/>\n</v:shapetype>\n<v:shape id=\"_x0000_s1027\" type=\"#_x0000_t75\" style='position:absolute; width:54000;height:31502;visibility:visible;mso-wrap-style:square'>\n<v:fill o:detectmouseclick=\"t\"/>\n<v:path o:connecttype=\"none\"/>\n</v:shape>\n",
			END = "<w:wrap type=\"none\"/>\n<w:anchorlock/>\n</v:group>\n</body>\n</html>";

	public static String toVML(Collection<? extends ExtDrawable> objs, String foreground, String background) {
		StringBuilder out = new StringBuilder(START);

		// primeiro o que vai atrás
		if (background != null)
			out.append(background);

		// objetos
		for (ExtDrawable obj : objs)
			out.append(obj.getVML());

		// por último o que vai na frente
		if (foreground != null)
			out.append(foreground);

		out.append(END);

		return out.toString();
	}

	// ========================== LISTA DE INSTRUÇÕES ==========================

	// Instruções -> VML

	/**
	 * Função que gera, a partir de uma {@link LID.DrawAction lista de instruções de
	 * desenho}, o código VML correspondente.
	 * 
	 * @param insts lista de instruções
	 * @return código VML
	 */
	public static String getVML(List<Object[]> insts) {
		if (insts == null)
			return null;
		StringBuilder out = new StringBuilder();
		for (Object[] d : insts)
			out.append(LID.DrawAction.getVML(d));
		return out.toString();
	}
}
