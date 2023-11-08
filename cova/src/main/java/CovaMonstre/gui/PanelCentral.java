package CovaMonstre.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.TexturePaint;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import CovaMonstre.modelo.Datos;

public class PanelCentral extends JPanel {

    private Datos dat;
    private int[][] tablero;
    private int modo = 0; // 0-nada , 1-pared, 2-robot

    private BufferedImage textureImage;
    private BufferedImage robotImage, paredImage, mounstruoImage, agenteImage, precipicioImage, tesoroImage;
    private int filaGlobal;
    private int columnaGlobal;

    public PanelCentral(Datos dat) {
        this.dat = dat;
        this.tablero = dat.getTablero();

        try {
            textureImage = ImageIO.read(getClass().getResource("../imagenes/texturaMarmol1.jpg"));
            robotImage = ImageIO.read(getClass().getResource("../imagenes/roomba.png"));
            paredImage = ImageIO.read(getClass().getResource("../imagenes/ladrillo.jpg"));
            
            agenteImage= ImageIO.read(getClass().getResource("../imagenes/agenteFlecha.png"));
            mounstruoImage = ImageIO.read(getClass().getResource("../imagenes/monstruo.png"));
            precipicioImage = ImageIO.read(getClass().getResource("../imagenes/precipicio.png"));
            tesoroImage = ImageIO.read(getClass().getResource("../imagenes/tesoro.png"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Calcular la posición de la casilla en la que se hizo clic
                int fila = e.getY() / (getHeight() / dat.getDimension());
                int columna = e.getX() / (getWidth() / dat.getDimension());

                if (modo == 1) { // Modo Pared activado
                    // Imprimir la posición en la consola
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        tablero[fila][columna] = 1;
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        tablero[fila][columna] = 0;
                    }
                    repaint();
                }
            }
        });

        // Agregar el MouseListener al PanelCentral
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                // Calcular la posición de la casilla en la que se hizo clic
                int fila = e.getY() / (getHeight() / dat.getDimension());
                int columna = e.getX() / (getWidth() / dat.getDimension());
                if (modo == 1) { // modo Pared activado
                    // Imprimir la posición en la consola
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        System.out.println("Clic Izquierdo en fila " + fila + ", columna " + columna);
                        tablero[fila][columna] = 1;
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        System.out.println("Clic Derecho en fila " + fila + ", columna " + columna);
                        tablero[fila][columna] = 0;
                    }
                } else if (modo == 2) {
                    // Imprimir la posición en la consola
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        System.out.println("Clic Izquierdo en fila " + fila + ", columna " + columna);
                        tablero[fila][columna] = 2;
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        System.out.println("Clic Derecho en fila " + fila + ", columna " + columna);
                        tablero[fila][columna] = 0;
                    }
                } else if (modo==7) {
                  if (SwingUtilities.isLeftMouseButton(e)) {
                        tablero[fila][columna] = 7;
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        tablero[fila][columna] = 0;
                    }
                } else if (modo==8) {
                  if (SwingUtilities.isLeftMouseButton(e)) {
                        tablero[fila][columna] = 8;
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        tablero[fila][columna] = 0;
                    }
                }
                repaint();
            }
        });
    }

    @Override
    public void paint(Graphics gr) {

        AlphaComposite opacidad = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
        AlphaComposite opacidad2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);

        int anchoCelda = getWidth() / dat.getDimension();
        int altoCelda = getHeight() / dat.getDimension();
        this.tablero = dat.getTablero();
        Rectangle rect = new Rectangle(0, 0, anchoCelda, altoCelda);

        BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        Graphics2D gInferior = (Graphics2D) bi.getGraphics();

        g.setComposite(opacidad);
        g.setPaint(new TexturePaint(textureImage, rect));
        gInferior.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        // Dibujar el tablero
        for (int fila = 0; fila < dat.getDimension(); fila++) {
            for (int columna = 0; columna < dat.getDimension(); columna++) {
                int x = columna * anchoCelda;
                int y = fila * altoCelda;

                g.setColor(Color.BLACK);
                g.drawRect(x, y, anchoCelda, altoCelda);

                if (tablero[fila][columna] == 1) {
                    g.setComposite(opacidad2);
                    g.drawImage(mounstruoImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);

                } else if (tablero[fila][columna] == 2) {
                    g.setComposite(opacidad2);
                    // Crear una transformación para rotar la imagen 90 grados
                    AffineTransform at = new AffineTransform();

                    switch (dat.getDir()) {
                        case (2): // derecha
                            at.rotate(Math.toRadians(90), x + anchoCelda / 2.0, y + altoCelda / 2.0);
                            break;
                        case (3): // abajo
                            at.rotate(Math.toRadians(180), x + anchoCelda / 2.0, y + altoCelda / 2.0);
                            break;
                        case (4): // izquierda
                            at.rotate(Math.toRadians(270), x + anchoCelda / 2.0, y + altoCelda / 2.0);
                            break;
                        default:
                            break;
                    }
                    // Aplicar la transformación antes de dibujar la imagen
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setTransform(at);
                    // Dibujar la imagen rotada
                    g2d.drawImage(robotImage, x + 5, y + 5, anchoCelda - 10, altoCelda - 10, null);
                    // Restaurar la transformación para evitar que afecte a otras operaciones de
                    // dibujo
                    g2d.setTransform(new AffineTransform());
                } else if (tablero[fila][columna] == 7) {
                    g.setComposite(opacidad2);
                    g.drawImage(precipicioImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
                } else if (tablero[fila][columna] == 8) {
                    g.setComposite(opacidad2);
                    g.drawImage(tesoroImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
                } else if (tablero[fila][columna] == 4) {
                    g.setComposite(opacidad2);
                    g.drawImage(agenteImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
                }
            }
        }
        gr.drawImage(bi, 0, 0, this);
    }

    // Modo pared o robot
    public void setModo(int num) {
        this.modo = num;
    }

    @Override
    public void repaint() {
        if (this.getGraphics() != null) {
            paint(this.getGraphics());
        }
    }
}
