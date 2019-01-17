import processing.core.PApplet;

import java.util.ArrayList;

public class App extends PApplet {


    // A környezet eltárolása - erre hivatkozva hívhatjuk meg a processing függvényeket
    public static PApplet processing;

    static ArrayList<Integer> innovationIn;
    static ArrayList<Integer> innovationOut;
    static int innovationCount = 0;
    static double weightMin, weightMax, c1, c2, c3, weightMaxSize, nodeMaxSize, threshold;
    //static Genome g1, g2;

    static Population pop;

    public static void main(String[] args) {
        PApplet.main("App", args);
    }

    public void settings() {
        size(1400, 800);
        //fullScreen();
    }

    public void setup() {
        processing = this;

        innovationIn = new ArrayList<>();
        innovationOut = new ArrayList<>();
        weightMaxSize = 5;
        nodeMaxSize = 21;

        weightMin = -100;
        weightMax = 100;
        c1 = 1;
        c2 = 1;
        c3 = 0.4;
        threshold = 4;

        pop = new Population(2, 1, 800);

        /*Genome g=new Genome(2,1,3,2,new int[]{0,0,1,0},new int[]{3},new int[]{2},new double[]{0.40533241629600525},new boolean[]{true},new int[]{1});
        g.draw(0, 0, 800, 400, nodeMaxSize, weightMaxSize,1);
        g.calculateFitness();
        println(g.fitness);
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
        frameRate(2);
        //noLoop();
        background(255);
        //println(g1.feedForward(new double[]{1, 1}));
        //g2=g2.reproduction(g1);
    }

    public void draw() {
        background(20);/*
        Genome g=new Genome(2,1,3,5,new int[]{0,0,4,0,2,3,1},new int[]{3,0,1,1,4,4,5,3,0,0,6},new int[]{2,2,2,4,2,5,2,4,4,6,4},new double[]{0.42464635893702507,1.3083996064960957,2.9164154678583145,1.575709780678153,1.0887770905392244,1.5617719488218427,1.5768220908939838,2.435418900102377,2.6000439868075773,0.6334415674209595,1.87896494474262},new boolean[]{true,false,true,true,true,true,true,true,true,true,true},new int[]{2,1,0,5,4,7,8,3,6,13,14});
        g.draw(0, 0, 800, 400, nodeMaxSize, weightMaxSize,1);
        g.calculateFitness();
        println(g.fitness);*/

        //pop.drawPopulation(0, 0, width, height, 8, 6, 0);
        println();
        println(frameCount);
        //pop.bestGenome.drawGenome1(350, 0, 700, 800/3, nodeMaxSize, weightMaxSize);
        pop.naturalSelection();
        println(pop.bestGenome.fitness);

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
        //println(pop.bestGenome.feedForward(new double[]{0, 0}));
        //pop.bestGenome.printGenome();
        /*double sum = 0;
        for (Gene gene : pop.bestGenome.genes) {
            sum += gene.weight;
        }
        println(sum + " " + (double) pop.bestGenome.nodes.size());
        println(pop.bestGenome.feedForward(new double[]{0.0, 0.0})[0]+" "+pop.bestGenome.feedForward(new double[]{1.0, 1.0})[0]);
        if(frameCount==40){
            System.out.println();
            System.out.println();
            pop.bestGenome.save();
            System.out.println();
            System.out.println();
        }*/
    }
}
