package objectdata;

import Model.Vertex;

import java.util.List;

public interface Mesh {
    List<Vertex> getVertices();
    List<Integer> getIndices();
}
