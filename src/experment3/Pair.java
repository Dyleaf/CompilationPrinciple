package experment3;

class Pair<E extends Object, F extends Object> {
    private E first;
    private F second;

    public Pair(E first, F second) {
        super();
        setFirst(first);
        setSecond(second);
    }

    public Pair() {
        super();
    }

    public F getSecond() {
        return second;
    }

    public void setSecond(F second) {
        this.second = second;
    }

    public E getFirst() {
        return first;
    }

    public void setFirst(E first) {
        this.first = first;
    }
}