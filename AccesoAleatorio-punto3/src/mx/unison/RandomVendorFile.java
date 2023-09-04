package mx.unison;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RandomVendorFile {
    private String fileName;

    public RandomVendorFile(String fileName) {
        this.fileName = fileName;
    }

    public long write(Vendor v) {
        RandomAccessFile out = null;
        long position = 0;
        byte buffer[] = null;

        try {
            out = new RandomAccessFile(fileName, "rws");

            // cuantos bytes hay en archivo
            position = out.length();

            // ir al ultimo byte
            out.seek(position);

            // escribir el codigo
            out.writeInt(v.getCodigo());

            // escribir los bytes que se requieren para imprimir
            // la cadena con el nombre
            buffer = v.getNombre().getBytes();
            out.write(buffer);

            // convertir de Date a long
            long dob = v.getFecha().getTime();
            out.writeLong(dob);

            // escribir los bytes que se requieren para imprimir
            // la cadena con la zona
            buffer = v.getZona().getBytes();
            out.write(buffer);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(RandomVendorFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return position;
    }

    public Vendor read(long position) {
        RandomAccessFile out = null;
        long bytes = 0;
        byte buffer[] = null;
        Vendor vendor = null;
        try {
            out = new RandomAccessFile(fileName, "rws");
            out.seek(position);

            int codigo = out.readInt();

            byte[] nameBytes = new byte[Vendor.MAX_NAME];
            out.read(nameBytes);

            long dateBytes = out.readLong();

            byte[] zonaBytes = new byte[Vendor.MAX_ZONE];
            out.read(zonaBytes);

            vendor = new Vendor(codigo, new String(nameBytes), new Date(dateBytes),
                    new String(zonaBytes));
            out.close();

        } catch (IOException ex) {
            Logger.getLogger(RandomVendorFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return vendor;
    }

    public void read(Vendor vendors[]) {
        RandomAccessFile out = null;
        long bytes = 0;
        byte buffer[] = null;
        Vendor vendor = null;
        try {
            out = new RandomAccessFile(fileName, "rws");
            for (int i = 0; i < vendors.length; i++) {

                int codigo = out.readInt();

                byte[] nameBytes = new byte[Vendor.MAX_NAME];
                out.read(nameBytes);

                long dateBytes = out.readLong();

                byte[] zonaBytes = new byte[Vendor.MAX_ZONE];
                out.read(zonaBytes);

                vendors[i] = new Vendor(codigo, new String(nameBytes), new Date(dateBytes),
                        new String(zonaBytes));
            }
            out.close();

        } catch (IOException ex) {
            Logger.getLogger(RandomVendorFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void consultarPorZona(String zona) {
        // Crear una lista para almacenar los vendedores encontrados
        List<Vendor> vendedoresEnZona = new ArrayList<>();

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
            long fileLength = randomAccessFile.length();

            for (long position = 0; position < fileLength; position += Vendor.RECORD_LEN) {
                Vendor vendedor = read(position);
                if (vendedor.getZona().trim().equalsIgnoreCase(zona)) {
                    vendedoresEnZona.add(vendedor);
                }
            }
            randomAccessFile.close();
        } catch (IOException ex) {
            Logger.getLogger(RandomVendorFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!vendedoresEnZona.isEmpty()) {
            System.out.println("Vendedores en la zona '" + zona + "':");
            for (Vendor vendedor : vendedoresEnZona) {
                System.out.println(vendedor);
            }
        } else {
            System.out.println("No se encontraron vendedores en la zona '" + zona + "'.");
        }
    }

    public void borrarVendedor(int codigo) {
        List<Vendor> vendedores = new ArrayList<>();
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
            long fileLength = randomAccessFile.length();
            for (long position = 0; position < fileLength; position += Vendor.RECORD_LEN) {
                Vendor vendedor = read(position);
                if (vendedor.getCodigo() != codigo) {
                    vendedores.add(vendedor);
                }
            }
            randomAccessFile.close();
            // Reescribir el archivo sin el vendedor borrado
            RandomAccessFile writeFile = new RandomAccessFile(fileName, "rws");
            writeFile.setLength(0); // Limpiar el archivo
            for (Vendor v : vendedores) {
                write(v);
            }
            writeFile.close();
            System.out.println("Vendedor con código " + codigo + " borrado con éxito.");
        } catch (IOException ex) {
            Logger.getLogger(RandomVendorFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        final String dataPath = "vendors-data.dat";
        RandomVendorFile randomFile = new RandomVendorFile(dataPath);
        Scanner input = new Scanner(System.in);

        System.out.println("1. Consultar vendedores por zona.");
        System.out.println("2. Leer un registro especifico por posicion.");
        System.out.println("3. Borrar vendedor por código.");
        System.out.print("Seleccione una opcion (1, 2 o 3): ");

        int opcion = input.nextInt();

        if (opcion == 1) {
            // Consultar vendedores por zona
            input.nextLine();
            System.out.print("Ingrese la zona a consultar: ");
            String zona = input.nextLine();
            randomFile.consultarPorZona(zona);
        } else if (opcion == 2) {
            // Leer un registro por posición
            System.out.print("Número de registro: ");
            int n = input.nextInt();
            int pos = (n * Vendor.RECORD_LEN) - Vendor.RECORD_LEN;
            long t1 = System.currentTimeMillis();
            Vendor p = randomFile.read(pos);
            long t2 = System.currentTimeMillis();
            if (p != null) {
                System.out.println("Vendedor encontrado:");
                System.out.println(p);
                System.out.println("Tiempo de lectura: " + (t2 - t1) + " milisegundos");
            } else {
                System.out.println("Registro no encontrado.");
            }
        } else if (opcion == 3) {
            // Borrar vendedor por código
            System.out.print("Ingrese el código del vendedor a borrar: ");
            int codigo = input.nextInt();
            randomFile.borrarVendedor(codigo);
        } else {
            System.out.println("Opcion no valida.");
        }
    }
}