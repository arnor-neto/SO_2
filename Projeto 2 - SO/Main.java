//package SO2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        String linha;
        boolean primeiro = true;
        int[] quadros = new int[1];
        ArrayList<Integer> referencias = new ArrayList<>();

        try {

            BufferedReader br = new BufferedReader(new FileReader("entradas.txt"));

            while (br.ready()) {
                linha = br.readLine();
                if (primeiro) {
                    quadros = new int[Integer.parseInt(linha)];
                    primeiro = false;
                } else {
                    referencias.add(Integer.parseInt(linha));
                }
            }
            br.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        FIFO fifo = new FIFO();
        OTM otm = new OTM();
        LRU lru = new LRU();

        System.out.println("FIFO " + fifo.run(quadros.clone(), copyArray(referencias)));
        System.out.println("OTM " + otm.run(quadros.clone(), copyArray(referencias)));
        System.out.println("LRU " + lru.run(quadros.clone(), copyArray(referencias)));
    }

    public static ArrayList<Integer> copyArray(ArrayList<Integer> original) {

        ArrayList<Integer> copy = new ArrayList<>();

        for (Integer i : original) {
            copy.add(i);
        }

        return copy;
    }
}

//First-In-First-Out (FIFO)
class FIFO {

    public int run(int[] quadros, ArrayList<Integer> referencias) {

        int pageIndex = 0;
        int faltas = 0;
        boolean found = false;

        //inicia com todos os quadros inválidos
        for (int i = 0; i < quadros.length; i++) {
            quadros[i] = -1;
        }

        while (!referencias.isEmpty()) { //enquanto houver referencias

            for (int i = 0; i < quadros.length; i++) { //procura a página
                if (!referencias.isEmpty()) {
                    if (referencias.get(0).equals(quadros[i])) {
                        referencias.remove(0);
                        found = true;
                    }
                }
            }

            if (!found) { //falta de página

                faltas++;

                quadros[pageIndex] = referencias.get(0);
                referencias.remove(0);

                if (pageIndex < quadros.length - 1) {
                    pageIndex++;
                } else {
                    pageIndex = 0;
                }
            }

            found = false;
        }

        return faltas;
    }
}

//Ótimo (OTM)
class OTM {

    public int run(int[] quadros, ArrayList<Integer> referencias) {

        int faltas = 0;
        int distancia = 0;
        boolean found = false;

        //inicia com todos os quadros inválidos
        for (int i = 0; i < quadros.length; i++) {
            quadros[i] = -1;
        }

        int[] distancias = new int[quadros.length];

        while (!referencias.isEmpty()) {//enquanto houver mais referencias

            for (int i = 0; i < quadros.length; i++) {//procura a página
                if (!referencias.isEmpty()) {
                    if (referencias.get(0).equals(quadros[i])) {
                        referencias.remove(0);
                        found = true;
                    }
                }
            }

            if (!found) { //falta de página

                //constroi mapa de distancias
                distancia = 0;
                boolean first = true;
                for (int i = 0; i < quadros.length; i++) {
                    if (referencias.contains(quadros[i])) {
                        for (Integer in : referencias) {
                            if (in.equals(quadros[i]) && first) {
                                distancias[i] = distancia;
                                first = false;
                            }
                            distancia++;
                        }
                    } else {
                        distancias[i] = Integer.MAX_VALUE;
                    }
                    distancia = 0;
                    first = true;
                }

                faltas++;

                //substitui referencia mais distante
                quadros[maior(distancias)] = referencias.get(0);
                referencias.remove(0);
            }
            found = false;
        }
        return faltas;
    }

    //retorna o índice do maior elemento
    public int maior(int[] distancias) {

        int maior;
        int indexMaior;

        indexMaior = 0;
        maior = distancias[0];
        for (int i = 0; i < distancias.length; i++) {
            if (distancias[i] > maior) {
                maior = distancias[i];
                indexMaior = i;
            }
        }
        return indexMaior;
    }
}

//Menos Recentemente Usada (LRU)
class LRU {

    public int run(int[] quadros, ArrayList<Integer> referencias) {

        int ciclo = 1;
        int faltas = 0;
        boolean found = false;

        //inicia com todos os quadros inválidos
        for (int i = 0; i < quadros.length; i++) {
            quadros[i] = -1;
        }

        int[] cicloUsado = new int[quadros.length];

        while (!referencias.isEmpty()) {//enquanto houver mais referencias

            for (int i = 0; i < quadros.length; i++) { //procura a página
                if (!referencias.isEmpty()) {
                    if (referencias.get(0).equals(quadros[i])) {
                        referencias.remove(0);
                        cicloUsado[i] = ciclo;
                        ciclo++;
                        found = true;
                    }
                }
            }

            if (!found) { //falta de página

                faltas++;

                quadros[menor(cicloUsado)] = referencias.get(0);
                referencias.remove(0);
                cicloUsado[menor(cicloUsado)] = ciclo;
                ciclo++;
            }

            found = false;
        }

        return faltas;
    }

    public int menor(int[] ciclos) {

        int menor;
        int indexMenor;

        indexMenor = 0;
        menor = ciclos[0];
        for (int i = 0; i < ciclos.length; i++) {
            if (ciclos[i] < menor) {
                menor = ciclos[i];
                indexMenor = i;
            }
        }
        return indexMenor;
    }
}
