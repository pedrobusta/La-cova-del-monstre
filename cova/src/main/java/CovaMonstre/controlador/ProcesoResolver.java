package CovaMonstre.controlador;

public class ProcesoResolver extends Thread {
    Agente a;
    int id;

    public ProcesoResolver(Agente a, int id) {
        this.a = a;
        this.id = id;
        System.out.println("initProcesoResolver");
        //a.resolver();
    }

    public void run() {
        a.resolver();
    }
}
