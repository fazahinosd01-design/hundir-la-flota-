import java.util.Random;
import java.util.Scanner;

public class App {
    /**
     * Scanner para la entrada de datos por teclado.
     */
    private static Scanner sc = new Scanner(System.in);

    /**
     * Tamaño del tablero de juego.
     */
    private static int TAM = 10;

    /**
     * Tablero del jugador 1.
     * Cada posición contiene:
     * 0 = agua, 1-5 = barco sin tocar, 6 = barco tocado, 7 = disparo a agua.
     */
    private static int barcosJ1[][] = new int[TAM][TAM];

    /**
     * Tablero del jugador 2.
     * Igual que {@link #barcosJ1}.
     */
    private static int barcosJ2[][] = new int[TAM][TAM];

    /**
     * Número de casillas de barco que quedan por hundir del jugador 1.
     */
    private static int nBarcos1;

    /**
     * Número de casillas de barco que quedan por hundir del jugador 2.
     */
    private static int nBarcos2;

    /**
     * Matriz auxiliar para colocar barcos temporalmente al generar el tablero.
     */
    private static int matrizAux[][] = new int[TAM][TAM];

    /**
     * Cantidad de barcos por tipo (índice 0 = tamaño 1, índice 4 = tamaño 5).
     */
    private static final int cantidad[] = { 5, 4, 3, 2, 1 };

    /**
     * Tamaño de los barcos (1 a 5 casillas).
     */
    private static final int tamanios[] = { 1, 2, 3, 4, 5 };

    /**
     * Nombres de los barcos según su tamaño.
     */
    private static final String[] nombres = { "Lancha", "Crucero", "Submarino", "Buque", "Portaaviones" };

