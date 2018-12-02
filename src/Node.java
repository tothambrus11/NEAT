import java.util.ArrayList;

class Node {
    int number;
    int layer = 0;
    float sum = 0;
    float value;
    ArrayList<Gene> outputs = new ArrayList<Gene>();

    Node(int number_) {
        number = number_;
    }

    void feed(Genome genome) {
        if (layer > 0) {
            value = function(sum);
        }
        for (Gene output : outputs) {
            if (output.enabled) {
                genome.getNode(output.to).sum += output.weight * value;
            }
        }
    }

    boolean connected(Node node) {
        if (node.layer < layer) {
            for (int i = 0; i < node.outputs.size(); i++) {
                if (node.outputs.get(i).to == this.number) {
                    return true;
                }
            }
        } else if (node.layer > layer) {
            for (Gene output : outputs) {
                if (output.to == node.number) {
                    return true;
                }
            }
        }
        return false;
    }

    float function(float x) {
        return (float) (1.0f / (1.0f + Math.pow(Math.E, -x)));
    }

    Node cloneNode() {
        Node clone = new Node(number);
        clone.layer = layer;
        return clone;
    }
}