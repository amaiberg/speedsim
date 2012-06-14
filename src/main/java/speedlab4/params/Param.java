package speedlab4.params;

import speedlab4.model.Visitable;

/**
 * Created by IntelliJ IDEA.
 * User: avnermaiberg
 * Date: 1/25/12
 * Time: 7:13 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Param<E> implements Visitable {
    public String name, description;
    public E value;
    public boolean reqRestart = false;

    public Param(String name, E value) {
        this.value = value;
        this.name = name;
    }

    public Param(String name, E value, String description) {
        this(name,value);
        this.description = description;
    }

    public Param(String name, E value, String description, boolean restart){
        this(name,value,description);
        reqRestart = restart;
    }

    public boolean equals(Param p) {
        return this.name.equals(p.name);
    }

    public abstract void setParam(E val);


}
