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

    public Agente(Datos dat, Notificar not) {
        this.dat = dat;
        this.prog = not;
        start = true;
    }

    public void resolver() {

        BC agent = new BC();

        while (start) {
            // 1- Get posicion del robot
            int[] posRobot = dat.getPosRobot();
            // 2- Get percepciones de las 8 casillas
            int w1 = hayMuro(posRobot[0], posRobot[1], -1, -1);
            int w2 = hayMuro(posRobot[0], posRobot[1], -1, 0);
            int w3 = hayMuro(posRobot[0], posRobot[1], -1, 1);
            int w4 = hayMuro(posRobot[0], posRobot[1], 0, 1);
            int w5 = hayMuro(posRobot[0], posRobot[1], 1, 1);
            int w6 = hayMuro(posRobot[0], posRobot[1], 1, 0);
            int w7 = hayMuro(posRobot[0], posRobot[1], 1, -1);
            int w8 = hayMuro(posRobot[0], posRobot[1], 0, -1);
            // 3- Get estado interno del agente <=> su dirección
            int dir = agent.getDir();
            // 4- Consultar al BC
            int[] percep = { w1, w2, w3, w4, w5, w6, w7, w8, dir };
            // Llamar al percepcion_accion para saber que movimiento realizar
            String accion = agent.getAccion(agent.Percepcion_Accion(percep));

            // Realizar dicho movimiento
            switch (accion) {
                case "NORTE":
                    dat.getTablero()[posRobot[0]][posRobot[1]] = 0; // poscion actual del robot a 0
                    dat.getTablero()[posRobot[0] - 1][posRobot[1]] = 2; // nueva posicion del robot
                    dat.setDir(1);
                    agent.setDir(1); // dirección norte
                    prog.notificar("repaint");
                    break;
                case "SUR":
                    dat.getTablero()[posRobot[0]][posRobot[1]] = 0;
                    dat.getTablero()[posRobot[0] + 1][posRobot[1]] = 2;
                    dat.setDir(3);
                    agent.setDir(3);
                    prog.notificar("repaint");
                    break;
                case "ESTE":
                    dat.getTablero()[posRobot[0]][posRobot[1]] = 0;
                    dat.getTablero()[posRobot[0]][posRobot[1] + 1] = 2;
                    dat.setDir(2);
                    agent.setDir(2);
                    prog.notificar("repaint");
                    break;
                case "OESTE":
                    dat.getTablero()[posRobot[0]][posRobot[1]] = 0;
                    dat.getTablero()[posRobot[0]][posRobot[1] - 1] = 2;
                    dat.setDir(4);
                    agent.setDir(4);
                    prog.notificar("repaint");
                    break;
                case "STOP":
                    break;
                default:
                    break;
            }
            esperar(100);
        }
    }

    public int hayMuro(int x, int y, int offsetX, int offsetY) {

        int valor = 1;

        // Comprobamos que el pos + offset esta dentro del tablero si no lo esta quiere
        // decir que estamos
        // mirando fuera del tablero
        if ((x + offsetX > -1 && y + offsetY > -1)
                && (x + offsetX < dat.getDimension() && y + offsetY < dat.getDimension())) {
            valor = dat.getTablero()[x + offsetX][y + offsetY];
        }

        return valor;
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
        }
    }
}
