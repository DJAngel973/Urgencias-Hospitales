# Proyecto de Fundamentos de Programación: generación de archivos para un sistema hospitalario en Java

Proyecto académico de la asignatura **Fundamentos de Programación**. Esta entrega presenta una aplicación de consola desarrollada en Java para generar archivos de texto con el proyecto escogido para un servicio de urgencias hospitalarias.

> **Decisión del grupo**
>
> Tomamos como base la propuesta de desarrollo planteada por nuestra compañera **Luna**, por considerarla una alternativa clara para resolver la primera entrega.

## Objetivo

Practicar los fundamentos de:

- Creación y uso de clases y objetos.
- Definición y uso de métodos.
- Manejo de arreglos y listas.
- Generación de datos aleatorios.
- Manejo de fechas.
- Creación y escritura de archivos de texto.
- Manejo básico de excepciones.

Esta primera entrega se concentra en el diseño e implementación de la clase `GenerateInfoFiles`, encargada de generar los archivos planos de entrada que utilizará el programa principal en las siguientes entregas.

## Estructura del proyecto

```text
ProyectoHospital/
├── data/
│   ├── cie10.csv
│   └── urgencias.txt
├── src/
│   ├── GenerateInfoFiles.java
│   └── Main.java
└── README.md
```

## ¿Qué hace `GenerateInfoFiles`?

La clase `GenerateInfoFiles` genera dos archivos dentro de la carpeta `data/`:

### `cie10.csv`

Guarda un diccionario de códigos CIE-10 y sus enfermedades correspondientes:

```text
J00;Resfriado Común
J18;Neumonía
I10;Hipertensión Esencial
```

### `urgencias.txt`

Guarda 300 registros simulados de atención en urgencias. Cada registro utiliza `;` como separador:

```text
NOMBRE;APELLIDO1;APELLIDO2;EDAD;SEXO;CIE10;TRIAGE;FECHA
```

Ejemplo:

```text
Laura;Gómez;Martínez;34;F;J00;4;2026-01-15
```

Los pacientes se seleccionan desde un banco reutilizable para que una misma persona pueda aparecer en varias atenciones. Las enfermedades, edades y niveles de triage se generan con diferentes probabilidades para producir datos más realistas.

## Clase `GenerateInfoFiles`

Esta es la clase central de la primera entrega. De acuerdo con las especificaciones, debe ejecutarse sin solicitar información al usuario y generar automáticamente los archivos planos pseudoaleatorios que servirán como entrada para la segunda clase con método `main`.

### Constantes principales

```java
private static final String CARPETA_DATOS = "data";
private static final int CANTIDAD_ATENCIONES = 300;
private static final long SEMILLA = 2026L;
```

- `CARPETA_DATOS`: indica dónde se guardan los archivos.
- `CANTIDAD_ATENCIONES`: define cuántos registros se generan.
- `SEMILLA`: permite que los datos aleatorios sean reproducibles durante las pruebas.

### Clase interna `Enfermedad`

```java
private static class Enfermedad {
    final String codigo;
    final String nombre;
    final int peso;
}
```

Representa una enfermedad mediante su código, nombre y peso de frecuencia. El peso se utiliza para que algunas enfermedades aparezcan más veces que otras.

### Constructor

```java
public GenerateInfoFiles()
```

Inicializa el objeto `Random` y carga el diccionario base de enfermedades mediante `inicializarDiccionarioCie10()`.

### `inicializarDiccionarioCie10()`

Agrega enfermedades a la lista `enfermedades` usando objetos de la clase interna `Enfermedad`.

### `generarArchivoCie10()`

Utiliza un `BufferedWriter` para recorrer la lista de enfermedades y escribir cada código y nombre en `data/cie10.csv`.

### `generarArchivoUrgencias()`

Realiza los siguientes pasos:

1. Calcula el peso total de las enfermedades.
2. Crea un banco de pacientes reutilizables.
3. Genera fechas entre el 1 de enero y el 31 de diciembre de 2026.
4. Selecciona pacientes, diagnósticos y niveles de triage.
5. Construye cada línea con `String.join(";", ...)`.
6. Escribe las 300 líneas en `data/urgencias.txt`.

### Métodos auxiliares

| Método | Función |
|---|---|
| `elegirCie10Ponderado()` | Selecciona un diagnóstico respetando sus pesos de frecuencia. |
| `generarTriagePonderado()` | Genera un nivel de triage del 1 al 5 con distintas probabilidades. |
| `generarEdad()` | Genera edades con diferentes probabilidades por grupo etario. |

## Librerías utilizadas

Todas pertenecen a la biblioteca estándar de Java; no se utilizan frameworks ni bases de datos.

| Librería | Uso |
|---|---|
| `java.io.BufferedWriter` | Escribir texto eficientemente en los archivos. |
| `java.io.FileWriter` | Abrir archivos para escritura. |
| `java.io.IOException` | Representar errores relacionados con archivos. |
| `java.time.LocalDate` | Representar fechas. |
| `java.time.temporal.ChronoUnit` | Calcular los días entre dos fechas. |
| `java.util.ArrayList` | Crear listas dinámicas. |
| `java.util.List` | Trabajar con listas mediante una abstracción general. |
| `java.util.Random` | Generar nombres, edades, diagnósticos y fechas aleatorias. |

## Creación y manejo de archivos

En esta entrega, el manejo de archivos se refiere principalmente a su **creación y escritura**, no a la lectura o modificación de archivos existentes.

El proceso realizado por `GenerateInfoFiles` es:

1. Definir la carpeta y las rutas de los archivos mediante constantes.
2. Crear la carpeta `data` si todavía no existe.
3. Crear o reemplazar el archivo `cie10.csv`.
4. Recorrer la lista de enfermedades y escribir cada código y nombre separados por `;`.
5. Crear o reemplazar el archivo `urgencias.txt`.
6. Generar los registros de atención y escribirlos línea por línea.
7. Cerrar automáticamente los archivos.
8. Mostrar un mensaje de finalización exitosa o un mensaje de error.

La escritura se realiza mediante `try-with-resources`:

```java
try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_CIE10))) {
    // Escritura del archivo
}
```

`FileWriter` abre el archivo para escritura. Si el archivo ya existe, su contenido anterior se reemplaza. `BufferedWriter` permite escribir texto de forma más eficiente y ofrece métodos como `write()` y `newLine()`.

El bloque `try-with-resources` cierra automáticamente el archivo al finalizar, incluso si ocurre un error. Los métodos declaran `throws IOException` y el método `main` captura la excepción para mostrar un mensaje al usuario.

Las rutas utilizadas son relativas al directorio desde el que se ejecuta el programa:

```java
private static final String CARPETA_DATOS = "data";
private static final String ARCHIVO_CIE10 = CARPETA_DATOS + "/cie10.csv";
private static final String ARCHIVO_URGENCIAS = CARPETA_DATOS + "/urgencias.txt";
```

Por esta razón, los archivos se almacenan dentro de la carpeta `data` del proyecto. Aunque uno de los archivos tiene extensión `.csv`, los datos utilizan punto y coma (`;`) como separador en lugar de coma.

## Alcance de esta entrega

Esta entrega se enfoca en la **creación, escritura y control básico de archivos de texto** mediante `GenerateInfoFiles`.
