package com.thanos.AlgoritmosNumeros.Controllers;

import com.thanos.AlgoritmosNumeros.Model.Calculadora;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CalculadoraController {

    @PostMapping("/expresion")
    public Map<String, Object> calcularinfix(@RequestBody Map<String, String> body) {
        Map<String, Object> respuesta = new HashMap<>();

        // Obtener la expresión del JSON, soportando "infix" o "expresion"
        String infix = body.get("infix");
        if (infix == null) {
            infix = body.get("expresion");
        }

        if (infix == null || infix.trim().isEmpty()) {
            respuesta.put("status", "error");
            respuesta.put("message", "La expresión 'infix' o 'expresion' es requerida en el cuerpo de la petición JSON");
            return respuesta;
        }

        try {
            // Conversión a postfix
            String postfix = Calculadora.convertirAPostfix(infix);
            respuesta.put("status", "success");
            respuesta.put("infix", infix);
            respuesta.put("postfix", postfix);

            // Intentar evaluar si es numérica
            try {
                Double resultado = Calculadora.evaluarPostfix(postfix);
                respuesta.put("resultado", resultado);
            } catch (ArithmeticException e) {
                // Capturar específicamente errores aritméticos como división por cero
                respuesta.put("resultado", "Error: " + e.getMessage());
            } catch (Exception e) {
                // Si falla por variables u otra razón
                respuesta.put("resultado", "No evaluable (contiene variables)");
            }

        } catch (IllegalArgumentException e) {
            respuesta.put("status", "error");
            respuesta.put("message", e.getMessage());
        } catch (Exception e) {
            respuesta.put("status", "error");
            respuesta.put("message", "Error interno al procesar la expresión: " + e.getMessage());
        }

        return respuesta;
    }
}
