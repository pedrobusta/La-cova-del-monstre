package CovaMonstre.controlador;

import java.util.ArrayList;

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
    private Conocimientos[][] BC;

    private boolean encontradoTesoro = false;
    private int agenteX;
    private int agenteY;

    private ArrayList<String> camino = new ArrayList<>(); // conjutos de acciones realizado

    // Constructor
    public Agente(Datos dat, Notificar not) {
        this.dat = dat;
        this.prog = not;
        this.start = true;
        this.delay = 200;

        this.agenteX = dat.getDimension() - 1;
        this.agenteY = 0;

        this.BC = new Conocimientos[dat.getDimension()][dat.getDimension()];
        for (int i = 0; i < dat.getDimension(); i++) {
            for (int j = 0; j < dat.getDimension(); j++) {
                BC[i][j] = new Conocimientos();
            }
        }
    }

    // Cerebro
    public void resolver() {

        int percep[] = new int[5]; // [Hedor, Brisa, Resplandor, Golpe, Gemido]

        while (start && !encontradoTesoro) {
            System.out.println(encontradoTesoro);
            // 1- Obtenemos el array de percepciones
            percep = obtenerPercepciones(percep, agenteX, agenteY);

            // 2- Actualizar y inferir BC
            informarBC(percep, agenteX, agenteY);

            // 3- Preguntar BC que acción debe hacer
            String accion = preguntarBC(agenteX, agenteY);
            System.out.println("ACCION: " + accion);

            // 4- Realizar dicho movimiento
            if (accion != " ") {
                actualizarCasillaActual(agenteX, agenteY);
                actualizarCasillaSiguiente(agenteX, agenteY, accion);
                prog.notificar("repaint");
                esperar(delay);
            }

        }
        // si ya encontradoTesoro hay que volver
        if (encontradoTesoro) {
            for (int i = camino.size() - 1; i >= 0; i--) {
                String accion = camino.get(i);
                actualizarCasillaActual(agenteX, agenteY);
                actualizarCasillaSiguiente(agenteX, agenteY, accion);
                prog.notificar("repaint");
                esperar(delay);

                camino.remove(i);
            }
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
                case 6:
                    // Tesoro + Agente
                    p[2] = 1;
                    break;
                case 15:
                    // hedor + agente
                    p[0] = 1;
                    break;
                case 16:
                    // brisa + agente
                    p[1] = 1;
                    break;
                case 17:
                    // hedor ^ brisa + agente
                    p[0] = 1;
                    p[1] = 1;
                    break;
                case 19:
                    // hedor ^ tesoro + agente
                    p[0] = 1;
                    p[2] = 1;
                    break;
                case 18:
                    // brisa ^ tesoro + agente
                    p[1] = 1;
                    p[2] = 1;
                    break;
                case 20:
                    // hedor ^ brisa ^ tesoro + agente
                    p[0] = 1;
                    p[1] = 1;
                    p[2] = 1;
                default:
                    break;
            }
        }
        return p;
    }

    public void informarBC(int p[], int x, int y) {
        // **************** */
        // PARTE ACTUALIZAR
        // *************** */

        // 1- actualizar casilla actual
        BC[x][y].setOk(true);

        // 2- Si no hemos visitado esa casilla generamos creencias
        if (!(BC[x][y].isVisitada())) {
            // Si hay resplandor => set tesoro encontrado
            if (p[2] == 1) {
                this.encontradoTesoro = true;
            } else {
                // Si tiene hedor => set las 4 casillas adyacente a possibles monstruo
                if (p[0] == 1) {
                    generarCreenciasHedor(x, y);
                }
                // Si tiene brisa => set las 4 casillas adyacentes a possibles precipicio
                if (p[1] == 1) {
                    generarCreenciasBrisa(x, y);
                }

                // CASO 0: NO HAY NADA - si no Hedor y no Brisa => SET las 4 casillas adyacentes
                // a ok
                if (p[0] == 0 && p[1] == 0) {
                    if (x > 0) {
                        BC[x - 1][y].setOk(true);
                    }
                    if (x < BC.length - 1) {
                        BC[x + 1][y].setOk(true);
                    }
                    if (y > 0) {
                        BC[x][y - 1].setOk(true);
                    }
                    if (y < BC.length - 1) {
                        BC[x][y + 1].setOk(true);
                    }

                }

                // **************** */
                // PARTE Inferir
                // *************** */

                // CASO 1 : NO HAY NADA PERO INFERIR LAS ADYACENTES
                // Si no hay hedor y no hay brisa en la casilla actual
                if (p[0] != 1 && p[1] != 1) {

                    // Arriba es posible monstruo o posibles precipicio => Contradicción
                    if ((x > 0) && (BC[x - 1][y].posibleMonstruo() && BC[x - 1][y].posiblePrecipicio())) {
                        BC[x - 1][y].setOk(true);
                    }
                    // Abajo es posible monstruo o posibles precipicio => Contradicción
                    if ((x < BC.length - 1) && (BC[x + 1][y].posibleMonstruo() && BC[x + 1][y].posiblePrecipicio())) {
                        BC[x + 1][y].setOk(true);
                    }
                    // Izquierda es posible monstruo o posibles precipicio => Contradicción
                    if ((y > 0) && (BC[x][y - 1].posibleMonstruo() || BC[x][y - 1].posiblePrecipicio())) {
                        BC[x][y - 1].setOk(true);
                    }
                    // Derecha es posible monstruo o posibles precipicio => Contradicción
                    if ((y < BC.length - 1) && (BC[x][y + 1].posibleMonstruo() || BC[x][y + 1].posiblePrecipicio())) {
                        BC[x][y + 1].setOk(true);
                    }

                    // CASO 2: Hay brisa y NO hedor => no puede haber monstruos en las 4 adyacentes
                } else if (p[0] != 1 && p[1] == 1) {
                    // Arriba es posible monstruo => Contradicción
                    if (x > 0 && BC[x - 1][y].posibleMonstruo()) {
                        BC[x - 1][y].setImposibleMonstruo(true);
                    }
                    // Abajo es posible monstruo => Contradicción
                    if (x < BC.length - 1 && BC[x + 1][y].posibleMonstruo()) {
                        BC[x + 1][y].setImposibleMonstruo(true);
                    }
                    // Izquierda es posible monstruo => Contradicción
                    if (y > 0 && BC[x][y - 1].posibleMonstruo()) {
                        BC[x][y - 1].setImposibleMonstruo(true);
                    }
                    // Derecha es posible monstruo => Contradicción
                    if (y < BC.length - 1 && BC[x][y + 1].posibleMonstruo()) {
                        BC[x][y + 1].setImposibleMonstruo(true);
                    }

                    // CASO 3: si hay hedor y no brisa => no puede haber precicpicios en las 4
                    // adyacentes
                } else if (p[0] == 1 && p[1] != 1) {
                    // Arriba es posible precipicio => Contradicción
                    if (x > 0 && BC[x - 1][y].posiblePrecipicio()) {
                        BC[x - 1][y].setImposiblePrecipicio(true);
                    }
                    // Abajo es posible precipicio => Contradicción
                    if (x < BC.length - 1 && BC[x + 1][y].posiblePrecipicio()) {
                        BC[x + 1][y].setImposiblePrecipicio(true);
                    }
                    // Izquierda es posible precipicio => Contradicción
                    if (y > 0 && BC[x][y - 1].posiblePrecipicio()) {
                        BC[x][y - 1].setImposiblePrecipicio(true);
                    }
                    // Derecha es posible precipicio => Contradicción
                    if (y < BC.length - 1 && BC[x][y + 1].posiblePrecipicio()) {
                        BC[x][y + 1].setImposiblePrecipicio(true);
                    }
                }

                // CASO 4: Cuando una casilla es imposible Monstruo y imposible precipicio
                if (x > 0) {
                    casillaOK(x - 1, y);
                }
                if (x < BC.length - 1) {
                    casillaOK(x + 1, y);
                }
                if (y < BC.length - 1) {
                    casillaOK(x, y + 1);
                }
                if (y > 0) {
                    casillaOK(x, y - 1);
                }

            }

        }

        BC[x][y].setVisitada(true);
    }

    public String preguntarBC(int x, int y) { // x fila , y columna
        // Prioridad en sentido del reloj
        if (!encontradoTesoro) {
            if (x > 0 && BC[x - 1][y].isOk() && !BC[x - 1][y].isVisitada()) {
                return "NORTE";
            } else if (y < BC.length - 1 && BC[x][y + 1].isOk() && !BC[x][y + 1].isVisitada()) {
                return "ESTE";
            } else if (x < BC.length - 1 && BC[x + 1][y].isOk() && !BC[x + 1][y].isVisitada()) {
                return "SUR";
            } else if (y > 0 && BC[x][y - 1].isOk() && !BC[x][y - 1].isVisitada()) {
                return "OESTE";

            } else if (x > 0 && BC[x - 1][y].isOk() && camino.get(camino.size() - 1) != "NORTE") {
                return "NORTE";
            } else if (y < BC.length - 1 && BC[x][y + 1].isOk() && camino.get(camino.size() - 1) != "ESTE") {
                return "ESTE";
            } else if (x < BC.length - 1 && BC[x + 1][y].isOk() && camino.get(camino.size() - 1) != "SUR") {
                return "SUR";
            } else if (y > 0 && BC[x][y - 1].isOk() && camino.get(camino.size() - 1) != "OESTE") {
                return "OESTE";

                // Si ya visitadas volver a atras
            } else if (x > 0 && BC[x - 1][y].isOk()) {
                return "NORTE";
            } else if (y < BC.length - 1 && BC[x][y + 1].isOk()) {
                return "ESTE";
            } else if (x < BC.length - 1 && BC[x + 1][y].isOk()) {
                return "SUR";
            } else if (y > 0 && BC[x][y - 1].isOk()) {
                return "OESTE";
            }

        }
        System.out.println("HOLA NO HE HECHO ACCION!");
        return " ";
    }

    // Actualizar casilla actual una vez eliminado el agente
    public void actualizarCasillaActual(int x, int y) {
        if (dat.getTablero()[x][y] == 4) {
            dat.getTablero()[x][y] = 0;
        } else if (dat.getTablero()[x][y] == 6) {
            dat.getTablero()[x][y] = 0;
        } else if (dat.getTablero()[x][y] == 15) {
            dat.getTablero()[x][y] = 9;
        } else if (dat.getTablero()[x][y] == 16) {
            dat.getTablero()[x][y] = 10;
        } else if (dat.getTablero()[x][y] == 17) {
            dat.getTablero()[x][y] = 11;
        } else if (dat.getTablero()[x][y] == 18) {
            dat.getTablero()[x][y] = 10;
        } else if (dat.getTablero()[x][y] == 19) {
            dat.getTablero()[x][y] = 9;
        } else if (dat.getTablero()[x][y] == 20) {
            dat.getTablero()[x][y] = 11;
        }
        ;
    }

    // Actualizar casilla siguiente una vez agregado el agente
    public void actualizarCasillaSiguiente(int x, int y, String accion) {
        switch (accion) {
            case "NORTE":
                this.agenteX--;
                if (!encontradoTesoro) {
                    this.camino.add("SUR"); // añadir accion inversa
                    if (dat.getTablero()[x - 1][y] == 0) {
                        dat.getTablero()[x - 1][y] = 4;
                    } else if (dat.getTablero()[x - 1][y] == 8) {
                        dat.getTablero()[x - 1][y] = 6;
                    } else if (dat.getTablero()[x - 1][y] == 9) {
                        dat.getTablero()[x - 1][y] = 15;
                    } else if (dat.getTablero()[x - 1][y] == 10) {
                        dat.getTablero()[x - 1][y] = 16;
                    } else if (dat.getTablero()[x - 1][y] == 11) {
                        dat.getTablero()[x - 1][y] = 17;
                    } else if (dat.getTablero()[x - 1][y] == 13) {
                        dat.getTablero()[x - 1][y] = 18;
                    } else if (dat.getTablero()[x - 1][y] == 12) {
                        dat.getTablero()[x - 1][y] = 19;
                    } else if (dat.getTablero()[x - 1][y] == 14) {
                        dat.getTablero()[x - 1][y] = 20;
                    }
                } else {
                    if (dat.getTablero()[x - 1][y] == 0) {
                        dat.getTablero()[x - 1][y] = 6;
                    } else if (dat.getTablero()[x - 1][y] == 8) {
                        dat.getTablero()[x - 1][y] = 6;
                    } else if (dat.getTablero()[x - 1][y] == 9) {
                        dat.getTablero()[x - 1][y] = 19;
                    } else if (dat.getTablero()[x - 1][y] == 10) {
                        dat.getTablero()[x - 1][y] = 18;
                    } else if (dat.getTablero()[x - 1][y] == 11) {
                        dat.getTablero()[x - 1][y] = 20;
                    } else if (dat.getTablero()[x - 1][y] == 13) {
                        dat.getTablero()[x - 1][y] = 18;
                    } else if (dat.getTablero()[x - 1][y] == 12) {
                        dat.getTablero()[x - 1][y] = 19;
                    } else if (dat.getTablero()[x - 1][y] == 14) {
                        dat.getTablero()[x - 1][y] = 20;
                    }
                }
                break;
            case "ESTE":
                this.agenteY++;
                if (!encontradoTesoro) {
                    this.camino.add("OESTE"); // añadir accion inversa
                    if (dat.getTablero()[x][y + 1] == 0) {
                        dat.getTablero()[x][y + 1] = 4;
                    } else if (dat.getTablero()[x][y + 1] == 8) {
                        dat.getTablero()[x][y + 1] = 6;
                    } else if (dat.getTablero()[x][y + 1] == 9) {
                        dat.getTablero()[x][y + 1] = 15;
                    } else if (dat.getTablero()[x][y + 1] == 10) {
                        dat.getTablero()[x][y + 1] = 16;
                    } else if (dat.getTablero()[x][y + 1] == 11) {
                        dat.getTablero()[x][y + 1] = 17;
                    } else if (dat.getTablero()[x][y + 1] == 13) {
                        dat.getTablero()[x][y + 1] = 18;
                    } else if (dat.getTablero()[x][y + 1] == 12) {
                        dat.getTablero()[x][y + 1] = 19;
                    } else if (dat.getTablero()[x][y + 1] == 14) {
                        dat.getTablero()[x][y + 1] = 20;
                    }
                } else {
                    if (dat.getTablero()[x][y + 1] == 0) {
                        dat.getTablero()[x][y + 1] = 6;
                    } else if (dat.getTablero()[x][y + 1] == 8) {
                        dat.getTablero()[x][y + 1] = 6;
                    } else if (dat.getTablero()[x][y + 1] == 9) {
                        dat.getTablero()[x][y + 1] = 19;
                    } else if (dat.getTablero()[x][y + 1] == 10) {
                        dat.getTablero()[x][y + 1] = 18;
                    } else if (dat.getTablero()[x][y + 1] == 11) {
                        dat.getTablero()[x][y + 1] = 20;
                    } else if (dat.getTablero()[x][y + 1] == 13) {
                        dat.getTablero()[x][y + 1] = 18;
                    } else if (dat.getTablero()[x][y + 1] == 12) {
                        dat.getTablero()[x][y + 1] = 19;
                    } else if (dat.getTablero()[x][y + 1] == 14) {
                        dat.getTablero()[x][y + 1] = 20;
                    }
                }
                break;
            case "SUR":
                this.agenteX++;
                if (!encontradoTesoro) {
                    this.camino.add("NORTE"); // añadir accion inversa
                    if (dat.getTablero()[x + 1][y] == 0) {
                        dat.getTablero()[x + 1][y] = 4;
                    } else if (dat.getTablero()[x + 1][y] == 8) {
                        dat.getTablero()[x + 1][y] = 6;
                    } else if (dat.getTablero()[x + 1][y] == 9) {
                        dat.getTablero()[x + 1][y] = 15;
                    } else if (dat.getTablero()[x + 1][y] == 10) {
                        dat.getTablero()[x + 1][y] = 16;
                    } else if (dat.getTablero()[x + 1][y] == 11) {
                        dat.getTablero()[x + 1][y] = 17;
                    } else if (dat.getTablero()[x + 1][y] == 13) {
                        dat.getTablero()[x + 1][y] = 18;
                    } else if (dat.getTablero()[x + 1][y] == 12) {
                        dat.getTablero()[x + 1][y] = 19;
                    } else if (dat.getTablero()[x + 1][y] == 14) {
                        dat.getTablero()[x + 1][y] = 20;
                    }
                } else {
                    if (dat.getTablero()[x + 1][y] == 0) {
                        dat.getTablero()[x + 1][y] = 6;
                    } else if (dat.getTablero()[x + 1][y] == 8) {
                        dat.getTablero()[x + 1][y] = 6;
                    } else if (dat.getTablero()[x + 1][y] == 9) {
                        dat.getTablero()[x + 1][y] = 19;
                    } else if (dat.getTablero()[x + 1][y] == 10) {
                        dat.getTablero()[x + 1][y] = 18;
                    } else if (dat.getTablero()[x + 1][y] == 11) {
                        dat.getTablero()[x + 1][y] = 20;
                    } else if (dat.getTablero()[x + 1][y] == 13) {
                        dat.getTablero()[x + 1][y] = 18;
                    } else if (dat.getTablero()[x + 1][y] == 12) {
                        dat.getTablero()[x + 1][y] = 19;
                    } else if (dat.getTablero()[x + 1][y] == 14) {
                        dat.getTablero()[x + 1][y] = 20;
                    }
                }
                break;
            case "OESTE":
                this.agenteY--;
                if (!encontradoTesoro) {
                    this.camino.add("ESTE"); // añadir accion inversa
                    if (dat.getTablero()[x][y - 1] == 0) {
                        dat.getTablero()[x][y - 1] = 4;
                    } else if (dat.getTablero()[x][y - 1] == 8) {
                        dat.getTablero()[x][y - 1] = 6;
                    } else if (dat.getTablero()[x][y - 1] == 9) {
                        dat.getTablero()[x][y - 1] = 15;
                    } else if (dat.getTablero()[x][y - 1] == 10) {
                        dat.getTablero()[x][y - 1] = 16;
                    } else if (dat.getTablero()[x][y - 1] == 11) {
                        dat.getTablero()[x][y - 1] = 17;
                    } else if (dat.getTablero()[x][y - 1] == 13) {
                        dat.getTablero()[x][y - 1] = 18;
                    } else if (dat.getTablero()[x][y - 1] == 12) {
                        dat.getTablero()[x][y - 1] = 19;
                    } else if (dat.getTablero()[x][y - 1] == 14) {
                        dat.getTablero()[x][y - 1] = 20;
                    }
                } else {
                    if (dat.getTablero()[x][y - 1] == 0) {
                        dat.getTablero()[x][y - 1] = 6;
                    } else if (dat.getTablero()[x][y - 1] == 8) {
                        dat.getTablero()[x][y - 1] = 6;
                    } else if (dat.getTablero()[x][y - 1] == 9) {
                        dat.getTablero()[x][y - 1] = 19;
                    } else if (dat.getTablero()[x][y - 1] == 10) {
                        dat.getTablero()[x][y - 1] = 18;
                    } else if (dat.getTablero()[x][y - 1] == 11) {
                        dat.getTablero()[x][y - 1] = 20;
                    } else if (dat.getTablero()[x][y - 1] == 13) {
                        dat.getTablero()[x][y - 1] = 18;
                    } else if (dat.getTablero()[x][y - 1] == 12) {
                        dat.getTablero()[x][y - 1] = 19;
                    } else if (dat.getTablero()[x][y - 1] == 14) {
                        dat.getTablero()[x][y - 1] = 20;
                    }
                }
                break;
            default:
                break;
        }
    }

    public void casillaOK(int x, int y) {

        if (BC[x][y].imposiblePrecipicio() &&
                BC[x][y].imposibleMonstruo()) {

            BC[x][y].setOk(true);
        }
    }

    public void generarCreenciasBrisa(int agenteX, int agenteY) {
        // Posibles precipicios (hay que mirar si esta dentro del trablero)
        BC[agenteX][agenteY].setBrisa(true);

        // Posible precipicio en agenteX - 1
        if ((hayMuro(agenteX, agenteY, -1, 0) != -1) && !(BC[agenteX - 1][agenteY].isOk())
                && (!BC[agenteX - 1][agenteY].isVisitada())) {
            BC[agenteX - 1][agenteY].setPosiblePrecipicio(true);
        }

        // Posible precipicio en agenteX + 1
        if ((hayMuro(agenteX, agenteY, 1, 0) != -1) && !(BC[agenteX + 1][agenteY].isOk())
                && (!BC[agenteX + 1][agenteY].isVisitada())) {
            BC[agenteX + 1][agenteY].setPosiblePrecipicio(true);
        }

        // Posible precipicio en agenteY - 1
        if ((hayMuro(agenteX, agenteY, 0, -1) != -1) && !(BC[agenteX][agenteY - 1].isOk())
                && (!BC[agenteX][agenteY - 1].isVisitada())) {
            BC[agenteX][agenteY - 1].setPosiblePrecipicio(true);
        }

        // Posible precipicio en agenteY + 1
        if ((hayMuro(agenteX, agenteY, 0, 1) != -1) && !(BC[agenteX][agenteY + 1].isOk())
                && (!BC[agenteX][agenteY + 1].isVisitada())) {
            BC[agenteX][agenteY + 1].setPosiblePrecipicio(true);
        }
    }

    public void generarCreenciasHedor(int agenteX, int agenteY) {
        // Posibles monstruos (hay que mirar si esta dentro del trablero)
        BC[agenteX][agenteY].setHedor(true);

        // arriba Posible monstruo en agenteX - 1
        if ((hayMuro(agenteX, agenteY, -1, 0) != -1) && !(BC[agenteX - 1][agenteY].isOk())
                && (!BC[agenteX - 1][agenteY].isVisitada())) {
            BC[agenteX - 1][agenteY].setPosibleMonstruo(true);
        }

        // ABAJO Posible monstruo en agenteX + 1
        if ((hayMuro(agenteX, agenteY, 1, 0) != -1) && !(BC[agenteX + 1][agenteY].isOk())
                && (!BC[agenteX + 1][agenteY].isVisitada())) {
            BC[agenteX + 1][agenteY].setPosibleMonstruo(true);
        }

        // izq Posible monstruo en agenteY - 1
        if ((hayMuro(agenteX, agenteY, 0, -1) != -1) && !(BC[agenteX][agenteY - 1].isOk())
                && (!BC[agenteX][agenteY - 1].isVisitada())) {
            BC[agenteX][agenteY - 1].setPosibleMonstruo(true);
        }

        // DERECHA posible monstruo en agenteY + 1
        if ((hayMuro(agenteX, agenteY, 0, 1) != -1) && !(BC[agenteX][agenteY + 1].isOk())
                && (!BC[agenteX][agenteY + 1].isVisitada())) {
            BC[agenteX][agenteY + 1].setPosibleMonstruo(true);
        }
    }

    // Disparar
    public void disparar(String direccion) {
        switch (direccion) {
            case "NORTE":
                //desde x actual hasta 0 si la casilla [x_i][y] == monstruo , actualiza dicha casilla
                // a monstruo muerto
                break;

            case "ESTE":

                break;
            case "SUR":

                break;
            case "OESTE":

                break;

            default:
                break;
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
