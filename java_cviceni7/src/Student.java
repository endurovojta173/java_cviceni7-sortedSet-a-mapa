import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Student {
    private String jmeno;
    private String prijmeni;
    private int UID;

    public Student(String jmeno, String prijmeni, int UID) {
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.UID = UID;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    static SortedSet<Student> readStudents(File dir, String fileName) {
        Comparator<Student> comparator = Comparator.comparing(Student::getPrijmeni);
        SortedSet<Student> students = new TreeSet<Student>(comparator);

        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Zadaná cesta není platný adresář.");
        }

        File file = new File(dir, fileName);
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            throw new IllegalArgumentException("Soubor " + fileName + " neexistuje nebo jej nelze číst.");
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                Student student = new Student(data[0].trim(), data[1].trim(), Integer.parseInt(data[2].trim()));
                students.add(student);
            }
        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return students;
    }

    /**
     * 1. Veřejná metoda, která najde soubory a pro každý zavolá čtecí metodu.
     */
    public static void readAllCourses(File dir, SortedMap<String, String> predmety, SortedMap<String, ArrayList<String>> studentiPredmety) {
        // Kontrola adresáře
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            System.err.println("Chyba: Neplatný adresář.");
            return;
        }
        // Filtr pro soubory: začínají "predmety" a končí ".csv"
        File[] files = dir.listFiles((d, name) -> name.startsWith("predmety") && name.endsWith(".csv"));
        if (files != null) {
            for (File f : files) {
                // Pro každý nalezený soubor zavoláme privátní metodu
                readStudentCourses(f, predmety, studentiPredmety);
            }
        }
    }
    /**
     * 2. Privátní metoda pro zpracování jednoho konkrétního souboru.
     */
    private static void readStudentCourses(File f, SortedMap<String, String> predmety, SortedMap<String, ArrayList<String>> studentiPredmety) {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {

            // 1. řádek: UID studenta
            String uidLine = br.readLine();
            if (uidLine == null) return; // Prázdný soubor
            String uid = uidLine.trim();

            // 2. řádek: Oddělovač
            String delimiterLine = br.readLine();
            if (delimiterLine == null) return;
            String delimiter = delimiterLine.trim();

            // Příprava seznamu pro studenta v mapě studentiPredmety
            // Pokud tam student ještě není, vytvoříme mu nový ArrayList
            if (!studentiPredmety.containsKey(uid)) {
                studentiPredmety.put(uid, new ArrayList<>());
            }
            ArrayList<String> studentCourses = studentiPredmety.get(uid);

            // Další řádky: Kód a název předmětu
            String line;
            while ((line = br.readLine()) != null) {
                // POZOR: Pokud je oddělovač speciální znak (např. | nebo .),
                // split potřebuje escapování. Pattern.quote to zajistí.
                String[] parts = line.split(Pattern.quote(delimiter));

                if (parts.length >= 2) {
                    String kodPredmetu = parts[0].trim();
                    String nazevPredmetu = parts[1].trim();

                    // A) Zápis do mapy všech předmětů (kód -> název)
                    predmety.put(kodPredmetu, nazevPredmetu);

                    // B) Zápis do mapy studenta (přidání kódu do jeho seznamu)
                    studentCourses.add(kodPredmetu);
                }
            }

        } catch (IOException e) {
            System.err.println("Chyba při čtení souboru " + f.getName() + ": " + e.getMessage());
        }
    }
    @Override
    public String toString() {
        return "Student{" +
                "jmeno='" + jmeno + '\'' +
                ", prijmeni='" + prijmeni + '\'' +
                ", UID=" + UID +
                '}';
    }
}


