package com.thanos.AlgoritmosNumeros.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Calculadora {

    // Método para tokenizar una expresión (soporta números, decimales, variables y operadores)
    public static List<String> tokenizar(String expresion) {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        int n = expresion.length();

        while (i < n) {
            char c = expresion.charAt(i);

            // Ignorar espacios en blanco
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            // Si es letra, dígito o punto decimal (para números con decimales o variables)
            if (Character.isLetterOrDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < n && (Character.isLetterOrDigit(expresion.charAt(i)) || expresion.charAt(i) == '.')) {
                    sb.append(expresion.charAt(i));
                    i++;
                }
                tokens.add(sb.toString());
            } else {
                // Operadores o paréntesis
                tokens.add(String.valueOf(c));
                i++;
            }
        }
        return tokens;
    }

    // Método para convertir de infijo a postfijo usando el algoritmo de Shunting-yard
    public static String convertirAPostfix(String expresion) {
        if (expresion == null || expresion.trim().isEmpty()) {
            throw new IllegalArgumentException("La expresión no puede estar vacía");
        }

        List<String> tokens = tokenizar(expresion);
        Stack<String> pila = new Stack<>();
        List<String> resultado = new ArrayList<>();

        for (String token : tokens) {
            if (esOperando(token)) {
                resultado.add(token);
            } else if (token.equals("(")) {
                pila.push(token);
            } else if (token.equals(")")) {
                while (!pila.isEmpty() && !pila.peek().equals("(")) {
                    resultado.add(pila.pop());
                }
                if (!pila.isEmpty() && pila.peek().equals("(")) {
                    pila.pop(); // Elimina el "(" de la pila
                } else {
                    throw new IllegalArgumentException("Paréntesis no balanceados");
                }
            } else if (esOperador(token)) {
                while (!pila.isEmpty() && esOperador(pila.peek()) && 
                       (obtenerPrecedencia(pila.peek()) > obtenerPrecedencia(token) ||
                        (obtenerPrecedencia(pila.peek()) == obtenerPrecedencia(token) && !token.equals("^")))) {
                    resultado.add(pila.pop());
                }
                pila.push(token);
            } else {
                throw new IllegalArgumentException("Símbolo no reconocido: " + token);
            }
        }

        // Vaciar los operadores restantes en la pila
        while (!pila.isEmpty()) {
            String top = pila.pop();
            if (top.equals("(") || top.equals(")")) {
                throw new IllegalArgumentException("Paréntesis no balanceados");
            }
            resultado.add(top);
        }

        // Construir la cadena de salida separada por espacios
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < resultado.size(); i++) {
            sb.append(resultado.get(i));
            if (i < resultado.size() - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    // Método para evaluar la expresión postfija numéricamente
    public static Double evaluarPostfix(String postfix) {
        if (postfix == null || postfix.trim().isEmpty()) {
            throw new IllegalArgumentException("La expresión postfija está vacía");
        }

        String[] tokens = postfix.split(" ");
        Stack<Double> pila = new Stack<>();

        for (String token : tokens) {
            if (token.isEmpty()) {
                continue;
            }

            if (esNumero(token)) {
                pila.push(Double.parseDouble(token));
            } else if (esOperador(token)) {
                if (pila.size() < 2) {
                    throw new IllegalArgumentException("Faltan operandos para el operador: " + token);
                }
                double val2 = pila.pop();
                double val1 = pila.pop();
                double res = 0;

                switch (token) {
                    case "+":
                        res = val1 + val2;
                        break;
                    case "-":
                        res = val1 - val2;
                        break;
                    case "*":
                        res = val1 * val2;
                        break;
                    case "/":
                        if (val2 == 0) {
                            throw new ArithmeticException("División por cero");
                        }
                        res = val1 / val2;
                        break;
                    case "^":
                        res = Math.pow(val1, val2);
                        break;
                }
                pila.push(res);
            } else {
                // Si contiene variables de texto (como 'A', 'B'), no se puede evaluar numéricamente
                throw new IllegalArgumentException("La expresión contiene variables y no se puede evaluar numéricamente");
            }
        }

        if (pila.size() != 1) {
            throw new IllegalArgumentException("Expresión postfija inválida, operandos sobrantes");
        }

        return pila.pop();
    }

    private static boolean esOperando(String token) {
        if (token.equals("(") || token.equals(")")) {
            return false;
        }
        return !esOperador(token);
    }

    private static boolean esOperador(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("^");
    }

    private static int obtenerPrecedencia(String operador) {
        switch (operador) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;
            default:
                return -1;
        }
    }

    private static boolean esNumero(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
