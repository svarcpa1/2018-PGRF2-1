package renderer;

import Model.Vertex;
import raster.ZBufferAlgorithm;
import transforms.Mat4;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * liner renderer
 */
public class RendererLine {

    private final ZBufferAlgorithm zb;
    private final RasterizerLines rasterizerL;
    private boolean axesMode = false;
    private static final double BORDER = 0.0000001;

    public RendererLine(ZBufferAlgorithm zb, RasterizerLines rasterizerL){
        this.zb=zb;
        this.rasterizerL=rasterizerL;
    }

    /**
     *
     * making buffered image from vertices and indices (lines - wire model)
     * condition for drawing axes (just 2 points)
     *
     * @param vertices list of objects vertices
     * @param indices list of objects indices
     * @param t transformation matrix
     * @return buffered image filled with help of zb
     */
    public BufferedImage draw(List<Vertex> vertices, List<Integer> indices, Mat4 t){

            if(axesMode){
                for(int i = 0; i < indices.size(); i += 2) {
                    Vertex o1 = vertices.get(indices.get(i));
                    Vertex o2 = vertices.get(indices.get(i + 1));

                    Vertex p1 = new Vertex(o1.getPosition().mul(t), o1.getColor(), o1.getNormal(), o1.getTexUV());
                    Vertex p2 = new Vertex(o2.getPosition().mul(t), o2.getColor(), o2.getNormal(), o2.getTexUV());

                    if(p2.getPosition().getW()>p1.getPosition().getW()){
                        Vertex tmp;
                        tmp = p1;
                        p1=p2;
                        p2=tmp;
                    }

                    //whole line is inside
                    if (p2.getPosition().getW()> BORDER){
                        rasterizerL.rasterizer(p1,p2);
                    }else {
                        //point b (p2) is out
                        if (p2.getPosition().getW()< BORDER){
                            double tmp = (p1.getPosition().getW() - BORDER) /
                                    (p1.getPosition().getW() - p2.getPosition().getW());
                            Vertex NewV2 = new Vertex(p1.getPosition().mul(1 - tmp).add(p2.getPosition().mul(tmp)),
                                    p2.getColor(),
                                    p2.getNormal(),
                                    p2.getTexUV());

                            rasterizerL.rasterizer(p1,NewV2);
                        }
                    }
                }
            }else{
                for(int i = 0; i < indices.size(); i += 3) {
                    Vertex o1 = vertices.get(indices.get(i));
                    Vertex o2 = vertices.get(indices.get(i + 1));
                    Vertex o3 = vertices.get(indices.get(i + 2));

                    Vertex p1 = new Vertex(o1.getPosition().mul(t), o1.getColor(), o1.getNormal(), o1.getTexUV());
                    Vertex p2 = new Vertex(o2.getPosition().mul(t), o2.getColor(), o2.getNormal(), o2.getTexUV());
                    Vertex p3 = new Vertex(o3.getPosition().mul(t), o3.getColor(), o3.getNormal(), o3.getTexUV());

                    //clipping triangles due to w (position of watcher)
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

                    //whole triangle is in in
                    if(p3.getPosition().getW() > BORDER){
                        rasterizerL.rasterizer(p1, p2);
                        rasterizerL.rasterizer(p2, p3);
                        rasterizerL.rasterizer(p3, p1);
                    }
                    else {
                        //1 vertex out
                        if (p2.getPosition().getW() > BORDER) {
                            //substract min, divided by range - mantra :)
                            double tmp = (p2.getPosition().getW() - BORDER) /
                                    (p2.getPosition().getW() - p3.getPosition().getW());
                            Vertex NewV1 = new Vertex(p2.getPosition().mul(1 - tmp).add(p3.getPosition().mul(tmp)),
                                    p3.getColor(),
                                    p3.getNormal(),
                                    p3.getTexUV());
                            tmp = (p1.getPosition().getW() - BORDER) /
                                    (p1.getPosition().getW() - p3.getPosition().getW());
                            Vertex NewV2 = new Vertex(p1.getPosition().mul(1 - tmp).add(p3.getPosition().mul(tmp)),
                                    p3.getColor(),
                                    p3.getNormal(),
                                    p3.getTexUV());

                            rasterizerL.rasterizer(p1, p2);
                            rasterizerL.rasterizer(p2, NewV2);
                            rasterizerL.rasterizer(NewV2, p1);

                            rasterizerL.rasterizer(p2, NewV2);
                            rasterizerL.rasterizer(NewV1, NewV2);
                            rasterizerL.rasterizer(NewV1, p2);
                        }
                        //2 vertexes out
                        else {
                            if (p1.getPosition().getW() > BORDER) {
                                //substract min, divided by range - mantra :)
                                double tmp = (p1.getPosition().getW() - BORDER) /
                                        (p1.getPosition().getW() - p2.getPosition().getW());
                                Vertex NewV1 = new Vertex(p1.getPosition().mul(1 - tmp).add(p2.getPosition().mul(tmp)),
                                        p2.getColor(),
                                        p2.getNormal(),
                                        p2.getTexUV());
                                tmp = (p1.getPosition().getW() - BORDER) /
                                        (p1.getPosition().getW() - p3.getPosition().getW());
                                Vertex NewV2 = new Vertex(p1.getPosition().mul(1 - tmp).add(p3.getPosition().mul(tmp)),
                                        p2.getColor(),
                                        p2.getNormal(),
                                        p2.getTexUV());

                                rasterizerL.rasterizer(p1, NewV1);
                                rasterizerL.rasterizer(NewV1, NewV2);
                                rasterizerL.rasterizer(NewV2, p1);
                            }
                        }
                    }
                }
            }
        return zb.getImageBuffer();
    }

    /**
     * decision if axes are draw
     * @param axesMode
     */
    public void setAxesMode(boolean axesMode) {
        this.axesMode = axesMode;
    }
}
