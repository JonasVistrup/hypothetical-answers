package Jonas.Logic;

import java.util.List;

public interface Selector {
    List<Clause> getClausesFor(Atom a);
}
