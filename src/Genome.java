import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PConstants.CENTER;

class Genome {
    ArrayList<Gene> genes = new ArrayList<>();
    ArrayList<Node> nodes = new ArrayList<>();
    ArrayList<Node> network = new ArrayList<>();
    int inputs, outputs, biasNode, layers = 2;
    double fitness = 0;

    Genome(int inputs, int outputs) {
        this.inputs = inputs;
        this.outputs = outputs;

        for (int i = 0; i < inputs; i++) {
            nodes.add(new Node(nodes.size()));
            nodes.get(i).layer = 0;
        }

        for (int i = 0; i < outputs; i++) {
            nodes.add(new Node(nodes.size()));
            nodes.get(inputs + i).layer = 1;
        }

        nodes.add(new Node(nodes.size()));
        biasNode = nodes.size() - 1;
        nodes.get(biasNode).layer = 0;
    }

    Genome(int inputs, int outputs, int biasNode, int layers) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.biasNode = biasNode;
        this.layers = layers;
    }


    void calculateFitness() {
        calculateNetwork();
        double sum = 0;
        sum += Math.abs(feedForward(new double[]{0.0, 0.0})[0] - 0);
        sum += Math.abs(feedForward(new double[]{0.0, 1.0})[0] - 1.0);
        sum += Math.abs(feedForward(new double[]{1.0, 0.0})[0] - 1.0);
        sum += Math.abs(feedForward(new double[]{1.0, 1.0})[0] - 0);
        /*sum = 0;
        for (Gene gene : genes) {
            sum += gene.weight;
        }
        fitness = Math.abs(10.0 / (21.5 - (sum + (double) nodes.size() / 2)));//10.0 / sum;*/
        //fitness = 1.0 / (Math.abs(0.6 - feedForward(new double[]{0.0, 0.0})[0]) + Math.abs(0.6 - feedForward(new double[]{0.2, 0.3})[0]));
        fitness = 10.0 / sum;
    }

    double[] feedForward(double[] input) {
        for (int i = 0; i < inputs; i++) {
            nodes.get(i).value = input[i];
        }
        nodes.get(biasNode).value = 1;

        for (Node node1 : network) {
            node1.feed(this);
        }

        double[] output = new double[outputs];
        for (int i = 0; i < outputs; i++) {
            output[i] = nodes.get(inputs + i).value;
        }

        for (Node node : nodes) {
            node.sum = 0;
        }
        return output;
    }

    void calculateNetwork() {
        network = new ArrayList<>();

        for (int l = 0; l < layers; l++) {
            for (Node node : nodes) {
                if (node.layer == l) {
                    network.add(node);
                }
            }
        }
    }


    Genome crossover(Genome partner) {
        Genome child = new Genome(inputs, outputs, biasNode, layers);

        for (int i = 0; i < nodes.size(); i++) {
            child.nodes.add(nodes.get(i).clone_());
        }

        for (int i = 0; i < genes.size(); i++) {
            int matchingGene = matchingGene(partner, genes.get(i).innovationNumber);
            boolean enabled = true;
            if (matchingGene != -1) {
                if (!genes.get(i).enabled || !partner.genes.get(matchingGene).enabled) {
                    if (App.processing.random(1) < 0.75) {
                        enabled = false;
                    }
                }
                if (App.processing.random(1) < 0.5) {
                    child.genes.add(genes.get(i).clone_());
                } else {
                    child.genes.add(partner.genes.get(matchingGene).clone_());
                }
            } else {
                child.genes.add(genes.get(i).clone_());
                enabled = genes.get(i).enabled;
            }
            child.genes.get(i).enabled = enabled;
        }
        connectNodes();

        for (int i = 0; i < partner.genes.size(); i++) {
            if (matchingGene(this, partner.genes.get(i).innovationNumber) == -1) {
                if (partner.genes.get(i).from < nodes.size() && partner.genes.get(i).to < nodes.size()) {
                    if (nodes.get(partner.genes.get(i).from).layer < nodes.get(partner.genes.get(i).to).layer) {
                        if (App.processing.random(1) < 0.5) {
                            child.genes.add(partner.genes.get(i).clone_());
                        }
                    }
                }
            }
        }

        child.connectNodes();
        return child;
    }


    boolean isSimilarTo(Genome partner, int size) {
        return distance(partner, size) < App.threshold;
    }

    double distance(Genome partner, int size) {
        int excessCount = 0;
        int disjointCount = 0;
        int machingCount = 0;
        double weightDifference = 0;

        int maximumDisjointInnovationNumber = largestInnovationNumber();
        int largestInnovationNumber = partner.largestInnovationNumber();
        if (largestInnovationNumber < maximumDisjointInnovationNumber) {
            maximumDisjointInnovationNumber = largestInnovationNumber;
        }

        for (int i = 0; i < genes.size(); i++) {
            int matchingGene = matchingGene(partner, genes.get(i).innovationNumber);
            if (matchingGene != -1) {
                weightDifference += Math.abs(genes.get(i).weight - partner.genes.get(matchingGene).weight);
                machingCount++;
            } else if (genes.get(i).innovationNumber <= maximumDisjointInnovationNumber) {
                disjointCount++;
            } else {
                excessCount++;
            }
        }
        for (int i = 0; i < partner.genes.size(); i++) {
            int matchingGene = matchingGene(this, partner.genes.get(i).innovationNumber);
            if (matchingGene == -1) {
                if (partner.genes.get(i).innovationNumber <= maximumDisjointInnovationNumber) {
                    disjointCount++;
                } else {
                    excessCount++;
                }
            }
        }
        /*if (size <= 20) */return App.c1 * excessCount + App.c2 * disjointCount + App.c3 * weightDifference / machingCount;
        //return App.c1 * excessCount / nodes.size() + App.c2 * disjointCount / nodes.size() + App.c3 * weightDifference / machingCount;
    }

    int matchingGene(Genome partner, int innovationNumber) {
        for (int i = 0; i < partner.genes.size(); i++) {
            if (partner.genes.get(i).innovationNumber == innovationNumber) {
                return i;
            }
        }
        return -1;
    }

    int largestInnovationNumber() {
        int largestInnovationNumber = -1;
        for (Gene gene : genes) {
            if (gene.innovationNumber > largestInnovationNumber) {
                largestInnovationNumber = gene.innovationNumber;
            }
        }
        return largestInnovationNumber;
    }


    void mutate() {
        if (genes.size() == 0 || App.processing.random(1) < 0.5) {
            addConnection();
            connectNodes();
        }
        if (App.processing.random(1) < 0.01) {
            addNode();
            connectNodes();
        }
        if (App.processing.random(1) < 0.8) {
            for (Gene gene : genes) {
                if (App.processing.random(1) < 0.9) {
                    gene.mutateWeight();
                } else {
                    gene.restartWeight();
                }
            }
            connectNodes();
        }
    }

    void addConnection() {
        if (!isFullyConnected()) {
            int nodeFrom = (int) App.processing.random(nodes.size());
            int nodeTo = (int) App.processing.random(nodes.size());

            while (nodes.get(nodeFrom).layer == nodes.get(nodeTo).layer || nodes.get(nodeFrom).isConnectedTo(nodes.get(nodeTo))) {
                nodeFrom = (int) App.processing.random(nodes.size());
                nodeTo = (int) App.processing.random(nodes.size());
            }
            int temp;
            if (nodes.get(nodeFrom).layer > nodes.get(nodeTo).layer) {
                temp = nodeTo;
                nodeTo = nodeFrom;
                nodeFrom = temp;
            }

            genes.add(new Gene(nodeFrom, nodeTo, App.processing.random(-1, 1)));
            connectNodes();
        } else {
            // The network is fully isConnectedTo
            addNode();
        }
    }

    boolean isFullyConnected() {
        int maxGenes = 0;
        int[] numberOfNodesInLayer = new int[layers];

        for (Node node : nodes) {
            numberOfNodesInLayer[node.layer]++;
        }

        for (int i = 0; i < layers - 1; i++) {
            int nodesBefore = 0;
            for (int j = i + 1; j < layers; j++) {
                nodesBefore += numberOfNodesInLayer[j];
            }
            maxGenes += numberOfNodesInLayer[i] * nodesBefore;
        }

        return maxGenes == genes.size();
    }

    void addNode() {
        if (genes.size() == 0) {
            addConnection();
            return;
        }

        int randomGene = (int) App.processing.random(genes.size());
        while (genes.get(randomGene).from == biasNode && genes.size() != 1) {
            randomGene = (int) App.processing.random(genes.size());
        }
        genes.get(randomGene).enabled = false;

        int newNodeNumber = nodes.size();
        nodes.add(new Node(newNodeNumber));

        genes.add(new Gene(genes.get(randomGene).from, newNodeNumber, 1));
        nodes.get(newNodeNumber).layer = nodes.get(genes.get(randomGene).from).layer + 1;

        genes.add(new Gene(newNodeNumber, genes.get(randomGene).to, genes.get(randomGene).weight));

        if (nodes.get(newNodeNumber).layer == nodes.get(genes.get(randomGene).to).layer) {
            for (int i = 0; i < nodes.size() - 1; i++) {
                if (nodes.get(i).layer >= nodes.get(newNodeNumber).layer) {
                    nodes.get(i).layer++;
                }
            }
            layers++;
        }
        connectNodes();
    }

    void connectNodes() {
        for (Node node : nodes) {
            node.outputs.clear();
        }

        for (Gene gene : genes) {
            nodes.get(gene.from).outputs.add(gene);
        }
    }


    void draw(double x1, double y1, double x2, double y2, double nodeSize, double weightSize, int mode) {
        ArrayList<ArrayList<Node>> allNodes = new ArrayList<>();
        ArrayList<PVector> nodePositions = new ArrayList<>();
        ArrayList<Integer> nodeNumbers = new ArrayList<>();
        if (mode == 0 || mode == 1) {
            for (int i = 0; i < layers; i++) {
                ArrayList<Node> temp = new ArrayList<>();
                for (Node node : nodes) {
                    if (node.layer == i) {
                        temp.add(node);
                    }
                }
                allNodes.add(temp);
            }
        }

        if (mode == 0 || mode == 1 || mode == 2 || mode == 3) {
            App.processing.textSize((float) (nodeSize*0.6));
            App.processing.textAlign(CENTER, CENTER);
            App.processing.text((float) fitness, (float) (x1 + x2) / 2, (float) (y1 + nodeSize * 0.4));
            y1 += nodeSize*0.3;
        }
        if (mode == 1 || mode == 2 || mode == 3) {
            x1 += nodeSize;
            x2 -= nodeSize;
            y1 += nodeSize;
            y2 -= nodeSize;

            if (inputs == 0) {
                if (outputs == 1) {
                    nodePositions.add(new PVector((float) x2, (float) (y2 + y1) / 2));
                    nodeNumbers.add(0);
                    nodePositions.add(new PVector((float) x1, (float) (y2 + y1) / 2));
                    nodeNumbers.add(biasNode);
                } else {
                    for (int i = 0; i < outputs; i++) {
                        nodePositions.add(new PVector((float) x2, (float) (y1 + (float) i * (y2 - y1) / (outputs - 1))));
                        nodeNumbers.add(i);
                    }
                    nodePositions.add(new PVector((float) x1, (float) ((y2 + y1) / 2)));
                    nodeNumbers.add(biasNode);
                }
            } else {
                for (int i = 0; i < inputs; i++) {
                    nodePositions.add(new PVector((float) x1, (float) (y1 + (float) i * (y2 - y1) / inputs)));
                    nodeNumbers.add(i);
                }
                if (outputs == 1) {
                    for (int i = 0; i < outputs; i++) {
                        nodePositions.add(new PVector((float) x2, (float) (y2 + y1) / 2));
                        nodeNumbers.add(inputs + i);
                    }
                    nodePositions.add(new PVector((float) x1, (float) y2));
                    nodeNumbers.add(biasNode);
                } else {
                    for (int i = 0; i < outputs; i++) {
                        nodePositions.add(new PVector((float) x2, (float) (y1 + (float) i * (y2 - y1) / (outputs - 1))));
                        nodeNumbers.add(inputs + i);
                    }
                    nodePositions.add(new PVector((float) x1, (float) y2));
                    nodeNumbers.add(biasNode);
                }
            }
        }
        switch (mode) {
            case 0:
                for (int i = 0; i < layers; i++) {
                    double x = x1 + (double) (i + 1) * (x2 - x1) / (layers + 1);
                    for (int j = 0; j < allNodes.get(i).size(); j++) {
                        double y = y1 + (double) (j + 1) * (y2 - y1) / (double) (allNodes.get(i).size() + 1);
                        nodePositions.add(new PVector((float) x, (float) y));
                        nodeNumbers.add(allNodes.get(i).get(j).number);
                    }
                }
                break;
            case 1:
                for (int i = 1; i < layers - 1; i++) {
                    double x = x1 + (double) i * (x2 - x1) / (layers - 1);
                    for (int j = 0; j < allNodes.get(i).size(); j++) {
                        double y = y1 + (double) (j + 1) * (y2 - y1) / (allNodes.get(i).size() + 1);
                        nodePositions.add(new PVector((float) x, (float) y));
                        nodeNumbers.add(allNodes.get(i).get(j).number);
                    }
                }
                break;
            case 2:
                calculateNetwork();

                if (nodes.size() != biasNode + 1) {
                    PVector origo = new PVector((float) ((x2 + x1) / 2), (float) (y2 + y1) / 2);
                    double r1 = (x2 - x1) / 4;
                    double r2 = (y2 - y1) / 2.5;
                    for (int i = 0; i < nodes.size() - biasNode - 1; i++) {
                        double angle = (double) i * 2 * Math.PI / (nodes.size() - biasNode - 1);
                        nodePositions.add(new PVector((float) (origo.x - r1 * Math.cos(angle)), (float) (origo.y - r2 * Math.sin(angle))));
                        nodeNumbers.add(biasNode + i + 1);
                    }
                }
                break;
            case 3:
                for (int i = biasNode + 1; i < nodes.size(); i++) {
                    nodePositions.add(new PVector(App.processing.random((float) x1, (float) x2), App.processing.random((float) y1, (float) y2)));
                    nodeNumbers.add(nodes.get(i).number);
                }
            default:
                //System.out.println("try other mode");
        }
        if (mode == 0 || mode == 1 || mode == 2 || mode == 3) {
            for (Gene gene : genes) {
                if (gene.enabled) {
                    PVector fromPos;
                    PVector toPos;
                    fromPos = nodePositions.get(nodeNumbers.indexOf(gene.from));
                    toPos = nodePositions.get(nodeNumbers.indexOf(gene.to));
                    if (gene.weight >= 0) {
                        App.processing.stroke(100, 149, 237, 150);
                        App.processing.fill(100, 149, 237, 200);
                    } else {
                        App.processing.stroke(248, 131, 121, 150);
                        App.processing.fill(248, 131, 121, 200);
                    }
                    double size = weightSize * Math.abs(gene.weight / 4) / (Math.abs(gene.weight / 4) + 1);
                    App.processing.strokeWeight((float) size);
                    App.processing.line(fromPos.x, fromPos.y, toPos.x, toPos.y);
                    PVector dotPos = toPos.copy().sub(toPos.copy().sub(fromPos).div(4));
                    App.processing.strokeWeight(0);
                    App.processing.ellipse(dotPos.x, dotPos.y, (float) (size * 2), (float) (size * 2));
                }
            }

            for (int i = 0; i < nodePositions.size(); i++) {
                App.processing.stroke(20, 10);
                App.processing.strokeWeight(2);
                App.processing.fill(100, 100);
                App.processing.ellipse(nodePositions.get(i).x, nodePositions.get(i).y, (float) nodeSize, (float) nodeSize);
                App.processing.fill(255);
                App.processing.textSize((float) (nodeSize * 0.5));
                App.processing.textAlign(CENTER, CENTER);
                App.processing.text(nodeNumbers.get(i), nodePositions.get(i).x, nodePositions.get(i).y);
            }
        }
    }

    void printGenome() {
        ArrayList<ArrayList<Node>> allNodes = new ArrayList<>();

        for (int i = 0; i < layers; i++) {
            ArrayList<Node> temp = new ArrayList<Node>();
            for (Node node : nodes) {
                if (node.layer == i) {
                    temp.add(node);
                }
            }
            allNodes.add(temp);
        }
        System.out.println();
        System.out.print("Nodes:");
        for (int i = 0; i < layers - 1; i++) {
            System.out.print("(");
            for (int j = 0; j < allNodes.get(i).size() - 1; j++) {
                System.out.print(allNodes.get(i).get(j).number + ",");
            }
            System.out.print(allNodes.get(i).get(allNodes.get(i).size() - 1).number + ")-");
        }
        System.out.print("(");
        for (int j = 0; j < allNodes.get(layers - 1).size() - 1; j++) {
            System.out.print(allNodes.get(layers - 1).get(j).number + ",");
        }
        System.out.println(allNodes.get(layers - 1).get(allNodes.get(layers - 1).size() - 1).number + ")");
        System.out.print("Genes:");
        for (int i = 0; i < layers; i++) {
            for (int j = 0; j < allNodes.get(i).size(); j++) {
                for (int k = 0; k < allNodes.get(i).get(j).outputs.size(); k++) {
                    if (allNodes.get(i).get(j).outputs.get(k).enabled) {
                        System.out.print("(" + allNodes.get(i).get(j).outputs.get(k).from + "->" + allNodes.get(i).get(j).outputs.get(k).to + "," + allNodes.get(i).get(j).outputs.get(k).weight + "," + allNodes.get(i).get(j).outputs.get(k).innovationNumber + ")");
                    }
                }
            }
        }
        System.out.println();
    }

    void save() {
        System.out.println();
        System.out.print(inputs + "," + outputs + "," + biasNode + "," + layers + ",new int[]{" + nodes.get(0).layer);
        for (int i = 1; i < nodes.size(); i++) {
            System.out.print("," + nodes.get(i).layer);
        }
        System.out.print("},new int[]{" + genes.get(0).from);
        for (int i = 1; i < genes.size(); i++) {
            System.out.print("," + genes.get(i).from);
        }
        System.out.print("},new int[]{" + genes.get(0).to);
        for (int i = 1; i < genes.size(); i++) {
            System.out.print("," + genes.get(i).to);
        }
        System.out.print("},new double[]{" + genes.get(0).weight);
        for (int i = 1; i < genes.size(); i++) {
            System.out.print("," + genes.get(i).weight);
        }
        System.out.print("},new boolean[]{" + genes.get(0).enabled);
        for (int i = 1; i < genes.size(); i++) {
            System.out.print("," + genes.get(i).enabled);
        }
        System.out.print("},new int[]{" + genes.get(0).innovationNumber);
        for (int i = 1; i < genes.size(); i++) {
            System.out.print("," + genes.get(i).innovationNumber);
        }
        System.out.print("}");
    }

    Genome(int inputs, int outputs, int biasNode, int layers, int[] layer, int[] from, int[] to, double[] weight, boolean[] enabled, int[] innovationNumber) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.biasNode = biasNode;
        this.layers = layers;
        for (int i = 0; i < layer.length; i++) {
            nodes.add(new Node(i));
            nodes.get(i).layer = layer[i];
        }
        for (int i = 0; i < from.length; i++) {
            genes.add(new Gene(from[i], to[i], weight[i]));
            genes.get(i).enabled = enabled[i];
            genes.get(i).innovationNumber = innovationNumber[i];
        }
        connectNodes();
    }


    Genome clone_() {
        Genome clone = new Genome(inputs, outputs, biasNode, layers);
        clone.fitness = fitness;
        for (Gene gene : genes) {
            clone.genes.add(gene.clone_());
        }
        for (Node node : nodes) {
            clone.nodes.add(node.clone_());
        }
        for (Node node : network) {
            clone.network.add(node.clone_());
        }
        return clone;
    }
}