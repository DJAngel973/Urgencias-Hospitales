import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Realiza todos los análisis epidemiológicos sobre los datos cargados.
 */
public class AnalizadorEpidemiologico {
    private final LectorDatos datos;
    private final List<Atencion> atenciones;

    public AnalizadorEpidemiologico(LectorDatos datos) {
        this.datos = datos;
        this.atenciones = datos.getAtenciones();
    }

    // ---------------------------------------------------------------
    // 1. ENFERMEDADES MÁS FRECUENTES
    // ---------------------------------------------------------------
    public String enfermedadesMasFrecuentes(int topN) {
        Map<String, Long> conteo = atenciones.stream()
            .collect(Collectors.groupingBy(Atencion::getCie10, Collectors.counting()));

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════\n");
        sb.append("       ENFERMEDADES MÁS FRECUENTES\n");
        sb.append("═══════════════════════════════════════════\n\n");

        int[] i = {1};
        conteo.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(topN)
            .forEach(e -> {
                String nombre = datos.nombreEnfermedad(e.getKey());
                sb.append(String.format("%2d. %-5s %-35s %3d casos%n",
                    i[0]++, e.getKey(), nombre, e.getValue()));
            });

        sb.append("\nTotal de atenciones analizadas: ").append(atenciones.size());
        return sb.toString();
    }

