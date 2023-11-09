package CovaMonstre.controlador;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import CovaMonstre.Notificar;
import CovaMonstre.modelo.BC;
import CovaMonstre.modelo.Datos;

/**
 *
 * @author juant
 */
public class Agente implements Notificar {

    private Datos dat;
    private Notificar prog;
    private boolean start;
    private int delay;

    public Agente(Datos dat, Notificar not) {
        this.dat = dat;
        this.prog = not;
        this.start = true;
        this.delay = 200;
    }

    public void resolver() {

        BC baseConocimiento = new BC();

        while (start) {
            int percep[] = new int[5]; // [Hedor, Brisa, Resplandor, Golpe, Gemido]
            percep = obtenerPercepciones(percep);
            int[] X = baseConocimiento.Percepcion_Caracteristica(percep);
            String accion = baseConocimiento.getAccion(baseConocimiento.Caracteristica_Accion(X, 0));

            // Realizar dicho movimiento
            switch (accion) {
                case "NORTE":
                    //dat.setDir(1);
                    baseConocimiento.setDir(1); // dirección norte
                    prog.notificar("repaint");
                    break;
                case "SUR":
                    //dat.setDir(3);
                    baseConocimiento.setDir(3);
                    prog.notificar("repaint");
                    break;
                case "ESTE":
                    //dat.setDir(2);
                    baseConocimiento.setDir(2);
                    prog.notificar("repaint");
                    break;
                case "OESTE":
                    //dat.setDir(4);
                    baseConocimiento.setDir(4);
                    prog.notificar("repaint");
                    break;
                case "DER": //girar 90º a la derecha
                    break;
                case "IZQ": //girar 90º a la izquierda
                    break;
                case "STOP":
                    break;
                default:
                    break;
            }
            esperar(delay);
        }
    }

    public int hayMuro(int x, int y, int offsetX, int offsetY) {
        int valor = 1;
        if ((x + offsetX > -1 && y + offsetY > -1)
                && (x + offsetX < dat.getDimension() && y + offsetY < dat.getDimension())) {
            valor = dat.getTablero()[x + offsetX][y + offsetY];
        }
        return valor;
    }

    public int[] obtenerPercepciones(int p[]){
        //hacer un switch de la casilla del agente con los posibles estados y rellenar binariamente las percep
        return p;
    }

    public void esperar(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notificar(String s) {

        switch (s) {
            case "START":
                start = true;
                ProcesoResolver calculador = new ProcesoResolver(this);
                calculador.start();
                break;
            case "STOP":
                start = false;
                break;
            case "upVelocidad": // velocidad++
                if (this.delay > 0) {
                    this.delay = this.delay - 50;
                }
                break;
            case "downVelocidad": // velocidad--
                this.delay = this.delay + 50;
                break;
        }
    }
}
