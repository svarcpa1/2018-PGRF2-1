package raster;

import java.awt.image.BufferedImage;
import java.util.Optional;

public class DepthBuffer implements Raster<Double>{

    private final int width;
	private final int height;
    private final double[][] depth;

    public DepthBuffer(BufferedImage image){
        this.width=image.getWidth();
        this.height=image.getHeight();
        depth = new double[image.getWidth()][image.getHeight()];
    }

    @Override
    public void clear(Double value) {
        for(int x =0; x<width; x++){
            for(int y =0; y<height; y++){
                depth[x][y]= value;
            }
        }
    }

    private boolean isInside(int x, int y){
        return ((x>=0 && x<width)&&(y>=0 && y<height));
    }

    @Override
    public Optional<Double> getPixel(int x, int y) {
        if (isInside(x,y)){
            return Optional.of(new Double(depth[x][y]));
        }else {
            return Optional.empty();
        }
    }

    @Override
    public int getWidth() {
        return width;
    }
    @Override
    public int getHeight() {
        return height;
    }
    @Override
    public void setPixel(int x, int y, Double value) {
        if (isInside(x,y)){
            depth[x][y]=value;
        }
    }
}
