package CovaMonstre.controlador;

public class ProcesoResolver extends Thread {
    Agente a;

    public ProcesoResolver(Agente a) {
        this.a = a;
    }

    public void run() {
        a.resolver();
    }
}
