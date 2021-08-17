import java.io.*;
import java.util.*;

public class Main {

    public static Scanner keyboard = new Scanner(System.in);
    public static PrintStream out = System.out;

    public static void pause_screen(String mensage) {
        out.print(mensage + "\nPresiona <ENTER> para continuar . . . ");
        keyboard.nextLine();
        out.println();
    }

    public static String read_str(String message) {
        out.print(message + ": ");
        return keyboard.nextLine();
    }

    public static int read_integer(String message) {
        try {
            return Integer.parseInt(read_str(message));
        } catch (NumberFormatException e) {
            out.print("N\u00FAmero incorrecto");
            return read_integer(message);
        }
    }

    public static float read_decimal(String message) {
        try {
            return Float.parseFloat(read_str(message));
        } catch (NumberFormatException e) {
            out.print("N\u00FAmero incorrecto. Ingr\u00E9salo nuevamente: ");
            return read_decimal(message);
        }
    }

    public static String path = "productos.tsv";

    public static void main(String[] args) {

        ForEachFunction<Producto> print = new ForEachFunction<Producto>() {
            @Override
            public void call(Producto producto, Object params) {
                out.println(producto);
                int[] counter = (int[]) params;
                counter[0]++;
            }
        };
        ForEachFunction<Producto> printOnFile = new ForEachFunction<Producto>() {
            @Override
            public void call(Producto producto, Object params) {
                PrintStream file_stream = (PrintStream) params;
                file_stream.print(producto.getCosto() + "\t");
                file_stream.print(producto.getVenta() + "\t");
                file_stream.print(producto.getImpuestos() + "\t");
                file_stream.print(producto.getProveedor() + "\t");
                file_stream.print(producto.getDescripcion() + "\t");
                file_stream.print(producto.getCodigo_de_barras() + "\t");
                file_stream.print(producto.getCantidad_existente() + "\n");
            }
        };
        if(!System.getProperties().get("os.name").equals("Linux") && System.console()!=null)
            try {
                out = new PrintStream(System.out, true, "CP850");
                keyboard = new Scanner(System.in, "CP850");
            } catch (UnsupportedEncodingException e) {}
        Vector<Producto> vector = new Vector<Producto>();
        int n;
        Producto datum = null, producto;
        int[] counter = {0};
        int i, main_option, suboption;
        String[] fields;
        try {
            Scanner file_stream = new Scanner(new FileReader(path));
            while (file_stream.hasNextLine()) {
                fields = file_stream.nextLine().split("\t");
                producto = new Producto();
                producto.setCosto(Float.parseFloat(fields[0]));
                producto.setVenta(Float.parseFloat(fields[1]));
                producto.setImpuestos(Float.parseFloat(fields[2]));
                producto.setProveedor(fields[3]);
                producto.setDescripcion(fields[4]);
                producto.setCodigo_de_barras(fields[5]);
                producto.setCantidad_existente(Integer.parseInt(fields[6]));
                vector.add(producto);
            }
            file_stream.close();
        } catch (FileNotFoundException e) {}
        producto = new Producto();
        do {
            out.println("MEN\u00DA");
            out.println("1.- Altas");
            out.println("2.- Consultas");
            out.println("3.- Actualizaciones");
            out.println("4.- Bajas");
            out.println("5.- Ordenar registros");
            out.println("6.- Listar registros");
            out.println("7.- Salir");
            do {
                main_option = read_integer ("Selecciona una opci\u00F3n");
                if(main_option<1 || main_option>7)
                    out.println("Opci\u00F3n incorrecta");
            } while (main_option<1 || main_option>7);
            out.println();
            if (vector.isEmpty() && main_option!=1 && main_option!=7) {
                pause_screen("No hay registros.\n");
                continue;
            }
            if (main_option<5) {
                producto.setCodigo_de_barras(read_str ("Ingresa el codigo de barras del producto"));
                i = vector.indexOf(producto);
                datum = i<0 ? null : vector.get(i);
                if (datum!=null) {
                    out.println();
                    print.call(datum, counter);
                }
            }
            if (main_option==1 && datum!=null)
                out.println("El registro ya existe.");
            else if (main_option>=2 && main_option<=4 && datum==null)
                out.println("\nRegistro no encontrado.");
            else switch (main_option) {
            case 1:
                producto.setCosto(read_decimal ("Ingresa el costo"));
                producto.setVenta(read_decimal ("Ingresa el venta"));
                producto.setImpuestos(read_decimal ("Ingresa el impuestos"));
                producto.setProveedor(read_str ("Ingresa el proveedor"));
                producto.setDescripcion(read_str ("Ingresa el descripcion"));
                producto.setCantidad_existente(read_integer ("Ingresa el cantidad existente"));
                vector.add(producto);
                producto = new Producto();
                out.println("\nRegistro agregado correctamente.");
                break;
            case 3:
                out.println("Men\u00FA de actualizaci\u00F3n de campos");
                out.println("1.- costo");
                out.println("2.- venta");
                out.println("3.- impuestos");
                out.println("4.- proveedor");
                out.println("5.- descripcion");
                out.println("6.- cantidad existente");
                do {
                    suboption = read_integer ("Selecciona el n\u00FAmero de campo a modificar");
                    if (suboption<1 || suboption>6)
                        out.println("Opci\u00F3n incorrecta");
                } while (suboption<1 || suboption>6);
                switch (suboption) {
                    case 1:
                        datum.setCosto(read_decimal ("Ingresa el nuevo costo"));
                        break;
                    case 2:
                        datum.setVenta(read_decimal ("Ingresa el nuevo venta"));
                        break;
                    case 3:
                        datum.setImpuestos(read_decimal ("Ingresa el nuevo impuestos"));
                        break;
                    case 4:
                        datum.setProveedor(read_str ("Ingresa el nuevo proveedor"));
                        break;
                    case 5:
                        datum.setDescripcion(read_str ("Ingresa el nuevo descripcion"));
                        break;
                    case 6:
                        datum.setCantidad_existente(read_integer ("Ingresa el nuevo cantidad existente"));
                        break;
                }
                out.println("\nRegistro actualizado correctamente.");
                break;
            case 4:
                vector.remove(datum);
                out.println("Registro eliminado correctamente.");
                break;
            case 5:
                Collections.sort(vector);
                out.println("Registros ordenados correctamente.");
                break;
            case 6:
                n = vector.size();
                counter[0] = 0;
                for (i=0; i<n; i++)
                    print.call(vector.get(i), counter);
                out.println("Total de registros: " + counter[0] + ".");
                break;
            }
            if (main_option<7 && main_option>=1)
                pause_screen("");
        } while (main_option!=7);
        try {
            PrintStream output_stream = new PrintStream(path);
            n = vector.size();
            for (i=0; i<n; i++)
                printOnFile.call(vector.get(i), output_stream);
            output_stream.close();
        } catch (FileNotFoundException e) {}
    }
}

