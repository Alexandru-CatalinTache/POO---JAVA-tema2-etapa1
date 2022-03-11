import entitati.Consumers;
import entitati.Distributors;

import java.util.ArrayList;

public class Output {
   private ArrayList<Consumers> consumers;
    private ArrayList<Distributors> distributors;

    public Output(final ArrayList<Consumers> consumers,
                  final ArrayList<Distributors> distributors) {
        this.consumers = consumers;
        this.distributors = distributors;
    }

    /**
     * lista consumatori
     * @return
     */
    public ArrayList<Consumers> getConsumers() {
        return consumers;
    }

    /**
     * lista distributori
     * @return
     */
    public ArrayList<Distributors> getDistributors() {
        return distributors;
    }
}
