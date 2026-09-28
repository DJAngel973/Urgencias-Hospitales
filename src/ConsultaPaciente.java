import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Módulo de consulta individual de paciente por nombre completo.
 */
public class ConsultaPaciente {
    private final LectorDatos datos;
    private final List<Atencion> atenciones;

    public ConsultaPaciente(LectorDatos datos) {
        this.datos = datos;
        this.atenciones = datos.getAtenciones();
    }

    public String consultarHistorial(String nombreCompleto) {
        String busqueda = nombreCompleto.trim().toLowerCase();

        List<Atencion> historial = atenciones.stream()
            .filter(a -> a.getNombreCompleto().toLowerCase().contains(busqueda))
            .sorted(Comparator.comparing(Atencion::getFecha))
            .collect(Collectors.toList());

        if (historial.isEmpty()) {
            return "❌ No se encontró ningún paciente con: " + nombreCompleto;
        }

        Atencion primera = historial.get(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        StringBuilder sb = new StringBuilder();
        sb.append("════════════════════════════════════════════════\n");
        sb.append("      HISTORIAL DE ATENCIÓN EN URGENCIAS\n");
        sb.append("════════════════════════════════════════════════\n\n");
        sb.append("Paciente: ").append(primera.getNombreCompleto()).append("\n");
        sb.append("Edad: ").append(primera.getEdad()).append(" años\n");
        sb.append("Sexo: ").append(primera.getSexo().equals("F") ? "Femenino" : "Masculino").append("\n");
        sb.append("Número de atenciones registradas: ").append(historial.size()).append("\n\n");
        sb.append("────────────────────────────────────────────────\n");
        sb.append(String.format("%-12s %-7s %s%n", "Fecha", "Triage", "Diagnóstico"));
        sb.append("────────────────────────────────────────────────\n");

        for (Atencion a : historial) {
            sb.append(String.format("%-12s %-7d %s - %s%n",
                a.getFecha().format(fmt),
                a.getTriage(),
                a.getCie10(),
                datos.nombreEnfermedad(a.getCie10())));
        }

        // Resumen
        Map<Integer, Long> triageCount = historial.stream()
            .collect(Collectors.groupingBy(Atencion::getTriage, Collectors.counting()));

        Map<String, Long> diagCount = historial.stream()
            .collect(Collectors.groupingBy(Atencion::getCie10, Collectors.counting()));

        String diagFrecuente = diagCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(e -> e.getKey() + " - " + datos.nombreEnfermedad(e.getKey()) + " (" + e.getValue() + " atenciones)")
            .orElse("N/A");

        String ultimaFecha = historial.stream()
            .max(Comparator.comparing(Atencion::getFecha))
            .map(a -> a.getFecha().format(fmt))
            .orElse("N/A");

        sb.append("\n────────────────────────────────────────────────\n");
        sb.append("RESUMEN DEL HISTORIAL\n");
        for (Map.Entry<Integer, Long> e : triageCount.entrySet()) {
            sb.append(String.format("  Triage %d: %d atención(es)%n", e.getKey(), e.getValue()));
        }
        sb.append("\nDiagnósticos registrados: ").append(diagCount.size()).append("\n");
        sb.append("Diagnóstico más frecuente:\n  ").append(diagFrecuente).append("\n");
        sb.append("Última atención: ").append(ultimaFecha).append("\n");

        return sb.toString();
    }

    // Lista de pacientes únicos para sugerir en la interfaz
    public List<String> listaPacientesUnicos() {
        return atenciones.stream()
            .map(Atencion::getNombreCompleto)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
}