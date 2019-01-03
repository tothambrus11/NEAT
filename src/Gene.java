class Gene {
    int from, to;
    double weight;
    boolean enabled = true;
    int innovationNumber;

    Gene(int from, int to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        this.innovationNumber = getInnovationNumber(from, to);
    }

    int getInnovationNumber(int from, int to) {
        boolean isNew = true;
        int connectionInnovationNumber = App.innovationCount;
        for (int i = 0; i < App.innovationCount; i++) {
            if (App.innovationIn.get(i) == from && App.innovationOut.get(i) == to) {
                isNew = false;
                connectionInnovationNumber = i;
                break;
            }
        }

        if (isNew) {
            App.innovationIn.add(from);
            App.innovationOut.add(to);
            App.innovationCount++;
        }
        return connectionInnovationNumber;
    }

    void mutateWeight() {
        weight += App.processing.randomGaussian();
        if (weight > App.weightMax) {
            weight = App.weightMax;
        } else if (weight < App.weightMin) {
            weight = App.weightMin;
        }
    }

    void restartWeight() {
        weight = App.processing.random((float) -0.5, (float) 0.5);
        if (weight > App.weightMax) {
            weight = App.weightMax;
        } else if (weight < App.weightMin) {
            weight = App.weightMin;
        }
    }


    Gene clone_() {
        Gene clone = new Gene(from, to, weight);
        clone.enabled = enabled;
        clone.innovationNumber = innovationNumber;
        return clone;
    }
}