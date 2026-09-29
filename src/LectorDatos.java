import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Carga en memoria el diccionario CIE-10 y las atenciones del hospital.
 *
 * Usa rutas relativas a la carpeta desde la que se ejecuta el programa:
 * {@code data/cie10.csv} contiene código y descripción, mientras que
 * {@code data/urgencias.txt} contiene ocho campos separados por punto y coma.
 */
public class LectorDatos {
    private final Map<String, String> diccionarioCIE10 = new LinkedHashMap<>();
    private final List<Atencion> atenciones = new ArrayList<>();

    /** Carga ambos archivos y solo devuelve {@code true} si los dos pudieron leerse. */
    public boolean cargarDatos() {
        return cargarCIE10() && cargarUrgencias();
    }

    /** Lee el diccionario y guarda sus códigos y nombres en un mapa ordenado. */
    private boolean cargarCIE10() {
        try (BufferedReader br = new BufferedReader(new FileReader("data/cie10.csv"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length >= 2) {
                    diccionarioCIE10.put(partes[0].trim(), partes[1].trim());
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error leyendo cie10.csv: " + e.getMessage());
            return false;
        }
    }

    /** Convierte cada línea válida del archivo de urgencias en un objeto {@link Atencion}. */
    private boolean cargarUrgencias() {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        try (BufferedReader br = new BufferedReader(new FileReader("data/urgencias.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(";");
                if (p.length >= 8) {
                    Atencion a = new Atencion(
                        p[0].trim(), p[1].trim(), p[2].trim(),
                        Integer.parseInt(p[3].trim()),
                        p[4].trim(),
                        p[5].trim(),
                        Integer.parseInt(p[6].trim()),
                        LocalDate.parse(p[7].trim(), fmt)
                    );
                    atenciones.add(a);
                }
            }
            return true;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error leyendo urgencias.txt: " + e.getMessage());
            return false;
        }
    }

    /** Devuelve el diccionario cargado para que otros módulos traduzcan códigos. */
    public Map<String, String> getDiccionarioCIE10() { return diccionarioCIE10; }
    /** Devuelve la lista de atenciones cargadas para análisis y consultas. */
    public List<Atencion> getAtenciones() { return atenciones; }

    /** Busca la descripción de un código; si no existe, devuelve el propio código. */
    public String nombreEnfermedad(String codigo) {
        return diccionarioCIE10.getOrDefault(codigo, codigo);
    }
}
