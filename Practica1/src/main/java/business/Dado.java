package business;

import java.util.Random;

/**
 Clase que representa un dado.
 */
public class Dado {
    private static final Random random = new Random();

    /**
     * Constructor de la clase Dado.
     */
    public Dado() {
    }

    /**
     Lanza un dado y devuelve un número aleatorio entre 1 y el número de caras indicado.
     @param numCaras El número de caras del dado.
     @return El resultado del lanzamiento del dado.
     */
    public static int lanzar(int numCaras) {
        return random.nextInt(numCaras) +1;
    }


    /**
     Genera un número aleatorio entre 0 y el rango especificado (sin incluir el número "rango").
     @param rango El rango del número aleatorio.
     @return El número aleatorio generado.
     */
    public static int random(int rango) {
        return random.nextInt(rango);
    }

}
