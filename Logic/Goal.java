import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Goal {
    private final Atom[] atoms;

    Goal(Atom... atoms) {
        this.atoms = atoms;
    }

    public Atom[] atoms() {
        return atoms;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Goal that = (Goal) obj;
        return Objects.equals(this.atoms, that.atoms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(atoms);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for(Atom a: atoms){
            b.append(a);
            b.append(",");
        }
        if(atoms.length==0){
            return "[]";
        }
        b.deleteCharAt(b.length()-1);
        return b.toString();
    }

    public Goal applySub(Substitution substitution) {
        Atom[] new_atoms = new Atom[atoms.length];
        for (int i = 0; i < atoms.length; i++) {
            new_atoms[i] = atoms[i].applySub(substitution);
        }
        return new Goal(new_atoms);
    }

    public Goal add(Atom atomToAdd) {
        ArrayList<Atom> new_atoms = new ArrayList<>(Arrays.asList(atoms));
        if (!new_atoms.contains(atomToAdd)) {
            new_atoms.add(atomToAdd);
        }
        return new Goal(new_atoms.toArray(new Atom[0]));
    }

    public Goal add(Atom[] atomsToAdd) {
        ArrayList<Atom> new_atoms = new ArrayList<>(Arrays.asList(atoms));
        for (Atom a : atomsToAdd) {
            if (!new_atoms.contains(a)) {
                new_atoms.add(a);
            }
        }
        return new Goal(new_atoms.toArray(new Atom[0]));
    }

    public Goal add(List<Atom> atomsToAdd) {
        ArrayList<Atom> new_atoms = new ArrayList<>(Arrays.asList(atoms));
        for (Atom a : atomsToAdd) {
            if (!new_atoms.contains(a)) {
                new_atoms.add(a);
            }
        }
        return new Goal(new_atoms.toArray(new Atom[0]));
    }

    public Goal remove(Atom selectedAtom) {
        ArrayList<Atom> new_atoms = new ArrayList<>();
        for (Atom a : atoms) {
            if (!selectedAtom.equals(a)) {
                new_atoms.add(a);
            }
        }
        return new Goal(new_atoms.toArray(new Atom[0]));
    }

    public boolean containsNegativeTime() {
        for(Atom a: atoms){
            if(a.temporal().isNegative()) return true;
        }
        return false;
    }

}
