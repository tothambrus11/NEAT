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
        int connectionInnovationNumber = innovationCount;
        for (int i = 0; i < innovationCount; i++) {
            if (innovationIn.get(i) == from && innovationOut.get(i) == to) {
                isNew = false;
                connectionInnovationNumber = i;
                break;
            }
        }

        if (isNew) {
            innovationIn.add(from);
            innovationOut.add(to);
            innovationCount++;
        }
        return connectionInnovationNumber;
    }

    void mutateWeight() {
        weight += randomGaussian() / 20;
        if (weight > weightMax) {
            weight = weightMax;
        }
        if (weight < weightMin) {
            weight = weightMin;
        }
    }

    void restartWeight() {
        weight = random(-1, 1);
        if (weight > weightMax) {
            weight = weightMax;
        }
        if (weight < weightMin) {
            weight = weightMin;
        }
    }

    Gene clone() {
        Gene clone = new Gene(from, to, weight);
        clone.enabled = enabled;
        clone.innovationNumber = innovationNumber;
        return clone;
    }
}