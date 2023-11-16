package CovaMonstre.controlador;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import CovaMonstre.Notificar;
import CovaMonstre.modelo.Conocimientos;
import CovaMonstre.modelo.Datos;

/**
 *
 * @author juant
 */
public class Agente implements Notificar {
    // Atributos
    private Datos dat;
    private Notificar prog;
    private boolean start;
    private int delay;
    // BC es un tablero de Conocimientos, cada conocimiento es estado de cada
    // casilla
    Conocimientos[][] BC = new Conocimientos[dat.getDimension()][dat.getDimension()];

    private boolean encontradoTesoro = false;

    // Constructor
    public Agente(Datos dat, Notificar not) {
        this.dat = dat;
        this.prog = not;
        this.start = true;
        this.delay = 200;
    }

    // Cerebro
    public void resolver() {

        // HAY que poner que las casillas principales son OK
        BC[dat.getDimension() - 1][0].setOk(true);
        BC[dat.getDimension() - 1][0].setVisitada(true);
        ;
        BC[dat.getDimension() - 1][1].setOk(true);
        BC[dat.getDimension() - 2][0].setOk(true);
        int percep[] = new int[5]; // [Hedor, Brisa, Resplandor, Golpe, Gemido]

        int agenteX = 0;
        int agenteY = 0;

        while (start && !encontradoTesoro) {
            // 1- Obtenemos el array de percepciones
            percep = obtenerPercepciones(percep, agenteX, agenteY);

            // 2- Actualizar y inferir BC
            informarBC(percep, agenteX, agenteY);

            // 3- Preguntar BC que acción debe hacer
            String accion = preguntarBC(agenteX, agenteY);

            // 4- Realizar dicho movimiento
            switch (accion) {
                case "NORTE":
                    //
                    prog.notificar("repaint");
                    break;
                case "SUR":
                    //
                    prog.notificar("repaint");
                    break;
                case "ESTE":
                    //
                    prog.notificar("repaint");
                    break;
                case "OESTE":
                    //
                    prog.notificar("repaint");
                    break;
                case "STOP":
                    break;
                default:
                    break;
            }
            esperar(delay);
        }

    }

    public int[] obtenerPercepciones(int p[], int agenteX, int agenteY) {
        // Reset las percepciones Excepto Gemido !!!
        for (int i = 0; i < p.length - 1; i++) {
            p[i] = 0;
        }
        // Ha matado monstruo y cuantos
        p[4] = dat.getNumMonstruoInit() - dat.getNumMonstruoActual();

        // Miramos si el agente se golpea con un muro
        if (hayMuro(agenteX, agenteY, 0, 0) == -1) {
            p[3] = 1;
        } else {
            switch (dat.getTablero()[agenteX][agenteY]) {
                case 9:
                    // hedor
                    p[0] = 1;
                    break;
                case 10:
                    // brisa
                    p[1] = 1;
                    break;
                case 11:
                    // hedor ^ brisa
                    p[0] = 1;
                    p[1] = 1;
                    break;
                case 12:
                    // hedor ^ tesoro
                    p[0] = 1;
                    p[2] = 1;
                    break;
                case 13:
                    // brisa ^ tesoro
                    p[1] = 1;
                    p[2] = 1;
                    break;
                case 14:
                    // hedor ^ brisa ^ tesoro
                    p[0] = 1;
                    p[1] = 1;
                    p[2] = 1;
                default:
                    break;
            }
        }
        return p;
    }

    public void informarBC(int p[], int agenteX, int agenteY) {
        // TODO

        // Si no hemos visitado esa casilla generamos creencias
        if (!(BC[agenteX][agenteY].isVisitada())) {
            // Generamos creencias sobre posibles precipicios
            if (p[1] == 1) {
                generarCreenciasBrisa(agenteX, agenteY);
            }
            // Generamos creencias sobre posibles mounstruos
            if (p[0] == 1) {
                generarCreenciasHedor(agenteX, agenteY);
            }
        }

        // Miramos si con el conocimiento que tenemos podemos inferir algo
        // El agente se encuentra en [agenteX,agenteY] miraremos la disponibilidad

        // [agenteX + 1] es OK?
        if (casillaOK(agenteX, agenteY, 1, 0)) {
            BC[agenteX + 1][agenteY].setOk(true);
        }

        // [agenteX -1] es OK?
        if (casillaOK(agenteX, agenteY, -1, 0)) {
            BC[agenteX - 1][agenteY].setOk(true);
        }

        // [agenteY + 1] es OK?
        if (casillaOK(agenteX, agenteY, 0, 1)) {
            BC[agenteX][agenteY + 1].setOk(true);
        }

        // [agenteY - 1] es OK?
        if (casillaOK(agenteX, agenteY, 0, -1)) {
            BC[agenteX][agenteY - 1].setOk(true);
        }
    }

