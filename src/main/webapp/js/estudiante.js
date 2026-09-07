/**
 * Clase Estudiante (Programación Orientada a Objetos en JavaScript).
 * Encapsula los datos y la lógica de cálculo de un estudiante, para que
 * el resto del código (app.js) solo tenga que crear objetos y leer sus
 * resultados, sin repetir las reglas de negocio.
 */
class Estudiante {

    static NOTA_MINIMA = 0.0;
    static NOTA_MAXIMA = 5.0;

    constructor(nombre, nota1, nota2, nota3, nota4, id = null) {
        this.id = id;
        this.nombre = nombre;
        this.nota1 = nota1;
        this.nota2 = nota2;
        this.nota3 = nota3;
        this.nota4 = nota4;
        this.promedio = 0.0;
        this.resultado = "";
    }

    /** Calcula el promedio de las cuatro notas, redondeado a un decimal y limitado a [0.0, 5.0]. */
    calcularPromedio() {
        const suma = this.nota1 + this.nota2 + this.nota3 + this.nota4;
        let valor = suma / 4;
        valor = Math.round(valor * 10) / 10;
        this.promedio = Math.max(Estudiante.NOTA_MINIMA, Math.min(Estudiante.NOTA_MAXIMA, valor));
        return this.promedio;
    }

    /** El estudiante aprueba si el promedio es igual o superior a 3.0. */
    determinarAprobacion() {
        return this.promedio >= 3.0;
    }

    /** Traduce el promedio cuantitativo a la valoración cualitativa. */
    determinarRendimiento() {
        if (this.promedio < 3.0) {
            this.resultado = "Rendimiento insuficiente (R.I)";
        } else if (this.promedio < 4.0) {
            this.resultado = "Aprobado (A)";
        } else if (this.promedio <= 4.5) {
            this.resultado = "Aprobado con sobresaliente (A.S)";
        } else {
            this.resultado = "Aprobado con excelente (A.E)";
        }
        return this.resultado;
    }

    /** Devuelve la clase CSS (badge de color) correspondiente al resultado. */
    claseResultado() {
        if (this.resultado.includes("R.I")) return "ri";
        if (this.resultado.includes("A.S")) return "as";
        if (this.resultado.includes("A.E")) return "ae";
        return "a";
    }

    /**
     * Valida una nota individual: obligatoria, numérica, sin negativos y
     * sin superar 5.0. Devuelve un mensaje de error o null si es válida.
     */
    static validarNota(valorTexto, etiqueta) {
        if (valorTexto === "" || valorTexto === null || valorTexto === undefined) {
            return `${etiqueta} es obligatoria.`;
        }
        // Un solo punto decimal, sin signo negativo.
        if (!/^\d+(\.\d+)?$/.test(String(valorTexto).trim())) {
            return `${etiqueta} debe ser un número positivo válido.`;
        }
        const nota = parseFloat(valorTexto);
        if (Number.isNaN(nota)) {
            return `${etiqueta} debe ser un valor numérico.`;
        }
        if (nota < Estudiante.NOTA_MINIMA) {
            return `${etiqueta} no puede ser negativa.`;
        }
        if (nota > Estudiante.NOTA_MAXIMA) {
            return `${etiqueta} no puede ser mayor que 5.0.`;
        }
        return null;
    }
}
