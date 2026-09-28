import java.time.LocalDate;

/**
 * Representa una atención de urgencias (una línea de urgencias.txt)
 */
public class Atencion {
    private final String nombre;
    private final String apellido1;
    private final String apellido2;
    private final int edad;
    private final String sexo;
    private final String cie10;
    private final int triage;
    private final LocalDate fecha;

    public Atencion(String nombre, String apellido1, String apellido2,
                    int edad, String sexo, String cie10, int triage, LocalDate fecha) {
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.edad = edad;
        this.sexo = sexo;
        this.cie10 = cie10;
        this.triage = triage;
        this.fecha = fecha;
    }

    // Getters
    public String getNombreCompleto() { return nombre + " " + apellido1 + " " + apellido2; }
    public String getNombre() { return nombre; }
    public String getApellido1() { return apellido1; }
    public String getApellido2() { return apellido2; }
    public int getEdad() { return edad; }
    public String getSexo() { return sexo; }
    public String getCie10() { return cie10; }
    public int getTriage() { return triage; }
    public LocalDate getFecha() { return fecha; }
} 
    

