package br.com.pereiraeng.drawing;

/**
 * Interface das classes que representam objetos que podem ser desenhados
 * externamente
 * 
 * @author Philipe PEREIRA
 *
 */
public interface ExtDrawable {

	public String getTikz();

	public String getSVG();

	public String getVML();
}
