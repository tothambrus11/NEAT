import processing.core.PApplet;

import java.util.ArrayList;

public class App extends PApplet {


    // A környezet eltárolása - erre hivatkozva hívhatjuk meg a processing függvényeket
    public static PApplet processing;

    static ArrayList<Integer> innovationIn;
    static ArrayList<Integer> innovationOut;
    static int innovationCount = 0;
    static float weightMin, weightMax, c1, c2, c3, weightMaxSize, nodeMaxSize, threshold;
    //static Genome g1, g2;

    static Population pop;

    public static void main(String[] args) {
        PApplet.main("App", args);
    }

    public void settings() {
        size(1400, 800);
    }

    public void setup() {
        processing = this;

        innovationIn = new ArrayList<>();
        innovationOut = new ArrayList<>();
        weightMaxSize = 4;
        nodeMaxSize = 15;

        weightMin = -100;
        weightMax = 100;
        c1 = 1;
        c2 = 1;
        c3 = 0.4f;
        threshold = 10;

        pop = new Population(2, 1, 12);
/*
        g1 = new Genome(2, 1);
        g1.nodes.add(new Node(4));
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
        g2.layers++;
        g2.nodes.add(new Node(5));
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
        g2.connectNodes();*/
        frameRate(1);
        background(255);
        //println(g1.feedForward(new float[]{1, 1}));
        //g2=g2.reproduction(g1);

    }

    public void draw() {
        background(255);
        pop.drawPopulation();
        println(pop.bestFitness);
        //pop.bestGenome.drawGenome1(350, 0, 700, 800/3, nodeMaxSize, weightMaxSize);
        pop.naturalSelection();
        //println(frameRate);
        //g1.mutateAll();
        //g1.calculateFitness();
        //println(pop.bestGenome.fitness);
        //println(pop.species.size());
        //background(255);
        //g2.mutateAll();
        //Genome ch=g2.reproduction(g1);
        //g2.mutateAll();
        //child.drawGenome1(0, 0, 800, 400, nodeMaxSize, weightMaxSize);
        //g1.printGenome();
        //g2.printGenome();
        //pop.genomes.get(1).drawGenome(0, 0, 400, 200, nodeMaxSize, weightMaxSize);
        //pop.genomes.get(1).drawGenome1(400, 0, 800, 200, nodeMaxSize, weightMaxSize);
        //pop.genomes.get(1).drawGenome2(0, 200, 400, 400, nodeMaxSize, weightMaxSize);
        //pop.genomes.get(1).drawGenome3(0, 0, 800, 400, nodeMaxSize, weightMaxSize);
        //if (random(1) < 0.03) {
        //    pop.genomes.set(0, g1.reproduction(pop.genomes.get(0)));
        //}
        //pop.mutateAll();
        //pop.genomes.get(1).addNode();
        //pop.sortToSpecies();
        //for (int i = 0; i < pop.species.size(); i++) {
        //    for (int j = 0; j < pop.species.get(i).size(); j++) {
        //        //pop.species.get(i).get(j).printGenome();
        //    }
        //}
        //println(pop.species.size());
        //println(pop.genomes.get(0).distance(pop.genomes.get(1), pop.size));
        //println(g2.distance(g1));
        //println(g1.feedForward(new float[]{1, 1}));
    }
}
