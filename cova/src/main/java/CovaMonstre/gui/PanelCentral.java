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
    private BufferedImage robotImage, paredImage, mounstruoImage, agenteImage, 
                          precipicioImage, tesoroImage,hedorImage,brisaImage,
                          hedorBrisaImage, hedorTesoroImage, brisaTesoroImage,
                          hedorBrisaTesoroImage,
                          agenteTesoroImage, 
                            agenteHedorImage, 
                            agenteBrisaImage, 
                            agenteHedorBrisaImage, 
                            agenteTesoroBrisaImage, 
                            agenteTesoroHedorImage ,
                            agenteTesoroHedorBrisaImage ;

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
            hedorImage = ImageIO.read(getClass().getResource("../imagenes/hedor.png"));
            brisaImage = ImageIO.read(getClass().getResource("../imagenes/brisa.png"));

            hedorBrisaImage = ImageIO.read(getClass().getResource("../imagenes/hedorBrisa.png"));
            hedorTesoroImage = ImageIO.read(getClass().getResource("../imagenes/hedorTesoro.png"));
            brisaTesoroImage = ImageIO.read(getClass().getResource("../imagenes/brisaTesoro.png"));
            hedorBrisaTesoroImage = ImageIO.read(getClass().getResource("../imagenes/hedorBrisaTesoro.png"));

            agenteTesoroImage = ImageIO.read(getClass().getResource("../imagenes/agenteTesoro.png"));
            agenteHedorImage = ImageIO.read(getClass().getResource("../imagenes/agenteHedor.png"));
            agenteBrisaImage = ImageIO.read(getClass().getResource("../imagenes/agenteBrisa.png"));
            agenteHedorBrisaImage = ImageIO.read(getClass().getResource("../imagenes/agenteHedorBrisa.png"));
            agenteTesoroBrisaImage = ImageIO.read(getClass().getResource("../imagenes/agenteTesoroBrisa.png"));
            agenteTesoroHedorImage = ImageIO.read(getClass().getResource("../imagenes/agenteTesoroHedor.png"));
            agenteTesoroHedorBrisaImage = ImageIO.read(getClass().getResource("../imagenes/agenteTesoroHedorBrisa.png"));

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

                switch (modo) {
                    case 1: //monstruo
                        if (SwingUtilities.isLeftMouseButton(e)) { //clic izq
                            tablero[fila][columna] = 1;
                            //Añadir hedor - 9
                            if(tablero[fila][columna+1] == 10){
                                tablero[fila][columna+1] = 11;
                            }else if(tablero[fila][columna+1] == 8){
                                tablero[fila][columna+1] = 12;
                            }else if(tablero[fila][columna+1] == 13){
                                tablero[fila][columna+1] = 14;
                            }else if(tablero[fila][columna+1] == 0){
                                tablero[fila][columna+1] = 9;
                            }

                            if(tablero[fila][columna-1] == 10){
                                tablero[fila][columna-1] = 11;
                            }else if(tablero[fila][columna-1] == 8){
                                tablero[fila][columna-1] = 12;
                            }else if(tablero[fila][columna-1] == 13){
                                tablero[fila][columna-1] = 14;
                            }else if(tablero[fila][columna-1] == 0){
                                tablero[fila][columna-1] = 9;
                            }

                            if(tablero[fila+1][columna] == 10){
                                tablero[fila+1][columna] = 11;
                            }else if(tablero[fila+1][columna] == 8){
                                tablero[fila+1][columna] = 12;
                            }else if(tablero[fila+1][columna] == 13){
                                tablero[fila+1][columna] = 14;
                            }else if(tablero[fila+1][columna] == 0){
                                tablero[fila+1][columna] = 9;
                            }

                            if(tablero[fila-1][columna] == 10){
                                tablero[fila-1][columna] = 11;
                            }else if(tablero[fila-1][columna] == 8){
                                tablero[fila-1][columna] = 12;
                            }else if(tablero[fila-1][columna] == 13){
                                tablero[fila-1][columna] = 14;
                            }else if(tablero[fila-1][columna] == 0){
                                tablero[fila-1][columna] = 9;
                            }

                        } else if (SwingUtilities.isRightMouseButton(e)) { //clic derecho
                            tablero[fila][columna] = 0;
                            //quitar hedor
                            //JTP comprobar ambiente
                            tablero[fila][columna+1] = 0;
                            tablero[fila][columna-1] = 0;
                            tablero[fila+1][columna] = 0;
                            tablero[fila-1][columna] = 0;
                        }
                        break;
                    case 7: //precicicio
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            tablero[fila][columna] = 7;
                            //añadir brisas - 10
                            if(tablero[fila][columna+1] == 9){
                                tablero[fila][columna+1] = 11;
                            }else if(tablero[fila][columna+1] == 8){
                                tablero[fila][columna+1] = 13;
                            }else if(tablero[fila][columna+1] == 12){
                                tablero[fila][columna+1] = 14;
                            }else if(tablero[fila][columna+1] == 0){
                                tablero[fila][columna+1] = 10;
                            }

                            if(tablero[fila][columna-1] == 9){
                                tablero[fila][columna-1] = 11;
                            }else if(tablero[fila][columna-1] == 8){
                                tablero[fila][columna-1] = 13;
                            }else if(tablero[fila][columna-1] == 12){
                                tablero[fila][columna-1] = 14;
                            }else if(tablero[fila][columna-1] == 0){
                                tablero[fila][columna-1] = 10;
                            }

                            if(tablero[fila+1][columna] == 9){
                                tablero[fila+1][columna] = 11;
                            }else if(tablero[fila+1][columna] == 8){
                                tablero[fila+1][columna] = 13;
                            }else if(tablero[fila+1][columna] == 12){
                                tablero[fila+1][columna] = 14;
                            }else if(tablero[fila+1][columna] == 0){
                                tablero[fila+1][columna] = 10;
                            }

                            if(tablero[fila-1][columna] == 9){
                                tablero[fila-1][columna] = 11;
                            }else if(tablero[fila-1][columna] == 8){
                                tablero[fila-1][columna] = 13;
                            }else if(tablero[fila-1][columna] == 12){
                                tablero[fila-1][columna] = 14;
                            }else if(tablero[fila-1][columna] == 0){
                                tablero[fila-1][columna] = 10;
                            }
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            tablero[fila][columna] = 0;
                            //quitar brisas 
                            //JTP comprobar ambiente
                            tablero[fila][columna+1] = 0;
                            tablero[fila][columna-1] = 0;
                            tablero[fila+1][columna] = 0;
                            tablero[fila-1][columna] = 0;
                        }
                        break;
                    case 8: //tesoro
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            if(tablero[fila][columna] == 0){
                                tablero[fila][columna] = 8;
                            }else if(tablero[fila][columna] == 9){
                                tablero[fila][columna] = 12;
                            }else if(tablero[fila][columna] == 10){
                                tablero[fila][columna] = 13;
                            }else if(tablero[fila][columna] == 11){
                                tablero[fila][columna] = 14;
                            }
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            tablero[fila][columna] = 0;
                        }
                        break;
                    default:
                        // Manejar otros modos si es necesario
                        break;
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

                //dibujar monstruo
                if (tablero[fila][columna] == 1) {
                    g.setComposite(opacidad2);
                    g.drawImage(mounstruoImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);

                //dibujar agente
                } else if (tablero[fila][columna] == 4) {
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
                    g2d.drawImage(agenteImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
                    // Restaurar la transformación para evitar que afecte a otras operaciones de
                    // dibujo
                    g2d.setTransform(new AffineTransform());

                //Precipicio
                } else if (tablero[fila][columna] == 7) {
                    g.setComposite(opacidad2);
                    g.drawImage(precipicioImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
                //Tesoro
                } else if (tablero[fila][columna] == 8) {
                    g.setComposite(opacidad2);
                    g.drawImage(tesoroImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
                //HEDOR 
                } else if (tablero[fila][columna] == 9) {
                    g.setComposite(opacidad2);
                    g.drawImage(hedorImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
                //Brisa
                } else if (tablero[fila][columna] == 10) {
                    g.setComposite(opacidad2);
                    g.drawImage(brisaImage, x + 25, y + 25, anchoCelda - 50, altoCelda - 50, null);
                //Hedor y Brisa 
                } else if (tablero[fila][columna] == 11) {
                    g.setComposite(opacidad2);
                    g.drawImage(hedorBrisaImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
                //HEDOR y Tesoro 
                } else if (tablero[fila][columna] == 12) {
                    g.setComposite(opacidad2);
                    g.drawImage(hedorTesoroImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
                //Brisa y Tesoro 
                } else if (tablero[fila][columna] == 13) {
                    g.setComposite(opacidad2);
                    g.drawImage(brisaTesoroImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
                //HEDOR y Brisa y Tesoro 
                } else if (tablero[fila][columna] == 14) {
                    g.setComposite(opacidad2);
                    g.drawImage(hedorBrisaTesoroImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
                }


                //Agente y Tesoro
                    g.setComposite(opacidad2);
                    g.drawImage(agenteTesoroImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
                //Agente y Hedor
                    g.setComposite(opacidad2);
                    g.drawImage(agenteHedorImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
                //Agente y Brisa
                    g.setComposite(opacidad2);
                    g.drawImage(agenteBrisaImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
                //Agente y Hedor y Brisa
                    g.setComposite(opacidad2);
                    g.drawImage(agenteHedorBrisaImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
                //Agente y Tesoro y Brisa
                    g.setComposite(opacidad2);
                    g.drawImage(agenteTesoroBrisaImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
                //Agente y Tesoro y Hedor
                    g.setComposite(opacidad2);
                    g.drawImage(agenteTesoroHedorImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
                //Agente y Tesoro y Hedor y Brisa
                    g.setComposite(opacidad2);
                    g.drawImage(agenteTesoroHedorBrisaImage, x + 1, y + 1, anchoCelda - 1, altoCelda - 1, null);
            }
        }
        gr.drawImage(bi, 0, 0, this);
    }

    
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
