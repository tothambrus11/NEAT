import java.util.ArrayList;

class Node {
    int number;
    int layer = 0;
    double sum = 0;
    double value;
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
        }
        if (node.layer > layer) {
            for (Gene output : outputs) {
                if (output.to == node.number) {
                    return true;
                }
            }
        }
        return false;
    }

    double function(double x) {
        //return (2.0*x)/(abs(2.0*x)+1)+0.1*x;
        //return atan(x);
        if (x > 0) return x;
        return 0.15f * x;
        //return (2.0*x)/(1+abs(x))+0.2*abs(x)*x/(abs(x)+1);
    }

    Node clone_() {
        Node clone = new Node(number);
        clone.layer = layer;
        return clone;
    }
}