package cl.duoc.diagnostico.cmartinez;

public class Ejercicio01PresentacionPersonal {
  public static void main(String[] args) {
    String nombre = "Carlos Martínez";
    int edad = 40;
    String carrera = "Ing Informática";
    int semestre = 10;
    boolean tieneExperienciaLaboral = true;

    String respuesta;

    if (tieneExperienciaLaboral) {
      respuesta = "Si";
    } else {
      respuesta = "No";
    }

    System.out.println("=== Tarjeta de Presentación ===");
    System.out.println("Nombre   : " + nombre);
    System.out.println("Edad     : " + edad + " años");
    System.out.println("Carrera  : " + carrera);
    System.out.println("Semestre : " + semestre);
    System.out.println("Experiencia laboral: " + respuesta);
    System.out.println("Experiencia laboral: " + (tieneExperienciaLaboral ? "Sí" : "No"));
  }
}
