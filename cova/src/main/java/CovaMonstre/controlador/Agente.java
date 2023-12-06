package CovaMonstre.controlador;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

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
    private int N;
    private Datos dat;
    private Notificar prog;
    private boolean start;
    private int delay;

    ProcesoResolver calculador = new ProcesoResolver(this, 1);
    ProcesoResolver calculador2 = new ProcesoResolver(this, 2);
    ProcesoResolver calculador3 = new ProcesoResolver(this, 3);
    ProcesoResolver calculador4 = new ProcesoResolver(this, 4);

    private Semaphore mutex = new Semaphore(1);

    // BC es un tablero de Conocimientos, cada conocimiento es estado de cada
    // casilla

    private int cont = 0; // contador de pasos

    // Constructor
    public Agente(Datos dat, Notificar not) {
        this.dat = dat;
        this.prog = not;
        this.start = true;
        this.delay = 500;
        this.N = 1;
    }

    // Cerebro
    public void resolver() {
        Conocimientos[][] BC;
        // conjutos de acciones realizado guardado en sentido inverso
        ArrayList<String> camino = new ArrayList<>();
        boolean encontradoTesoro = false;
        int percep[] = new int[5]; // [Hedor, Brisa, Resplandor, Golpe, Gemido]
        int agenteX = -1;
        int agenteY = -1;

        if (Thread.currentThread().equals(calculador)) {
            agenteX = dat.getDimension() - 1;
            agenteY = 0;
        } else if (Thread.currentThread().equals(calculador2)) {
            agenteX = dat.getDimension() - 1;
            agenteY = dat.getDimension() - 1;
        } else if (Thread.currentThread().equals(calculador3)) {
            agenteX = 0;
            agenteY = 0;
        } else if (Thread.currentThread().equals(calculador4)) {
            agenteX = 0;
            agenteY = dat.getDimension() - 1;
        }

        BC = new Conocimientos[dat.getDimension()][dat.getDimension()];
        for (int i = 0; i < dat.getDimension(); i++) {
            for (int j = 0; j < dat.getDimension(); j++) {
                BC[i][j] = new Conocimientos();
            }
        }
        try {
            while (start && !encontradoTesoro) {

                // 1- Obtenemos el array de percepciones
                percep = obtenerPercepciones(percep, agenteX, agenteY);
                // System.out.println(percep[0]);

                // 2- Actualizar y inferir BC
                encontradoTesoro = informarBC(percep, agenteX, agenteY, BC);

                // 3- Preguntar BC que acción debe hacer
                String accion = preguntarBC(agenteX, agenteY, encontradoTesoro, camino, BC);
                // System.out.println("accion -> " + accion);
                // 4- Realizar dicho movimiento
                if (accion != " ") {
                    mutex.acquire();
                    actualizarCasillaActual(agenteX, agenteY, encontradoTesoro);
                    int act[] = actualizarCasillaSiguiente(agenteX, agenteY, accion, encontradoTesoro, camino);
                    esperar(10);
                    agenteX = agenteX + act[0];
                    agenteY = agenteY + act[1];
                    prog.notificar("repaint");
                    esperar(delay);
                    mutex.release();
                }
                esperar(1);

                // si ya encontradoTesoro hay que volver
                if (encontradoTesoro) {
                    for (int i = camino.size() - 1; i >= 0; i--) {
                        accion = camino.get(i);
                        mutex.acquire();
                        actualizarCasillaActual(agenteX, agenteY, encontradoTesoro);
                        int act[] = actualizarCasillaSiguiente(agenteX, agenteY, accion, encontradoTesoro, camino);
                        esperar(10);
                        agenteX = agenteX + act[0];
                        agenteY = agenteY + act[1];
                        prog.notificar("repaint");
                        camino.remove(i);
                        esperar(delay);
                        mutex.release();
                    }
                    // Setear que he llegado al final
                    dat.getTablero()[agenteX][agenteY] = 100;

                }
            }
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    public boolean informarBC(int p[], int x, int y, Conocimientos BC[][]) {
        boolean encontradoTesoro = false;
        // **************** */
        // PARTE ACTUALIZAR
        // *************** */

        // 1- actualizar casilla actual
        BC[x][y].setOk(true);

        // 2- Si no hemos visitado esa casilla generamos creencias
        if (!(BC[x][y].isVisitada())) {
            // 3- Si hay resplandor => set tesoro encontrado
            if (p[2] == 1) {
                encontradoTesoro = true;
            } else {
                // 4- Si tiene hedor => set las 4 casillas adyacente a possibles monstruo
                if (p[0] == 1) {
                    generarCreenciasHedor(x, y, BC);
                    // inferir si las 4 adyacentes son monstruo
                }
                // 5- Si tiene brisa => set las 4 casillas adyacentes a possibles precipicio
                if (p[1] == 1) {
                    generarCreenciasBrisa(x, y, BC);
                }
                // 6- CASO 0: NO HAY NADA => SET las 4 casillas adyacentes a ok
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

                // CASO 1: Hay brisa y NO hedor => no puede haber monstruos en las 4 adyacentes
                if (p[0] != 1 && p[1] == 1) {
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

                    // CASO 2: si hay hedor y no brisa => no puede haber precicpicios en las 4
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

                // CASO 3: Cuando una casilla es imposible Monstruo y imposible precipicio
                if (x > 0) {
                    casillaOK(x - 1, y, BC);
                }
                if (x < BC.length - 1) {
                    casillaOK(x + 1, y, BC);
                }
                if (y < BC.length - 1) {
                    casillaOK(x, y + 1, BC);
                }
                if (y > 0) {
                    casillaOK(x, y - 1, BC);
                }

            }

            // monstruo ?
            if (p[0] == 1) {
                if (esDerechaMonstruo(x, y, BC)) {
                    System.out.println("monstruoEncontrado -> " + esDerechaMonstruo(x, y, BC));
                    disparar(x, y);
                }
            }

            BC[x][y].setVisitada(true);
        }
        return encontradoTesoro;
    }

    public String preguntarBC(int x, int y, boolean encontradoTesoro, ArrayList<String> camino, Conocimientos BC[][]) { // x
                                                                                                                        // fila
                                                                                                                        // ,
                                                                                                                        // y
                                                                                                                        // columna
        // Prioridad en sentido del reloj
        if (!encontradoTesoro) {
            if (cont < BC.length * BC.length) {
                cont++;
                // sentido del reloj no visitada fist
                if (x > 0 && BC[x - 1][y].isOk() && !BC[x - 1][y].isVisitada()) {
                    return "NORTE";
                } else if (y < BC.length - 1 && BC[x][y + 1].isOk() && !BC[x][y + 1].isVisitada()) {
                    return "ESTE";
                } else if (x < BC.length - 1 && BC[x + 1][y].isOk() && !BC[x + 1][y].isVisitada()) {
                    return "SUR";
                } else if (y > 0 && BC[x][y - 1].isOk() && !BC[x][y - 1].isVisitada()) {
                    return "OESTE";

                // sentido del reloj visitada pero no repetido first
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
            
            } else if (cont <= BC.length * BC.length * 2) {
                cont++;
                
                // sentido aleatorio no visitada fist
                if (x > 0 && BC[x - 1][y].isOk() && !BC[x - 1][y].isVisitada()) {
                    return "NORTE";
                } else if (y > 0 && BC[x][y - 1].isOk() && !BC[x][y - 1].isVisitada()) {
                    return "OESTE";
                } else if (x < BC.length - 1 && BC[x + 1][y].isOk() && !BC[x + 1][y].isVisitada()) {
                    return "SUR";
                } else if (y < BC.length - 1 && BC[x][y + 1].isOk() && !BC[x][y + 1].isVisitada()) {
                    return "ESTE";

                // sentido aleatorio visitada pero no repetido first
                } else if (x > 0 && BC[x - 1][y].isOk() && camino.get(camino.size() - 1) != "NORTE") {
                    return "NORTE";
                } else if (y > 0 && BC[x][y - 1].isOk() && camino.get(camino.size() - 1) != "OESTE") {
                    return "OESTE";
                } else if (x < BC.length - 1 && BC[x + 1][y].isOk() && camino.get(camino.size() - 1) != "SUR") {
                    return "SUR";
                } else if (y < BC.length - 1 && BC[x][y + 1].isOk() && camino.get(camino.size() - 1) != "ESTE") {
                    return "ESTE";

                // Si ya visitadas volver a atras
                } else if (y > 0 && BC[x][y - 1].isOk()) {
                    return "OESTE";
                } else if (x > 0 && BC[x - 1][y].isOk()) {
                    return "NORTE";
                } else if (y < BC.length - 1 && BC[x][y + 1].isOk()) {
                    return "ESTE";
                } else if (x < BC.length - 1 && BC[x + 1][y].isOk()) {
                    return "SUR";
                }

            } else if (cont <= BC.length * BC.length * 2 + 50) {
                cont++;
                if (cont > BC.length * BC.length * 2 + 50) {
                    cont = 0;
                }

                // Crear un ArrayList de String
                ArrayList<String> acciones = new ArrayList<>();

                // Verificar las condiciones y añadir a la lista de acciones
                if (y > 0 && BC[x][y - 1].isOk()) {
                    acciones.add("OESTE");
                }
                if (x > 0 && BC[x - 1][y].isOk()) {
                    acciones.add("NORTE");
                } 
                if (y < BC.length - 1 && BC[x][y + 1].isOk()) {
                    acciones.add("ESTE");
                }
                if (x < BC.length - 1 && BC[x + 1][y].isOk()) {
                    acciones.add("SUR");
                }

                if (!acciones.isEmpty()) {
                    System.out.println("randooooooooo");
                    Random random = new Random();
                    int indiceAleatorio = random.nextInt(acciones.size());
                    String accionSeleccionada = acciones.get(indiceAleatorio);
                    return accionSeleccionada;
                }

            }
        }
        return " ";
    }

    // Actualizar casilla actual una vez eliminado el agente
    public void actualizarCasillaActual(int x, int y, boolean encontradoTesoro) {
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
        } else if (dat.getTablero()[x][y] == 21) {
            dat.getTablero()[x][y] = 4;
        } else if (dat.getTablero()[x][y] == 22) {
            dat.getTablero()[x][y] = 21;
        } else if (dat.getTablero()[x][y] == 23) {
            dat.getTablero()[x][y] = 22;
        } else if (dat.getTablero()[x][y] == 24) {
            dat.getTablero()[x][y] = 6;
        } else if (dat.getTablero()[x][y] == 25) {

            dat.getTablero()[x][y] = 4;
        }
        ;
    }

    // Actualizar casilla siguiente una vez agregado el agente
    public int[] actualizarCasillaSiguiente(int x, int y, String accion, boolean encontradoTesoro,
            ArrayList<String> camino) {
        int ret[] = new int[2];
        ret[0] = 0;
        ret[1] = 0;
        switch (accion) {
            case "NORTE":
                ret[0] = -1;
                if (!encontradoTesoro) {
                    camino.add("SUR"); // añadir accion inversa
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
                    } else if (dat.getTablero()[x - 1][y] == 4) {
                        // Tenemos 2 agentes en la misma casilla
                        dat.getTablero()[x - 1][y] = 21;
                    } else if (dat.getTablero()[x - 1][y] == 21) {
                        // Tenemos 3 agentes en la misma casilla
                        dat.getTablero()[x - 1][y] = 22;
                    } else if (dat.getTablero()[x - 1][y] == 22) {
                        // Tenemos 4 agentes en la misma casilla
                        dat.getTablero()[x - 1][y] = 23;
                    } else if (dat.getTablero()[x - 1][y] == 6) {
                        // Pasa por encima de un agente con tesoro
                        dat.getTablero()[x - 1][y] = 24;
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
                    } else if (dat.getTablero()[x - 1][y] == 4) {
                        // Tenemos 2 agentes en la misma casilla
                        dat.getTablero()[x - 1][y] = 21;
                    } else if (dat.getTablero()[x - 1][y] == 21) {
                        // Tenemos 3 agentes en la misma casilla
                        dat.getTablero()[x - 1][y] = 22;
                    } else if (dat.getTablero()[x - 1][y] == 22) {
                        // Tenemos 4 agentes en la misma casilla
                        dat.getTablero()[x - 1][y] = 23;
                    } else if (dat.getTablero()[x - 1][y] == 6) {
                        // Pasa por encima de un agente con tesoro teniendo el tesoro
                        dat.getTablero()[x - 1][y] = 25;
                    }
                }
                break;
            case "ESTE":
                ret[1] = 1;
                if (!encontradoTesoro) {
                    camino.add("OESTE"); // añadir accion inversa
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
                    } else if (dat.getTablero()[x][y + 1] == 4) {
                        // Tenemos 2 agentes en la misma casilla
                        dat.getTablero()[x][y + 1] = 21;
                    } else if (dat.getTablero()[x][y + 1] == 21) {
                        // Tenemos 3 agentes en la misma casilla
                        dat.getTablero()[x][y + 1] = 22;
                    } else if (dat.getTablero()[x][y + 1] == 22) {
                        // Tenemos 4 agentes en la misma casilla
                        dat.getTablero()[x][y + 1] = 23;
                    } else if (dat.getTablero()[x][y + 1] == 6) {
                        // Pasa por encima de un agente con tesoro
                        dat.getTablero()[x][y + 1] = 24;
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
                    } else if (dat.getTablero()[x][y + 1] == 4) {
                        // Tenemos 2 agentes en la misma casilla
                        dat.getTablero()[x][y + 1] = 21;
                    } else if (dat.getTablero()[x][y + 1] == 21) {
                        // Tenemos 3 agentes en la misma casilla
                        dat.getTablero()[x][y + 1] = 22;
                    } else if (dat.getTablero()[x][y + 1] == 22) {
                        // Tenemos 4 agentes en la misma casilla
                        dat.getTablero()[x][y + 1] = 23;
                    } else if (dat.getTablero()[x][y + 1] == 6) {
                        // Pasa por encima de un agente con tesoro
                        dat.getTablero()[x][y + 1] = 25;
                    }
                }
                break;
            case "SUR":
                ret[0] = 1;
                if (!encontradoTesoro) {
                    camino.add("NORTE"); // añadir accion inversa
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
                    } else if (dat.getTablero()[x + 1][y] == 4) {
                        // Tenemos 2 agentes en la misma casilla
                        dat.getTablero()[x + 1][y] = 21;
                    } else if (dat.getTablero()[x + 1][y] == 21) {
                        // Tenemos 3 agentes en la misma casilla
                        dat.getTablero()[x + 1][y] = 22;
                    } else if (dat.getTablero()[x + 1][y] == 22) {
                        // Tenemos 4 agentes en la misma casilla
                        dat.getTablero()[x + 1][y] = 23;
                    } else if (dat.getTablero()[x + 1][y] == 6) {
                        // Pasa por encima de un agente con tesoro
                        dat.getTablero()[x + 1][y] = 24;
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
                    } else if (dat.getTablero()[x + 1][y] == 4) {
                        // Tenemos 2 agentes en la misma casilla
                        dat.getTablero()[x + 1][y] = 21;
                    } else if (dat.getTablero()[x + 1][y] == 21) {
                        // Tenemos 3 agentes en la misma casilla
                        dat.getTablero()[x + 1][y] = 22;
                    } else if (dat.getTablero()[x + 1][y] == 22) {
                        // Tenemos 4 agentes en la misma casilla
                        dat.getTablero()[x + 1][y] = 23;
                    } else if (dat.getTablero()[x + 1][y] == 6) {
                        // Pasa por encima de un agente con tesoro
                        dat.getTablero()[x + 1][y] = 25;
                    }
                }
                break;
            case "OESTE":
                ret[1] = -1;
                if (!encontradoTesoro) {
                    camino.add("ESTE"); // añadir accion inversa
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
                    } else if (dat.getTablero()[x][y - 1] == 4) {
                        // Tenemos 2 agentes en la misma casilla
                        dat.getTablero()[x][y - 1] = 21;
                    } else if (dat.getTablero()[x][y - 1] == 21) {
                        // Tenemos 3 agentes en la misma casilla
                        dat.getTablero()[x][y - 1] = 22;
                    } else if (dat.getTablero()[x][y - 1] == 22) {
                        // Tenemos 4 agentes en la misma casilla
                        dat.getTablero()[x][y - 1] = 23;
                    } else if (dat.getTablero()[x][y - 1] == 6) {
                        // Pasa por encima de un agente con tesoro
                        dat.getTablero()[x][y - 1] = 24;
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
                    } else if (dat.getTablero()[x][y - 1] == 4) {
                        // Tenemos 2 agentes en la misma casilla
                        dat.getTablero()[x][y - 1] = 21;
                    } else if (dat.getTablero()[x][y - 1] == 21) {
                        // Tenemos 3 agentes en la misma casilla
                        dat.getTablero()[x][y - 1] = 22;
                    } else if (dat.getTablero()[x][y - 1] == 22) {
                        // Tenemos 4 agentes en la misma casilla
                        dat.getTablero()[x][y - 1] = 23;
                    } else if (dat.getTablero()[x][y - 1] == 6) {
                        // Pasa por encima de un agente con tesoro teniendo el tesoro
                        dat.getTablero()[x][y - 1] = 25;
                    }
                }
                break;
            default:
                break;

        }
        return ret;
    }

    public void casillaOK(int x, int y, Conocimientos BC[][]) {

        if (BC[x][y].imposiblePrecipicio() &&
                BC[x][y].imposibleMonstruo()) {

            BC[x][y].setOk(true);
        }
    }

    public void generarCreenciasBrisa(int agenteX, int agenteY, Conocimientos BC[][]) {
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

    public void generarCreenciasHedor(int agenteX, int agenteY, Conocimientos BC[][]) {
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

    public boolean esDerechaMonstruo(int x, int y, Conocimientos BC[][]) {
        int cont = 0;
        try {
            if (BC[x][y - 1].isOk()) {
                cont++;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            cont++;
        }

        try {
            if (BC[x - 1][y].isOk()) {
                cont++;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            cont++;
        }

        try {
            if (BC[x + 1][y].isOk()) {
                cont++;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            cont++;
        }

        if (cont == 3) {
            return true;
        } else {
            return false;
        }
    }

    // Disparar hacia derecha
    public void disparar(int x, int y) {
        // matar monstruo
        int aux = dat.getTablero()[x][y]; // casilla actual
        dat.getTablero()[x][y] = 3; // dispararImage
        prog.notificar("repaint");
        esperar(500);

        dat.getTablero()[x][y + 1] = 2; // asignar al tablero monstruo muerto
        dat.getTablero()[x][y] = aux;
        prog.notificar("repaint");
        esperar(500);

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

    public void pintarAgenteCorrespondiente() {
        switch (N) {
            case 1:
                dat.colocarAgente(1);
                dat.quitarAgente(2);
                dat.quitarAgente(3);
                dat.quitarAgente(4);
                prog.notificar("repaint");
                break;
            case 2:
                dat.colocarAgente(1);
                dat.colocarAgente(2);
                dat.quitarAgente(3);
                dat.quitarAgente(4);
                prog.notificar("repaint");
                break;
            case 3:
                dat.colocarAgente(1);
                dat.colocarAgente(2);
                dat.colocarAgente(3);
                dat.quitarAgente(4);
                prog.notificar("repaint");
                break;
            case 4:
                dat.colocarAgente(1);
                dat.colocarAgente(2);
                dat.colocarAgente(3);
                dat.colocarAgente(4);
                prog.notificar("repaint");
                break;
            default:
                if (N < 1) {
                    N = 1;
                } else if (N > 4) {
                    N = 4;
                }
                break;
        }
    }

    @Override
    public void notificar(String s) {
        switch (s) {
            case "START":
                start = true;
                switch (N) {
                    case 1:
                        calculador.start();
                        break;
                    case 2:
                        calculador.start();
                        calculador2.start();
                        break;
                    case 3:
                        calculador.start();
                        calculador2.start();
                        calculador3.start();
                        break;
                    case 4:
                        calculador.start();
                        calculador2.start();
                        calculador3.start();
                        calculador4.start();
                        break;
                }
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
            case "upAG": // agente++
                N++;
                pintarAgenteCorrespondiente();
                break;
            case "downAG": // agente--
                N--;
                pintarAgenteCorrespondiente();
                break;
        }
    }
}