    /**
     * Direcciones posibles para colocar los barcos: arriba, derecha, abajo,
     * izquierda.
     */
    private static final int direcciones[][] = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };

    // Colores ANSI para imprimir el tablero en consola
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_GREY = "\u001B[90m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static final String[] colores = { ANSI_BLACK, ANSI_CYAN, ANSI_BLUE, ANSI_YELLOW, ANSI_GREEN, ANSI_PURPLE,
            ANSI_RED, ANSI_GREY };

    /**
     * Método principal que inicia el juego.
     *
     * @param args Argumentos de la línea de comandos (no se usan).
     * @throws Exception Si ocurre algún error de ejecución inesperado.
     */
    public static void main(String[] args) throws Exception {
        prepararJuego();
        sc.close();
    }

    /**
     * Muestra un menú de selección de modo de juego y devuelve la opción elegida.
     * 
     * Muestra un menú en consola con opciones: 1 = PVP, 2 = PVE, 0 = salir.
     * Valida la entrada del usuario y repite la solicitud hasta que sea correcta.
     *
     * @return Opción elegida por el usuario: 0 = Salir, 1 = PVP, 2 = PVE.
     */
    public static int menuJuego() {
        int opcion = -1;
        do {
            System.out.println("\n--- MENÚ BATTLESHIP ---");
            System.out.println("1. Jugador vs Jugador (PVP)");
            System.out.println("2. Jugador vs Máquina (PVE)");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");

            if (sc.hasNextInt()) {
                opcion = sc.nextInt();
                if (opcion < 0 || opcion > 2) {
                    System.out.println("Opción no válida.");
                }
            } else {
                System.out.println("Entrada no válida. Introduce un número.");
                sc.next(); // Limpiar buffer
            }
        } while (opcion < 0 || opcion > 2);

        return opcion;
    }

    /**
     * Inicializa los tableros de ambos jugadores, calcula los barcos y solicita el
     * modo de juego.
     * Dependiendo de la opción elegida, inicia PVP o PVE.
     * 
     * Este método realiza los siguientes pasos:
     * <ol>
     * <li>Llama a {@link #generarTablero()} para crear los tableros de ambos
     * jugadores.</li>
     * <li>Calcula el número total de casillas de barco con
     * {@link #calcularNBarcos(int[], int[])}.</li>
     * <li>Muestra ambos tableros completos.</li>
     * <li>Muestra el menú de selección de juego y ejecuta el modo
     * seleccionado.</li>
     * </ol>
     *
     * @postcondición Los tableros de los jugadores están generados y el juego
     *                inicia en el modo seleccionado.
     */
    public static void prepararJuego() {
        barcosJ1 = generarTablero();
        barcosJ2 = generarTablero();

        nBarcos1 = calcularNBarcos(cantidad, tamanios);
        nBarcos2 = calcularNBarcos(cantidad, tamanios);

        System.out.println("TABLERO JUGADOR 1 GENERADO:");
        mostrarTablero(barcosJ1);
        System.out.println("TABLERO JUGADOR 2 GENERADO:");
        mostrarTablero(barcosJ2);

        int opcion = menuJuego();
        if (opcion == 1) {
            jugarPVP();
        } else if (opcion == 2) {
            jugarPVE();
        } else {
            System.out.println("Saliendo del juego...");
        }
    }

    /**
     * Calcula el número total de casillas de barco dadas las cantidades y tamaños
     * de barcos.
     *
     * @param cantidades Array con la cantidad de barcos por tipo.
     * @param tamanios   Array con los tamaños de los barcos correspondientes.
     * @return Total de casillas de barco.
     * @precondición {@code cantidades.length == tamanios.length}.
     */
    public static int calcularNBarcos(int[] cantidades, int[] tamanios) {
        int total = 0;
        for (int i = 0; i < tamanios.length; i++) {
            total += cantidades[i] * tamanios[i];
        }
        return total;
    }

    /**
     * Ejecuta el modo Jugador vs Jugador.
     * Permite que ambos jugadores disparen alternativamente hasta que uno gane.
     *
     * @precondición Los tableros de ambos jugadores deben estar inicializados.
     * @postcondición El juego termina cuando {@link #nBarcos1} o {@link #nBarcos2}
     *                llega a 0.
     */
    public static void jugarPVP() {
        System.out.println("\n--- INICIO MODO PVP ---");
        boolean turnoJ1 = true;

        while (nBarcos1 > 0 && nBarcos2 > 0) {
            if (turnoJ1) {
                System.out.println("\n>> TURNO JUGADOR 1");
                mostrarJugador1();
                int[] coords = leerCoordenadas();
                int x = coords[0];
                int y = coords[1];

                if (disparar(barcosJ2, x, y)) {
                    System.out.println(ANSI_GREEN + "¡TOCADO!" + ANSI_WHITE);
                    nBarcos2--;
                    if (cantarDisparo(barcosJ2, x, y)) {
                        System.out.println(ANSI_RED + "¡HUNDIDO!" + ANSI_WHITE);
                    }
                } else {
                    System.out.println(ANSI_BLUE + "Agua..." + ANSI_WHITE);
                }
            } else {
                System.out.println("\n>> TURNO JUGADOR 2");
                mostrarJugador2();
                int[] coords = leerCoordenadas();
                int x = coords[0];
                int y = coords[1];

                if (disparar(barcosJ1, x, y)) {
                    System.out.println(ANSI_GREEN + "¡TOCADO!" + ANSI_WHITE);
                    nBarcos1--;
                    if (cantarDisparo(barcosJ1, x, y)) {
                        System.out.println(ANSI_RED + "¡HUNDIDO!" + ANSI_WHITE);
                    }
                } else {
                    System.out.println(ANSI_BLUE + "Agua..." + ANSI_WHITE);
                }
            }
            turnoJ1 = !turnoJ1;
        }

        if (nBarcos1 > 0) {
            System.out.println("\n¡JUGADOR 1 GANA!");
        } else {
            System.out.println("\n¡JUGADOR 2 GANA!");
        }
    }

    /**
     * Ejecuta el modo Jugador vs Máquina.
     * La máquina dispara aleatoriamente.
     *
     * @precondición Los tableros de ambos jugadores deben estar inicializados.
     * @postcondición El juego termina cuando {@link #nBarcos1} o {@link #nBarcos2}
     *                llega a 0.
     */
    public static void jugarPVE() {
        System.out.println("\n---  ESTE ES EL INICIO MODO PVE ---");
        boolean turnoJ1 = true;
        Random r = new Random();

        while (nBarcos1 > 0 && nBarcos2 > 0) {
            if (turnoJ1) {
                System.out.println("\n>> TURNO JUGADOR 1");
                mostrarJugador1();
                int[] coords = leerCoordenadas();
                int x = coords[0];
                int y = coords[1];

                if (disparar(barcosJ2, x, y)) {
                    System.out.println(ANSI_GREEN + "¡TOCADO!" + ANSI_WHITE);
                    nBarcos2--;
                    if (cantarDisparo(barcosJ2, x, y)) {
                        System.out.println(ANSI_RED + "¡HUNDIDO!" + ANSI_WHITE);
                    }
                } else {
                    System.out.println(ANSI_BLUE + "Agua..." + ANSI_WHITE);
                }
            } else {
                System.out.println("\n>> TURNO MÁQUINA");
                int x, y;
                // IA simple: dispara aleatorio hasta encontrar hueco no disparado
                do {
                    x = r.nextInt(TAM);
                    y = r.nextInt(TAM);
                } while (barcosJ1[x][y] == 6 || barcosJ1[x][y] == 7);

                System.out.println("La máquina dispara a: " + x + " " + y);

                if (disparar(barcosJ1, x, y)) {
                    System.out.println(ANSI_RED + "¡TE HAN TOCADO!" + ANSI_WHITE);
                    nBarcos1--;
                    if (cantarDisparo(barcosJ1, x, y)) {
                        System.out.println(ANSI_RED + "¡BARCO HUNDIDO POR LA MÁQUINA!" + ANSI_WHITE);
                    }
                } else {
                    System.out.println(ANSI_BLUE + "La máquina ha fallado." + ANSI_WHITE);
                }
            }
            turnoJ1 = !turnoJ1;
        }

        if (nBarcos1 > 0) {
            System.out.println("\n¡JUGADOR 1 GANA!");
        } else {
            System.out.println("\n¡LA MÁQUINA GANA!");
        }
    }

    /**
     * Realiza un disparo sobre el tablero especificado.
     *
     * @param matriz Tablero donde se dispara.
     * @param x      Coordenada X (fila) del disparo.
     * @param y      Coordenada Y (columna) del disparo.
     * @return {@code true} si se tocó un barco, {@code false} si fue agua o disparo
     *         repetido.
     * @precondición {@code 0 <= x < TAM && 0 <= y < TAM}.
     * @postcondición La matriz queda actualizada con el resultado del disparo (6 =
     *                tocado, 7 = agua).
     */
    public static boolean disparar(int[][] matriz, int x, int y) {
        if (x < 0 || x >= TAM || y < 0 || y >= TAM) {
            System.out.println("Disparo fuera de rango.");
            return false;
        }

        if (matriz[x][y] == 0) {
            matriz[x][y] = 7; // Agua disparada
            return false;
        } else if (matriz[x][y] >= 1 && matriz[x][y] <= 5) {
            matriz[x][y] = 6; // Tocado
            return true;
        } else {
            // Ya disparado (6 o 7)
            System.out.println("Ya has disparado aquí.");
            return false;
        }
    }

    /**
     * Determina si un barco ha sido tocado o hundido a partir de la casilla
     * disparada.
     *
     * @param matriz Tablero donde se encuentra el barco.
     * @param x      Coordenada X (fila) del disparo.
     * @param y      Coordenada Y (columna) del disparo.
     * @return {@code true} si el barco está completamente hundido, {@code false} si
     *         solo ha sido tocado.
     */
    public static boolean cantarDisparo(int[][] matriz, int x, int y) {
        // Algoritmo de inundación (BFS/DFS) para ver si el componente conexo de 6s
        // tiene algún vecino que sea barco intacto (1-5).

        // Matriz de visitados para no ciclar
        boolean[][] visitado = new boolean[TAM][TAM];
        return checkHundido(matriz, x, y, visitado);
    }

    private static boolean checkHundido(int[][] matriz, int x, int y, boolean[][] visitado) {
        if (x < 0 || x >= TAM || y < 0 || y >= TAM)
            return true; // Fuera del tablero es "seguro"
        if (visitado[x][y])
            return true;
        visitado[x][y] = true;

        int val = matriz[x][y];

        // Si encontramos una parte de barco intacta, NO está hundido
        if (val >= 1 && val <= 5)
            return false;

        // Si es agua o agua disparada, es un límite, no seguimos buscando por aquí
        if (val == 0 || val == 7)
            return true;

        // Si es 6 (tocado), seguimos buscando en sus vecinos
        if (val == 6) {
            boolean arriba = checkHundido(matriz, x - 1, y, visitado);
            boolean abajo = checkHundido(matriz, x + 1, y, visitado);
            boolean izquierda = checkHundido(matriz, x, y - 1, visitado);
            boolean derecha = checkHundido(matriz, x, y + 1, visitado);

            return arriba && abajo && izquierda && derecha;
        }

        return true;
    }

    // #region Preparación del tablero

    /**
     * Genera un tablero aleatorio con los barcos colocados.
     * 
     * Para cada tipo de barco:
     * <ul>
     * <li>Se intenta colocar la cantidad correspondiente de barcos de ese
     * tamaño.</li>
     * <li>Se elige una posición aleatoria y una dirección válida usando
     * {@link #comprobarDirecciones(int, int, int)}.</li>
     * <li>Se coloca el barco con {@link #copiarBarcoEn(int, int, int, int)}.</li>
     * </ul>
     *
     * @return Matriz {@link int[][]} de tamaño {@link #TAM} x {@link #TAM} con los
     *         barcos colocados.
     *         0 = agua, 1-5 = barco sin tocar.
     * @precondición {@link #TAM} debe ser mayor que 0.
     * @postcondición {@link #matrizAux} queda reiniciada a cero y se devuelve un
     *                tablero completo.
     */
    public static int[][] generarTablero() {
        Random r = new Random();
        int x, y, direccion = -1;

        // Reiniciar matrizAux para asegurar que está limpia
        matrizAux = new int[TAM][TAM];

        for (int i = cantidad.length - 1; i >= 0; i--) {
            for (int j = 0; j < cantidad[i]; j++) {
                int intentos = 0;
                do {
                    x = r.nextInt(TAM);
                    y = r.nextInt(TAM);
                    intentos++;
                    // Evitar bucle infinito si no hay espacio (aunque en 10x10 debería caber)
                    if (intentos > 1000) {
                        // Si falla mucho, reiniciamos todo el tablero (estrategia simple)
                        matrizAux = new int[TAM][TAM];
                        i = cantidad.length - 1;
                        j = -1; // Se incrementará a 0 en el bucle
                        break;
                    }
                } while ((direccion = comprobarDirecciones(x, y, tamanios[i])) == -1);

                if (intentos <= 1000) {
                    copiarBarcoEn(x, y, direccion, tamanios[i]);
                }
            }
        }

        int[][] matrizJuego = new int[TAM][TAM];
        for (int i = 0; i < TAM; i++) {
            matrizJuego[i] = matrizAux[i].clone();
        }
        // Limpiar para la siguiente generación
        matrizAux = new int[TAM][TAM];
        return matrizJuego;
    }

    /**
     * Comprueba si una posición (x,y) está libre para colocar un barco. Comprueba
     * que todas las casillas del barco tengan espacio
     * para lo cual debe haber al menos una casilla vacía entre barco y barco.
     * 
     * La comprobación se realiza en la matriz matrizAux
     *
     * @param x Fila de la posición a comprobar.
     * @param y Columna de la posición a comprobar.
     * @return {@code true} si la posición y sus adyacentes están libres,
     *         {@code false} en caso contrario.
     */
    public static boolean comprobarPosicion(int x, int y) {
        if (x < 0 || x >= TAM || y < 0 || y >= TAM) {
            return false;
        }

       
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                
                if (i >= 0 && i < TAM && j >= 0 && j < TAM) {
                    if (matrizAux[i][j] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determina una dirección viable para colocar un barco de tamaño dado desde
     * (x,y).
     *
     * @param x        Fila de inicio.
     * @param y        Columna de inicio.
     * @param tamBarco Tamaño del barco.
     * @return Índice de la dirección válida
     *         (0=arriba,1=derecha,2=abajo,3=izquierda), -1 si no hay direcciones
     *         válidas.
     * @precondición {@code 1 <= tamBarco <= 5} y
     *               {@code 0 <= x < TAM && 0 <= y < TAM}.
     */
    public static int comprobarDirecciones(int x, int y, int tamBarco) {
        Random r = new Random();
        int[] direccionesViables = new int[4];
        int nDireccionesViables = 0;
        boolean viable = true;

        // Primero comprobamos si el punto de origen es válido
        if (!comprobarPosicion(x, y))
            return -1;

        if (tamBarco == 1)
            return 0; // Dirección irrelevante para tamaño 1, devolvemos 0 (arriba) por defecto

        for (int i = 0; i < direcciones.length; i++) {
            viable = true;
            for (int j = 1; j < tamBarco; j++) { // Empezamos en j=1 porque j=0 ya se comprobó arriba
                if (!comprobarPosicion(x + direcciones[i][0] * j, y + direcciones[i][1] * j)) {
                    viable = false;
                    break;
                }
            }

            if (viable) {
                direccionesViables[nDireccionesViables] = i;
                nDireccionesViables++;
            }
        }

        if (nDireccionesViables == 0)
            return -1;
        else
            return direccionesViables[r.nextInt(nDireccionesViables)];
    }

    /**
     * Copia un barco en la posición (x,y) siguiendo la dirección indicada.
     *
     * @param x         Fila inicial.
     * @param y         Columna inicial.
     * @param direccion Dirección del barco
     *                  (0=arriba,1=derecha,2=abajo,3=izquierda).
     * @param tamanio   Tamaño del barco.
     * @precondición {@code 0 <= x,y < TAM}, {@code direccion ∈ [0,3]},
     *               {@code tamanio > 0}.
     * @postcondición {@link #matrizAux} queda modificada con el barco colocado.
     */
    public static void copiarBarcoEn(int x, int y, int direccion, int tamanio) {
        for (int i = 0; i < tamanio; i++) {
            matrizAux[x + direcciones[direccion][0] * i][y + direcciones[direccion][1] * i] = tamanio;
        }
    }

    // #endregion

    /**
     * Muestra por consola el tablero completo, incluyendo barcos y disparos.
     *
     * @param matriz Tablero a mostrar.
     */
    public static void mostrarTablero(int[][] matriz) {
        System.out.print("   ");
        for (int i = 0; i < TAM; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        for (int i = 0; i < TAM; i++) {
            System.out.print(i + "  ");
            for (int j = 0; j < TAM; j++) {
                int valor = matriz[i][j];
                String simbolo = " ";
                String color = ANSI_BLUE; 

                if (valor == 0) {
                    simbolo = "w";
                    color = ANSI_BLUE;
                } else if (valor >= 1 && valor <= 5) {
                    simbolo = "b"; // Barco
                    color = ANSI_GREY;
                } else if (valor == 6) {
                    simbolo = "x"; // Tocado
                    color = ANSI_RED;
                } else if (valor == 7) {
                    simbolo = "o"; 
                    color = ANSI_CYAN;
                }

                System.out.print(color + simbolo + ANSI_WHITE + " ");
            }
            System.out.println();
        }
    }

    /**
     * Muestra el tablero del jugador 1 y el tablero rival, ocultando los barcos
     * enemigos.
     */
    public static void mostrarJugador1() {
        System.out.println("\n--- TU TABLERO (JUGADOR 1) ---");
        mostrarTablero(barcosJ1);

        System.out.println("\n--- TABLERO RIVAL (JUGADOR 2) ---");
        mostrarTableroOculto(barcosJ2);
    }

    /**
     * Muestra el tablero del jugador 2 y el tablero rival, ocultando los barcos
     * enemigos.
     */
    public static void mostrarJugador2() {
        System.out.println("\n--- TU TABLERO (JUGADOR 2) ---");
        mostrarTablero(barcosJ2);

        System.out.println("\n--- TABLERO RIVAL (JUGADOR 1) ---");
        mostrarTableroOculto(barcosJ1);
    }

    /**
     * Muestra el tablero ocultando los barcos intactos (para ver al enemigo).
     * 
     * @param matriz Tablero a mostrar.
     */
    public static void mostrarTableroOculto(int[][] matriz) {
        System.out.print("   ");
        for (int i = 0; i < TAM; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        for (int i = 0; i < TAM; i++) {
            System.out.print(i + "  ");
            for (int j = 0; j < TAM; j++) {
                int valor = matriz[i][j];
                String simbolo = " ";
                String color = ANSI_BLUE;

                if (valor == 0 || (valor >= 1 && valor <= 5)) {
                    simbolo = "~"; // Ocultamos barcos como agua
                    color = ANSI_BLUE;
                } else if (valor == 6) {
                    simbolo = "X"; // Tocado
                    color = ANSI_RED;
                } else if (valor == 7) {
                    simbolo = "O"; // Agua disparada
                    color = ANSI_CYAN;
                }

                System.out.print(color + simbolo + ANSI_WHITE + " ");
            }
            System.out.println();
        }
    }

    /**
     * Lee las coordenadas de disparo del usuario de forma robusta.
     * 
     * @return Array de 2 enteros [x, y] con las coordenadas validadas.
     */
    public static int[] leerCoordenadas() {
        System.out.println("Introduce coordenadas de disparo (Fila Columna):");
        int x = -1, y = -1;
        boolean coordenadasValidas = false;
        while (!coordenadasValidas) {
            if (sc.hasNextInt()) {
                x = sc.nextInt();
                if (sc.hasNextInt()) {
                    y = sc.nextInt();
                    coordenadasValidas = true;
                } else {
                    System.out.println("Coordenada e inválida. Introduce Fila y Columna (números):");
                    sc.next(); // Limpiar
                }
            } else {
                System.out.println("Entrada inválida. Introduce Fila y Columna (números):");
                sc.next(); // Limpiar
            }
        }
        return new int[] { x, y };
    }
}