package CovaMonstre.modelo;

public class BC {

    private int dir; //direccion 1 arriba , 2 derecha, 3 abajo, 4 izque

    enum Movs {
        NORTE,
        SUR,
        ESTE,
        OESTE,
        NOMEM,
        STOP
    }

    public BC() {
        this.dir = 0; // 0 no direccion
    }

    public int[] Percepcion_Caracteristica(int percep[]) {

        int[] v_caracteristica = new int[10];
        return v_caracteristica;
    }

    public Movs Caracteristica_Accion(int X[],int dir) {
        Movs movimiento = Movs.NORTE;
 
        return movimiento;
    }

    public int getDir(){
        return this.dir;
    }
    
    public void setDir(int dir){
        this.dir = dir;
    }

    public String getAccion(Movs mov) {
        String str = mov.name();
        return str;
    }
}
