package src;

import java.io.IOException;
import java.util.Scanner;

/**
 * Main
 * ----
 * Esqueleto del menú principal del sistema "Urgencias Hospital".
 *
 * IMPORTANTE (alcance de la Entrega 1):
 * Solo la opción [1] Generar Archivos está implementada, ya que la
 * entrega 1 consiste únicamente en la clase GenerateInfoFiles.
 * Las demás opciones (importar/ver base, ver salida, descargar salida)
 * pertenecen a entregas posteriores y aquí solo se dejan como
 * referencia de hacia dónde va el proyecto.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            mostrarMenu();
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    generarArchivos();
                    break;
                case "2":
                case "3":
                case "4":
                    System.out.println("\nEsta opción se implementará en una entrega posterior.\n");
                    break;
                case "5":
                    salir = true;
                    System.out.println("\n¡Hasta luego!");
                    break;
                default:
                    System.out.println("\nOpción no válida. Intente de nuevo.\n");
            }
        }
        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("========================================");
        System.out.println("           URGENCIAS HOSPITAL");
        System.out.println("========================================");
        System.out.println("[1] Generar Archivos");
        System.out.println("[2] Importar/Ver Base");
        System.out.println("[3] Importar/Ver Salida");
        System.out.println("[4] Descargar Salida");
        System.out.println("[5] Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void generarArchivos() {
        GenerateInfoFiles generador = new GenerateInfoFiles();
        try {
            java.io.File carpeta = new java.io.File("data");
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }
            generador.generarArchivoCie10();
            generador.generarArchivoUrgencias();
            System.out.println("\nArchivos generados exitosamente en la carpeta 'data/':");
            System.out.println(" - cie10.csv");
            System.out.println(" - urgencias.txt\n");
        } catch (IOException e) {
            System.err.println("\nError generando los archivos: " + e.getMessage() + "\n");
        }
    }
}