interface ForEachFunction<T extends Comparable<T>> {
    void call(T datum, Object params);
}

class Producto implements Comparable<Producto> {

    private float costo;
    private float venta;
    private float impuestos;
    private String proveedor;
    private String descripcion;
    private String codigo_de_barras;
    private int cantidad_existente;

    @Override
    public boolean equals(Object producto) {
        return this==producto || (producto instanceof Producto && codigo_de_barras.equals(((Producto)producto).codigo_de_barras));
    }

    @Override
    public int compareTo(Producto producto) {
        return codigo_de_barras.compareTo(producto.codigo_de_barras);
    }

    @Override
    public String toString() {
        return
            "costo             : " + costo + "\n" +
            "venta             : " + venta + "\n" +
            "impuestos         : " + impuestos + "\n" +
            "proveedor         : " + proveedor + "\n" +
            "descripcion       : " + descripcion + "\n" +
            "codigo de barras  : " + codigo_de_barras + "\n" +
            "cantidad existente: " + cantidad_existente + "\n";
    }

    public float getCosto() {
        return costo;
    }

    public void setCosto(float costo) {
        this.costo = costo;
    }

    public float getVenta() {
        return venta;
    }

    public void setVenta(float venta) {
        this.venta = venta;
    }

    public float getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(float impuestos) {
        this.impuestos = impuestos;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodigo_de_barras() {
        return codigo_de_barras;
    }

    public void setCodigo_de_barras(String codigo_de_barras) {
        this.codigo_de_barras = codigo_de_barras;
    }

    public int getCantidad_existente() {
        return cantidad_existente;
    }

    public void setCantidad_existente(int cantidad_existente) {
        this.cantidad_existente = cantidad_existente;
    }
}