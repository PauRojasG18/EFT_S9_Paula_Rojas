/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package teatromoroultimo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author paurojas
 */
public class TeatroMoroUltimo {

    static Scanner scanner = new Scanner(System.in);

    static final String[] zonas = {"VIP", "PALCO", "PLATEA BAJA", "PLATEA ALTA", "GALERÍA"};
    static final int[] precioZona = {40000, 30000, 25000, 20000, 10000};
    static final int asientosPorZona = 20;
    static final boolean[][] asientosOcupados = new boolean[zonas.length][asientosPorZona];

    static final List<Reserva> reservas = new ArrayList<>();
    static final List<Venta> ventas = new ArrayList<>();

    static int ventaID = 1;
    
    // MENÚ PRINCIPAL
    
    public static void main(String[] args) {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n\u001B[32m===== BIENVENIDO/A A TEATRO MORO =====\u001B[0m");
            System.out.println("1. Reservar entradas");
            System.out.println("2. Comprar entradas");
            System.out.println("3. Imprimir boleta");
            System.out.println("4. Modificar reserva");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> reservarEntradas();
                case 2 -> comprarEntradas();
                case 3 -> imprimirBoleta();
                case 4 -> modificarReserva();
                case 5 -> salir = true;
                default -> System.out.println("\u001B[31mOpción no válida.\u001B[0m");
            }
        }
    }

    // RESERVA DE LAS ENTRADAS
    static void reservarEntradas() {
        System.out.println("Nombre del comprador: ");
        String nombre = scanner.nextLine();
        Reserva reserva = new Reserva(nombre);
        boolean continuar = true;

        while (continuar) {
            int zona = seleccionarZona();
            int asiento = seleccionarAsiento(zona);
            int edad = solicitarEdad();
            String genero = solicitarGenero();
            boolean esEstudiante = solicitarEstudiante();
            double descuento = calcularDescuento(edad, genero, esEstudiante);

            reserva.agregarEntrada(zona, asiento, edad, genero, esEstudiante, descuento);
            asientosOcupados[zona][asiento] = true;

            System.out.print("\u00bfDesea agregar otra entrada a esta reserva? (s/n): ");
            String resp = scanner.next();
            scanner.nextLine();
            if (!resp.equalsIgnoreCase("s")) continuar = false;
        }

        reservas.add(reserva);
        System.out.println("\u001B[32mReserva registrada con éxito.\u001B[0m");
    }

