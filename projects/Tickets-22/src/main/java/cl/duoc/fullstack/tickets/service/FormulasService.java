package cl.duoc.fullstack.tickets.service;

import com.jayway.jsonpath.internal.Utils;

public class FormulasService {
    private final Utiles utiles;
    public FormulasService(Utiles utiles) {
        this.utiles = utiles;
    }
    public double perimetro(double[] largoLados){
        double sumatoria = 0.0;
        for (double lado : largoLados) {
            if(utiles.esPar(lado)){
                sumatoria += lado;
            }
        }
        return sumatoria;
    }
}
