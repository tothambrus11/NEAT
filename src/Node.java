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
        for (int i = 0; i < outputs.size(); i++) {
            if (outputs.get(i).enabled) {
                genome.getNode(outputs.get(i).to).sum += outputs.get(i).weight * value;
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
            for (int i = 0; i < outputs.size(); i++) {
                if (outputs.get(i).to == node.number) {
                    return true;
                }
            }
        }
        return false;
    }

    float function(float x) {
        //return (2.0*x)/(abs(2.0*x)+1)+0.1*x;
        //return atan(x);
        if (x > 0) return x;
        return 0.15 * x;
        //return (2.0*x)/(1+abs(x))+0.2*abs(x)*x/(abs(x)+1);
    }

    Node clone() {
        Node clone = new Node(number);
        clone.layer = layer;
        return clone;
    }
}