    // COMPRA DE LAS ENTRADAS RESERVADAS
    static void comprarEntradas() {
        if (reservas.isEmpty()) {
            System.out.println("\u001B[31mNo hay reservas disponibles.\u001B[0m");
            return;
        }

        System.out.println("\nReservas disponibles: ");
        for (int i = 0; i < reservas.size(); i++) {
            Reserva r = reservas.get(i);
            System.out.println((i + 1) + ". Nombre: " + r.nombreComprador + " - Entradas: " + r.entradas.size());
        }
        System.out.print("Seleccione una reserva para comprar (1-" + reservas.size() + "): ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine();

        if (index < 0 || index >= reservas.size()) {
            System.out.println("\u001B[31mSelección inválida.\u001B[0m");
            return;
        }

        Reserva reserva = reservas.get(index);
        Venta venta = new Venta(ventaID++, reserva.nombreComprador);

        double total = 0;
        for (Entrada e : reserva.entradas) {
            double precioBase = precioZona[e.zona];
            double precioFinal = precioBase - (precioBase * e.descuento);
            total += precioFinal;
            venta.agregarEntrada(e);
        }
        venta.totalPagado = total;

        ventas.add(venta);
        reservas.remove(index);

        System.out.println("\u001B[32mCompra realizada con éxito.\u001B[0m");
    }

    // ARROJAS LAS BOLETAS DE LAS COMPRAS REALIZADAS
    static void imprimirBoleta() {
        if (ventas.isEmpty()) {
            System.out.println("\u001B[31mNo hay ventas registradas.\u001B[0m");
            return;
        }

        System.out.println("\n\u001B[33mBoletas disponibles:\u001B[0m");
        for (Venta v : ventas) {
            System.out.println("\n========== BOLETA VENTA ID: " + v.idVenta + "==========");
            System.out.println("Comprador: " + v.nombreCliente);
            for (Entrada e : v.entradas) {
                String zona = zonas[e.zona];
                double precioBase = precioZona[e.zona];
                double precioFinal = precioBase - (precioBase * e.descuento);
                System.out.println("Zona: " + zona + ", Asiento: " + (e.asiento + 1) +
                        ", Edad: " + e.edad + ", Descuento: " + (int)(e.descuento * 100) + "%" +
                        ", Precio: $" + (int)precioFinal);
            }
            System.out.println("TOTAL PAGADO: $" + (int)v.totalPagado);
        }
    }

    // MODIFICAR RESERVAS EXISTENTES
    static void modificarReserva() {
        if (reservas.isEmpty()) {
            System.out.println("\u001B[31mNo hay reservas disponibles.\u001B[0m");
            return;
        }

        System.out.println("\nReservas Disponibles:");
        for (int i = 0; i < reservas.size(); i++) {
            Reserva r = reservas.get(i);
            System.out.println((i + 1) + ". Nombre: " + r.nombreComprador + "- Entradas: " + r.entradas.size());
        }
        System.out.print("Seleccione una reserva a modificar (1-" + reservas.size() + "): ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine();

        if (index < 0 || index >= reservas.size()){
            System.out.println("\u001B[31mSelección inválida.\u001B[0m");
            return;
        }

        Reserva reserva = reservas.get(index);
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n¿Qué modificación desea realizar?");
            System.out.println("1. Agregar un asiento");
            System.out.println("2. Eliminar un asiento");
            System.out.println("3. Salir de la modificación");
            System.out.print("Seleccione una opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> {
                    int zona = seleccionarZona();
                    int asiento = seleccionarAsiento(zona);
                    int edad = solicitarEdad();
                    String genero = solicitarGenero();
                    boolean esEstudiante = solicitarEstudiante();
                    double descuento = calcularDescuento(edad, genero, esEstudiante);

                    reserva.agregarEntrada(zona, asiento, edad, genero, esEstudiante, descuento);
                    asientosOcupados[zona][asiento] = true;

                    System.out.println("\u001B[32mAsiento agregado con éxito.\u001B[0m");
                }
                case 2 -> {
                    if (reserva.entradas.isEmpty()) {
                        System.out.println("\u001B[31mNo hay asientos en esta reserva.\u001B[0m");
                        break;
                    }
                    System.out.println("\nEntradas actuales en la reserva:");
                    for (int i = 0; i < reserva.entradas.size(); i++) {
                        Entrada e = reserva.entradas.get(i);
                        System.out.println((i + 1) + ". Zona: " + zonas[e.zona] + ", Asiento: " + (e.asiento + 1));
                    }

                    System.out.print("Seleccione la entrada que desea eliminar (1-" + reserva.entradas.size() + "): ");
                    int entradaIndex = scanner.nextInt() - 1;
                    scanner.nextLine();

                    if (entradaIndex < 0 || entradaIndex >= reserva.entradas.size()) {
                        System.out.println("\u001B[31mSelección inválida.\u001B[0m");
                        break;
                    }

                    Entrada entradaEliminada = reserva.entradas.remove(entradaIndex);
                    asientosOcupados[entradaEliminada.zona][entradaEliminada.asiento] = false;

                    System.out.println("\u001B[32mAsiento eliminado con éxito.\u001B[0m");
                }
                case 3 -> continuar = false;
                default -> System.out.println("\u001B[31mOpción no válida.\u001B[0m");
            }
        }
    }

    // SELECCIONAR LA ZONA DE LAS ENTRADAS
    static int seleccionarZona() {
        System.out.println("\nSeleccione zona:");
        for (int i = 0; i < zonas.length; i++) {
            System.out.println((i + 1) + ". " + zonas[i] + " ($" + precioZona[i] + ")");
        }
        int zona;
        do {
            System.out.print("Opción: ");
            zona = scanner.nextInt() - 1;
        } while (zona < 0 || zona >= zonas.length);
        return zona;
    }

