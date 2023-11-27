package CovaMonstre.gui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import CovaMonstre.App;
import CovaMonstre.Notificar;
import CovaMonstre.modelo.Datos;

/**
 *
 * @author pedro
 */
public class PanelControl extends JLayeredPane implements ActionListener {

    private BufferedImage textureImage;
    private JTextField texto;
    private Notificar prog;
    private JPanel panelDibujo;
   // private JPanel panelDibujo2;
    private Datos dat;
    private PanelCentral tableroPanel;

    public PanelControl(Notificar prog, Datos dat, PanelCentral tableroPanel) {

        this.prog = prog;
        this.dat = dat;
        this.tableroPanel = tableroPanel;

        this.setLayout(new BorderLayout());
        // fondo del panel
        try {
            textureImage = ImageIO.read(getClass().getResource("../imagenes/texturaMoqueta.jpg"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        panelDibujo = new JPanel() {
            @Override
            public void paint(Graphics gr) {
                AlphaComposite opacidad = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f);
                Rectangle rect = new Rectangle(0, 0, this.getWidth(), this.getHeight());
                BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = (Graphics2D) bi.getGraphics();
                Graphics2D g2 = (Graphics2D) bi.getGraphics();
                g2.setColor(new Color(45, 87, 44));
                g.setComposite(opacidad);
                g.setPaint(new TexturePaint(textureImage, rect));
                g2.fillRect(0, 0, this.getWidth(), this.getHeight());
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
                gr.drawImage(bi, 0, 0, this);
            }
        };
        panelDibujo.setPreferredSize(new Dimension(200, 800));
        panelDibujo.setBounds(0, 0, 170, 800);

        ponOpciones();
        add(panelDibujo, BorderLayout.CENTER);
    }

    public void ponOpciones() {

        // ICONO AJADRES
        ImageIcon titulo = new ImageIcon(getClass().getResource("../imagenes/icon.png"));
        JLabel label = new JLabel(titulo);
        label.setBounds(10, 10, 130, 120);

        // DIMENSION
        JLabel info = new JLabel("Introduce la dimensión N");
        info.setFont(new Font("Arial", Font.BOLD, 12));
        info.setForeground(Color.white);
        info.setBounds(20, 150, 180, 10);

        // Cambiar dimension
        texto = new JTextField();
        texto.setForeground(Color.gray);
        texto.setBounds(25, 175, 60, 30);

        JButton cambia = new JButton("Cambia");
        cambia.setFont(new Font("Arial", Font.BOLD, 12));
        cambia.setBorder(BorderFactory.createLineBorder(new Color(39, 66, 38)));
        cambia.addActionListener(this);
        cambia.setBounds(90, 175, 70, 30);
        cambia.setBackground(new Color(126, 100, 78));
        cambia.setForeground(Color.black);

        // Elegir piezas
        JLabel info2 = new JLabel("- - - - - - - -");
        info2.setFont(new Font("Arial", Font.BOLD, 12));
        info2.setForeground(Color.white);
        info2.setBounds(10, 210, 200, 30);

        // ICONO MONSTRUO
        JLabel paredLabel = new JLabel();
        paredLabel.setIcon(escalar(new ImageIcon(getClass().getResource("../imagenes/monstruo.png")), 120));
        paredLabel.setBounds(32, 250, 120, 120);
        paredLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                System.out.println("Modo pared activado");
                tableroPanel.setModo(1);
            }
        });

