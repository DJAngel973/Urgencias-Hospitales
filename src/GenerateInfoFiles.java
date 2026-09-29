

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GenerateInfoFiles
 * ------------------
 * Entrega 1 del proyecto "Urgencias Hospital".
 *
 * Esta clase es responsable de generar los archivos planos que servirán
 * como ENTRADA al programa principal (el que hará el análisis
 * epidemiológico y la consulta de pacientes en entregas posteriores):
 *
 * 1) cie10.csv -> diccionario de códigos CIE-10 y su descripción.
 * 2) urgencias.txt -> historial de atenciones de urgencias, con la
 * estructura enriquecida que se necesitará para
 * el análisis epidemiológico y la consulta por
 * paciente (nombre completo).
 *
 * Formato de urgencias.txt (separado por ';'):
 * NOMBRE;APELLIDO1;APELLIDO2;EDAD;SEXO;CIE10;TRIAGE;FECHA
 *
 * Ejemplo:
 * Laura;Gómez;Martínez;34;F;J00;4;2026-01-15
 *
 * Notas de diseño para este y las siguientes entregas:
 * - Se generan pacientes "recurrentes" (mismo nombre completo, varias
 * atenciones en fechas distintas) para poder construir después el
 * módulo de "Consulta individual" (historial por paciente).
 * - La frecuencia de enfermedades y de triage NO es uniforme: se usan
 * pesos para que existan enfermedades y niveles de triage más
 * comunes que otros, tal como ocurriría en datos reales de urgencias
 * (esto es clave para que el reporte epidemiológico de la entrega 2
 * tenga sentido: "enfermedades más frecuentes", "picos", etc.).
 */
public class GenerateInfoFiles {

    // ---------------------------------------------------------------
    // Rutas de los archivos de salida (entrada del programa principal)
    // ---------------------------------------------------------------
    private static final String CARPETA_DATOS = "data";
    private static final String ARCHIVO_CIE10 = CARPETA_DATOS + "/cie10.csv";
    private static final String ARCHIVO_URGENCIAS = CARPETA_DATOS + "/urgencias.txt";

    // Cantidad de atenciones (líneas) a generar en urgencias.txt
    private static final int CANTIDAD_ATENCIONES = 300;

    // Semilla fija para que la generación sea reproducible mientras se
    // depura el proyecto. Se puede quitar (usar Random() sin semilla)
    // cuando se quiera generar datos distintos en cada ejecución.
    private static final long SEMILLA = 2026L;

    private final Random random;

    // ---------------------------------------------------------------
    // Diccionario CIE-10 (código -> nombre de la enfermedad).
    // Se incluye un "peso" relativo para simular que unas enfermedades
    // son mucho más frecuentes que otras en urgencias.
    // ---------------------------------------------------------------
    private static class Enfermedad {
        final String codigo;
        final String nombre;
        final int peso; // mayor peso = aparece con más frecuencia

