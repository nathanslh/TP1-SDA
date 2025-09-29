import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.StringTokenizer;

// Kelas untuk demonstran
class Demonstran implements Comparable<Demonstran> {
    int ID;
    int E; // Energi basis
    int U; // Urgensi
    boolean isRemoved = false;
    int FlagB; // Pencatat nilai globalOffset saat demonstran masuk/pindah antrean

    // Konstruktor kelas Demonstran
    public Demonstran(int ID, int E, int U) {
        this.ID = ID;
        this.E = E;
        this.U = U;
        this.FlagB = 0;
    }

    // Method getter dan setter
    public int getID() { 
        return ID; 
    
    }
    public int getEnergiAktual(int globalOffset) { 
        return E - (globalOffset - FlagB); 
    
    }
    public int getU() { 
        return U; 
    }

    public void setE(int E) { 
        this.E = E; 
    }

    public void setFlagB(int flagB) { 
        this.FlagB = flagB; 
    }

    public void setRemoved(boolean removed) { 
        isRemoved = removed; 
    }

    public boolean isRemoved() { 
        return isRemoved; 
    }

    @Override
    public int compareTo(Demonstran other) {
        int thisEnergi = this.getEnergiAktual(TP1.globalOffset);
        int otherEnergi = other.getEnergiAktual(TP1.globalOffset);
        if (thisEnergi != otherEnergi) return thisEnergi - otherEnergi;
        if (this.U != other.U) return other.U - this.U;
        return this.ID - other.ID;
    }
}

// Kelas untuk spanduk
class S {
    int E;

    public S(int E){ 
        this.E = E; 
    }

    public int getE(){ 
        return E; 
    }
}

public class TP1 {
    static int ID = 0; // ID Demonstran
    static int globalOffset = 0; // Offset global untuk pengurangan energi
    static PriorityQueue<Demonstran> antreanDemo = new PriorityQueue<>(); 
    static ArrayDeque<Demonstran> antreanKonsumsi = new ArrayDeque<>();
    static Stack <S> tumpukanSpanduk = new Stack<>();
    static HashMap<Integer, Demonstran> mapDemonstran = new HashMap<>(); // Untuk pencarian cepat
    static HashMap<Integer, Demonstran> mapKonsumsi = new HashMap<>(); // Untuk pencarian cepat
    static int[] energiKonsumsi; 
    
    public static void main(String[] args) throws IOException {
        InputReader in = new InputReader(System.in);
        PrintWriter out = new PrintWriter(System.out);

        // Input N, M, Q
        int N = in.nextInteger();
        int M = in.nextInteger();
        int Q = in.nextInteger();

        // Input energi konsumsi
        energiKonsumsi = new int[N];
        for (int i = 0; i < N; i++) {
            energiKonsumsi[i] = in.nextInteger();
        }

        // Input energi poster
        for (int j = 0; j < M; j++) { 
            in.nextInteger(); 
        }

        // Input nilai keefektifan poster
        for (int k = 0; k < M; k++) { 
            in.nextInteger(); 
        }

        // Input aktivitas
        for (int i = 0; i < Q; i++) {
            String aktivitas = in.next();
            if (aktivitas.equals("A")) {
                A(in.nextInteger(), in.nextInteger(), out);
            } else if (aktivitas.equals("B")) {
                B(out);
            } else if (aktivitas.equals("K")) {
                K(in.nextInteger(), in.nextInteger(), out);
            } else if (aktivitas.equals("S")) {
                tumpukanSpanduk.push(new S(in.nextInteger()));
                out.println(tumpukanSpanduk.size());
            } else if (aktivitas.equals("L")) {
                L(in.nextInteger(), out);
            } else if (aktivitas.equals("O")) {
                O(in.nextInteger(), out);
            } else if (aktivitas.equals("P")) {
                P(in.nextInteger(), out);
            }
        }
        out.close();
    }

    // Method untuk aktivitas A
    public static void A(int E, int U, PrintWriter out) {
        Demonstran person = new Demonstran(ID, E, U);
        person.setFlagB(globalOffset); // Set FlagB ke nilai globalOffset saat ini
        antreanDemo.add(person); // Tambah ke antrean demonstran
        mapDemonstran.put(ID, person); // Masukan ke mapDemonstran
        out.println(ID);  
        ID++; // Inkrement ID
    }

