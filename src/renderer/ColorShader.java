package renderer;

import Model.Vertex;
import transforms.Col;
import transforms.Vec2D;
import java.util.function.Function;

/**
 *Shader class enriched with texture coloring(thanks to vertices setup only on pyramid)
 * texture - vertical strips
 */
public class ColorShader implements Function<Vertex, Col> {

    @Override
    public Col apply(Vertex vertex) {
        Vec2D texUV = vertex.getTexUV().mul(1/vertex.getOne());
        //8x8
        if((((int)(texUV.getY()*8)%2)==0)){
            //color due to column
            return vertex.getColor().mul(1/vertex.getOne());
        }
        //black hatching
        return new Col(0x000000);
    }
}