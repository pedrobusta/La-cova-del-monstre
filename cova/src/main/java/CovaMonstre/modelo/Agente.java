package CovaMonstre.modelo;

public class Agente {

    enum Movs {
        NORTE,
        SUR,
        ESTE,
        OESTE,
        NOMEM,
    }

    public Agente() {
    }

    public Movs Percepcion_Accion(int percepciones[], String memoria) {
        int caracteristicas[] = procesamientoPerceptual(percepciones);
        System.out.println("\t\tPERCEPCIONES");
        System.out.println("------------------------------");
        for (int i = 0; i < percepciones.length; i++) {
            System.out.println("w" + i + ": " + percepciones[i]);
        }
        Movs movimiento = seleccionAccion(caracteristicas, memoria);
        return movimiento;
    }

    private int[] procesamientoPerceptual(int percepciones[]) {

        int caracteristicas[] = new int[10];
        for (int i = 0; i < caracteristicas.length; i++) {
            caracteristicas[i] = 0;
        }

        // x5 = s1=1 and s2=0 and s3=1
        if (percepciones[0] == 1 && percepciones[1] == 0 && percepciones[2] == 1 && percepciones[5] == 0) {
            caracteristicas[4] = 1;
        }

        // x6 = s3=1 and s4=0 and s5=1
        if (percepciones[2] == 1 && percepciones[3] == 0 && percepciones[4] == 1 && percepciones[7] == 0) {
            caracteristicas[5] = 1;
        }

        // x7 = s5=1 and s6=0 and s7=1
        if (percepciones[4] == 1 && percepciones[5] == 0 && percepciones[6] == 1 && percepciones[1] == 0) {
            caracteristicas[6] = 1;
        }

        // x8 = s7=1 and s8=0 and s1=1
        if (percepciones[6] == 1 && percepciones[7] == 0 && percepciones[0] == 1 && percepciones[3] == 0) {
            caracteristicas[7] = 1;
            System.out.println("hola???");
        }

        // estoy dentro de un pasillo vertical
        if (percepciones[3] == 1 && percepciones[7] == 1 && percepciones[1] == 0 && percepciones[5] == 0) {
            caracteristicas[8] = 1;
        }
        // estoy dentro de un pasillo horizontal
        if (percepciones[1] == 1 && percepciones[5] == 1 && percepciones[3] == 0 && percepciones[7] == 0) {
            caracteristicas[9] = 1;
        }

        // x1 = s2 V s3
        if (percepciones[1] == 1 || percepciones[2] == 1) {
            caracteristicas[0] = 1;
        }

        // x2 = s4 V s5
        if (percepciones[3] == 1 || percepciones[4] == 1) {
            caracteristicas[1] = 1;
        }

        // x3 = s6 V s7
        if (percepciones[5] == 1 || percepciones[6] == 1) {
            caracteristicas[2] = 1;
        }

        // x4 = s8 V s1
        if (percepciones[7] == 1 || percepciones[0] == 1) {
            caracteristicas[3] = 1;
        }

        return caracteristicas;
    }

    public Movs seleccionAccion(int caracteristicas[], String memoria) {
        Movs movimiento = Movs.NORTE;
        Movs mem = Movs.valueOf(memoria);
        int flag = 0;

        if (caracteristicas[0] == 1 && caracteristicas[1] == 0) {
            movimiento = Movs.ESTE;
            flag++;
        } else if (caracteristicas[1] == 1 && caracteristicas[2] == 0) {
            movimiento = Movs.SUR;
            flag++;
        } else if (caracteristicas[2] == 1 && caracteristicas[3] == 0) {
            movimiento = Movs.OESTE;
            flag++;
        } else if (caracteristicas[3] == 1 && caracteristicas[0] == 0) {
            movimiento = Movs.NORTE;
            flag++;
        }

        if (caracteristicas[8] == 1 || caracteristicas[9] == 1) {
            movimiento = mem;
            System.out.println("CARACTERISTICA 8!");
            flag++;
        } else if (caracteristicas[4] == 1 && caracteristicas[6] == 0) {
            System.out.println("CARACTERISTICA 4!");
            movimiento = Movs.NORTE;
            if (mem == Movs.SUR) {
                movimiento = Movs.ESTE;
            } else if (mem == Movs.OESTE) {
                movimiento = Movs.SUR;
            }
            flag++;
        } else if (caracteristicas[6] == 1 && caracteristicas[4] == 0) {
            System.out.println("CARACTERISTICA 6!");
            movimiento = Movs.SUR;
            if (mem == Movs.NORTE) {
                movimiento = Movs.OESTE;
            }
            flag++;
        } else if (caracteristicas[5] == 1 && caracteristicas[7] == 0) {
            System.out.println("CARACTERISTICA 7!");
            movimiento = Movs.ESTE;
            if (mem == Movs.OESTE) {
                movimiento = Movs.SUR;
            }
            flag++;
        } else if (caracteristicas[7] == 1 && caracteristicas[5] == 0) {
            System.out.println("CARACTERISTICA 9!");
            movimiento = Movs.OESTE;
            if (mem == Movs.ESTE) {
                movimiento = Movs.NORTE;
            }
            flag++;
        }

        if (flag == 0) {
            System.out.println("hola he hecho el contrario!");
            movimiento = contrarioMov(mem);
        }
        System.out.println("HE ENVIADO EL MOV: " + movimiento.toString());
        return movimiento;
    }

    public String getAccion(Movs mov) {
        String str = mov.name();
        return str;
    }

    public Movs contrarioMov(Movs mov) {
        switch (mov) {
            case NORTE:
                return Movs.SUR;
            case SUR:
                return Movs.NORTE;
            case ESTE:
                return Movs.OESTE;
            case OESTE:
                return Movs.ESTE;
            default:
                return Movs.NORTE;
        }
    }
}