        // ICONO PRECIPICIO
        JLabel robotLabel = new JLabel();
        robotLabel.setIcon(escalar(new ImageIcon(getClass().getResource("../imagenes/precipicio.png")), 120));
        robotLabel.setBounds(32, 380, 120, 120);
        robotLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                System.out.println("Modo robot activado");
                tableroPanel.setModo(7);
            }
        });

        // ICONO TESORO
        JLabel tesoro = new JLabel();
        tesoro.setIcon(escalar(new ImageIcon(getClass().getResource("../imagenes/tesoro.png")), 120));
        tesoro.setBounds(32, 500, 120, 120);
        tesoro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableroPanel.setModo(8);
            }
        });

        // Panel Velocidad
        JLabel labelVelocidad = new JLabel("VELOCIDAD");
        labelVelocidad.setFont(new Font("Arial", Font.BOLD, 13));
        labelVelocidad.setForeground(Color.white);
        labelVelocidad.setBounds(45, 630, 100, 30);


        JLabel labelAgentes = new JLabel("AGENTES");
        labelAgentes.setFont(new Font("Arial", Font.BOLD, 13));
        labelAgentes.setForeground(Color.white);
        labelAgentes.setBounds(52, 669, 100, 30);

        JButton btnUP = new JButton("+");
        btnUP.setActionCommand("upVelocidad");
        btnUP.setBackground(new Color(126, 100, 78));
        btnUP.setBounds(123, 630, 25, 25);
        btnUP.setBorder(BorderFactory.createLineBorder(new Color(39, 66, 38)));
        btnUP.addActionListener(this);

        JButton btnDowm = new JButton("-");
        btnDowm.setActionCommand("downVelocidad");
        btnDowm.setBackground(new Color(126, 100, 78));
        btnDowm.setBounds(16, 630, 25, 25);
        btnDowm.setBorder(BorderFactory.createLineBorder(new Color(39, 66, 38)));
        btnDowm.addActionListener(this);

        JButton btnUPAG = new JButton("+");
        btnUPAG.setActionCommand("upAG");
        btnUPAG.setBackground(new Color(126, 100, 78));
        btnUPAG.setBounds(123, 670, 25, 25);
        btnUPAG.setBorder(BorderFactory.createLineBorder(new Color(39, 66, 38)));
        btnUPAG.addActionListener(this);

        JButton btnDowmAG = new JButton("-");
        btnDowmAG.setActionCommand("downAG");
        btnDowmAG.setBackground(new Color(126, 100, 78));
        btnDowmAG.setBounds(16, 670, 25, 25);
        btnDowmAG.setBorder(BorderFactory.createLineBorder(new Color(39, 66, 38)));
        btnDowmAG.addActionListener(this);

        // Boton Stop
        JButton stop = new JButton("STOP");
        stop.setActionCommand("Stop");
        stop.setBackground(new Color(126, 100, 78));
        stop.setBounds(30, 710, 120, 70);
        stop.setBorder(BorderFactory.createLineBorder(new Color(39, 66, 38)));
        stop.addActionListener(this);

        // Boton Start
        JButton calcula = new JButton();
        calcula.setActionCommand("Start");
        ImageIcon calc = new ImageIcon(getClass().getResource("../imagenes/ejecutar.png"));
        calc = escalar(calc, 50);
        calcula.setIcon(calc);
        calcula.setBackground(new Color(126, 100, 78));
        calcula.setForeground(Color.white);
        calcula.setBounds(30, 790, 120, 70);
        calcula.setBorder(BorderFactory.createLineBorder(new Color(39, 66, 38)));
        calcula.addActionListener(this);

        add(label);
        add(info2);
        add(info);
        add(texto);
        add(cambia);

        add(paredLabel);
        add(robotLabel);
        add(tesoro);

        add(labelAgentes);
        add(btnDowmAG);
        add(btnUPAG);

        add(labelVelocidad);
        add(btnDowm);
        add(btnUP);

        add(stop);
        add(calcula);
        
    }

    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
            case "Cambia":
                prog.notificar("Cambia-" + texto.getText());
                break;
            case "Start":
                prog.notificar("START");
                break;
            case "Stop":
                prog.notificar("STOP");
                break;
            case "upVelocidad": // velocidad++
                prog.notificar("upVelocidad");
                break;
            case "downVelocidad": // velocidad--
                prog.notificar("downVelocidad");
                break;
            case "upAG": // agente++
                prog.notificar("upAG");
                break;
            case "downAG": // agente--
                prog.notificar("downAG");
                break;
            default:
                break;
        }
    }

    private ImageIcon escalar(ImageIcon im, int tam) {
        ImageIcon i = new ImageIcon(im.getImage()
                .getScaledInstance(tam, tam, Image.SCALE_SMOOTH));
        return i;
    }

}
