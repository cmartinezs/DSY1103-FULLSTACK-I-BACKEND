package cl.duoc.fullstack.tickets.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormulasServiceTest {
    @Mock
    Utiles utilesMock;

    @InjectMocks
    FormulasService service;

    @Test
    void perimetro() {
        double[] lados = new double[] {2,3,5,7,4};

        when(utilesMock.esPar(2)).thenReturn(false);
        when(utilesMock.esPar(3)).thenReturn(false);
        when(utilesMock.esPar(5)).thenReturn(false);
        when(utilesMock.esPar(7)).thenReturn(false);
        when(utilesMock.esPar(4)).thenReturn(true);

        double perimetro = service.perimetro(lados);


        assertThat(perimetro).isEqualTo(6);
    }
}