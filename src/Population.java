import java.util.ArrayList;

class Population {
    int inputs, outputs, size;
    ArrayList<Genome> genomes, representators;
    ArrayList<ArrayList<Genome>> species;
    Genome bestGenome;

    Population(int inputs_, int outputs_, int size_) {
        inputs = inputs_;
        outputs = outputs_;
        size = size_;
        genomes = new ArrayList<>();
        for (int i = 0; i < size_; i++) {
            genomes.add(new Genome(inputs, outputs));
            genomes.get(i).mutate();
        }
        bestGenome = genomes.get(0).clone_();
        System.out.println(size);
    }

    void naturalSelection() {
        generateSpecies();
        test();
        setBestGenome();
        crossover();
        mutate();
        setRepresentators();
    }

    void mutate() {
        for (int i = 0; i < size; i++) {
            genomes.get(i).mutate();
        }
    }

    void generateSpecies() {
        species = new ArrayList<>();
        if (representators != null) {
            for (int i = 0; i < representators.size(); i++) {
                species.add(new ArrayList<>());
                species.get(i).add(representators.get(i));
            }
        } else {
            ArrayList<Genome> temp = new ArrayList<>();
            temp.add(genomes.get(0));
            species.add(temp);
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < species.size(); j++) {
                if (genomes.get(i).isSimilarTo(species.get(j).get(0), size)) {
                    species.get(j).add(genomes.get(i));
                    break;
                } else if (j == species.size() - 1) {
                    species.add(new ArrayList<Genome>());
                    species.get(species.size() - 1).add(genomes.get(i));
                }
            }
        }
    }

    void crossover() {
    }

    void setRepresentators() {
        representators = new ArrayList<Genome>(species.size());
        for (int i = 0; i < species.size(); i++) {
            representators.set(i, species.get(i).get((int) App.processing.random(species.get(i).size())));
        }
    }

    void test() {
        for (int i = 0; i < size; i++) {
            genomes.get(i).calculateFitness();
            System.out.println(genomes.get(i).fitness);
        }
    }

    void setBestGenome() {
        for (int i = 0; i < size; i++) {
            if (genomes.get(i).fitness > bestGenome.fitness) {
                bestGenome = genomes.get(i).clone_();
            }
        }
    }
}