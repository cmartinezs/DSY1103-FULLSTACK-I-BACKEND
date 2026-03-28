package cl.duoc.diagnostico.cmartinez;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Ejercicio06ListaCompras {
  static void main(){
    List<String> carrito = new ArrayList<>();
    carrito.add("Leche");
    carrito.add("Pan");
    carrito.add("Huevos");
    carrito.add("Mantequilla");
    carrito.add("Jugo");

    System.out.println("=== Carrito de compras ===");
    for (int i = 0; i < carrito.size(); i++) {
      System.out.println((i+1) + ". " + carrito.get(i));
    }
    System.out.println("Total de productos: " + carrito.size());

    String aBuscar= "Huevos";
    boolean encontrado = false;
    for(String producto: carrito){
      if(producto.equals(aBuscar)){
        encontrado = true;
        break;
      }
    }

    /*Optional<String> found =
        carrito.stream()
        .filter(producto -> !producto.equals(aBuscar))
            .findFirst();

    List<Integer> list = carrito.stream()
        .filter(producto -> !producto.equals(aBuscar))
        .map(producto -> producto.length())
        .toList();*/

    if (encontrado){
      System.out.println("✔ Huevos está en el carrito.");
    } else {
      System.out.println("✘ Huevos no está en el carrito.");
    }

    carrito.remove("Pan");

    System.out.println("=== Carrito de compras ===");
    for (int i = 0; i < carrito.size(); i++) {
      System.out.println((i+1) + ". " + carrito.get(i));
    }
    System.out.println("Total de productos: " + carrito.size());
  }
}
