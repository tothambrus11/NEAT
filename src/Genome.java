import java.util.ArrayList;

class Genome {
    ArrayList<Gene> genes = new ArrayList<Gene>();
    ArrayList<Node> nodes = new ArrayList<Node>();
    ArrayList<Node> network = new ArrayList<Node>();
    int inputs, outputs, biasNode, layers = 2, nodeCount = 0;
    float fitness = 0;

    Genome(int inputs_, int outputs_) {
        inputs = inputs_;
        outputs = outputs_;

        for (int i = 0; i < inputs; i++) {
            nodes.add(new Node(nodeCount));
            nodeCount++;
            nodes.get(i).layer = 0;
        }

        for (int i = 0; i < outputs; i++) {
            nodes.add(new Node(nodeCount));
            nodeCount++;
            nodes.get(inputs + i).layer = 1;
        }

        nodes.add(new Node(nodeCount));
        biasNode = nodeCount;
        nodeCount++;
        nodes.get(biasNode).layer = 0;
    }

    Genome() {
    }

    Genome crossover(Genome partner) {
        Genome child = clone();

        for (int i = 0; i < nodes.size(); i++) {
            child.nodes.add(nodes.get(i).clone());
        }

        for (int i = 0; i < genes.size(); i++) {
            int matchingGene = matchingGene(partner, genes.get(i).innovationNumber);
            boolean enabled = true;
            if (matchingGene != -1) {
                if (!genes.get(i).enabled || !partner.genes.get(matchingGene).enabled) {
                    if (random(1) < 0.75) {
                        enabled = false;
                    }
                }
                if (random(1) < 0.5) {
                    child.genes.add(genes.get(i).clone());
                } else {
                    child.genes.add(partner.genes.get(matchingGene).clone());
                }
            } else {
                child.genes.add(genes.get(i).clone());
                enabled = genes.get(i).enabled;
            }
            child.genes.get(i).enabled = enabled;
        }
        connectNodes();

        for (int i = 0; i < partner.genes.size(); i++) {
            if (matchingGene(this, partner.genes.get(i).innovationNumber) == -1) {
                if (partner.genes.get(i).from < nodes.size() && partner.genes.get(i).to < nodes.size()) {
                    if (getNode(partner.genes.get(i).from).layer < getNode(partner.genes.get(i).to).layer) {
                        if (App.processing.random(1) < 0.5) {
                            child.genes.add(partner.genes.get(i).clone());
                        }
                    }
                }
            }
        }

        child.connectNodes();
        return child;
    }

