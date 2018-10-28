package raster;

import transforms.Col;

import java.awt.image.BufferedImage;
import java.util.Optional;

public class ZBufferAlgorithm {
    private final ImageBuffer imageBuffer;
    private final DepthBuffer depthBuffer;

    public ZBufferAlgorithm(BufferedImage image){
        this.depthBuffer = new DepthBuffer(image);
        this.imageBuffer = new ImageBuffer(image);
    }

    /**
     * testing if the point should be draw
     * @param x x coordinate of the point
     * @param y y coordinate of the point
     * @param z z coordinate of the point
     * @param color
     */
    public void test(int x, int y, double z, Col color){
        Optional<Double> zValue = depthBuffer.getPixel(x,y);
        if(z>=0 && zValue.isPresent() && zValue.get()>z){
            imageBuffer.setPixel(x,y,color.getRGB());
            depthBuffer.setPixel(x,y,z);
        }
    }

    /**
     * clearing image buffer and depth buffer
     * @param color
     */
    public void clear(Col color){
        depthBuffer.clear(1.0);
        imageBuffer.clear(color.getRGB());
    }

    public BufferedImage getImageBuffer() {
        return imageBuffer.getImage();
    }
    public int getWidth(){
        return imageBuffer.getWidth();
    }
    public int getHeight(){
        return imageBuffer.getHeight();
    }
}
