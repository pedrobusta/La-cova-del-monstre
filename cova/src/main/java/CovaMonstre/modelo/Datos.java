package CovaMonstre.modelo;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author juant
 */
public class Datos {
    //Atributos
    private int dimension;
    private int tablero[][];
    private int dir; //direccion del robot para pintar 1-arriba, 2-derecha, 3-abajo, 4-izquierda

    private int numMonstruoInit = 0;     
    private int numMonstruoActual = 0;

    //Constructor
    public Datos() {
        this.dir =1;
        regenerar(10); 
    }
    public Datos(int n) {
        this.dir=1;
        regenerar(n);
    }
    
    //Metodos
    public void regenerar(int d) {
        dimension = d;
        tablero = new int[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                tablero[i][j] = 0;
            }
        }
        // Posicionamos el agente con flecha
        tablero[dimension-1][0] = 4;
    }
    
    public void colocarAgente(int ag){
        switch (ag) {
            case 1:
                tablero[dimension-1][0] = 4;
                break;
            case 2:
                tablero[dimension-1][dimension-1] = 4;
                break;
            case 3:
                tablero[0][0] = 4;
                break;
            case 4:
                tablero[0][dimension-1] = 4;
                break;
        }
    }

     public void quitarAgente(int ag){
        switch (ag) {
            case 1:
                tablero[dimension-1][0] = 0;
                break;
            case 2:
                tablero[dimension-1][dimension-1] = 0;
                break;
            case 3:
                tablero[0][0] = 0;
                break;
            case 4:
                tablero[0][dimension-1] = 0;
                break;
        }
    }


    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
    
    public int[][] getTablero(){
        return this.tablero;
    }
    
    public int[] getPosRobot(){
        int[] posRobot={0,0};
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if(tablero[i][j]== 2){
                    posRobot[0] = i;
                    posRobot[1] = j;
                }
            }
        }
        return posRobot;
    }

    public int getDir(){
        return this.dir;
    }
    
    public void setDir(int dir){
        this.dir = dir;
    }

    public void setNumMonstruoActual(int num){
        this.numMonstruoActual = num;
    }

    public int getNumMonstruoInit(){
        return this.numMonstruoInit;
    }

    public int getNumMonstruoActual(){
        return this.numMonstruoActual;
    }
}

