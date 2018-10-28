package objectdata;

import Model.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;
import transforms.Vec3D;

import java.util.ArrayList;
import java.util.List;

/**
 * CLASS HOLDING DATA FOR CUBE RENDERING
 */
public class Cube implements Mesh{
    private final List<Vertex> vertices;
    private final List<Integer> indices;

    public Cube() {
        vertices = new ArrayList<>();
        indices = new ArrayList<>();

        Point3D a =new Point3D(0,0,0);
        Point3D b =new Point3D(1,0,0);
        Point3D c =new Point3D(1,1,0);
        Point3D d =new Point3D(0,1,0);
        Point3D e =new Point3D(0,0,1);
        Point3D f =new Point3D(1,0,1);
        Point3D g =new Point3D(1,1,1);
        Point3D h =new Point3D(0,1,1);

        vertices.add(new Vertex(a,new Col(0xfff000), new Vec3D(0,0,-1),
                new Vec2D(0.1,0.1)));
        vertices.add(new Vertex(b,new Col(0xff0000), new Vec3D(0,0,-1),
                new Vec2D(0.1,0.1)));
        vertices.add(new Vertex(c,new Col(0xffff00), new Vec3D(0,0,-1),
                new Vec2D(0.1,0.1)));
        vertices.add(new Vertex(d,new Col(0xff0000), new Vec3D(0,0,-1),
                new Vec2D(0.1,0.1)));
        vertices.add(new Vertex(e,new Col(0x0000ff), new Vec3D(0,0,-1),
                new Vec2D(0.1,0.1)));
        vertices.add(new Vertex(f,new Col(0x000fff), new Vec3D(0,0,-1),
                new Vec2D(0.1,0.1)));
        vertices.add(new Vertex(g,new Col(0x0000ff), new Vec3D(0,0,-1),
                new Vec2D(0.1,0.1)));
        vertices.add(new Vertex(h,new Col(0x00ffff), new Vec3D(0,0,-1),
                new Vec2D(0.1,0.1)));

        //right-turned triangles
        indices.add(0); indices.add(1); indices.add(2);
        indices.add(3); indices.add(0); indices.add(2);//front
        indices.add(3); indices.add(7); indices.add(2);
        indices.add(7); indices.add(2); indices.add(6);//bottom
        indices.add(6); indices.add(1); indices.add(2);
        indices.add(6); indices.add(5); indices.add(1);//rear
        indices.add(5); indices.add(1); indices.add(4);
        indices.add(4); indices.add(0); indices.add(1);//top
        indices.add(4); indices.add(3); indices.add(0);
        indices.add(3); indices.add(4); indices.add(7);//right
        indices.add(4); indices.add(6); indices.add(7);
        indices.add(5); indices.add(4); indices.add(6);//left
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
