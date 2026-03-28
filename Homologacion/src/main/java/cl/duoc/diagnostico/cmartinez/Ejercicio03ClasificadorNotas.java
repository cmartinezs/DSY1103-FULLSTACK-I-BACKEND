package cl.duoc.diagnostico.cmartinez;

public class Ejercicio03ClasificadorNotas {
  static void main(){
    double nota = 46;
    String nombreAlumno = "Carlos";

    String clasificacion;
    String mensaje;

    if(nota >= 1.0 && nota <= 3.9) {
      clasificacion = "Reprobado";
      mensaje = "No aprobaste. Debes rendir el examen de repetición.";
    } else if (nota >= 4.0 && nota <= 4.9) {
      clasificacion = "Suficiente";
      mensaje = "Aprobado, pero puedes mejorar.";
    } else if (nota >= 5.0 && nota <= 5.9) {
      clasificacion = "Bueno";
      mensaje = "Buen trabajo, sigue así.";
    } else if (nota >= 6.0 && nota <= 7.0) {
      clasificacion = "Excelente";
      mensaje = "¡Felicitaciones! Rendimiento sobresaliente.";
    } else {
      System.out.println("Nota inválida.");
      return;
    }

    System.out.printf(
    """
    Alumno  : %s
    Nota    : %f
    Estado  : %s
    Mensaje : %s
    """, nombreAlumno, nota, clasificacion, mensaje);
  }
}
