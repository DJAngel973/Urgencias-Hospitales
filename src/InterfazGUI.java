import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Interfaz gráfica principal del sistema (Swing).
 * Tiene 4 pestañas: Archivos, Análisis Epidemiológico, Consulta Paciente, Reporte.
 */
public class InterfazGUI extends JFrame {
    private LectorDatos datos;
    private AnalizadorEpidemiologico analizador;
    private ConsultaPaciente consultaPaciente;

    private JTextArea areaResultados;
    private JTextField fieldBusqueda;
    private JComboBox<String> comboPacientes;
    private JLabel estadoLabel;

    public InterfazGUI() {
        setTitle("🏥 Sistema de Urgencias Hospital - Análisis Epidemiológico");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane pestañas = new JTabbedPane();
        pestañas.addTab("📁 Archivos", panelArchivos());
        pestañas.addTab("📊 Análisis Epidemiológico", panelAnalisis());
        pestañas.addTab("👤 Consultar Paciente", panelConsulta());
        pestañas.addTab("📄 Reporte Completo", panelReporte());

        add(pestañas, BorderLayout.CENTER);

        // Barra de estado inferior
        estadoLabel = new JLabel(" Listo. Seleccione una pestaña para comenzar.");
        estadoLabel.setBorder(BorderFactory.createEtchedBorder());
        add(estadoLabel, BorderLayout.SOUTH);
    }

    // ───────── Pestaña 1: Archivos ─────────
    private JPanel panelArchivos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Generación de Archivos de Entrada", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titulo, BorderLayout.NORTH);

        JTextArea info = new JTextArea(
            "Esta opción genera los archivos planos que sirven como entrada al sistema:\n\n" +
            "  • data/cie10.csv     → Diccionario de códigos CIE-10\n" +
            "  • data/urgencias.txt → Historial de atenciones (300 registros)\n\n" +
            "Después de generar los archivos, puede pasar a las pestañas de análisis."
        );
        info.setEditable(false);
        info.setFont(new Font("Monospaced", Font.PLAIN, 13));
        panel.add(new JScrollPane(info), BorderLayout.CENTER);

