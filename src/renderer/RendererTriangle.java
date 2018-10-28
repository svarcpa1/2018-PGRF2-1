package renderer;

import Model.Vertex;
import raster.ZBufferAlgorithm;
import transforms.Mat4;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * class for rendering triangles
 */
public class RendererTriangle {

    private final ZBufferAlgorithm zb;
    private final RasterizerTriangles rasterizerT;
    private static final double BORDER = 0.0000001;

    public RendererTriangle(ZBufferAlgorithm zb, RasterizerTriangles rasterizerT){
        this.zb=zb;
        this.rasterizerT=rasterizerT;
    }

    /**
     *
     * making buffered image from vertices and indices
     *
     * @param vertices list of objects vertices
     * @param indices list of objects indices
     * @param t transformation matrix
     * @return buffered image filled with help of zb
     */
    public BufferedImage draw(List<Vertex> vertices, List<Integer> indices, Mat4 t){
        for (int i = 0; i < indices.size(); i += 3) {

            Vertex o1 = vertices.get(indices.get(i));
            Vertex o2 = vertices.get(indices.get(i + 1));
            Vertex o3 = vertices.get(indices.get(i + 2));

            Vertex p1 = new Vertex(o1.getPosition().mul(t),o1.getColor(),o1.getNormal(),o1.getTexUV());
            Vertex p2 = new Vertex(o2.getPosition().mul(t),o2.getColor(),o2.getNormal(),o2.getTexUV());
            Vertex p3 = new Vertex(o3.getPosition().mul(t),o3.getColor(),o3.getNormal(),o3.getTexUV());

            //clopping triangles due to w (position of watcher)
            //p1>p2>p3
            //sorting from w (use for clipping); p1 is the most far away
            if(p2.getPosition().getW()>p1.getPosition().getW()){
                Vertex tmp;
                tmp = p1;
                p1=p2;
                p2=tmp;
            }
            if(p3.getPosition().getW()>p2.getPosition().getW()){
                Vertex tmp;
                tmp = p2;
                p2=p3;
                p3=tmp;
            }
            if(p2.getPosition().getW()>p1.getPosition().getW()){
                Vertex tmp;
                tmp = p1;
                p1=p2;
                p2=tmp;
            }

            //whole triangle is in
            if(p3.getPosition().getW() > BORDER){
                rasterizerT.rasterizer(p1,p2,p3);
            }
            else{
                //1 vertex out
                if (p2.getPosition().getW() > BORDER){

                    //substract min, divided by range - mantra :)
                    double tmp = (p2.getPosition().getW() - BORDER) /
                            (p2.getPosition().getW() - p3.getPosition().getW());
                    Vertex NewV1 = new Vertex(p2.getPosition().mul(1 - tmp).add(p3.getPosition().mul(tmp)),
                            p3.getColor(),
                            p3.getNormal(),
                            p3.getTexUV());
                    tmp = (p1.getPosition().getW() - BORDER) / (p1.getPosition().getW() - p3.getPosition().getW());
                    Vertex NewV2 = new Vertex(p1.getPosition().mul(1 - tmp).add(p3.getPosition().mul(tmp)),
                            p3.getColor(),
                            p3.getNormal(),
                            p3.getTexUV());

                    rasterizerT.rasterizer(p1, NewV1, NewV2);
                    rasterizerT.rasterizer(p1, p2, NewV1);
                }
                //2 vertexes out
                else{

                    if(p1.getPosition().getW()>BORDER) {
                        //substract min, divided by range - mantra :)
                        double tmp = (p1.getPosition().getW() - BORDER) /
                                (p1.getPosition().getW() - p2.getPosition().getW());
                        Vertex NewV1 = new Vertex(p1.getPosition().mul(1 - tmp).add(p2.getPosition().mul(tmp)),
                                p2.getColor(),
                                p2.getNormal(),
                                p2.getTexUV());
                        tmp = (p1.getPosition().getW() - BORDER) / (p1.getPosition().getW() - p3.getPosition().getW());
                        Vertex NewV2 = new Vertex(p1.getPosition().mul(1 - tmp).add(p3.getPosition().mul(tmp)),
                                p2.getColor(),
                                p2.getNormal(),
                                p2.getTexUV());

                        rasterizerT.rasterizer(p1, NewV1, NewV2);
                    }
                }
            }
        }
        return zb.getImageBuffer();
    }
}
