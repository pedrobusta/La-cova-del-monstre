package CovaMonstre.gui;

import javax.swing.*;
import java.awt.*;
import CovaMonstre.Notificar;
import CovaMonstre.modelo.Datos;

public class Gui {

    private PanelCentral central;
    private PanelControl panelControl;

    private Datos dat;
    private Notificar prog;

    private JFrame vent;
    private JPanel contenedor;

    public Gui(String s, int x, int y, Datos d, Notificar p) {
        dat = d;
        prog = p;
        vent = new JFrame(s);
        vent.setPreferredSize(new Dimension(x, y));
        crear();
    }

    public void crear() {
        contenedor = new JPanel();
        contenedor.setLayout(new BorderLayout());

        // crear barra central
        central = new PanelCentral(dat);
        contenedor.add(BorderLayout.CENTER, central);

        // crear barra Control
        panelControl = new PanelControl(prog, dat, central);
        panelControl.setPreferredSize(new Dimension(180, 100));
        contenedor.add(BorderLayout.WEST, panelControl);

        vent.add(contenedor);

        // Hacer visible el JFrame
        vent.pack();
        vent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vent.setLocationRelativeTo(null); // Centrar en la pantalla
        vent.setVisible(true);
    }

    public void repintar() {
        central.repaint();
    }

}
