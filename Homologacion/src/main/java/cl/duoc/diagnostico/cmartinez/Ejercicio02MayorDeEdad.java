package cl.duoc.diagnostico.cmartinez;

public class Ejercicio02MayorDeEdad {
  public static void main(String[] args) {
    int edad = 10;
    String nombre = "Carlos";

    if (edad >= 18) {
      System.out.println("Bienvenido/a, " + nombre + ".");
      System.out.println("Puedes completar el trámite de forma autónoma.");
    } else {
      System.out.println("Hola, " + nombre + ".");
      System.out.println("Debes asistir con tu tutor legal para completar este trámite.");
      System.out.println("Tu edad registrada: " + edad + " años.");
    }
  }
}
