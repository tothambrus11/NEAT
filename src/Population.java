import java.util.ArrayList;

class Population {
    int inputs, outputs, size;
    ArrayList<Genome> genomes, representators;
    ArrayList<ArrayList<Genome>> species;
    Genome bestGenome;
    double bestFitness;

    Population(int inputs_, int outputs_, int size_) {
        inputs = inputs_;
        outputs = outputs_;
        size = size_;
        genomes = new ArrayList<>();
        for (int i = 0; i < size_; i++) {
            genomes.add(new Genome(inputs, outputs));
            for (int j = 0; j < 5; j++) {
                genomes.get(i).mutate();
            }
        }
        bestGenome = genomes.get(0);
        bestGenome.calculateFitness();
        bestFitness = bestGenome.fitness;
    }

    void naturalSelection() {
        sortToSpecies();
        System.out.println(species.size());
        calculateFitness();
        drawPopulation(0, 0, App.processing.width, App.processing.height, 8, 6, 1);
        setBestGenome();
        modifyFitness();
        reproduction();
        mutateAll();
    }

    void sortToSpecies() {
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
        for (Genome genome : genomes) {
            for (int i = 0; i < species.size(); i++) {
                if (genome.isSimilarTo(species.get(i).get(0), size)) {
                    species.get(i).add(genome);
                    break;
                } else if (i == species.size() - 1) {
                    species.add(new ArrayList<>());
                    species.get(species.size() - 1).add(genome);
                }
            }
        }
    }

    void calculateFitness() {
        for (Genome genome : genomes) {
            genome.calculateFitness();
        }
    }

    void setBestGenome() {
        int index = -1;
        for (int i = 0; i < genomes.size(); i++) {
            if (genomes.get(i).fitness > bestFitness) {
                index = i;
            }
        }
        if (index != -1) {
            bestGenome = genomes.get(index).clone_();
            bestFitness = genomes.get(index).fitness;
            System.out.println("new best");
            bestGenome.calculateNetwork();
            System.out.println(bestGenome.feedForward(new double[]{0.0, 0.0})[0]);
            System.out.println(bestGenome.feedForward(new double[]{0.0, 1.0})[0]);
            System.out.println(bestGenome.feedForward(new double[]{1.0, 0.0})[0]);
            System.out.println(bestGenome.feedForward(new double[]{1.0, 1.0})[0]);
            System.out.println();
            bestGenome.save();
        }
    }

    void modifyFitness() {
        for (ArrayList<Genome> genomesInSpecies : species) {
            for (Genome genome : genomesInSpecies) {
                genome.fitness /= genomesInSpecies.size();
            }
        }
    }

    void reproduction() {
        genomes.clear();
        representators = new ArrayList<>();
        for (ArrayList<Genome> genomesInSpecies : species) {
            representators.add(genomesInSpecies.get((int) App.processing.random(genomesInSpecies.size())));
            Genome bestGenomeInCurrentSpecies = genomesInSpecies.get(0);
            for (Genome genome : genomesInSpecies) {
                if (bestGenomeInCurrentSpecies.fitness < genome.fitness) {
                    bestGenomeInCurrentSpecies = genome;
                }
            }
            genomes.add(bestGenomeInCurrentSpecies);
        }

        while (genomes.size() < size) {
            Genome parent1 = getRandomGenome();
            Genome parent2 = getRandomGenome();
            if (parent1.fitness > parent2.fitness) {
                genomes.add(parent1.crossover(parent2));
            } else {
                genomes.add(parent2.crossover(parent1));
            }
        }
    }

    void mutateAll() {
        for (Genome genome : genomes) {
            genome.mutate();
        }
    }

    Genome getRandomGenome() {
        double fitnessSum = 0;
        for (ArrayList<Genome> genomesInSpecies : species) {
            for (Genome genome : genomesInSpecies) {
                fitnessSum += genome.fitness;
            }
        }

        double currentSum = species.get(0).get(0).fitness, randomIndex = App.processing.random((float) fitnessSum);
        int i = 0, j = 0;
        while (currentSum < randomIndex) {
            j++;
            if (j == species.get(i).size()) {
                i++;
                j = 0;
            }
            currentSum += species.get(i).get(j).fitness;
        }
        return species.get(i).get(j);
    }

    void drawPopulation(double x1, double y1, double x2, double y2, int nx, int ny, int mode) {
        if (mode == 0) {
            double dx = (x2 - x1) / nx;
            double dy = (y2 - y1) / ny;
            App.processing.stroke(255, 100);
            for (int i = 0; i < nx + 1; i++) {
                App.processing.line((float) (x1 + (x2 - x1) / nx * i), (float) y1, (float) (x1 + (x2 - x1) / nx * i), (float) y2);
            }
            for (int i = 0; i < ny + 1; i++) {
                App.processing.line((float) x1, (float) (y1 + (y2 - y1) / ny * i), (float) x2, (float) (y1 + (y2 - y1) / ny * i));
            }
            double y = y1;
            int index = 0;
            for (int i = 0; i < ny; i++) {
                double x = x1;
                for (int j = 0; j < nx; j++) {
                    if (index < genomes.size()) {
                        genomes.get(index).draw(x, y, x + dx, y + dy, App.nodeMaxSize, App.weightMaxSize, 1);
                    }
                    index++;
                    x += dx;
                }
                y += dy;
            }
        }
        if (mode == 1) {
            double dx = (x2 - x1) / nx;
            double dy = (y2 - y1) / ny;
            App.processing.stroke(255, 100);
            for (int i = 0; i < nx + 1; i++) {
                App.processing.line((float) (x1 + (x2 - x1) / nx * i), (float) y1, (float) (x1 + (x2 - x1) / nx * i), (float) y2);
            }
            for (int i = 0; i < ny + 1; i++) {
                App.processing.line((float) x1, (float) (y1 + (y2 - y1) / ny * i), (float) x2, (float) (y1 + (y2 - y1) / ny * i));
            }
            bestGenome.draw(x1, y1, x1 + dx, y1 + dy, App.nodeMaxSize, App.weightMaxSize, 1);
            double y = y1;
            int index = 0;
            double x = x1;
            for (int j = 1; j < nx; j++) {
                x += dx;
                if (index < genomes.size()) {
                    genomes.get(index).draw(x, y, x + dx, y + dy, App.nodeMaxSize, App.weightMaxSize, 1);
                }
                index++;
            }
            for (int i = 1; i < ny; i++) {
                y += dy;
                x = x1;
                for (int j = 0; j < nx; j++) {
                    if (index < genomes.size()) {
                        genomes.get(index).draw(x, y, x + dx, y + dy, App.nodeMaxSize, App.weightMaxSize, 1);
                    }
                    index++;
                    x += dx;
                }
            }
        }
    }
}