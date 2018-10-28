package objectdata;

import Model.Vertex;
import transforms.*;

import java.util.ArrayList;
import java.util.List;

/**
 * CLASS HOLDING DATA FOR BICUBIC FLAT
 */
public class Flat{
    private final List<Point3D> vertices;
    private final List<Integer> indices;
    private final List<Vertex> reCountVertices;
    private Mat4 matRule;
    private final Bicubic bc;
    private static final int POINTS = 20;

    public Flat(int intTypeFlat){
        vertices = new ArrayList<>();
        indices = new ArrayList<>();
        reCountVertices= new ArrayList<>();

        //rows
        //p00-p03
        vertices.add(new Point3D(0,0,2));
        vertices.add(new Point3D(0.5,0.2,2));
        vertices.add(new Point3D(1,0.4,2));
        vertices.add(new Point3D(1.2,0.6,2));
        //p10-p13
        vertices.add(new Point3D(0,0.3,1.5));
        vertices.add(new Point3D(0.5,3,1.5));
        vertices.add(new Point3D(1,0.3,1.5));
        vertices.add(new Point3D(1.2,3,1.5));
        //p20-p23
        vertices.add(new Point3D(0,1,1));
        vertices.add(new Point3D(0.5,0,1));
        vertices.add(new Point3D(1,0,1));
        vertices.add(new Point3D(1.2,1,1));
        //p30-p33
        vertices.add(new Point3D(0,1,0.5));
        vertices.add(new Point3D(0.5,0.5,0.5));
        vertices.add(new Point3D(1,0,0.5));
        vertices.add(new Point3D(1.2,.5,0.5));

        bc=new Bicubic(setTypeFlat(intTypeFlat)
                ,vertices.get(0),vertices.get(1),vertices.get(2),vertices.get(3)
                ,vertices.get(4),vertices.get(5),vertices.get(6),vertices.get(7)
                ,vertices.get(8),vertices.get(9),vertices.get(10),vertices.get(11)
                ,vertices.get(12),vertices.get(13),vertices.get(14),vertices.get(15));

        //for cycle for generation points (using compute method) and filling them int reCount lists
        for (int i=0; i<POINTS; i++) {
            for (int j=0; j<POINTS; j++) {
                reCountVertices.add(new Vertex((bc.compute((double) i /
                            (POINTS),(double) j / (POINTS))),
                        new Col(0xA7BFFF),
                        new Vec3D(0,0,-1),
                        new Vec2D(0.1,0.1)));
            }
        }

        //for cycle for connecting points
        for (int i=0; i<POINTS-1; i++) {
            for (int j=0; j<POINTS-1; j++) {

                //first triangle
                indices.add(i*POINTS+j);
                indices.add((i+1)*POINTS+j);
                indices.add(i*POINTS+(j+1));

                //second triangle
                indices.add(i*POINTS+(j+1));
                indices.add((i+1)*POINTS+(j+1));
                indices.add((i+1)*POINTS+j);
            }
        }
    }

    /**
     * Method for setting cubic dependent on typeCurve
     * @param intTypeFlat code of type of flat
     * @return Cubic object filled with specific curve cubic
     */
    public Mat4 setTypeFlat(int intTypeFlat){
        if (intTypeFlat==0) matRule = Cubic.FERGUSON;
        if (intTypeFlat==1) matRule = Cubic.BEZIER;
        if (intTypeFlat==2) matRule = Cubic.COONS;
        return matRule;
    }

    public List<Integer> getIndices() {
        return indices;
    }
    public List<Vertex> getReCountVertices() {
        return reCountVertices;
    }
}