    // Method untuk aktivitas B
    public static void B(PrintWriter out) {
        // Bersihkan antrean demonstran dari yang sudah dihapus atau energinya habis
        while (!antreanDemo.isEmpty()) {
            Demonstran peek = antreanDemo.peek();
            if (peek.isRemoved() || peek.getEnergiAktual(globalOffset) <= 0) {
                mapDemonstran.remove(antreanDemo.poll().getID());
            } else {
                break;
            }
        }

        // Jika antrean kosong
        if (antreanDemo.isEmpty()) {
            out.println("-1");

        } else {
            Demonstran demo = antreanDemo.poll(); // Ambil demonstran dengan prioritas tertinggi
            mapDemonstran.remove(demo.getID());
            int energiAktual = demo.getEnergiAktual(globalOffset);
            
            int energiSpanduk = 0;
            if (!tumpukanSpanduk.isEmpty()) {
                energiSpanduk = tumpukanSpanduk.pop().getE();
            }
            int sisaEnergi = energiAktual - energiSpanduk;

            if (sisaEnergi > 0) {
                out.println(sisaEnergi);
                demo.setE(sisaEnergi);
                demo.setFlagB(globalOffset + 1); // Set FlagB ke nilai globalOffset + 1
                antreanKonsumsi.addLast(demo); // Pindah ke antrean konsumsi
                mapKonsumsi.put(demo.getID(), demo); // Masukan ke mapKonsumsi
            } else {
                out.println(demo.getID());
            }
        }
        globalOffset++; // Inkrement globalOffset
    }
    
    public static void L(int id, PrintWriter out) {

        // Cek di mapDemonstran
        if (mapDemonstran.containsKey(id)){
            Demonstran demo = mapDemonstran.get(id);
            int energiTerakhir = demo.getEnergiAktual(globalOffset);

            if (energiTerakhir <= 0) {
                out.println("-1");
            } else {
                out.println(1 + " " + energiTerakhir);
            }
            demo.setRemoved(true); // Tandai sebagai dihapus
            mapDemonstran.remove(id); // Hapus dari mapDemonstran

        // Cek di mapKonsumsi
        } else if (mapKonsumsi.containsKey(id)){
            Demonstran demo = mapKonsumsi.get(id);
            int energiTerakhir = demo.getEnergiAktual(globalOffset);

            if (energiTerakhir <= 0) {
                out.println("-1");
            } else {
                out.println(2 + " " + energiTerakhir);
            }
            demo.setRemoved(true); // Tandai sebagai dihapus
            mapKonsumsi.remove(id); // Hapus dari mapKonsumsi
            
        } else {
            out.println("-1");
        }
    }

    public static void K(int bawah, int atas, PrintWriter out) {
        // Bersihkan antrean konsumsi dari yang sudah dihapus atau energinya habis
        while (!antreanKonsumsi.isEmpty()) {
            Demonstran peek = antreanKonsumsi.peekFirst();
            if (peek.isRemoved() || peek.getEnergiAktual(globalOffset) <= 0) {
                mapKonsumsi.remove(antreanKonsumsi.pollFirst().getID());
            } else {
                break;
            }
        }

        // Jika antrean kosong
        if (antreanKonsumsi.isEmpty()) {
            out.println("-1");
            return;
        }

        Demonstran demo = antreanKonsumsi.pollFirst(); // Ambil demonstran paling depan
        mapKonsumsi.remove(demo.getID()); // 
        int energiAktual = demo.getEnergiAktual(globalOffset);
        int konsumsiDiambil = findLargestConsumption(bawah, atas);

        if (konsumsiDiambil != 0) {
            out.println(konsumsiDiambil);
            demo.setE(energiAktual + konsumsiDiambil);
        } else {
            out.println("0");
            demo.setE(energiAktual);
        }
        
        demo.setFlagB(globalOffset);
        mapDemonstran.put(demo.getID(), demo);
        antreanDemo.add(demo);
    }
    
    // Method untuk mencari konsumsi terbesar
    private static int findLargestConsumption(int bawah, int atas) {
        if (energiKonsumsi.length == 0) return 0;
        int idx = Arrays.binarySearch(energiKonsumsi, atas);
        if (idx < 0) {
            idx = -(idx + 1) - 1;
        }
        if (idx >= 0 && energiKonsumsi[idx] >= bawah) {
            return energiKonsumsi[idx];
        }
        return 0;
    }

    public static void O(int X, PrintWriter out) { }
    public static void P(int X, PrintWriter out) { }

    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the
    // usual Scanner(System.in) and System.out
    // please use these classes to avoid your fast algorithm gets Time Limit
    // Exceeded caused by slow input-output (IO)
    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInteger() {
            return Integer.parseInt(next());
        }
    }
}