        JButton btnGenerar = new JButton("🔄 Generar Archivos");
        btnGenerar.setFont(new Font("Arial", Font.BOLD, 14));
        btnGenerar.addActionListener(e -> {
            try {
                GenerateInfoFiles gen = new GenerateInfoFiles();
                new java.io.File("data").mkdirs();
                gen.generarArchivoCie10();
                gen.generarArchivoUrgencias();
                JOptionPane.showMessageDialog(this,
                    "✅ Archivos generados exitosamente en la carpeta data/",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                estadoLabel.setText(" Archivos generados. Ahora puede cargarlos y analizarlos.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(btnGenerar, BorderLayout.SOUTH);
        return panel;
    }

    // ───────── Pestaña 2: Análisis Epidemiológico ─────────
    private JPanel panelAnalisis() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de botones (izquierda)
        JPanel panelBotones = new JPanel(new GridLayout(0, 1, 5, 5));
        panelBotones.setPreferredSize(new Dimension(230, 0));

        JButton btnCargar = new JButton("📥 Cargar Datos");
        JButton btnEnfFrec = new JButton("🦠 Enfermedades más frecuentes");
        JButton btnTriage = new JButton("🚑 Distribución por triage");
        JButton btnEnfTriage = new JButton("🏥 Enfermedades por triage");
        JButton btnEdad = new JButton("🎂 Distribución por edad");
        JButton btnEnfEdad = new JButton("👶👴 Enfermedades por edad");
        JButton btnSexo = new JButton("⚧ Distribución por sexo");
        JButton btnEnfSexo = new JButton("⚕ Enfermedades por sexo");
        JButton btnMes = new JButton("📅 Atenciones por mes");
        JButton btnPromEdad = new JButton("📊 Promedio de edad");
        JButton btnCriticos = new JButton("⚠ Casos críticos");
        JButton btnTendencias = new JButton("💊 Tendencias / Medicamentos");

        panelBotones.add(btnCargar);
        panelBotones.add(new JSeparator());
        panelBotones.add(btnEnfFrec);
        panelBotones.add(btnTriage);
        panelBotones.add(btnEnfTriage);
        panelBotones.add(btnEdad);
        panelBotones.add(btnEnfEdad);
        panelBotones.add(btnSexo);
        panelBotones.add(btnEnfSexo);
        panelBotones.add(btnMes);
        panelBotones.add(btnPromEdad);
        panelBotones.add(btnCriticos);
        panelBotones.add(btnTendencias);

        panel.add(panelBotones, BorderLayout.WEST);

        // Área de resultados
        areaResultados = new JTextArea();
        areaResultados.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaResultados.setEditable(false);
        panel.add(new JScrollPane(areaResultados), BorderLayout.CENTER);

        // Acciones de botones
        btnCargar.addActionListener(e -> cargarDatos());

        btnEnfFrec.addActionListener(e -> ejecutarAnalisis(() -> analizador.enfermedadesMasFrecuentes(10)));
        btnTriage.addActionListener(e -> ejecutarAnalisis(() -> analizador.distribucionPorTriage()));
        btnEnfTriage.addActionListener(e -> ejecutarAnalisis(() -> analizador.enfermedadesPorTriage(3)));
        btnEdad.addActionListener(e -> ejecutarAnalisis(() -> analizador.distribucionPorEdad()));
        btnEnfEdad.addActionListener(e -> ejecutarAnalisis(() -> analizador.enfermedadesPorGrupoEdad(3)));
        btnSexo.addActionListener(e -> ejecutarAnalisis(() -> analizador.distribucionPorSexo()));
        btnEnfSexo.addActionListener(e -> ejecutarAnalisis(() -> analizador.enfermedadesPorSexo(5)));
        btnMes.addActionListener(e -> ejecutarAnalisis(() -> analizador.atencionesPorMes()));
        btnPromEdad.addActionListener(e -> ejecutarAnalisis(() -> analizador.promedioEdad()));
        btnCriticos.addActionListener(e -> ejecutarAnalisis(() -> analizador.casosCriticos()));
        btnTendencias.addActionListener(e -> ejecutarAnalisis(() -> analizador.tendenciasMedicamentos()));

        return panel;
    }

    // ───────── Pestaña 3: Consultar Paciente ─────────
    private JPanel panelConsulta() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel superior = new JPanel(new BorderLayout(5, 5));
        superior.add(new JLabel("Seleccione o escriba el nombre del paciente:"), BorderLayout.NORTH);

        comboPacientes = new JComboBox<>();
        comboPacientes.setEditable(true);
        superior.add(comboPacientes, BorderLayout.CENTER);

        JButton btnBuscar = new JButton("🔍 Buscar Historial");
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 13));
        superior.add(btnBuscar, BorderLayout.EAST);

        panel.add(superior, BorderLayout.NORTH);

        JTextArea areaPaciente = new JTextArea();
        areaPaciente.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaPaciente.setEditable(false);
        panel.add(new JScrollPane(areaPaciente), BorderLayout.CENTER);

        JButton btnCargarPacientes = new JButton("📥 Cargar lista de pacientes");
        btnCargarPacientes.addActionListener(e -> {
            if (cargarDatos()) {
                comboPacientes.removeAllItems();
                for (String p : consultaPaciente.listaPacientesUnicos()) {
                    comboPacientes.addItem(p);
                }
                estadoLabel.setText(" Pacientes cargados: " + comboPacientes.getItemCount());
            }
        });
        panel.add(btnCargarPacientes, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> {
            if (analizador == null) {
                JOptionPane.showMessageDialog(this, "Primero cargue los datos.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String nombre = (String) comboPacientes.getSelectedItem();
            if (nombre != null && !nombre.trim().isEmpty()) {
                areaPaciente.setText(consultaPaciente.consultarHistorial(nombre));
                areaPaciente.setCaretPosition(0);
            }
        });

        return panel;
    }

    // ───────── Pestaña 4: Reporte Completo ─────────
    private JPanel panelReporte() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextArea areaReporte = new JTextArea();
        areaReporte.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaReporte.setEditable(false);
        panel.add(new JScrollPane(areaReporte), BorderLayout.CENTER);

        JButton btnGenerarReporte = new JButton("📄 Generar Reporte Epidemiológico Completo");
        btnGenerarReporte.setFont(new Font("Arial", Font.BOLD, 14));
        btnGenerarReporte.addActionListener(e -> {
            if (cargarDatos()) {
                areaReporte.setText(analizador.reporteCompleto());
                areaReporte.setCaretPosition(0);
                estadoLabel.setText(" Reporte completo generado.");
            }
        });
        panel.add(btnGenerarReporte, BorderLayout.NORTH);
        return panel;
    }

    // ───────── Métodos auxiliares ─────────
    private boolean cargarDatos() {
        datos = new LectorDatos();
        if (!datos.cargarDatos()) {
            JOptionPane.showMessageDialog(this,
                "❌ No se pudieron cargar los archivos.\n" +
                "Asegúrese de haberlos generado primero (pestaña 'Archivos').",
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        analizador = new AnalizadorEpidemiologico(datos);
        consultaPaciente = new ConsultaPaciente(datos);
        estadoLabel.setText(" Datos cargados: " + datos.getAtenciones().size() + " atenciones, "
            + datos.getDiccionarioCIE10().size() + " códigos CIE-10.");
        return true;
    }

    private void ejecutarAnalisis(java.util.function.Supplier<String> accion) {
        if (analizador == null) {
            if (!cargarDatos()) return;
        }
        areaResultados.setText(accion.get());
        areaResultados.setCaretPosition(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new InterfazGUI().setVisible(true);
        });
    }
}