    public String preguntarBC(int x, int y) {
        // TODO

        return "";
    }

    public boolean casillaOK(int agenteX, int agenteY, int offsetX, int offsetY) {
        boolean ok = true;
        if (BC[agenteX + offsetX][agenteY + offsetY].posibleMonstruo() ||
                BC[agenteX + offsetX][agenteY + offsetY].posiblePrecipicio()) {
            ok = false;
        }
        return ok;
    }

    public void generarCreenciasBrisa(int agenteX, int agenteY) {
        // Posibles precipicios (hay que mirar si esta dentro del trablero)
        BC[agenteX][agenteY].setBrisa(true);

        // Posible precipicio en agenteX - 1
        if ((hayMuro(agenteX, agenteY, -1, 0) != -1) && (!BC[agenteX - 1][agenteY].isVisitada())) {
            BC[agenteX - 1][agenteY].setPosiblePrecipicio(true);
        }

        // Posible precipicio en agenteX + 1
        if ((hayMuro(agenteX, agenteY, 1, 0) != -1) && (!BC[agenteX + 1][agenteY].isVisitada())) {
            BC[agenteX + 1][agenteY].setPosiblePrecipicio(true);
        }

        // Posible precipicio en agenteY - 1
        if ((hayMuro(agenteX, agenteY, 0, -1) != -1) && (!BC[agenteX][agenteY - 1].isVisitada())) {
            BC[agenteX][agenteY - 1].setPosiblePrecipicio(true);
        }

        // Posible precipicio en agenteY + 1
        if ((hayMuro(agenteX, agenteY, 0, 1) != -1) && (!BC[agenteX][agenteY + 1].isVisitada())) {
            BC[agenteX][agenteY + 1].setPosiblePrecipicio(true);
        }
    }

    public void generarCreenciasHedor(int agenteX, int agenteY) {
        // Posibles monstruos (hay que mirar si esta dentro del trablero)
        BC[agenteX][agenteY].setHedor(true);

        // Posible monstruo en agenteX - 1
        if ((hayMuro(agenteX, agenteY, -1, 0) != -1) && (!BC[agenteX - 1][agenteY].isVisitada())) {
            BC[agenteX - 1][agenteY].setPosibleMonstruo(true);
        }

        // Posible monstruo en agenteX + 1
        if ((hayMuro(agenteX, agenteY, 1, 0) != -1) && (!BC[agenteX + 1][agenteY].isVisitada())) {
            BC[agenteX + 1][agenteY].setPosibleMonstruo(true);
        }

        // Posible monstruo en agenteY - 1
        if ((hayMuro(agenteX, agenteY, 0, -1) != -1) && (!BC[agenteX][agenteY - 1].isVisitada())) {
            BC[agenteX][agenteY - 1].setPosibleMonstruo(true);
        }

        // Posible monstruo en agenteY + 1
        if ((hayMuro(agenteX, agenteY, 0, 1) != -1) && (!BC[agenteX][agenteY + 1].isVisitada())) {
            BC[agenteX][agenteY + 1].setPosibleMonstruo(true);
        }
    }

    public void esperar(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int hayMuro(int x, int y, int offsetX, int offsetY) {
        int valor = -1;
        if ((x + offsetX > -1 && y + offsetY > -1)
                && (x + offsetX < dat.getDimension() && y + offsetY < dat.getDimension())) {
            valor = dat.getTablero()[x + offsetX][y + offsetY];
        }
        return valor;
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
