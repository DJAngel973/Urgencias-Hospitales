import java.util.Scanner;

/**
 * Punto de entrada alternativo del sistema.
 *
 * Presenta un menú inicial para escoger entre la interfaz gráfica y el modo
 * consola. La lógica de generación se delega a {@link GenerateInfoFiles};
 * esta clase solo coordina la navegación del usuario.
 */
public class Main {
    /** Inicia el menú principal y dirige al modo seleccionado. */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("════════════════════════════════════════");
        System.out.println("    SISTEMA DE URGENCIAS HOSPITAL");
        System.out.println("════════════════════════════════════════");
        System.out.println("[1] Abrir interfaz gráfica (GUI)");
        System.out.println("[2] Modo consola (CLI)");
        System.out.print("Seleccione: ");
        String op = sc.nextLine().trim();

        if (op.equals("1")) {
            // Lanzar GUI
            javax.swing.SwingUtilities.invokeLater(() -> new InterfazGUI().setVisible(true));
        } else {
            // Modo consola (tu menú original)
            menuConsola(sc);
        }
        sc.close();
    }

    /** Muestra el menú CLI mientras el usuario no elija la opción de salida. */
    private static void menuConsola(Scanner scanner) {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n========================================");
            System.out.println("         URGENCIAS HOSPITAL (CLI)");
            System.out.println("========================================");
            System.out.println("[1] Generar Archivos");
            System.out.println("[2] Importar/Ver Base");
            System.out.println("[3] Importar/Ver Salida");
            System.out.println("[4] Descargar Salida");
            System.out.println("[5] Salir");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1":
                    generarArchivos();
                    break;
                case "2": case "3": case "4":
                    System.out.println("\nEsta opción se implementará en una entrega posterior.\n");
                    break;
                case "5":
                    salir = true;
                    System.out.println("\n¡Hasta luego!");
                    break;
                default:
                    System.out.println("\nOpción no válida.\n");
            }
        }
    }

    /** Crea la carpeta de datos y delega la generación de archivos al generador. */
    private static void generarArchivos() {
        try {
            new java.io.File("data").mkdirs();
            GenerateInfoFiles gen = new GenerateInfoFiles();
            gen.generarArchivoCie10();
            gen.generarArchivoUrgencias();
            System.out.println("\n✅ Archivos generados en data/");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}