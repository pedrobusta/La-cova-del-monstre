package CovaMonstre.controlador;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import CovaMonstre.Notificar;
import CovaMonstre.modelo.Agente;
import CovaMonstre.modelo.Datos;

/**
 *
 * @author juant
 */
public class Controlador implements Notificar {

    private Datos dat;
    private Notificar prog;

    public Controlador(Datos dat, Notificar not) {
        this.dat = dat;
        this.prog = not;
    }

    public void resolver() {
        // System.out.println("init resolver");

        Agente agent = new Agente();
        String accion = "NOMEM";
        while (true) {
            // Get posicion del robot
            int[] posRobot = dat.getPosRobot();
            // System.out.println(posRobot[0] + " ," + posRobot[1]);
            // Actualizar sus percepciones
            // int w1 = dat.getTablero()[posRobot[0] - 1][posRobot[1] - 1];
            int w1 = hayMuro(posRobot[0], posRobot[1], -1, -1);
            // int w2 = dat.getTablero()[posRobot[0] - 1][posRobot[1]];
            int w2 = hayMuro(posRobot[0], posRobot[1], -1, 0);
            // int w3 = dat.getTablero()[posRobot[0] - 1][posRobot[1] + 1];
            int w3 = hayMuro(posRobot[0], posRobot[1], -1, 1);
            // int w4 = dat.getTablero()[posRobot[0]][posRobot[1] + 1];
            int w4 = hayMuro(posRobot[0], posRobot[1], 0, 1);
            // int w5 = dat.getTablero()[posRobot[0] + 1][posRobot[1] + 1];
            int w5 = hayMuro(posRobot[0], posRobot[1], 1, 1);
            // int w6 = dat.getTablero()[posRobot[0] + 1][posRobot[1]];
            int w6 = hayMuro(posRobot[0], posRobot[1], 1, 0);
            // int w7 = dat.getTablero()[posRobot[0] + 1][posRobot[1] - 1];
            int w7 = hayMuro(posRobot[0], posRobot[1], 1, -1);
            // int w8 = dat.getTablero()[posRobot[0]][posRobot[1] - 1];
            int w8 = hayMuro(posRobot[0], posRobot[1], 0, -1);
            int[] percep = { w1, w2, w3, w4, w5, w6, w7, w8 };

            // ERROR! Estamos haciendo dos veces que el agente nos devuelva una acción se
            // eliminara la primera 1-11-23 ~ pedro

            // Llamar al percepcion_accion para saber que movimiento realizar
            // agent.Percepcion_Accion(percep);
            // System.out.println(agent.Percepcion_Accion(percep));
            
            accion = agent.getAccion(agent.Percepcion_Accion(percep,accion));

            // Realizar dicho movimiento
            switch (accion) {
                case "NORTE":
                    // .out.println("mov -> arriba");
                    dat.getTablero()[posRobot[0]][posRobot[1]] = 0;
                    dat.getTablero()[posRobot[0] - 1][posRobot[1]] = 2;
                    prog.notificar("repaint");
                    break;
                case "SUR":
                    // System.out.println("mov -> abajo");
                    dat.getTablero()[posRobot[0]][posRobot[1]] = 0;
                    dat.getTablero()[posRobot[0] + 1][posRobot[1]] = 2;
                    prog.notificar("repaint");
                    break;
                case "ESTE":
                    // System.out.println("mov -> derecha");
                    dat.getTablero()[posRobot[0]][posRobot[1]] = 0;
                    dat.getTablero()[posRobot[0]][posRobot[1] + 1] = 2;
                    prog.notificar("repaint");

                    break;
                case "OESTE":
                    // System.out.println("mov -> izquierda");
                    dat.getTablero()[posRobot[0]][posRobot[1]] = 0;
                    dat.getTablero()[posRobot[0]][posRobot[1] - 1] = 2;
                    prog.notificar("repaint");
                    break;
                default:
                    break;
            }
            esperar(1000);

        }

        // prog.notificar("repaint");
    }

    public int hayMuro(int x, int y, int offsetX, int offsetY) {

        int valor = 1;

        // Comprobamos que el pos + offset esta dentro del tablero si no lo esta quiere decir que estamos
        // mirando fuera del tablero 
        if((x+offsetX > -1 && y+offsetY > -1) && (x+offsetX < 10 && y+offsetY < 10)){
            valor = dat.getTablero()[x+offsetX][y+offsetY];
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
                resolver();
                break;
        }
    }
}
