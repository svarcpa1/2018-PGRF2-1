package renderer;

import Model.Vertex;
import raster.ZBufferAlgorithm;
import transforms.Vec3D;

import java.util.Optional;

/**
 * class for triangle rasterize
 */
public class RasterizerTriangles {

    private final ZBufferAlgorithm zb;
    private ColorShader shader;

    public RasterizerTriangles(ZBufferAlgorithm zb, ColorShader shader){
        this.zb=zb;
        this.shader=shader;
    }

    /**
     *
     * triangle rasterize
     *
     * @param v1 first vertex
     * @param v2 second vertex
     * @param v3 third vertex
     */
    public void rasterizer(Vertex v1, Vertex v2, Vertex v3){

        Optional<Vec3D> p1 = v1.getPosition().dehomog();
        Optional<Vec3D> p2 = v2.getPosition().dehomog();
        Optional<Vec3D> p3 = v3.getPosition().dehomog();

        Vec3D a,b,c;

        if (p1.isPresent())
            a=p1.get();
        else return;
        if (p2.isPresent())
            b=p2.get();
        else return;
        if (p3.isPresent())
            c=p3.get();
        else return;

        //fast cutting, when whole triangle is out
        if(	    Math.max( c.getX(), Math.max(a.getX(), b.getX())) < -1.0 ||
                Math.min( c.getX(), Math.min(a.getX(), b.getX())) > 1.0  ||
                Math.max( c.getY(), Math.max(a.getY(), b.getY())) < -1.0 ||
                Math.min( c.getY(), Math.min(a.getY(), b.getY())) > 1.0  ||
                Math.max( c.getZ(), Math.max(a.getZ(), b.getZ())) < 0.0  ||
                Math.min( c.getZ(), Math.min(a.getZ(), b.getZ())) > 1.0) return;

        Vertex vA = v1;
        Vertex vB = v2;
        Vertex vC = v3;

        //viewport transform
        a= a.mul(new Vec3D(1,-1,1))
                .add(new Vec3D(1,1,0))
                .mul(new Vec3D((zb.getWidth()-1)/2.0,
                        (zb.getHeight()-1)/2.0,
                        1));
        b= b.mul(new Vec3D(1,-1,1))
                .add(new Vec3D(1,1,0))
                .mul(new Vec3D((zb.getWidth()-1)/2.0,
                        (zb.getHeight()-1)/2.0,
                        1));
        c= c.mul(new Vec3D(1,-1,1))
                .add(new Vec3D(1,1,0))
                .mul(new Vec3D((zb.getWidth()-1)/2.0,
                        (zb.getHeight()-1)/2.0,
                        1));

        //vector sort - checking - a<=b<=c
        Vec3D tmp;
        Vertex vTmp;
        if(a.getY()>=b.getY()){
            tmp = a;
            a=b;
            b=tmp;

            vTmp = vA;
            vA=vB;
            vB=vTmp;
        }
        if(b.getY()>=c.getY()){
            tmp = b;
            b=c;
            c=tmp;

            vTmp = vB;
            vB=vC;
            vC=vTmp;
        }
        if(a.getY()>=b.getY()){
            tmp = a;
            a=b;
            b=tmp;

            vTmp = vA;
            vA=vB;
            vB=vTmp;
        }

        //first (up) half - AY-BY  ABD
        //choosing biggest value from 0 and a.getY (Math.max)
        for(int y = Math.max((int)a.getY()+1,0); y<Math.min(b.getY(), zb.getHeight()-1); y++){

            double t1, t2;
            t1 = ((y-a.getY())/(b.getY()-a.getY()));
            t2 = ((y-a.getY())/(c.getY()-a.getY()));

            //vertex on AB indice
            Vertex vAB = vA.mul(1-t1).add(vB.mul(t1));
            Vertex vAC = vA.mul(1-t2).add(vC.mul(t2));
            Vec3D ab = a.mul(1.0-t1).add(b.mul(t1));
            Vec3D ac = a.mul(1.0-t2).add(c.mul(t2));

            //controlling ab.getx<ac.getx => from left to right
            if(ab.getX()>ac.getX()){
                Vec3D temporary;
                Vertex temporary1;

                temporary1=vAB;
                vAB=vAC;
                vAC=temporary1;

                temporary=ab;
                ab=ac;
                ac=temporary;
            }
            for(int x = Math.max ((int)ab.getX()+1,0); x<Math.min(ac.getX(), zb.getWidth()-1) ; x++){

                double s = (x-ab.getX())/(ac.getX()-ab.getX());
                Vec3D abc = ab.mul(1-s).add(ac.mul(s));
                Vertex bABC= vAB.mul(1-s).add(vAC.mul(s));
                zb.test(x,y,abc.getZ(),shader.apply(bABC));
            }
        }

        //second (bottom) half - BY-CY
        for(int y = Math.max((int)b.getY()+1,0); y<Math.min(c.getY(), zb.getHeight()-1); y++){

            double t1, t2;
            t1 = ((y-c.getY())/(b.getY()-c.getY()));
            t2 = ((y-c.getY())/(a.getY()-c.getY()));

            //vertex on CB indice
            Vertex vCB = vC.mul(1-t1).add(vB.mul(t1));
            Vertex vCA = vC.mul(1-t2).add(vA.mul(t2));
            Vec3D cb = c.mul(1-t1).add(b.mul(t1));
            Vec3D ca = c.mul(1-t2).add(a.mul(t2));

            //controlling cb.getx<ca.getx => from left to right
            if(cb.getX()>ca.getX()){
                Vec3D temporary;
                Vertex temporary1;

                temporary1=vCB;
                vCB=vCA;
                vCA=temporary1;

                temporary=cb;
                cb=ca;
                ca=temporary;
            }
            for(int x = Math.max((int)cb.getX()+1,0) ; x< Math.min(ca.getX(),zb.getWidth()-1); x++){
                double s = (x-cb.getX())/(ca.getX()-cb.getX());
                Vec3D abc = cb.mul(1-s).add(ca.mul(s));
                Vertex bBCA= vCB.mul(1-s).add(vCA.mul(s));
                zb.test(x,y,abc.getZ(),shader.apply(bBCA));
            }
        }
    }
}
