package CovaMonstre;

import CovaMonstre.gui.Gui;
import CovaMonstre.controlador.Agente;
import CovaMonstre.modelo.Datos;

/**
 * Hello world!
 *
 */
public class App implements Notificar {

    private Gui gui;
    private Datos dat;
    private Agente con;

    public static void main(String[] args) {
        (new App()).inicio();
    }

    private void inicio() {
        dat = new Datos();
        gui = construirInterfaz();
        con = new Agente(dat, this);
    }

    private Gui construirInterfaz() {
        Gui gui = new Gui("Robot", 1050, 900, dat, this);
        return gui;
    }

    // Metodo para comunicar ----------------------------------------------------
    @Override
    public void notificar(String s) {

        if (s.startsWith("Cambia-")) {
            String aux = s.substring(s.indexOf("-") + 1);
            dat.regenerar(Integer.parseInt(aux));
            gui.repintar();
        } else {
            switch (s) {
                case "START":
                     //System.out.println("main recibido START");
                    con.notificar("START");
                    break;
                case "repaint":
                    gui.repintar();
                    break;
                case "STOP":
                    con.notificar("STOP");
                    break;
                case "upVelocidad": // velocidad++
                    con.notificar("upVelocidad");
                    break;
                case "downVelocidad": // velocidad--
                    con.notificar("downVelocidad");
                    break;
                default:
                    break;
            }

        }

    }
}
