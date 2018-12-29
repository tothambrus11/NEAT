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
        bestGenome = genomes.get(0);
        bestGenome.calculateFitness();
    }

    void naturalSelection() {
        generateSpecies();
        calculateFitness();
        setBestGenome();
        modifyFitness();
        crossover();
        mutate();
    }

    void mutate() {
        for (Genome genome : genomes) {
            genome.mutate();
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

    /**
     * Minden faj legjobbját örökíti (új genomes-ba)
     * A maradék helyet feltölti a ranom krosszoverálgatással
     */
    void crossover() {
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

        while(genomes.size()<size){
            Genome parent1=getRandomGenome();
            Genome parent2=getRandomGenome();
            if(parent1.fitness>parent2.fitness) {
                genomes.add(parent1.crossover(parent2));
            } else {
                genomes.add(parent2.crossover(parent1));
            }
        }
    }

    void modifyFitness() {
        for (ArrayList<Genome> genomesInSpecies : species) {
            for (Genome genome : genomesInSpecies) {
                genome.fitness /= genomesInSpecies.size();
            }
        }
    }

    void calculateFitness() {
        for (Genome genome : genomes) {
            genome.calculateFitness();
        }
    }

    void setBestGenome() {
        for (Genome genome : genomes) {
            if (genome.fitness > bestGenome.fitness) {
                bestGenome = genome;
            }
        }
    }

    Genome getRandomGenome(){
        double fitnessSum = 0;
        for(ArrayList<Genome> genomesInSpecies : species){
            for (Genome genome:genomesInSpecies ) {
                fitnessSum+=genome.fitness;
            }
        }

        double currentSum=species.get(0).get(0).fitness, randomIndex=App.processing.random((float)fitnessSum);
        int i=0,j=0;
        while(currentSum<randomIndex) {
            j++;
            if(j==species.get(i).size()){
                i++;
                j=0;
            }
            currentSum+=species.get(i).get(j).fitness;
        }
        return species.get(i).get(j);
    }
}