import java.io.*;
import java.util.Scanner;

public class ManejoAAA {
    public static void main(String[] args) {

        String CSVFile = "C:\\Users\\Usuario\\Desktop\\DS3\\randomFiles\\vendor.csv";
        String newCSVFile = "C:\\Users\\Usuario\\Desktop\\DS3\\randomFiles\\vendor_newversion.csv";
        String campoNew = "Ve_VentaMensuales";

        try {
            BufferedReader br=new BufferedReader(new FileReader(CSVFile));
            PrintWriter pw=new PrintWriter(new FileWriter(newCSVFile,true));//agrega datos al final del archivo

            //lee y omite la primera fila(encabezado) de{ csv original
            String encabezado= br.readLine();

            //agrega encabezado si newCSVFile es modificado
            if (new File(newCSVFile).length()==0){
                pw.println(encabezado+","+campoNew);
            }
            String linea;

            while ((linea= br.readLine())!=null){
                // divide la línea en campos utilizando una coma como separador
                String[] campo=linea.split(",");
                //se asigna "Ve_VentasMensuales"
                String ventasMensuales= calculoVM(campo);
                //imprime los datos originales+vendedor nuevo
                pw.println(linea+","+ventasMensuales);
            }
            br.close();
            pw.close();

            System.out.println("Archivo CSV modificado creado con éxito: " + newCSVFile);
            //nuevo vendedor
            agregarNuevoVendedor(newCSVFile);

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private static String calculoVM(String[] campo) {
        return "$"+" - ";
    }
    private static void agregarNuevoVendedor(String newCSVFile){
        try{
            Scanner scanner = new Scanner(System.in);

            System.out.println("Agregando un nuevo vendedor...");

            System.out.print("Ingrese el código del vendedor: ");
            String codigo = scanner.nextLine();

            System.out.print("Ingrese el nombre del vendedor: ");
            String nombre = scanner.nextLine();

            System.out.print("Ingrese la fecha de nacimiento del vendedor (DD/MM/AAAA): ");
            String fechaNacimiento = scanner.nextLine();

            System.out.print("Ingrese la zona del vendedor: ");
            String zona = scanner.nextLine();

            System.out.print("Ingrese las ventas mensuales del vendedor: ");
            String ventasMensuales = scanner.nextLine();

            PrintWriter pw=new PrintWriter(new FileWriter(newCSVFile,true));
            pw.println(codigo+","+nombre+ "," + fechaNacimiento+ "," + zona+ "," + "$"+ventasMensuales);
            pw.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
