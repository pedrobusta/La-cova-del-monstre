package CovaMonstre.modelo;

public class Conocimientos {
    //Atributos
    private boolean visitada = false;
    private boolean ok = false;
    private boolean hedor = false;
    private boolean brisa = false;
    private boolean posibleMonstruo = false;
    private boolean posiblePrecipicio = false;
    
    //Constructor
    public Conocimientos(){
    }
    
    //Metodos
    public boolean isVisitada(){
        return visitada;
    }
    
    public boolean isOk(){
        return ok;
    }
    
    public boolean hayHedor(){
        return hedor;
    }
    
    public boolean hayBrisa(){
        return brisa;
    }
    
    public boolean posibleMonstruo(){
        return posibleMonstruo;
    }
    
    public boolean posiblePrecipicio(){
        return posiblePrecipicio;
    }

    public void setVisitada(boolean bool) {
        this.visitada = bool;
    }

    public void setOk(boolean bool) {
        this.ok = bool;
    }

    public void setHedor(boolean bool) {
        this.hedor = bool;
    }

    public void setBrisa(boolean bool) {
        this.brisa = bool;
    }

    public void setPosibleMonstruo(boolean bool) {
        this.posibleMonstruo = bool;
    }

    public void setPosiblePrecipicio(boolean bool) {
        this.posiblePrecipicio = bool;
    }

    
}
