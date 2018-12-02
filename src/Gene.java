class Gene {
    int from, to;
    float weight;
    boolean enabled = true;
    int innovationNumber;

    Gene(int from_, int to_, float weight_) {
        from = from_;
        to = to_;
        weight = weight_;
        innovationNumber = getInnovationNumber(from, to);
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
        weight += App.processing.randomGaussian() / 20;
        if (weight > App.weightMax) {
            weight = App.weightMax;
        }
        if (weight < App.weightMin) {
            weight = App.weightMin;
        }
    }

    void restartWeight() {
        weight = App.processing.random(-1, 1);
        if (weight > App.weightMax) {
            weight = App.weightMax;
        }
        if (weight < App.weightMin) {
            weight = App.weightMin;
        }
    }

    void changeActivation() {
        enabled = !enabled;
    }

    Gene cloneGene() {
        Gene clone = new Gene(from, to, weight);
        clone.enabled = enabled;
        clone.innovationNumber = innovationNumber;

        return clone;
    }
}