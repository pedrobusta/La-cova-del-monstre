package CovaMonstre.controlador;

public class ProcesoResolver extends Thread {
    Agente a;

    public ProcesoResolver(Agente a) {
        this.a = a;
        System.out.println("initProcesoResolver");
        a.resolver();
    }

    public void run() {
        a.resolver();
    }
}
