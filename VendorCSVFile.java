package unison;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VendorCSVFile {
    private String fileName;

    public VendorCSVFile(String fileName) {
        this.fileName = fileName;
    }

    public void write(Vendor v) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(fileName, true), true);
            out.println(v.toString());
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(VendorCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Vendor find(int codigo) {
        String lookFor = String.valueOf(codigo);
        String record = null;
        Vendor x = null;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            while ((record = in.readLine()) != null) {
                if (record.startsWith(lookFor)) {
                    x = parseRecord(record);
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VendorCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VendorCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        return x;
    }

    private Date parseDOB(String d) throws ParseException {
        int len = d.length();
        Date date = null;
        if (len == 8) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
            date = dateFormat.parse(d);
        }
        if (len == 10) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            date = dateFormat.parse(d);
        }
        return date;
    }

    private Vendor parseRecord(String record) {
        StringTokenizer st1 = new StringTokenizer(record, ",");

        Vendor v = new Vendor();

        v.setCodigo(Integer.parseInt(st1.nextToken()));
        v.setNombre(st1.nextToken());
        String fecha = st1.nextToken();

        Date dob = null;
        try {
            dob = parseDOB(fecha);
        } catch (ParseException e) {
            System.out.printf(e.getMessage());
        }
        v.setFecha(dob);
        v.setZona(st1.nextToken());
        return v;
    }


    public void modificarVendedor(int codigoEmpleado, String nuevoNombre, Date nuevaFecha, String nuevaZona) {
        // leer todos los registros del CSV
        List<Vendor> vendedores = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean skipHeader = true; // variable para omitir la primera línea
            while ((line = reader.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }
                Vendor vendedor = parseRecord(line);
                vendedores.add(vendedor);
            }
        } catch (IOException ex) {
            Logger.getLogger(VendorCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        // buscar el vendedor por el codigo
        for (int i = 0; i < vendedores.size(); i++) {
            if (vendedores.get(i).getCodigo() == codigoEmpleado) {
                // modificar los datos del vendedor
                Vendor vendedor = vendedores.get(i);
                vendedor.setNombre(nuevoNombre);
                vendedor.setFecha(nuevaFecha);
                vendedor.setZona(nuevaZona);
                vendedores.set(i, vendedor);

                // actualizar los registros
                try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                    for (Vendor v : vendedores) {
                        writer.println(v.toString());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(VendorCSVFile.class.getName()).log(Level.SEVERE, null, ex);
                }

                System.out.println("Datos del vendedor con código " + codigoEmpleado + " modificados con éxito.");
                return;
            }
        }

        System.out.println("Vendedor con código " + codigoEmpleado + " no encontrado.");
    }




    public static void main(String[] args) {
        final String fileName = "random_files/vendors.csv";

        VendorCSVFile csvFile = new VendorCSVFile(fileName);

        Scanner input = new Scanner(System.in);

        System.out.println("1. Buscar vendedor por número de empleado.");
        System.out.println("2. Modificar datos de un vendedor (excepto numero de empleado).");
        System.out.print("Seleccione una opcion (1 o 2): ");

        int opcion = input.nextInt();

        if (opcion == 1) {
            // buscar vendedor por numero de empleado
            System.out.print("Numero de empleado: ");
            int codigoEmpleado = input.nextInt();
            long t1 = System.currentTimeMillis();
            Vendor p = csvFile.find(codigoEmpleado);
            long t2 = System.currentTimeMillis();
            if (p != null) {
                System.out.println("Vendedor encontrado:");
                System.out.println(p);
            } else {
                System.out.println("Vendedor no encontrado.");
            }
            System.out.println("Tiempo de busqueda: " + (t2 - t1) + " milisegundos");
        } else if (opcion == 2) {
            // modificar datos de un vendedor
            System.out.print("Numero de empleado del vendedor a modificar: ");
            int codigoEmpleado = input.nextInt();
            input.nextLine();

            System.out.print("Nuevo nombre: ");
            String nuevoNombre = input.nextLine();

            System.out.print("Nueva fecha (MM/dd/yyyy): ");
            String nuevaFechaStr = input.nextLine();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date nuevaFecha = null;
            try {
                nuevaFecha = dateFormat.parse(nuevaFechaStr);
            } catch (ParseException e) {
                System.out.println("Formato de fecha incorrecto.");
                return;
            }

            System.out.print("Nueva zona: ");
            String nuevaZona = input.nextLine();

            csvFile.modificarVendedor(codigoEmpleado, nuevoNombre, nuevaFecha, nuevaZona);


        } else {
            System.out.println("Opcion no valida.");
        }
    }



}
