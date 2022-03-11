package rounds;

import entitati.Consumers;

import java.util.ArrayList;
import java.util.HashMap;

public class Round {

    private int numberOfRound;
    private ArrayList<Consumers> newConsumers = new ArrayList<>();
    // hashmap in care am id-ul distribuitorului si costurile schimbate pentru acesta luna curenta
    private HashMap<Integer,
            ArrayList<Integer>> distributorChanges = new HashMap<>();

    public  Round(final int numberOfRounds, final ArrayList<Consumers> newConsumers,
                final HashMap<Integer, ArrayList<Integer>> distributorChanges) {
        this.numberOfRound = numberOfRounds;

        this.newConsumers = newConsumers;
        this.distributorChanges = distributorChanges;
    }

    public final int getNumberOfRounds() {
        return numberOfRound;
    }

    public final void setNumberOfRounds(final int numberOfRounds) {
        this.numberOfRound = numberOfRounds;
    }

    public final ArrayList<Consumers> getNewConsumers() {
        return newConsumers;
    }

    public final void setNewConsumers(final ArrayList<Consumers> newConsumers) {
        this.newConsumers = newConsumers;
    }

    public final HashMap<Integer, ArrayList<Integer>> getDistributorChanges() {
        return distributorChanges;
    }

    public final void setDistributorChanges(final HashMap<Integer,
            ArrayList<Integer>> distributorChanges) {
        this.distributorChanges = distributorChanges;
    }


    @Override
    public String toString() {
        return "Round{"
                + "numberOfRound=" + numberOfRound
                + ", newConsumers=" + newConsumers
                + ", distributorChanges=" + distributorChanges
                + '}';
    }
}