    // ---------------------------------------------------------------
    // 2. DISTRIBUCIÓN POR TRIAGE
    // ---------------------------------------------------------------
    public String distribucionPorTriage() {
        String[] etiquetas = {"Crítico", "Emergencia", "Urgente", "Menos urgente", "No urgente"};
        Map<Integer, Long> conteo = atenciones.stream()
            .collect(Collectors.groupingBy(Atencion::getTriage, Collectors.counting()));

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════\n");
        sb.append("      DISTRIBUCIÓN POR NIVEL DE TRIAGE\n");
        sb.append("═══════════════════════════════════════════\n\n");

        long total = atenciones.size();
        for (int t = 1; t <= 5; t++) {
            long cant = conteo.getOrDefault(t, 0L);
            double porcentaje = (cant * 100.0) / total;
            sb.append(String.format("Triage %d - %-15s %4d pacientes  (%5.1f%%)%n",
                t, etiquetas[t-1], cant, porcentaje));
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------
    // 3. ENFERMEDADES MÁS FRECUENTES SEGÚN TRIAGE
    // ---------------------------------------------------------------
    public String enfermedadesPorTriage(int topPorNivel) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════\n");
        sb.append("  ENFERMEDADES MÁS FRECUENTES POR TRIAGE\n");
        sb.append("═══════════════════════════════════════════\n");

        for (int t = 1; t <= 5; t++) {
            final int triage = t;
            Map<String, Long> conteo = atenciones.stream()
                .filter(a -> a.getTriage() == triage)
                .collect(Collectors.groupingBy(Atencion::getCie10, Collectors.counting()));

            if (conteo.isEmpty()) continue;

            sb.append("\n── TRIAGE ").append(t).append(" ──────────────────────────\n");
            int[] i = {1};
            conteo.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topPorNivel)
                .forEach(e -> sb.append(String.format("   %d. %-5s %-30s %3d casos%n",
                    i[0]++, e.getKey(), datos.nombreEnfermedad(e.getKey()), e.getValue())));
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------
    // 4. DISTRIBUCIÓN POR RANGOS DE EDAD
    // ---------------------------------------------------------------
    private String rangoEdad(int edad) {
        if (edad <= 12) return "0 - 12 años";
        if (edad <= 17) return "13 - 17 años";
        if (edad <= 30) return "18 - 30 años";
        if (edad <= 45) return "31 - 45 años";
        if (edad <= 60) return "46 - 60 años";
        if (edad <= 75) return "61 - 75 años";
        return "76+ años";
    }

    public String distribucionPorEdad() {
        String[] orden = {"0 - 12 años", "13 - 17 años", "18 - 30 años",
                          "31 - 45 años", "46 - 60 años", "61 - 75 años", "76+ años"};
        Map<String, Long> conteo = atenciones.stream()
            .collect(Collectors.groupingBy(a -> rangoEdad(a.getEdad()), Collectors.counting()));

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════\n");
        sb.append("      DISTRIBUCIÓN POR RANGOS DE EDAD\n");
        sb.append("═══════════════════════════════════════════\n\n");

        long max = 0; String grupoMax = "";
        for (String rango : orden) {
            long cant = conteo.getOrDefault(rango, 0L);
            if (cant > max) { max = cant; grupoMax = rango; }
            sb.append(String.format("%-15s %4d pacientes%n", rango, cant));
        }
        sb.append("\n👉 Grupo con más atenciones: ").append(grupoMax)
          .append(" (").append(max).append(" pacientes)");
        return sb.toString();
    }

    // ---------------------------------------------------------------
    // 5. ENFERMEDADES PREDOMINANTES POR GRUPO DE EDAD
    // ---------------------------------------------------------------
    public String enfermedadesPorGrupoEdad(int topPorGrupo) {
        String[] orden = {"0 - 12 años", "13 - 17 años", "18 - 30 años",
                          "31 - 45 años", "46 - 60 años", "61 - 75 años", "76+ años"};

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════\n");
        sb.append("  ENFERMEDADES PREDOMINANTES POR EDAD\n");
        sb.append("═══════════════════════════════════════════\n");

        for (String grupo : orden) {
            Map<String, Long> conteo = atenciones.stream()
                .filter(a -> rangoEdad(a.getEdad()).equals(grupo))
                .collect(Collectors.groupingBy(Atencion::getCie10, Collectors.counting()));

            if (conteo.isEmpty()) continue;

            sb.append("\n── ").append(grupo).append(" ─────────────────\n");
            int[] i = {1};
            conteo.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topPorGrupo)
                .forEach(e -> sb.append(String.format("   %d. %-5s %-28s %3d casos%n",
                    i[0]++, e.getKey(), datos.nombreEnfermedad(e.getKey()), e.getValue())));
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------
    // 6. DISTRIBUCIÓN POR SEXO + ENFERMEDADES POR SEXO
    // ---------------------------------------------------------------
    public String distribucionPorSexo() {
        Map<String, Long> conteo = atenciones.stream()
            .collect(Collectors.groupingBy(Atencion::getSexo, Collectors.counting()));

        long total = atenciones.size();
        long f = conteo.getOrDefault("F", 0L);
        long m = conteo.getOrDefault("M", 0L);

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════\n");
        sb.append("          DISTRIBUCIÓN POR SEXO\n");
        sb.append("═══════════════════════════════════════════\n\n");
        sb.append(String.format("Femenino:  %4d pacientes (%5.1f%%)%n", f, (f*100.0)/total));
        sb.append(String.format("Masculino: %4d pacientes (%5.1f%%)%n", m, (m*100.0)/total));
        return sb.toString();
    }

    public String enfermedadesPorSexo(int topN) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════\n");
        sb.append("       ENFERMEDADES MÁS FRECUENTES POR SEXO\n");
        sb.append("═══════════════════════════════════════════\n");

        for (String sexo : new String[]{"F", "M"}) {
            Map<String, Long> conteo = atenciones.stream()
                .filter(a -> a.getSexo().equals(sexo))
                .collect(Collectors.groupingBy(Atencion::getCie10, Collectors.counting()));

            sb.append("\n── ").append(sexo.equals("F") ? "FEMENINO" : "MASCULINO")
              .append(" ────────────────────────\n");
            int[] i = {1};
            conteo.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topN)
                .forEach(e -> sb.append(String.format("   %d. %-5s %-28s %3d casos%n",
                    i[0]++, e.getKey(), datos.nombreEnfermedad(e.getKey()), e.getValue())));
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------
    // 7. ATENCIONES POR MES (tendencias temporales)
    // ---------------------------------------------------------------
    public String atencionesPorMes() {
        Map<Month, Long> conteo = atenciones.stream()
            .collect(Collectors.groupingBy(a -> a.getFecha().getMonth(), Collectors.counting()));

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════\n");
        sb.append("        ATENCIONES POR MES (2026)\n");
        sb.append("═══════════════════════════════════════════\n\n");

        Month mesMax = null; long max = 0;
        for (Month mes : Month.values()) {
            long cant = conteo.getOrDefault(mes, 0L);
            if (cant > max) { max = cant; mesMax = mes; }
            String barra = "█".repeat((int)(cant / 5.0));
            sb.append(String.format("%-12s %4d  %s%n",
                mes.getDisplayName(TextStyle.FULL, new Locale("es")), cant, barra));
        }
        if (mesMax != null) {
            sb.append("\n📈 Mes con más consultas: ")
              .append(mesMax.getDisplayName(TextStyle.FULL, new Locale("es")))
              .append(" (").append(max).append(" atenciones)");
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------
    // 8. PROMEDIO DE EDAD
    // ---------------------------------------------------------------
    public String promedioEdad() {
        double promedio = atenciones.stream().mapToInt(Atencion::getEdad).average().orElse(0);
        int min = atenciones.stream().mapToInt(Atencion::getEdad).min().orElse(0);
        int max = atenciones.stream().mapToInt(Atencion::getEdad).max().orElse(0);

        return "═══════════════════════════════════════════\n" +
               "          PROMEDIO DE EDAD\n" +
               "═══════════════════════════════════════════\n\n" +
               String.format("Edad promedio: %.1f años%n", promedio) +
               String.format("Edad mínima:   %d años%n", min) +
               String.format("Edad máxima:   %d años%n", max);
    }

    // ---------------------------------------------------------------
    // 9. CASOS CRÍTICOS (Triage 1 y 2)
    // ---------------------------------------------------------------
    public String casosCriticos() {
        List<Atencion> criticos = atenciones.stream()
            .filter(a -> a.getTriage() <= 2)
            .sorted(Comparator.comparing(Atencion::getFecha))
            .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════\n");
        sb.append("     CASOS CRÍTICOS / ALTA PRIORIDAD\n");
        sb.append("     (Triage 1 - Crítico y 2 - Emergencia)\n");
        sb.append("═══════════════════════════════════════════\n\n");
        sb.append("Total de casos críticos: ").append(criticos.size()).append("\n\n");

        Map<String, Long> conteoEnf = criticos.stream()
            .collect(Collectors.groupingBy(Atencion::getCie10, Collectors.counting()));
        sb.append("Enfermedades en casos críticos:\n");
        conteoEnf.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(e -> sb.append(String.format("   %-5s %-30s %3d casos%n",
                e.getKey(), datos.nombreEnfermedad(e.getKey()), e.getValue())));
        return sb.toString();
    }

    // ---------------------------------------------------------------
    // 10. TENDENCIAS / PREVISIÓN DE MEDICAMENTOS
    // ---------------------------------------------------------------
    public String tendenciasMedicamentos() {
        // Top 3 enfermedades + su proporción de casos críticos
        Map<String, Long> conteo = atenciones.stream()
            .collect(Collectors.groupingBy(Atencion::getCie10, Collectors.counting()));

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════\n");
        sb.append("   TENDENCIAS PARA PREVER MEDICAMENTOS\n");
        sb.append("═══════════════════════════════════════════\n\n");
        sb.append("Enfermedades con mayor volumen (revisar stock):\n\n");

        int[] i = {1};
        conteo.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .forEach(e -> {
                long total = e.getValue();
                long criticos = atenciones.stream()
                    .filter(a -> a.getCie10().equals(e.getKey()) && a.getTriage() <= 2)
                    .count();
                sb.append(String.format("%d. %-5s %-28s %3d casos (%.0f%% críticos)%n",
                    i[0]++, e.getKey(), datos.nombreEnfermedad(e.getKey()),
                    total, (criticos * 100.0) / total));
            });

        sb.append("\n💡 Recomendación: priorizar medicamentos para las 3 primeras").append("\n");
        sb.append("   enfermedades y reforzar stock en los meses con más consultas.");
        return sb.toString();
    }

    // ---------------------------------------------------------------
    // REPORTE COMPLETO (todos los análisis juntos)
    // ---------------------------------------------------------------
    public String reporteCompleto() {
        return enfermedadesMasFrecuentes(10) + "\n\n" +
               distribucionPorTriage() + "\n\n" +
               enfermedadesPorTriage(3) + "\n\n" +
               distribucionPorEdad() + "\n\n" +
               enfermedadesPorGrupoEdad(3) + "\n\n" +
               distribucionPorSexo() + "\n\n" +
               enfermedadesPorSexo(5) + "\n\n" +
               atencionesPorMes() + "\n\n" +
               promedioEdad() + "\n\n" +
               casosCriticos() + "\n\n" +
               tendenciasMedicamentos();
    }
}