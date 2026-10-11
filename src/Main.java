import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
                case "2":
                    importarYVerBase();
                    break;
                case "3":
                    importarYVerSalida();
                    break;
                case "4":
                    descargarSalida(scanner);
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

    /** Lee y muestra un resumen de los archivos base del sistema. */
    private static void importarYVerBase() {
        LectorDatos lector = new LectorDatos();
        if (!lector.cargarDatos()) {
            System.out.println("\n❌ No se pudo importar la base. Genere primero los archivos.\n");
            return;
        }

        System.out.println("\n✅ BASE IMPORTADA CORRECTAMENTE");
        System.out.println("Códigos CIE-10: " + lector.getDiccionarioCIE10().size());
        System.out.println("Atenciones: " + lector.getAtenciones().size());
        System.out.println("\nDiccionario CIE-10:");
        lector.getDiccionarioCIE10().forEach((codigo, nombre) ->
            System.out.println("  " + codigo + " - " + nombre));
        System.out.println("\nPrimeras atenciones:");
        lector.getAtenciones().stream().limit(10).forEach(atencion ->
            System.out.println("  " + atencion.getNombreCompleto()
                + " | " + atencion.getCie10()
                + " | triage " + atencion.getTriage()
                + " | " + atencion.getFecha()));
        System.out.println();
    }

    /** Importa y muestra el archivo de salida epidemiológico. */
    private static void importarYVerSalida() {
        Path salida = Paths.get("data", "patologias.csv");
        if (!Files.exists(salida)) {
            System.out.println("\n❌ No existe data/patologias.csv.");
            System.out.println("Genérelo primero desde la interfaz gráfica.\n");
            return;
        }

        try {
            System.out.println("\n✅ SALIDA IMPORTADA: data/patologias.csv");
            System.out.println("════════════════════════════════════════");
            Files.readAllLines(salida, StandardCharsets.UTF_8)
                .forEach(System.out::println);
            System.out.println();
        } catch (IOException e) {
            System.out.println("\n❌ No se pudo leer la salida: " + e.getMessage() + "\n");
        }
    }

    /** Copia la salida a una ubicación indicada por el usuario. */
    private static void descargarSalida(Scanner scanner) {
        Path origen = Paths.get("data", "patologias.csv");
        if (!Files.exists(origen)) {
            System.out.println("\n❌ No existe data/patologias.csv para descargar.");
            System.out.println("Genérelo primero desde la interfaz gráfica.\n");
            return;
        }

        System.out.print("Indique la ruta de destino (Enter = descargas/patologias.csv): ");
        String ruta = scanner.nextLine().trim();
        Path destino = ruta.isEmpty()
            ? Paths.get("descargas", "patologias.csv")
            : Paths.get(ruta);

        try {
            Path carpetaDestino = destino.getParent();
            if (carpetaDestino != null) Files.createDirectories(carpetaDestino);
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ Salida copiada en: " + destino.toAbsolutePath() + "\n");
        } catch (IOException e) {
            System.out.println("❌ No se pudo descargar la salida: " + e.getMessage() + "\n");
        }
    }
}
