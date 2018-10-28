package objectdata;

import Model.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;
import transforms.Vec3D;

import java.util.ArrayList;
import java.util.List;

/**
 * CLASS HOLDING DATA FOR PYRAMID RENDERING
 */
public class Pyramid implements Mesh {
    private final List<Vertex> vertices;
    private final List<Integer> indices;

    public Pyramid() {
        vertices = new ArrayList<>();
        indices = new ArrayList<>();

        Point3D a = new Point3D(0.0,0.5,0.5);
        Point3D b = new Point3D(0.5,0.0,0.5);
        Point3D c = new Point3D(1.0,0.5,0.5);
        Point3D d = new Point3D(0.5,1.0,0.5);
        Point3D e = new Point3D(0.5,0.5,2.0);

        vertices.add(new Vertex(a,new Col(0xff0000),new Vec3D(0,0,-1),
                new Vec2D(0.2,0.4)));
        vertices.add(new Vertex(b,new Col(0xff0000),new Vec3D(0,0,-1),
                new Vec2D(0.7,0.8)));
        vertices.add(new Vertex(c,new Col(0xff0000),new Vec3D(0,0,-1),
                new Vec2D(0.2,0.4)));
        vertices.add(new Vertex(d,new Col(0xff0000),new Vec3D(0,0,-1),
                new Vec2D(0.7,0.8)));
        vertices.add(new Vertex(e,new Col(0x00ff00),new Vec3D(0,0,-1),
                new Vec2D(0.7,0.8)));

        indices.add(0); indices.add(1); indices.add(4);
        indices.add(1); indices.add(2); indices.add(4);
        indices.add(2); indices.add(3); indices.add(4);
        indices.add(3); indices.add(0); indices.add(4);
        indices.add(0); indices.add(1); indices.add(3);
        indices.add(1); indices.add(2); indices.add(3);
    }

    @Override
    public List<Vertex> getVertices() {
        return vertices;
    }
    @Override
    public List<Integer> getIndices() {
        return indices;
    }
}