        Enfermedad(String codigo, String nombre, int peso) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.peso = peso;
        }
    }

    private final List<Enfermedad> enfermedades = new ArrayList<>();

    // Nombres y apellidos usados para generar pacientes de forma aleatoria
    private static final String[] NOMBRES = {
            "Laura", "Carlos", "Mariana", "Andrés", "Camila", "Juan",
            "Valentina", "Santiago", "Isabella", "Sebastián", "Sofía",
            "Daniel", "Gabriela", "Miguel", "Natalia", "David", "Paula",
            "Alejandro", "Luisa", "Felipe"
    };

    private static final String[] APELLIDOS = {
            "Gómez", "Pérez", "Rodríguez", "Martínez", "López", "García",
            "Hernández", "Ramírez", "Torres", "Castaño", "Osorio", "Vargas",
            "Jiménez", "Morales", "Ortiz", "Suárez", "Rojas", "Castro"
    };

    public GenerateInfoFiles() {
        this.random = new Random(SEMILLA);
        inicializarDiccionarioCie10();
    }

    /**
     * Carga el diccionario CIE-10 base. En una versión real este
     * catálogo es enorme; aquí se usa un subconjunto representativo
     * de patologías comunes en un servicio de urgencias.
     */
    private void inicializarDiccionarioCie10() {
        enfermedades.add(new Enfermedad("J00", "Resfriado Común", 30));
        enfermedades.add(new Enfermedad("J18", "Neumonía", 12));
        enfermedades.add(new Enfermedad("I10", "Hipertensión Esencial", 15));
        enfermedades.add(new Enfermedad("E11", "Diabetes Mellitus Tipo 2", 10));
        enfermedades.add(new Enfermedad("K35", "Apendicitis Aguda", 6));
        enfermedades.add(new Enfermedad("A09", "Diarrea y Gastroenteritis", 14));
        enfermedades.add(new Enfermedad("I21", "Infarto Agudo de Miocardio", 3));
        enfermedades.add(new Enfermedad("J45", "Asma", 9));
        enfermedades.add(new Enfermedad("S52", "Fractura de Antebrazo", 5));
        enfermedades.add(new Enfermedad("N39", "Infección de Vías Urinarias", 11));
        enfermedades.add(new Enfermedad("R10", "Dolor Abdominal", 13));
        enfermedades.add(new Enfermedad("M54", "Dorsalgia (Dolor de Espalda)", 8));
        enfermedades.add(new Enfermedad("G43", "Migraña", 7));
        enfermedades.add(new Enfermedad("T14", "Traumatismo No Especificado", 6));
        enfermedades.add(new Enfermedad("F41", "Trastorno de Ansiedad", 5));
    }

    // ---------------------------------------------------------------
    // Generación de cie10.csv
    // ---------------------------------------------------------------
    /** Reemplaza {@code data/cie10.csv} con el código y nombre de cada enfermedad. */
    public void generarArchivoCie10() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_CIE10))) {
            for (Enfermedad e : enfermedades) {
                writer.write(e.codigo + ";" + e.nombre);
                writer.newLine();
            }
        }
    }

    // ---------------------------------------------------------------
    // Generación de urgencias.txt
    // ---------------------------------------------------------------
    /** Genera 300 atenciones con pacientes, diagnósticos, triage y fechas simuladas. */
    public void generarArchivoUrgencias() throws IOException {
        // Peso total para el sorteo ponderado de enfermedades
        int pesoTotal = 0;
        for (Enfermedad e : enfermedades) {
            pesoTotal += e.peso;
        }

        // 1) Generar un "banco" de pacientes (nombre completo + edad + sexo fijos)
        // para que varios de ellos se repitan con múltiples atenciones,
        // tal como pasaría en la vida real (mismo paciente, distintas fechas).
        int cantidadPacientesUnicos = Math.max(1, CANTIDAD_ATENCIONES / 3);
        List<String[]> bancoPacientes = new ArrayList<>();
        for (int i = 0; i < cantidadPacientesUnicos; i++) {
            String nombre = NOMBRES[random.nextInt(NOMBRES.length)];
            String apellido1 = APELLIDOS[random.nextInt(APELLIDOS.length)];
            String apellido2 = APELLIDOS[random.nextInt(APELLIDOS.length)];
            int edad = generarEdad();
            String sexo = random.nextBoolean() ? "F" : "M";
            bancoPacientes.add(new String[] { nombre, apellido1, apellido2,
                    String.valueOf(edad), sexo });
        }

        LocalDate inicioRango = LocalDate.of(2026, 1, 1);
        LocalDate finRango = LocalDate.of(2026, 12, 31);
        long diasEnRango = ChronoUnit.DAYS.between(inicioRango, finRango);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_URGENCIAS))) {
            for (int i = 0; i < CANTIDAD_ATENCIONES; i++) {
                // Elegir un paciente del banco (esto genera repeticiones naturales)
                String[] paciente = bancoPacientes.get(random.nextInt(bancoPacientes.size()));

                String cie10 = elegirCie10Ponderado(pesoTotal);
                int triage = generarTriagePonderado();
                LocalDate fecha = inicioRango.plusDays(random.nextInt((int) diasEnRango + 1));

                String linea = String.join(";",
                        paciente[0], // NOMBRE
                        paciente[1], // APELLIDO1
                        paciente[2], // APELLIDO2
                        paciente[3], // EDAD
                        paciente[4], // SEXO
                        cie10, // CIE10
                        String.valueOf(triage), // TRIAGE
                        fecha.toString() // FECHA (yyyy-MM-dd)
                );
                writer.write(linea);
                writer.newLine();
            }
        }
    }

    /** Sortea un código CIE-10 respetando los pesos de frecuencia. */
    /** Selecciona un diagnóstico mediante sorteo proporcional a su peso. */
    private String elegirCie10Ponderado(int pesoTotal) {
        int sorteo = random.nextInt(pesoTotal);
        int acumulado = 0;
        for (Enfermedad e : enfermedades) {
            acumulado += e.peso;
            if (sorteo < acumulado) {
                return e.codigo;
            }
        }
        // Fallback teórico (no debería alcanzarse)
        return enfermedades.get(enfermedades.size() - 1).codigo;
    }

    /**
     * Genera un nivel de triage (1 a 5) con distribución realista:
     * pocos casos críticos (1) y una mayoría en niveles intermedios (3-4).
     */
    /** Selecciona un nivel de triage del 1 al 5 usando una distribución ponderada. */
    private int generarTriagePonderado() {
        int[] pesosTriage = { 5, 15, 35, 30, 15 }; // índices 0..4 -> triage 1..5
        int total = 0;
        for (int p : pesosTriage)
            total += p;
        int sorteo = random.nextInt(total);
        int acumulado = 0;
        for (int i = 0; i < pesosTriage.length; i++) {
            acumulado += pesosTriage[i];
            if (sorteo < acumulado) {
                return i + 1;
            }
        }
        return 5;
    }

    /** Genera una edad con más peso en adultos y adultos mayores. */
    /** Genera una edad entre 0 y 95, dando mayor peso a los grupos adultos. */
    private int generarEdad() {
        double p = random.nextDouble();
        if (p < 0.12)
            return random.nextInt(13); // 0-12
        if (p < 0.18)
            return 13 + random.nextInt(5); // 13-17
        if (p < 0.35)
            return 18 + random.nextInt(13); // 18-30
        if (p < 0.55)
            return 31 + random.nextInt(15); // 31-45
        if (p < 0.78)
            return 46 + random.nextInt(15); // 46-60
        if (p < 0.95)
            return 61 + random.nextInt(15); // 61-75
        return 76 + random.nextInt(20); // 76+
    }

    /**
     * Punto de entrada de la Entrega 1: genera ambos archivos de origen.
     */
    public static void main(String[] args) {
        GenerateInfoFiles generador = new GenerateInfoFiles();
        try {
            java.io.File carpeta = new java.io.File(CARPETA_DATOS);
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            generador.generarArchivoCie10();
            generador.generarArchivoUrgencias();

            System.out.println("Archivos generados exitosamente:");
            System.out.println(" - " + ARCHIVO_CIE10);
            System.out.println(" - " + ARCHIVO_URGENCIAS + " (" + CANTIDAD_ATENCIONES + " registros)");
        } catch (IOException e) {
            System.err.println("Error generando los archivos: " + e.getMessage());
        }
    }
}
