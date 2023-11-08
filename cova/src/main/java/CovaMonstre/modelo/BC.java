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

    //recibe unas percerpciones y devuelve una accion 
    public Movs Percepcion_Accion(int percep[]) {
        Movs movimiento = Movs.NORTE;
       
        int w1 = percep[0];
        int w2 = percep[1];
        int w3 = percep[2];
        int w4 = percep[3];
        int w5 = percep[4];
        int w6 = percep[5];
        int w7 = percep[6];
        int w8 = percep[7];
        int dir = percep[8];
        
        if(w2==1 && w4==1 && w6==1 && w8==1){  
            movimiento = Movs.STOP;
            return movimiento;
        }if(w2==0 && w1==1 && dir==2){
            movimiento = Movs.NORTE;
            return movimiento;
        }if(w4==0 && w3==1 && dir==3){
            movimiento = Movs.ESTE;
            return movimiento;
        }if(w6==0 && w5==1 && dir==4){
            movimiento = Movs.SUR;
            return movimiento;
        }if(w8==0 && w7==1 && dir==1){
            movimiento = Movs.OESTE;
            return movimiento;
        }
        
        if(w2==0 && w8==1 && dir==1){
            movimiento = Movs.NORTE;
            return movimiento;
        }if(w4==0 && w2==1 && dir==2){
            movimiento = Movs.ESTE;
            return movimiento;
        }if(w6==0 && w4==1 && dir==3){
            movimiento = Movs.SUR;
            return movimiento;
        }if(w8==0 && w6==1 && dir==4){
            movimiento = Movs.OESTE;
            return movimiento;
        }
        
        if(w2==0 && w8==1 ){
            movimiento = Movs.NORTE;
            return movimiento;
        }if(w4==0 && w2==1){
            movimiento = Movs.ESTE;
            return movimiento;
        }if(w6==0 && w4==1 ){
            movimiento = Movs.SUR;
            return movimiento;
        }if(w8==0 && w6==1 ){
            movimiento = Movs.OESTE;
            return movimiento;
        }
 
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
