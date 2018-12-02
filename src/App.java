import processing.core.PApplet;

import java.util.ArrayList;

public class App extends PApplet {


    // A környezet eltárolása - erre hivatkozva hívhatjuk meg a processing függvényeket
    public static PApplet processing;
    static ArrayList<Integer> innovationIn;
    static ArrayList<Integer> innovationOut;
    static int innovationCount = 0;
    static float weightMin, weightMax, c1, c2, c3, weightMaxSize, nodeMaxSize;
    static Genome g1, g2;

    public static void main(String[] args) {
        PApplet.main("App", args);
    }

    public void settings() {
        size(800, 400);
    }

    public void setup() {
        innovationIn = new ArrayList<Integer>();
        innovationOut = new ArrayList<Integer>();
        weightMaxSize = 4;
        nodeMaxSize = 15;

        weightMin = -10;
        weightMax = 10;
        c1 = 1;
        c2 = 1;
        c3 = 0.4f;

        g1 = new Genome(2, 1);
        g1.nodes.add(new Node(4));
        g1.nodeCount++;
        g1.layers++;
        g1.nodes.get(2).layer = 2;
        g1.nodes.get(4).layer = 1;
        g1.genes.add(new Gene(0, 2, 1));
        g1.genes.add(new Gene(1, 2, 1));
        g1.genes.get(1).enabled = false;
        g1.genes.add(new Gene(3, 2, 1));
        g1.genes.add(new Gene(4, 2, 1));
        g1.genes.add(new Gene(1, 4, 1));
        g1.genes.add(new Gene(0, 4, 1));
        g1.connectNodes();

        g2 = new Genome(2, 1);
        g2.nodes.add(new Node(4));
        g2.nodeCount++;
        g2.layers++;
        g2.nodes.add(new Node(5));
        g2.nodeCount++;
        g2.layers++;
        g2.nodes.get(2).layer = 3;
        g2.nodes.get(4).layer = 1;
        g2.nodes.get(5).layer = 2;
        g2.genes.add(new Gene(0, 2, -1));
        g2.genes.add(new Gene(1, 2, -1));
        g2.genes.get(1).enabled = false;
        g2.genes.add(new Gene(3, 2, -1));
        g2.genes.add(new Gene(1, 4, -1));
        g2.genes.add(new Gene(4, 2, -1));
        g2.genes.get(4).enabled = false;
        g2.genes.add(new Gene(4, 5, -1));
        g2.genes.add(new Gene(5, 2, -1));
        g2.genes.add(new Gene(3, 4, -1));
        g2.genes.add(new Gene(0, 5, -1));
        g2.connectNodes();
        frameRate(100);
        background(255);

        processing = this;
    }

    public void draw() {
        background(255);
        g2.mutate();
        g1.printGenome();
        g2.printGenome();
        g1.drawGenome(0, 0, 400, 200, nodeMaxSize, weightMaxSize);
        g2.drawGenome(400, 0, 800, 200, nodeMaxSize, weightMaxSize);
        g2.drawGenome1(0, 200, 400, 400, nodeMaxSize, weightMaxSize);
        g2.drawGenome2(400, 200, 800, 400, nodeMaxSize, weightMaxSize);
        if (random(1) < 0.005) g2 = g1.crossover(g2);
        println(g2.distance(g1, c1 / 100, c2 / 100, c3));
    }


}
