import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Genera el resumen epidemiológico por patología en formato CSV.
 *
 * Esta clase no tiene método {@code main}: la interfaz principal es la
 * responsable de iniciar la aplicación. Recibe los datos ya cargados para
 * mantener separadas la lectura, el cálculo y la interacción con el usuario.
 */
public class GeneradorPatologias {
    private static final String ARCHIVO_SALIDA = "data/patologias.csv";
    private static final String SEPARADOR = ";";

    /** Genera o reemplaza {@code data/patologias.csv} y devuelve sus filas. */
    public int generarArchivo(List<Atencion> atenciones,
                              Map<String, String> diccionarioCie10) throws IOException {
        if (atenciones == null || atenciones.isEmpty()) {
            throw new IOException("No hay atenciones cargadas para generar la salida.");
        }

        Map<String, ResumenPatologia> resumenes = agruparPorPatologia(atenciones, diccionarioCie10);
        File carpeta = new File("data");
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta data.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_SALIDA))) {
            writer.write("CIE10;PATOLOGIA;TOTAL_ATENCIONES;PORCENTAJE_TOTAL;TRIAGE_1;TRIAGE_2;"
                + "TRIAGE_3;TRIAGE_4;TRIAGE_5;CASOS_CRITICOS;PORCENTAJE_CRITICOS;"
                + "FEMENINO;MASCULINO;EDAD_PROMEDIO");
            writer.newLine();

            for (ResumenPatologia resumen : resumenes.values()) {
                writer.write(resumen.comoFila(atenciones.size()));
                writer.newLine();
            }
        }
        return resumenes.size();
    }

    private Map<String, ResumenPatologia> agruparPorPatologia(
            List<Atencion> atenciones, Map<String, String> diccionarioCie10) {
        Map<String, ResumenPatologia> resumenes = new LinkedHashMap<>();
        for (Atencion atencion : atenciones) {
            String codigo = atencion.getCie10();
            ResumenPatologia resumen = resumenes.get(codigo);
            if (resumen == null) {
                String nombre = diccionarioCie10.getOrDefault(codigo, codigo);
                resumen = new ResumenPatologia(codigo, nombre);
                resumenes.put(codigo, resumen);
            }
            resumen.agregar(atencion);
        }
        return resumenes;
    }

    /** Acumula las métricas de una patología sin modificar las atenciones. */
    private static class ResumenPatologia {
        private final String codigo;
        private final String nombre;
        private int total;
        private final int[] porTriage = new int[5];
        private int femeninos;
        private int masculinos;
        private int sumaEdades;

        private ResumenPatologia(String codigo, String nombre) {
            this.codigo = codigo;
            this.nombre = nombre;
        }

        private void agregar(Atencion atencion) {
            total++;
            if (atencion.getTriage() >= 1 && atencion.getTriage() <= 5) {
                porTriage[atencion.getTriage() - 1]++;
            }
            if ("F".equalsIgnoreCase(atencion.getSexo())) {
                femeninos++;
            } else if ("M".equalsIgnoreCase(atencion.getSexo())) {
                masculinos++;
            }
            sumaEdades += atencion.getEdad();
        }

        private String comoFila(int totalGeneral) {
            int casosCriticos = porTriage[0] + porTriage[1];
            double porcentajeTotal = total * 100.0 / totalGeneral;
            double porcentajeCriticos = casosCriticos * 100.0 / total;
            double edadPromedio = sumaEdades * 1.0 / total;

            return String.join(SEPARADOR,
                codigo, nombre,
                String.valueOf(total),
                String.format(java.util.Locale.US, "%.2f", porcentajeTotal),
                String.valueOf(porTriage[0]), String.valueOf(porTriage[1]),
                String.valueOf(porTriage[2]), String.valueOf(porTriage[3]),
                String.valueOf(porTriage[4]), String.valueOf(casosCriticos),
                String.format(java.util.Locale.US, "%.2f", porcentajeCriticos),
                String.valueOf(femeninos), String.valueOf(masculinos),
                String.format(java.util.Locale.US, "%.2f", edadPromedio));
        }
    }
}