    // SELECCIONAR LOS ASIENTOS EN LA ZONA SELECCIONADA
    static int seleccionarAsiento(int zona) {
        System.out.println("Asientos disponibles en zona " + zonas[zona] + ":");
        for (int i = 0; i < asientosPorZona; i++) {
            System.out.print(asientosOcupados[zona][i] ? "[X] " : "[" + (i + 1) + "] ");
        }
        System.out.println("");

        int asiento;
        do {
            System.out.print("Seleccione asiento (1-" + asientosPorZona + "): ");
            asiento = scanner.nextInt() - 1;
        } while (asiento < 0 || asiento >= asientosPorZona || asientosOcupados[zona][asiento]);

        return asiento;
    }

    // SOLICITAR DATOS COMO EDAD, GÉNERO Y SI ES ESTUDIANTE, PARA GENERAR DESCUENTOS
    static int solicitarEdad() {
        int edad;
        do {
            System.out.print("Edad del asistente: ");
            edad = scanner.nextInt();
        } while (edad < 0);
        return edad;
    }

    static String solicitarGenero() {
        System.out.print("Género (M/F): ");
        return scanner.next();
    }

    static boolean solicitarEstudiante() {
        System.out.print("¿Es estudiante? (s/n): ");
        return scanner.next().equalsIgnoreCase("s");
    }

    // SE APLICARÁ SOLO EL DESCUENTO MAYOR, NO ACUMULABLE
    static double calcularDescuento(int edad, String genero, boolean esEstudiante) {
        double descuentoNiño = (edad < 12) ? 0.10 : 0.0; // MENOR DE 12 AÑOS, DESCUENTO DEL 10%
        double descuentoTerceraEdad = (edad > 60) ? 0.25 : 0.0; // MAYOR DE 60 AÑOS, DESCUENTO DEL 25%
        double descuentoMujer = (genero.equalsIgnoreCase("f")) ? 0.20 : 0.0; // MUJERES, DESCUENTO DEL 20%
        double descuentoEstudiante = esEstudiante ? 0.15 : 0.0; // ESTUDIANTE, DESCUENTO DEL 15%

        return Math.max(Math.max(descuentoNiño, descuentoTerceraEdad),
                Math.max(descuentoMujer, descuentoEstudiante));
    }

    // ENTRADA SELECCIONADA
    static class Entrada {
        int zona;
        int asiento;
        int edad;
        String genero;
        boolean esEstudiante;
        double descuento;

        Entrada(int zona, int asiento, int edad, String genero, boolean esEstudiante, double descuento) {
            this.zona = zona;
            this.asiento = asiento;
            this.edad = edad;
            this.genero = genero;
            this.esEstudiante = esEstudiante;
            this.descuento = descuento;
        }
    }
    
    // REGISTRAR LAS RESERVAS
    static class Reserva {
        String nombreComprador;
        List<Entrada> entradas = new ArrayList<>();

        Reserva(String nombreComprador) {
            this.nombreComprador = nombreComprador;
        }

        void agregarEntrada(int zona, int asiento, int edad, String genero, boolean esEstudiante, double descuento) {
            entradas.add(new Entrada(zona, asiento, edad, genero, esEstudiante, descuento));
        }
    }

    // REGISTRAR LAS VENTAN CON ID PROPIO
    static class Venta {
        int idVenta;
        String nombreCliente;
        List<Entrada> entradas = new ArrayList<>();
        double totalPagado;

        Venta(int id, String nombreCliente) {
            this.idVenta = id;
            this.nombreCliente = nombreCliente;
        }

        void agregarEntrada(Entrada entrada) {
            entradas.add(entrada);
        }
    }
}

// MUCHAS GRACIAS POR TODOS LOS CONOCIMIENTOS ENSEÑADOS DURANTE ESTA ASIGNATURA.