    float[] feedForward(float[] input) {
        calculateNetwork();
        for (int i = 0; i < inputs; i++) {
            nodes.get(i).value = input[i];
        }
        nodes.get(biasNode).value = 1;

        for (int i = 0; i < network.size(); i++) {
            network.get(i).feed(this);
        }

        float[] output = new float[outputs];
        for (int i = 0; i < outputs; i++) {
            output[i] = nodes.get(inputs + i).value;
        }

        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).sum = 0;
        }
        return output;
    }

    float distance(Genome partner, int size) {
        float excessCount = 0;
        float disjointCount = 0;
        float weightDifference = 0;

        int maximumDisjointInnovationNumber = largestInnovationNumber();
        int largestInnovationNumber = partner.largestInnovationNumber();
        if (largestInnovationNumber < maximumDisjointInnovationNumber) {
            maximumDisjointInnovationNumber = largestInnovationNumber;
        }

        for (int i = 0; i < genes.size(); i++) {
            int matchingGene = matchingGene(partner, genes.get(i).innovationNumber);
            if (matchingGene != -1) {
                weightDifference += abs(genes.get(i).weight - partner.genes.get(matchingGene).weight);
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
        if (size <= 20) return c1 * excessCount + c2 * disjointCount + c3 * weightDifference;
        return c1 * excessCount + c2 * disjointCount + c3 * weightDifference;
    }

    void mutate() {
        if (genes.size() == 0 || random(1) < 0.15) {
            addConnection();
            connectNodes();
        }
        if (random(1) < 0.03) {
            addNode();
            connectNodes();
        }
        if (random(1) < 0.8) {
            for (int i = 0; i < genes.size(); i++) {
                if (random(1) < 0.9) {
                    genes.get(i).mutateWeight();
                } else {
                    genes.get(i).restartWeight();
                }
            }
            connectNodes();
        }
    }

    boolean isSimilarTo(Genome partner, int size) {
        return distance(partner, size) < threshold;
    }

    Genome clone() {
        Genome clone = new Genome();
        clone.inputs = inputs;
        clone.outputs = outputs;
        clone.layers = layers;
        clone.nodeCount = nodeCount;
        clone.biasNode = biasNode;
        clone.fitness = fitness;
        return clone;
    }

    Node getNode(int number) {
    /*for (int i=0; i<nodes.size(); i++) {
     if (nodes.get(i).number==number) {
     return nodes.get(i);
     }
     }
     return null;*/
        return nodes.get(number);
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
        for (int i = 0; i < genes.size(); i++) {
            if (genes.get(i).innovationNumber > largestInnovationNumber) {
                largestInnovationNumber = genes.get(i).innovationNumber;
            }
        }
        return largestInnovationNumber;
    }

    boolean isFullyConnected() {
        int maxGenes = 0;
        int[] numberOfNodesInLayer = new int[layers];

        for (int i = 0; i < nodes.size(); i++) {
            numberOfNodesInLayer[nodes.get(i).layer]++;
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

        int randomGene = floor(random(genes.size()));
        while (genes.get(randomGene).from == biasNode && genes.size() != 1) {
            randomGene = floor(random(genes.size()));
        }
        genes.get(randomGene).enabled = false;

        int newNodeNumber = nodeCount;
        nodes.add(new Node(newNodeNumber));
        nodeCount++;

        genes.add(new Gene(genes.get(randomGene).from, newNodeNumber, 1));
        nodes.get(newNodeNumber).layer = getNode(genes.get(randomGene).from).layer + 1;

        genes.add(new Gene(newNodeNumber, genes.get(randomGene).to, genes.get(randomGene).weight));

        if (getNode(newNodeNumber).layer == getNode(genes.get(randomGene).to).layer) {
            for (int i = 0; i < nodes.size() - 1; i++) {
                if (nodes.get(i).layer >= getNode(newNodeNumber).layer) {
                    nodes.get(i).layer++;
                }
            }
            layers++;
        }
        connectNodes();
    }

    void addConnection() {
        if (isFullyConnected()) {
            //println("the network is fully connected");
            addNode();
            return;
        }

        int nodeFrom = floor(random(nodes.size()));
        int nodeTo = floor(random(nodes.size()));

        while (nodes.get(nodeFrom).layer == nodes.get(nodeTo).layer || nodes.get(nodeFrom).connected(nodes.get(nodeTo))) {
            nodeFrom = floor(random(nodes.size()));
            nodeTo = floor(random(nodes.size()));
        }
        int temp;
        if (nodes.get(nodeFrom).layer > nodes.get(nodeTo).layer) {
            temp = nodeTo;
            nodeTo = nodeFrom;
            nodeFrom = temp;
        }

        genes.add(new Gene(nodeFrom, nodeTo, random(-1, 1)));
        connectNodes();
    }

    void connectNodes() {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).outputs.clear();
        }

        for (int i = 0; i < genes.size(); i++) {
            getNode(genes.get(i).from).outputs.add(genes.get(i));
        }
    }

    void calculateNetwork() {
        network = new ArrayList<Node>();

        for (int l = 0; l < layers; l++) {
            for (int i = 0; i < nodes.size(); i++) {
                if (nodes.get(i).layer == l) {
                    network.add(nodes.get(i));
                }
            }
        }
    }

    void drawGenome(float x1, float y1, float x2, float y2, float nodeSize, float weightSize) {
        ArrayList<ArrayList<Node>> allNodes = new ArrayList<ArrayList<Node>>();
        ArrayList<PVector> nodePositions = new ArrayList<PVector>();
        ArrayList<Integer> nodeNumbers = new ArrayList<Integer>();

        for (int i = 0; i < layers; i++) {
            ArrayList<Node> temp = new ArrayList<Node>();
            for (int j = 0; j < nodes.size(); j++) {
                if (nodes.get(j).layer == i) {
                    temp.add(nodes.get(j));
                }
            }
            allNodes.add(temp);
        }

        for (int i = 0; i < layers; i++) {
            float x = x1 + (float) (i + 1) * (x2 - x1) / (layers + 1);
            for (int j = 0; j < allNodes.get(i).size(); j++) {
                float y = y1 + (float) (j + 1) * (y2 - y1) / (float) (allNodes.get(i).size() + 1);
                nodePositions.add(new PVector(x, y));
                nodeNumbers.add(allNodes.get(i).get(j).number);
            }
        }

        for (int i = 0; i < genes.size(); i++) {
            if (genes.get(i).enabled) {
                PVector fromPos;
                PVector toPos;
                fromPos = nodePositions.get(nodeNumbers.indexOf(genes.get(i).from));
                toPos = nodePositions.get(nodeNumbers.indexOf(genes.get(i).to));
                if (genes.get(i).weight >= 0) {
                    stroke(0, 255, 0);
                    fill(0, 255, 0);
                } else {
                    stroke(255, 0, 0);
                    fill(255, 0, 0);
                }
                float size = weightSize * pow(genes.get(i).weight, 2) / (pow(genes.get(i).weight, 2) + 1);
                strokeWeight(size);
                line(fromPos.x, fromPos.y, toPos.x, toPos.y);
                PVector dotPos = toPos.copy().sub(toPos.copy().sub(fromPos).div(4));
                strokeWeight(0);
                ellipse(dotPos.x, dotPos.y, 3 * size, 3 * size);
            }
        }

        for (int i = 0; i < nodePositions.size(); i++) {
            fill(255);
            stroke(0);
            strokeWeight(1);
            ellipse(nodePositions.get(i).x, nodePositions.get(i).y, nodeSize, nodeSize);
            textSize(nodeSize / 2);
            fill(0);
            textAlign(CENTER, CENTER);
            text(nodeNumbers.get(i), nodePositions.get(i).x, nodePositions.get(i).y);
        }
    }

    void drawGenome1(float x1, float y1, float x2, float y2, float nodeSize, float weightSize) {
        ArrayList<ArrayList<Node>> allNodes = new ArrayList<ArrayList<Node>>();
        ArrayList<PVector> nodePositions = new ArrayList<PVector>();
        ArrayList<Integer> nodeNumbers = new ArrayList<Integer>();

        x1 += nodeSize;
        x2 -= nodeSize;
        y1 += nodeSize;
        y2 -= nodeSize;

        for (int i = 0; i < layers; i++) {
            ArrayList<Node> temp = new ArrayList<Node>();
            for (int j = 0; j < nodes.size(); j++) {
                if (nodes.get(j).layer == i) {
                    temp.add(nodes.get(j));
                }
            }
            allNodes.add(temp);
        }

        if (inputs == 0) {
            if (outputs == 1) {
                nodePositions.add(new PVector(x2, (y2 + y1) / 2));
                nodeNumbers.add(0);
                nodePositions.add(new PVector(x1, (y2 + y1) / 2));
                nodeNumbers.add(biasNode);
            } else {
                for (int i = 0; i < outputs; i++) {
                    nodePositions.add(new PVector(x2, y1 + (float) i * (y2 - y1) / (outputs - 1)));
                    nodeNumbers.add(i);
                }
                nodePositions.add(new PVector(x1, (y2 + y1) / 2));
                nodeNumbers.add(biasNode);
            }
        } else {
            if (outputs == 1) {
                for (int i = 0; i < inputs; i++) {
                    nodePositions.add(new PVector(x1, y1 + (float) i * (y2 - y1) / inputs));
                    nodeNumbers.add(i);
                }
                for (int i = 0; i < outputs; i++) {
                    nodePositions.add(new PVector(x2, (y2 + y1) / 2));
                    nodeNumbers.add(inputs + i);
                }
                nodePositions.add(new PVector(x1, y2));
                nodeNumbers.add(biasNode);
            } else {
                for (int i = 0; i < inputs; i++) {
                    nodePositions.add(new PVector(x1, y1 + (float) i * (y2 - y1) / inputs));
                    nodeNumbers.add(i);
                }
                for (int i = 0; i < outputs; i++) {
                    nodePositions.add(new PVector(x2, y1 + (float) i * (y2 - y1) / (outputs - 1)));
                    nodeNumbers.add(inputs + i);
                }
                nodePositions.add(new PVector(x1, y2));
                nodeNumbers.add(biasNode);
            }
        }

        for (int i = 1; i < layers - 1; i++) {
            float x = x1 + (float) i * (x2 - x1) / (layers - 1);
            for (int j = 0; j < allNodes.get(i).size(); j++) {
                float y = y1 + (float) (j + 1) * (y2 - y1) / (allNodes.get(i).size() + 1);
                nodePositions.add(new PVector(x, y));
                nodeNumbers.add(allNodes.get(i).get(j).number);
            }
        }

        for (int i = 0; i < genes.size(); i++) {
            if (genes.get(i).enabled) {
                PVector fromPos;
                PVector toPos;
                fromPos = nodePositions.get(nodeNumbers.indexOf(genes.get(i).from));
                toPos = nodePositions.get(nodeNumbers.indexOf(genes.get(i).to));
                if (genes.get(i).weight >= 0) {
                    stroke(0, 255, 0);
                    fill(0, 255, 0);
                } else {
                    stroke(255, 0, 0);
                    fill(255, 0, 0);
                }
                float size = weightSize * pow(genes.get(i).weight, 2) / (pow(genes.get(i).weight, 2) + 1);
                strokeWeight(size);
                line(fromPos.x, fromPos.y, toPos.x, toPos.y);
                PVector dotPos = toPos.copy().sub(toPos.copy().sub(fromPos).div(4));
                strokeWeight(0);
                ellipse(dotPos.x, dotPos.y, 3 * size, 3 * size);
            }
        }

        for (int i = 0; i < nodePositions.size(); i++) {
            fill(255);
            stroke(0);
            strokeWeight(1);
            ellipse(nodePositions.get(i).x, nodePositions.get(i).y, nodeSize, nodeSize);
            textSize(nodeSize / 2);
            fill(0);
            textAlign(CENTER, CENTER);
            text(nodeNumbers.get(i), nodePositions.get(i).x, nodePositions.get(i).y);
        }
    }

    void drawGenome2(float x1, float y1, float x2, float y2, float nodeSize, float weightSize) {
        ArrayList<PVector> nodePositions = new ArrayList<PVector>();
        ArrayList<Integer> nodeNumbers = new ArrayList<Integer>();

        x1 += nodeSize;
        x2 -= nodeSize;
        y1 += nodeSize;
        y2 -= nodeSize;

        calculateNetwork();

        if (inputs == 0) {
            if (outputs == 1) {
                nodePositions.add(new PVector(x2, (y2 + y1) / 2));
                nodeNumbers.add(0);
                nodePositions.add(new PVector(x1, (y2 + y1) / 2));
                nodeNumbers.add(biasNode);
            } else {
                for (int i = 0; i < outputs; i++) {
                    nodePositions.add(new PVector(x2, y1 + (float) i * (y2 - y1) / (outputs - 1)));
                    nodeNumbers.add(i);
                }
                nodePositions.add(new PVector(x1, (y2 + y1) / 2));
                nodeNumbers.add(biasNode);
            }
        } else {
            if (outputs == 1) {
                for (int i = 0; i < inputs; i++) {
                    nodePositions.add(new PVector(x1, y1 + (float) i * (y2 - y1) / inputs));
                    nodeNumbers.add(i);
                }
                for (int i = 0; i < outputs; i++) {
                    nodePositions.add(new PVector(x2, (y2 + y1) / 2));
                    nodeNumbers.add(inputs + i);
                }
                nodePositions.add(new PVector(x1, y2));
                nodeNumbers.add(biasNode);
            } else {
                for (int i = 0; i < inputs; i++) {
                    nodePositions.add(new PVector(x1, y1 + (float) i * (y2 - y1) / inputs));
                    nodeNumbers.add(i);
                }
                for (int i = 0; i < outputs; i++) {
                    nodePositions.add(new PVector(x2, y1 + (float) i * (y2 - y1) / (outputs - 1)));
                    nodeNumbers.add(inputs + i);
                }
                nodePositions.add(new PVector(x1, y2));
                nodeNumbers.add(biasNode);
            }
        }

        if (nodes.size() != biasNode + 1) {
            PVector origo = new PVector((x2 + x1) / 2, (y2 + y1) / 2);
            float r1 = (x2 - x1) / 4;
            float r2 = (y2 - y1) / 2.5;
            for (int i = 0; i < nodes.size() - biasNode - 1; i++) {
                float angle = (float) i * 2 * PI / (nodes.size() - biasNode - 1);
                nodePositions.add(new PVector(origo.x - r1 * cos(angle), origo.y - r2 * sin(angle)));
                nodeNumbers.add(biasNode + i + 1);
            }
        }

        for (int i = 0; i < genes.size(); i++) {
            if (genes.get(i).enabled) {
                PVector fromPos;
                PVector toPos;
                fromPos = nodePositions.get(nodeNumbers.indexOf(genes.get(i).from));
                toPos = nodePositions.get(nodeNumbers.indexOf(genes.get(i).to));
                if (genes.get(i).weight >= 0) {
                    stroke(0, 255, 0);
                    fill(0, 255, 0);
                } else {
                    stroke(255, 0, 0);
                    fill(255, 0, 0);
                }
                float size = weightSize * pow(genes.get(i).weight, 2) / (pow(genes.get(i).weight, 2) + 1);
                strokeWeight(size);
                line(fromPos.x, fromPos.y, toPos.x, toPos.y);
                PVector dotPos = toPos.copy().sub(toPos.copy().sub(fromPos).div(4));
                strokeWeight(0);
                ellipse(dotPos.x, dotPos.y, 3 * size, 3 * size);
            }
        }

        for (int i = 0; i < nodePositions.size(); i++) {
            fill(255);
            stroke(0);
            strokeWeight(1);
            ellipse(nodePositions.get(i).x, nodePositions.get(i).y, nodeSize, nodeSize);
            textSize(nodeSize / 2);
            fill(0);
            textAlign(CENTER, CENTER);
            text(nodeNumbers.get(i), nodePositions.get(i).x, nodePositions.get(i).y);
        }
    }

    void drawGenome3(float x1, float y1, float x2, float y2, float nodeSize, float weightSize) {
        ArrayList<PVector> nodePositions = new ArrayList<PVector>();
        ArrayList<Integer> nodeNumbers = new ArrayList<Integer>();

        x1 += nodeSize;
        x2 -= nodeSize;
        y1 += nodeSize;
        y2 -= nodeSize;

        for (int i = 0; i < nodes.size(); i++) {
            nodePositions.add(new PVector(random(x1, x2), random(y1, y2)));
            nodeNumbers.add(nodes.get(i).number);
        }

        for (int i = 0; i < genes.size(); i++) {
            if (genes.get(i).enabled) {
                PVector fromPos;
                PVector toPos;
                fromPos = nodePositions.get(nodeNumbers.indexOf(genes.get(i).from));
                toPos = nodePositions.get(nodeNumbers.indexOf(genes.get(i).to));
                if (genes.get(i).weight >= 0) {
                    stroke(0, 255, 0);
                    fill(0, 255, 0);
                } else {
                    stroke(255, 0, 0);
                    fill(255, 0, 0);
                }
                float size = weightSize * pow(genes.get(i).weight, 2) / (pow(genes.get(i).weight, 2) + 1);
                strokeWeight(size);
                line(fromPos.x, fromPos.y, toPos.x, toPos.y);
                PVector dotPos = toPos.copy().sub(toPos.copy().sub(fromPos).div(4));
                strokeWeight(0);
                ellipse(dotPos.x, dotPos.y, 3 * size, 3 * size);
            }
        }

        for (int i = 0; i < nodePositions.size(); i++) {
            fill(255);
            stroke(0);
            strokeWeight(1);
            ellipse(nodePositions.get(i).x, nodePositions.get(i).y, nodeSize, nodeSize);
            textSize(nodeSize / 2);
            fill(0);
            textAlign(CENTER, CENTER);
            text(nodeNumbers.get(i), nodePositions.get(i).x, nodePositions.get(i).y);
        }
    }

    void printGenome() {
        ArrayList<ArrayList<Node>> allNodes = new ArrayList<ArrayList<Node>>();

        for (int i = 0; i < layers; i++) {
            ArrayList<Node> temp = new ArrayList<Node>();
            for (int j = 0; j < nodes.size(); j++) {
                if (nodes.get(j).layer == i) {
                    temp.add(nodes.get(j));
                }
            }
            allNodes.add(temp);
        }
        println();
        print("Nodes:");
        for (int i = 0; i < layers - 1; i++) {
            print("(");
            for (int j = 0; j < allNodes.get(i).size() - 1; j++) {
                print(allNodes.get(i).get(j).number + ",");
            }
            print(allNodes.get(i).get(allNodes.get(i).size() - 1).number + ")-");
        }
        print("(");
        for (int j = 0; j < allNodes.get(layers - 1).size() - 1; j++) {
            print(allNodes.get(layers - 1).get(j).number + ",");
        }
        println(allNodes.get(layers - 1).get(allNodes.get(layers - 1).size() - 1).number + ")");
        print("Genes:");
        for (int i = 0; i < layers; i++) {
            for (int j = 0; j < allNodes.get(i).size(); j++) {
                for (int k = 0; k < allNodes.get(i).get(j).outputs.size(); k++) {
                    if (allNodes.get(i).get(j).outputs.get(k).enabled) {
                        print("(" + allNodes.get(i).get(j).outputs.get(k).from + "->" + allNodes.get(i).get(j).outputs.get(k).to + "," + allNodes.get(i).get(j).outputs.get(k).weight + "," + allNodes.get(i).get(j).outputs.get(k).innovationNumber + ")");
                    }
                }
            }
        }
        println();
    }

    void calculateFitness() {
        float sum = 0;
        sum += abs(feedForward(new float[]{0, 0})[0] - 0);
        sum += abs(feedForward(new float[]{0, 1})[0] - 1);
        sum += abs(feedForward(new float[]{1, 0})[0] - 1);
        sum += abs(feedForward(new float[]{1, 1})[0] - 0);
        fitness = 4 - sum;
    }
}