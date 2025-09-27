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
    int E; // Energi konsumsi
    int U; // Urgensi
    boolean isRemoved = false; // flag untuk menandai demonstran yang keluar antrean

    public Demonstran(int ID, int E, int U) {
        this.ID = ID;
        this.E = E;
        this.U = U;
        this.isRemoved = false;
    }

    public int getID() {
        return ID;
    }

    public int getE() {
        return E;
    }

    public int getU() {
        return U;
    }

    public void setE(int E) {
        this.E = E;
    }

    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    @Override
    public int compareTo(Demonstran other) {
        // Prioritas: energi kecil ? urgensi besar ? ID kecil
        if (this.E != other.E) return this.E - other.E;
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
    static int ID = 0; // ID unik untuk demonstran
    static PriorityQueue<Demonstran> antreanDemo = new PriorityQueue<>();
    static ArrayDeque<Demonstran> antreanKonsumsi = new ArrayDeque<>();
    static Stack <S> tumpukanSpanduk = new Stack<>();
    static HashMap<Integer, Demonstran> mapDemonstran = new HashMap<>();
    static HashMap<Integer, Demonstran> mapKonsumsi = new HashMap<>();

    public static void main(String[] args) throws IOException {
        InputReader in = new InputReader(System.in);
        PrintWriter out = new PrintWriter(System.out);

        // Baca input N, M, Q
        int N = in.nextInteger();
        int M = in.nextInteger();
        int Q = in.nextInteger();

        // Input energi konsumsi
        int[] energiKonsumsi = new int[N];
        for (int i = 0; i < N; i++) {
            energiKonsumsi[i] = in.nextInteger();
        }

        // Input energi poster
        int[] energiPoster = new int[M];
        for (int j = 0; j < M; j++) {
            int P = in.nextInteger();
            energiPoster[j] = P;
        }
        Arrays.sort(energiPoster); // Sort ascending nilai energi poster

        // Input keefektifan poster
        int[] keefektifanPoster = new int[M];
        for (int k = 0; k < M; k++) {
            int V = in.nextInteger();
            keefektifanPoster[k] = V;
        }

        // Sort descending nilai efektivitas poster
        Arrays.sort(keefektifanPoster);
        for (int k = 0; k < M / 2; k++) {
            int temp = keefektifanPoster[k];
            keefektifanPoster[k] = keefektifanPoster[M - 1 - k];
            keefektifanPoster[M - 1 - k] = temp;
        }

        for (int i = 0; i < Q; i++) {
            String aktivitas = in.next();

            if (aktivitas.equals("A")) {
                int E = in.nextInteger();
                int U = in.nextInteger();
                A(E, U, out);

            } else if (aktivitas.equals("B")) {
                B(out);

            } else if (aktivitas.equals("K")) {
                int bawah = in.nextInteger();
                int atas = in.nextInteger();  
                K(bawah, atas, out);    

            } else if (aktivitas.equals("S")) {
                int energiSpanduk = in.nextInteger(); 
                S spanduk = new S(energiSpanduk);
                tumpukanSpanduk.push(spanduk);
                out.println(tumpukanSpanduk.size());

            } else if (aktivitas.equals("L")) {
                int id = in.nextInteger();
                L(id, out);
                
            } else if (aktivitas.equals("O")) {
                int X = in.nextInteger();
                O(X, out);
            } else if (aktivitas.equals("P")) {
                int X = in.nextInteger();
                P(X, out);
            }
        }

        out.close();
    
    }

    public static void A(int E, int U, PrintWriter out) {
        Demonstran person = new Demonstran(ID, E, U);
        antreanDemo.add(person);  // masuk ke antrean demo
        mapDemonstran.put(ID, person); // simpan ke mapDemonstran
        out.println(ID);  
        ID++;
    }

public static void B(PrintWriter out) {

    // Hapus elemen yang ditandai removed sebelum diproses
    while (!antreanDemo.isEmpty() && antreanDemo.peek().isRemoved()) {
        antreanDemo.poll(); 
    }

    // Antrian demonstran kosong
    if (antreanDemo.isEmpty()) {
        out.println("-1");
        return;
    }

    // Ambil demonstran prioritas tertinggi
    Demonstran demo = antreanDemo.poll();

    // Default energi spanduk
    int energiSpanduk = 0;
    if (!tumpukanSpanduk.isEmpty()) {
        energiSpanduk = tumpukanSpanduk.pop().getE();
    }

    int sisaEnergi = demo.getE() - energiSpanduk; 

    if (sisaEnergi > 0) {
        demo.setE(sisaEnergi);
        out.println(sisaEnergi);
        antreanKonsumsi.addLast(demo);
        mapDemonstran.remove(demo.getID()); 
        mapKonsumsi.put(demo.getID(), demo); 
    } else {
        out.println("0"); 
        mapDemonstran.remove(demo.getID());
    }

    
    PriorityQueue<Demonstran> antreanDemoBaru = new PriorityQueue<>();
    while (!antreanDemo.isEmpty()) {
        Demonstran d = antreanDemo.poll();
        d.setE(d.getE() - 1);
        
        if (d.getE() > 0 && !d.isRemoved()) {
            antreanDemoBaru.add(d);
        } else {
            mapDemonstran.remove(d.getID());
        }
    }
    antreanDemo = antreanDemoBaru;
}

    public static void L(int id, PrintWriter out) {
        Demonstran demo = null;
        int lokasi = 0;
        int energiTerakhir = 0;

        if (mapDemonstran.containsKey(id)){
            demo = mapDemonstran.get(id);
            lokasi = 1;
            energiTerakhir = demo.getE();
            System.out.println(lokasi + " " + energiTerakhir);

        } else if (mapKonsumsi.containsKey(id)){
            demo = mapKonsumsi.get(id);
            lokasi = 2;
            energiTerakhir = demo.getE();
            System.out.println(lokasi + " " + energiTerakhir);

        } else {
            out.println("-1");
            return;
        }
    }

    public static void K(int bawah, int atas, PrintWriter out) {
        if(antreanKonsumsi.isEmpty()){
            out.println("-1");
            return;
        }
    }

    public static void O(int X, PrintWriter out) {
        if(X == 0){
            out.println("0");
            return;
        }       

    }

    public static void P(int X, PrintWriter out) {
    } 

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
