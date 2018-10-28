package renderer;

import Model.Vertex;
import raster.ZBufferAlgorithm;
import transforms.Col;
import transforms.Vec3D;

import java.util.Optional;
import java.util.function.Function;

/**
 * class for line rasterize
 */
public class RasterizerLines {

    private final ZBufferAlgorithm zb;
    private final Function<Vertex, Col> shader;

    public RasterizerLines(ZBufferAlgorithm zb, Function<Vertex, Col>shader){
        this.zb=zb;
        this.shader=shader;
    }

    /**
     *
     * line rasterize
     *
     * @param v1 first vertex
     * @param v2 second vertex
     */
    public void rasterizer(Vertex v1, Vertex v2){

        Optional<Vec3D> p1 = v1.getPosition().dehomog();
        Optional<Vec3D> p2 = v2.getPosition().dehomog();

        Vec3D a, b;

        if (p1.isPresent())
            a = p1.get();
        else return;
        if (p2.isPresent())
            b = p2.get();
        else return;

        //fast cutting, when whole triangle is out
        if(	    Math.max(a.getX(), b.getX()) < -1.0 ||
                Math.min(a.getX(), b.getX()) > 1.0  ||
                Math.max(a.getY(), b.getY()) < -1.0 ||
                Math.min(a.getY(), b.getY()) > 1.0  ||
                Math.max(a.getZ(), b.getZ()) < 0.0  ||
                Math.min(a.getZ(), b.getZ()) > 1.0) return;

        Vertex vA = v1;
        Vertex vB = v2;

        //viewport transform
        a = a.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D((zb.getWidth() - 1) / 2.0,
                        (zb.getHeight() - 1) / 2.0,
                        1));
        b = b.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D((zb.getWidth() - 1) / 2.0,
                        (zb.getHeight() - 1) / 2.0,
                        1));

        //leading axe is y
        if(Math.abs(a.getX()-b.getX())<=Math.abs(a.getY()-b.getY())){
            if(a.getY()>b.getY()){
                Vec3D tmp;
                Vertex vTmp;

                tmp = a;
                a = b;
                b = tmp;

                vTmp = vA;
                vA = vB;
                vB = vTmp;
            }
            for(int y =Math.max ((int)a.getY()+1,0); y<Math.min(b.getY(), zb.getHeight()-1) ; y++){
                double s = (y-a.getY())/(b.getY()-a.getY());
                Vec3D abc = a.mul(1-s).add(b.mul(s));
                Vertex bABC= vA.mul(1-s).add(vB.mul(s));
                zb.test((int)abc.getX(),y,abc.getZ(),shader.apply(bABC));
            }
        }
        else {
            if(a.getX()>b.getX()){
                Vec3D tmp;
                Vertex vTmp;

                tmp = a;
                a = b;
                b = tmp;

                vTmp = vA;
                vA = vB;
                vB = vTmp;
            }
            for(int x =Math.max((int)a.getX()+1,0); x<Math.min(b.getX(),zb.getWidth()-1) ; x++){
                double s = (x-a.getX())/(b.getX()-a.getX());
                Vec3D abc = a.mul(1-s).add(b.mul(s));
                Vertex bABC= vA.mul(1-s).add(vB.mul(s));
                zb.test(x,(int)abc.getY(),abc.getZ(),shader.apply(bABC));
            }
        }
    }
}
